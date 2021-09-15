package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;

public abstract class AbstractAnonymizationService implements IEntryTypeAnonymisationService
{
    protected String _wildcard;
    
    @Override
    public IEntryTypeAnonymisationService withWildcard( String wildcard )
    {
        _wildcard = wildcard;
        return this;
    }
    /**
     * Checks if the entry must be anonymized
     * @param entry
     * @return
     */
    protected boolean isAnonymizable( Entry entry )
    {
        if ( entry == null )
        {
            return false;
        }
        Field anonymizableField = entry.getFieldByCode( IEntryTypeService.FIELD_ANONYMIZABLE );
        
        if ( anonymizableField == null )
        {
            return false;
        }
        
        return Boolean.valueOf( anonymizableField.getValue( ) );
    }
    
    /**
     * Get the anonymization pattern
     * @param entry
     * @return
     */
    protected String getPattern( Entry entry )
    {
        if ( entry == null )
        {
            return "";
        }
        Field anonymizableField = entry.getFieldByCode( IEntryTypeService.FIELD_ANONYMIZABLE );
        
        if ( anonymizableField == null )
        {
            return "";
        }
        
        return StringUtils.defaultString( anonymizableField.getTitle( ) );
    }
}
