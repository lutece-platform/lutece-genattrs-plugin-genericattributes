/**
 * 
 */
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.GismapProviderManager;
import fr.paris.lutece.plugins.genericattributes.business.IGismapProvider;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.util.ReferenceList;
/**
 * @author bass
 *
 */
public abstract class AbstractEntryTypeGeospatial extends EntryTypeService {

	/** The Constant PARAMETER_MAP_PROVIDER. */
    public static final String PARAMETER_GISMAP_PROVIDER = "gismap_provider";

    /** The Constant PARAMETER_SUFFIX_GEO. */
    public static final String PARAMETER_SUFFIX_GEO = "_geo";

     /** The Constant PARAMETER_SUFFIX_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ADDRESS = "_address";

    /** The Constant PARAMETER_SUFFIX_ID_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ID_ADDRESS = "_idAddress";
    

    /** The Constant CONSTANT_GEO. */
    public static final String CONSTANT_GEO = "GEO";

    /** The Constant CONSTANT_PROVIDER. */
    public static final String CONSTANT_PROVIDER = "provider";

    /** The Constant CONSTANT_ADDRESS. */
    public static final String CONSTANT_ADDRESS = "address";

    /** The Constant CONSTANT_ID_ADDRESS. */
    public static final String CONSTANT_ID_ADDRESS = "idAddress";
    
    
    private static final String MESSAGE_SPECIFY_GEO = "genericattributes.message.specifyGeo";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strGismapProvider = request.getParameter( PARAMETER_GISMAP_PROVIDER );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        /**
         * we need 5 fields : 1 for Geo, 1 for map provider, 1 for
         * address, 1 for id address and layer for layer of geo
         **/
        List<Field> listFields = new ArrayList<Field>(  );
        listFields.add( buildField( entry, CONSTANT_GEO ) );
        listFields.add( buildFieldGismapProvider( entry, strGismapProvider ) );
        listFields.add( buildField( entry, CONSTANT_ADDRESS ) );
        listFields.add( buildField( entry, CONSTANT_ID_ADDRESS ) );

        entry.setFields( listFields );

        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );

        entry.setMandatory( strMandatory != null );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse,
        Locale locale )
    {
        String strGeoValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_GEO );
        String strAddressValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_ADDRESS );
        String strIdAddressValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_ID_ADDRESS );
        
        Field fieldGeo = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_GEO, entry.getFields(  ) );
        Field fieldAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ADDRESS, entry.getFields(  ) );
        Field fieldIdAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ID_ADDRESS,
                entry.getFields(  ) );
        
        /**
         * Create the field "idAddress" in case the field does not exist in the
         * database.
         */
        if ( fieldIdAddress == null )
        {
            fieldIdAddress = buildField( entry, CONSTANT_ID_ADDRESS );
            FieldHome.create( fieldIdAddress );
        }

        // 1 : Response Geo
        Response responseGeo = new Response(  );
        responseGeo.setEntry( entry );
        responseGeo.setResponseValue( strGeoValue );
        responseGeo.setField( fieldGeo );
        responseGeo.setToStringValueResponse( strGeoValue );
        listResponse.add( responseGeo );

        // 3 : Response Address
        Response responseAddress = new Response(  );
        responseAddress.setEntry( entry );
        responseAddress.setResponseValue( strAddressValue );
        responseAddress.setField( fieldAddress );
        responseAddress.setToStringValueResponse( strAddressValue );
        listResponse.add( responseAddress );

        // 4 : Response Id Address
        Response responseIdAddress = new Response(  );
        responseIdAddress.setEntry( entry );
        responseIdAddress.setResponseValue( strIdAddressValue );
        responseIdAddress.setField( fieldIdAddress );
        responseIdAddress.setToStringValueResponse( strIdAddressValue );
        listResponse.add( responseIdAddress );
        
        if ( entry.isMandatory(  ) )
        {
            if ( StringUtils.isBlank( strAddressValue ) )
            {
                return new MandatoryError( entry, locale );
            }
        }

        if ( ( StringUtils.isBlank( strGeoValue ) ) ||
                ( StringUtils.isNotBlank( strGeoValue ) ) )
        {
            if ( StringUtils.isBlank( strAddressValue ) )
            {
                GenericAttributeError error = new GenericAttributeError(  );

                error.setMandatoryError( entry.isMandatory(  ) );
                error.setTitleQuestion( entry.getTitle(  ) );
                error.setErrorMessage( MESSAGE_SPECIFY_GEO );

                return error;
            }
        }

        return super.getResponseData( entry, request, listResponse, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( response.getField(  ) != null )
        {
            String strTitle = response.getField(  ).getTitle(  );

            if ( CONSTANT_ADDRESS.equals( strTitle ) )
            {
                return response.getResponseValue(  );
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * Returns the available map providers
     * @return all known map providers
     */
    public List<IGismapProvider> getGismapProviders(  )
    {
        return GismapProviderManager.getGismapProvidersList(  );
    }

    /**
     * Builds the {@link ReferenceList} of all available map providers
     * @return the {@link ReferenceList}
     */
    public ReferenceList getGismapProvidersRefList(  )
    {
        ReferenceList refList = new ReferenceList(  );

        refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        for ( IGismapProvider gismapProvider : GismapProviderManager.getGismapProvidersList(  ) )
        {
            refList.add( gismapProvider.toRefItem(  ) );
        }

        return refList;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        String fieldName = StringUtils.EMPTY;

        if ( response.getField(  ) != null )
        {
            fieldName = ObjectUtils.toString( response.getField(  ).getTitle(  ) );
        }

        return fieldName + GenericAttributesUtils.CONSTANT_EQUAL + response.getResponseValue(  );
    }

    // PRIVATE METHODS

    /**
     * Builds the field.
     * @param entry The entry
     * @param strFieldTitle the str field title
     * @return the field
     */
    private Field buildField( Entry entry, String strFieldTitle )
    {
        Field field = new Field(  );
        field.setTitle( strFieldTitle );
        field.setValue( strFieldTitle );
        field.setParentEntry( entry );

        return field;
    }

    /**
     * Builds the field map provider.
     * @param entry The entry
     * @param strMapProvider the map provider
     * @return the field
     */
    private Field buildFieldGismapProvider( Entry entry, String strGismapProvider )
    {
        Field fieldGismapProvider = new Field(  );
        fieldGismapProvider.setTitle( CONSTANT_PROVIDER );

        if ( StringUtils.isNotBlank( strGismapProvider ) )
        {
            String strTrimedGismapProvider = strGismapProvider.trim(  );
            fieldGismapProvider.setValue( strTrimedGismapProvider );
            entry.setGismapProvider( GismapProviderManager.getGismapProvider( strTrimedGismapProvider ) );
        }
        else
        {
            fieldGismapProvider.setValue( StringUtils.EMPTY );
        }

        fieldGismapProvider.setParentEntry( entry );

        return fieldGismapProvider;
    }

}