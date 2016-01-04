/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.util.ReferenceList;


/**
 * Abstract entry type for geolocation
 */
public abstract class AbstractEntryTypeGeolocation extends EntryTypeService
{
    /** The Constant PARAMETER_MAP_PROVIDER. */
    public static final String PARAMETER_MAP_PROVIDER = "map_provider";

    /** The Constant PARAMETER_SUFFIX_X. */
    public static final String PARAMETER_SUFFIX_X = "_x";

    /** The Constant PARAMETER_SUFFIX_Y. */
    public static final String PARAMETER_SUFFIX_Y = "_y";

    /** The Constant PARAMETER_SUFFIX_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ADDRESS = "_address";

    /** The Constant PARAMETER_SUFFIX_ID_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ID_ADDRESS = "_idAddress";

    /** The Constant CONSTANT_X. */
    public static final String CONSTANT_X = "X";

    /** The Constant CONSTANT_Y. */
    public static final String CONSTANT_Y = "Y";

    /** The Constant CONSTANT_PROVIDER. */
    public static final String CONSTANT_PROVIDER = "provider";

    /** The Constant CONSTANT_ADDRESS. */
    public static final String CONSTANT_ADDRESS = "address";

    /** The Constant CONSTANT_ID_ADDRESS. */
    public static final String CONSTANT_ID_ADDRESS = "idAddress";
    private static final String MESSAGE_SPECIFY_BOTH_X_AND_Y = "genericattributes.message.specifyBothXAndY";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strMapProvider = request.getParameter( PARAMETER_MAP_PROVIDER );
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
         * we need 5 fields : 1 for X, 1 for Y, 1 for map provider, 1 for
         * address and 1 for id address
         **/
        List<Field> listFields = new ArrayList<Field>(  );
        listFields.add( buildField( entry, CONSTANT_X ) );
        listFields.add( buildField( entry, CONSTANT_Y ) );
        listFields.add( buildFieldMapProvider( entry, strMapProvider ) );
        listFields.add( buildField( entry, CONSTANT_ADDRESS ) );
        listFields.add( buildField( entry, CONSTANT_ID_ADDRESS ) );

        entry.setFields( listFields );
        entry.setCode( strCode );
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
        String strXValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_X );
        String strYValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_Y );
        String strAddressValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_ADDRESS );
        String strIdAddressValue = request.getParameter( entry.getIdEntry(  ) + PARAMETER_SUFFIX_ID_ADDRESS );

        Field fieldX = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_X, entry.getFields(  ) );
        Field fieldY = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_Y, entry.getFields(  ) );
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

        // 1 : Response X
        Response responseX = new Response(  );
        responseX.setEntry( entry );
        responseX.setResponseValue( strXValue );
        responseX.setField( fieldX );
        responseX.setToStringValueResponse( strXValue );
        listResponse.add( responseX );

        // 2 : Response Y
        Response responseY = new Response(  );
        responseY.setEntry( entry );
        responseY.setResponseValue( strYValue );
        responseY.setField( fieldY );
        responseY.setToStringValueResponse( strYValue );
        listResponse.add( responseY );

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

        if ( ( StringUtils.isBlank( strXValue ) && StringUtils.isNotBlank( strYValue ) ) ||
                ( StringUtils.isNotBlank( strXValue ) && StringUtils.isBlank( strYValue ) ) )
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
    public List<IMapProvider> getMapProviders(  )
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
        field.setCode( strFieldTitle );

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
