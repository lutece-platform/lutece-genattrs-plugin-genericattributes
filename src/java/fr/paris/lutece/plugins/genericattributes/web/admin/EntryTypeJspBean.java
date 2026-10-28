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
package fr.paris.lutece.plugins.genericattributes.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.DerivedEntryTypeService;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.DerivedEntryTypeService.ValidationError;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.url.UrlItem;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped
@Named
@Controller( controllerJsp = "ManageEntryType.jsp", controllerPath = "jsp/admin/plugins/genericattributes/", right = "ENTRY_TYPE_MANAGEMENT", securityTokenEnabled = true )
public class EntryTypeJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -346665476651669896L;

    // Parameters
    private static final String PARAMETER_ID = "id_type";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_ICON = "iconName";
    private static final String PARAMETER_REGEX_PATTERN = "regex_pattern";
    private static final String PARAMETER_REGEX_ERROR_MESSAGE = "regex_error_message";

    // Views
    private static final String VIEW_MANAGE_ENTRY_TYPE = "manageEntryType";
    private static final String VIEW_EDIT_ENTRY_TYPE = "editEntryType";
    private static final String VIEW_CREATE_ENTRY_TYPE = "createEntryType";
    private static final String VIEW_CONFIRM_REMOVE_ENTRY_TYPE = "confirmRemoveEntryType";

    // Templates
    private static final String TEMPLATE_MANAGE_ENTRY_TYPE = "/admin/plugins/genericattributes/manage_entry_type.html";
    private static final String TEMPLATE_EDIT_ENTRY_TYPE = "/admin/plugins/genericattributes/modify_entry_type.html";
    private static final String TEMPLATE_CREATE_ENTRY_TYPE = "/admin/plugins/genericattributes/create_entry_type.html";

    // Marks
    private static final String MARK_ENTRY_TYPE_LIST = "entryTypeList";
    private static final String MARK_ENTRY_TYPE = "entryType";
    private static final String MARK_ENTRY_TYPE_PARENT = "entryTypeParent";
    private static final String MARK_ENTRY_TYPE_TITLES = "entryTypeTitles";
    private static final String MARK_REGEX_SUPPORTED_TYPES = "regexSupportedTypes";
    private static final String MARK_MAX_LENGTH_TITLE = "maxLengthTitle";
    private static final String MARK_MAX_LENGTH_REGEX_PATTERN = "maxLengthRegexPattern";
    private static final String MARK_MAX_LENGTH_REGEX_ERROR_MESSAGE = "maxLengthRegexErrorMessage";

    private static final String METHOD_POST = "POST";

    // Messages
    private static final String MESSAGE_ERROR_REGEX_NOT_SUPPORTED = "genericattributes.manageEntryType.message.error.regexNotSupported";
    private static final String MESSAGE_CONFIRM_REMOVE_ENTRY_TYPE = "genericattributes.manageEntryType.message.confirmRemove";
    private static final String MESSAGE_ERROR_REMOVE_USED = "genericattributes.manageEntryType.message.error.removeUsed";
    private static final String MESSAGE_ERROR_REMOVE_DERIVED = "genericattributes.manageEntryType.message.error.removeHasDerived";
    private static final String INFO_ENTRY_TYPE_CREATED = "genericattributes.manageEntryType.message.info.created";
    private static final String INFO_ENTRY_TYPE_REMOVED = "genericattributes.manageEntryType.message.info.removed";

    // Actions
    private static final String ACTION_CHANGE_ACTIVE = "doChangeActive";
    private static final String ACTION_MOVE_UP = "doMoveUp";
    private static final String ACTION_MOVE_DOWN = "doMoveDown";
    private static final String ACTION_DO_EDIT = "modifyEntryType";
    private static final String ACTION_CREATE_ENTRY_TYPE = "createEntryType";
    private static final String ACTION_REMOVE_ENTRY_TYPE = "removeEntryType";

    @Inject
    private Models _model;

    @Inject
    private DerivedEntryTypeService _derivedEntryTypeService;

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
        List<EntryType> listEntryType = EntryTypeHome.getCompleteList( );
        Map<String, String> mapTitles = new HashMap<>( );
        List<Integer> listRegexSupportedTypes = new ArrayList<>( );

        for ( EntryType entryType : listEntryType )
        {
            mapTitles.put( String.valueOf( entryType.getIdType( ) ), entryType.getTitle( ) );

            if ( _derivedEntryTypeService.isRegularExpressionSupported( entryType ) )
            {
                listRegexSupportedTypes.add( entryType.getIdType( ) );
            }
        }

        _model.put( MARK_ENTRY_TYPE_LIST, listEntryType );
        _model.put( MARK_ENTRY_TYPE_TITLES, mapTitles );
        _model.put( MARK_REGEX_SUPPORTED_TYPES, listRegexSupportedTypes );

        return getPage( TEMPLATE_MANAGE_ENTRY_TYPE );
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

    @View( value = VIEW_EDIT_ENTRY_TYPE, securityTokenAction = ACTION_DO_EDIT )
    public String getEditEntryType( HttpServletRequest request )
    {
        int idType = NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 );
        EntryType entryType = EntryTypeHome.findByPrimaryKey( idType );

        if ( entryType == null )
        {
            return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
        }

        return getEditPage( entryType );
    }

    private String getEditPage( EntryType entryType )
    {
        _model.put( MARK_ENTRY_TYPE, entryType );
        if ( entryType.isDerived( ) )
        {
            _model.put( MARK_ENTRY_TYPE_PARENT, EntryTypeHome.findByPrimaryKey( entryType.getIdTypeParent( ) ) );
        }
        putMaxLengths( );

        return getPage( TEMPLATE_EDIT_ENTRY_TYPE );
    }

    @Action( ACTION_DO_EDIT )
    public String doEdit( HttpServletRequest request )
    {
        if ( !isPost( request ) )
        {
            return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
        }

        int idType = NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 );
        EntryType entryType = EntryTypeHome.findByPrimaryKey( idType );

        if ( entryType != null )
        {
            entryType.setTitle( request.getParameter( PARAMETER_TITLE ) );
            entryType.setIconName( request.getParameter( PARAMETER_ICON ) );

            if ( entryType.isDerived( ) )
            {
                entryType.setRegexPattern( request.getParameter( PARAMETER_REGEX_PATTERN ) );
                entryType.setRegexErrorMessage( StringUtils.trimToNull( request.getParameter( PARAMETER_REGEX_ERROR_MESSAGE ) ) );
            }

            if ( !validate( entryType ) )
            {
                return getEditPage( entryType );
            }

            EntryTypeHome.update( entryType );
        }

        return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
    }

    /**
     * Build the view creating an entry type derived from an existing one: the new entry type uses the same entry type service, and adds a regular
     * expression control
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_CREATE_ENTRY_TYPE )
    public String getCreateEntryType( HttpServletRequest request )
    {
        EntryType entryTypeParent = EntryTypeHome.findByPrimaryKey( NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 ) );

        if ( entryTypeParent == null || !_derivedEntryTypeService.isRegularExpressionSupported( entryTypeParent ) )
        {
            return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
        }

        return getCreatePage( entryTypeParent, _derivedEntryTypeService.newDerivedEntryType( entryTypeParent, null, entryTypeParent.getIconName( ), null, null ) );
    }

    private String getCreatePage( EntryType entryTypeParent, EntryType entryType )
    {
        _model.put( MARK_ENTRY_TYPE_PARENT, entryTypeParent );
        _model.put( MARK_ENTRY_TYPE, entryType );
        putMaxLengths( );

        return getPage( TEMPLATE_CREATE_ENTRY_TYPE );
    }

    /**
     * Create an entry type derived from an existing one
     * 
     * @param request
     *            The HTTP request
     * @return The redirect URL
     */
    @Action( ACTION_CREATE_ENTRY_TYPE )
    public String doCreateEntryType( HttpServletRequest request )
    {
        if ( !isPost( request ) )
        {
            return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
        }

        int idTypeParent = NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 );
        EntryType entryTypeParent = EntryTypeHome.findByPrimaryKey( idTypeParent );

        if ( entryTypeParent == null )
        {
            return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
        }

        if ( !_derivedEntryTypeService.isRegularExpressionSupported( entryTypeParent ) )
        {
            addError( MESSAGE_ERROR_REGEX_NOT_SUPPORTED, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
        }

        EntryType entryType = _derivedEntryTypeService.newDerivedEntryType( entryTypeParent, request.getParameter( PARAMETER_TITLE ),
                request.getParameter( PARAMETER_ICON ), request.getParameter( PARAMETER_REGEX_PATTERN ), request.getParameter( PARAMETER_REGEX_ERROR_MESSAGE ) );

        if ( !validate( entryType ) )
        {
            return getCreatePage( entryTypeParent, entryType );
        }

        _derivedEntryTypeService.create( entryType );
        addInfo( INFO_ENTRY_TYPE_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
    }

    /**
     * Ask the confirmation of the removal of a derived entry type, or explain why it cannot be removed
     * 
     * @param request
     *            The HTTP request
     * @return The URL of the confirmation message
     */
    @View( value = VIEW_CONFIRM_REMOVE_ENTRY_TYPE, securityTokenAction = ACTION_REMOVE_ENTRY_TYPE )
    public String getConfirmRemoveEntryType( HttpServletRequest request )
    {
        EntryType entryType = EntryTypeHome.findByPrimaryKey( NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 ) );

        if ( entryType == null || !entryType.isDerived( ) )
        {
            return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
        }

        String strRemoveError = getRemoveError( entryType );

        if ( strRemoveError != null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, strRemoveError, new Object [ ] {
                    entryType.getTitle( ), EntryTypeHome.countEntries( entryType.getIdType( ) )
            }, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_ENTRY_TYPE ) );
        url.addParameter( PARAMETER_ID, entryType.getIdType( ) );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ENTRY_TYPE, new Object [ ] {
                entryType.getTitle( )
        }, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Remove a derived entry type no entry uses
     * 
     * @param request
     *            The HTTP request
     * @return The redirect URL
     */
    @Action( ACTION_REMOVE_ENTRY_TYPE )
    public String doRemoveEntryType( HttpServletRequest request )
    {
        if ( !isPost( request ) )
        {
            return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
        }

        EntryType entryType = EntryTypeHome.findByPrimaryKey( NumberUtils.toInt( request.getParameter( PARAMETER_ID ), -1 ) );

        if ( entryType != null && entryType.isDerived( ) && getRemoveError( entryType ) == null )
        {
            EntryTypeHome.remove( entryType.getIdType( ) );
            reorder( entryType.getPlugin( ) );
            addInfo( INFO_ENTRY_TYPE_REMOVED, getLocale( ) );
        }

        return redirectView( request, VIEW_MANAGE_ENTRY_TYPE );
    }

    /**
     * Get the reason why an entry type cannot be removed
     * 
     * @param entryType
     *            the entry type
     * @return the key of the error message, or null if the entry type can be removed
     */
    private String getRemoveError( EntryType entryType )
    {
        if ( EntryTypeHome.countEntries( entryType.getIdType( ) ) > 0 )
        {
            return MESSAGE_ERROR_REMOVE_USED;
        }
        if ( EntryTypeHome.countDerivedEntryTypes( entryType.getIdType( ) ) > 0 )
        {
            return MESSAGE_ERROR_REMOVE_DERIVED;
        }
        return null;
    }

    /**
     * Renumber the display order of the entry types of a plugin after a removal, so that the move up and down actions keep working
     * 
     * @param strPlugin
     *            the plugin owning the entry types
     */
    private void reorder( String strPlugin )
    {
        int nOrder = 1;

        for ( EntryType entryType : EntryTypeHome.getList( strPlugin ) )
        {
            if ( entryType.getOrder( ) != nOrder )
            {
                entryType.setOrder( nOrder );
                EntryTypeHome.update( entryType );
            }
            nOrder++;
        }
    }

    /**
     * Validate an entry type before saving it, and register the errors to display
     * 
     * @param entryType
     *            the entry type
     * @return true if the entry type is valid
     */
    private boolean validate( EntryType entryType )
    {
        List<ValidationError> listErrors = _derivedEntryTypeService.validate( entryType );

        for ( ValidationError error : listErrors )
        {
            addError( I18nService.getLocalizedString( error.messageKey( ), new String [ ] {
                    error.argument( )
            }, getLocale( ) ) );
        }

        return listErrors.isEmpty( );
    }

    private void putMaxLengths( )
    {
        _model.put( MARK_MAX_LENGTH_TITLE, DerivedEntryTypeService.MAX_LENGTH_TITLE );
        _model.put( MARK_MAX_LENGTH_REGEX_PATTERN, DerivedEntryTypeService.MAX_LENGTH_REGEX_PATTERN );
        _model.put( MARK_MAX_LENGTH_REGEX_ERROR_MESSAGE, DerivedEntryTypeService.MAX_LENGTH_REGEX_ERROR_MESSAGE );
    }

    /**
     * The security token protects the POST requests only: an action changing data must refuse the other methods, or a link would bypass the token
     * 
     * @param request
     *            The HTTP request
     * @return true if the request is a POST
     */
    private static boolean isPost( HttpServletRequest request )
    {
        return METHOD_POST.equalsIgnoreCase( request.getMethod( ) );
    }
}
