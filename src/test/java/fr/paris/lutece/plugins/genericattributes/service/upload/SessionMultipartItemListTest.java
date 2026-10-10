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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.test.mocks.MockHttpSession;

/**
 * Uploaded files kept in the session, two attributes per file (description, content)
 */
public class SessionMultipartItemListTest
{
    private static final String FIELD = "nIt0_attribute20";

    @Test
    public void testListingDoesNotReadTheContent( )
    {
        MockHttpSession session = new MockHttpSession( );
        CountingItem first = new CountingItem( "a.txt", "aaa" );
        new SessionMultipartItemList( session, FIELD ).add( first );
        new SessionMultipartItemList( session, FIELD ).add( new CountingItem( "b.txt", "bb" ) );

        List<MultipartItem> list = new SessionMultipartItemList( session, FIELD );
        assertEquals( 2, list.size( ) );
        assertEquals( "a.txt", list.get( 0 ).getName( ) );
        assertEquals( 2, list.get( 1 ).getSize( ) );
        assertEquals( 0, first._nReads, "listing and counting read the descriptions only" );
        assertArrayEquals( "aaa".getBytes( StandardCharsets.UTF_8 ), list.get( 0 ).get( ) );
        assertEquals( 0, new SessionMultipartItemList( session, "other" ).size( ) );
    }

    @Test
    public void testConcurrentUploadsWriteDifferentAttributes( )
    {
        // Two requests of the session, each with its own view (as on two instances): both files are kept
        MockHttpSession session = new MockHttpSession( );
        List<MultipartItem> request1 = new SessionMultipartItemList( session, FIELD );
        List<MultipartItem> request2 = new SessionMultipartItemList( session, FIELD );
        request1.add( new CountingItem( "a.txt", "a" ) );
        request2.add( new CountingItem( "b.txt", "b" ) );

        assertEquals( 2, new SessionMultipartItemList( session, FIELD ).size( ) );
    }

    @Test
    public void testRemoveAndMoveKeepTheContent( ) throws Exception
    {
        MockHttpSession session = new MockHttpSession( );
        new SessionMultipartItemList( session, FIELD ).add( new CountingItem( "a.txt", "aaa" ) );

        // Moved to another field (forms questionMoved): add to the new field, then remove from the previous one
        List<MultipartItem> source = new SessionMultipartItemList( session, FIELD );
        List<MultipartItem> target = new SessionMultipartItemList( session, "nIt1_attribute20" );
        target.addAll( source );
        MultipartItem removed = source.remove( 0 );
        assertArrayEquals( "aaa".getBytes( StandardCharsets.UTF_8 ), removed.get( ) );
        assertEquals( 0, new SessionMultipartItemList( session, FIELD ).size( ) );
        assertArrayEquals( "aaa".getBytes( StandardCharsets.UTF_8 ), new SessionMultipartItemList( session, "nIt1_attribute20" ).get( 0 ).get( ) );

        assertEquals( 1, SessionMultipartItemList.removeAll( session ).size( ) );
        assertTrue( Collections.list( session.getAttributeNames( ) ).isEmpty( ) );
    }

    @Test
    public void testContentFollowsTheSerializedSession( ) throws Exception
    {
        MockHttpSession session = new MockHttpSession( );
        new SessionMultipartItemList( session, FIELD ).add( new CountingItem( "a.txt", "file content" ) );

        // Every attribute is serialized and read back, as on another instance
        MockHttpSession copy = new MockHttpSession( );
        for ( String strName : Collections.list( session.getAttributeNames( ) ) )
        {
            copy.setAttribute( strName, roundTrip( session.getAttribute( strName ) ) );
        }

        MultipartItem item = new SessionMultipartItemList( copy, FIELD ).get( 0 );
        assertEquals( "a.txt", item.getName( ) );
        assertEquals( "text/plain", item.getContentType( ) );
        assertEquals( 12, item.getSize( ) );
        assertArrayEquals( "file content".getBytes( StandardCharsets.UTF_8 ), item.getInputStream( ).readAllBytes( ) );
    }

    private static Object roundTrip( Object object ) throws Exception
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream( );
        try ( ObjectOutputStream out = new ObjectOutputStream( bytes ) )
        {
            out.writeObject( object );
        }
        return new ObjectInputStream( new ByteArrayInputStream( bytes.toByteArray( ) ) ).readObject( );
    }

    /**
     * In-memory item, not serializable, counting the reads of its content
     */
    private static class CountingItem implements MultipartItem
    {
        private final String _strName;
        private final byte [ ] _content;
        private int _nReads;

        CountingItem( String strName, String strContent )
        {
            _strName = strName;
            _content = strContent.getBytes( StandardCharsets.UTF_8 );
        }

        @Override
        public String getFieldName( )
        {
            return FIELD;
        }

        @Override
        public String getName( )
        {
            return _strName;
        }

        @Override
        public String getContentType( )
        {
            return "text/plain";
        }

        @Override
        public long getSize( )
        {
            return _content.length;
        }

        @Override
        public InputStream getInputStream( )
        {
            _nReads++;
            return new ByteArrayInputStream( _content );
        }

        @Override
        public byte [ ] get( )
        {
            _nReads++;
            return _content.clone( );
        }

        @Override
        public void delete( )
        {
            // nothing to delete
        }
    }
}
