/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
/**
 *
 */
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;

/**
 * @author bass
 *
 */
public abstract class AbstractEntryTypeGeolocation extends EntryTypeService
{

    /** The Constant PARAMETER_MAP_PROVIDER. */
    public static final String PARAMETER_MAP_PROVIDER = "map_provider";

    /** The Constant PARAMETER_EDIT_MODE. */
    public static final String PARAMETER_EDIT_MODE = "edit_mode";

    /** The Constant PARAMETER_VIEW_NUMBER. */
    public static final String PARAMETER_VIEW_NUMBER = "view_number";

    /** The Constant PARAMETER_SUFFIX_ID_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ID_ADDRESS = "_idAddress";

    /** The Constant PARAMETER_SUFFIX_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ADDRESS = "_address";

    /** The Constant PARAMETER_SUFFIX_ADDITIONAL_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ADDITIONAL_ADDRESS = "_additional_address";

    /** The Constant PARAMETER_SUFFIX_X_ADDRESS. */
    public static final String PARAMETER_SUFFIX_X = "_x";

    /** The Constant PARAMETER_SUFFIX_Y_ADDRESS. */
    public static final String PARAMETER_SUFFIX_Y = "_y";

    /** The Constant PARAMETER_SUFFIX_GEOMETRY. */
    public static final String PARAMETER_SUFFIX_GEOMETRY = "_geometry";
    /**
     * The Constant PARAMETER_PREFIX_ITERATION.
     */
    public static final String PARAMETER_PREFIX_ITERATION = "nIt";

    /**
     * The Constant PARAMETER_NUMBER_ITERATION.
     */
    public static final String PARAMETER_NUMBER_ITERATION = "number_iteration_geolocation";

    /**
     * The Constant ATTRIBUTE_LAST_ITERATION_GEOLOCATION.
     */
    public static final String ATTRIBUTE_LAST_ITERATION_GEOLOCATION = "last_iteration_geolocation";

    private static final String MESSAGE_SPECIFY_BOTH_X_AND_Y = "genericattributes.message.specifyBothXAndY";
    public static final String PARAMETER_EDIT_MODE_LIST = "gismap.edit.mode.list";
    
