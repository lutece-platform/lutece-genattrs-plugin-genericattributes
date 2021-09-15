package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import java.util.concurrent.ThreadLocalRandom;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by a random number.
 */
public class RandomNumberAnonymizationService extends AbstractTextAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        return String.valueOf( ThreadLocalRandom.current().nextLong( ) );
    }
   
}
