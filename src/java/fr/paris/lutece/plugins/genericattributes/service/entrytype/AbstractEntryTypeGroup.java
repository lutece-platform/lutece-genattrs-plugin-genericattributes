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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Abstract entry type for groups
 */
public abstract class AbstractEntryTypeGroup extends EntryTypeService
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
    	return getRequestData( entry, request, locale, null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale, String errorReturnUrl )
    {
        initCommonRequestData( entry, request );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strFieldError = StringUtils.EMPTY;
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_INSERT_GROUP;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale )
            };

            if ( StringUtils.isNotBlank( errorReturnUrl ) )
            {
            	return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, errorReturnUrl, AdminMessage.TYPE_STOP );
            }
            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        entry.setCode( strCode );
        entry.setTitle( strTitle );
        entry.setCSSClass( strCSSClass );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initCommonRequestData( Entry entry, HttpServletRequest request )
    {
        if ( entry.getFields( ) == null )
        {
            entry.setFields( new ArrayList<>( ) );
        }
        // Initialize the field for this EntryType's activation status
        String strDisabled = request.getParameter( PARAMETER_DISABLED );
        GenericAttributesUtils.createOrUpdateField( entry, IEntryTypeService.FIELD_DISABLED, null, String.valueOf( strDisabled != null ) );
    }
}
