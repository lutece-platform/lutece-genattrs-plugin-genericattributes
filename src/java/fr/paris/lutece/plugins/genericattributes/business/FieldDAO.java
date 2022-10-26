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

import java.sql.Date;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for ReportingFiche objects
 */
public final class FieldDAO implements IFieldDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_field,id_entry,code,title,value,default_value,pos,value_type_date,no_display_title,comment,id_file_key"
            + " FROM genatt_field ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_QUERY_SELECT_ALL + " WHERE id_field = ? ORDER BY pos";
    private static final String SQL_QUERY_FIND_BY_CODE = SQL_QUERY_SELECT_ALL + " WHERE code = ? ORDER BY pos";
    private static final String SQL_QUERY_INSERT = "INSERT INTO genatt_field(id_entry,code,title,value,default_value,pos,value_type_date,no_display_title,comment,id_file_key)"
            + " VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM genatt_field WHERE id_field = ? ";
    private static final String SQL_QUERY_INSERT_VERIF_BY = "INSERT INTO genatt_verify_by(id_field,id_expression) VALUES(?,?) ";
    private static final String SQL_QUERY_DELETE_VERIF_BY = "DELETE FROM genatt_verify_by WHERE id_field = ? and id_expression= ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE genatt_field SET "
            + "id_field=?,id_entry=?,code=?,title=?,value=?,default_value=?,pos=?,value_type_date=?,no_display_title=?,comment=?,id_file_key=? WHERE id_field = ?";
    private static final String SQL_QUERY_SELECT_FIELD_BY_ID_ENTRY = SQL_QUERY_SELECT_ALL + " WHERE id_entry = ? ORDER BY pos";
    private static final String SQL_QUERY_NEW_POSITION = "SELECT MAX(pos)" + " FROM genatt_field ";
    private static final String SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_FIELD = "SELECT id_expression " + " FROM genatt_verify_by where id_field=?";
    private static final String SQL_QUERY_COUNT_FIELD_BY_ID_REGULAR_EXPRESSION = "SELECT COUNT(id_field) " + " FROM genatt_verify_by where id_expression = ?";
    private static final String SQL_QUERY_SELECT_FIELD_BY_LIST_ID_ENTRY = SQL_QUERY_SELECT_ALL + " WHERE id_entry IN ";
    
    private static final String PUBLIC_IMAGE_RESOURCE = "public_image_resource";
    /**
     * Generates a new field position
     * 
     * @param plugin
     *            the plugin
     * @return the new entry position
     */
    private int newPosition( Plugin plugin )
    {
        int nPos;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_POSITION, plugin ) )
        {
            daoUtil.executeQuery( );

            if ( !daoUtil.next( ) )
            {
                // if the table is empty
                nPos = 1;
            }

            nPos = daoUtil.getInt( 1 ) + 1;
        }

        return nPos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int insert( Field field, Plugin plugin )
    {
        field.setPosition( newPosition( plugin ) );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, field.getParentEntry( ).getIdEntry( ) );
            daoUtil.setString( nIndex++, field.getCode( ) );
            daoUtil.setString( nIndex++, field.getTitle( ) );
            daoUtil.setString( nIndex++, field.getValue( ) );
            daoUtil.setBoolean( nIndex++, field.isDefaultValue( ) );
            daoUtil.setInt( nIndex++, field.getPosition( ) );
            daoUtil.setDate( nIndex++, ( field.getValueTypeDate( ) == null ) ? null : new Date( field.getValueTypeDate( ).getTime( ) ) );
            daoUtil.setBoolean( nIndex++, field.isNoDisplayTitle( ) );
            daoUtil.setString( nIndex++, field.getComment( ) );
            daoUtil.setString( nIndex++, ( field.getFileImage( ) == null ) ? null : field.getFileImage( ).getFileKey( ) );
            

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                field.setIdField( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        return field.getIdField( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field load( int nId, Plugin plugin )
    {
        Field field = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                field = dataToObject( daoUtil );
            }

        }
        return field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdField, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdField );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Field field, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, field.getIdField( ) );
            daoUtil.setInt( nIndex++, field.getParentEntry( ).getIdEntry( ) );
            daoUtil.setString( nIndex++, field.getCode( ) );
            daoUtil.setString( nIndex++, field.getTitle( ) );
            daoUtil.setString( nIndex++, field.getValue( ) );
            daoUtil.setBoolean( nIndex++, field.isDefaultValue( ) );
            daoUtil.setInt( nIndex++, field.getPosition( ) );
            daoUtil.setDate( nIndex++, ( field.getValueTypeDate( ) == null ) ? null : new Date( field.getValueTypeDate( ).getTime( ) ) );
            daoUtil.setBoolean( nIndex++, field.isNoDisplayTitle( ) );
            daoUtil.setString( nIndex++, field.getComment( ) );
            daoUtil.setString( nIndex++, ( field.getFileImage( ) == null ) ? null : field.getFileImage( ).getFileKey( ) );

            daoUtil.setInt( nIndex++, field.getIdField( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Field> selectFieldListByIdEntry( int nIdEntry, Plugin plugin )
    {
        List<Field> fieldList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FIELD_BY_ID_ENTRY, plugin ) )
        {
            daoUtil.setInt( 1, nIdEntry );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Field field = dataToObject( daoUtil );
                fieldList.add( field );
            }
        }
        return fieldList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteVerifyBy( int nIdField, int nIdExpression, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_VERIF_BY, plugin ) )
        {
            daoUtil.setInt( 1, nIdField );
            daoUtil.setInt( 2, nIdExpression );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertVerifyBy( int nIdField, int nIdExpression, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_VERIF_BY, plugin ) )
        {
            daoUtil.setInt( 1, nIdField );
            daoUtil.setInt( 2, nIdExpression );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectListRegularExpressionKeyByIdField( int nIdField, Plugin plugin )
    {
        List<Integer> regularExpressionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_FIELD, plugin ) )
        {
            daoUtil.setInt( 1, nIdField );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                regularExpressionList.add( daoUtil.getInt( 1 ) );
            }

        }

        return regularExpressionList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegularExpressionIsUse( int nIdExpression, Plugin plugin )
    {
        int nNumberEntry = 0;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_FIELD_BY_ID_REGULAR_EXPRESSION, plugin ) )
        {
            daoUtil.setInt( 1, nIdExpression );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nNumberEntry = daoUtil.getInt( 1 );
            }

        }

        return nNumberEntry != 0;
    }

    @Override
    public List<Field> loadMultipleByEntryIdList( List<Integer> idList, Plugin plugin )
    {
        List<Field> list = new ArrayList<>( );
        if ( CollectionUtils.isEmpty( idList ) )
        {
            return list;
        }
        String query = SQL_QUERY_SELECT_FIELD_BY_LIST_ID_ENTRY + " ( " + idList.stream( ).distinct( ).map( i -> "?" ).collect( Collectors.joining( "," ) )
                + " )";

        try ( DAOUtil daoUtil = new DAOUtil( query, plugin ) )
        {
            for ( int i = 0; i < idList.size( ); i++ )
            {
                daoUtil.setInt( i + 1, idList.get( i ) );
            }
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.add( dataToObject( daoUtil ) );
            }
        }
        return list;
    }

    private Field dataToObject( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Field field = new Field( );
        field.setIdField( daoUtil.getInt( nIndex++ ) );
        // parent entry
        Entry entry = new Entry( );
        entry.setIdEntry( daoUtil.getInt( nIndex++ ) );
        field.setParentEntry( entry );
        field.setCode( daoUtil.getString( nIndex++ ) );
        field.setTitle( daoUtil.getString( nIndex++ ) );
        field.setValue( daoUtil.getString( nIndex++ ) );
        field.setDefaultValue( daoUtil.getBoolean( nIndex++ ) );
        field.setPosition( daoUtil.getInt( nIndex++ ) );
        field.setValueTypeDate( daoUtil.getDate( nIndex++ ) );
        field.setNoDisplayTitle( daoUtil.getBoolean( nIndex++ ) );
        field.setComment( daoUtil.getString( nIndex++ ) );
        File fileImage = new File( ); 
        if ( daoUtil.getString( nIndex ) != null )
        {
	        fileImage.setFileKey( daoUtil.getString( nIndex ) );
	        fileImage.setUrl( ImageResourceManager.getImageUrl( PUBLIC_IMAGE_RESOURCE, Integer.parseInt( fileImage.getFileKey( ) ) )  );
        }
        field.setFileImage( fileImage );

        return field;
    }

    @Override
    public List<Field> loadByCode( String code, Plugin plugin )
    {
        List<Field> result = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_CODE, plugin ) )
        {
            daoUtil.setString( 1, code );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                result.add( dataToObject( daoUtil ) );
            }

        }
        return result;
    }
}
