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
package fr.paris.lutece.plugins.genericattributes.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * class EntryTypeDAO
 *
 */
public class EntryTypeDAO implements IEntryTypeDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_type,title,is_group,is_comment,class_name,icon_name,is_mylutece_user,plugin"
            + " FROM genatt_entry_type WHERE id_type=?";
    private static final String SQL_QUERY_SELECT = "SELECT id_type,title,is_group,is_comment,class_name,icon_name,is_mylutece_user,plugin"
            + " FROM genatt_entry_type WHERE plugin = ?  order by id_type";

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryType load( int idKey, Plugin plugin )
    {
        EntryType entryType = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
        daoUtil.setInt( 1, idKey );
        daoUtil.executeQuery( );

        

        if ( daoUtil.next( ) )
        {
            entryType = new EntryType( );
            entryType.setIdType( daoUtil.getInt( 1 ) );
            entryType.setTitle( daoUtil.getString( 2 ) );
            entryType.setGroup( daoUtil.getBoolean( 3 ) );
            entryType.setComment( daoUtil.getBoolean( 4 ) );
            entryType.setBeanName( daoUtil.getString( 5 ) );
            entryType.setIconName( daoUtil.getString( 6 ) );
            entryType.setMyLuteceUser( daoUtil.getBoolean( 7 ) );
            entryType.setPlugin( daoUtil.getString( 8 ) );
        }

        }

        return entryType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EntryType> select( String strPlugin, Plugin plugin )
    {
        List<EntryType> listEntryType = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
        daoUtil.setString( 1, strPlugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            EntryType entryType = new EntryType( );
            entryType.setIdType( daoUtil.getInt( 1 ) );
            entryType.setTitle( daoUtil.getString( 2 ) );
            entryType.setGroup( daoUtil.getBoolean( 3 ) );
            entryType.setComment( daoUtil.getBoolean( 4 ) );
            entryType.setBeanName( daoUtil.getString( 5 ) );
            entryType.setIconName( daoUtil.getString( 6 ) );
            entryType.setMyLuteceUser( daoUtil.getBoolean( 7 ) );
            entryType.setPlugin( daoUtil.getString( 8 ) );
            listEntryType.add( entryType );
        }

        }

        return listEntryType;
    }
}
