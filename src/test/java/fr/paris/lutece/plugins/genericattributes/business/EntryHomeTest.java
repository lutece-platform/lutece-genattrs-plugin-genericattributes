/*
 * Copyright (c) 2002-2020, City of Paris
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

import fr.paris.lutece.plugins.genericattributes.service.GenericAttributesPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

/**
 * Test class for the EntryHome
 */
public class EntryHomeTest extends AbstractEntryTest
{
    // Constants
    private static final int NUMBER_FIELDS_ENTRY_ONE = 3;
    private static final int NUMBER_RESPONSE_ENTRY_ONE = 3;
    private static final int NUMBER_FIELDS_ENTRY_TWO = 5;
    private static final int NUMBER_RESPONSE_ENTRY_TWO = 0;

    private static final String TITLE_1 = "Title 1";
    private static final String TITLE_2 = "Title 2";

    // Variables
    private static int _nIdEntry;
    private static int _nIdEntry2;
    private static int _nIdEntryGroup;
    private static List<Entry> listEntry = new ArrayList<>( );
    private final Plugin _plugin = PluginService.getPlugin( GenericAttributesPlugin.PLUGIN_NAME );

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp( ) throws Exception
    {
        super.setUp( );

        // Create an entry of type group
        Entry entryGroup = createEntryGroup( );
        _nIdEntryGroup = entryGroup.getIdEntry( );

        // Create entries
        Entry entryOne = manageCreateEntry( entryGroup, TITLE_1, NUMBER_FIELDS_ENTRY_ONE, NUMBER_RESPONSE_ENTRY_ONE );
        _nIdEntry = entryOne.getIdEntry( );

        Entry entryTwo = manageCreateEntry( entryGroup, TITLE_2, NUMBER_FIELDS_ENTRY_TWO, NUMBER_RESPONSE_ENTRY_TWO );
        _nIdEntry2 = entryTwo.getIdEntry( );

        listEntry.add( entryOne );
        listEntry.add( entryTwo );
        listEntry.add( entryGroup );
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
     * Test the remove method of the EntryHome for an entry which is not inside a Entry of type group
     */
    public void testRemoveEntry( )
    {
        EntryHome.remove( _nIdEntry );

        checkEntryRemoving( _nIdEntry );
    }

    public void testGetNumberEntryByFilter( )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdEntryParent( _nIdEntryGroup );
        int res = EntryHome.getNumberEntryByFilter( entryFilter );
        assertEquals( 1, res );
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

    public void testFindByPrimaryKeyList( )
    {
        List<Integer> idList = new ArrayList<>( );
        idList.add( _nIdEntry );
        idList.add( _nIdEntry2 );

        List<Entry> list = EntryHome.findByPrimaryKeyList( idList );
        assertEquals( 2, list.size( ) );
    }

    public void testUpdate( )
    {
        Entry entry = EntryHome.findByPrimaryKey( _nIdEntry );

        assertEquals( TITLE_1, entry.getTitle( ) );

        entry.setTitle( TITLE_2 );
        EntryHome.update( entry );

        entry = EntryHome.findByPrimaryKey( _nIdEntry );
        assertEquals( TITLE_2, entry.getTitle( ) );
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
        assertEquals( "There are Fields which are linked to the removed entry which are not been removed !", Boolean.TRUE.booleanValue( ),
                listFields.isEmpty( ) );

        ResponseFilter responseFilter = new ResponseFilter( );
        responseFilter.setIdEntry( nIdEntry );
        List<Response> listResponses = ResponseHome.getResponseList( responseFilter );
        assertEquals( "There are Responses which are linked to the removed entry which are not been removed !", Boolean.TRUE.booleanValue( ),
                listResponses.isEmpty( ) );

    }
}
