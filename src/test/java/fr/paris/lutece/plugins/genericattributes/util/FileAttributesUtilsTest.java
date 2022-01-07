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
package fr.paris.lutece.plugins.genericattributes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.test.LuteceTestCase;

public class FileAttributesUtilsTest extends LuteceTestCase
{

    private static final String ENTRY_TITLE = "Entry 1";

    public void testCheckFileSizeOk( )
    {
        Field field = new Field( );
        field.setCode( IEntryTypeService.FIELD_FILE_MAX_SIZE );
        field.setValue( "1024" );

        Entry entry = new Entry( );
        entry.setTitle( ENTRY_TITLE );
        entry.setFields( new ArrayList<>( ) );
        entry.getFields( ).add( field );

        FileItem file = new GenAttFileItem( "azerty".getBytes( ), "test.csv" );

        List<FileItem> listUploadedFileItems = new ArrayList<>( );
        listUploadedFileItems.add( file );
        GenericAttributeError error = FileAttributesUtils.checkFileSize( entry, listUploadedFileItems, new ArrayList<>( ), Locale.FRANCE );

        assertNull( error );
    }

    public void testCheckFileSizeKo( )
    {
        Field field = new Field( );
        field.setCode( IEntryTypeService.FIELD_FILE_MAX_SIZE );
        field.setValue( "2" );

        Entry entry = new Entry( );
        entry.setTitle( ENTRY_TITLE );
        entry.setFields( new ArrayList<>( ) );
        entry.getFields( ).add( field );

        FileItem file = new GenAttFileItem( "azerty".getBytes( ), "test.csv" );

        List<FileItem> listUploadedFileItems = new ArrayList<>( );
        listUploadedFileItems.add( file );
        GenericAttributeError error = FileAttributesUtils.checkFileSize( entry, listUploadedFileItems, new ArrayList<>( ), Locale.FRANCE );

        Object [ ] params = {
                "2"
        };
        String strMessage = I18nService.getLocalizedString( FileAttributesUtils.PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE, params, Locale.FRANCE );

        assertNotNull( error );
        assertFalse( error.isMandatoryError( ) );
        assertEquals( ENTRY_TITLE, error.getTitleQuestion( ) );
        assertEquals( strMessage, error.getErrorMessage( ) );
    }

    public void testCheckNumberFileOK( )
    {
        Field field = new Field( );
        field.setCode( IEntryTypeService.FIELD_MAX_FILES );
        field.setValue( "2" );

        Entry entry = new Entry( );
        entry.setTitle( ENTRY_TITLE );
        entry.setFields( new ArrayList<>( ) );
        entry.getFields( ).add( field );

        FileItem file = new GenAttFileItem( "azerty".getBytes( ), "test.csv" );

        List<FileItem> listUploadedFileItems = new ArrayList<>( );
        listUploadedFileItems.add( file );
        listUploadedFileItems.add( file );
        GenericAttributeError error = FileAttributesUtils.checkNumberFiles( entry, listUploadedFileItems, new ArrayList<>( ), Locale.FRANCE );

        assertNull( error );
    }

    public void testCheckNumberFileKO( )
    {
        Field field = new Field( );
        field.setCode( IEntryTypeService.FIELD_MAX_FILES );
        field.setValue( "2" );

        Entry entry = new Entry( );
        entry.setTitle( ENTRY_TITLE );
        entry.setFields( new ArrayList<>( ) );
        entry.getFields( ).add( field );

        FileItem file = new GenAttFileItem( "azerty".getBytes( ), "test.csv" );

        List<FileItem> listUploadedFileItems = new ArrayList<>( );
        listUploadedFileItems.add( file );
        listUploadedFileItems.add( file );
        listUploadedFileItems.add( file );
        GenericAttributeError error = FileAttributesUtils.checkNumberFiles( entry, listUploadedFileItems, new ArrayList<>( ), Locale.FRANCE );

        Object [ ] params = {
                "2"
        };
        String strMessage = I18nService.getLocalizedString( FileAttributesUtils.PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES, params, Locale.FRANCE );

        assertNotNull( error );
        assertFalse( error.isMandatoryError( ) );
        assertEquals( ENTRY_TITLE, error.getTitleQuestion( ) );
        assertEquals( strMessage, error.getErrorMessage( ) );
    }
}
