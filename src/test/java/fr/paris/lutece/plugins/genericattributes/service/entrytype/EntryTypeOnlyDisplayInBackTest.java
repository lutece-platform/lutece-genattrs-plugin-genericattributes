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

import java.util.ArrayList;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * The switch « only display in back office » of the question types that read their own request data
 */
public class EntryTypeOnlyDisplayInBackTest extends LuteceTestCase
{
    @Test
    public void testGeolocation( )
    {
        Entry entry = newEntry( );
        new TestEntryTypeGeolocation( ).getRequestData( entry, request( ), Locale.FRENCH );
        assertTrue( entry.isOnlyDisplayInBack( ) );
    }

    @Test
    public void testSelectSQL( )
    {
        Entry entry = newEntry( );
        // the query of this test is not valid: the error message is expected, the switch is read before
        new TestEntryTypeSelectSQL( ).getRequestData( entry, request( ), Locale.FRENCH );
        assertTrue( entry.isOnlyDisplayInBack( ) );
    }

    private static Entry newEntry( )
    {
        Entry entry = new Entry( );
        entry.setFields( new ArrayList<>( ) );
        return entry;
    }

    private static MockHttpServletRequest request( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( IEntryTypeService.PARAMETER_TITLE, "Question" );
        request.addParameter( IEntryTypeService.PARAMETER_ONLY_DISPLAY_IN_BACK, "1" );
        return request;
    }

    private static final class TestEntryTypeGeolocation extends AbstractEntryTypeGeolocation
    {
        @Override
        public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
        {
            return "";
        }

        @Override
        public String getTemplateCreate( Entry entry, boolean bDisplayFront )
        {
            return "";
        }

        @Override
        public String getTemplateModify( Entry entry, boolean bDisplayFront )
        {
            return "";
        }
    }

    private static final class TestEntryTypeSelectSQL extends AbstractEntryTypeSelectSQL
    {
        @Override
        public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
        {
            return "";
        }

        @Override
        public String getTemplateCreate( Entry entry, boolean bDisplayFront )
        {
            return "";
        }

        @Override
        public String getTemplateModify( Entry entry, boolean bDisplayFront )
        {
            return "";
        }
    }
}
