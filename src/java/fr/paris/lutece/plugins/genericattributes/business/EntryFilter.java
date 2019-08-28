/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.genericattributes.business;

import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;

import org.apache.commons.lang.StringUtils;

/**
 *
 * class EntryFilter
 *
 */
public class EntryFilter
{
    /**
     * Value for boolean filters to represent Boolean.FALSE
     */
    public static final int FILTER_FALSE = 0;

    /**
     * Value for boolean filters to represent Boolean.TRUE
     */
    public static final int FILTER_TRUE = 1;
    private int _nIdResource = GenericAttributesUtils.CONSTANT_ID_NULL;
    private String _strResourceType;
    private int _nIdFieldDepend = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIdEntryParent = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nEntryParentNull = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nFieldDependNull = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIdIsGroup = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIdIsComment = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIdEntryType = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIsOnlyDisplayInBack = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIsEditableBack = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIsIndexed = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIsUsedInCorrectFormResponse = GenericAttributesUtils.CONSTANT_ID_NULL;

    /**
     * Get the id of the resource in the filter
     * 
     * @return The id of resource insert in the filter
     */
    public int getIdResource( )
    {
        return _nIdResource;
    }

    /**
     * Set the id of resource in the filter
     * 
     * @param nIdResource
     *            The id of resource to insert in the filter
     */
    public void setIdResource( int nIdResource )
    {
        _nIdResource = nIdResource;
    }

