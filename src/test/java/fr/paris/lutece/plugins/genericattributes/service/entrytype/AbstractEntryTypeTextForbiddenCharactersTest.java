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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * A text answer is stored as typed: the templates escape it on output, so the characters &lt; &gt; # and " are no longer refused on input.
 */
public class AbstractEntryTypeTextForbiddenCharactersTest extends LuteceTestCase
{
    private static final int ID_ENTRY = 1;
    private static final String VALUE = "12 # bis <b>\"rue\"</b> & cour";

    private static class TestEntryTypeText extends AbstractEntryTypeText
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

    @Test
    public void testTextAnswerWithFormerlyForbiddenCharactersIsAccepted( )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( ID_ENTRY );
        entry.setTitle( "Address" );

        List<Field> listFields = new ArrayList<>( );
        listFields.add( field( IEntryTypeService.FIELD_MAX_SIZE, "-1" ) );
        listFields.add( field( IEntryTypeService.FIELD_TEXT_CONF, null ) );
        entry.setFields( listFields );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( IEntryTypeService.PREFIX_ATTRIBUTE + ID_ENTRY, VALUE );

        List<Response> listResponse = new ArrayList<>( );
        GenericAttributeError error = new TestEntryTypeText( ).getResponseData( entry, request, listResponse, Locale.FRENCH );

        assertNull( error, "The characters < > # \" & are accepted" );
        assertEquals( VALUE, listResponse.get( 0 ).getResponseValue( ), "The answer is stored as typed, not encoded" );
    }

    private static Field field( String strCode, String strValue )
    {
        Field field = new Field( );
        field.setCode( strCode );
        field.setValue( strValue );

        return field;
    }
}
