/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.genericattributes.service.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import fr.paris.lutece.portal.service.upload.MultipartItem;
import jakarta.servlet.http.HttpSession;

/**
 * Uploaded files of a field, kept in the HTTP session with two attributes per file: its description and its content. The list
 * is a view: adding or removing a file writes or removes its attributes.
 * <ul>
 * <li>Concurrent uploads of a session served by several instances write different attributes: no file is lost (a distributed
 * session writes back every attribute a request has read).</li>
 * <li>Counting and listing the files read the descriptions only: the content is read, and written back, only when it is
 * used.</li>
 * </ul>
 * Files are kept in the order they were added; {@link #add(int, MultipartItem)} always appends.
 */
public class SessionMultipartItemList extends AbstractList<MultipartItem>
{
    private static final String PREFIX_INFO = "genericattributes.upload.file.";
    private static final String PREFIX_CONTENT = "genericattributes.upload.content.";
    private static final char SEPARATOR = '|';
    // Orders the files added in the same millisecond on this instance
    private static final AtomicLong _sequence = new AtomicLong( );

    private final HttpSession _session;
    private final String _strPrefix;
    private final List<String> _listInfoNames = new ArrayList<>( );

    /**
     * Constructor
     *
     * @param session
     *            the session
     * @param strFieldName
     *            the field name
     */
    public SessionMultipartItemList( HttpSession session, String strFieldName )
    {
        _session = session;
        _strPrefix = PREFIX_INFO + strFieldName + SEPARATOR;
        for ( String strName : Collections.list( session.getAttributeNames( ) ) )
        {
            if ( strName.startsWith( _strPrefix ) )
            {
                _listInfoNames.add( strName );
            }
        }
        // The file id starts with its creation time
        Collections.sort( _listInfoNames );
    }

    @Override
    public MultipartItem get( int nIndex )
    {
        return new SessionUploadedFile( _listInfoNames.get( nIndex ), null );
    }

    @Override
    public int size( )
    {
        return _listInfoNames.size( );
    }

    @Override
    public void add( int nIndex, MultipartItem item )
    {
        String strId = String.format( "%013d%06d-%s", System.currentTimeMillis( ), _sequence.incrementAndGet( ) % 1_000_000, UUID.randomUUID( ) );
        String strInfoName = _strPrefix + strId;
        _session.setAttribute( PREFIX_CONTENT + strId, content( item ) );
        _session.setAttribute( strInfoName, new UploadedFileInfo( item ) );
        _listInfoNames.add( strInfoName );
        modCount++;
    }

    @Override
    public MultipartItem set( int nIndex, MultipartItem item )
    {
        String strInfoName = _listInfoNames.get( nIndex );
        MultipartItem previous = new SessionUploadedFile( strInfoName, contentOf( strInfoName ) );
        _session.setAttribute( contentName( strInfoName ), content( item ) );
        _session.setAttribute( strInfoName, new UploadedFileInfo( item ) );

        return previous;
    }

    @Override
    public MultipartItem remove( int nIndex )
    {
        String strInfoName = _listInfoNames.remove( nIndex );
        // The removed file keeps its content: the caller may delete it or add it to another field
        MultipartItem removed = new SessionUploadedFile( strInfoName, contentOf( strInfoName ) );
        _session.removeAttribute( strInfoName );
        _session.removeAttribute( contentName( strInfoName ) );
        modCount++;

        return removed;
    }

    /**
     * Removes every uploaded file of the session and deletes their content
     *
     * @param session
     *            the session
     * @return the content of the files removed, to delete
     */
    public static List<MultipartItem> removeAll( HttpSession session )
    {
        List<MultipartItem> listContents = new ArrayList<>( );
        for ( String strName : Collections.list( session.getAttributeNames( ) ) )
        {
            if ( strName.startsWith( PREFIX_CONTENT ) )
            {
                Object content = session.getAttribute( strName );
                if ( content instanceof MultipartItem )
                {
                    listContents.add( (MultipartItem) content );
                }
                session.removeAttribute( strName );
            }
            else
                if ( strName.startsWith( PREFIX_INFO ) )
                {
                    session.removeAttribute( strName );
                }
        }

        return listContents;
    }

    private MultipartItem content( MultipartItem item )
    {
        // A file moved from another field keeps its content
        return ( item instanceof SessionUploadedFile ) ? ( (SessionUploadedFile) item ).content( ) : SessionMultipartItem.of( item );
    }

    private MultipartItem contentOf( String strInfoName )
    {
        return (MultipartItem) _session.getAttribute( contentName( strInfoName ) );
    }

    private static String contentName( String strInfoName )
    {
        return PREFIX_CONTENT + strInfoName.substring( strInfoName.lastIndexOf( SEPARATOR ) + 1 );
    }

    /**
     * Uploaded file read from the session: its description is read at once, its content when it is used
     */
    private final class SessionUploadedFile implements MultipartItem
    {
        private final String _strInfoName;
        private final UploadedFileInfo _info;
        private MultipartItem _content;

        private SessionUploadedFile( String strInfoName, MultipartItem content )
        {
            _strInfoName = strInfoName;
            _info = (UploadedFileInfo) _session.getAttribute( strInfoName );
            _content = content;
        }

        private MultipartItem content( )
        {
            if ( _content == null )
            {
                _content = contentOf( _strInfoName );
            }
            return _content;
        }

        @Override
        public String getFieldName( )
        {
            return _info.getFieldName( );
        }

        @Override
        public String getName( )
        {
            return _info.getName( );
        }

        @Override
        public String getContentType( )
        {
            return _info.getContentType( );
        }

        @Override
        public long getSize( )
        {
            return _info.getSize( );
        }

        @Override
        public InputStream getInputStream( ) throws IOException
        {
            return content( ).getInputStream( );
        }

        @Override
        public byte [ ] get( ) throws UncheckedIOException
        {
            return content( ).get( );
        }

        @Override
        public void delete( ) throws IOException
        {
            MultipartItem content = content( );
            if ( content != null )
            {
                content.delete( );
            }
        }
    }
}
