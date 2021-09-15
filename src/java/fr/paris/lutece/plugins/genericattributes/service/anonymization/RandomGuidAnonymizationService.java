package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import java.util.UUID;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by a random guid.
 */
public class RandomGuidAnonymizationService extends AbstractTextAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        return UUID.randomUUID( ).toString( );
    }
   
}
