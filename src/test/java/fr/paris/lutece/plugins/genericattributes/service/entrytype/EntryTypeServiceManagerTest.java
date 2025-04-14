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
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.util.EntryTypeServiceExtension;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.test.LuteceTestCase;


public class EntryTypeServiceManagerTest extends LuteceTestCase
{
    private static final int NUMBER_OF_QUERIES = 250_000;
    
    @Test
    public void testGetEntryTypeService( )
    {
        Random rand = new Random( );
        List<Entry> entriesToQuery = IntStream.range( 0, EntryTypeServiceExtension.NUMBER_OF_ENTRYTYPES )
                .mapToObj( i -> EntryTypeServiceExtension.beanNames.get( rand.nextInt( EntryTypeServiceExtension.NUMBER_OF_ENTRYTYPES ) ) ).map( beanName -> {
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
            AppLogService.info( "Querying {} entries {} times took {} ms", EntryTypeServiceExtension.NUMBER_OF_ENTRYTYPES, NUMBER_OF_QUERIES, Duration.between( start, finish ).toMillis( ) );
        } );
    }

    @Test
    public void testGetEntryTypeServiceNullEntry( )
    {
        assertNull( EntryTypeServiceManager.getEntryTypeService( null ) );
    }

    @Test
    public void testGetEntryTypeServiceNullEntryType( )
    {
        assertNull( EntryTypeServiceManager.getEntryTypeService( new Entry( ) ) );
    }
}
