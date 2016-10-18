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

import java.sql.Date;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for ReportingFiche objects
 */
public final class FieldDAO implements IFieldDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_field ) FROM genatt_field";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_field,id_entry,code,title,value,height,width,default_value,max_size_enter,pos,value_type_date,no_display_title,comment,role_key,image_type"
            + " FROM genatt_field  WHERE id_field = ? ORDER BY pos";
    private static final String SQL_QUERY_INSERT = "INSERT INTO genatt_field(id_field,id_entry,code,title,value,height,width,default_value,max_size_enter,pos,value_type_date,no_display_title,comment,role_key, image_type)"
            + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM genatt_field WHERE id_field = ? ";
    private static final String SQL_QUERY_INSERT_VERIF_BY = "INSERT INTO genatt_verify_by(id_field,id_expression) VALUES(?,?) ";
    private static final String SQL_QUERY_DELETE_VERIF_BY = "DELETE FROM genatt_verify_by WHERE id_field = ? and id_expression= ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE  genatt_field SET "
            + "id_field=?,id_entry=?,code=?,title=?,value=?,height=?,width=?,default_value=?,max_size_enter=?,pos=?,value_type_date=?,no_display_title=?,comment=?, role_key=?, image_type=? WHERE id_field = ?";
    private static final String SQL_QUERY_SELECT_FIELD_BY_ID_ENTRY = "SELECT id_field,id_entry,code,title,value,height,width,default_value,"
            + "max_size_enter,pos,value_type_date,no_display_title,comment,role_key, image_type FROM genatt_field  WHERE id_entry = ? ORDER BY pos";
    private static final String SQL_QUERY_NEW_POSITION = "SELECT MAX(pos)" + " FROM genatt_field ";
    private static final String SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_FIELD = "SELECT id_expression " + " FROM genatt_verify_by where id_field=?";
    private static final String SQL_QUERY_COUNT_FIELD_BY_ID_REGULAR_EXPRESSION = "SELECT COUNT(id_field) " + " FROM genatt_verify_by where id_expression = ?";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free( );

        return nKey;
    }

    /**
     * Generates a new field position
     * 
     * @param plugin
     *            the plugin
     * @return the new entry position
     */
    private int newPosition( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_POSITION, plugin );
        daoUtil.executeQuery( );

        int nPos;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nPos = 1;
        }

        nPos = daoUtil.getInt( 1 ) + 1;
        daoUtil.free( );

        return nPos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int insert( Field field, Plugin plugin )
    {
        field.setIdField( newPrimaryKey( plugin ) );
        field.setPosition( newPosition( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, field.getIdField( ) );
        daoUtil.setInt( 2, field.getParentEntry( ).getIdEntry( ) );
        daoUtil.setString( 3, field.getCode( ) );
        daoUtil.setString( 4, field.getTitle( ) );
        daoUtil.setString( 5, field.getValue( ) );
        daoUtil.setInt( 6, field.getHeight( ) );
        daoUtil.setInt( 7, field.getWidth( ) );
        daoUtil.setBoolean( 8, field.isDefaultValue( ) );
        daoUtil.setInt( 9, field.getMaxSizeEnter( ) );
        daoUtil.setInt( 10, field.getPosition( ) );
        daoUtil.setDate( 11, ( field.getValueTypeDate( ) == null ) ? null : new Date( field.getValueTypeDate( ).getTime( ) ) );
        daoUtil.setBoolean( 12, field.isNoDisplayTitle( ) );
        daoUtil.setString( 13, field.getComment( ) );
        daoUtil.setString( 14, field.getRoleKey( ) );
        daoUtil.setString( 15, field.getImageType( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );

        return field.getIdField( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        Field field = null;
        Entry entry = null;

        if ( daoUtil.next( ) )
        {
            field = new Field( );
            field.setIdField( daoUtil.getInt( 1 ) );
            // parent entry
            entry = new Entry( );
            entry.setIdEntry( daoUtil.getInt( 2 ) );
            field.setParentEntry( entry );
            field.setCode( daoUtil.getString( 3 ) );
            field.setTitle( daoUtil.getString( 4 ) );
            field.setValue( daoUtil.getString( 5 ) );
            field.setHeight( daoUtil.getInt( 6 ) );
            field.setWidth( daoUtil.getInt( 7 ) );
            field.setDefaultValue( daoUtil.getBoolean( 8 ) );
            field.setMaxSizeEnter( daoUtil.getInt( 9 ) );
            field.setPosition( daoUtil.getInt( 10 ) );
            field.setValueTypeDate( daoUtil.getDate( 11 ) );
            field.setNoDisplayTitle( daoUtil.getBoolean( 12 ) );
            field.setComment( daoUtil.getString( 13 ) );
            field.setRoleKey( daoUtil.getString( 14 ) );
            field.setImageType( daoUtil.getString( 15 ) );
        }

        daoUtil.free( );

        return field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdField, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdField );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Field field, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, field.getIdField( ) );
        daoUtil.setInt( 2, field.getParentEntry( ).getIdEntry( ) );
        daoUtil.setString( 3, field.getCode( ) );
        daoUtil.setString( 4, field.getTitle( ) );
        daoUtil.setString( 5, field.getValue( ) );
        daoUtil.setInt( 6, field.getHeight( ) );
        daoUtil.setInt( 7, field.getWidth( ) );
        daoUtil.setBoolean( 8, field.isDefaultValue( ) );
        daoUtil.setInt( 9, field.getMaxSizeEnter( ) );
        daoUtil.setInt( 10, field.getPosition( ) );
        daoUtil.setDate( 11, ( field.getValueTypeDate( ) == null ) ? null : new Date( field.getValueTypeDate( ).getTime( ) ) );
        daoUtil.setBoolean( 12, field.isNoDisplayTitle( ) );
        daoUtil.setString( 13, field.getComment( ) );
        daoUtil.setString( 14, field.getRoleKey( ) );
        daoUtil.setString( 15, field.getImageType( ) );

        daoUtil.setInt( 16, field.getIdField( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Field> selectFieldListByIdEntry( int nIdEntry, Plugin plugin )
    {
        List<Field> fieldList = new ArrayList<Field>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FIELD_BY_ID_ENTRY, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeQuery( );

        Field field = null;
        Entry entry = null;

        while ( daoUtil.next( ) )
        {
            field = new Field( );
            field.setIdField( daoUtil.getInt( 1 ) );
            // parent entry
            entry = new Entry( );
            entry.setIdEntry( daoUtil.getInt( 2 ) );
            field.setParentEntry( entry );
            field.setCode( daoUtil.getString( 3 ) );
            field.setTitle( daoUtil.getString( 4 ) );
            field.setValue( daoUtil.getString( 5 ) );
            field.setHeight( daoUtil.getInt( 6 ) );
            field.setWidth( daoUtil.getInt( 7 ) );
            field.setDefaultValue( daoUtil.getBoolean( 8 ) );
            field.setMaxSizeEnter( daoUtil.getInt( 9 ) );
            field.setPosition( daoUtil.getInt( 10 ) );
            field.setValueTypeDate( daoUtil.getDate( 11 ) );
            field.setNoDisplayTitle( daoUtil.getBoolean( 12 ) );
            field.setComment( daoUtil.getString( 13 ) );
            field.setRoleKey( daoUtil.getString( 14 ) );
            field.setImageType( daoUtil.getString( 15 ) );

            fieldList.add( field );
        }

        daoUtil.free( );

        return fieldList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteVerifyBy( int nIdField, int nIdExpression, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_VERIF_BY, plugin );
        daoUtil.setInt( 1, nIdField );
        daoUtil.setInt( 2, nIdExpression );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertVerifyBy( int nIdField, int nIdExpression, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_VERIF_BY, plugin );
        daoUtil.setInt( 1, nIdField );
        daoUtil.setInt( 2, nIdExpression );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectListRegularExpressionKeyByIdField( int nIdField, Plugin plugin )
    {
        List<Integer> regularExpressionList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_FIELD, plugin );
        daoUtil.setInt( 1, nIdField );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            regularExpressionList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return regularExpressionList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegularExpressionIsUse( int nIdExpression, Plugin plugin )
    {
        int nNumberEntry = 0;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_FIELD_BY_ID_REGULAR_EXPRESSION, plugin );
        daoUtil.setInt( 1, nIdExpression );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nNumberEntry = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nNumberEntry != 0;
    }
}
