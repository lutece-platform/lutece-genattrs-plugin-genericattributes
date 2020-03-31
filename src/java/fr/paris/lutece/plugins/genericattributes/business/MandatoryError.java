/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.genericattributes.business;

import fr.paris.lutece.portal.service.i18n.I18nService;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;

/**
 * Error that indicates that a mandatory field has not been set
 */
public class MandatoryError extends GenericAttributeError
{
    private static final long serialVersionUID = 6954786925249205627L;
    private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryField";

    /**
     * Creates a new mandatory error
     * 
     * @param entry
     *            the entry
     * @param locale
     *            the locale
     */
    public MandatoryError( Entry entry, Locale locale )
    {
        if ( ( entry != null ) && StringUtils.isNotBlank( entry.getTitle( ) ) )
        {
            this.setTitleQuestion( entry.getTitle( ) );

            Object [ ] param = {
                    entry.getTitle( )
            };
            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_MANDATORY_FIELD, param, locale );
            this.setErrorMessage( strErrorMessage );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMandatoryError( )
    {
        return true;
    }
}
