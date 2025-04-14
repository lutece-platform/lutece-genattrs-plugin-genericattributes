/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.genericattributes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeService;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.init.LuteceInitException;
import fr.paris.lutece.test.Utils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.enterprise.inject.spi.InjectionTargetFactory;

public class EntryTypeServiceExtension implements Extension
{
    public static final List<String> beanNames = new ArrayList<>( );
    public static final int NUMBER_OF_ENTRYTYPES = 30;

    static class MockEntryTypeService extends EntryTypeService
    {

        public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
        {
            return null;
        }


        public String getTemplateCreate( Entry entry, boolean bDisplayFront )
        {
            return null;
        }


        public String getTemplateModify( Entry entry, boolean bDisplayFront )
        {
            return null;
        }
    }

    protected void addBeansToCdi( @Observes final AfterBeanDiscovery abd, final BeanManager bm ) throws LuteceInitException
    {
        IntStream.range( 0, NUMBER_OF_ENTRYTYPES ).forEach( i -> {
            String beanName = Utils.getRandomName( "entryTypeServiceManagerTest" );
            beanNames.add( beanName );

            final AnnotatedType<MockEntryTypeService> at = bm.createAnnotatedType( MockEntryTypeService.class );
            final InjectionTargetFactory<MockEntryTypeService> injectionTargetFactory = bm.getInjectionTargetFactory( at );
            final InjectionTarget<MockEntryTypeService> injectionTarget = injectionTargetFactory.createInjectionTarget( null );

            abd.addBean( )
                .beanClass( MockEntryTypeService.class )
                .name( beanName )
                .addInjectionPoints( injectionTarget.getInjectionPoints( ) )
                .addTypes( MockEntryTypeService.class, IEntryTypeService.class )
                .addQualifier( NamedLiteral.of( beanName ) )
                .scope( ApplicationScoped.class )
                .produceWith( obj -> new MockEntryTypeService( ) );

        } );
    }
}
