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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;

/**
 * Abstract entry type for comments
 */
public abstract class AbstractEntryTypeComment extends EntryTypeService
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strCode = request.getParameter( PARAMETER_ENTRY_CODE );
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strDisplayBo = request.getParameter( PARAMETER_DISPLAY_BO );
        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strComment ) )
        {
            strFieldError = FIELD_COMMENT;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        entry.setCode( strCode );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setIndexed( strIndexed != null );

        GenericAttributesUtils.createOrUpdateField( entry, FIELD_DISPLAY_BO, null, String.valueOf( strDisplayBo != null ) );

        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            FileItem fileItem = multipartRequest.getFile( PARAMETER_FILE );

            if ( fileItem != null && fileItem.get( ) != null )
            {
                removeOldFiles( entry );

                PhysicalFile physicalFile = new PhysicalFile( );
                physicalFile.setValue( fileItem.get( ) );

                File file = new File( );
                file.setTitle( fileItem.getName( ) );
                file.setMimeType( fileItem.getContentType( ) );
                file.setSize( physicalFile.getValue( ).length );
                file.setPhysicalFile( physicalFile );

                String idFile = getFileStoreServiceProvider( ).storeFile( file );
                GenericAttributesUtils.createOrUpdateField( entry, FIELD_DOWNLOADABLE_FILE, file.getTitle( ), idFile );
            }
        }
        return null;
    }

    private void removeOldFiles( Entry entry )
    {
        Field oldFile = entry.getFieldByCode( FIELD_DOWNLOADABLE_FILE );
        if ( oldFile != null )
        {
            getFileStoreServiceProvider( ).delete( oldFile.getValue( ) );
            FieldHome.remove( oldFile.getIdField( ) );
        }
    }

    protected IFileStoreServiceProvider getFileStoreServiceProvider( )
    {
        return FileService.getInstance( ).getFileStoreServiceProvider( "defaultDatabaseFileStoreProvider" );
    }
}
