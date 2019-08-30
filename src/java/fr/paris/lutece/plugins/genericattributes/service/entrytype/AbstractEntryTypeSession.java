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
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * Abstract entry type for sessions attributes This entry is used to fetch the value of a session's attribute. One example is when coupling form with crm, the
 * module-crm-form will put in session the ID demand and the user GUID. This entry will be able to fetch the ID demand and user GUID when validating the form.
 * Then, it is easier to export the value to directory with the module-form-exportdirectory.
 *
 */
public abstract class AbstractEntryTypeSession extends EntryTypeService
{
    private static final String FIELD_ATTRIBUTE_NAME = "genericattributes.createEntry.labelAttributeName";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strAttibuteName = request.getParameter( PARAMETER_VALUE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = ERROR_FIELD_TITLE;
        }
        else
            if ( StringUtils.isBlank( strAttibuteName ) )
            {
                strFieldError = FIELD_ATTRIBUTE_NAME;
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
        entry.setHelpMessage( StringUtils.EMPTY );
        entry.setComment( StringUtils.EMPTY );
        entry.setMandatory( StringUtils.isNotEmpty( strMandatory ) );
        entry.setConfirmField( false );
        entry.setConfirmFieldTitle( null );
        entry.setUnique( false );

        if ( entry.getFields( ) == null )
        {
            List<Field> listFields = new ArrayList<Field>( );
            Field field = new Field( );
            listFields.add( field );
            entry.setFields( listFields );
        }

        entry.getFields( ).get( 0 ).setValue( strAttibuteName );
        entry.getFields( ).get( 0 ).setWidth( 0 );
        entry.getFields( ).get( 0 ).setMaxSizeEnter( 0 );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strValueEntry = StringUtils.EMPTY;
        HttpSession session = request.getSession( false );

        if ( session != null )
        {
            if ( ( entry.getFields( ) != null ) && !entry.getFields( ).isEmpty( ) && ( entry.getFields( ).get( 0 ) != null ) )
            {
                String strAttributeName = entry.getFields( ).get( 0 ).getValue( );
                strValueEntry = (String) session.getAttribute( strAttributeName );
            }
        }

        if ( StringUtils.isNotBlank( strValueEntry ) )
        {
            Response response = new Response( );
            response.setEntry( entry );
            response.setResponseValue( strValueEntry );
            response.setToStringValueResponse( StringUtils.EMPTY );
            response.setIterationNumber( getResponseIterationValue( request ) );

            listResponse.add( response );
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
        return StringUtils.EMPTY;
    }
}
