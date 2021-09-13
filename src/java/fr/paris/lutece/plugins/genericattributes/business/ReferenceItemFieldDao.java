package fr.paris.lutece.plugins.genericattributes.business;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
*
* class ReferenceItemFieldDao
*
*/
public class ReferenceItemFieldDao implements IReferenceItemFieldDao
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO genatt_referenceitem_field ( id_field, id_item ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE_BY_ITEM = "DELETE FROM genatt_referenceitem_field WHERE id_item = ? ";
    private static final String SQL_QUERY_DELETE_BY_FIELD = "DELETE FROM genatt_referenceitem_field WHERE id_field = ? ";
    private static final String SQL_QUERY_SELECT_FIELD_BY_ITEM = "SELECT id_field FROM genatt_referenceitem_field WHERE id_item = ? ";
    private static final String SQL_QUERY_SELECT_ITEM_BY_FIELD = "SELECT id_item FROM genatt_referenceitem_field WHERE id_field = ? ";
    
    @Override
    public void insert( int idField, int idReferenceItem, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( 1, idField );
            daoUtil.setInt( 2, idReferenceItem );
            daoUtil.executeUpdate( );
        }
        
    }

    @Override
    public void deleteByIdItem( int idReferenceItem, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ITEM, plugin ) )
        {
            daoUtil.setInt( 1, idReferenceItem );
            
            daoUtil.executeUpdate( );
        }
    }
    
    @Override
    public void deleteByIdField( int idField, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_FIELD, plugin ) )
        {
            daoUtil.setInt( 1, idField );
            
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<Integer> loadFieldByItem( int idReferenceItem, Plugin plugin )
    {
        List<Integer> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FIELD_BY_ITEM, plugin ) )
        {
            daoUtil.setInt( 1, idReferenceItem );
            
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                list.add( daoUtil.getInt( 1 ) );
            }
        }
        
        return list;
    }
    
    @Override
    public Integer loadItemByField( int idField, Plugin plugin )
    {
        Integer idItem = -1;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ITEM_BY_FIELD, plugin ) )
        {
            daoUtil.setInt( 1, idField );
            
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                idItem = daoUtil.getInt( 1 );
            }
        }
        
        return idItem;
    }
}
