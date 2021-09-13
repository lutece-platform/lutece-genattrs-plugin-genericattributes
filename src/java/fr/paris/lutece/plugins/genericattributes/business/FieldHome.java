/*
 * Copyright (c) 2002-2021, City of Paris
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
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.sql.TransactionManager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * This class provides instances management methods (create, find, ...) for Field objects
 */
public final class FieldHome
{
    // Static variable pointed at the DAO instance
    private static IFieldDAO _dao = SpringContextService.getBean( "genericattributes.fieldDAO" );
    private static Plugin _plugin;

    /**
     * Private constructor - this class need not be instantiated
     */
    private FieldHome( )
    {
    }

    /**
     * Creation of an instance of field
     * 
     * @param field
     *            The instance of the Field which contains the informations to store
     * @return The primary key of the new Field.
     */
    public static int create( Field field )
    {
        int id =  _dao.insert( field, getPlugin( ) );
        if ( field.getLinkedItem( ) != null )
        {
            ReferenceItemFieldHome.create( field.getIdField( ), field.getLinkedItem( ).getId( ) );
        }
        return id;
    }

    /**
     * Copy of an instance of field
     * 
     * @param field
     *            The instance of the Field who must copy
     */
    public static void copy( Field field )
    {
        Field fieldCopy = field;

        TransactionManager.beginTransaction( getPlugin( ) );

        try
        {
            fieldCopy.setIdField( create( field ) );

            for ( Entry entry : field.getConditionalQuestions( ) )
            {
                entry.setFieldDepend( fieldCopy );
                EntryHome.copy( entry );
            }

            for ( RegularExpression regularExpression : field.getRegularExpressionList( ) )
            {
                createVerifyBy( fieldCopy.getIdField( ), regularExpression.getIdExpression( ) );
            }

            TransactionManager.commitTransaction( getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }
    }

    /**
     * Update of the field which is specified in parameter
     * 
     * @param field
     *            The instance of the Field which contains the informations to update
     */
    public static void update( Field field )
    {
        _dao.store( field, getPlugin( ) );
    }

    /**
     * Remove the field whose identifier is specified in parameter
     * 
     * @param nIdField
     *            The field Id
     */
    public static void remove( int nIdField )
    {
        Field field = findByPrimaryKey( nIdField );
        if ( field != null )
        {
            for ( Entry entry : field.getConditionalQuestions( ) )
            {
                EntryHome.remove( entry.getIdEntry( ) );
            }
        }
        List<Integer> listRegularExpressionKeyEntry = getListRegularExpressionKeyByIdField( nIdField );

        for ( Integer regularExpressionKey : listRegularExpressionKeyEntry )
        {
            removeVerifyBy( nIdField, regularExpressionKey );
        }

        _dao.delete( nIdField, getPlugin( ) );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Field whose identifier is specified in parameter
     * 
     * @param nKey
     *            The field primary key
     * @return an instance of Field
     */
    public static Field findByPrimaryKey( int nKey )
    {
        Field field = _dao.load( nKey, getPlugin( ) );
        if ( field == null )
        {
            return null;
        }

        EntryFilter filter = new EntryFilter( );
        filter.setIdFieldDepend( nKey );
        field.setConditionalQuestions( EntryHome.getEntryList( filter ) );

        List<RegularExpression> listRegularExpression = new ArrayList<>( );

        if ( RegularExpressionService.getInstance( ).isAvailable( ) )
        {
            List<Integer> listRegularExpressionKeyEntry = getListRegularExpressionKeyByIdField( nKey );

            if ( CollectionUtils.isNotEmpty( listRegularExpressionKeyEntry ) )
            {
                RegularExpression regularExpression = null;

                for ( Integer regularExpressionKey : listRegularExpressionKeyEntry )
                {
                    regularExpression = RegularExpressionService.getInstance( ).getRegularExpressionByKey( regularExpressionKey );

                    if ( regularExpression != null )
                    {
                        listRegularExpression.add( regularExpression );
                    }
                }
            }
        }

        field.setRegularExpressionList( listRegularExpression );

        return field;
    }

    /**
     * Load the data of all the field of the entry and returns them in a list
     * 
     * @param nIdEntry
     *            the id of the entry
     * @return the list of field
     */
    public static List<Field> getFieldListByIdEntry( int nIdEntry )
    {
        return _dao.selectFieldListByIdEntry( nIdEntry, getPlugin( ) );
    }

    /**
     * Delete an association between field and a regular expression
     * 
     * @param nIdField
     *            The identifier of the field
     * @param nIdExpression
     *            The identifier of the regular expression
     */
    public static void removeVerifyBy( int nIdField, int nIdExpression )
    {
        _dao.deleteVerifyBy( nIdField, nIdExpression, getPlugin( ) );
    }

    /**
     * Insert an association between field and a regular expression
     * 
     * @param nIdField
     *            The identifier of the field
     * @param nIdExpression
     *            The identifier of the regular expression
     */
    public static void createVerifyBy( int nIdField, int nIdExpression )
    {
        _dao.insertVerifyBy( nIdField, nIdExpression, getPlugin( ) );
    }

    /**
     * Load the key of all the regularExpression associate to the field and returns them in a list
     * 
     * @param nIdField
     *            the id of the field
     * @return the list of regular expression key
     */
    public static List<Integer> getListRegularExpressionKeyByIdField( int nIdField )
    {
        return _dao.selectListRegularExpressionKeyByIdField( nIdField, getPlugin( ) );
    }

    /**
     * Verify if the regular expression is use
     * 
     * @param nIdExpression
     *            The identifier of the regular expression
     * @return true if the regular expression is use
     */
    public static boolean isRegularExpressionIsUse( int nIdExpression )
    {
        return _dao.isRegularExpressionIsUse( nIdExpression, getPlugin( ) );
    }

    /**
     * Load the data of all the field by code
     * 
     * @param code
     * @return the list of field
     */
    public static List<Field> getFieldListByCode( String code )
    {
        return _dao.loadByCode( code, getPlugin() );
    }
    
    /**
     * Load the data of all the field of the list of entry entry and returns them in a list
     * 
     * @param idList
     *            the ids of the entries
     * @return the list of field
     */
    public static List<Field> getFieldListByListIdEntry( List<Integer> idList )
    {
        return _dao.loadMultipleByEntryIdList( idList, getPlugin( ) );
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
}
