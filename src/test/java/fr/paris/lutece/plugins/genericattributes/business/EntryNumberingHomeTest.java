/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.genericattributes.business;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;

/**
 * Counter of the numbering entries, shared by the instances through the database.
 */
public class EntryNumberingHomeTest extends LuteceTestCase
{
    // An entry without any response: the counter starts at 1
    private static final int ID_ENTRY = 987001;

    /**
     * Removes the counter.
     */
    @AfterEach
    protected void removeCounter( )
    {
        EntryNumberingHome.remove( ID_ENTRY );
    }

    /**
     * The values follow each other, starting after the highest value already given.
     */
    @Test
    public void testValuesFollowEachOther( )
    {
        assertEquals( 1, EntryNumberingHome.nextValue( ID_ENTRY ) );
        assertEquals( 2, EntryNumberingHome.nextValue( ID_ENTRY ) );
        assertEquals( 3, EntryNumberingHome.nextValue( ID_ENTRY ) );
    }

    /**
     * Concurrent submissions (threads standing for instances) never get the same value.
     *
     * @throws Exception
     *             if a thread fails
     */
    @Test
    public void testConcurrentValuesAreUnique( ) throws Exception
    {
        int nThreads = 8;
        int nValuesPerThread = 25;
        Set<Integer> setValues = ConcurrentHashMap.newKeySet( );
        CountDownLatch start = new CountDownLatch( 1 );
        ExecutorService executor = Executors.newFixedThreadPool( nThreads );
        try
        {
            List<Future<Integer>> results = new ArrayList<>( );
            for ( int i = 0; i < nThreads; i++ )
            {
                Callable<Integer> task = ( ) -> {
                    start.await( );
                    int nDuplicates = 0;
                    for ( int k = 0; k < nValuesPerThread; k++ )
                    {
                        nDuplicates += setValues.add( EntryNumberingHome.nextValue( ID_ENTRY ) ) ? 0 : 1;
                    }
                    return nDuplicates;
                };
                results.add( executor.submit( task ) );
            }
            start.countDown( );
            int nDuplicates = 0;
            for ( Future<Integer> result : results )
            {
                nDuplicates += result.get( );
            }
            assertEquals( 0, nDuplicates, "A value was given twice" );
            assertEquals( nThreads * nValuesPerThread, setValues.size( ) );
        }
        finally
        {
            executor.shutdownNow( );
        }
    }
}
