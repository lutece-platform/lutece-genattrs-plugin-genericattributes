package fr.paris.lutece.plugins.genericattributes.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.ReferenceItemFieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItem;
import fr.paris.lutece.plugins.referencelist.service.IReferenceItemListener;

public class GenattReferenceItemListener implements IReferenceItemListener
{

    @Override
    public void addReferenceItem( ReferenceItem item )
    {
        List<Entry> entryList = listConcernedEntries( item.getIdreference( ) );
        if ( CollectionUtils.isEmpty( entryList ) )
        {
            return;
        }
        
        for ( Entry entry : entryList )
        {
            Field field = GenericAttributesUtils.createFieldFromReferenceItem( entry, item );
            FieldHome.create( field );
            ReferenceItemFieldHome.create( field.getIdField( ), item.getId( ) );
        }
    }
    
    @Override
    public void removeReferenceItem( ReferenceItem item )
    {
        List<Integer> idFields = ReferenceItemFieldHome.findIdFieldByIdItem( item.getId( ) );
        for ( Integer id : idFields )
        {
            FieldHome.remove( id );
        }
        ReferenceItemFieldHome.removeByItem( item.getId( ) );
    }
    
    @Override
    public void updateReferenceItem( ReferenceItem item )
    {
        List<Integer> idFields = ReferenceItemFieldHome.findIdFieldByIdItem( item.getId( ) );
        for ( Integer id : idFields )
        {
            Field field = FieldHome.findByPrimaryKey( id );
            field.setValue( item.getCode( ) );
            field.setTitle( item.getName( ) );
            FieldHome.update( field );
        }
        
    }
    
    private List<Entry> listConcernedEntries( int idList )
    {
        List<Field> listField = FieldHome.getFieldListByCode( IEntryTypeService.FIELD_USE_REF_LIST );
        return listField.stream( ).filter( f -> Integer.parseInt( f.getTitle( ) ) == idList )
        .map( Field::getParentEntry ).map( Entry::getIdEntry ).distinct( ).map( EntryHome::findByPrimaryKey ).collect( Collectors.toList( ) );
    }
}
