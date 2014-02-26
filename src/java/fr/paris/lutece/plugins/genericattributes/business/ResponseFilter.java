/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;

import java.util.List;


/**
 *
 * class ResponseFilter
 *
 */
public class ResponseFilter
{
    private int _nIdResource = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIdField = GenericAttributesUtils.CONSTANT_ID_NULL;
    private int _nIdEntry = GenericAttributesUtils.CONSTANT_ID_NULL;
    private Timestamp _tDateFirst;
    private Timestamp _tDateLast;
    private boolean _bGroupbyDay;
    private boolean _bGroupbyWeek;
    private boolean _bGroupbyMonth;
    private String _strOrderBy;
    private boolean _bIsOrderByAsc = true;
    private List<Integer> _listId;

    /**
     * Get the id of a resource in the filter
     * @return The id of the resource to insert in the filter
     */
    public int getIdResource(  )
    {
        return _nIdResource;
    }

    /**
     * Set the id of a resource in the filter
     * @param nIdResource the id of resource to insert in the filter
     */
    public void setIdResource( int nIdResource )
    {
        _nIdResource = nIdResource;
    }

    /**
     *
     * @return true if the filter contain an id of a resource
     */
    public boolean containsIdResource(  )
    {
        return ( _nIdResource != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return the id of field insert in the filter
     */
    public int getIdField(  )
    {
        return _nIdField;
    }

    /**
     * set the id of field depend in the filter
     * @param idField the id of field depend to insert in the filter
     */
    public void setIdField( int idField )
    {
        _nIdField = idField;
    }

    /**
     *
     * @return true if the filter contain an id of field depend
     */
    public boolean containsIdField(  )
    {
        return ( _nIdField != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return the id of entry insert in the filter
     */
    public int getIdEntry(  )
    {
        return _nIdEntry;
    }

    /**
     * set the id of entry depend in the filter
     * @param idEntry the id of entry depend to insert in the filter
     */
    public void setIdEntry( int idEntry )
    {
        _nIdEntry = idEntry;
    }

    /**
     *
     * @return true if the filter contain an id of entry depend
     */
    public boolean containsIdEntry(  )
    {
        return ( _nIdEntry != GenericAttributesUtils.CONSTANT_ID_NULL );
    }

    /**
     *
     * @return date of the first submit
     */
    public Timestamp getDateFirst(  )
    {
        return _tDateFirst;
    }

    /**
     * set the date of the first submit
     * @param begin date of the first submit
     */
    public void setDateFirst( Timestamp begin )
    {
        _tDateFirst = begin;
    }

    /**
     *
     * @return true if the filter contain the date of the first submit
     */
    public boolean containsDateFirst(  )
    {
        return ( _tDateFirst != null );
    }

    /**
     *
     * @return date of the last submit
     */
    public Timestamp getDateLast(  )
    {
        return _tDateLast;
    }

    /**
     * set the date of the last submit
     * @param end the date of the last submit
     */
    public void setDateLast( Timestamp end )
    {
        _tDateLast = end;
    }

    /**
     *
     * @return true if the filter contain the date of the last submit
     */
    public boolean containsDateLast(  )
    {
        return ( _tDateLast != null );
    }

    /**
     *
     * @return true if the response must be group by day
     */
    public boolean isGroupbyDay(  )
    {
        return _bGroupbyDay;
    }

    /**
     * set true if the response must be group by day
     * @param groupbyDay true if the response must be group by day
     */
    public void setGroupbyDay( boolean groupbyDay )
    {
        _bGroupbyDay = groupbyDay;
    }

    /**
     * true if the response must be group by month
     * @return true if the response must be group by month
     */
    public boolean isGroupbyMonth(  )
    {
        return _bGroupbyMonth;
    }

    /**
     * set true if the response must be group by month
     * @param groupbyMonth true if the response must be group by month
     */
    public void setGroupbyMonth( boolean groupbyMonth )
    {
        _bGroupbyMonth = groupbyMonth;
    }

    /**
     * true if the response must be group by week
     * @return true if the response must be group by week
     */
    public boolean isGroupbyWeek(  )
    {
        return _bGroupbyWeek;
    }

    /**
     * set true if the response must be group by week
     * @param groupbyWeek true if the response must be group by week
     */
    public void setGroupbyWeek( boolean groupbyWeek )
    {
        _bGroupbyWeek = groupbyWeek;
    }

    /**
     * Set order by
     * @param strOrderBy The order by
     */
    public void setOrderBy( String strOrderBy )
    {
        _strOrderBy = strOrderBy;
    }

    /**
     * Get order by
     * @return the order by
     */
    public String getOrderBy(  )
    {
        return _strOrderBy;
    }

    /**
     * Check if the filter contains order by
     * @return true if it contains, false otherwise
     */
    public boolean containsOrderBy(  )
    {
        return StringUtils.isNotBlank( _strOrderBy );
    }

    /**
     * Set order by asc
     * @param bIsOrderByAsc true if the order by is asc
     */
    public void setOrderByAsc( boolean bIsOrderByAsc )
    {
        _bIsOrderByAsc = bIsOrderByAsc;
    }

    /**
     * Check if the order by is asc
     * @return true if the order by is asc;
     */
    public boolean isOrderByAsc(  )
    {
        return _bIsOrderByAsc;
    }

    /**
     * @return the _listId
     */
    public List<Integer> getListId(  )
    {
        return this._listId;
    }

    /**
     * @param listId the _listId to set
     */
    public void setListId( List<Integer> listId )
    {
        this._listId = listId;
    }

    /**
     *
     * @return true if the filter contain an id of entry depend
     */
    public boolean containsListIdResource(  )
    {
        return CollectionUtils.isNotEmpty( _listId );
    }
}
