/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Abstract entry type for check boxes
 */
public abstract class AbstractEntryTypeArray extends EntryTypeService
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strNumberRows = request.getParameter( PARAMETER_NUMBER_ROWS );
        String strNumberColumns = request.getParameter( PARAMETER_NUMBER_COLUMNS );
        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = ERROR_FIELD_TITLE;
        }
        else
            if ( StringUtils.isBlank( strNumberRows ) )
            {
                strFieldError = FIELD_NUMBER_ROWS;
            }
            else
                if ( StringUtils.isBlank( strNumberColumns ) )
                {
                    strFieldError = FIELD_NUMBER_COLUMNS;
                }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }
        else
            if ( !isValid( strNumberRows ) )
            {
                Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( FIELD_NUMBER_ROWS, locale )
                };

                return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
            }
            else
                if ( !isValid( strNumberColumns ) )
                {
                    Object [ ] tabRequiredFields = {
                        I18nService.getLocalizedString( FIELD_NUMBER_COLUMNS, locale )
                    };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
                }

        // for don't update fields listFields=null
        int row = Integer.valueOf( strNumberRows );
        int column = Integer.valueOf( strNumberColumns );
        entry.setCode( strCode );
        entry.setTitle( strTitle );
        entry.setHelpMessage( null );
        entry.setComment( strComment );
        entry.setCSSClass( null );
        entry.setMapProvider( null );
        entry.setNumberColumn( column );
        entry.setNumberRow( row );

        ArrayList<Field> listFields = new ArrayList<Field>( );
        List<Field> fields = FieldHome.getFieldListByIdEntry( entry.getIdEntry( ) );

        for ( int i = 1; i <= ( row + 1 ); i++ )
        {
            for ( int j = 1; j <= ( column + 1 ); j++ )
            {
                Field existingFields = null;

                for ( Field f : fields )
                {
                    if ( f.getValue( ).equals( i + "_" + j ) )
                    {
                        existingFields = f;

                        break;
                    }
                }

                String strTitleRow = request.getParameter( "field_" + i + "_" + j );

                if ( ( i == 1 ) && ( j != 1 ) )
                {
                    Field field = new Field( );

                    if ( existingFields != null )
                    {
                        field = existingFields;
                    }

                    field.setParentEntry( entry );
                    field.setValue( i + "_" + j );
                    field.setTitle( StringUtils.defaultString( strTitleRow ) );
                    listFields.add( field );
                }
                else
                    if ( ( i != 1 ) && ( j == 1 ) )
                    {
                        Field field = new Field( );

                        if ( existingFields != null )
                        {
                            field = existingFields;
                        }

                        field.setParentEntry( entry );
                        field.setValue( i + "_" + j );
                        field.setTitle( StringUtils.defaultString( strTitleRow ) );
                        listFields.add( field );
                    }
                    else
                    {
                        Field field = new Field( );
                        field.setParentEntry( entry );
                        field.setValue( i + "_" + j );
                        listFields.add( field );
                    }
            }
        }

        entry.setFields( listFields );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        for ( int i = 1; i <= ( entry.getNumberRow( ) + 1 ); i++ )
        {
            for ( int j = 1; j <= ( entry.getNumberColumn( ) + 1 ); j++ )
            {
                String strTitleRow = request.getParameter( "response_" + i + "_" + j );

                Field existingFields = null;

                for ( Field f : entry.getFields( ) )
                {
                    if ( f.getValue( ).equals( i + "_" + j ) )
                    {
                        existingFields = f;

                        break;
                    }
                }

                Response response = new Response( );
                response.setEntry( entry );
                response.setResponseValue( strTitleRow );
                response.setToStringValueResponse( strTitleRow );
                response.setField( existingFields );
                response.setIterationNumber( getResponseIterationValue( request ) );
                listResponse.add( response );
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( response.getField( ) != null )
        {
            if ( response.getField( ).getTitle( ) == null )
            {
                Field field = FieldHome.findByPrimaryKey( response.getField( ).getIdField( ) );

                if ( field != null )
                {
                    response.setField( field );
                }
            }

            return response.getField( ).getTitle( );
        }

        return null;
    }

    /**
     * Check if param is a valid integer
     * 
     * @param strValue
     *            the value to check
     * @return true if valid, false otherwise
     */
    private boolean isValid( String strValue )
    {
        if ( !StringUtils.isNumeric( strValue ) )
        {
            return false;
        }
        else
            if ( Integer.valueOf( strValue ) <= 0 )
            {
                return false;
            }

        return true;
    }
}
