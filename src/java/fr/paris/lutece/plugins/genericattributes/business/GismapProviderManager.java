package fr.paris.lutece.plugins.genericattributes.business;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

public final class GismapProviderManager 
{
	/**
     * MapProviderManager empty constructor
     */
    private GismapProviderManager(  )
    {
    }

    /**
     * Gets the mapProvider for the provided key.
     * @param strKey the key
     * @return <code>null</code> if <code>strKey</code> is blank, the map
     *         provider if found, <code>null</code> otherwise.
     * @see StringUtils#isBlank(String)
     */
    public static IGismapProvider getGismapProvider( String strKey )
    {
        if ( StringUtils.isBlank( strKey ) )
        {
            return null;
        }

        for ( IGismapProvider gismapProvider : getGismapProvidersList(  ) )
        {
            if ( strKey.equals( gismapProvider.getKey(  ) ) )
            {
                return gismapProvider;
            }
        }

        AppLogService.info( GismapProviderManager.class.getName(  ) + " : No map provider found for key " + strKey );

        return null;
    }

    /**
     * Builds all available providers list
     * @return all available providers
     */
    public static List<IGismapProvider> getGismapProvidersList(  )
    {
        return SpringContextService.getBeansOfType( IGismapProvider.class );
    }

}
