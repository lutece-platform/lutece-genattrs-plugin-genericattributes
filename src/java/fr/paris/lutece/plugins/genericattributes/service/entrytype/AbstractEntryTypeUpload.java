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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fr.paris.lutece.plugins.asynchronousupload.service.IAsyncUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.file.FileService;
import fr.paris.lutece.plugins.genericattributes.util.FileAttributesUtils;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Abstract entry type for uploads
 */
public abstract class AbstractEntryTypeUpload extends EntryTypeService
{
    // PARAMETERS
    protected static final String PARAMETER_ID_RESPONSE = "id_response";
    protected static final String PARAMETER_MAX_FILES = "max_files";
    protected static final String PARAMETER_FILE_MAX_SIZE = "file_max_size";
    protected static final String PARAMETER_EXPORT_BINARY = "export_binary";
    protected static final String PARAMETER_FILE_TYPE = "file_type";

    // CONSTANTS
    protected static final String ALL = "*";
    protected static final String COMMA = ",";

    // Private parameters
    protected static final String PARAMETER_RESOURCE_TYPE = "resource_type";
    protected static final String PARAMETER_ID = "id";
    protected static final String URL_IMAGE_SERVLET = "image";

    // MESSAGES
    protected static final String MESSAGE_ERROR_NOT_AN_IMAGE = "genericattributes.message.notAnImage";

    /**
     * Get the asynchronous upload handler to use for entries of this type
     * 
     * @return The asynchronous upload handler to use for entries of this type
     */
    public abstract IAsyncUploadHandler getAsynchronousUploadHandler( );

    /**
     * Get the URL to download the file of a response
     * 
     * @param nResponseId
     *            The id of the response to download the file of
     * @param strBaseUrl
     *            The base URL
     * @return The URL to redirect the user to download the file
     */
    public abstract String getUrlDownloadFile( int nResponseId, String strBaseUrl );

    /**
     * Check whether this entry type allows only images or every file type
     * 
     * @return True if this entry type allows only images, false if it allow every file type
     */
    protected abstract boolean checkForImages( );

