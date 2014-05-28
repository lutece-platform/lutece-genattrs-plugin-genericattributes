/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.asynchronousupload.service.AbstractAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.filesystem.UploadUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;


/**
 * Abstract class to manage uploaded files for generic attributes entries of
 * type files
 */
public abstract class AbstractGenAttUploadHandler extends AbstractAsynchronousUploadHandler
{
    private static final String PREFIX_ENTRY_ID = IEntryTypeService.PREFIX_ATTRIBUTE;

    // Error messages
    private static final String ERROR_MESSAGE_UNKNOWN_ERROR = "genericattributes.message.unknownError";

    /** <sessionId,<fieldName,fileItems>> */
    /** contains uploaded file items */
    private static Map<String, Map<String, List<FileItem>>> _mapAsynchronousUpload = new ConcurrentHashMap<String, Map<String, List<FileItem>>>(  );

    /**
     * {@inheritDoc}
     */
    @Override
    public String canUploadFiles( HttpServletRequest request, String strFieldName,
        List<FileItem> listFileItemsToUpload, Locale locale )
    {
        if ( StringUtils.isNotBlank( strFieldName ) && ( strFieldName.length(  ) > PREFIX_ENTRY_ID.length(  ) ) )
        {
            initMap( request.getSession(  ).getId(  ), strFieldName );

            String strIdEntry = getEntryIdFromFieldName( strFieldName );

            if ( StringUtils.isEmpty( strIdEntry ) || !StringUtils.isNumeric( strIdEntry ) )
            {
                return I18nService.getLocalizedString( ERROR_MESSAGE_UNKNOWN_ERROR, locale );
            }

            int nIdEntry = Integer.parseInt( strIdEntry );
            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            List<FileItem> listUploadedFileItems = getListUploadedFiles( request, strFieldName );

            if ( entry != null )
            {
                GenericAttributeError error = EntryTypeServiceManager.getEntryTypeService( entry )
                                                                     .canUploadFiles( entry, listUploadedFileItems,
                        listFileItemsToUpload, locale );

                if ( error != null )
                {
                    return error.getErrorMessage(  );
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
    public List<FileItem> getListUploadedFiles( HttpServletRequest request, String strFieldName )
    {
        Map<String, List<FileItem>> mapUserFiles = _mapAsynchronousUpload.get( request.getSession(  ).getId(  ) );

        return ( mapUserFiles == null ) ? null : mapUserFiles.get( strFieldName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileItem> getFileItems( String strFieldName, String strSessionId )
    {
        if ( StringUtils.isBlank( strFieldName ) )
        {
            throw new AppException( "id field name is not provided for the current file upload" );
        }

        initMap( strSessionId, strFieldName );

        // find session-related files in the map
        Map<String, List<FileItem>> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );

        return mapFileItemsSession.get( strFieldName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFileItemToUploadedFile( FileItem fileItem, String strFieldName, HttpServletRequest request )
    {
        // This is the name that will be displayed in the form. We keep
        // the original name, but clean it to make it cross-platform.
        String strFileName = UploadUtil.cleanFileName( fileItem.getName(  ).trim(  ) );

        initMap( request.getSession(  ).getId(  ), buildFieldName( strFieldName ) );

        // Check if this file has not already been uploaded
        List<FileItem> uploadedFiles = getFileItems( strFieldName, request.getSession(  ).getId(  ) );

        if ( uploadedFiles != null )
        {
            boolean bNew = true;

            if ( !uploadedFiles.isEmpty(  ) )
            {
                Iterator<FileItem> iterUploadedFiles = uploadedFiles.iterator(  );

                while ( bNew && iterUploadedFiles.hasNext(  ) )
                {
                    FileItem uploadedFile = iterUploadedFiles.next(  );
                    String strUploadedFileName = UploadUtil.cleanFileName( uploadedFile.getName(  ).trim(  ) );
                    // If we find a file with the same name and the same
                    // length, we consider that the current file has
                    // already been uploaded
                    bNew = !( StringUtils.equals( strUploadedFileName, strFileName ) &&
                        ( uploadedFile.getSize(  ) == fileItem.getSize(  ) ) );
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
    public void removeFileItem( String strFieldName, String strSessionId, int nIndex )
    {
        // Remove the file (this will also delete the file physically)
        List<FileItem> uploadedFiles = getFileItems( strFieldName, strSessionId );

        if ( ( uploadedFiles != null ) && !uploadedFiles.isEmpty(  ) && ( uploadedFiles.size(  ) > nIndex ) )
        {
            // Remove the object from the Hashmap
            FileItem fileItem = uploadedFiles.remove( nIndex );
            fileItem.delete(  );
        }
    }

    /**
     * Removes all files associated to the session
     * @param strSessionId the session id
     */
    public void removeSessionFiles( String strSessionId )
    {
        _mapAsynchronousUpload.remove( strSessionId );
    }

    /**
     * Build the field name from a given id entry
     * i.e. : form_1
     * @param strIdEntry the id entry
     * @return the field name
     */
    protected String buildFieldName( String strIdEntry )
    {
        return PREFIX_ENTRY_ID + strIdEntry;
    }

    /**
     * Get the id of the entry associated with a given field name
     * @param strFieldName The name of the field
     * @return The id of the entry
     */
    protected String getEntryIdFromFieldName( String strFieldName )
    {
        if ( StringUtils.isEmpty( strFieldName ) || ( strFieldName.length(  ) < PREFIX_ENTRY_ID.length(  ) ) )
        {
            return null;
        }

        return strFieldName.substring( PREFIX_ENTRY_ID.length(  ) );
    }

    /**
     * Init the map
     * @param strSessionId the session id
     * @param strFieldName the field name
     */
    private void initMap( String strSessionId, String strFieldName )
    {
        // find session-related files in the map
        Map<String, List<FileItem>> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );

        // create map if not exists
        if ( mapFileItemsSession == null )
        {
            synchronized ( strSessionId )
            {
                mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );

                if ( mapFileItemsSession == null )
                {
                    mapFileItemsSession = new ConcurrentHashMap<String, List<FileItem>>(  );
                    _mapAsynchronousUpload.put( strSessionId, mapFileItemsSession );
                }
            }
        }

        List<FileItem> listFileItems = mapFileItemsSession.get( strFieldName );

        if ( listFileItems == null )
        {
            listFileItems = new ArrayList<FileItem>(  );
            mapFileItemsSession.put( strFieldName, listFileItems );
        }
    }
}
