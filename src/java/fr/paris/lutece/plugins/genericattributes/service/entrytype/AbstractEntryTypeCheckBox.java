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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

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
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * Abstract entry type for check boxes
 */
public abstract class AbstractEntryTypeCheckBox extends EntryTypeService
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strErrorMessage = request.getParameter( PARAMETER_ERROR_MESSAGE );
        String strFieldInLine = request.getParameter( PARAMETER_FIELD_IN_LINE );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strOnlyDisplayInBack = request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK );

        int nFieldInLine = -1;

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        // for don't update fields listFields=null
        entry.setFields( null );
        entry.setCode( strCode );
        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );

        entry.setMandatory( strMandatory != null );
        entry.setOnlyDisplayInBack( strOnlyDisplayInBack != null );
        entry.setErrorMessage( strErrorMessage );

        try
        {
            nFieldInLine = Integer.parseInt( strFieldInLine );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        entry.setFieldInLine( nFieldInLine == 1 );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse,
        Locale locale )
    {
        String[] strTabIdField = request.getParameterValues( PREFIX_ATTRIBUTE + entry.getIdEntry(  ) );
        List<Field> listFieldInResponse = new ArrayList<Field>(  );
        int nIdField = -1;
        Field field = null;
        Response response;

        if ( strTabIdField != null )
        {
            for ( int cpt = 0; cpt < strTabIdField.length; cpt++ )
            {
                try
                {
                    nIdField = Integer.parseInt( strTabIdField[cpt] );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne.getMessage(  ), ne );
                }

                field = GenericAttributesUtils.findFieldByIdInTheList( nIdField, entry.getFields(  ) );

                if ( field != null )
                {
                    listFieldInResponse.add( field );
                }
            }
        }

        if ( listFieldInResponse.size(  ) != 0 )
        {
            for ( Field fieldInResponse : listFieldInResponse )
            {
                response = new Response(  );
                response.setEntry( entry );
                response.setResponseValue( fieldInResponse.getValue(  ) );
                response.setField( fieldInResponse );
                listResponse.add( response );
            }
        }

        if ( entry.isMandatory(  ) )
        {
            boolean bAllFieldEmpty = true;

            for ( Field fieldInResponse : listFieldInResponse )
            {
                if ( !fieldInResponse.getValue(  ).equals( StringUtils.EMPTY ) )
                {
                    bAllFieldEmpty = false;
                }
            }

            if ( bAllFieldEmpty )
            {
                if ( StringUtils.isNotBlank( entry.getErrorMessage(  ) ) )
                {
                    GenericAttributeError error = new GenericAttributeError(  );
                    error.setMandatoryError( true );
                    error.setErrorMessage( entry.getErrorMessage(  ) );

                    return error;
                }

                return new MandatoryError( entry, locale );
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
        return response.getResponseValue(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( response.getField(  ) != null )
        {
            if ( response.getField(  ).getTitle(  ) == null )
            {
                Field field = FieldHome.findByPrimaryKey( response.getField(  ).getIdField(  ) );

                if ( field != null )
                {
                    response.setField( field );
                }
            }

            return response.getField(  ).getTitle(  );
        }

        return null;
    }
}
