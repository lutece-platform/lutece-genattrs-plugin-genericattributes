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
package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import java.util.Locale;

import fr.paris.lutece.portal.service.i18n.I18nService;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;


public class EntryAnonymizationType implements IEntryAnonymizationType
{
    private String _wildcard;
    private String _helpKey;
    private String _serviceName;

    /**
     * Constructor
     * 
     * @param wildcard
     * @param helpKey
     * @param serviceName
     */
    public EntryAnonymizationType( String wildcard, String helpKey, String serviceName )
    {
        super( );
        _wildcard = wildcard;
        _helpKey = helpKey;
        _serviceName = serviceName;
    }

    /**
     * @return the wildcard
     */
    public String getWildcard( )
    {
        return _wildcard;
    }

    /**
     * @return the helpKey
     */
    public String getHelpKey( )
    {
        return _helpKey;
    }

    @Override
    public String getHelpMessage( Locale locale )
    {
        return getWildcard( ) + " " + I18nService.getLocalizedString( getHelpKey( ), locale );
    }

    @Override
    public IEntryTypeAnonymisationService getAnonymisationTypeService( )
    {
        return CDI.current( ).select( IEntryTypeAnonymisationService.class, NamedLiteral.of( _serviceName ) ).get( ).withWildcard( _wildcard );
    }
}
