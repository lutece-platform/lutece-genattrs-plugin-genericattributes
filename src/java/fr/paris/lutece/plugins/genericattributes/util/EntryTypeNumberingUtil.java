package fr.paris.lutece.plugins.genericattributes.util;

import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;

/**
 * Contains the map of values for {@link EntryTypeNumbering}
 */
public final class EntryTypeNumberingUtil
{

    private static final EntryTypeNumberingUtil INSTANCE = new EntryTypeNumberingUtil( );
    private final Map<Integer, Integer> _valuesMap;

    private EntryTypeNumberingUtil( )
    {
        _valuesMap = new HashMap<>( );
    }

    public static EntryTypeNumberingUtil getInstance( )
    {
        return INSTANCE;
    }

    public synchronized int getNextValue( int entryId )
    {
        if ( _valuesMap.containsKey( entryId ) )
        {
            int old = _valuesMap.get( entryId );
            _valuesMap.put( entryId, old + 1 );
        }
        else
        {
            _valuesMap.put( entryId, ResponseHome.findMaxNumber( entryId ) );
        }

        return _valuesMap.get( entryId );
    }
}
