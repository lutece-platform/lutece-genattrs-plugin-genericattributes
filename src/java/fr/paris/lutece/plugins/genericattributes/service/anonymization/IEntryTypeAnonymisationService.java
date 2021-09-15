package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;

public interface IEntryTypeAnonymisationService
{
    /**
     * set the wildcard an return the service
     * @param wildcard
     * @return
     */
    IEntryTypeAnonymisationService withWildcard( String wildcard );
    
    /**
     * Anonymize the response
     * @param entry
     * @param response
     * @param first
     */
    void anonymizeResponse( Entry entry, Response response, boolean first );
}
