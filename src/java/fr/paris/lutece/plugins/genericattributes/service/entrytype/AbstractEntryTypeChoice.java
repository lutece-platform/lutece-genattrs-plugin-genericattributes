/*
 * Copyright (c) 2002-2021, City of Paris
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
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.ReferenceItemFieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItem;
import fr.paris.lutece.plugins.referencelist.service.ReferenceItemListService;

public abstract class AbstractEntryTypeChoice extends EntryTypeService
{

    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue( );
    }

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

    protected String createFieldsUseRefList( Entry entry, HttpServletRequest request )
    {
        String strUseRefList = request.getParameter( PARAMETER_USE_REF_LIST );
        String strRefListSelect = request.getParameter( PARAMETER_REF_LIST_SELECT );

        boolean useRefList = false;
        int idRefList = -1;
        if ( StringUtils.isNotEmpty( strUseRefList ) )
        {
            if ( StringUtils.isEmpty( strRefListSelect ) )
            {
                return MESSAGE_MANDATORY_FIELD;
            }
            useRefList = true;
            idRefList = Integer.parseInt( strRefListSelect );
        }

        int oldIdRefList = -1;
        Field field = entry.getFieldByCode( FIELD_USE_REF_LIST );
        if ( field != null )
        {
            oldIdRefList = Integer.parseInt( field.getTitle( ) );
        }
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_USE_REF_LIST, String.valueOf( idRefList ), String.valueOf( useRefList ) );

        // If the reference list has changed
        if ( oldIdRefList != idRefList )
        {
            // We remove the old answer choices
            List<Field> anwserFields = entry.getFields( ).stream( ).filter( f -> IEntryTypeService.FIELD_ANSWER_CHOICE.equals( f.getCode( ) ) )
                    .collect( Collectors.toList( ) );

            for ( Field anwserField : anwserFields )
            {
                FieldHome.remove( anwserField.getIdField( ) );
                ReferenceItemFieldHome.removeByField( anwserField.getIdField( ) );
                entry.getFields( ).removeIf( f -> f.getIdField( ) == anwserField.getIdField( ) );
            }

            // We create the needed answer choices
            if ( useRefList )
            {
                for ( ReferenceItem item : ReferenceItemListService.getInstance( ).getReferenceItemsList( idRefList ) )
                {
                    Field anwserField = GenericAttributesUtils.createFieldFromReferenceItem( entry, item );
                    entry.getFields( ).add( anwserField );
                }
            }
        }
        return null;
    }
}
