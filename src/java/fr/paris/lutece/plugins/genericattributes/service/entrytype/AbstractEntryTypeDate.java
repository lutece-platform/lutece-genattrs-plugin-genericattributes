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

import java.text.SimpleDateFormat;
import java.util.Date;
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
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.string.StringUtil;

/**
 * Abstract entry type for dates
 */
public abstract class AbstractEntryTypeDate extends EntryTypeService
{
    private static final String MESSAGE_ILLOGICAL_DATE = "genericattributes.message.illogicalDate";

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
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strOnlyDisplayInBack = request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strPlaceholder = request.getParameter( PARAMETER_PLACEHOLDER );

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

        Date dDateValue = null;

        if ( StringUtils.isNotBlank( strValue ) )
        {
            dDateValue = DateUtil.parseIsoDate( strValue );
            if ( dDateValue == null )
            {
                Object [ ] messageArgs = new Object [ ] {
                        DateUtil.ISO_PATTERN_DATE
                };
                return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_DATE, messageArgs, AdminMessage.TYPE_STOP );
            }
        }

        entry.setCode( strCode );
        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );

        GenericAttributesUtils.createOrUpdateField( entry, FIELD_PLACEHOLDER, null, strPlaceholder != null ? strPlaceholder : StringUtils.EMPTY );
        Field field = GenericAttributesUtils.createOrUpdateField( entry, FIELD_DATE_VALUE, null, null );
        field.setValueTypeDate( dDateValue );

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
        String strValueEntry = StringUtils.trim( request.getParameter( PREFIX_ATTRIBUTE + entry.getIdEntry( ) ) );
        Response response = new Response( );
        response.setEntry( entry );

        if ( strValueEntry == null )
        {
            return null;
        }

        Date tDateValue = DateUtil.parseIsoDate( strValueEntry );

        if ( tDateValue != null )
        {
            response.setResponseValue( String.valueOf( tDateValue.getTime( ) ) );
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

        // Checks if the entry value contains XSS characters
        if ( StringUtil.containsXssCharacters( strValueEntry ) )
        {
            GenericAttributeError error = new GenericAttributeError( );
            error.setMandatoryError( false );
            error.setTitleQuestion( entry.getTitle( ) );
            error.setErrorMessage( I18nService.getLocalizedString( MESSAGE_XSS_FIELD, request.getLocale( ) ) );

            return error;
        }

        if ( entry.isMandatory( ) && StringUtils.isBlank( strValueEntry ) )
        {
            return new MandatoryError( entry, locale );
        }

        if ( StringUtils.isNotBlank( strValueEntry ) && ( tDateValue == null ) )
        {
            SimpleDateFormat sdf = (SimpleDateFormat) DateUtil.getDateFormat( locale );
            Object [ ] messageArgs = new Object [ ] {
                    sdf.toPattern( )
            };

            String strError = I18nService.getLocalizedString( MESSAGE_ILLOGICAL_DATE, messageArgs, locale );
            GenericAttributeError error = new GenericAttributeError( );
            error.setTitleQuestion( entry.getTitle( ) );
            error.setMandatoryError( false );
            error.setErrorMessage( strError );

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
        return getResponseValueForRecap( entry, request, response, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        String strValueEntry = response.getResponseValue( );
        if ( StringUtils.isEmpty( strValueEntry ) )
        {
            return StringUtils.EMPTY;
        }
        Date date = new Date( Long.valueOf( strValueEntry ) );
        return DateUtil.getDateString( date, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponseToStringValue( Entry entry, Response response, Locale locale )
    {
        String strValueEntry = response.getResponseValue( );
        if ( StringUtils.isNotEmpty( strValueEntry ) )
        {
            Date tDateValue = DateUtil.formatDate( response.getResponseValue( ), locale );
            if ( tDateValue != null )
            {
                response.setResponseValue( String.valueOf( tDateValue.getTime( ) ) );
            }
        }
    }
}
