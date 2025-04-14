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
package fr.paris.lutece.plugins.genericattributes.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.ReferenceItemFieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItem;
import fr.paris.lutece.plugins.referencelist.service.ReferenceItemEvent;
import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.event.Type;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;

@ApplicationScoped
public class GenattReferenceItemListener
{
    public void addReferenceItem( @ObservesAsync @Type(EventAction.CREATE) ReferenceItemEvent event )
    {
    	ReferenceItem item = event.getReferenceItem( );
        List<Entry> entryList = listConcernedEntries( item.getIdreference( ) );
        if ( CollectionUtils.isEmpty( entryList ) )
        {
            return;
        }

        for ( Entry entry : entryList )
        {
            Field field = GenericAttributesUtils.createFieldFromReferenceItem( entry, item );
            FieldHome.create( field );
        }
    }

    public void removeReferenceItem( @ObservesAsync @Type(EventAction.REMOVE) ReferenceItemEvent event )
    {
    	ReferenceItem item = event.getReferenceItem( );
        List<Integer> idFields = ReferenceItemFieldHome.findIdFieldByIdItem( item.getId( ) );
        for ( Integer id : idFields )
        {
            FieldHome.remove( id );
        }
        ReferenceItemFieldHome.removeByItem( item.getId( ) );
    }

    public void updateReferenceItem( @ObservesAsync @Type(EventAction.UPDATE) ReferenceItemEvent event )
    {
    	ReferenceItem item = event.getReferenceItem( );
        List<Integer> idFields = ReferenceItemFieldHome.findIdFieldByIdItem( item.getId( ) );
        for ( Integer id : idFields )
        {
            Field field = FieldHome.findByPrimaryKey( id );
            field.setValue( item.getCode( ) );
            field.setTitle( item.getName( ) );
            FieldHome.update( field );
        }
    }

    private List<Entry> listConcernedEntries( int idList )
    {
        List<Field> listField = FieldHome.getFieldListByCode( IEntryTypeService.FIELD_USE_REF_LIST );
        return listField.stream( ).filter( f -> Integer.parseInt( f.getTitle( ) ) == idList ).map( Field::getParentEntry ).map( Entry::getIdEntry ).distinct( )
                .map( EntryHome::findByPrimaryKey ).collect( Collectors.toList( ) );
    }
}
