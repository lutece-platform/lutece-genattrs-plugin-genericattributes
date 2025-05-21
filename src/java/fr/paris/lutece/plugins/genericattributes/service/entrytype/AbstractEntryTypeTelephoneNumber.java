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
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.string.StringUtil;

/**
 * Abstract entry type for telephone number
 */
public abstract class AbstractEntryTypeTelephoneNumber extends EntryTypeService
{

    private static final String PARAMETER_DEFAULT_REGION = "defaultRegion";
    public static final String FIELD_DEFAULT_REGION = "defaultRegion";
    private static final String MESSAGE_ERROR_PHONENUMBER = "genericattributes.message.error.phonenumber";
    private static final String MESSAGE_UNKNOWN_DEFAULT_REGION = "genericattributes.message.error.unknown.default.region";
    private static final String PROPERTY_DEFAULT_DEFAULT_REGION = "genericattributes.telephoneNumber.default.default.region";

    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strErrorMessage = request.getParameter( PARAMETER_ERROR_MESSAGE );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strOnlyDisplayInBack = request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strAutocomplete = request.getParameter( PARAMETER_AUTOCOMPLETE );
        String strDefaultRegion = request.getParameter( PARAMETER_DEFAULT_REGION );
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

        entry.setCode( strCode );
        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );

        Field fieldAutocomplete = GenericAttributesUtils.createOrUpdateField( entry, FIELD_AUTOCOMPLETE, null, null );
        if ( StringUtils.isNotBlank( strAutocomplete ) )
        {
            fieldAutocomplete.setValue( strAutocomplete.trim( ) );
        }
        Field fieldDefaultRegion = GenericAttributesUtils.createOrUpdateField( entry, FIELD_DEFAULT_REGION, null, null );
        if ( StringUtils.isNotBlank( strDefaultRegion ) )
        {
            if ( !PhoneNumberUtil.getInstance( ).getSupportedRegions( ).contains( strDefaultRegion ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_UNKNOWN_DEFAULT_REGION, new Object [ ] {
                        strDefaultRegion
                }, AdminMessage.TYPE_STOP );
            }
            fieldDefaultRegion.setValue( strDefaultRegion );
        }
        else
        {
            fieldDefaultRegion.setValue( getDefaultDefaultRegion( ) );
        }

        GenericAttributesUtils.createOrUpdateField( entry, FIELD_PLACEHOLDER, null, strPlaceholder != null ? strPlaceholder : StringUtils.EMPTY );

        entry.setMandatory( strMandatory != null );
        entry.setErrorMessage( strErrorMessage );
        entry.setOnlyDisplayInBack( strOnlyDisplayInBack != null );
        entry.setIndexed( strIndexed != null );

        return null;

    }

    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strValueEntry = StringUtils.defaultString( request.getParameter( PREFIX_ATTRIBUTE + entry.getIdEntry( ) ) ).trim( );
        Response response = new Response( );
        response.setEntry( entry );
        response.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( response );

        response.setResponseValue( strValueEntry );

        if ( StringUtils.isNotBlank( response.getResponseValue( ) ) )
        {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance( );
            PhoneNumber number;
            try
            {
                number = phoneUtil.parse( strValueEntry, entry.getFieldByCode( FIELD_DEFAULT_REGION ).getValue( ) );
            }
            catch( NumberParseException e )
            {
                String strKeyErrorMessage;
                switch( e.getErrorType( ) )
                {
                    case INVALID_COUNTRY_CODE:
                    case NOT_A_NUMBER:
                    case TOO_LONG:
                    case TOO_SHORT_AFTER_IDD:
                    case TOO_SHORT_NSN:
                        strKeyErrorMessage = MESSAGE_ERROR_PHONENUMBER + "." + e.getErrorType( ).name( );
                        break;
                    default:
                        strKeyErrorMessage = MESSAGE_ERROR_PHONENUMBER;
                        break;
                }
                GenericAttributeError error = new GenericAttributeError( );
                error.setMandatoryError( false );
                error.setTitleQuestion( entry.getTitle( ) );
                error.setErrorMessage( I18nService.getLocalizedString( strKeyErrorMessage, request.getLocale( ) ) );

                return error;
            }
            if ( !phoneUtil.isValidNumber( number ) )
            {
                GenericAttributeError error = new GenericAttributeError( );
                error.setMandatoryError( false );
                error.setTitleQuestion( entry.getTitle( ) );
                error.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_PHONENUMBER, request.getLocale( ) ) );

                return error;
            }

            response.setResponseValue( phoneUtil.format( number, PhoneNumberFormat.E164 ) ); // normalized
                                                                                             // value
            response.setToStringValueResponse( getResponseValueForRecap( entry, request, response, locale ) );
        }
        else
        {
            response.setToStringValueResponse( StringUtils.EMPTY );
        }

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
            if ( StringUtils.isNotEmpty( entry.getErrorMessage( ) ) )
            {
                GenericAttributeError error = new GenericAttributeError( );
                error.setMandatoryError( true );
                error.setErrorMessage( entry.getErrorMessage( ) );

                return error;
            }

            return new MandatoryError( entry, locale );
        }

        return null;

    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Export the normalized value.
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        // export the normalized value
        return response.getResponseValue( );
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * If the phone number is in the default region, display in this region format. If not, display in international format.
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance( );
        PhoneNumber number;
        try
        {
            number = phoneUtil.parse( response.getResponseValue( ), entry.getFieldByCode( FIELD_DEFAULT_REGION ).getValue( ) );
            if ( "FR".equals( phoneUtil.getRegionCodeForNumber( number ) ) )
            {
                return phoneUtil.format( number, PhoneNumberFormat.NATIONAL );
            }
            return phoneUtil.format( number, PhoneNumberFormat.INTERNATIONAL );
        }
        catch( NumberParseException e )
        {
            return null;
        }
    }

    /**
     * Supported region codes for selection in admin interface
     * 
     * @return the support region codes set
     */
    public List<String> getSupportedRegionCodes( )
    {
        return PhoneNumberUtil.getInstance( ).getSupportedRegions( ).stream( ).sorted( ).collect( Collectors.toList( ) );
    }

    /**
     * Gets the default default region
     * 
     * @return the default default region
     */
    public String getDefaultDefaultRegion( )
    {
        return AppPropertiesService.getProperty( PROPERTY_DEFAULT_DEFAULT_REGION, "FR" );
    }
}
