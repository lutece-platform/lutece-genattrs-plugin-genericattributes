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

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


import fr.paris.lutece.plugins.genericattributes.business.CartoProviderManager;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.ICartoProvider;
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
public abstract class AbstractEntryTypeCartography extends EntryTypeService
{
    public static final String PARAMETER_ID_ENTRY = "id_entry";
    public static final String PARAMETER_ID_RESOURCE = "id_resource";

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
    
    public static final String PARAMETER_SUFFIX_GEOJSON = "_geojson";
    
    public static final String PARAMETER_SUFFIX_ID_LAYER = "_idlayer";
    
    public static final String PARAMETER_COORDINATE_X = "coordinate_x";
    public static final String PARAMETER_COORDINATE_Y = "coordinate_y";
    public static final String PARAMETER_COORDINATE_POLYGON = "coordinate_polygon";
    public static final String PARAMETER_COORDINATE_POLYLINE = "coordinate_polyline";
    public static final String PARAMETER_DATALAYER = "iddatalayer";

    private static final String MESSAGE_SPECIFY_BOTH_X_AND_Y = "genericattributes.message.specifyBothXAndY";
    public static final String PARAMETER_EDIT_MODE_LIST = "gismap.edit.mode.list";
    
    public static final String PROPERTY_ENTRY_TYPE_CARTOGRAPHY_EXPORT_WITH_FIELD_NAME = "genericattributes.entrytype.cartography.export.field.name";

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
        //createOrUpdateProviderField( entry, strMapProvider );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_PROVIDER, null, strMapProvider );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_EDIT_MODE, null, strEditMode );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_VIEW_NUMBER, null, strViewNumber );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_GEOMETRY, null, FIELD_GEOMETRY );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_GEOJSON, null, FIELD_GEOJSON );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_ID_LAYER, null, FIELD_ID_LAYER );

        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setIndexed( strIndexed != null );
        entry.setMandatory( strMandatory != null );
        entry.setCode( strCode );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        
        String strCoordinateX = request.getParameter( PARAMETER_COORDINATE_X );
        String strCoordinateY = request.getParameter( PARAMETER_COORDINATE_Y );
        String strCoordinatePolygon = request.getParameter( PARAMETER_COORDINATE_POLYGON );
        String strCoordinatePolyline = request.getParameter( PARAMETER_COORDINATE_POLYLINE );
        String strIdLayer = request.getParameter( PARAMETER_DATALAYER );
        
        List<ICartoProvider> lstcartoprovider = CartoProviderManager.getMapProvidersList();
        String strGeoJson = "";
        
        //if ( !lstcartoprovider.isEmpty( )  )
        //{
	        ICartoProvider cartoService = lstcartoprovider.get(0);
	        
	        if ( strCoordinateX != null && !strCoordinateX.isEmpty( ) && strCoordinateY != null && !strCoordinateY.isEmpty( ) )
	        {
	        	strGeoJson = cartoService.getGeolocItemPoint(Double.valueOf( strCoordinateX ), Double.valueOf( strCoordinateY ), "");
	        }
	        else if ( strCoordinatePolygon != null && !strCoordinatePolygon.isEmpty( ) )
	        {
	        	strGeoJson = cartoService.getGeolocItemPolygon( strCoordinatePolygon );
	        }
	        else if ( strCoordinatePolyline != null && !strCoordinatePolyline.isEmpty( ) )
	        {
	        	strGeoJson = cartoService.getGeolocItemPolyline( strCoordinatePolyline );
	        }
        //}

        Field fieldIdAddress = entry.getFieldByCode( FIELD_ID_ADDRESS );
        Field fieldGeoJson = entry.getFieldByCode( FIELD_GEOJSON );
        Field fieldIdLayer = entry.getFieldByCode( FIELD_ID_LAYER );

        /**
         * Create the field "idAddress" in case the field does not exist in the database.
         */
        if ( fieldIdAddress == null )
        {
            fieldIdAddress = GenericAttributesUtils.createOrUpdateField( entry, FIELD_ID_ADDRESS, null, FIELD_ID_ADDRESS );
            FieldHome.create( fieldIdAddress );
        }
        
        // 1 : Response Desc GeoJson
        Response responseGeoJSON = new Response( );
        responseGeoJSON.setEntry( entry );
        responseGeoJSON.setResponseValue( strGeoJson );
        responseGeoJSON.setField( fieldGeoJson );
        responseGeoJSON.setToStringValueResponse( strGeoJson );
        responseGeoJSON.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseGeoJSON );
        
        // 2 : Response Desc IDLayer
        String solrTag = cartoService.getSolrTag( strIdLayer );
        if ( !solrTag.isEmpty( ) )
        {
        Response responseIdLayer = new Response( );
        responseIdLayer.setEntry( entry );
        responseIdLayer.setResponseValue( solrTag );
        responseIdLayer.setField( fieldIdLayer );
        responseIdLayer.setToStringValueResponse( strIdLayer );
        responseIdLayer.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseIdLayer );
        }

        return super.getResponseData( entry, request, listResponse, locale );
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
     * Builds the {@link ReferenceList} of all available map providers
     * 
     * @return the {@link ReferenceList}
     */
    public ReferenceList getMapProvidersRefList( )
    {
    	List<ICartoProvider> lstcartoprovider = CartoProviderManager.getMapProvidersList();
    	if ( !lstcartoprovider.isEmpty( ) )
    	{
    		return lstcartoprovider.get(0).getMapProvidersRefList( );
    	}
    	else
    	{
    		return new ReferenceList( );
    	}
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        String result = StringUtils.EMPTY;

        boolean isExportWithFieldName = AppPropertiesService.getPropertyBoolean( PROPERTY_ENTRY_TYPE_CARTOGRAPHY_EXPORT_WITH_FIELD_NAME, true );

        if ( isExportWithFieldName && response.getField( ) != null )
        {
            result = Objects.toString( response.getField( ).getCode( ) ) +  GenericAttributesUtils.CONSTANT_EQUAL;
        }
        result += response.getResponseValue( );
        return result;
    }

}
