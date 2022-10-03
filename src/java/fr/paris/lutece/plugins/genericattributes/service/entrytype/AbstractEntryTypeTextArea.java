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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.editor.EditorBbcodeService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.util.string.StringUtil;

/**
 * Abstract entry type for text areas
 */
public abstract class AbstractEntryTypeTextArea extends EntryTypeService
{
    private static final String PARAMETER_USE_RICH_TEXT = "useRichText";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strHeight = request.getParameter( PARAMETER_HEIGHT );
        String strMaxSizeEnter = request.getParameter( PARAMETER_MAX_SIZE_ENTER );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strUseRichText = request.getParameter( PARAMETER_USE_RICH_TEXT );
        String strOnlyDisplayInBack = request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strPlaceholder = request.getParameter( PARAMETER_PLACEHOLDER );

        int nWidth = -1;
        int nHeight = -1;
        int nMaxSizeEnter = -1;

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = ERROR_FIELD_TITLE;
        }

        else
            if ( StringUtils.isBlank( strWidth ) )
            {
                strFieldError = ERROR_FIELD_WIDTH;
            }
            else
                if ( StringUtils.isBlank( strHeight ) )
                {
                    strFieldError = ERROR_FIELD_HEIGHT;
                }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        try
        {
            nHeight = Integer.parseInt( strHeight );
        }
        catch( NumberFormatException ne )
        {
            strFieldError = ERROR_FIELD_HEIGHT;
        }

        try
        {
            nWidth = Integer.parseInt( strWidth );
        }
        catch( NumberFormatException ne )
        {
            strFieldError = ERROR_FIELD_WIDTH;
        }

        try
        {
            if ( StringUtils.isNotBlank( strMaxSizeEnter ) )
            {
                nMaxSizeEnter = Integer.parseInt( strMaxSizeEnter );
            }
        }
        catch( NumberFormatException ne )
        {
            strFieldError = FIELD_MAX_SIZE_ENTER;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        entry.setTitle( strTitle );
        entry.setCode( strCode );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );

        GenericAttributesUtils.createOrUpdateField( entry, FIELD_TEXT_CONF, null, strValue );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_MAX_SIZE, null, String.valueOf( nMaxSizeEnter ) );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_WIDTH, null, String.valueOf( nWidth ) );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_HEIGHT, null, String.valueOf( nHeight ) );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_RICHTEXT, null, String.valueOf( strUseRichText != null ) );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_PLACEHOLDER, null, strPlaceholder != null ? strPlaceholder : StringUtils.EMPTY );

        entry.setMandatory( strMandatory != null );
        entry.setOnlyDisplayInBack( strOnlyDisplayInBack != null );
        entry.setIndexed( strIndexed != null );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strValueEntry = request.getParameter( PREFIX_ATTRIBUTE + entry.getIdEntry( ) );
        Response response = new Response( );
        response.setEntry( entry );

        if ( strValueEntry == null )
        {
            return null;
        }

        int nMaxSize = Integer.parseInt( entry.getFieldByCode( FIELD_MAX_SIZE ).getValue( ) );

        Field fieldRichText = entry.getFieldByCode( IEntryTypeService.FIELD_RICHTEXT );
        boolean useRichtext = fieldRichText != null && Boolean.valueOf( fieldRichText.getValue( ) );

        if ( useRichtext )
        {
            response.setResponseValue( EditorBbcodeService.getInstance( ).parse( strValueEntry ) );
        }
        else
        {
            response.setResponseValue( strValueEntry );
        }

        if ( StringUtils.isNotBlank( response.getResponseValue( ) ) )
        {
            // if we use a rich text, we set the toStringValueResponse to the BBCode string
            if ( useRichtext )
            {
                response.setToStringValueResponse( strValueEntry );
            }
            else
            {
                response.setToStringValueResponse( getResponseValueForRecap( entry, request, response, locale ) );
            }
        }
        else
        {
            response.setToStringValueResponse( StringUtils.EMPTY );
        }

        response.setIterationNumber( getResponseIterationValue( request ) );

        listResponse.add( response );

        // check max size for the field. 0 means no limit
        if ( ( nMaxSize != -1 ) && ( strValueEntry.length( ) > nMaxSize ) )
        {
            GenericAttributeError error = new GenericAttributeError( );
            error.setMandatoryError( false );
            error.setTitleQuestion( entry.getTitle( ) );

            Object [ ] messageArgs = new Object [ ] {
                    nMaxSize,
            };
            error.setErrorMessage( I18nService.getLocalizedString( MESSAGE_MAXLENGTH, messageArgs, request.getLocale( ) ) );

            return error;
        }

        if ( entry.isMandatory( ) && StringUtils.isBlank( strValueEntry ) )
        {
            return new MandatoryError( entry, locale );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue( );
    }
}
