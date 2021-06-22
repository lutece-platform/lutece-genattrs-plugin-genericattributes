/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;

import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.HtmlTemplate;

@Controller( controllerJsp = "ManageEntryType.jsp", controllerPath = "jsp/admin/plugins/genericattributes/", right = "ENTRY_TYPE_MANAGEMENT" )
public class EntryTypeJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -346665476651669896L;

    // Parameters
    private static final String PARAMETER_ID = "id_type";

    // Views
    private static final String VIEW_MANAGE_ENTRY_TYPE = "manageEntryType";

    // Templates
    private static final String TEMPLATE_MANAGE_ENTRY_TYPE = "/admin/plugins/genericattributes/manage_entry_type.html";

    // Marks
    private static final String MARK_ENTRY_TYPE_LIST = "entryTypeList";

    // Actions
    private static final String ACTION_CHANGE_ACTIVE = "doChangeActive";
    private static final String ACTION_MOVE_UP = "doMoveUp";
    private static final String ACTION_MOVE_DOWN = "doMoveDown";

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_ENTRY_TYPE, defaultView = true )
    public String getManageEntryType( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeHome.getCompleteList( ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ENTRY_TYPE, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Change the inactive value of the EntryType
     * 
     * @param request
     * @return
     */
    @Action( ACTION_CHANGE_ACTIVE )
    public String doChangeActive( HttpServletRequest request )
    {
        int idType = NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 );

        if ( idType != -1 )
        {
            EntryType entryType = EntryTypeHome.findByPrimaryKey( idType );
            if ( entryType != null )
            {
                entryType.setInactive( !entryType.isInactive( ) );
                EntryTypeHome.update( entryType );
            }
        }

        return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
    }

    /**
     * Increase the priority of the EntryType
     * 
     * @param request
     * @return
     */
    @Action( ACTION_MOVE_UP )
    public String doMoveUp( HttpServletRequest request )
    {
        int idType = NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 );

        if ( idType != -1 )
        {
            EntryType entryTypeToMoveUp = EntryTypeHome.findByPrimaryKey( idType );
            changeOrder( entryTypeToMoveUp, -1 );
        }

        return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
    }

    /**
     * Decrease the priority of the EntryType
     * 
     * @param request
     * @return
     */
    @Action( ACTION_MOVE_DOWN )
    public String doMoveDown( HttpServletRequest request )
    {
        int idType = NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 );

        if ( idType != -1 )
        {
            EntryType entryTypeToMoveDown = EntryTypeHome.findByPrimaryKey( idType );
            changeOrder( entryTypeToMoveDown, 1 );
        }

        return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
    }

    private void changeOrder( EntryType entryToMove, int orderChange )
    {
        int oldOrder = entryToMove.getOrder( );
        int newOrder = oldOrder + orderChange;

        for ( EntryType entryType : EntryTypeHome.getList( entryToMove.getPlugin( ) ) )
        {
            if ( entryType.getOrder( ) == newOrder )
            {
                entryType.setOrder( oldOrder );
                EntryTypeHome.update( entryType );

                entryToMove.setOrder( newOrder );
                EntryTypeHome.update( entryToMove );
            }
        }
    }
}
