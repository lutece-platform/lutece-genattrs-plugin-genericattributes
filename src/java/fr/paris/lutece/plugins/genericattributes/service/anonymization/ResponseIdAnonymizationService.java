package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by the response id.
 */
public class ResponseIdAnonymizationService extends AbstractTextAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        return String.valueOf( response.getIdResponse( ) );
    }
   
}
