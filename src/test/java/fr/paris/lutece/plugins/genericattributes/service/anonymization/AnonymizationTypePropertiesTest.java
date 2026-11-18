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
package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Configuration of the anonymization types: each property holds the kind of value its name announces
 */
public class AnonymizationTypePropertiesTest
{
    private static final Path PROPERTIES = Path.of( "webapp/WEB-INF/conf/plugins/genericattributes.properties" );
    private static final Path SERVICES = Path.of( "src/java/fr/paris/lutece/plugins/genericattributes/service/anonymization" );
    private static final Pattern NAMED = Pattern.compile( "@Named\\s*\\(\\s*\"([^\"]+)\"" );

    @Test
    public void testAnonymizationTypeProperties( ) throws IOException
    {
        Properties properties = new Properties( );
        try ( Reader reader = Files.newBufferedReader( PROPERTIES, StandardCharsets.UTF_8 ) )
        {
            properties.load( reader );
        }
        Set<String> setServiceNames = serviceNames( );
        int nCount = 0;
        for ( String strKey : properties.stringPropertyNames( ) )
        {
            if ( !strKey.contains( "AnonymizationType." ) )
            {
                continue;
            }
            String strValue = properties.getProperty( strKey );
            nCount++;
            if ( strKey.endsWith( ".wildcard" ) )
            {
                assertTrue( strValue.matches( "%\\w" ), strKey + " must be a wildcard (%x), not " + strValue );
            }
            else if ( strKey.endsWith( ".helpKey" ) )
            {
                assertTrue( strValue.startsWith( "genericattributes.anonymization." ), strKey + " must be an i18n key, not " + strValue );
            }
            else if ( strKey.endsWith( ".serviceName" ) )
            {
                assertTrue( setServiceNames.contains( strValue ), strKey + " must name an anonymization service bean, not " + strValue );
            }
        }
        assertFalse( nCount == 0, "no anonymization type found" );
    }

    private static Set<String> serviceNames( ) throws IOException
    {
        try ( Stream<Path> stream = Files.list( SERVICES ) )
        {
            return stream.filter( p -> p.toString( ).endsWith( ".java" ) ).flatMap( p -> {
                try
                {
                    Matcher matcher = NAMED.matcher( Files.readString( p, StandardCharsets.UTF_8 ) );
                    return matcher.results( ).map( r -> r.group( 1 ) );
                }
                catch( IOException e )
                {
                    throw new IllegalStateException( e );
                }
            } ).collect( Collectors.toSet( ) );
        }
    }
}
