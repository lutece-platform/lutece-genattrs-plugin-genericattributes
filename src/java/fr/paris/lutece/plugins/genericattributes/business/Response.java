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

import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;

import java.io.Serializable;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * class Response
 */
public class Response implements Serializable
{
    /**
     * Active status value
     */
    public static final int CONSTANT_STATUS_ACTIVE = 1;

    /**
     * Anonymized status value
     */
    public static final int CONSTANT_STATUS_ANONYMIZED = 10;

    /**
     * Default iteration number of a Response
     */
    private static final int DEFAULT_ITERATION_NUMBER = NumberUtils.INTEGER_MINUS_ONE;

    /**
     * The resource type of responses
     */
    public static final String RESOURCE_TYPE = "GENERICATTRIBUTES_RESPONSE";
    private static final long serialVersionUID = -4566081273426609947L;
    private int _nIdResponse;
    private String _strToStringValueResponse;
    private Entry _entry;
    private int _nIterationNumber = DEFAULT_ITERATION_NUMBER;
    private Field _field;
    private String _strResponseValue;
    private int _nStatus;
    private File _file;
    private boolean _bIsImage;

    /**
     * Default constructor
     */
    public Response( )
    {
        // Do nothing
    }

    /**
     * Creates a response that is a copy of another response
     * 
     * @param response
     *            The response to copy
     */
    public Response( Response response )
    {
        this._nIdResponse = response.getIdResponse( );
        this._strToStringValueResponse = response.getToStringValueResponse( );
        this._entry = response.getEntry( );
        this._field = response.getField( );
        this._strResponseValue = response.getResponseValue( );
        this._nStatus = response.getStatus( );

        File file = response.getFile( );

        if ( file != null )
        {
            _file = new File( );
            _file.setExtension( file.getExtension( ) );
            _file.setIdFile( file.getIdFile( ) );
            _file.setMimeType( file.getMimeType( ) );
            _file.setSize( file.getSize( ) );
            _file.setTitle( file.getTitle( ) );

            PhysicalFile physicalFile = file.getPhysicalFile( );

            if ( physicalFile != null )
            {
                PhysicalFile pfDuplicated = new PhysicalFile( );
                pfDuplicated.setIdPhysicalFile( pfDuplicated.getIdPhysicalFile( ) );
                pfDuplicated.setValue( pfDuplicated.getValue( ) );
                _file.setPhysicalFile( pfDuplicated );
            }
        }
    }

    /**
     *
     * @return the question associate to the response
     */
    public Entry getEntry( )
    {
        return _entry;
    }

    /**
     * set the question associate to the response
     * 
     * @param entry
     *            the question associate to the response
     */
    public void setEntry( Entry entry )
    {
        _entry = entry;
    }

    /**
     * Return the iteration number of the response
     * 
     * @return the nIterationNumber
     */
    public int getIterationNumber( )
    {
        return _nIterationNumber;
    }

    /**
     * Set the iteration number of the response
     * 
     * @param nIterationNumber
     *            the nIterationNumber to set
     */
    public void setIterationNumber( int nIterationNumber )
    {
        this._nIterationNumber = nIterationNumber;
    }

    /**
     *
     * @return the id of the response
     */
    public int getIdResponse( )
    {
        return _nIdResponse;
    }

    /**
     * set the id of the response
     * 
     * @param idResponse
     *            the id of the response
     */
    public void setIdResponse( int idResponse )
    {
        _nIdResponse = idResponse;
    }

    /**
     * get the field associate to the response
     * 
     * @return the field associate to the response
     */
    public Field getField( )
    {
        return _field;
    }

    /**
     * set the field associate to the response
     * 
     * @param field
     *            field
     */
    public void setField( Field field )
    {
        _field = field;
    }

    /**
     * return the string value response
     * 
     * @return the string value of the response
     */
    public String getToStringValueResponse( )
    {
        if ( _strToStringValueResponse != null )
        {
            return _strToStringValueResponse;
        }

        return _strResponseValue;
    }

    /**
     * set the string value response
     * 
     * @param strValueResponse
     *            the string value of the response
     */
    public void setToStringValueResponse( String strValueResponse )
    {
        _strToStringValueResponse = strValueResponse;
    }

    /**
     * Set the response value
     * 
     * @param strResponseValue
     *            the response value
     */
    public void setResponseValue( String strResponseValue )
    {
        _strResponseValue = strResponseValue;
    }

    /**
     * Get the response value
     * 
     * @return the response value
     */
    public String getResponseValue( )
    {
        return _strResponseValue;
    }

    /**
     * Get the status of this response
     * 
     * @return The status of this response
     */
    public int getStatus( )
    {
        return _nStatus;
    }

    /**
     * Set the status of this response
     * 
     * @param nStatus
     *            The status of this response
     */
    public void setStatus( int nStatus )
    {
        this._nStatus = nStatus;
    }

    /**
     * Get the file associated with this response
     * 
     * @return the file The file associated with this response
     */
    public File getFile( )
    {
        return _file;
    }

    /**
     * Set the file associated with this response
     * 
     * @param file
     *            The file associated with this response
     */
    public void setFile( File file )
    {
        this._file = file;
    }

    /**
     * Get the isImage of this response
     * 
     * @return The bIsImage of this response
     */
    public boolean getIsImage( )
    {
        return _bIsImage;
    }

    /**
     * Set the isImage of this response
     * 
     * @param bisImage
     *            of this response
     */
    public void setIsImage( boolean bIsImage )
    {
        this._bIsImage = bIsImage;
    }
}
