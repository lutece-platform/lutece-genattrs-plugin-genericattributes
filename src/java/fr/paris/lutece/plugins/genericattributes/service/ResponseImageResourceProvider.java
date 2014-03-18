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
package fr.paris.lutece.plugins.genericattributes.service;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.image.ImageResourceProvider;
import fr.paris.lutece.util.file.FileUtil;


/**
 * Resource provider for images
 */
public class ResponseImageResourceProvider implements ImageResourceProvider
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceTypeId(  )
    {
        return Response.RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageResource getImageResource( int nIdResource )
    {
        Response response = ResponseHome.findByPrimaryKey( nIdResource );

        if ( response.getFile(  ) != null )
        {
            File file = FileHome.findByPrimaryKey( response.getFile(  ).getIdFile(  ) );

            if ( ( file.getPhysicalFile(  ) != null ) && FileUtil.hasImageExtension( file.getTitle(  ) ) )
            {
                PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile(  )
                                                                                   .getIdPhysicalFile(  ) );
                ImageResource image = new ImageResource(  );
                image.setImage( physicalFile.getValue(  ) );
                image.setMimeType( file.getMimeType(  ) );

                return image;
            }
        }

        return null;
    }
}
