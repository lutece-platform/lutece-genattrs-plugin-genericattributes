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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractAsynchronousUploadHandler;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import javax.servlet.http.HttpServletRequest;


/**
 * Abstract entries of type files. This abstract entry type extends the abstract
 * entry type upload.
 */
public abstract class AbstractEntryTypeFile extends AbstractEntryTypeUpload
{
    private static final String MESSAGE_ERROR_NOT_AN_IMAGE = "genericattributes.message.notAnImage";

    /**
     * Check whether this entry type allows only images or every file type
     * @return True if this entry type allows only images, false if it allow
     *         every file type
     */
    protected abstract boolean checkForImages(  );

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract AbstractAsynchronousUploadHandler getAsynchronousUploadHandler(  );

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse,
        Locale locale )
    {
        List<FileItem> listFilesSource = null;

        if ( request instanceof MultipartHttpServletRequest )
        {
            List<FileItem> asynchronousFileItem = getFileSources( entry, request );

            if ( asynchronousFileItem != null )
            {
                listFilesSource = asynchronousFileItem;
            }

            GenericAttributeError genAttError = null;

            if ( getAsynchronousUploadHandler(  ).hasRemoveFlag( request, Integer.toString( entry.getIdEntry(  ) ) ) ||
                    getAsynchronousUploadHandler(  ).hasAddFileFlag( request, Integer.toString( entry.getIdEntry(  ) ) ) )
            {
                if ( ( listFilesSource != null ) && !listFilesSource.isEmpty(  ) )
                {
                    for ( FileItem fileItem : listFilesSource )
                    {
                        listResponse.add( getResponseFromFile( fileItem, entry, false ) );
                    }
                }

                genAttError = new GenericAttributeError(  );
                genAttError.setErrorMessage( StringUtils.EMPTY );
                genAttError.setMandatoryError( false );
                genAttError.setIsDisplayableError( false );

                return genAttError;
            }

            if ( ( listFilesSource != null ) && !listFilesSource.isEmpty(  ) )
            {
                genAttError = checkResponseData( entry, listFilesSource, locale, request );

                for ( FileItem fileItem : listFilesSource )
                {
                    listResponse.add( getResponseFromFile( fileItem, entry, genAttError == null ) );
                }

                if ( genAttError != null )
                {
                    return genAttError;
                }

                for ( FileItem fileItem : listFilesSource )
                {
                    String strFilename = FileUploadService.getFileNameOnly( fileItem );

                    if ( checkForImages(  ) )
                    {
                        BufferedImage image = null;

                        try
                        {
                            if ( fileItem.get(  ) != null )
                            {
                                image = ImageIO.read( new ByteArrayInputStream( fileItem.get(  ) ) );
                            }
                        }
                        catch ( IOException e )
                        {
                            AppLogService.error( e );
                        }

                        if ( ( image == null ) && StringUtils.isNotBlank( strFilename ) )
                        {
                            genAttError = new GenericAttributeError(  );
                            genAttError.setMandatoryError( false );

                            Object[] args = { fileItem.getName(  ) };
                            genAttError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ERROR_NOT_AN_IMAGE,
                                    args, request.getLocale(  ) ) );
                            genAttError.setTitleQuestion( entry.getTitle(  ) );
                        }
                    }
                }

                return genAttError;
            }

            if ( entry.isMandatory(  ) )
            {
                genAttError = new MandatoryError( entry, locale );

                Response response = new Response(  );
                response.setEntry( entry );
                listResponse.add( response );
            }

            return genAttError;
        }

        return entry.isMandatory(  ) ? new MandatoryError( entry, locale ) : null;
    }

    private Response getResponseFromFile( FileItem fileItem, Entry entry, boolean bCreatePhysicalFile )
    {
        if ( fileItem instanceof GenAttFileItem )
        {
            GenAttFileItem genAttFileItem = (GenAttFileItem) fileItem;

            if ( genAttFileItem.getIdResponse(  ) > 0 )
            {
                Response response = ResponseHome.findByPrimaryKey( genAttFileItem.getIdResponse(  ) );
                response.setEntry( entry );
                response.setFile( FileHome.findByPrimaryKey( response.getFile(  ).getIdFile(  ) ) );

                if ( bCreatePhysicalFile )
                {
                    response.getFile(  ).getPhysicalFile(  ).setValue( fileItem.get(  ) );
                }

                return response;
            }
        }

        Response response = new Response(  );
        response.setEntry( entry );

        File file = new File(  );

        if ( bCreatePhysicalFile )
        {
            PhysicalFile physicalFile = new PhysicalFile(  );
            physicalFile.setValue( fileItem.get(  ) );
            file.setPhysicalFile( physicalFile );
        }

        file.setTitle( fileItem.getName(  ) );
        file.setSize( ( fileItem.getSize(  ) < Integer.MAX_VALUE ) ? (int) fileItem.getSize(  ) : Integer.MAX_VALUE );
        response.setFile( file );

        return response;
    }
}
