/*
 * Copyright (c) 2002-2021, City of Paris
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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.EntryTypeNumberingUtil;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

/**
 * Abstract entry type for incremental fields
 */
public abstract class AbstractEntryTypeNumbering extends EntryTypeService
{
    // PARAMETERS
    private static final String PARAMETER_PREFIX = "prefix";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strPrefix = request.getParameter( PARAMETER_PREFIX );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );

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

        GenericAttributesUtils.createOrUpdateField( entry, FIELD_PREFIX, null, StringUtils.isNotEmpty( strPrefix ) ? strPrefix : StringUtils.EMPTY );
        entry.setIndexed( strIndexed != null );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        int numbering = EntryTypeNumberingUtil.getInstance( ).getNextValue( entry.getIdEntry( ) );
        Response response = new Response( );
        response.setEntry( entry );
        response.setResponseValue( String.valueOf( numbering ) );
        response.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( response );

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
        Field field = entry.getFieldByCode( FIELD_PREFIX );

        if ( field != null && StringUtils.isNotBlank( field.getValue( ) ) )
        {
            return field.getValue( ) + response.getResponseValue( );
        }

        return response.getResponseValue( );
    }
}
