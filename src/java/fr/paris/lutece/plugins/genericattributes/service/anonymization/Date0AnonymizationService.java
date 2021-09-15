package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by the date 1970-01-01.
 */
public class Date0AnonymizationService extends AbstractDateAnonymizationService
{
    
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        return "0";
    }
}
