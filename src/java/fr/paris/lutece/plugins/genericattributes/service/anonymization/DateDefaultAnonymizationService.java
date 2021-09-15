package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by the date 1970-01-01.
 */
public class DateDefaultAnonymizationService extends AbstractDateAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        Field defaultField = entry.getFieldByCode( IEntryTypeService.FIELD_DATE_VALUE );
        if ( defaultField != null && defaultField.getValueTypeDate( ) != null )
        {
            return String.valueOf( defaultField.getValueTypeDate( ).getTime( ) );
        }
        return null;
    }
}
