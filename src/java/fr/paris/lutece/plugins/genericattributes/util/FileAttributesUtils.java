/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
package fr.paris.lutece.plugins.genericattributes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Utility class of plugin generic attributes
 */
public final class FileAttributesUtils
{
    private static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES = "genericattributes.message.error.uploading_file.max_files";
    public static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE = "genericattributes.message.error.uploading_file.file_max_size";
    public static final String PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE = "genericattributes.upload.file.default_max_size";
    private FileAttributesUtils( )
    {
        // Empty Contructor
    }
    
    public static GenericAttributeError checkFileSize( Entry entry, List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload, Locale locale )
    {
        GenericAttributeError error = null;
        Field fieldFileMaxSize = entry.getFieldByCode( IEntryTypeService.FIELD_FILE_MAX_SIZE );
        int nMaxSize = GenericAttributesUtils.CONSTANT_ID_NULL;

        if ( ( fieldFileMaxSize != null ) && StringUtils.isNotBlank( fieldFileMaxSize.getValue( ) ) && StringUtils.isNumeric( fieldFileMaxSize.getValue( ) ) )
        {
            nMaxSize = GenericAttributesUtils.convertStringToInt( fieldFileMaxSize.getValue( ) );
        }

        // If no max size defined in the db, then fetch the default max size from the properties file
        if ( nMaxSize == GenericAttributesUtils.CONSTANT_ID_NULL )
        {
            nMaxSize = AppPropertiesService.getPropertyInt( PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE, 5242880 );
        }

        // If nMaxSize == -1, then no size limit
        if ( ( nMaxSize != GenericAttributesUtils.CONSTANT_ID_NULL ) && ( listFileItemsToUpload != null ) && ( listUploadedFileItems != null ) )
        {
            boolean bHasFileMaxSizeError = false;
            List<FileItem> listFileItems = new ArrayList<>( );
            listFileItems.addAll( listUploadedFileItems );
            listFileItems.addAll( listFileItemsToUpload );

            for ( FileItem fileItem : listFileItems )
            {
                if ( fileItem.getSize( ) > nMaxSize )
                {
                    bHasFileMaxSizeError = true;

                    break;
                }
            }

            if ( bHasFileMaxSizeError )
            {
                Object [ ] params = {
                    nMaxSize
                };
                String strMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE, params, locale );
                error = new GenericAttributeError( );
                error.setMandatoryError( false );
                error.setTitleQuestion( entry.getTitle( ) );
                error.setErrorMessage( strMessage );
            }
        }
        return error;
    }
    
    public static GenericAttributeError checkNumberFiles( Entry entry, List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload, Locale locale )
    {
        GenericAttributeError error = null;
        Field fieldMaxFiles = entry.getFieldByCode( IEntryTypeService.FIELD_MAX_FILES );

        // By default, max file is set at 1
        int nMaxFiles = 1;

        if ( ( fieldMaxFiles != null ) && StringUtils.isNotBlank( fieldMaxFiles.getValue( ) ) && StringUtils.isNumeric( fieldMaxFiles.getValue( ) ) )
        {
            nMaxFiles = GenericAttributesUtils.convertStringToInt( fieldMaxFiles.getValue( ) );
        }

        if ( ( listUploadedFileItems != null ) && ( listFileItemsToUpload != null ) )
        {
            int nNbFiles = listUploadedFileItems.size( ) + listFileItemsToUpload.size( );

            if ( nNbFiles > nMaxFiles )
            {
                Object [ ] params = {
                    nMaxFiles
                };
                String strMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES, params, locale );
                error = new GenericAttributeError( );
                error.setMandatoryError( false );
                error.setTitleQuestion( entry.getTitle( ) );
                error.setErrorMessage( strMessage );

                return error;
            }
        }
        
        return error;
    }
}
