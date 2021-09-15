package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;

public abstract class AbstractTextAnonymizationService extends AbstractAnonymizationService
{
    
    /**
     * Get the value which will replace the wildcard
     * @param entry
     * @param response
     * @return
     */
    protected abstract String getAnonymisedValue( Entry entry, Response response );
    
    @Override
    public void anonymizeResponse( Entry entry, Response response, boolean first )
    {
        if ( isAnonymizable( entry ) )
        {
            String pattern = response.getResponseValue( );
            if ( first )
            {
                pattern = getPattern( entry );
            }
            if ( pattern.contains( _wildcard ) )
            {
                pattern = pattern.replace( _wildcard, getAnonymisedValue( entry, response ) );
            }
            response.setResponseValue( pattern );
        }
    }
}
