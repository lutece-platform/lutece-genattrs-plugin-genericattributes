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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

public abstract class AbstractEntryTypeImage extends AbstractEntryTypeUpload
{
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        if ( request instanceof MultipartHttpServletRequest )
        {
            GenericAttributeError genAttError = null;

            if ( !entry.isMandatory( ) )
            {
                String sourceBase = request.getParameter( ( PREFIX_ATTRIBUTE + entry.getIdEntry( ) ) );

                Response response = getResponseFromImage( sourceBase, entry, false );
                response.setIterationNumber( getResponseIterationValue( request ) );

                listResponse.add( response );
                return genAttError;
            }

            if ( entry.isMandatory( ) )
            {
                genAttError = new MandatoryError( entry, locale );

                Response response = new Response( );
                response.setEntry( entry );
                listResponse.add( response );
            }

            return genAttError;
        }

        return entry.isMandatory( ) ? new MandatoryError( entry, locale ) : null;
    }

    // PRIVATE METHODS

    /**
     * Get a generic attributes response from a file item
     * 
     * @param imageSource
     *            The file item
     * @param entry
     *            The entry
     * @param bCreatePhysicalFile
     *            True to create the physical file associated with the file of the response, false otherwise. Note that the physical file will never be saved in
     *            the database by this method, like any other created object.
     * @return The created response
     */
    protected Response getResponseFromImage( String imageSource, Entry entry, boolean bCreatePhysicalFile )
    {
        Response response = new Response( );
        response.setEntry( entry );

        File file = new File( );
        DatatypeConverter.parseBase64Binary( imageSource );

        file.setTitle( "crop_" + entry.getTitle( ) );

        if ( bCreatePhysicalFile )
        {
            file.setMimeType( FileSystemUtil.getMIMEType( file.getTitle( ) ) );

            PhysicalFile physicalFile = new PhysicalFile( );
            physicalFile.setValue( DatatypeConverter.parseBase64Binary( imageSource ) );
            file.setPhysicalFile( physicalFile );
        }

        response.setFile( file );

        return response;
    }
}
