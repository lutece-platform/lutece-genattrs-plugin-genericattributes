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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

/**
 *
 * @class AbstractEntryTypeCamera
 *
 */
public abstract class AbstractEntryTypeCamera extends AbstractEntryTypeImage
{
    private static final String PROPERTY_IMAGE_TITLE = AppPropertiesService
            .getProperty( "genericattributes.image.prefix.title", "default" );
    private static final String PROPERTY_IMAGE_TITLE_DATE_FORMAT = AppPropertiesService
            .getProperty( "genericattributes.image.date.format.title", "YYYY-MM-DD hh:mm:ss" );

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
                ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( )
                : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMaxImageSize = request.getParameter( PARAMETER_MAX_IMAGE_SIZE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strHeight = request.getParameter( PARAMETER_HEIGHT );
        String strUnique = request.getParameter( PARAMETER_UNIQUE );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strOnlyDisplayInBack = request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK );
        String strErrorMessage = request.getParameter( PARAMETER_ERROR_MESSAGE );

        String strTypeImage = request.getParameter( PARAMETER_IMAGE_TYPE );

        int nWidth = -1;
        int nheight = -1;
        int nMaxImageSize = -1;

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = ERROR_FIELD_TITLE;
        }

        else if ( StringUtils.isBlank( strWidth ) )
        {
            strFieldError = ERROR_FIELD_WIDTH;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields =
            { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
        }

        try
        {
            nWidth = Integer.parseInt( strWidth );
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = ERROR_FIELD_WIDTH;
        }

        try
        {
            if ( StringUtils.isNotBlank( strHeight ) )
            {
                nheight = Integer.parseInt( strHeight );
            }
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = ERROR_FIELD_HEIGHT;
        }

        try
        {
            if ( StringUtils.isNotBlank( strMaxImageSize ) )
            {
                nMaxImageSize = Integer.parseInt( strMaxImageSize );
            }
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = ERROR_FIELD_HEIGHT;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields =
            { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
        }

        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setErrorMessage( strErrorMessage );
        entry.setCode( strCode );

        createOrUpdateField( entry, FIELD_IMAGE_TYPE, null, strTypeImage );
        createOrUpdateField( entry, FIELD_WIDTH, null, String.valueOf( nWidth ) );
        createOrUpdateField( entry, FIELD_HEIGHT, null, String.valueOf( nheight ) );
        createOrUpdateField( entry, FIELD_MAX_SIZE, null, String.valueOf( nMaxImageSize ) );

        entry.setMandatory( strMandatory != null );
        entry.setOnlyDisplayInBack( strOnlyDisplayInBack != null );
        entry.setUnique( strUnique != null );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse,
            Locale locale )
    {
        if ( !( request instanceof MultipartHttpServletRequest ) )
        {
            return entry.isMandatory( ) ? new MandatoryError( entry, locale ) : null;
        }

        GenericAttributeError genAttError = null;
        String sourceBase = request.getParameter( ( IEntryTypeService.PREFIX_ATTRIBUTE + entry.getIdEntry( ) ) );

        if ( sourceBase != null )
        {
            genAttError = doCheckforImages( sourceBase, entry, request.getLocale( ) );

            if ( genAttError != null )
            {
                return genAttError;
            }

            listResponse.add( getResponseFromImage( request, sourceBase, entry, true ) );

            if ( !entry.isMandatory( ) )
            {
                return genAttError;
            }

            if ( entry.isMandatory( ) && ( StringUtils.isBlank( sourceBase ) || StringUtils.isEmpty( sourceBase ) ) )
            {
                if ( StringUtils.isNotEmpty( entry.getErrorMessage( ) ) )
                {
                    GenericAttributeError error = new GenericAttributeError( );
                    error.setMandatoryError( true );
                    error.setErrorMessage( entry.getErrorMessage( ) );

                    return error;
                }

                return new MandatoryError( entry, locale );
            }
        }

        return genAttError;
    }

    /**
     * Get a generic attributes response from a request
     * 
     * @param request
     * @param imageSource         the image in base64 form
     * @param entry               The entry
     * @param bCreatePhysicalFile True to create the physical file associated with
     *                            the file of the response, false otherwise. Note
     *                            that the physical file will never be saved in the
     *                            database by this method, like any other created
     *                            object.
     * @return The created response
     */
    protected Response getResponseFromImage( HttpServletRequest request, String imageSource, Entry entry,
            boolean bCreatePhysicalFile )
    {
        Response response = new Response( );
        response.setEntry( entry );
        String fileName = null;
        SimpleDateFormat dt = new SimpleDateFormat( PROPERTY_IMAGE_TITLE_DATE_FORMAT );

        Field imageTypeField = entry.getFieldByCode( FIELD_IMAGE_TYPE );
        String imageType = Optional.ofNullable( imageTypeField ).map( i -> "." + i.getValue( ) ).orElse( "" );

        Calendar c = Calendar.getInstance( );
        String[] imageTitle = PROPERTY_IMAGE_TITLE.trim( ).split( "," );
        if ( imageTitle != null )
        {
            fileName = "";
            for ( String imgTitle : imageTitle )
            {
                if ( request.getParameter( imgTitle ) != null
                        && StringUtils.isNotBlank( request.getParameter( imgTitle ) ) )
                {

                    fileName = fileName.concat( request.getParameter( imgTitle ) ).concat( "-" );
                }
            }
        }

        if ( StringUtils.isNotBlank( imageSource ) && StringUtils.isNotEmpty( imageSource ) )
        {
            File file = new File( );

            if ( fileName != null )
            {
                file.setTitle( fileName + dt.format( c.getTime( ) ) + imageType );
            }
            else
            {
                file.setTitle( entry.getTitle( ) + dt.format( c.getTime( ) ) + imageType );
            }
            file.setExtension( imageType );
            if ( bCreatePhysicalFile )
            {
                file.setMimeType( FileSystemUtil.getMIMEType( file.getTitle( ) ) );

                PhysicalFile physicalFile = new PhysicalFile( );
                String base64Image = imageSource.split( "," )[1];
                byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary( base64Image );
                physicalFile.setValue( imageBytes );
                file.setPhysicalFile( physicalFile );
            }

            response.setFile( file );
            response.setIsImage( true );
            response.setToStringValueResponse( imageSource );
        }
        else
        {
            response.setToStringValueResponse( StringUtils.EMPTY );
        }

        response.setIterationNumber( getResponseIterationValue( request ) );

        return response;
    }

    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( ( response.getFile( ) != null ) && StringUtils.isNotBlank( response.getToStringValueResponse( ) ) )
        {
            return response.getToStringValueResponse( );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Do check that an uploaded source is an image
     * 
     * @param imageSource The file imageSource
     * @param entry       the entry
     * @param locale      The locale
     * @return The error if any, or null if the file is a valid image
     */
    public GenericAttributeError doCheckforImages( String imageSource, Entry entry, Locale locale )
    {
        BufferedImage image = null;
        GenericAttributeError genAttError = new GenericAttributeError( );
        genAttError.setMandatoryError( false );

        Object[] args =
        { entry.getTitle( ) };
        genAttError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_NOT_AN_IMAGE, args, locale ) );
        genAttError.setTitleQuestion( entry.getTitle( ) );

        if ( ( imageSource != null ) && ( imageSource.split( "," ).length > 1 ) )
        {
            String base64Image = imageSource.split( "," )[1];
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary( base64Image );
            ByteArrayInputStream bis = new ByteArrayInputStream( imageBytes );

            try
            {
                image = ImageIO.read( bis );
                bis.close( );
            }
            catch ( IOException e )
            {
                return genAttError;
            }
        }

        if ( ( image == null ) && StringUtils.isNotBlank( imageSource ) )
        {
            return genAttError;
        }

        return doCheckSize( image, entry, locale );
    }

    /**
     * Do check the size of image
     * 
     * @param image
     * @param entry
     * @param locale The locale
     * @return The error if any, or null if not erroe
     */
    public GenericAttributeError doCheckSize( BufferedImage image, Entry entry, Locale locale )
    {
        int nMaxSize = Integer.parseInt( entry.getFieldByCode( FIELD_MAX_FILES ).getValue( ) );

        Field imageTypeField = entry.getFieldByCode( FIELD_IMAGE_TYPE );
        String imageType = imageTypeField != null ? imageTypeField.getValue( ) : "png";

        // If no max size defined in the db, then fetch the default max size from the
        // properties file
        if ( nMaxSize == GenericAttributesUtils.CONSTANT_ID_NULL )
        {
            nMaxSize = AppPropertiesService.getPropertyInt( PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE, 5242880 );
        }

        // If nMaxSize == -1, then no size limit
        if ( ( nMaxSize != GenericAttributesUtils.CONSTANT_ID_NULL ) && ( image != null ) )
        {
            boolean bHasFileMaxSizeError = false;
            ByteArrayOutputStream tmp = new ByteArrayOutputStream( );

            try
            {
                ImageIO.write( image, imageType, tmp );
                tmp.close( );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );

                String strMessage = "IOException when reading Image Size";
                GenericAttributeError error = new GenericAttributeError( );
                error.setMandatoryError( false );
                error.setTitleQuestion( entry.getTitle( ) );
                error.setErrorMessage( strMessage );

                return error;
            }

            Integer contentLength = tmp.size( );

            if ( contentLength > nMaxSize )
            {
                bHasFileMaxSizeError = true;
            }

            if ( bHasFileMaxSizeError )
            {
                Object[] params =
                { nMaxSize };
                String strMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE,
                        params, locale );
                GenericAttributeError error = new GenericAttributeError( );
                error.setMandatoryError( false );
                error.setTitleQuestion( entry.getTitle( ) );
                error.setErrorMessage( strMessage );

                return error;
            }
        }

        return null;
    }
}
