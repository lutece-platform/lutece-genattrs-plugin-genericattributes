package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import java.util.Locale;

public interface IEntryAnonymizationType
{

    /**
     *  Get the help Message
     * @param locale
     * @return
     */
    String getHelpMessage( Locale locale );
    
    /**
     *  Get anonymization Service
     * @return
     */
    IEntryTypeAnonymisationService getAnonymisationTypeService( );
}
