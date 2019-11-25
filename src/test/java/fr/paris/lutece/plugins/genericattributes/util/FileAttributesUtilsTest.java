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
    
    private static final String ENTRY_TITLE= "Entry 1";

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
        GenericAttributeError error = FileAttributesUtils.checkFileSize( entry, listUploadedFileItems , new ArrayList<>( ), Locale.FRANCE );
        
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
        GenericAttributeError error = FileAttributesUtils.checkFileSize( entry, listUploadedFileItems , new ArrayList<>( ), Locale.FRANCE );
        
        Object[] params = { "2" };
        String strMessage = I18nService.getLocalizedString( FileAttributesUtils.PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE,
                params, Locale.FRANCE );
        
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
        GenericAttributeError error = FileAttributesUtils.checkNumberFiles( entry, listUploadedFileItems , new ArrayList<>( ), Locale.FRANCE );
        
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
        GenericAttributeError error = FileAttributesUtils.checkNumberFiles( entry, listUploadedFileItems , new ArrayList<>( ), Locale.FRANCE );
        
        Object[] params = { "2" };
        String strMessage = I18nService.getLocalizedString( FileAttributesUtils.PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES,
                params, Locale.FRANCE );
        
        assertNotNull( error );
        assertFalse( error.isMandatoryError( ) );
        assertEquals( ENTRY_TITLE, error.getTitleQuestion( ) );
        assertEquals( strMessage, error.getErrorMessage( ) );
    }
}