    /**
     * Get the URL to download a file of a response throw the image servlet.
     * 
     * @param nResponseId
     *            The id of the response
     * @param strBaseUrl
     *            The base URL
     * @return The URL of to download the image
     */
    protected String getUrlDownloadImage( int nResponseId, String strBaseUrl )
    {
        UrlItem url = new UrlItem( strBaseUrl + URL_IMAGE_SERVLET );
        url.addParameter( PARAMETER_RESOURCE_TYPE, Response.RESOURCE_TYPE );
        url.addParameter( PARAMETER_ID, nResponseId );

        return url.getUrl( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError canUploadFiles( Entry entry, List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload, Locale locale )
    {
        /** 1) Check max files */
        GenericAttributeError error = FileAttributesUtils.checkNumberFiles( entry, listUploadedFileItems, listFileItemsToUpload, locale );
        if ( error != null )
        {
            return error;
        }

        /** 2) Check files size */
        error  = FileAttributesUtils.checkFileSize( entry, listUploadedFileItems, listFileItemsToUpload, locale );
        if ( error != null )
        {
            return error;
        }

        if ( listFileItemsToUpload != null )
        {
            for ( FileItem fileItem : listFileItemsToUpload )
            {
                if ( checkForImages( ) )
                {
                    error = doCheckforImages( fileItem, entry, locale );

                    if ( error != null )
                    {
                        return error;
                    }
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        // Check whether the binaries must be exported or just displaying an URL to download the file
        if ( entry.getFields( ) == null )
        {
            entry.setFields( FieldHome.getFieldListByIdEntry( entry.getIdEntry( ) ) );
        }

        Field field = entry.getFieldByCode( FIELD_FILE_BINARY );

        if ( ( field != null ) && StringUtils.isNotBlank( field.getValue( ) ) && Boolean.TRUE.equals( Boolean.valueOf( field.getValue( ) ) ) )
        {
            if ( response.getFile( ) != null )
            {
                FileService fileService = SpringContextService.getBean( FileService.BEAN_SERVICE );
                File file = fileService.findByPrimaryKey( response.getFile( ).getIdFile( ), true );

                if ( ( file != null ) && ( file.getPhysicalFile( ) != null ) && ( file.getPhysicalFile( ).getValue( ) != null ) )
                {
                    String strPhysicalFile = Arrays.toString( file.getPhysicalFile( ).getValue( ) );

                    if ( StringUtils.isNotBlank( strPhysicalFile ) )
                    {
                        // Removing the square brackets ("[]") that "Arrays.toString" added
                        return strPhysicalFile.substring( 1, strPhysicalFile.length( ) - 1 );
                    }
                }
            }

            return StringUtils.EMPTY;
        }

        String strBaseUrl = ( request != null ) ? AppPathService.getBaseUrl( request ) : AppPathService.getBaseUrl( );

        return getUrlDownloadFile( response.getIdResponse( ), strBaseUrl );
    }

    // CHECKS

    /**
     * Check the record field data
     * 
     * @param entry
     *            The entry
     * @param listFilesSource
     *            the list of source files to upload
     * @param locale
     *            the locale
     * @param request
     *            the HTTP request
     * @return The error if there is any
     */
    protected GenericAttributeError checkResponseData( Entry entry, List<FileItem> listFilesSource, Locale locale, HttpServletRequest request )
    {
        // Check if the user can upload the file. The File is already uploaded in the asynchronous uploaded files map
        // Thus the list of files to upload is in the list of uploaded files
        GenericAttributeError error = canUploadFiles( entry, listFilesSource, new ArrayList<FileItem>( ), locale );

        if ( error != null )
        {
            return error;
        }
        return FileAttributesUtils.checkResponseData( entry, listFilesSource, locale );
    }

    // FINDERS

    /**
     * Get the file source from the session
     * 
     * @param request
     *            the HttpServletRequest
     * @param strAttributeName
     *            the attribute name
     * @return the file item
     */
    protected List<FileItem> getFileSources( HttpServletRequest request, String strAttributeName )
    {
        if ( request != null )
        {
            // Files are only removed if a given flag is in the request
            getAsynchronousUploadHandler( ).doRemoveFile( request, strAttributeName );

            // Files are only added if a given flag is in the request
            getAsynchronousUploadHandler( ).addFilesUploadedSynchronously( request, strAttributeName );

            return getAsynchronousUploadHandler( ).getListUploadedFiles( strAttributeName, request.getSession( ) );
        }

        return new ArrayList<>( );
    }

    /**
     * Gives the attribute name
     * 
     * @param entry
     *            the entry
     * @param request
     *            the request
     * @return the attribute name
     */
    protected String getAttributeName( Entry entry, HttpServletRequest request )
    {
        String strAttributePrefix = IEntryTypeService.PREFIX_ATTRIBUTE + Integer.toString( entry.getIdEntry( ) );
        int nIterationNumber = getResponseIterationValue( request );

        if ( nIterationNumber != NumberUtils.INTEGER_MINUS_ONE )
        {
            strAttributePrefix = IEntryTypeService.PREFIX_ITERATION_ATTRIBUTE + nIterationNumber + "_" + strAttributePrefix;
        }

        return strAttributePrefix;
    }

    // PRIVATE METHODS

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strOnlyDisplayInBack = request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK );
        String strEditableBack = request.getParameter( PARAMETER_EDITABLE_BACK );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );

        String strError = FileAttributesUtils.checkEntryData( request, locale );

        if ( StringUtils.isNotBlank( strError ) )
        {
            return strError;
        }

        entry.setTitle( strTitle );
        entry.setCode( strCode );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setIndexed( strIndexed != null );

        FileAttributesUtils.createOrUpdateFileFields( entry, request );

        entry.setMandatory( strMandatory != null );
        entry.setOnlyDisplayInBack( strOnlyDisplayInBack != null );
        entry.setEditableBack( strEditableBack != null );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getReferenceListRegularExpression( Entry entry, Plugin plugin )
    {
        ReferenceList refListRegularExpression = null;

        if ( RegularExpressionService.getInstance( ).isAvailable( ) )
        {
            refListRegularExpression = new ReferenceList( );

            List<RegularExpression> listRegularExpression = RegularExpressionService.getInstance( ).getAllRegularExpression( );

            for ( RegularExpression regularExpression : listRegularExpression )
            {
                if ( !entry.getFields( ).get( 0 ).getRegularExpressionList( ).contains( regularExpression ) )
                {
                    refListRegularExpression.addItem( regularExpression.getIdExpression( ), regularExpression.getTitle( ) );
                }
            }
        }

        return refListRegularExpression;
    }

    /**
     * toStringValue should stay <code>null</code>.
     * 
     * @param entry
     *            The entry
     * @param response
     *            The response
     * @param locale
     *            the locale - will use a default one if not specified
     */
    @Override
    public void setResponseToStringValue( Entry entry, Response response, Locale locale )
    {
        // nothing - null is default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( ( response.getFile( ) != null ) && StringUtils.isNotBlank( response.getFile( ).getTitle( ) ) )
        {
            return response.getFile( ).getTitle( );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Do check that an uploaded file is an image
     * 
     * @param fileItem
     *            The file item
     * @param entry
     *            the entry
     * @param locale
     *            The locale
     * @return The error if any, or null if the file is a valid image
     */
    public GenericAttributeError doCheckforImages( FileItem fileItem, Entry entry, Locale locale )
    {
        String strFilename = FileUploadService.getFileNameOnly( fileItem );
        BufferedImage image = null;

        try
        {
            if ( fileItem.get( ) != null )
            {
                image = ImageIO.read( new ByteArrayInputStream( fileItem.get( ) ) );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( e );
        }

        if ( ( image == null ) && StringUtils.isNotBlank( strFilename ) )
        {
            GenericAttributeError genAttError = new GenericAttributeError( );
            genAttError.setMandatoryError( false );

            Object [ ] args = {
                fileItem.getName( )
            };
            genAttError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_NOT_AN_IMAGE, args, locale ) );
            genAttError.setTitleQuestion( entry.getTitle( ) );

            return genAttError;
        }

        return null;
    }
}
