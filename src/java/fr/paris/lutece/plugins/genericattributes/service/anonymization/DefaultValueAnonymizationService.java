package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by the default value.
 */
public class DefaultValueAnonymizationService extends AbstractTextAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        Field prefixTextConf = entry.getFieldByCode( IEntryTypeService.FIELD_TEXT_CONF );
        if ( prefixTextConf == null )
        {
            return "";
        }
        return StringUtils.defaultString( prefixTextConf.getValue( ) );
    }
   
}
