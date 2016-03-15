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
import fr.paris.lutece.plugins.genericattributes.business.IMapProvider;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.MapProviderManager;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
/**
 * @author bass
 *
 */
public abstract class AbstractEntryTypeGeospatial extends EntryTypeService {

	/** The Constant PARAMETER_MAP_PROVIDER. */
    public static final String PARAMETER_MAP_PROVIDER = "map_provider";
    
    /** The Constant PARAMETER_EDIT_MODE. */
    public static final String PARAMETER_EDIT_MODE = "edit_mode";
    
    /** The Constant PARAMETER_SUFFIX_ID_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ID_ADDRESS = "_idAddress";
    
    /** The Constant PARAMETER_SUFFIX_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ADDRESS = "_address";
    
    /** The Constant PARAMETER_SUFFIX_X_ADDRESS. */
    public static final String PARAMETER_SUFFIX_X_ADDRESS = "_xAddress";
    
    /** The Constant PARAMETER_SUFFIX_Y_ADDRESS. */
    public static final String PARAMETER_SUFFIX_Y_ADDRESS = "_yAddress";

    /** The Constant PARAMETER_SUFFIX_ID_GEO. */
    public static final String PARAMETER_SUFFIX_ID_GEO = "_idGeo";
    
    /** The Constant PARAMETER_SUFFIX_DESC_GEO. */
    public static final String PARAMETER_SUFFIX_DESC_GEO = "_descGeo";
    
    /** The Constant PARAMETER_SUFFIX_CEN_GEO. */
    //public static final String PARAMETER_SUFFIX_CEN_GEO = "_cenGeo";
    
    /** The Constant PARAMETER_SUFFIX_LAB_GEO. */
    //public static final String PARAMETER_SUFFIX_LAB_GEO = "_labGeo";
    
    /** The Constant PARAMETER_SUFFIX_THE_GEO. */
    //public static final String PARAMETER_SUFFIX_THE_GEO = "_theGeo";
    
    /** The Constant PARAMETER_SUFFIX_SPATIAL_EXTEN_INSTALLED. */
    public static final String PARAMETER_SUFFIX_SPATIAL_EXTEN_INSTALLED = "_spatialExInstalled";

 
    /** The Constant CONSTANT_PROVIDER. */
    public static final String CONSTANT_PROVIDER = "provider";
    
    /** The Constant CONSTANT_ID_ADDRESS. */
    public static final String CONSTANT_ID_ADDRESS = "idAddress";
    
    /** The Constant CONSTANT_ADDRESS. */
    public static final String CONSTANT_ADDRESS = "address";
    
    /** The Constant CONSTANT_X_ADDRESS. */
    public static final String CONSTANT_X_ADDRESS = "xAddress";
    
    /** The Constant CONSTANT_Y_ADDRESS. */
    public static final String CONSTANT_Y_ADDRESS = "yAddress";
    
    /** The Constant CONSTANT_ID_GEO. */
    public static final String CONSTANT_ID_GEO = "idGeo";

    /** The Constant CONSTANT_DESC_GEO. */
    public static final String CONSTANT_DESC_GEO = "descGeo";

    /** The Constant CONSTANT_CEN_GEO. */
    //public static final String CONSTANT_CEN_GEO = "cenGeo";

    /** The Constant CONSTANT_LAN_GEO. */
    //public static final String CONSTANT_LAB_GEO = "labGeo";
    
    /** The Constant CONSTANT_THE_GEO. */
    //public static final String CONSTANT_THE_GEO = "theGeo";
    
    /** The Constant CONSTANT_SPATIAL_EXTEN_INSTALLED. */
    public static final String CONSTANT_SPATIAL_EXTEN_INSTALLED = "spatialExInstalled";
    
    
    private static final String MESSAGE_SPECIFY_GEO = "genericattributes.message.specifyGeo";
    
    private static final String MESSAGE_SPECIFY_BOTH_X_AND_Y = "genericattributes.message.specifyBothXAndY";

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
        String strMapProvider = request.getParameter( PARAMETER_MAP_PROVIDER );
        String strEditMode = request.getParameter( PARAMETER_EDIT_MODE );
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
         * we need 10 fields : 1 for map provider, 1 for id address,
         * 1 for label address, 1 for x address, 1 for y address, 1 for id geographical object,
         * 1 for description geographical object, 1 for centroid geographical object, 1 for
         * label geographical object and 1 for thematic geographical object
         **/
        List<Field> listFields = new ArrayList<Field>(  );
        listFields.add( buildFieldMapProvider( entry, strMapProvider ) );
        listFields.add( buildField( entry, CONSTANT_ID_ADDRESS ) );
        listFields.add( buildField( entry, CONSTANT_ADDRESS ) );
        listFields.add( buildField( entry, CONSTANT_X_ADDRESS ) );
        listFields.add( buildField( entry, CONSTANT_Y_ADDRESS ) );
        listFields.add( buildField( entry, CONSTANT_ID_GEO ) );
        listFields.add( buildField( entry, CONSTANT_DESC_GEO ) );
        //listFields.add( buildField( entry, CONSTANT_CEN_GEO ) );
        //listFields.add( buildField( entry, CONSTANT_LAB_GEO ) );
        //listFields.add( buildField( entry, CONSTANT_THE_GEO ) );
        listFields.add( buildField( entry, CONSTANT_SPATIAL_EXTEN_INSTALLED ) );

