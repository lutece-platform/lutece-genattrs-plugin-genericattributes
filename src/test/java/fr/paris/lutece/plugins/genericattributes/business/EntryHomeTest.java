/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.genericattributes.service.GenericAttributesPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Test class for the EntryHome
 */
public class EntryHomeTest extends LuteceTestCase
{
    // Constants
    private static final String FIELD_TITLE = "field_title";
    private static final String FIELD_VALUE = "field_value";
    private static final String RESPONSE_VALUE = "response_value";
    private static final String ENTRY_TYPE_GROUP_TITLE = "Group";
    private static final String ENTRY_TYPE_TEXT_TITLE = "Text";
    private static final int NUMBER_FIELDS_ENTRY_ONE = 3;
    private static final int NUMBER_RESPONSE_ENTRY_ONE = 3;
    private static final int NUMBER_FIELDS_ENTRY_TWO = 5;
    private static final int NUMBER_RESPONSE_ENTRY_TWO = 0;
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_type ) FROM genatt_entry_type";
    private static final String SQL_QUERY_INSERT_ENTRY_TYPE = "INSERT INTO genatt_entry_type ( id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin ) "
            + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE_ENTRY_TYPE = "DELETE FROM genatt_entry_type WHERE id_type = ? ";

    // Variables
    private static int _nIdEntry;
    private static int _nIdEntryGroup;
    private static int _nEntryTypeTextPrimaryKey;
    private static int _nEntryTypeGroupPrimaryKey;
    private static List<Entry> listEntry = new ArrayList<>( );
    private final Plugin _plugin = PluginService.getPlugin( GenericAttributesPlugin.PLUGIN_NAME );
    private IEntryDAO _entryDAO = new EntryDAO( );

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp( ) throws Exception
    {
        super.setUp( );

        // Create the entry type
        _nEntryTypeGroupPrimaryKey = createEntryType( ENTRY_TYPE_GROUP_TITLE, NumberUtils.INTEGER_ONE, StringUtils.EMPTY, StringUtils.EMPTY );
        _nEntryTypeTextPrimaryKey = createEntryType( ENTRY_TYPE_TEXT_TITLE, NumberUtils.INTEGER_ZERO, StringUtils.EMPTY, StringUtils.EMPTY );

        // Create an entry of type group
        Entry entryGroup = createEntryGroup( );
        _nIdEntryGroup = entryGroup.getIdEntry( );

        // Create entries
        Entry entryOne = manageCreateEntry( entryGroup, NUMBER_FIELDS_ENTRY_ONE, NUMBER_RESPONSE_ENTRY_ONE );
        _nIdEntry = entryOne.getIdEntry( );

        Entry entryTwo = manageCreateEntry( entryGroup, NUMBER_FIELDS_ENTRY_TWO, NUMBER_RESPONSE_ENTRY_TWO );

        listEntry.add( entryOne );
        listEntry.add( entryTwo );
        listEntry.add( entryGroup );
    }

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
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
     * Create an EntryType
     * 
     * @param title
     *            The title of the EntryType
     * @param nIsGroup
     *            1 if it's a group 0 otherwise
     * @param strClassName
     *            The name of the class of the group
     * @return the identifier of the created entryType
     */
    private int createEntryType( String title, int nIsGroup, String strClassName, String strIconName )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ENTRY_TYPE, _plugin );

        int nIndex = NumberUtils.INTEGER_ZERO;
        int nEntryTypeId = newPrimaryKey( _plugin );
        daoUtil.setInt( ++nIndex, nEntryTypeId );
        daoUtil.setString( ++nIndex, title );
        daoUtil.setInt( ++nIndex, nIsGroup );
        daoUtil.setInt( ++nIndex, NumberUtils.INTEGER_ZERO );
        daoUtil.setInt( ++nIndex, NumberUtils.INTEGER_ZERO );
        daoUtil.setString( ++nIndex, strClassName );
        daoUtil.setString( ++nIndex, strIconName );
        daoUtil.setString( ++nIndex, GenericAttributesPlugin.PLUGIN_NAME );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        return nEntryTypeId;
    }

    /**
     * Create an entry
     * 
     * @param entryParent
     *            The parent of the entry can be null
     * @param entryType
     *            The entry type of the entry to create
     * @return the created entry
     */
    private Entry createEntry( Entry entryParent, EntryType entryType )
    {
        Entry entry = new Entry( );
        entry.setEntryType( entryType );
        entry.setResourceType( StringUtils.EMPTY );

        if ( entryParent != null )
        {
            entry.setParent( entryParent );
        }

        int nIdEntry = _entryDAO.insert( entry, _plugin );
        entry.setIdEntry( nIdEntry );

        return entry;
    }

    /**
     * Create an entry of Type group
     * 
     * @return the created entry of type group
     */
    private Entry createEntryGroup( )
    {
        Entry entry = new Entry( );

        EntryType entryTypeGroup = EntryTypeHome.findByPrimaryKey( _nEntryTypeGroupPrimaryKey );
        entry.setResourceType( StringUtils.EMPTY );
        entry.setEntryType( entryTypeGroup );

        int nIdEntryGroup = _entryDAO.insert( entry, _plugin );
        entry.setIdEntry( nIdEntryGroup );

        return entry;
    }

    /**
     * Manage the creation of an entry. Create an entry and its Fields and Responses objects
     * 
     * @param entryParent
     *            The parent of the entry can be null
     * @param nNumberOfFields
     *            The number of fields of the Entry
     * @param nNumberOfResponses
     *            The number of Response of the Entry
     * @return the new created Entry
     */
    private Entry manageCreateEntry( Entry entryParent, int nNumberOfFields, int nNumberOfResponses )
    {
        // Create an entry
        EntryType entryType = EntryTypeHome.findByPrimaryKey( _nEntryTypeTextPrimaryKey );
        Entry entry = createEntry( entryParent, entryType );

        // Create fields for the entry
        for ( int i = 0; i < nNumberOfFields; i++ )
        {
            createField( entry );
        }

        // Create responses for the entry
        for ( int i = 0; i < nNumberOfResponses; i++ )
        {
            createResponse( entry );
        }

        return entry;
    }

    /**
     * Create a Field for an entry
     * 
     * @param entry
     *            The entry of the field
     */
    private void createField( Entry entry )
    {
        Field field = new Field( );
        field.setParentEntry( entry );
        field.setTitle( FIELD_TITLE );
        field.setValue( FIELD_VALUE );

        int nIdField = FieldHome.create( field );
        field.setIdField( nIdField );
    }

    /**
     * Create a Response for an entry
     * 
     * @param entry
     *            The entry of the Response
     */
    private void createResponse( Entry entry )
    {
        Response response = new Response( );
        response.setEntry( entry );
        response.setResponseValue( RESPONSE_VALUE );

        ResponseHome.create( response );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown( ) throws Exception
    {
        super.tearDown( );

        for ( Entry entry : listEntry )
        {
            // Remove the fields
            removeFields( entry.getIdEntry( ) );

            // Remove the Responses
            removeResponses( entry.getIdEntry( ) );

            // Remove the entries
            removeEntry( entry.getIdEntry( ) );
        }

        // Remove the entryType
        removeEntryType( _nEntryTypeGroupPrimaryKey );
        removeEntryType( _nEntryTypeTextPrimaryKey );
    }

    /**
     * Remove all the created fields
     * 
     * @param nIdEntry
     *            The identifier of the entry
     */
    private void removeFields( int nIdEntry )
    {
        List<Field> listField = FieldHome.getFieldListByIdEntry( nIdEntry );
        if ( listField != null && !listField.isEmpty( ) )
        {
            for ( Field field : listField )
            {
                FieldHome.remove( field.getIdField( ) );
            }
        }
    }

    /**
     * Remove all the created Responses
     * 
     * @param nIdEntry
     *            The identifier of the entry to remove the responses
     */
    private void removeResponses( int nIdEntry )
    {
        ResponseFilter responseFilter = new ResponseFilter( );
        responseFilter.setIdEntry( nIdEntry );
        List<Response> listResponse = ResponseHome.getResponseList( responseFilter );
        if ( listResponse != null && !listResponse.isEmpty( ) )
        {
            for ( Response response : listResponse )
            {
                ResponseHome.remove( response.getIdResponse( ) );
            }
        }
    }

    /**
     * Remove an entry by its identifier
     * 
     * @param nIdEntry
     *            The identifier of the entry
     */
    private void removeEntry( int nIdEntry )
    {
        _entryDAO.delete( nIdEntry, _plugin );
    }

    /**
     * Remove an EntryType
     * 
     * @param nIdEntryType
     *            The identifier of the EntryType to remove
     */
    private void removeEntryType( int nIdEntryType )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ENTRY_TYPE, _plugin );

        int nIndex = NumberUtils.INTEGER_ZERO;
        daoUtil.setInt( ++nIndex, nIdEntryType );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Test the remove method of the EntryHome for an entry which is not inside a Entry of type group
     */
    public void testRemoveEntry( )
    {
        EntryHome.remove( _nIdEntry );

        checkEntryRemoving( _nIdEntry );
    }

    /**
     * Test the remove method of the EntryHome for an entry which is inside a Entry of type group
     */
    public void testRemoveEntryGroup( )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdEntryParent( _nIdEntryGroup );
        List<Entry> listEntryChildren = _entryDAO.selectEntryListByFilter( entryFilter, _plugin );

        EntryHome.remove( _nIdEntryGroup );

        checkEntryRemoving( _nIdEntryGroup );

        // For each children of the group we will check if they have been removed with all their objects
        for ( Entry entry : listEntryChildren )
        {
            checkEntryRemoving( entry.getIdEntry( ) );
        }
    }

    /**
     * Check if all data linked to the entry with the specified identifier has been correctly removed
     * 
     * @param nIdEntry
     *            The identifier of the entry which has been removed
     */
    private void checkEntryRemoving( int nIdEntry )
    {
        Entry entry = _entryDAO.load( nIdEntry, _plugin );
        assertEquals( "The has not been removed !", Boolean.TRUE.booleanValue( ), entry == null );

        List<Field> listFields = FieldHome.getFieldListByIdEntry( nIdEntry );
        assertEquals( "There are Fields which are linked to the removed entry which are not been removed !", Boolean.TRUE.booleanValue( ), listFields.isEmpty( ) );

        ResponseFilter responseFilter = new ResponseFilter( );
        responseFilter.setIdEntry( nIdEntry );
        List<Response> listResponses = ResponseHome.getResponseList( responseFilter );
        assertEquals( "There are Responses which are linked to the removed entry which are not been removed !", Boolean.TRUE.booleanValue( ),
                listResponses.isEmpty( ) );

    }
}
