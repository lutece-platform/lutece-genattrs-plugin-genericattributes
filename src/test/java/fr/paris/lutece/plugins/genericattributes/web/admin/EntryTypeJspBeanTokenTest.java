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
package fr.paris.lutece.plugins.genericattributes.web.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * The forms of the entry type list carry the security token generated for the list view: every action they post must be checked under this key.
 */
public class EntryTypeJspBeanTokenTest
{
    private static final String TEMPLATE_MANAGE_ENTRY_TYPE = "webapp/WEB-INF/templates/admin/plugins/genericattributes/manage_entry_type.html";
    private static final Pattern PATTERN_POSTED_ACTION = Pattern.compile( "name='action' value='([^']+)'" );

    @Test
    public void testListActionsAcceptTheTokenOfTheListView( ) throws IOException
    {
        String strTemplate = new String( Files.readAllBytes( Paths.get( TEMPLATE_MANAGE_ENTRY_TYPE ) ), StandardCharsets.UTF_8 );
        Set<String> postedActions = new LinkedHashSet<>( );
        Matcher matcher = PATTERN_POSTED_ACTION.matcher( strTemplate );
        while ( matcher.find( ) )
        {
            postedActions.add( matcher.group( 1 ) );
        }
        assertFalse( postedActions.isEmpty( ), "No action posted by the list page" );

        String strViewTokenKey = null;
        for ( Method method : EntryTypeJspBean.class.getDeclaredMethods( ) )
        {
            View view = method.getAnnotation( View.class );
            if ( view != null && view.defaultView( ) )
            {
                strViewTokenKey = view.securityTokenAction( ).isEmpty( ) ? view.value( ) : view.securityTokenAction( );
            }
        }
        assertNotNull( strViewTokenKey, "No default view" );

        for ( String strAction : postedActions )
        {
            Action action = findAction( strAction );
            assertNotNull( action, "No @Action for " + strAction );
            String strTokenKey = action.securityTokenAction( ).isEmpty( ) ? action.value( ) : action.securityTokenAction( );
            assertEquals( strViewTokenKey, strTokenKey, "Token key of the action " + strAction );
        }
    }

    private static Action findAction( String strAction )
    {
        for ( Method method : EntryTypeJspBean.class.getDeclaredMethods( ) )
        {
            Action action = method.getAnnotation( Action.class );
            if ( action != null && action.value( ).equals( strAction ) )
            {
                return action;
            }
        }
        return null;
    }
}
