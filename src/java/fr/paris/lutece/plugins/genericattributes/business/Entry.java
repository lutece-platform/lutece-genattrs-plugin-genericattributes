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
package fr.paris.lutece.plugins.genericattributes.business;

import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.Serializable;

import java.util.List;

/**
 *
 * class Entry
 *
 */
public class Entry implements Serializable, Cloneable
{
    private static final long serialVersionUID = 7623715927165156626L;

    // Other constants
    private int _nIdEntry;
    private String _strTitle;
    private String _strCode;
    private String _strHelpMessage;
    private String _strComment;
    private boolean _bMandatory;
    private boolean _bFieldInLine;
    private int _nPosition;
    private int _nIdResource;
    private String _strResourceType;
    private EntryType _entryType;
    private Entry _entryParent;
    private List<Entry> _listEntryChildren;
    private List<Field> _listFields;
    private Field _fieldDepend;
    private int _nNumberConditionalQuestion;
    private boolean _bUnique;
    private GenericAttributeError _error;
    private String _strCSSClass;
    private String _strErrorMessage;
    private boolean _bOnlyDisplayInBack;
    private boolean _bIndexed;

    /**
     * Get the list of children of this entry
     * 
     * @return the list of entry who are insert in the group
     */
    public List<Entry> getChildren( )
    {
        return _listEntryChildren;
    }

    /**
     * Get the comment of this entry
     * 
     * @return the entry comment
     */
    public String getComment( )
    {
        return _strComment;
    }

    /**
     * Get the type of the entry
     * 
     * @return the type of the entry
     */
    public EntryType getEntryType( )
    {
        return _entryType;
    }

    /**
     * Get the list of fields of this entry
     * 
     * @return the list of field who are associate to the entry
     */
    public List<Field> getFields( )
    {
        return _listFields;
    }

    /**
     * Get the help message of this entry
     * 
     * @return The help message of this entry
     */
    public String getHelpMessage( )
    {
        return _strHelpMessage;
    }

    /**
     * Get the id of this entry
     * 
     * @return the id of entry
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * @return parent entry if the entry is insert in a group
     */
    public Entry getParent( )
    {
        return _entryParent;
    }

    /**
     * Get the position of the entry
     * 
     * @return position entry
     */
    public int getPosition( )
    {
        return _nPosition;
    }

    /**
     * Get the title of this entry
     * 
     * @return The title of this entry
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Check if generated fields must be displayed in line
     * 
     * @return true if the field associate must be display in line
     */
    public boolean isFieldInLine( )
    {
        return _bFieldInLine;
    }

    /**
     * Check if this entry is mandatory or not
     * 
     * @return true if the question is mandatory
     */
    public boolean isMandatory( )
    {
        return _bMandatory;
    }

    /**
     * Set the list of entry who are insert in the group
     * 
     * @param children
     *            the list of entry
     */
    public void setChildren( List<Entry> children )
    {
        _listEntryChildren = children;
    }

