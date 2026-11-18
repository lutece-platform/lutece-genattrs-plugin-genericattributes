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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Number entry type: values too large for an integer
 */
public class AbstractEntryTypeNumberTest extends LuteceTestCase
{
    private static final int ID_ENTRY = 7;
    private static final String TOO_LARGE = "99999999999";

    private static class TestEntryTypeNumber extends AbstractEntryTypeNumber
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

    private static Entry buildEntry( String strMax )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( ID_ENTRY );
        entry.setTitle( "Number" );
        List<Field> listFields = new ArrayList<>( );
        if ( strMax != null )
        {
            Field field = new Field( );
            field.setCode( IEntryTypeService.FIELD_MAX );
            field.setValue( strMax );
            listFields.add( field );
        }
        entry.setFields( listFields );
        return entry;
    }

    private static GenericAttributeError answer( Entry entry, String strValue )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( IEntryTypeService.PREFIX_ATTRIBUTE + ID_ENTRY, strValue );
        List<Response> listResponse = new ArrayList<>( );
        return new TestEntryTypeNumber( ).getResponseData( entry, request, listResponse, Locale.ENGLISH );
    }

    /**
     * A value over 2147483647 gives the error of the maximum value instead of a NumberFormatException
     */
    @Test
    public void testValueTooLargeWithoutMax( )
    {
        GenericAttributeError error = answer( buildEntry( null ), TOO_LARGE );
        assertNotNull( error );
        assertFalse( error.isMandatoryError( ) );
        assertEquals( I18nService.getLocalizedString( IEntryTypeService.MESSAGE_NUMERIC_MAX, new Object [ ] {
                String.valueOf( Integer.MAX_VALUE )
        }, Locale.ENGLISH ), error.getErrorMessage( ) );
    }

    /**
     * With a configured maximum, the error gives that maximum
     */
    @Test
    public void testValueTooLargeWithMax( )
    {
        GenericAttributeError error = answer( buildEntry( "100" ), TOO_LARGE );
        assertNotNull( error );
        assertEquals( I18nService.getLocalizedString( IEntryTypeService.MESSAGE_NUMERIC_MAX, new Object [ ] {
                "100"
        }, Locale.ENGLISH ), error.getErrorMessage( ) );
    }

    /**
     * A value in the range of an integer is accepted
     */
    @Test
    public void testValueInRange( )
    {
        assertNull( answer( buildEntry( null ), "2147483647" ) );
    }
}
