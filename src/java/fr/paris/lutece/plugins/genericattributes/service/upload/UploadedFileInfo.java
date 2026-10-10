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
package fr.paris.lutece.plugins.genericattributes.service.upload;

import java.io.Serializable;

import fr.paris.lutece.portal.service.upload.MultipartItem;

/**
 * Description of an uploaded file kept in the session, apart from its content: listing or counting the files reads only this
 * small attribute, so a distributed session does not write the content back
 */
public class UploadedFileInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String _strFieldName;
    private final String _strName;
    private final String _strContentType;
    private final long _lSize;

    /**
     * Constructor
     *
     * @param item
     *            the uploaded item
     */
    public UploadedFileInfo( MultipartItem item )
    {
        _strFieldName = item.getFieldName( );
        _strName = item.getName( );
        _strContentType = item.getContentType( );
        _lSize = item.getSize( );
    }

    /**
     * @return the field name
     */
    public String getFieldName( )
    {
        return _strFieldName;
    }

    /**
     * @return the file name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * @return the content type
     */
    public String getContentType( )
    {
        return _strContentType;
    }

    /**
     * @return the size
     */
    public long getSize( )
    {
        return _lSize;
    }
}