    /**
     * Check if this filter contains a resource id
     * 
     * @return true if the filter contain an id of resource
     */
    public boolean containsIdResource( )
    {
        return ( _nIdResource != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return the id of field insert in the filter
     */
    public int getIdFieldDepend( )
    {
        return _nIdFieldDepend;
    }

    /**
     * Set the id of field depend in the filter
     * 
     * @param idField
     *            the id of field depend to insert in the filter
     */
    public void setIdFieldDepend( int idField )
    {
        _nIdFieldDepend = idField;
    }

    /**
     * Check if this filter contains a field id
     * 
     * @return true if the filter contain an id of field depend
     */
    public boolean containsIdField( )
    {
        return ( _nIdFieldDepend != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return the id of parent entry insert in the filter
     */
    public int getIdEntryParent( )
    {
        return _nIdEntryParent;
    }

    /**
     * set the id of parent entry
     * 
     * @param idEntryParent
     *            the id of parent entry to insert in the filter
     */
    public void setIdEntryParent( int idEntryParent )
    {
        _nIdEntryParent = idEntryParent;
    }

    /**
     *
     * @return true if the filter contain an parent id
     */
    public boolean containsIdEntryParent( )
    {
        return ( _nIdEntryParent != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return 1 if the id of parent entry must be null
     */
    public int getEntryParentNull( )
    {
        return _nEntryParentNull;
    }

    /**
     * set 1 if the id of parent entry must be null
     * 
     * @param idEntryParentNull
     *            1 if the id of parent entry must be null
     */
    public void setEntryParentNull( int idEntryParentNull )
    {
        _nEntryParentNull = idEntryParentNull;
    }

    /**
     *
     * @return true if the parent entry must be null
     */
    public boolean containsEntryParentNull( )
    {
        return ( _nEntryParentNull != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return 1 if the id of field depend must be null
     */
    public int getFieldDependNull( )
    {
        return _nFieldDependNull;
    }

    /**
     * set 1 if the id of field depend must be null
     * 
     * @param idFieldDependNull
     *            1 if the id of field depend must be null
     */
    public void setFieldDependNull( int idFieldDependNull )
    {
        _nFieldDependNull = idFieldDependNull;
    }

    /**
     *
     * @return true if the id of field depend must be null
     */
    public boolean containsFieldDependNull( )
    {
        return ( _nFieldDependNull != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return 1 if the entry is a group,0 if the entry is not a group
     */
    public int getIdIsGroup( )
    {
        return _nIdIsGroup;
    }

    /**
     * set 1 if the entry must be a group,0 if the entry must not be a group
     * 
     * @param idIsGroup
     *            1 if the entry must be a group,0 if the entry must not be a group
     */
    public void setIdIsGroup( int idIsGroup )
    {
        _nIdIsGroup = idIsGroup;
    }

    /**
     *
     * @return true if the entry must be a group or must not be a group
     */
    public boolean containsIdIsGroup( )
    {
        return ( _nIdIsGroup != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return 1 if the entry must be a comment,0 if the entry must not be a comment
     */
    public int getIdIsComment( )
    {
        return _nIdIsComment;
    }

    /**
     * set 1 if the entry must be a comment,0 if the entry must not be a comment
     * 
     * @param idComment
     *            1 if the entry must be a comment,0 if the entry must not be a comment
     */
    public void setIdIsComment( int idComment )
    {
        _nIdIsComment = idComment;
    }

    /**
     *
     * @return true if the entry must be a comment or must not be a comment
     */
    public boolean containsIdIsComment( )
    {
        return ( _nIdIsComment != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     * Get the id entry type
     * 
     * @return the id of entry type insert in the filter
     */
    public int getIdEntryType( )
    {
        return _nIdEntryType;
    }

    /**
     * set the id of entry type
     * 
     * @param nIdEntryType
     *            the id of entry type to insert in the filter
     */
    public void setIdEntryType( int nIdEntryType )
    {
        _nIdEntryType = nIdEntryType;
    }

    /**
     * Check if the filter contains the id entry type
     * 
     * @return true if the filter contains an id entry type
     */
    public boolean containsIdEntryType( )
    {
        return ( _nIdEntryType != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     * Get the resource type of the filter
     * 
     * @return The resource type of the filter
     */
    public String getResourceType( )
    {
        return _strResourceType;
    }

    /**
     * Set the result type of the filter
     * 
     * @param strResourceType
     *            The resource type of the filter
     */
    public void setResourceType( String strResourceType )
    {
        this._strResourceType = strResourceType;
    }

    /**
     * Check if the filter contains the resource type
     * 
     * @return True if the filter contains the resource type, false otherwise
     */
    public boolean containsResourceType( )
    {
        return StringUtils.isNotEmpty( _strResourceType );
    }

    /**
     *
     * @return 1 if the entry must only display in back a comment,0 if the entry must not only display in back
     */
    public int getIsOnlyDisplayInBack( )
    {
        return _nIsOnlyDisplayInBack;
    }

    /**
     *
     * @param _nIsOnlyDisplayInBack
     */
    public void setIsOnlyDisplayInBack( int _nIsOnlyDisplayInBack )
    {
        this._nIsOnlyDisplayInBack = _nIsOnlyDisplayInBack;
    }

    /**
     *
     * @return true if the entry must be only display in back or must not be only display in back
     */
    public boolean containsIsOnlyDisplayInBack( )
    {
        return ( _nIsOnlyDisplayInBack != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return 1 if the entry must be editable back,0 if the entry must not be editable back
     */
    public int getIsEditableBack( )
    {
        return _nIsEditableBack;
    }

    /**
     *
     * @param _nIsEditableBack
     */
    public void setIsEditableBack( int _nIsEditableBack )
    {
        this._nIsEditableBack = _nIsEditableBack;
    }

    /**
     *
     * @return true if the entry must be editable back or must not be editable back
     */
    public boolean containsIsEditableBack( )
    {
        return ( _nIsEditableBack != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    public int getIsIndexed( )
    {
        return _nIsIndexed;
    }

    public void setIsIndexed( int nIsIndexed )
    {
        _nIsIndexed = nIsIndexed;
    }

    public boolean containsIsIndexed( )
    {
        return ( _nIsIndexed != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    public int getIsUsedInCorrectFormResponse( )
    {
        return _nIsUsedInCorrectFormResponse;
    }

    public void setIsUsedInCorrectFormResponse( int nIsUsedInCorrectFormResponse )
    {
        _nIsUsedInCorrectFormResponse = nIsUsedInCorrectFormResponse;
    }

    public boolean containsIsUsedInCorrectFormResponse( )
    {
        return _nIsUsedInCorrectFormResponse != GenericAttributesUtils.CONSTANT_ID_NULL;
    }
}
