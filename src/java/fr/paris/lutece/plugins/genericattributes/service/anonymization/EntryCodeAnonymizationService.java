package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by the entry code.
 */
public class EntryCodeAnonymizationService extends AbstractTextAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        return entry.getCode( );
    }
   
}
