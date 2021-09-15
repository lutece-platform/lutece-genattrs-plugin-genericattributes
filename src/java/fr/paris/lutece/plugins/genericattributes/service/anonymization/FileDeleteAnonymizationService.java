package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.file.FileHome;

/**
 * {@link IEntryTypeAnonymisationService} that delete the file.
 */
public class FileDeleteAnonymizationService extends AbstractAnonymizationService
{

    @Override
    public void anonymizeResponse( Entry entry, Response response, boolean first )
    {
        if ( isAnonymizable( entry ) )
        {
            String pattern = getPattern( entry );
            if ( pattern.contains( _wildcard ) && response.getFile( ) != null )
            {
                FileHome.remove( response.getFile( ).getIdFile( ) );
                response.setFile( null );
            }
        }
    }

}
