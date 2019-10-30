/*
 * Copyright (c) 2002-2019, Mairie de Paris
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

import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

/**
 *
 * class EntryTypeHome
 *
 */
public final class EntryTypeHome
{
    // Static variable pointed at the DAO instance
    private static IEntryTypeDAO _dao = SpringContextService.getBean( "genericattributes.entryTypeDAO" );
    private static Plugin _plugin;

    /**
     * Private constructor - this class need not be instantiated
     */
    private EntryTypeHome( )
    {
    }

    /**
     * Get the generic attributes plugin
     * 
     * @return The generic attributes plugin
     */
    private static Plugin getPlugin( )
    {
        if ( _plugin == null )
        {
            _plugin = GenericAttributesUtils.getPlugin( );
        }

        return _plugin;
    }

    /**
     * Returns an instance of a EntryType whose identifier is specified in parameter
     *
     * @param nKey
     *            The entry type primary key
     * @return an instance of EntryType
     */
    public static EntryType findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, getPlugin( ) );
    }

    /**
     * Load entry types associated with a given plugin and returns them in a list
     * 
     * @param strPluginName
     *            The name of plugin to get entry types of
     * @return the list of entry type associated with the plugin
     */
    public static List<EntryType> getList( String strPluginName )
    {
        return _dao.select( strPluginName, getPlugin( ) );
    }
}