    /**
     * Set entry comment
     * 
     * @param strComment
     *            entry comment
     */
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }

    /**
     * Set the type of the entry
     * 
     * @param entryType
     *            the type of the entry
     */
    public void setEntryType( EntryType entryType )
    {
        _entryType = entryType;
    }

    /**
     * Set true if the field associate must be display in line
     * 
     * @param bFieldInLine
     *            true if the field associate must be display in line
     */
    public void setFieldInLine( boolean bFieldInLine )
    {
        _bFieldInLine = bFieldInLine;
    }

    /**
     * Set the list of field who are associate to the entry
     * 
     * @param fields
     *            the list of field
     */
    public void setFields( List<Field> fields )
    {
        _listFields = fields;
    }

    /**
     * Set the entry help message
     * 
     * @param strHelpMessage
     *            the entry help message
     */
    public void setHelpMessage( String strHelpMessage )
    {
        _strHelpMessage = strHelpMessage;
    }

    /**
     * Set the id of the entry
     * 
     * @param nIdEntry
     *            the id of the entry
     */
    public void setIdEntry( int nIdEntry )
    {
        _nIdEntry = nIdEntry;
    }

    /**
     * Set true if the question is mandatory
     * 
     * @param bMandatory
     *            true if the question is mandatory
     */
    public void setMandatory( boolean bMandatory )
    {
        _bMandatory = bMandatory;
    }

    /**
     * Set parent entry if the entry is insert in a group
     * 
     * @param parent
     *            parent entry
     */
    public void setParent( Entry parent )
    {
        _entryParent = parent;
    }

    /**
     * Set position entry
     * 
     * @param position
     *            position entry
     */
    public void setPosition( int position )
    {
        _nPosition = position;
    }

    /**
     * Set title entry
     * 
     * @param strTitle
     *            title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Get the id of the resource associated with this entry
     * 
     * @return The id of the resource associated with this entry
     */
    public int getIdResource( )
    {
        return _nIdResource;
    }

    /**
     * Set the id of the resource associated with this entry
     * 
     * @param nIdResource
     *            The id of the resource associated with this entry
     */
    public void setIdResource( int nIdResource )
    {
        this._nIdResource = nIdResource;
    }

    /**
     * Get the type of the resource associated with this entry
     * 
     * @return The type of the resource associated with this entry
     */
    public String getResourceType( )
    {
        return _strResourceType;
    }

    /**
     * Set the type of the resource associated with this entry
     * 
     * @param strResourceType
     *            The type of the resource associated with this entry
     */
    public void setResourceType( String strResourceType )
    {
        this._strResourceType = strResourceType;
    }

    /**
     * Get the field depend of this entry
     * 
     * @return the field if the entry is a conditional question
     */
    public Field getFieldDepend( )
    {
        return _fieldDepend;
    }

    /**
     * Set the field if the entry is a conditional question
     * 
     * @param depend
     *            depend the field if the entry is a conditional question
     */
    public void setFieldDepend( Field depend )
    {
        _fieldDepend = depend;
    }

    /**
     * Get the number of conditional questions associated with the entry
     * 
     * @return the number of conditional questions associated with the entry
     */
    public int getNumberConditionalQuestion( )
    {
        return _nNumberConditionalQuestion;
    }

    /**
     * Set the number of conditional questions who are associated with the entry
     * 
     * @param numberConditionalQuestion
     *            the number of conditional questions which are associated with the entry
     *
     */
    public void setNumberConditionalQuestion( int numberConditionalQuestion )
    {
        _nNumberConditionalQuestion = numberConditionalQuestion;
    }

    /**
     * Set to true if the value of the response to this question must be unique
     * 
     * @param bUnique
     *            true if the value of the response to this question must be unique, false otherwise
     */
    public void setUnique( boolean bUnique )
    {
        this._bUnique = bUnique;
    }

    /**
     * Check if the value of the response must be unique
     * 
     * @return true if the value of the response to this question must be unique
     */
    public boolean isUnique( )
    {
        return _bUnique;
    }

    /**
     * Get the error associated to the entry
     * 
     * @return the error
     */
    public GenericAttributeError getError( )
    {
        return _error;
    }

    /**
     * Set the error associated to the entry
     * 
     * @param error
     *            the error
     */
    public void setError( GenericAttributeError error )
    {
        _error = error;
    }

    /**
     * Set the CSS class of the generated fields
     * 
     * @param strCSSClass
     *            The CSS class to set
     */
    public void setCSSClass( String strCSSClass )
    {
        this._strCSSClass = strCSSClass;
    }

    /**
     * Get the CSS class of the generated fields
     * 
     * @return The CSS class
     */
    public String getCSSClass( )
    {
        return _strCSSClass;
    }

    /**
     * Get the error message associated with this entry. This error message should be used by the right entry type. For example, EntryTypeCheckBox use it as a
     * message to indicates that this field is mandatory.
     * 
     * @return The error message of this entry
     */
    public String getErrorMessage( )
    {
        return _strErrorMessage;
    }

    /**
     * Set the error message associated with this entry. This error message should be used by the right entry type. For example, EntryTypeCheckBox use it as a
     * message to indicates that this field is mandatory.
     * 
     * @param strErrorMessage
     *            The error message of this entry
     */
    public void setErrorMessage( String strErrorMessage )
    {
        this._strErrorMessage = strErrorMessage;
    }

    /**
     * @return the _strCode
     */
    public String getCode( )
    {
        return _strCode;
    }

    /**
     * @param strCode
     *            the code to set
     */
    public void setCode( String strCode )
    {
        this._strCode = strCode;
    }

    /**
     * Creates and returns a copy of this object.
     * 
     * @return a clone of this instance.
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone( )
    {
        try
        {
            return super.clone( );
        }
        catch( CloneNotSupportedException e )
        {
            AppLogService.error( e.getMessage( ), e );

            return new Entry( );
        }
    }

    public boolean isOnlyDisplayInBack( )
    {
        return _bOnlyDisplayInBack;
    }

    public void setOnlyDisplayInBack( boolean onlyDisplayInBack )
    {
        this._bOnlyDisplayInBack = onlyDisplayInBack;
    }

    public boolean isIndexed( )
    {
        return _bIndexed;
    }

    public void setIndexed( boolean bIndexed )
    {
        _bIndexed = bIndexed;
    }

    /**
     * @return true if the field exportable is present and set to true
     */
    public boolean isExportable( )
    {
        Field fieldExportable = getFieldByCode( IEntryTypeService.FIELD_EXPORTABLE );
        return fieldExportable != null && Boolean.valueOf( fieldExportable.getValue( ) );
    }

    /**
     * @return true if the field exportable_pdf is present and set to true
     */
    public boolean isExportablePdf( )
    {
        Field fieldExportablePdf = getFieldByCode( IEntryTypeService.FIELD_EXPORTABLE_PDF );
        return fieldExportablePdf != null && Boolean.valueOf( fieldExportablePdf.getValue( ) );
    }

    /**
     * @return true if the field published is present and set to true
     */
    public boolean isPublished( )
    {
        Field fieldPublished = getFieldByCode( IEntryTypeService.FIELD_PUBLISHED );
        return fieldPublished != null && Boolean.valueOf( fieldPublished.getValue( ) );
    }

    /**
     * @return true if the field disabled is present and set to true
     */
    public boolean isDisabled( )
    {
        Field fieldDisabled = getFieldByCode( IEntryTypeService.FIELD_DISABLED );
        return fieldDisabled != null && Boolean.valueOf( fieldDisabled.getValue( ) );
    }

    /**
     * Get the selected map provider
     * 
     * @see IMapProvider
     * @return the select map provider
     */
    public IMapProvider getMapProvider( )
    {
        Field fieldProvider = getFieldByCode( IEntryTypeService.FIELD_PROVIDER );
        if ( fieldProvider == null )
        {
            return null;
        }
        return MapProviderManager.getMapProvider( fieldProvider.getValue( ) );
    }

    /**
     * Get the field by its code.
     * 
     * @param strCode
     * @return
     */
    public Field getFieldByCode( String strCode )
    {
        if ( _listFields == null || _listFields.isEmpty( ) )
        {
            return null;
        }

        return _listFields.stream( ).filter( field -> field.getCode( ).equals( strCode ) ).findFirst( ).orElse( null );
    }
}
