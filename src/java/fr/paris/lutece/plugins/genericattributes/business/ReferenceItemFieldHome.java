package fr.paris.lutece.plugins.genericattributes.business;

import java.util.List;

import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
*
* class ReferenceItemFieldHome
*
*/
public class ReferenceItemFieldHome
{
    private static IReferenceItemFieldDao _dao = SpringContextService.getBean( "genericattributes.referenceItemFieldDao" );
    private static Plugin _plugin = GenericAttributesUtils.getPlugin( );
    
    private ReferenceItemFieldHome( )
    {
    }
    
    /**
     * Create a new record in the database
     * @param idField
     * @param idItem
     */
    public static void create( int idField, int idItem )
    {
        _dao.insert( idField, idItem, _plugin );
    }
    
    /**
     * Deletes record from the database
     * @param idItem
     */
    public static void removeByItem( int idItem )
    {
        _dao.deleteByIdItem( idItem, _plugin );
    }
    
    /**
     * Deletes record from the database
     * @param idItem
     */
    public static void removeByField( int idField )
    {
        _dao.deleteByIdField( idField, _plugin );
    }
    
    /**
     * Loads records form the database.
     * @param idItem
     * @return
     */
    public static List<Integer> findIdFieldByIdItem( int idItem )
    {
       return _dao.loadFieldByItem( idItem, _plugin );
    }
    
    /**
     * Loads records form the database.
     * @param idField
     * @return
     */
    public static Integer findIdItemByIdField( int idField )
    {
       return _dao.loadItemByField( idField, _plugin );
    }
}