    public static final String PROPERTY_ENTRY_TYPE_GEOLOCALISATION_EXPORT_WITH_FIELD_NAME = "genericattributes.entrytype.geolocalisation.export.field.name";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strMapProvider = request.getParameter( PARAMETER_MAP_PROVIDER );
        String strEditMode = request.getParameter( PARAMETER_EDIT_MODE );
        String strViewNumber = request.getParameter( PARAMETER_VIEW_NUMBER );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = ERROR_FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        /**
         * we need 10 fields : 1 for map provider, 1 for id address, 1 for label address, 1 for x address, 1 for y address, 1 for id geographical object, 1 for
         * description geographical object, 1 for centroid geographical object, 1 for label geographical object and 1 for thematic geographical object
         **/
        createOrUpdateProviderField( entry, strMapProvider );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_EDIT_MODE, null, strEditMode );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_VIEW_NUMBER, null, strViewNumber );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_ID_ADDRESS, null, FIELD_ID_ADDRESS );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_ADDRESS, null, FIELD_ADDRESS );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_ADDITIONAL_ADDRESS, null, FIELD_ADDITIONAL_ADDRESS );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_X, null, FIELD_X );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_Y, null, FIELD_Y );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_GEOMETRY, null, FIELD_GEOMETRY );

        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setIndexed( strIndexed != null );
        entry.setMandatory( strMandatory != null );
        entry.setCode( strCode );
        return null;
    }

    private void createOrUpdateProviderField( Entry entry, String fieldValue )
    {
        Field field = entry.getFieldByCode( FIELD_PROVIDER );
        if ( field == null )
        {
            entry.getFields( ).add( buildFieldMapProvider( entry, fieldValue ) );
        }
        else
        {
            Field newProvider = buildFieldMapProvider( entry, fieldValue );
            field.setValue( newProvider.getValue( ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        Integer lastIterationGeolocation = 0;
        Integer iterationNumberToSave = 0;

        if ( request.getAttribute( ATTRIBUTE_LAST_ITERATION_GEOLOCATION ) != null )
        {
            lastIterationGeolocation = (Integer) request.getAttribute( ATTRIBUTE_LAST_ITERATION_GEOLOCATION );
            lastIterationGeolocation += 1;
            request.setAttribute( ATTRIBUTE_LAST_ITERATION_GEOLOCATION, lastIterationGeolocation );
        }
        else
        {
            request.setAttribute( ATTRIBUTE_LAST_ITERATION_GEOLOCATION, 0 );
        }
        if ( request.getParameter( PARAMETER_NUMBER_ITERATION ) != null )
        {
            iterationNumberToSave = lastIterationGeolocation;
        }
        else
        {
            iterationNumberToSave = -1;
        }

        // Get the value of the fields containing the user's input
        String prefixIteration = String.valueOf(entry.getIdEntry( ));
        String prefixWithIteration = PARAMETER_PREFIX_ITERATION + lastIterationGeolocation + "_" + IEntryTypeService.PREFIX_ATTRIBUTE + entry.getIdEntry();
        if(request.getParameter(prefixWithIteration+PARAMETER_SUFFIX_ADDRESS) != null)
        {
          prefixIteration = prefixWithIteration;
        }

        // Retrieve the values from the user's input
        String strIdAddressValue = request.getParameter(prefixIteration + PARAMETER_SUFFIX_ID_ADDRESS);
        String strAddressValue = request.getParameter(prefixIteration + PARAMETER_SUFFIX_ADDRESS);
        String strAdditionalAddressValue = request.getParameter(prefixIteration + entry.getIdEntry() + PARAMETER_SUFFIX_ADDITIONAL_ADDRESS);
        String strXValue = request.getParameter(prefixIteration + PARAMETER_SUFFIX_X);
        String strYValue = request.getParameter(prefixIteration + PARAMETER_SUFFIX_Y);
        String strGeometryValue = request.getParameter(prefixIteration + PARAMETER_SUFFIX_GEOMETRY);

        // Get the List of responses for this Entry
        List<Response> listEntryResponse = getGeolocationResponseList( entry, iterationNumberToSave, strIdAddressValue, strAddressValue, strAdditionalAddressValue,
                strXValue, strYValue, strGeometryValue );
        if ( CollectionUtils.isNotEmpty( listEntryResponse ) )
        {
            listResponse.addAll( listEntryResponse );
        }

        if (entry.isMandatory() && StringUtils.isBlank(strAddressValue)) {
            return new MandatoryError(entry, locale);
        }

        if (((StringUtils.isBlank(strXValue) && StringUtils.isNotBlank(strYValue))
                || (StringUtils.isNotBlank(strXValue) && StringUtils.isBlank(strYValue))) && StringUtils.isBlank(strAddressValue)) {
            GenericAttributeError error = new GenericAttributeError();

            error.setMandatoryError(entry.isMandatory());
            error.setTitleQuestion(entry.getTitle());
            error.setErrorMessage(MESSAGE_SPECIFY_BOTH_X_AND_Y);

            return error;
        }
        return super.getResponseData(entry, request, listResponse, locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( response.getField( ) != null )
        {
            String strCode = response.getField( ).getCode( );

            if ( FIELD_ADDRESS.equals( strCode ) )
            {
                return response.getResponseValue( );
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * Returns the available map providers
     * 
     * @return all known map providers
     */
    public List<IMapProvider> getMaproviders( )
    {
        return MapProviderManager.getMapProvidersList( );
    }

    /**
     * Builds the {@link ReferenceList} of all available map providers
     * 
     * @return the {@link ReferenceList}
     */
    public ReferenceList getMapProvidersRefList( )
    {
        ReferenceList refList = new ReferenceList( );

        refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        for ( IMapProvider mapProvider : MapProviderManager.getMapProvidersList( ) )
        {
            refList.add( mapProvider.toRefItem( ) );
        }

        return refList;
    }

    /**
     * Builds the {@link ReferenceList} of all available edit mode
     * 
     * @return the {@link ReferenceList}
     */
    public ReferenceList getEditModeRefList( )
    {
        String strEditModeListProperty = AppPropertiesService.getProperty( PARAMETER_EDIT_MODE_LIST );
        ReferenceList refList = new ReferenceList( );
        refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        if ( strEditModeListProperty != null )
        {
            String [ ] strEditModeListPropertyArray = strEditModeListProperty.split( "," );

            for ( int i = 0; i < strEditModeListPropertyArray.length; i++ )
            {
                refList.addItem( strEditModeListPropertyArray [i], strEditModeListPropertyArray [i] );
            }
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
        String result = StringUtils.EMPTY;

        boolean isExportWithFieldName = AppPropertiesService.getPropertyBoolean( PROPERTY_ENTRY_TYPE_GEOLOCALISATION_EXPORT_WITH_FIELD_NAME, true );

        if ( isExportWithFieldName && response.getField( ) != null )
        {
            result = Objects.toString( response.getField( ).getCode( ) ) +  GenericAttributesUtils.CONSTANT_EQUAL;
        }
        result += response.getResponseValue( );
        return result;
    }

    // PRIVATE METHODS

    /**
     * Builds the field map provider.
     * 
     * @param entry
     *            The entry
     * @param strMapProvider
     *            the map provider
     * @return the field
     */
    private Field buildFieldMapProvider( Entry entry, String strMapProvider )
    {
        Field fieldMapProvider = new Field( );
        fieldMapProvider.setCode( FIELD_PROVIDER );

        if ( StringUtils.isNotBlank( strMapProvider ) )
        {
            String strTrimedMapProvider = strMapProvider.trim( );
            fieldMapProvider.setValue( strTrimedMapProvider );
        }
        else
        {
            fieldMapProvider.setValue( StringUtils.EMPTY );
        }

        fieldMapProvider.setParentEntry( entry );

        return fieldMapProvider;
    }

    /**
     * Get the List of Responses from a Geolocation Entry, containing the address values for the user's input
     * 
     * @param entry
     *            The Entry to process
     * @param iterationNumberToSave
     *            Current iteration of this Entry
     * @param strIdAddressValue
     *            Specific ID of the address
     * @param strAddressValue
     *            Actual value of the address
     * @param strAdditionalAddressValue
     *            Optional extra address value
     * @param strXValue
     *            X coordinates of the address
     * @param strYValue
     *            Y coordinates of the address
     * @param strGeometryValue
     * 
     * @return a List of Reponses
     */
    private List<Response> getGeolocationResponseList( Entry entry, int iterationNumberToSave, String strIdAddressValue, String strAddressValue,
            String strAdditionalAddressValue, String strXValue, String strYValue, String strGeometryValue )
    {
        // List of all the responses retrieved from the Geolocation Entry's input
        List<Response> listGeolocationResponses = new ArrayList<>( );

        // Fields part of Geolocation entries
        Field fieldIdAddress = entry.getFieldByCode( FIELD_ID_ADDRESS );
        Field fieldAddress = entry.getFieldByCode( FIELD_ADDRESS );
        Field fieldAdditionalAddress = entry.getFieldByCode( FIELD_ADDITIONAL_ADDRESS );
        Field fieldX = entry.getFieldByCode( FIELD_X );
        Field fieldY = entry.getFieldByCode( FIELD_Y );
        Field fieldGeometry = entry.getFieldByCode( FIELD_GEOMETRY );

        // Create the field "idAddress" in case the field does not exist in the database
        if ( fieldIdAddress == null )
        {
            fieldIdAddress = GenericAttributesUtils.createOrUpdateField( entry, FIELD_ID_ADDRESS, null, FIELD_ID_ADDRESS );
            FieldHome.create( fieldIdAddress );
        }

        // 1 : Response Id Address
        if ( strIdAddressValue != null )
        {
            Response responseIdAddress = new Response( );
            responseIdAddress.setEntry( entry );
            responseIdAddress.setResponseValue( strIdAddressValue );
            responseIdAddress.setField( fieldIdAddress );
            responseIdAddress.setToStringValueResponse( strIdAddressValue );
            responseIdAddress.setIterationNumber( iterationNumberToSave );
            listGeolocationResponses.add( responseIdAddress );
        }

        // 2 : Response Address
        if ( strAddressValue != null )
        {
            Response responseAddress = new Response( );
            responseAddress.setEntry( entry );
            responseAddress.setResponseValue( strAddressValue );
            responseAddress.setField( fieldAddress );
            responseAddress.setToStringValueResponse( strAddressValue );
            responseAddress.setIterationNumber( iterationNumberToSave );
            listGeolocationResponses.add( responseAddress );
        }

        // 3 : Response Additional Address
        if ( strAdditionalAddressValue != null )
        {
            Response responseAdditionalAddress = new Response( );
            responseAdditionalAddress.setEntry( entry );
            responseAdditionalAddress.setResponseValue( strAdditionalAddressValue );
            responseAdditionalAddress.setField( fieldAdditionalAddress );
            responseAdditionalAddress.setToStringValueResponse( strAdditionalAddressValue );
            responseAdditionalAddress.setIterationNumber( iterationNumberToSave );
            listGeolocationResponses.add( responseAdditionalAddress );
        }
        // 4 : Response X
        if ( strXValue != null )
        {
            Response responseX = new Response( );
            responseX.setEntry( entry );
            responseX.setResponseValue( strXValue );
            responseX.setField( fieldX );
            responseX.setToStringValueResponse( strXValue );
            responseX.setIterationNumber( iterationNumberToSave );
            listGeolocationResponses.add( responseX );
        }

        // 5: Response Y
        if ( strYValue != null )
        {
            Response responseY = new Response( );
            responseY.setEntry( entry );
            responseY.setResponseValue( strYValue );
            responseY.setField( fieldY );
            responseY.setToStringValueResponse( strYValue );
            responseY.setIterationNumber( iterationNumberToSave );
            listGeolocationResponses.add( responseY );
        }

        // 6 : Response Desc Geo
        if ( strGeometryValue != null )
        {
            Response responseGeomerty = new Response( );
            responseGeomerty.setEntry( entry );
            responseGeomerty.setResponseValue( strGeometryValue );
            responseGeomerty.setField( fieldGeometry );
            responseGeomerty.setToStringValueResponse( strGeometryValue );
            responseGeomerty.setIterationNumber( iterationNumberToSave );
            listGeolocationResponses.add( responseGeomerty );
        }
        return listGeolocationResponses;
    }
}
