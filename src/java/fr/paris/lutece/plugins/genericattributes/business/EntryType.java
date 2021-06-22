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
package fr.paris.lutece.plugins.genericattributes.business;

import java.io.Serializable;

/**
 *
 * class entryType
 *
 */
public class EntryType implements Serializable
{
    private static final long serialVersionUID = 2528514010464635136L;
    private int _nIdType;
    private String _strTitle;
    private String _strBeanName;
    private String _strIconName;
    private Boolean _bGroup;
    private Boolean _bComment;
    private Boolean _bMyLuteceUser;
    private String _strPlugin;

    private int _nOrder;
    private boolean _bInactive;

    /**
     *
     * @return the id of the entry type
     */
    public int getIdType( )
    {
        return _nIdType;
    }

    /**
     * set the id of the entry type
     * 
     * @param idType
     *            the id of the entry type
     */
    public void setIdType( int idType )
    {
        _nIdType = idType;
    }

    /***
     *
     * @return true if the type is a group
     */
    public Boolean getGroup( )
    {
        return _bGroup;
    }

    /**
     * set true if the type is a group
     * 
     * @param isGroup
     *            if the type is a group
     */
    public void setGroup( Boolean isGroup )
    {
        _bGroup = isGroup;
    }

    /**
     *
     * @return the title of the entry type
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * set the title of the entry type
     * 
     * @param title
     *            the title of the entry type
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     * Get the name of the bean of the entry type service
     * 
     * @return The name of the bean of the entry type service
     */
    public String getBeanName( )
    {
        return _strBeanName;
    }

    /**
     * set the path for access to the Class Entry
     * 
     * @param strBeanName
     *            The name of the bean of the entry type service of this entry type
     */
    public void setBeanName( String strBeanName )
    {
        _strBeanName = strBeanName;
    }

    /**
     * Get the icon name
     * 
     * @return the strIconName
     */
    public String getIconName( )
    {
        return _strIconName;
    }

    /**
     * Set the icon name
     * 
     * @param strIconName
     *            the strIconName to set
     */
    public void setIconName( String strIconName )
    {
        _strIconName = strIconName;
    }

    /**
     *
     * @return true if the type is a comment
     */
    public Boolean getComment( )
    {
        return _bComment;
    }

    /**
     *
     * @param isComment
     *            set true if the type is a comment
     */
    public void setComment( Boolean isComment )
    {
        _bComment = isComment;
    }

    /**
     *
     * @return true if the type is MyLutece user
     */
    public Boolean getMyLuteceUser( )
    {
        return _bMyLuteceUser;
    }

    /**
     *
     * @param isMyLuteceUser
     *            set true if the type is MyLutece user
     */
    public void setMyLuteceUser( Boolean isMyLuteceUser )
    {
        _bMyLuteceUser = isMyLuteceUser;
    }

    /**
     * Get the name of the plugin associated with this entry type
     * 
     * @return The name of the plugin associated with this entry type
     */
    public String getPlugin( )
    {
        return _strPlugin;
    }

    /**
     * Set the name of the plugin associated with this entry type
     * 
     * @param strPlugin
     *            The name of the plugin associated with this entry type
     */
    public void setPlugin( String strPlugin )
    {
        this._strPlugin = strPlugin;
    }

    /**
     * @return the nOrder
     */
    public int getOrder( )
    {
        return _nOrder;
    }

    /**
     * @param nOrder
     *            the nOrder to set
     */
    public void setOrder( int nOrder )
    {
        _nOrder = nOrder;
    }

    /**
     * @return the bInactive
     */
    public boolean isInactive( )
    {
        return _bInactive;
    }

    /**
     * @param bInactive
     *            the bInactive to set
     */
    public void setInactive( boolean bInactive )
    {
        _bInactive = bInactive;
    }
}
