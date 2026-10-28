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

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.service.GenericAttributesPlugin;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.DerivedEntryTypeService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the EntryTypeHome, on the entry types derived from another one
 */
public class EntryTypeHomeTest extends LuteceTestCase
{
    private static final String TITLE_PARENT = "Short text";
    private static final String TITLE_DERIVED = "Email";
    private static final String BEAN_NAME = "genericattributes.test.entryTypeText";
    private static final String REGEX = "^[^@\\s]+@[^@\\s]+\\.[a-z]{2,}$";
    private static final String REGEX_ERROR = "Invalid e-mail";

    @Test
    public void testCreateDerivedEntryType( )
    {
        EntryType parent = EntryTypeHome.create( newEntryType( TITLE_PARENT, 0, null, null ) );
        EntryType derived = newEntryType( TITLE_DERIVED, parent.getIdType( ), REGEX, REGEX_ERROR );
        derived.setOrder( EntryTypeHome.getMaxOrder( GenericAttributesPlugin.PLUGIN_NAME ) + 1 );
        EntryTypeHome.create( derived );

        try
        {
            EntryType loadedParent = EntryTypeHome.findByPrimaryKey( parent.getIdType( ) );
            assertFalse( loadedParent.isDerived( ) );
            assertNull( loadedParent.getRegexPattern( ) );

            EntryType loaded = EntryTypeHome.findByPrimaryKey( derived.getIdType( ) );
            assertTrue( loaded.isDerived( ) );
            assertEquals( parent.getIdType( ), loaded.getIdTypeParent( ) );
            assertEquals( BEAN_NAME, loaded.getBeanName( ) );
            assertEquals( REGEX, loaded.getRegexPattern( ) );
            assertEquals( REGEX_ERROR, loaded.getRegexErrorMessage( ) );
            assertEquals( derived.getOrder( ), EntryTypeHome.getMaxOrder( GenericAttributesPlugin.PLUGIN_NAME ) );

            assertEquals( 1, EntryTypeHome.countDerivedEntryTypes( parent.getIdType( ) ) );
            assertEquals( 0, EntryTypeHome.countDerivedEntryTypes( derived.getIdType( ) ) );
            assertEquals( 0, EntryTypeHome.countEntries( derived.getIdType( ) ) );
            assertTrue( derived.getIdType( ) >= EntryTypeDAO.DERIVED_ENTRY_TYPE_MIN_ID, "a derived entry type gets an id out of the range of the plugins" );

            loaded.setRegexPattern( "^[0-9]+$" );
            loaded.setRegexErrorMessage( null );
            EntryTypeHome.update( loaded );
            loaded = EntryTypeHome.findByPrimaryKey( derived.getIdType( ) );
            assertEquals( "^[0-9]+$", loaded.getRegexPattern( ) );
            assertNull( loaded.getRegexErrorMessage( ) );
        }
        finally
        {
            EntryTypeHome.remove( derived.getIdType( ) );
            EntryTypeHome.remove( parent.getIdType( ) );
        }

        assertNull( EntryTypeHome.findByPrimaryKey( derived.getIdType( ) ) );
    }

    @Test
    public void testValidation( )
    {
        DerivedEntryTypeService service = new DerivedEntryTypeService( );
        EntryType derived = newEntryType( TITLE_DERIVED, 1, REGEX, REGEX_ERROR );
        assertTrue( service.validate( derived ).isEmpty( ) );

        derived.setRegexPattern( "[unclosed" );
        assertEquals( 1, service.validate( derived ).size( ) );

        derived.setRegexPattern( "a".repeat( DerivedEntryTypeService.MAX_LENGTH_REGEX_PATTERN + 1 ) );
        derived.setRegexErrorMessage( "m".repeat( DerivedEntryTypeService.MAX_LENGTH_REGEX_ERROR_MESSAGE + 1 ) );
        derived.setTitle( " " );
        assertEquals( 3, service.validate( derived ).size( ) );
    }

    @Test
    public void testResolveImportedEntryType( )
    {
        DerivedEntryTypeService service = new DerivedEntryTypeService( );
        EntryType parent = EntryTypeHome.create( newEntryType( TITLE_PARENT, 0, null, null ) );
        EntryType created = null;

        try
        {
            // An export from another instance: its derived entry type has an id unknown here
            EntryType imported = newEntryType( TITLE_DERIVED, 4242, REGEX, REGEX_ERROR );
            imported.setIdType( 987654 );

            created = service.resolveImportedEntryType( imported );
            assertNotNull( created );
            assertTrue( created.isDerived( ) );
            assertEquals( parent.getIdType( ), created.getIdTypeParent( ) );
            assertEquals( REGEX, created.getRegexPattern( ) );

            // A second import finds the entry type created by the first one
            assertEquals( created.getIdType( ), service.resolveImportedEntryType( imported ).getIdType( ) );

            // A native entry type resolves to the local native one
            EntryType importedNative = newEntryType( TITLE_PARENT, 0, null, null );
            importedNative.setIdType( 987655 );
            assertEquals( parent.getIdType( ), service.resolveImportedEntryType( importedNative ).getIdType( ) );
        }
        finally
        {
            if ( created != null && created.isDerived( ) )
            {
                EntryTypeHome.remove( created.getIdType( ) );
            }
            EntryTypeHome.remove( parent.getIdType( ) );
        }
    }

    private EntryType newEntryType( String strTitle, int nIdTypeParent, String strRegex, String strRegexError )
    {
        EntryType entryType = new EntryType( );
        entryType.setTitle( strTitle );
        entryType.setBeanName( BEAN_NAME );
        entryType.setGroup( false );
        entryType.setComment( false );
        entryType.setMyLuteceUser( false );
        entryType.setPlugin( GenericAttributesPlugin.PLUGIN_NAME );
        entryType.setIdTypeParent( nIdTypeParent );
        entryType.setRegexPattern( strRegex );
        entryType.setRegexErrorMessage( strRegexError );
        return entryType;
    }
}
