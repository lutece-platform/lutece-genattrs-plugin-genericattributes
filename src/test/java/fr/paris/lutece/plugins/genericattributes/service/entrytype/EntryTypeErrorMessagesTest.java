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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * The error messages of the entry types are read from Java into a GenericAttributeError and written by the templates,
 * escaped: they are plain text.
 */
public class EntryTypeErrorMessagesTest
{
    private static final String RESOURCES = "src/java/fr/paris/lutece/plugins/genericattributes/resources";
    private static final String [ ] KEYS = { "message.errorXssField", "message.specifyBothXAndY" };

    // An entity or a tag is shown as text once escaped
    private static final Pattern MARKUP = Pattern.compile( "&[#\\w]+;|<[a-zA-Z/]" );

    @Test
    public void testErrorMessagesArePlainText( ) throws IOException
    {
        List<Path> listBundles;
        try ( Stream<Path> stream = Files.list( Path.of( RESOURCES ) ) )
        {
            listBundles = stream.filter( p -> p.getFileName( ).toString( ).endsWith( ".properties" ) ).collect( Collectors.toList( ) );
        }
        assertFalse( listBundles.isEmpty( ), "no message bundle found" );

        for ( Path bundle : listBundles )
        {
            Properties properties = new Properties( );
            try ( Reader reader = Files.newBufferedReader( bundle, StandardCharsets.UTF_8 ) )
            {
                properties.load( reader );
            }
            for ( String strKey : KEYS )
            {
                String strMessage = properties.getProperty( strKey );
                if ( strMessage != null )
                {
                    assertFalse( MARKUP.matcher( strMessage ).find( ), bundle + ": entity or tag in " + strKey );
                }
            }
        }
    }
}
