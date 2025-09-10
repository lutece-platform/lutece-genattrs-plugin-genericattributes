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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.file.GenericAttributeFileService;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractGenAttUploadHandler;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

/**
 * Abstract entries of type files. This abstract entry type extends the abstract entry type upload.
 */
public abstract class AbstractEntryTypeFile extends AbstractEntryTypeUpload
{
    public static final String ENTRY_TYPE_KEYNAME = "entryTypeFile";

	/**
     * {@inheritDoc}
     */
    @Override
    public abstract AbstractGenAttUploadHandler getAsynchronousUploadHandler( );

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        if ( !( request instanceof MultipartHttpServletRequest ) )
        {
            return entry.isMandatory( ) ? new MandatoryError( entry, locale ) : null;
        }

        String strAttributeName = getAttributeName( entry, request );

        if ( strAttributeName == null )
        {
            return null;
        }

        if ( getAsynchronousUploadHandler( ).hasAddFileFlag( request, strAttributeName ) )
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            List<FileItem> listFileItemsToUpload = multipartRequest.getFileList( strAttributeName );
            List<FileItem> listUploadedFileItems = getAsynchronousUploadHandler( ).getListUploadedFiles( strAttributeName, request.getSession( ) );
            GenericAttributeError error = null;

            // remove when multipartRequest.getFileList( ) will be fixed.
            if ( listFileItemsToUpload.size( ) == 1 && listFileItemsToUpload.get( 0 ).getName( ).isEmpty( ) )
            {
                listFileItemsToUpload = null;
            }

            if ( listFileItemsToUpload != null )
            {
                error = this.canUploadFiles( entry, listUploadedFileItems, listFileItemsToUpload, locale );
            }

            if ( error != null )
            {
                for ( FileItem fileItem : listUploadedFileItems )
                {
                    Response response = getResponseFromFile( fileItem, entry, false );
                    response.setIterationNumber( getResponseIterationValue( request ) );

                    listResponse.add( response );
                }
                return error;
            }
        }

        List<FileItem> listFilesSource = getFileSources( request, strAttributeName );

        GenericAttributeError genAttError = null;
        List<RegularExpression> listRegularExpression = entry.getFields().stream()
                .filter(field -> field.getRegularExpressionList()!=null)
                .flatMap(field -> field.getRegularExpressionList().stream())
                .collect(Collectors.toList());

        if ( getAsynchronousUploadHandler( ).hasRemoveFlag( request, strAttributeName )
                || getAsynchronousUploadHandler( ).hasAddFileFlag( request, strAttributeName ) )
        {
            if ( CollectionUtils.isNotEmpty( listFilesSource ) )
            {
                for ( FileItem fileItem : listFilesSource )
                {
                    listResponse.add( getResponseFromFile( fileItem, entry, false ) );
                }
            }

            genAttError = new GenericAttributeError( );
            genAttError.setErrorMessage( StringUtils.EMPTY );
            genAttError.setMandatoryError( false );
            genAttError.setIsDisplayableError( false );

            return genAttError;
        }

        if ( CollectionUtils.isNotEmpty( listFilesSource ) )
        {
            genAttError = checkResponseData( entry, listFilesSource, locale, request );

            for ( FileItem fileItem : listFilesSource )
            {
                Response response = getResponseFromFile( fileItem, entry, genAttError == null );
                response.setIterationNumber( getResponseIterationValue( request ) );

                genAttError = checkRegularExpression( listRegularExpression, response, entry);
                listResponse.add( response );
            }

            if ( genAttError != null )
            {
                return genAttError;
            }

            for ( FileItem fileItem : listFilesSource )
            {
                if ( checkForImages( ) )
                {
                    genAttError = doCheckforImages( fileItem, entry, request.getLocale( ) );
                }
            }

            return genAttError;
        }

        if ( entry.isMandatory( ) )
        {
            genAttError = new MandatoryError( entry, locale );

            Response response = new Response( );
            response.setEntry( entry );
            response.setIterationNumber( getResponseIterationValue( request ) );
            listResponse.add( response );
        }

        return genAttError;
    }

    /**
     * check regular expressions
     * @param listRegularExpression list of regular expression
     * @param response The Response
     * @param entry The Entry
     * @return error
     */
    private GenericAttributeError checkRegularExpression( List<RegularExpression> listRegularExpression, Response response, Entry entry )
    {

        for ( RegularExpression re : listRegularExpression)
        {

            if ( !RegularExpressionService.getInstance( ).isMatches( getMimeTypeFromResponse(response), re ) )
            {
                GenericAttributeError error = new GenericAttributeError( );
                error.setMandatoryError( false );
                error.setTitleQuestion( entry.getTitle( ) );
                error.setErrorMessage( re.getErrorMessage( ) );

                return error;
            }
        }
        return null;
    }

    /**
     * Retrieves the MIME type of the file contained in the given response.
     *
     * @param response The Response
     * @return The MIME type of the file if present, otherwise returns null.
     */
    private String getMimeTypeFromResponse(Response response)
    {
        File file = response.getFile();
        if (file != null)
        {
            return file.getMimeType();
        }
        return null;
    }

    /**
     * Get a generic attributes response from a file item
     * 
     * @param fileItem
     *            The file item
     * @param entry
     *            The entry
     * @param bCreatePhysicalFile
     *            True to create the physical file associated with the file of the response, false otherwise. Note that the physical file will never be saved in
     *            the database by this method, like any other created object.
     * @return The created response
     */
    private Response getResponseFromFile( FileItem fileItem, Entry entry, boolean bCreatePhysicalFile )
    {
        if ( fileItem instanceof GenAttFileItem )
        {
            GenAttFileItem genAttFileItem = (GenAttFileItem) fileItem;

            if ( genAttFileItem.getIdResponse( ) > 0 )
            {
                Response response = ResponseHome.findByPrimaryKey( genAttFileItem.getIdResponse( ) );
                response.setEntry( entry );
                response.setFile( GenericAttributeFileService.getInstance( ).load( response.getFile( ).getFileKey( ), response.getFile( ).getOrigin( ) ) );

                if ( bCreatePhysicalFile )
                {
                    response.getFile( ).getPhysicalFile( ).setValue( fileItem.get( ) );
                }

                return response;
            }
        }

        Response response = new Response( );
        response.setEntry( entry );

        File file = new File( );
        file.setTitle( fileItem.getName( ) );
        file.setSize( ( fileItem.getSize( ) < Integer.MAX_VALUE ) ? (int) fileItem.getSize( ) : Integer.MAX_VALUE );

        if ( bCreatePhysicalFile )
        {
            file.setMimeType( FileSystemUtil.getMIMEType( file.getTitle( ) ) );

            PhysicalFile physicalFile = new PhysicalFile( );
            physicalFile.setValue( fileItem.get( ) );
            file.setPhysicalFile( physicalFile );
        }

        response.setFile( file );

        return response;
    }
}
