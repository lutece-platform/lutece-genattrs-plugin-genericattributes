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
package fr.paris.lutece.plugins.genericattributes.business;

import java.util.List;

import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 *
 * class ReferenceItemFieldHome
 *
 */
public class ReferenceItemFieldHome
{
    private static IReferenceItemFieldDao _dao = SpringContextService.getBean( "genericattributes.referenceItemFieldDao" );
    private static Plugin _plugin = GenericAttributesUtils.getPlugin( );

    private ReferenceItemFieldHome( )
    {
    }

    /**
     * Create a new record in the database
     * 
     * @param idField
     * @param idItem
     */
    public static void create( int idField, int idItem )
    {
        _dao.insert( idField, idItem, _plugin );
    }

    /**
     * Deletes record from the database
     * 
     * @param idItem
     */
    public static void removeByItem( int idItem )
    {
        _dao.deleteByIdItem( idItem, _plugin );
    }

    /**
     * Deletes record from the database
     * 
     * @param idItem
     */
    public static void removeByField( int idField )
    {
        _dao.deleteByIdField( idField, _plugin );
    }

    /**
     * Loads records form the database.
     * 
     * @param idItem
     * @return
     */
    public static List<Integer> findIdFieldByIdItem( int idItem )
    {
        return _dao.loadFieldByItem( idItem, _plugin );
    }

    /**
     * Loads records form the database.
     * 
     * @param idField
     * @return
     */
    public static Integer findIdItemByIdField( int idField )
    {
        return _dao.loadItemByField( idField, _plugin );
    }
}
