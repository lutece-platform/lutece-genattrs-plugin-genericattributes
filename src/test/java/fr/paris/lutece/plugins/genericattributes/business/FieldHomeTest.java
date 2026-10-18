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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.service.GenattReferenceItemListener;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItem;
import fr.paris.lutece.plugins.referencelist.service.ReferenceItemEvent;

public class FieldHomeTest extends AbstractEntryTest
{

    private static final String TITLE_1 = "Title 1";
    private static final String TITLE_2 = "Title 2";
    private static final int ID_REFERENCE_ITEM = 987654;
    private static int _nIdEntry;
    private static int _nIdEntryGroup;

    @Test
    public void testSaveLoadDelete( )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( _nIdEntry );

        Field field = new Field( );
        field.setCode( "code" );
        field.setParentEntry( entry );
        field.setTitle( "title" );
        field.setValue( "value" );

        FieldHome.create( field );
        assertNotEquals( 0, field.getIdField( ) );

        Field loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
        assertEquals( "title", loaded.getTitle( ) );

        FieldHome.remove( field.getIdField( ) );
        loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
        assertNull( loaded );
    }

    /**
     * Removing a field created from a reference list removes its link to the reference item
     */
    @Test
    public void testRemoveFieldRemovesReferenceItemLink( )
    {
        Field field = createLinkedField( );
        assertTrue( ReferenceItemFieldHome.findIdFieldByIdItem( ID_REFERENCE_ITEM ).contains( field.getIdField( ) ) );

        FieldHome.remove( field.getIdField( ) );

        assertFalse( ReferenceItemFieldHome.findIdFieldByIdItem( ID_REFERENCE_ITEM ).contains( field.getIdField( ) ) );
    }

    /**
     * The update of a reference item skips the links to removed fields and still updates the other fields
     */
    @Test
    public void testUpdateReferenceItemSkipsRemovedField( )
    {
        int nIdRemovedField = Integer.MAX_VALUE - 1;
        ReferenceItemFieldHome.create( nIdRemovedField, ID_REFERENCE_ITEM );
        Field field = createLinkedField( );
        try
        {
            ReferenceItem item = new ReferenceItem( );
            item.setId( ID_REFERENCE_ITEM );
            item.setCode( "newCode" );
            item.setName( "newName" );

            new GenattReferenceItemListener( ).updateReferenceItem( new ReferenceItemEvent( item ) );

            Field loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
            assertEquals( "newCode", loaded.getValue( ) );
            assertEquals( "newName", loaded.getTitle( ) );
            assertFalse( ReferenceItemFieldHome.findIdFieldByIdItem( ID_REFERENCE_ITEM ).contains( nIdRemovedField ) );
        }
        finally
        {
            ReferenceItemFieldHome.removeByField( nIdRemovedField );
            FieldHome.remove( field.getIdField( ) );
        }
    }

    private Field createLinkedField( )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( _nIdEntry );

        ReferenceItem item = new ReferenceItem( );
        item.setId( ID_REFERENCE_ITEM );

        Field field = new Field( );
        field.setCode( "answer_choice" );
        field.setParentEntry( entry );
        field.setTitle( "title" );
        field.setValue( "value" );
        field.setLinkedItem( item );
        FieldHome.create( field );

        return field;
    }

    @BeforeEach
    public void setUp( ) throws Exception
    {
    	super.setUp( );
        // Create an entry of type group
        Entry entryGroup = createEntryGroup( );
        _nIdEntryGroup = entryGroup.getIdEntry( );

        // Create entries
        Entry entryOne = manageCreateEntry( entryGroup, TITLE_1, 0, 0 );
        _nIdEntry = entryOne.getIdEntry( );

        Entry entryTwo = manageCreateEntry( entryGroup, TITLE_2, 0, 0 );
    }
}
