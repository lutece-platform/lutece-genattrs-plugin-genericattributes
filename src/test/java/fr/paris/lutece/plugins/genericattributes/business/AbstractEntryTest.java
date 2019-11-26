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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.genericattributes.service.GenericAttributesPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;

public abstract class AbstractEntryTest extends LuteceTestCase
{
    private static final String ENTRY_TYPE_GROUP_TITLE = "Group";
    private static final String ENTRY_TYPE_TEXT_TITLE = "Text";
    private static final String FIELD_TITLE = "field_title";
    private static final String FIELD_VALUE = "field_value";
    private static final String RESPONSE_VALUE = "response_value";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_type ) FROM genatt_entry_type";
    private static final String SQL_QUERY_INSERT_ENTRY_TYPE = "INSERT INTO genatt_entry_type ( id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin ) "
            + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE_ENTRY_TYPE = "DELETE FROM genatt_entry_type WHERE id_type = ? ";
    
    protected static int _nEntryTypeTextPrimaryKey;
    protected static int _nEntryTypeGroupPrimaryKey;
    protected final Plugin _plugin = PluginService.getPlugin( GenericAttributesPlugin.PLUGIN_NAME );
    protected IEntryDAO _entryDAO = new EntryDAO( );

    /**
     * Create an EntryType
     * 
     * @param title        The title of the EntryType
     * @param nIsGroup     1 if it's a group 0 otherwise
     * @param strClassName The name of the class of the group
     * @return the identifier of the created entryType
     */
    protected int createEntryType( String title, int nIsGroup, String strClassName, String strIconName )
    {
        int nEntryTypeId;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ENTRY_TYPE, _plugin ) )
        {
            int nIndex = NumberUtils.INTEGER_ZERO;
            nEntryTypeId = newPrimaryKey( _plugin );
            daoUtil.setInt( ++nIndex, nEntryTypeId );
            daoUtil.setString( ++nIndex, title );
            daoUtil.setInt( ++nIndex, nIsGroup );
            daoUtil.setInt( ++nIndex, NumberUtils.INTEGER_ZERO );
            daoUtil.setInt( ++nIndex, NumberUtils.INTEGER_ZERO );
            daoUtil.setString( ++nIndex, strClassName );
            daoUtil.setString( ++nIndex, strIconName );
            daoUtil.setString( ++nIndex, GenericAttributesPlugin.PLUGIN_NAME );

            daoUtil.executeUpdate( );
        }

        return nEntryTypeId;
    }

    /**
     * Remove an EntryType
     * 
     * @param nIdEntryType The identifier of the EntryType to remove
     */
    protected void removeEntryType( int nIdEntryType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ENTRY_TYPE, _plugin ) )
        {
            int nIndex = NumberUtils.INTEGER_ZERO;
            daoUtil.setInt( ++nIndex, nIdEntryType );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        int nKey;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );

            if ( !daoUtil.next( ) )
            {
                // if the table is empty
                nKey = 1;
            }

            nKey = daoUtil.getInt( 1 ) + 1;
        }

        return nKey;
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
    protected Entry manageCreateEntry( Entry entryParent, String title, int nNumberOfFields, int nNumberOfResponses )
    {
        // Create an entry
        EntryType entryType = EntryTypeHome.findByPrimaryKey( _nEntryTypeTextPrimaryKey );
        Entry entry = createEntry( entryParent, title, entryType );

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
     * Create an entry
     * 
     * @param entryParent
     *            The parent of the entry can be null
     * @param entryType
     *            The entry type of the entry to create
     * @return the created entry
     */
    protected Entry createEntry( Entry entryParent, String title, EntryType entryType )
    {
        Entry entry = new Entry( );
        entry.setEntryType( entryType );
        entry.setResourceType( StringUtils.EMPTY );
        entry.setTitle( title );

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
    protected Entry createEntryGroup( )
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
     * Create a Field for an entry
     * 
     * @param entry
     *            The entry of the field
     */
    protected void createField( Entry entry )
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
    protected void createResponse( Entry entry )
    {
        Response response = new Response( );
        response.setEntry( entry );
        response.setResponseValue( RESPONSE_VALUE );

        ResponseHome.create( response );
    }
    
    @Override
    public void setUp( ) throws Exception
    {
        super.setUp( );

        // Create the entry type
        _nEntryTypeGroupPrimaryKey = createEntryType( ENTRY_TYPE_GROUP_TITLE, NumberUtils.INTEGER_ONE, StringUtils.EMPTY, StringUtils.EMPTY );
        _nEntryTypeTextPrimaryKey = createEntryType( ENTRY_TYPE_TEXT_TITLE, NumberUtils.INTEGER_ZERO, StringUtils.EMPTY, StringUtils.EMPTY );
    }
}
