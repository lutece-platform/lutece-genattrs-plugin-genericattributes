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

import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.util.RegularExpressionUtils;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test the regular expression control an entry type carries
 */
public class EntryTypeRegularExpressionTest extends LuteceTestCase
{
    private static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String ERROR_MESSAGE = "Please enter a valid e-mail address";

    private final TestEntryTypeService _service = new TestEntryTypeService( );

    @Test
    public void testNoRegularExpression( )
    {
        assertNull( _service.check( newEntry( null, null ), "anything" ) );
    }

    @Test
    public void testMatchingValue( )
    {
        assertNull( _service.check( newEntry( REGEX_EMAIL, ERROR_MESSAGE ), "jane.doe@paris.fr" ) );
    }

    @Test
    public void testEmptyValueIsNotControlled( )
    {
        assertNull( _service.check( newEntry( REGEX_EMAIL, ERROR_MESSAGE ), "" ) );
    }

    @Test
    public void testNotMatchingValue( )
    {
        GenericAttributeError error = _service.check( newEntry( REGEX_EMAIL, ERROR_MESSAGE ), "jane.doe@paris" );
        assertNotNull( error );
        assertFalse( error.isMandatoryError( ) );
        assertEquals( ERROR_MESSAGE, error.getErrorMessage( ) );
        assertEquals( "Email", error.getTitleQuestion( ) );
    }

    @Test
    public void testPartialMatchIsRejected( )
    {
        assertNotNull( _service.check( newEntry( "[0-9]+", ERROR_MESSAGE ), "123abc" ) );
    }

    @Test
    public void testDefaultErrorMessage( )
    {
        GenericAttributeError error = _service.check( newEntry( REGEX_EMAIL, null ), "not an e-mail" );
        assertNotNull( error );
        assertNotNull( error.getErrorMessage( ) );
    }

    @Test
    public void testInvalidRegularExpressionRefusesTheValue( )
    {
        assertNotNull( _service.check( newEntry( "[unclosed", ERROR_MESSAGE ), "anything" ) );
    }

    @Test
    public void testBlankValueIsNotControlled( )
    {
        assertNull( _service.check( newEntry( REGEX_EMAIL, ERROR_MESSAGE ), "   " ) );
    }

    @Test
    public void testSurroundingSpacesAreIgnored( )
    {
        assertNull( _service.check( newEntry( REGEX_EMAIL, ERROR_MESSAGE ), "  jane.doe@paris.fr \n" ) );
    }

    @Test
    public void testErrorMessageIsEscaped( )
    {
        GenericAttributeError error = _service.check( newEntry( REGEX_EMAIL, "<img src=x onerror=alert(1)>" ), "not an e-mail" );
        assertEquals( "&lt;img src=x onerror=alert(1)&gt;", error.getErrorMessage( ) );
    }

    @Test
    public void testCatastrophicBacktrackingIsStopped( )
    {
        // Without a time limit, Java takes about 12 s on 28 characters with this expression
        String strRegex = "(.*a){20}";
        String strValue = "a".repeat( 30 ) + "!";
        long lStart = System.currentTimeMillis( );
        assertEquals( RegularExpressionUtils.MatchResult.UNDECIDED, RegularExpressionUtils.matches( strRegex, strValue, 100, 10000 ) );
        assertTrue( System.currentTimeMillis( ) - lStart < 5000, "the matching must stop near its time limit" );
        assertNotNull( _service.check( newEntry( strRegex, ERROR_MESSAGE ), strValue ), "a value the expression cannot settle is refused" );
    }

    @Test
    public void testTooLongValueIsNotEvaluated( )
    {
        assertEquals( RegularExpressionUtils.MatchResult.UNDECIDED, RegularExpressionUtils.matches( ".*", "abcdef", 100, 5 ) );
        assertEquals( RegularExpressionUtils.MatchResult.MATCH, RegularExpressionUtils.matches( ".*", "abcde", 100, 5 ) );
    }

    private Entry newEntry( String strRegex, String strErrorMessage )
    {
        EntryType entryType = new EntryType( );
        entryType.setIdTypeParent( 1 );
        entryType.setRegexPattern( strRegex );
        entryType.setRegexErrorMessage( strErrorMessage );

        Entry entry = new Entry( );
        entry.setTitle( "Email" );
        entry.setEntryType( entryType );
        return entry;
    }

    /**
     * Exposes the protected check of {@link EntryTypeService}
     */
    private static class TestEntryTypeService extends AbstractEntryTypeText
    {
        GenericAttributeError check( Entry entry, String strValue )
        {
            return checkEntryTypeRegularExpression( entry, strValue, Locale.ENGLISH );
        }

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
}
