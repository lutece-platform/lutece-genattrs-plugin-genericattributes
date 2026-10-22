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
package fr.paris.lutece.plugins.genericattributes.business;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the ResponseHome: what is stored is read back
 */
public class ResponseHomeTest extends AbstractEntryTest
{
    private static final String TITLE = "Sortable list";
    private static final String VALUE = "Beta";
    private static final int SORT_ORDER = 3;

    private Entry _entry;
    private int _nIdResponse;

    /**
     * {@inheritDoc}
     */
    @BeforeEach
    public void setUp( ) throws Exception
    {
        super.setUp( );
        _entry = manageCreateEntry( null, TITLE, 0, 0 );
    }

    /**
     * Removes the response, the entry and the entry types
     */
    @AfterEach
    public void tearDown( )
    {
        ResponseHome.remove( _nIdResponse );
        _entryDAO.delete( _entry.getIdEntry( ), _plugin );
        removeEntryType( _nEntryTypeGroupPrimaryKey );
        removeEntryType( _nEntryTypeTextPrimaryKey );
    }

    /**
     * A response without file is read back with its status and its sort order (order of a sortable list)
     */
    @Test
    public void testStatusAndSortOrderReadBackWithoutFile( )
    {
        Response response = new Response( );
        response.setEntry( _entry );
        response.setResponseValue( VALUE );
        response.setStatus( Response.CONSTANT_STATUS_ACTIVE );
        response.setSortOrder( SORT_ORDER );
        ResponseHome.create( response );
        _nIdResponse = response.getIdResponse( );

        Response read = ResponseHome.findByPrimaryKey( _nIdResponse );
        assertEquals( VALUE, read.getResponseValue( ) );
        assertEquals( Response.CONSTANT_STATUS_ACTIVE, read.getStatus( ) );
        assertEquals( SORT_ORDER, read.getSortOrder( ) );
    }
}
