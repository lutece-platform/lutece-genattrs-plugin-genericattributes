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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.fileimage.FileImagePublicService;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

/**
 * 
 * AbstractEntryTypeGalleryImage
 *
 */
public abstract class AbstractEntryTypeGalleryImage extends EntryTypeService
{
    // PARAMETERS
    protected static final String PARAMETER_ID_RESPONSE    = "id_response";
    protected static final String PARAMETER_CODE_GALLERY   = "code_gallery";

    // MESSAGES
    protected static final String ERROR_FIELD_CODE_GALLERY = "genericattributes.createEntry.labelChooseGalleryImage";

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
        for ( FileItem fileSource : listFilesSource )
        {
            // Check mandatory attribute
            String strFilename = Optional.ofNullable( fileSource ).map( FileUploadService::getFileNameOnly ).orElse( StringUtils.EMPTY );

            if ( entry.isMandatory( ) && StringUtils.isBlank( strFilename ) )
            {
                return new MandatoryError( entry, locale );
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strAttributeName = getAttributeName( entry, request );

        // Upload file from gallery
        String strFileGallery = request.getParameter( strAttributeName );
        
        if ( StringUtils.isNotEmpty( strFileGallery ) )
        {
            FileImagePublicService.init( );

            File file = FileHome.findByPrimaryKey( Integer.parseInt( strFileGallery ) );
            file.getPhysicalFile( ).setValue( PhysicalFileHome.findByPrimaryKey( file.getIdFile( ) ).getValue( ) );

            Response response = new Response( );
            response.setEntry( entry );
            response.setFile( file );
            response.setIterationNumber( getResponseIterationValue( request ) );

            listResponse.add( response );

            return null;
        } else if ( StringUtils.isEmpty( strFileGallery ) && entry.isMandatory( ) )
        {
            GenericAttributeError genAttError = new MandatoryError( entry, locale );

            Response response = new Response( );
            response.setEntry( entry );
            listResponse.add( response );
            return genAttError;
        } else
        {
            Response response = new Response( );
            response.setEntry( entry );
            listResponse.add( response );

            return null;
        }

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
        return IEntryTypeService.PREFIX_ATTRIBUTE + entry.getIdEntry( );
    }

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
        String strIndexed = request.getParameter( PARAMETER_INDEXED );

        String strError = checkEntryData( request, locale );

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

        String strCodeGallery = request.getParameter( PARAMETER_CODE_GALLERY );
        GenericAttributesUtils.createOrUpdateField( entry, PARAMETER_CODE_GALLERY, null, strCodeGallery );

        String strExportBinary = request.getParameter( IEntryTypeService.PARAMETER_EXPORT_BINARY );
        GenericAttributesUtils.createOrUpdateField( entry, IEntryTypeService.FIELD_FILE_BINARY, null, Boolean.toString( StringUtils.isNotBlank( strExportBinary ) ) );

        entry.setMandatory( strMandatory != null );
        entry.setOnlyDisplayInBack( strOnlyDisplayInBack != null );
        return null;
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
     * Check the entry data
     * 
     * @param request
     *            the HTTP request
     * @param locale
     *            the locale
     * @return the error message url if there is an error, an empty string otherwise
     */
    private static String checkEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( IEntryTypeService.PARAMETER_TITLE );
        String strFieldError = StringUtils.EMPTY;
        String strCodeGalleryImage = request.getParameter( PARAMETER_CODE_GALLERY );

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = IEntryTypeService.ERROR_FIELD_TITLE;
        }

        if ( StringUtils.isEmpty( strCodeGalleryImage ) )
        {
            strFieldError = ERROR_FIELD_CODE_GALLERY;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, IEntryTypeService.MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        return StringUtils.EMPTY;
    }
}
