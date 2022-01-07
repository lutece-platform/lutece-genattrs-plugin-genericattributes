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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
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

    private static final String MESSAGE_SPECIFY_BOTH_X_AND_Y = "genericattributes.message.specifyBothXAndY";
    public static final String PARAMETER_EDIT_MODE_LIST = "gismap.edit.mode.list";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
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
        String strIdAddressValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_ID_ADDRESS );
        String strAddressValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_ADDRESS );
        String strAdditionalAddressValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_ADDITIONAL_ADDRESS );
        String strXValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_X );
        String strYValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_Y );
        String strGeometryValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_GEOMETRY );

        Field fieldIdAddress = entry.getFieldByCode( FIELD_ID_ADDRESS );
        Field fieldAddress = entry.getFieldByCode( FIELD_ADDRESS );
        Field fieldAdditionalAddress = entry.getFieldByCode( FIELD_ADDITIONAL_ADDRESS );
        Field fieldX = entry.getFieldByCode( FIELD_X );
        Field fieldY = entry.getFieldByCode( FIELD_Y );
        Field fieldGeometry = entry.getFieldByCode( FIELD_GEOMETRY );

        /**
         * Create the field "idAddress" in case the field does not exist in the database.
         */
        if ( fieldIdAddress == null )
        {
            fieldIdAddress = GenericAttributesUtils.createOrUpdateField( entry, FIELD_ID_ADDRESS, null, FIELD_ID_ADDRESS );
            FieldHome.create( fieldIdAddress );
        }

        // 1 : Response Id Address
        Response responseIdAddress = new Response( );
        responseIdAddress.setEntry( entry );
        responseIdAddress.setResponseValue( strIdAddressValue );
        responseIdAddress.setField( fieldIdAddress );
        responseIdAddress.setToStringValueResponse( strIdAddressValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseIdAddress );

        // 2 : Response Address
        Response responseAddress = new Response( );
        responseAddress.setEntry( entry );
        responseAddress.setResponseValue( strAddressValue );
        responseAddress.setField( fieldAddress );
        responseAddress.setToStringValueResponse( strAddressValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseAddress );

        // 3 : Response Additional Address
        Response responseAdditionalAddress = new Response( );
        responseAdditionalAddress.setEntry( entry );
        responseAdditionalAddress.setResponseValue( strAdditionalAddressValue );
        responseAdditionalAddress.setField( fieldAdditionalAddress );
        responseAdditionalAddress.setToStringValueResponse( strAdditionalAddressValue );
        responseAdditionalAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseAdditionalAddress );

        // 4 : Response X
        Response responseX = new Response( );
        responseX.setEntry( entry );
        responseX.setResponseValue( strXValue );
        responseX.setField( fieldX );
        responseX.setToStringValueResponse( strXValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseX );

        // 5: Response Y
        Response responseY = new Response( );
        responseY.setEntry( entry );
        responseY.setResponseValue( strYValue );
        responseY.setField( fieldY );
        responseY.setToStringValueResponse( strYValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseY );

        // 6 : Response Desc Geo
        Response responseGeomerty = new Response( );
        responseGeomerty.setEntry( entry );
        responseGeomerty.setResponseValue( strGeometryValue );
        responseGeomerty.setField( fieldGeometry );
        responseGeomerty.setToStringValueResponse( strGeometryValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseGeomerty );

        if ( entry.isMandatory( ) && StringUtils.isBlank( strAddressValue ) )
        {
            return new MandatoryError( entry, locale );
        }

        if ( ( ( StringUtils.isBlank( strXValue ) && StringUtils.isNotBlank( strYValue ) )
                || ( StringUtils.isNotBlank( strXValue ) && StringUtils.isBlank( strYValue ) ) ) && StringUtils.isBlank( strAddressValue ) )
        {
            GenericAttributeError error = new GenericAttributeError( );

            error.setMandatoryError( entry.isMandatory( ) );
            error.setTitleQuestion( entry.getTitle( ) );
            error.setErrorMessage( MESSAGE_SPECIFY_BOTH_X_AND_Y );

            return error;
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
        String fieldName = StringUtils.EMPTY;

        if ( response.getField( ) != null )
        {
            fieldName = ObjectUtils.toString( response.getField( ).getCode( ) );
        }

        return fieldName + GenericAttributesUtils.CONSTANT_EQUAL + response.getResponseValue( );
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
}
