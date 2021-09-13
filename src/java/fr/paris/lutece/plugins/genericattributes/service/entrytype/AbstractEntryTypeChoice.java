package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.ReferenceItemFieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItem;
import fr.paris.lutece.plugins.referencelist.service.ReferenceItemListService;

public abstract class AbstractEntryTypeChoice extends EntryTypeService
{

    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue( );
    }

    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( response.getField( ) != null )
        {
            if ( response.getField( ).getTitle( ) == null )
            {
                Field field = FieldHome.findByPrimaryKey( response.getField( ).getIdField( ) );

                if ( field != null )
                {
                    response.setField( field );
                }
            }

            return response.getField( ).getTitle( );
        }

        return null;
    }
    
    protected String createFieldsUseRefList( Entry entry, HttpServletRequest request )
    {
        String strUseRefList = request.getParameter( PARAMETER_USE_REF_LIST );
        String strRefListSelect = request.getParameter( PARAMETER_REF_LIST_SELECT );
        
        boolean useRefList = false;
        int idRefList = -1;
        if ( StringUtils.isNotEmpty( strUseRefList ) )
        {
            if ( StringUtils.isEmpty( strRefListSelect ) )
            {
                return MESSAGE_MANDATORY_FIELD;            
            }
            useRefList = true;
            idRefList = Integer.parseInt( strRefListSelect );
        }
        
        int oldIdRefList = -1;
        Field field = entry.getFieldByCode( FIELD_USE_REF_LIST );
        if ( field != null )
        {
            oldIdRefList = Integer.parseInt( field.getTitle( ) );
        }
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_USE_REF_LIST, String.valueOf( idRefList ), String.valueOf( useRefList ) );
        
        // If the reference list has changed
        if ( oldIdRefList != idRefList )
        {
            // We remove the old answer choices
            List<Field> anwserFields = entry.getFields( ).stream( )
                    .filter( f -> IEntryTypeService.FIELD_ANSWER_CHOICE.equals( f.getCode( ) ) )
                    .collect( Collectors.toList( ) );
            
            for ( Field anwserField : anwserFields )
            {
                FieldHome.remove( anwserField.getIdField( ) );
                ReferenceItemFieldHome.removeByField( anwserField.getIdField( ) );
                entry.getFields( ).removeIf( f -> f.getIdField( ) == anwserField.getIdField( ) );
            }
            
            // We create the needed answer choices
            if ( useRefList )
            {
                for ( ReferenceItem item : ReferenceItemListService.getInstance( ).getReferenceItemsList( idRefList ) )
                {
                    Field anwserField = GenericAttributesUtils.createFieldFromReferenceItem( entry, item );
                    entry.getFields( ).add( anwserField );
                }
            }
        }
        return null;
    }
}
