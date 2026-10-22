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
package fr.paris.lutece.plugins.genericattributes.service;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.BeanUtils;
import fr.paris.lutece.portal.service.util.RemovalListener;
import fr.paris.lutece.portal.service.util.RemovalListenerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Refuses the removal of a regular expression while a field of an entry is verified by it: once removed, the entry could no longer check its
 * answers.
 */
@ApplicationScoped
public class GenericAttributesRegularExpressionRemovalListener implements RemovalListener
{
    private static final String MESSAGE_REGULAR_EXPRESSION_USED = "genericattributes.message.regularExpressionUsed";
    private static final String SEPARATOR = ", ";
    private static final String QUOTE = "\"";

    @Inject
    @Named( BeanUtils.BEAN_REGULAR_EXPRESSION_REMOVAL_SERVICE )
    private RemovalListenerService _regularExpressionRemovalService;

    private boolean _bRegistered;

    /**
     * Registers this listener with the removal service of the regular expressions when the application starts.
     *
     * @param context
     *            the context of the started application
     */
    public void register( @Observes @Initialized( ApplicationScoped.class ) Object context )
    {
        if ( !_bRegistered )
        {
            _regularExpressionRemovalService.registerListener( this );
            _bRegistered = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRemoved( String strId )
    {
        return !StringUtils.isNumeric( StringUtils.trim( strId ) ) || !FieldHome.isRegularExpressionIsUse( Integer.parseInt( strId.trim( ) ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemovalRefusedMessage( String strId, Locale locale )
    {
        Set<String> setTitles = new LinkedHashSet<>( );
        if ( StringUtils.isNumeric( StringUtils.trim( strId ) ) )
        {
            for ( String strTitle : FieldHome.getEntryTitlesByRegularExpression( Integer.parseInt( strId.trim( ) ) ) )
            {
                setTitles.add( QUOTE + StringUtils.defaultString( strTitle ) + QUOTE );
            }
        }
        return I18nService.getLocalizedString( MESSAGE_REGULAR_EXPRESSION_USED, new Object [ ] {
                String.join( SEPARATOR, setTitles )
        }, locale );
    }
}
