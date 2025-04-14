/*
 * Copyright (c) 2002-2022, City of Paris
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
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class of plugin generic attributes
 */
public final class FileAttributesUtils
{
    public static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES = "genericattributes.message.error.uploading_file.max_files";
    public static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE = "genericattributes.message.error.uploading_file.file_max_size";
    public static final String PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE = "genericattributes.upload.file.default_max_size";

    private FileAttributesUtils( )
    {
        // Empty Contructor
    }

    public static GenericAttributeError checkFileSize( Entry entry, List<MultipartItem> listUploadedFileItems, List<MultipartItem> listFileItemsToUpload, Locale locale )
    {
        GenericAttributeError error = null;
        Field fieldFileMaxSize = entry.getFieldByCode( IEntryTypeService.FIELD_FILE_MAX_SIZE );
        int nMaxSize = GenericAttributesUtils.CONSTANT_ID_NULL;

        if ( ( fieldFileMaxSize != null ) && StringUtils.isNotBlank( fieldFileMaxSize.getValue( ) ) && StringUtils.isNumeric( fieldFileMaxSize.getValue( ) ) )
        {
            nMaxSize = GenericAttributesUtils.convertStringToInt( fieldFileMaxSize.getValue( ) );
        }

        // If no max size defined in the db, then fetch the default max size from the
        // properties file
        if ( nMaxSize == GenericAttributesUtils.CONSTANT_ID_NULL )
        {
            nMaxSize = AppPropertiesService.getPropertyInt( PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE, 5242880 );
        }

        // If nMaxSize == -1, then no size limit
        if ( ( nMaxSize != GenericAttributesUtils.CONSTANT_ID_NULL ) && ( listFileItemsToUpload != null ) && ( listUploadedFileItems != null ) )
        {
            boolean bHasFileMaxSizeError = false;
            List<MultipartItem> listFileItems = new ArrayList<>( );
            listFileItems.addAll( listUploadedFileItems );
            listFileItems.addAll( listFileItemsToUpload );

            for ( MultipartItem fileItem : listFileItems )
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

    public static GenericAttributeError checkNumberFiles( Entry entry, List<MultipartItem> listUploadedFileItems, List<MultipartItem> listFileItemsToUpload,
            Locale locale )
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

    /**
     * Check the entry data
     * 
     * @param request
     *            the HTTP request
     * @param locale
     *            the locale
     * @return the error message url if there is an error, an empty string otherwise
     */
    public static String checkEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( IEntryTypeService.PARAMETER_TITLE );
        String strMaxFiles = request.getParameter( IEntryTypeService.PARAMETER_MAX_FILES );
        String strFileMaxSize = request.getParameter( IEntryTypeService.PARAMETER_FILE_MAX_SIZE );
        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = IEntryTypeService.ERROR_FIELD_TITLE;
        }
        else
            if ( StringUtils.isBlank( strMaxFiles ) )
            {
                strFieldError = IEntryTypeService.ERROR_FIELD_MAX_FILES;
            }
            else
                if ( StringUtils.isBlank( strFileMaxSize ) )
                {
                    strFieldError = IEntryTypeService.ERROR_FIELD_FILE_MAX_SIZE;
                }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, IEntryTypeService.MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( !StringUtils.isNumeric( strMaxFiles ) )
        {
            strFieldError = IEntryTypeService.ERROR_FIELD_MAX_FILES;
        }
        else
            if ( !StringUtils.isNumeric( strFileMaxSize ) )
            {
                strFieldError = IEntryTypeService.ERROR_FIELD_FILE_MAX_SIZE;
            }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, IEntryTypeService.MESSAGE_NUMERIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Check the record field data
     * 
     * @param entry
     *            The entry
     * @param listFilesSource
     *            the list of source files to upload
     * @param locale
     *            the locale
     * @return The error if there is any
     */
    public static GenericAttributeError checkResponseData( Entry entry, List<MultipartItem> listFilesSource, Locale locale )
    {
        for ( MultipartItem fileSource : listFilesSource )
        {
            // Check mandatory attribute
            String strFilename = Optional.ofNullable( fileSource ).map( FileUploadService::getFileNameOnly ).orElse( StringUtils.EMPTY );

            if ( entry.isMandatory( ) && StringUtils.isBlank( strFilename ) )
            {
                return new MandatoryError( entry, locale );
            }

            String strMimeType = FileSystemUtil.getMIMEType( strFilename );

            // Check mime type with regular expressions
            List<RegularExpression> listRegularExpression = entry.getFields( ).get( 0 ).getRegularExpressionList( );

            if ( StringUtils.isNotBlank( strFilename ) && CollectionUtils.isNotEmpty( listRegularExpression )
                    && RegularExpressionService.getInstance( ).isAvailable( ) )
            {
                for ( RegularExpression regularExpression : listRegularExpression )
                {
                    if ( !RegularExpressionService.getInstance( ).isMatches( strMimeType, regularExpression ) )
                    {
                        GenericAttributeError error = new GenericAttributeError( );
                        error.setMandatoryError( false );
                        error.setTitleQuestion( entry.getTitle( ) );
                        error.setErrorMessage( regularExpression.getErrorMessage( ) );

                        return error;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Set the list of fields
     * 
     * @param entry
     *            The entry
     * @param request
     *            the HTTP request
     */
    public static void createOrUpdateFileFields( Entry entry, HttpServletRequest request )
    {
        String strFileMaxSize = request.getParameter( IEntryTypeService.PARAMETER_FILE_MAX_SIZE );
        int nFileMaxSize = GenericAttributesUtils.convertStringToInt( strFileMaxSize );

        String strMaxFiles = request.getParameter( IEntryTypeService.PARAMETER_MAX_FILES );
        int nMaxFiles = GenericAttributesUtils.convertStringToInt( strMaxFiles );

        String strExportBinary = request.getParameter( IEntryTypeService.PARAMETER_EXPORT_BINARY );

        GenericAttributesUtils.createOrUpdateField( entry, IEntryTypeService.FIELD_FILE_MAX_SIZE, null, String.valueOf( nFileMaxSize ) );
        GenericAttributesUtils.createOrUpdateField( entry, IEntryTypeService.FIELD_MAX_FILES, null, String.valueOf( nMaxFiles ) );
        GenericAttributesUtils.createOrUpdateField( entry, IEntryTypeService.FIELD_FILE_BINARY, null,
                Boolean.toString( StringUtils.isNotBlank( strExportBinary ) ) );
    }
}
