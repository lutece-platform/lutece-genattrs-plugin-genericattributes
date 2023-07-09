/*
 * Copyright (c) 2002-2023, City of Paris
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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.Utils;

public class EntryTypeServiceManagerTest extends LuteceTestCase
{

    static final class MockEntryTypeService extends EntryTypeService
    {

        @Override
        public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
        {
            return null;
        }

        @Override
        public String getTemplateCreate( Entry entry, boolean bDisplayFront )
        {
            return null;
        }

        @Override
        public String getTemplateModify( Entry entry, boolean bDisplayFront )
        {
            return null;
        }

    }

    private static final int NUMBER_OF_ENTRYTYPES = 30;
    private static final int NUMBER_OF_QUERIES = 250_000;

    private List<String> beanNames;

    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        ConfigurableListableBeanFactory beanFactory = ( ( ConfigurableApplicationContext ) SpringContextService
                .getContext( ) ).getBeanFactory( );
        beanNames = new ArrayList<>( NUMBER_OF_ENTRYTYPES );
        IntStream.range( 0, NUMBER_OF_ENTRYTYPES ).forEach( i -> {
            String beanName = Utils.getRandomName( EntryTypeServiceManagerTest.class.getSimpleName( ) );
            beanFactory.registerSingleton( beanName, new MockEntryTypeService( ) );
            beanNames.add( beanName );
        } );

    }

    @Override
    protected void tearDown( ) throws Exception
    {
        ConfigurableListableBeanFactory beanFactory = ( ( ConfigurableApplicationContext ) SpringContextService
                .getContext( ) ).getBeanFactory( );
        beanNames.forEach( beanName -> {
            beanFactory.destroyBean( SpringContextService.getBean( beanName ) );
        } );
        super.tearDown( );
    }

    public void testGetEntryTypeService( )
    {
        Random rand = new Random( );
        List<Entry> entriesToQuery = IntStream.range( 0, NUMBER_OF_QUERIES )
                .mapToObj( i -> beanNames.get( rand.nextInt( NUMBER_OF_ENTRYTYPES ) ) ).map( beanName -> {
                    EntryType et = new EntryType( );
                    et.setBeanName( beanName );
                    return et;
                } ).map( entryType -> {
                    Entry entry = new Entry( );
                    entry.setEntryType( entryType );
                    return entry;
                } ).collect( Collectors.toList( ) );
        IntStream.of( 0, 1, 2, 3, 4, 5 ).forEach( i -> {
            Instant start = Instant.now( );
            entriesToQuery.forEach( entry -> EntryTypeServiceManager.getEntryTypeService( entry ) );
            Instant finish = Instant.now( );
            System.out.println( "Querying " + NUMBER_OF_ENTRYTYPES + " entries " + NUMBER_OF_QUERIES + " times took "
                    + Duration.between( start, finish ).toMillis( ) + "ms" );
        } );
    }

    public void testGetEntryTypeServiceNullEntry( )
    {
        assertNull( EntryTypeServiceManager.getEntryTypeService( null ) );
    }

    public void testGetEntryTypeServiceNullEntryType( )
    {
        assertNull( EntryTypeServiceManager.getEntryTypeService( new Entry( ) ) );
    }
}
