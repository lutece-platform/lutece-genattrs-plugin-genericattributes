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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * class ReferenceItemFieldDao
 *
 */
@ApplicationScoped
public class ReferenceItemFieldDao implements IReferenceItemFieldDao
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO genatt_referenceitem_field ( id_field, id_item ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE_BY_ITEM = "DELETE FROM genatt_referenceitem_field WHERE id_item = ? ";
    private static final String SQL_QUERY_DELETE_BY_FIELD = "DELETE FROM genatt_referenceitem_field WHERE id_field = ? ";
    private static final String SQL_QUERY_SELECT_FIELD_BY_ITEM = "SELECT id_field FROM genatt_referenceitem_field WHERE id_item = ? ";
    private static final String SQL_QUERY_SELECT_ITEM_BY_FIELD = "SELECT id_item FROM genatt_referenceitem_field WHERE id_field = ? ";

    @Override
    public void insert( int idField, int idReferenceItem, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( 1, idField );
            daoUtil.setInt( 2, idReferenceItem );
            daoUtil.executeUpdate( );
        }

    }

    @Override
    public void deleteByIdItem( int idReferenceItem, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ITEM, plugin ) )
        {
            daoUtil.setInt( 1, idReferenceItem );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByIdField( int idField, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_FIELD, plugin ) )
        {
            daoUtil.setInt( 1, idField );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<Integer> loadFieldByItem( int idReferenceItem, Plugin plugin )
    {
        List<Integer> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FIELD_BY_ITEM, plugin ) )
        {
            daoUtil.setInt( 1, idReferenceItem );

            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                list.add( daoUtil.getInt( 1 ) );
            }
        }

        return list;
    }

    @Override
    public Integer loadItemByField( int idField, Plugin plugin )
    {
        Integer idItem = -1;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ITEM_BY_FIELD, plugin ) )
        {
            daoUtil.setInt( 1, idField );

            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                idItem = daoUtil.getInt( 1 );
            }
        }

        return idItem;
    }
}
