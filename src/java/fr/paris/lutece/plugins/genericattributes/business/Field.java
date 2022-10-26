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

import fr.paris.lutece.plugins.referencelist.business.ReferenceItem;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import java.io.Serializable;

import java.util.Date;
import java.util.List;

/**
 * class Field
 */
public class Field implements Serializable
{
    private static final long serialVersionUID = -3613999153894144425L;
    private int _nIdField;
    private Entry _parentEntry;
    private String _strCode;
    private String _strTitle;
    private String _strValue;
    private String _strComment;
    private int _nPosition;
    private boolean _bDefaultValue;
    private Date _tValueTypeDate;
    private List<Entry> _listConditionalQuestions;
    private List<RegularExpression> _listRegularExpressionList;
    private boolean _bNoDisplayTitle;
    private File _fileImage;

    private ReferenceItem _linkedItem;

    /**
     *
     * @return the id of the field
     */
    public int getIdField( )
    {
        return _nIdField;
    }

    /**
     * set the id of the field
     * 
     * @param idField
     *            the id of the field
     */
    public void setIdField( int idField )
    {
        _nIdField = idField;
    }

    /**
     *
     * @return the position of the field in the list of the entry's fields
     */
    public int getPosition( )
    {
        return _nPosition;
    }

    /**
     * set the position of the field in the list of the entry's fields
     * 
     * @param position
     *            the position of the field in the list of fields
     */
    public void setPosition( int position )
    {
        _nPosition = position;
    }

    /**
     *
     * @return the entry of the field
     */
    public Entry getParentEntry( )
    {
        return _parentEntry;
    }

    /**
     * set the entry of the field
     * 
     * @param entry
     *            the entry of the field
     */
    public void setParentEntry( Entry entry )
    {
        _parentEntry = entry;
    }

    /**
     *
     * @return a list of regular expression which is associate to the field
     */
    public List<RegularExpression> getRegularExpressionList( )
    {
        return _listRegularExpressionList;
    }

    /**
     * set a list of regular expression which is associate to the field
     * 
     * @param regularExpressionList
     *            a list of regular expression which is associate to the field
     */
    public void setRegularExpressionList( List<RegularExpression> regularExpressionList )
    {
        _listRegularExpressionList = regularExpressionList;
    }

    /**
     *
     * @return the title of the field
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * set the title of the field
     * 
     * @param title
     *            the title of the field
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     *
     * @return the value of the field
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * set the value of the field
     * 
     * @param value
     *            the value of the field
     */
    public void setValue( String value )
    {
        _strValue = value;
    }

    /**
     * Get the comment of the field
     * 
     * @return The comment of the field
     */
    public String getComment( )
    {
        return _strComment;
    }

    /**
     * Set the comment of the field
     * 
     * @param strComment
     *            The comment of the field
     */
    public void setComment( String strComment )
    {
        this._strComment = strComment;
    }

    /**
     *
     * @return a list of Entry which is associate to the field
     */
    public List<Entry> getConditionalQuestions( )
    {
        return _listConditionalQuestions;
    }

    /**
     * set a list of Entry which is associate to the field
     * 
     * @param conditionalQuestions
     *            a list of Entry which is associate to the field
     */
    public void setConditionalQuestions( List<Entry> conditionalQuestions )
    {
        _listConditionalQuestions = conditionalQuestions;
    }

    /**
     *
     * @return true if the field is a default field of the entry
     */
    public boolean isDefaultValue( )
    {
        return _bDefaultValue;
    }

    /**
     * set true if the field is a default field of the entry
     * 
     * @param defaultValue
     *            true if the field is a default field of the entry
     */
    public void setDefaultValue( boolean defaultValue )
    {
        _bDefaultValue = defaultValue;
    }

    /**
     *
     * @return the value of type Date
     */
    public Date getValueTypeDate( )
    {
        return _tValueTypeDate;
    }

    /**
     * set the value of type Date
     * 
     * @param defaultValueTypeDate
     *            the value of type Date
     */
    public void setValueTypeDate( Date defaultValueTypeDate )
    {
        _tValueTypeDate = defaultValueTypeDate;
    }

    /**
     *
     * @return true if the title of field is not display
     */
    public boolean isNoDisplayTitle( )
    {
        return _bNoDisplayTitle;
    }

    /**
     * set true if the title of field is not display
     * 
     * @param bNoDisplayTitle
     *            true if the title of field is not display
     */
    public void setNoDisplayTitle( boolean bNoDisplayTitle )
    {
        _bNoDisplayTitle = bNoDisplayTitle;
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
     * @return the linkedItem
     */
    public ReferenceItem getLinkedItem( )
    {
        return _linkedItem;
    }

    /**
     * @param linkedItem
     *            the linkedItem to set
     */
    public void setLinkedItem( ReferenceItem linkedItem )
    {
        _linkedItem = linkedItem;
    }

	/**
	 * Gets the file store key.
	 *
	 * @return the file store key
	 */
	public File getFileImage( ) {
		return _fileImage;
	}

	/**
	 * Sets the file store key.
	 *
	 * @param strFileStoreKey the new file store key
	 */
	public void setFileImage( File strFileStoreKey ) {
		this._fileImage = strFileStoreKey;
	}

}
