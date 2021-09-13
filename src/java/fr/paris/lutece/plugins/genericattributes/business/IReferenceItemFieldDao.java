package fr.paris.lutece.plugins.genericattributes.business;

import java.util.List;

import fr.paris.lutece.plugins.referencelist.business.ReferenceItem;
import fr.paris.lutece.portal.service.plugin.Plugin;


/**
 *
 * interface IReferenceItemFieldDao
 *
 */
public interface IReferenceItemFieldDao
{

    /**
     * Insert a new record in the table.
     * 
     * @param idField
     *            the id of the {@link Field}
     * @param idReferenceItem
     *              the id of the {@link ReferenceItem}
     * @param plugin
     *            the plugin
     */
    void insert( int idField, int idReferenceItem, Plugin plugin );
    
    /**
     * Remove records in the table.
     * 
     * @param idReferenceItem
     *              the id of the {@link ReferenceItem}
     * @param plugin
     *            the plugin
     */
    void deleteByIdItem( int idReferenceItem, Plugin plugin );
    
    /**
    * Remove records in the table.
    * 
    * @param idField
    *              the id of the Field
    * @param plugin
    *            the plugin
    */
    void deleteByIdField( int idField, Plugin plugin );
    
    /**
     * load records from the table.
     * 
     * @param idReferenceItem
     *              the id of the {@link ReferenceItem}
     * @param plugin
     *            the plugin
     */
    List<Integer> loadFieldByItem( int idReferenceItem, Plugin plugin );
    
    /**
     * load record from the table.
     * 
     * @param idField
     *              the id of the field
     * @param plugin
     *            the plugin
     */
    Integer loadItemByField( int idField, Plugin plugin );
}
