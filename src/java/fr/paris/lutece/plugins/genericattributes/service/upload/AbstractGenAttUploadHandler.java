/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.asynchronousupload.service.AbstractAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.filesystem.UploadUtil;

/**
 * Abstract class to manage uploaded files for generic attributes entries of type files
 */
public abstract class AbstractGenAttUploadHandler extends AbstractAsynchronousUploadHandler
{
    private static final String PREFIX_ENTRY_ID = IEntryTypeService.PREFIX_ATTRIBUTE;
    private static final Pattern PATTERN_PREFIX_ENTRY_ID = Pattern.compile( "[^0-9]+([0-9]+)$" );
    private static final String PARAM_CUSTOM_SESSION_ID = "CUSTOM_SESSION";

    // Error messages
    private static final String ERROR_MESSAGE_UNKNOWN_ERROR = "genericattributes.message.unknownError";

    /** <sessionId,<fieldName,fileItems>> */
    /** contains uploaded file items */
    private static Map<String, Map<String, List<FileItem>>> _mapAsynchronousUpload = new ConcurrentHashMap<>( );

    /**
     * {@inheritDoc}
     */
    @Override
    public String canUploadFiles( HttpServletRequest request, String strFieldName, List<FileItem> listFileItemsToUpload, Locale locale )
    {
        if ( StringUtils.isNotBlank( strFieldName ) && ( strFieldName.length( ) > PREFIX_ENTRY_ID.length( ) ) )
        {
            String sessionId = getCustomSessionId( request.getSession( ) );
            initMap( sessionId, strFieldName );

            String strIdEntry = getEntryIdFromFieldName( strFieldName );

            if ( StringUtils.isEmpty( strIdEntry ) || !StringUtils.isNumeric( strIdEntry ) )
            {
                return I18nService.getLocalizedString( ERROR_MESSAGE_UNKNOWN_ERROR, locale );
            }

            int nIdEntry = Integer.parseInt( strIdEntry );
            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            List<FileItem> listUploadedFileItems = getListUploadedFiles( strFieldName, request.getSession( ) );

            if ( entry != null )
            {
                GenericAttributeError error = EntryTypeServiceManager.getEntryTypeService( entry ).canUploadFiles( entry, listUploadedFileItems,
                        listFileItemsToUpload, locale );

                if ( error != null )
                {
                    return error.getErrorMessage( );
                }

                return null;
            }
        }

        return I18nService.getLocalizedString( ERROR_MESSAGE_UNKNOWN_ERROR, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileItem> getListUploadedFiles( String strFieldName, HttpSession session )
    {
        if ( StringUtils.isBlank( strFieldName ) )
        {
            throw new AppException( "id field name is not provided for the current file upload" );
        }
        
        String sessionId = getCustomSessionId( session );

        initMap( sessionId, strFieldName );

        // find session-related files in the map
        Map<String, List<FileItem>> mapFileItemsSession = _mapAsynchronousUpload.get( sessionId );

        return mapFileItemsSession.get( strFieldName );
    }
    
    private String getCustomSessionId( HttpSession session )
    {
        String sessionId = (String) session.getAttribute( PARAM_CUSTOM_SESSION_ID );
        if ( sessionId == null )
        {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute( PARAM_CUSTOM_SESSION_ID, sessionId );
        }
        return sessionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFileItemToUploadedFilesList( FileItem fileItem, String strFieldName, HttpServletRequest request )
    {
        // This is the name that will be displayed in the form. We keep
        // the original name, but clean it to make it cross-platform.
        String strFileName = UploadUtil.cleanFileName( fileItem.getName( ).trim( ) );

        String sessionId = getCustomSessionId( request.getSession( ) );
        initMap( sessionId, strFieldName );

        // Check if this file has not already been uploaded
        List<FileItem> uploadedFiles = getListUploadedFiles( strFieldName, request.getSession( ) );

        if ( uploadedFiles != null )
        {
            boolean bNew = true;

            if ( !uploadedFiles.isEmpty( ) )
            {
                Iterator<FileItem> iterUploadedFiles = uploadedFiles.iterator( );

                while ( bNew && iterUploadedFiles.hasNext( ) )
                {
                    FileItem uploadedFile = iterUploadedFiles.next( );
                    String strUploadedFileName = UploadUtil.cleanFileName( uploadedFile.getName( ).trim( ) );
                    // If we find a file with the same name and the same
                    // length, we consider that the current file has
                    // already been uploaded
                    bNew = !( StringUtils.equals( strUploadedFileName, strFileName ) && ( uploadedFile.getSize( ) == fileItem.getSize( ) ) );
                }
            }

            if ( bNew )
            {
                uploadedFiles.add( fileItem );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFileItem( String strFieldName, HttpSession session, int nIndex )
    {
        // Remove the file (this will also delete the file physically)
        List<FileItem> uploadedFiles = getListUploadedFiles( strFieldName, session );

        if ( ( uploadedFiles != null ) && !uploadedFiles.isEmpty( ) && ( uploadedFiles.size( ) > nIndex ) )
        {
            // Remove the object from the Hashmap
            FileItem fileItem = uploadedFiles.remove( nIndex );
            fileItem.delete( );
        }
    }

    /**
     * Removes all files associated to the session
     * 
     * @param strSessionId
     *            the session id
     */
    public void removeSessionFiles( HttpSession session )
    {
        String sessionId = (String) session.getAttribute( PARAM_CUSTOM_SESSION_ID );
        if ( sessionId != null) 
        {
            _mapAsynchronousUpload.remove( sessionId );
        }
       
    }

    /**
     * Build the field name from a given id entry i.e. : form_1
     * 
     * @param strIdEntry
     *            the id entry
     * @return the field name
     */
    protected String buildFieldName( String strIdEntry )
    {
        return PREFIX_ENTRY_ID + strIdEntry;
    }

    /**
     * Get the id of the entry associated with a given field name
     * 
     * @param strFieldName
     *            The name of the field
     * @return The id of the entry
     */
    protected String getEntryIdFromFieldName( String strFieldName )
    {
        if ( StringUtils.isEmpty( strFieldName ) || ( strFieldName.length( ) < PREFIX_ENTRY_ID.length( ) ) )
        {
            return null;
        }

        Matcher matcher = PATTERN_PREFIX_ENTRY_ID.matcher( strFieldName );
        if ( matcher.find( ) )
        {
            return matcher.group( 1 );
        }

        return null;
    }

    /**
     * Init the map
     * 
     * @param strSessionId
     *            the session id
     * @param strFieldName
     *            the field name
     */
    private void initMap( String strSessionId, String strFieldName )
    {
        // find session-related files in the map
        Map<String, List<FileItem>> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );

        // create map if not exists
        if ( mapFileItemsSession == null )
        {
            synchronized( this )
            {
                // Ignore double check locking error : assignation and instanciation of objects are separated.
                mapFileItemsSession = _mapAsynchronousUpload.computeIfAbsent( strSessionId, s -> new ConcurrentHashMap<>( ) );
            }
        }

        mapFileItemsSession.computeIfAbsent( strFieldName, s -> new ArrayList<>( ) );
    }
}