        entry.setFields( listFields );

        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setEditMode( strEditMode );
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
        String strIdAddressValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_ID_ADDRESS );
        String strAddressValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_ADDRESS );
        String strXAddressValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_X_ADDRESS );
        String strYAddressValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_Y_ADDRESS );
        String strIdGeoValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_ID_GEO );
        String strDescGeoValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_DESC_GEO );
        //String strCenGeoValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_CEN_GEO );
        //String strLabGeoValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_LAB_GEO );
        //String strTheGeoValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_THE_GEO );
        String strSpatialExInsValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_SPATIAL_EXTEN_INSTALLED );
        
        Field fieldIdAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ID_ADDRESS,entry.getFields(  ) );
        Field fieldAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ADDRESS, entry.getFields(  ) );
        Field fieldXAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_X_ADDRESS,entry.getFields(  ) );
        Field fieldYAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_Y_ADDRESS,entry.getFields(  ) );
        Field fieldIdGeo = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ID_GEO, entry.getFields(  ) );
        Field fieldDescGeo = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_DESC_GEO, entry.getFields(  ) );
        //Field fieldCenGeo = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_CEN_GEO, entry.getFields(  ) );
        //Field fieldLabGeo = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_LAB_GEO, entry.getFields(  ) );
        //Field fieldTheGeo = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_THE_GEO, entry.getFields(  ) );
        Field fieldSpatialExIns = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_SPATIAL_EXTEN_INSTALLED, entry.getFields(  ) );
        
        /**
         * Create the field "idAddress" in case the field does not exist in the
         * database.
         */
        if ( fieldIdAddress == null )
        {
            fieldIdAddress = buildField( entry, CONSTANT_ID_ADDRESS );
            FieldHome.create( fieldIdAddress );
        }
        
        // 1 : Response Id Address
        Response responseIdAddress = new Response(  );
        responseIdAddress.setEntry( entry );
        responseIdAddress.setResponseValue( strIdAddressValue );
        responseIdAddress.setField( fieldIdAddress );
        responseIdAddress.setToStringValueResponse( strIdAddressValue );
        listResponse.add( responseIdAddress );
        
        // 2 : Response Address
        Response responseAddress = new Response(  );
        responseAddress.setEntry( entry );
        responseAddress.setResponseValue( strAddressValue );
        responseAddress.setField( fieldAddress );
        responseAddress.setToStringValueResponse( strAddressValue );
        listResponse.add( responseAddress );
        
        // 3 : Response X Address
        Response responseXAddress = new Response(  );
        responseXAddress.setEntry( entry );
        responseXAddress.setResponseValue( strXAddressValue );
        responseXAddress.setField( fieldXAddress );
        responseXAddress.setToStringValueResponse( strXAddressValue );
        listResponse.add( responseXAddress );
        
        // 4: Response Y Address
        Response responseYAddress = new Response(  );
        responseYAddress.setEntry( entry );
        responseYAddress.setResponseValue( strYAddressValue );
        responseYAddress.setField( fieldYAddress );
        responseYAddress.setToStringValueResponse( strYAddressValue );
        listResponse.add( responseYAddress );

        // 5 : Response Id Geo
        Response responseIdGeo = new Response(  );
        responseIdGeo.setEntry( entry );
        responseIdGeo.setResponseValue( strIdGeoValue );
        responseIdGeo.setField( fieldIdGeo );
        responseIdGeo.setToStringValueResponse( strIdGeoValue );
        listResponse.add( responseIdGeo );
        
        // 6 : Response Desc Geo
        Response responseDescGeo = new Response(  );
        responseDescGeo.setEntry( entry );
        responseDescGeo.setResponseValue( strDescGeoValue );
        responseDescGeo.setField( fieldDescGeo );
        responseDescGeo.setToStringValueResponse( strDescGeoValue );
        listResponse.add( responseDescGeo );

        // 7 : Response Cen Geo
        /*Response responseCenGeo = new Response(  );
        responseCenGeo.setEntry( entry );
        responseCenGeo.setResponseValue( strCenGeoValue );
        responseCenGeo.setField( fieldCenGeo );
        responseCenGeo.setToStringValueResponse( strCenGeoValue );
        listResponse.add( responseCenGeo );*/
        
        // 8 : Response Lab Geo
        /*Response responseLabGeo = new Response(  );
        responseLabGeo.setEntry( entry );
        responseLabGeo.setResponseValue( strLabGeoValue );
        responseLabGeo.setField( fieldLabGeo );
        responseLabGeo.setToStringValueResponse( strLabGeoValue );
        listResponse.add( responseLabGeo );*/
        
        // 9 : Response The Geo
        /*Response responseTheGeo = new Response(  );
        responseTheGeo.setEntry( entry );
        responseTheGeo.setResponseValue( strTheGeoValue );
        responseTheGeo.setField( fieldTheGeo );
        responseTheGeo.setToStringValueResponse( strTheGeoValue );
        listResponse.add( responseTheGeo );*/
        
        // 10 : Response The Geo
        Response responseSpatialExIns = new Response(  );
        responseSpatialExIns.setEntry( entry );
        responseSpatialExIns.setResponseValue( strSpatialExInsValue );
        responseSpatialExIns.setField( fieldSpatialExIns );
        responseSpatialExIns.setToStringValueResponse( strSpatialExInsValue );
        listResponse.add( responseSpatialExIns );
        
        
        if ( entry.isMandatory(  ) )
        {
            if ( StringUtils.isBlank( strAddressValue ) )
            {
                return new MandatoryError( entry, locale );
            }
        }

        if ( ( StringUtils.isBlank( strDescGeoValue ) ) ||
                ( StringUtils.isNotBlank( strDescGeoValue ) ) )
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
        if ( ( StringUtils.isBlank( strXAddressValue ) && StringUtils.isNotBlank( strYAddressValue ) ) ||
                ( StringUtils.isNotBlank( strXAddressValue ) && StringUtils.isBlank( strYAddressValue ) ) )
        {
            if ( StringUtils.isBlank( strAddressValue ) )
            {
                GenericAttributeError error = new GenericAttributeError(  );

                error.setMandatoryError( entry.isMandatory(  ) );
                error.setTitleQuestion( entry.getTitle(  ) );
                error.setErrorMessage( MESSAGE_SPECIFY_BOTH_X_AND_Y );

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
    public List<IMapProvider> getMaproviders(  )
    {
        return MapProviderManager.getMapProvidersList(  );
    }

    /**
     * Builds the {@link ReferenceList} of all available map providers
     * @return the {@link ReferenceList}
     */
    public ReferenceList getMapProvidersRefList(  )
    {
        ReferenceList refList = new ReferenceList(  );

        refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        for ( IMapProvider mapProvider : MapProviderManager.getMapProvidersList(  ) )
        {
            refList.add( mapProvider.toRefItem(  ) );
        }

        return refList;
    }
    
    /**
     * Builds the {@link ReferenceList} of all available edit mode
     * @return the {@link ReferenceList}
     */
    public ReferenceList getEditModeRefList(  )
    {
        ReferenceList refList = new ReferenceList(  );
        
        refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        ReferenceItem refItem1 = new ReferenceItem( );
        refItem1.setCode( "1" );
        refItem1.setName( "SuggestPOI" );
        refList.add( refItem1 );
        
        ReferenceItem refItem2 = new ReferenceItem( );
        refItem2.setCode( "2" );
        refItem2.setName( "Point" );
        refList.add( refItem2 );
        
        ReferenceItem refItem3 = new ReferenceItem( );
        refItem3.setCode( "3" );
        refItem3.setName( "Ligne" );
        refList.add( refItem3 );
        
        ReferenceItem refItem4 = new ReferenceItem( );
        refItem4.setCode( "4" );
        refItem4.setName( "Polygone" );
        refList.add( refItem4 );

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
    private Field buildFieldMapProvider( Entry entry, String strMapProvider )
    {
        Field fieldMapProvider = new Field(  );
        fieldMapProvider.setTitle( CONSTANT_PROVIDER );

        if ( StringUtils.isNotBlank( strMapProvider ) )
        {
            String strTrimedMapProvider = strMapProvider.trim(  );
            fieldMapProvider.setValue( strTrimedMapProvider );
            entry.setMapProvider( MapProviderManager.getMapProvider( strTrimedMapProvider ) );
        }
        else
        {
            fieldMapProvider.setValue( StringUtils.EMPTY );
        }

        fieldMapProvider.setParentEntry( entry );

        return fieldMapProvider;
    }

}