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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

/**
 * Abstract entry type for selects
 */
public abstract class AbstractEntryTypeSelectOrder extends AbstractEntryTypeChoice
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
    	return getRequestData( entry, request, locale, null );
    }
	
	/**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale, String errorReturnUrl )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strOnlyDisplayInBack = request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strSortableListType = request.getParameter( PARAMETER_SORTABLE_LIST_TYPE );

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

            if( StringUtils.isNotBlank( errorReturnUrl ) )
            {
            	return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, errorReturnUrl, AdminMessage.TYPE_STOP );
            }
            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        strFieldError = createFieldsUseRefList( entry, request );
        if ( StringUtils.isNotBlank( strFieldError ) )
        {
        	Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( ERROR_FIELD_REF_LIST, locale )
            };
        	
        	if( StringUtils.isNotBlank( errorReturnUrl ) )
            {
        		return AdminMessageService.getMessageUrl( request, strFieldError, tabRequiredFields, errorReturnUrl, AdminMessage.TYPE_STOP );
            }
            return AdminMessageService.getMessageUrl( request, strFieldError, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        entry.setCode( strCode );
        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );

        entry.setMandatory( strMandatory != null );
        entry.setOnlyDisplayInBack( strOnlyDisplayInBack != null );
        entry.setIndexed( strIndexed != null );

        // Set the Entry's specific fields
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_SORTABLE_LIST_TYPE, null, strSortableListType );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strIdField = request.getParameter( PREFIX_ATTRIBUTE + entry.getIdEntry( ) );
        
        if ( strIdField != null )
        {
	        String[] fieldsIds = strIdField.split( ";" );
	        
	        boolean hasResponse = false;
	        int order = 1;
	        for ( String idField : fieldsIds )
	        {
	            String id = idField.substring( idField.indexOf( '=' ) + 1 );
	            Field field = FieldHome.findByPrimaryKey( NumberUtils.toInt( id, -1 ) );
	            if ( field != null )
	            {
	                Response response = new Response( );
	                response.setEntry( entry );
	                response.setResponseValue( field.getValue( ) );
	                response.setField( field );
	                response.setIterationNumber( getResponseIterationValue( request ) );
	                response.setSortOrder( order++ );
	
	                listResponse.add( response );
	                hasResponse = true;
	            }
	        }

	        if ( entry.isMandatory( ) && !hasResponse )
	        {
	            // Set the Response that will contain the errors to display
	            Response response = new Response( );
	            response.setEntry( entry );
	            response.setResponseValue( StringUtils.EMPTY );
	            listResponse.add( response );

	            return new MandatoryError( entry, locale );
	        }
        }
        return null;
    }
}
