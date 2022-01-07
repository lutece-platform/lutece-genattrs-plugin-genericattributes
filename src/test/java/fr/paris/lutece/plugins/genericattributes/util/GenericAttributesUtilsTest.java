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
package fr.paris.lutece.plugins.genericattributes.util;

import java.util.ArrayList;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.test.LuteceTestCase;

public class GenericAttributesUtilsTest extends LuteceTestCase
{

    private static final String CODE = "code";
    private static final String TITLE_1 = "title1";
    private static final String VALUE_1 = "code1";
    private static final String TITLE_2 = "title2";
    private static final String VALUE_2 = "code2";

    public void testCreateOrUpdateFieldNew( )
    {
        Entry entry = new Entry( );
        entry.setFields( new ArrayList<>( ) );

        Field field = GenericAttributesUtils.createOrUpdateField( entry, CODE, TITLE_1, VALUE_1 );

        assertEquals( 1, entry.getFields( ).size( ) );

        assertTrue( field.getParentEntry( ) == entry );
        assertTrue( entry.getFields( ).get( 0 ) == field );
        assertEquals( CODE, field.getCode( ) );
        assertEquals( TITLE_1, field.getTitle( ) );
        assertEquals( VALUE_1, field.getValue( ) );
    }

    public void testCreateOrUpdateFieldUpdate( )
    {
        Field field = new Field( );
        field.setCode( CODE );
        field.setTitle( TITLE_1 );
        field.setValue( VALUE_1 );

        Entry entry = new Entry( );
        entry.setFields( new ArrayList<>( ) );
        entry.getFields( ).add( field );
        field.setParentEntry( entry );

        Field fieldUpdated = GenericAttributesUtils.createOrUpdateField( entry, CODE, TITLE_2, VALUE_2 );
        assertTrue( fieldUpdated == field );
        assertEquals( CODE, fieldUpdated.getCode( ) );
        assertEquals( TITLE_2, fieldUpdated.getTitle( ) );
        assertEquals( VALUE_2, fieldUpdated.getValue( ) );

    }
}
