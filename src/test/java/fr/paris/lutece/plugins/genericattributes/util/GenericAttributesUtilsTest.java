package fr.paris.lutece.plugins.genericattributes.util;

import java.util.ArrayList;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.test.LuteceTestCase;

public class GenericAttributesUtilsTest extends LuteceTestCase
{
    
    private static final String CODE = "code";
    private static final String TITLE_1 = "title1";
    private static final String VALUE_1 = "code1";
    private static final String TITLE_2 = "title2";
    private static final String VALUE_2 = "code2";

    public void testCreateOrUpdateFieldNew( )
    {
        Entry entry = new Entry( );
        entry.setFields( new ArrayList<>( ) );
        
        Field field = GenericAttributesUtils.createOrUpdateField( entry, CODE, TITLE_1, VALUE_1 );
        
        assertEquals( 1, entry.getFields( ).size( ) );
        
        assertTrue( field.getParentEntry( ) == entry );
        assertTrue( entry.getFields( ).get( 0 ) == field );
        assertEquals(CODE, field.getCode( ) );
        assertEquals(TITLE_1, field.getTitle( ) );
        assertEquals(VALUE_1, field.getValue( ) );
    }
    
    public void testCreateOrUpdateFieldUpdate( )
    {
        Field field = new Field( );
        field.setCode( CODE );
        field.setTitle( TITLE_1 );
        field.setValue( VALUE_1 );
        
        Entry entry = new Entry( );
        entry.setFields( new ArrayList<>( ) );
        entry.getFields( ).add( field );
        field.setParentEntry( entry );
        
        Field fieldUpdated = GenericAttributesUtils.createOrUpdateField( entry, CODE, TITLE_2, VALUE_2 );
        assertTrue( fieldUpdated == field );
        assertEquals(CODE, fieldUpdated.getCode( ) );
        assertEquals(TITLE_2, fieldUpdated.getTitle( ) );
        assertEquals(VALUE_2, fieldUpdated.getValue( ) );
        
    }
}
