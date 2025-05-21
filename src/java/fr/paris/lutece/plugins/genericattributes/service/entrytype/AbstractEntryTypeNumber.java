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

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

/**
 * Abstract entry type for text
 */
public abstract class AbstractEntryTypeNumber extends EntryTypeService
{
    // PARAMETERS
    private static final String PARAMETER_SUFFIX = "suffix";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strMin = request.getParameter( PARAMETER_MIN );
        String strMax = request.getParameter( PARAMETER_MAX );
        String strUnique = request.getParameter( PARAMETER_UNIQUE );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strOnlyDisplayInBack = request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK );
        String strErrorMessage = request.getParameter( PARAMETER_ERROR_MESSAGE );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strSuffix = request.getParameter( PARAMETER_SUFFIX );
        String strPlaceholder = request.getParameter( PARAMETER_PLACEHOLDER );
        String strAutocomplete = request.getParameter( PARAMETER_AUTOCOMPLETE );

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = ERROR_FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        Integer defaultValue = null;
        if ( StringUtils.isNumeric( strValue ) )
        {
            defaultValue = Integer.parseInt( strValue );
        }

        Integer maxValue = null;
        if ( StringUtils.isNumeric( strMax ) )
        {
            maxValue = Integer.parseInt( strMax );
        }

        Integer minValue = null;
        if ( StringUtils.isNumeric( strMin ) )
        {
            minValue = Integer.parseInt( strMin );
        }

        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setIndexed( strIndexed != null );
        entry.setErrorMessage( strErrorMessage );
        entry.setCode( strCode );
        entry.setUnique( strUnique != null );

        Field fieldAutocomplete = GenericAttributesUtils.createOrUpdateField( entry, FIELD_AUTOCOMPLETE, null, null );
        if ( StringUtils.isNotBlank( strAutocomplete ) )
        {
            fieldAutocomplete.setValue( strAutocomplete.trim( ) );
        }

        GenericAttributesUtils.createOrUpdateField( entry, FIELD_TEXT_CONF, null, defaultValue == null ? null : String.valueOf( defaultValue ) );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_MIN, null, minValue == null ? null : String.valueOf( minValue ) );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_MAX, null, maxValue == null ? null : String.valueOf( maxValue ) );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_SUFFIX, null, StringUtils.isNotEmpty( strSuffix ) ? strSuffix : StringUtils.EMPTY );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_PLACEHOLDER, null, strPlaceholder != null ? strPlaceholder : StringUtils.EMPTY );

        entry.setMandatory( strMandatory != null );
        entry.setOnlyDisplayInBack( strOnlyDisplayInBack != null );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strValueEntry = StringUtils.trim( request.getParameter( PREFIX_ATTRIBUTE + entry.getIdEntry( ) ) );

        Response response = new Response( );
        response.setEntry( entry );
        response.setResponseValue( strValueEntry );
        if ( strValueEntry == null )
        {
            return null;
        }

        if ( StringUtils.isNotBlank( response.getResponseValue( ) ) )
        {
            response.setToStringValueResponse( getResponseValueForRecap( entry, request, response, locale ) );
        }
        else
        {
            response.setToStringValueResponse( StringUtils.EMPTY );
        }

        response.setIterationNumber( getResponseIterationValue( request ) );

        listResponse.add( response );

        return checkErrors( entry, strValueEntry, locale );
    }

    private GenericAttributeError checkErrors( Entry entry, String strValueEntry, Locale locale )
    {
        if ( entry.isMandatory( ) && StringUtils.isBlank( strValueEntry ) )
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

        if ( StringUtils.isNotBlank( strValueEntry ) && !StringUtils.isNumeric( strValueEntry ) )
        {
            GenericAttributeError error = new GenericAttributeError( );
            error.setMandatoryError( false );
            error.setTitleQuestion( entry.getTitle( ) );
            error.setErrorMessage( I18nService.getLocalizedString( MESSAGE_NUMERIC_FIELD, locale ) );

            return error;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return getResponseValue( entry, response );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return getResponseValue( entry, response );
    }

    /**
     * Get the response value
     * 
     * @param entry
     *            The entry
     * @return the response value of the response for this entry
     * @param response
     *            The response
     */
    private String getResponseValue( Entry entry, Response response )
    {
        if ( entry.getFields( ) == null )
        {
            entry.setFields( FieldHome.getFieldListByIdEntry( entry.getIdEntry( ) ) );
        }

        StringBuilder sb = new StringBuilder( );
        sb.append( response.getResponseValue( ) );

        Field field = entry.getFieldByCode( FIELD_SUFFIX );
        if ( field != null && StringUtils.isNotBlank( field.getValue( ) ) )
        {
            if ( !field.getValue( ).startsWith( StringUtils.SPACE ) )
            {
                sb.append( StringUtils.SPACE );
            }
            sb.append( field.getValue( ) );
        }
        return sb.toString( );
    }
}
