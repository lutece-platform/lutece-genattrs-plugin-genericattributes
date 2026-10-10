/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.function.IntSupplier;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.sql.DAOUtil;
import fr.paris.lutece.util.sql.TransactionManager;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Counter of the numbering entries in the table genatt_entry_numbering. The increment is an atomic UPDATE read back in the same transaction
 * (the row stays locked until the commit), so two submissions on two instances never get the same value.
 */
@ApplicationScoped
public class EntryNumberingDAO implements IEntryNumberingDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT last_value FROM genatt_entry_numbering WHERE id_entry = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO genatt_entry_numbering ( id_entry, last_value ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_INCREMENT = "UPDATE genatt_entry_numbering SET last_value = last_value + 1 WHERE id_entry = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM genatt_entry_numbering WHERE id_entry = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextValue( int nIdEntry, IntSupplier currentValue, Plugin plugin )
    {
        if ( select( nIdEntry, plugin ) == null )
        {
            createIfAbsent( nIdEntry, currentValue.getAsInt( ), plugin );
        }

        TransactionManager.beginTransaction( plugin );
        try
        {
            try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INCREMENT, plugin ) )
            {
                daoUtil.setInt( 1, nIdEntry );
                daoUtil.executeUpdate( );
            }
            Integer nValue = select( nIdEntry, plugin );
            TransactionManager.commitTransaction( plugin );
            return nValue;
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( plugin );
            throw new AppException( "Unable to get the next value of the numbering entry " + nIdEntry, e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdEntry, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdEntry );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Reads the counter of an entry
     *
     * @param nIdEntry
     *            The entry id
     * @param plugin
     *            The plugin
     * @return the value, or null if the counter does not exist
     */
    private Integer select( int nIdEntry, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdEntry );
            daoUtil.executeQuery( );
            return daoUtil.next( ) ? daoUtil.getInt( 1 ) : null;
        }
    }

    /**
     * Creates the counter of an entry; another instance may create it at the same time (primary key)
     *
     * @param nIdEntry
     *            The entry id
     * @param nValue
     *            The current value
     * @param plugin
     *            The plugin
     */
    private void createIfAbsent( int nIdEntry, int nValue, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( 1, nIdEntry );
            daoUtil.setInt( 2, nValue );
            daoUtil.executeUpdate( );
        }
        catch( AppException e )
        {
            if ( select( nIdEntry, plugin ) == null )
            {
                throw e;
            }
        }
    }
}
