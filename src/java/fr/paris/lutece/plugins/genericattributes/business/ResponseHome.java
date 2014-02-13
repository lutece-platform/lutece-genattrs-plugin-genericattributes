/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for
 * Response objects
 */
public final class ResponseHome
{
    // Static variable pointed at the DAO instance
    private static IResponseDAO _dao = SpringContextService.getBean( "genericattributes.responseDAO" );
    private static Plugin _plugin;

    /**
     * Private constructor - this class need not be instantiated
     */
    private ResponseHome(  )
    {
    }

    /**
     * Creation of an instance of response
     *
     * @param response The instance of the response which contains the
     *            informations to store
     *
     */
    public static void create( Response response )
    {
        if ( response.getFile(  ) != null )
        {
            FileHome.create( response.getFile(  ) );
        }

        _dao.insert( response, getPlugin(  ) );
    }

    /**
     * Update of the response which is specified in parameter
     *
     * @param response The instance of the Response which contains the
     *            informations to update
     *
     */
    public static void update( Response response )
    {
        if ( response.getFile(  ) != null )
        {
            FileHome.update( response.getFile(  ) );
        }

        _dao.store( response, getPlugin(  ) );
    }

    /**
     * Remove a response from its id
     * @param nIdResponse The id of the response
     */
    public static void remove( int nIdResponse )
    {
        Response response = findByPrimaryKey( nIdResponse );

        if ( response != null )
        {
            if ( response.getFile(  ) != null )
            {
                FileHome.remove( response.getFile(  ).getIdFile(  ) );
            }

            _dao.delete( nIdResponse, getPlugin(  ) );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Response whose identifier is specified in
     * parameter
     *
     * @param nKey The entry primary key
     * @return an instance of Response
     */
    public static Response findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, getPlugin(  ) );
    }

    /**
     * Load the data of all the response who verify the filter and returns them
     * in a list
     * @param filter the filter
     * @return the list of response
     */
    public static List<Response> getResponseList( ResponseFilter filter )
    {
        return _dao.selectListByFilter( filter, getPlugin(  ) );
    }

    /**
     * return a list of statistic on the entry
     * @param nIdEntry the id of the entry
     * @return return a list of statistic on the entry
     */
    public static List<StatisticEntrySubmit> getStatisticByIdEntry( int nIdEntry )
    {
        return _dao.getStatisticByIdEntry( nIdEntry, getPlugin(  ) );
    }

    /**
     * Get the max number from a given id resource
     * @param nIdEntry the id of the entry
     * @param nIdResource the id resource
     * @param strResourceType The resource type
     * @return the max number
     */
    public static int findMaxNumber( int nIdEntry, int nIdResource, String strResourceType )
    {
        return _dao.getMaxNumber( nIdEntry, nIdResource, strResourceType, getPlugin(  ) );
    }

    /**
     * Get the generic attributes plugin
     * @return The generic attributes plugin
     */
    private static Plugin getPlugin(  )
    {
        if ( _plugin == null )
        {
            _plugin = GenericAttributesUtils.getPlugin(  );
        }

        return _plugin;
    }
}
