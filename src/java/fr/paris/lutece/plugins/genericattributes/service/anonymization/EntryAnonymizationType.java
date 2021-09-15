package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import java.util.Locale;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class EntryAnonymizationType implements IEntryAnonymizationType
{
    private String _wildcard;
    private String _helpKey;
    private String _serviceName;
    
    /**
     * Constructor
     * @param wildcard
     * @param helpKey
     * @param serviceName
     */
    public EntryAnonymizationType( String wildcard, String helpKey, String serviceName )
    {
        super( );
        _wildcard = wildcard;
        _helpKey = helpKey;
        _serviceName = serviceName;
    }
    
    /**
     * @return the wildcard
     */
    public String getWildcard( )
    {
        return _wildcard;
    }

    /**
     * @return the helpKey
     */
    public String getHelpKey( )
    {
        return _helpKey;
    }

    @Override
    public String getHelpMessage( Locale locale )
    {
        return getWildcard( ) + " " + I18nService.getLocalizedString( getHelpKey(), locale );
    }
    
    @Override
    public IEntryTypeAnonymisationService getAnonymisationTypeService( )
    {
        IEntryTypeAnonymisationService service = SpringContextService.getBean( _serviceName );
        return service.withWildcard( _wildcard );
    }
}
