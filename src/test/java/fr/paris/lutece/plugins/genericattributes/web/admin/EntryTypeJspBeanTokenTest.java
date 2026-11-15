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

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.event.MvcEvent.ControllerInvocationType;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * The security token of the entry type screens: each action must accept the token generated when the page posting it was displayed
 */
public class EntryTypeJspBeanTokenTest extends LuteceTestCase
{
    private static final String CONTROLLER = "/jsp/admin/plugins/genericattributes/ManageEntryType.jsp";

    @Inject
    private SecurityTokenHandler _handler;

    /**
     * The buttons of the list of entry types (enable or disable, move up, move down) post the token of the list
     */
    @Test
    public void testManageViewTokenAcceptedByItsActions( )
    {
        for ( String strAction : new String [ ] {
                "doChangeActive", "doMoveUp", "doMoveDown"
        } )
        {
            assertTrue( isTokenOfViewAccepted( "manageEntryType", strAction ), "The token of the list must be accepted by " + strAction );
        }
    }

    /**
     * The forms of creation, modification and removal of an entry type post the token of their page
     */
    @Test
    public void testFormViewTokenAcceptedByItsAction( )
    {
        assertTrue( isTokenOfViewAccepted( "createEntryType", "createEntryType" ) );
        assertTrue( isTokenOfViewAccepted( "editEntryType", "modifyEntryType" ) );
        assertTrue( isTokenOfViewAccepted( "confirmRemoveEntryType", "removeEntryType" ) );
    }

    /**
     * Displays the view as the MVC controller does, then posts the action with the token of the page, as the security filter checks it
     */
    private boolean isTokenOfViewAccepted( String strView, String strAction )
    {
        _handler.registerActions( CONTROLLER.substring( 1 ), EntryTypeJspBean.class.getDeclaredMethods( ) );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setServletPath( CONTROLLER );
        _handler.handle( request );
        _handler.handleToken( request, ControllerInvocationType.VIEW, findMethod( View.class, strView ) );
        String strToken = _handler.resolveTokenValue( request );
        assertNotNull( strToken );

        request.setMethod( "POST" );
        request.setParameter( "action", strAction );
        request.setParameter( SecurityTokenHandler.MARK_CSRF_TOKEN, strToken );
        assertNotNull( findMethod( Action.class, strAction ) );

        return _handler.validate( request, _handler.resolveTokenAction( request, strAction ) );
    }

    private static Method findMethod( Class<? extends java.lang.annotation.Annotation> annotation, String strName )
    {
        for ( Method method : EntryTypeJspBean.class.getDeclaredMethods( ) )
        {
            if ( method.isAnnotationPresent( View.class ) && annotation == View.class && strName.equals( method.getAnnotation( View.class ).value( ) )
                    || method.isAnnotationPresent( Action.class ) && annotation == Action.class
                            && strName.equals( method.getAnnotation( Action.class ).value( ) ) )
            {
                return method;
            }
        }
        return null;
    }
}
