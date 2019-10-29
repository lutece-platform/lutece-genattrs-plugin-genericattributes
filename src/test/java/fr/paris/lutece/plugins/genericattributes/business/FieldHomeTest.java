package fr.paris.lutece.plugins.genericattributes.business;

import static org.junit.Assert.assertNotEquals;

public class FieldHomeTest extends AbstractEntryTest
{
    private static int _nIdEntry;
    private static int _nIdEntryGroup;
    
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
    
    @Override
    public void setUp( ) throws Exception
    {
        super.setUp( );
        
     // Create an entry of type group
        Entry entryGroup = createEntryGroup( );
        _nIdEntryGroup = entryGroup.getIdEntry( );

        // Create entries
        Entry entryOne = manageCreateEntry( entryGroup, 0, 0 );
        _nIdEntry = entryOne.getIdEntry( );

        Entry entryTwo = manageCreateEntry( entryGroup, 0, 0 );
    }
}
