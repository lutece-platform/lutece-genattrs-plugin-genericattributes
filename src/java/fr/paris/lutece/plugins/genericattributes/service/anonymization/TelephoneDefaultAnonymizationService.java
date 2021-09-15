package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeTelephoneNumber;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by an example default number
 */
public class TelephoneDefaultAnonymizationService extends AbstractTextAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        Field defaultField = entry.getFieldByCode( AbstractEntryTypeTelephoneNumber.FIELD_DEFAULT_REGION );
        if ( defaultField != null )
        {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance( );
            return phoneUtil.format( phoneUtil.getExampleNumber( defaultField.getValue( ) ), PhoneNumberFormat.E164 );
        }
        return null;
    }
}
