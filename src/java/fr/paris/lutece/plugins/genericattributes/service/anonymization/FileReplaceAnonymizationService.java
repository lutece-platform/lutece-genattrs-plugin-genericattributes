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
package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.file.GenericAttributeFileService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * {@link IEntryTypeAnonymisationService} that replace the file with an empty file.
 */
@ApplicationScoped
@Named("genericattributes.fileReplaceAnonymizationService")
public class FileReplaceAnonymizationService extends AbstractAnonymizationService
{

    private static final String FILES_DIRECTORY = "/WEB-INF/anonymization/";

    @Override
    public void anonymizeResponse( Entry entry, Response response, boolean first )
    {
        if ( isAnonymizable( entry ) )
        {
            String pattern = getPattern( entry );
            if ( pattern.contains( _wildcard ) && response.getFile( ) != null )
            {
                File responseFile = GenericAttributeFileService.getInstance( ).load( response.getFile( ).getFileKey( ), response.getFile( ).getOrigin( ) );

                if ( responseFile != null )
                {
                    String fileType = FilenameUtils.getExtension( responseFile.getTitle( ) );

                    try
                    {
                        java.io.File emptyFile = getEmptyFile( "empty." + fileType );
                        responseFile.setTitle( emptyFile.getName( ) );
                        responseFile.setSize( (int) emptyFile.length( ) );
                        responseFile.getPhysicalFile( ).setValue( FileUtils.readFileToByteArray( emptyFile ) );
                        response.setFile( responseFile );
                    }
                    catch( IOException e )
                    {
                        AppLogService.error( "Error while replacing file", e );
                        GenericAttributeFileService.getInstance( ).delete( response.getFile( ).getFileKey( ), response.getFile( ).getOrigin( ) );
                        response.setFile( null );
                    }
                }
                else
                {
                    response.setFile( null );
                }
            }
        }
    }

    private java.io.File getEmptyFile( String filename )
    {
        Path path = Paths.get( AppPathService.getWebAppPath( ), FILES_DIRECTORY, filename );

        if ( !path.toFile( ).exists( ) )
        {
            path = Paths.get( AppPathService.getWebAppPath( ), FILES_DIRECTORY, "empty.txt" );
        }
        return path.toFile( );
    }
}
