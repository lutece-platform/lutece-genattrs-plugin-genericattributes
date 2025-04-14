/*
 * Copyright (c) 2002-2025, City of Paris
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

import fr.paris.lutece.portal.business.event.IEventParam;

public class EntryEvent 
{
	private String _strId;
    private String _strResourceType;
    private IEventParam<?> _param;
    
    /**
     * default constructor
     */
    public EntryEvent( )
    {
    }

    /**
     * Constructor with fields
     * 
     * @param strId
     *            the identifier
     * @param strResourceType
     *            the resource type 
     */
    public EntryEvent( String strId, String strResourceType )
    {
    	_strId = strId;
    	_strResourceType = strResourceType;
    }
    
    /**
     * Constructor with fields
     * 
     * @param strId
     *            the identifier
     * @param strResourceType
     *            the resource type
     * @param param
     *            the param 
     */
    public EntryEvent( String strId, String strResourceType, IEventParam<?> param )
    {
    	_strId = strId;
    	_strResourceType = strResourceType;
        _param = param;
    }

    /**
     * Gets the identifier
     * 
     * @return the identifier
     */
    public String getId( )
    {
        return _strId;
    }

    /**
     * Sets the identifier
     * 
     * @param strId
     *            the identifier
     */
    public void setId( String strId )
    {
        _strId = strId;
    }

    /**
     * Gets the resource type
     * 
     * @return the resource type 
     */
    public String getResourceType( )
    {
        return _strResourceType;
    }

    /**
     * Sets the resource type
     * 
     * @param strType
     *            the resource type 
     */
    public void setResourceType( String strResourceType )
    {
    	_strResourceType = strResourceType;
    }
    
    /**
     * Sets the event Param.
     * 
     * @param param
     */
    public void setParam( IEventParam<?> param )
    {
        _param = param;
    }

    /**
     * Gets the event param
     * 
     * @return the param
     */
    public IEventParam<?> getParam( )
    {
        return _param;
    }
}
