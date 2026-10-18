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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpSession;

/**
 * Parallel uploads of one session to a field limited to one file: only one file is kept
 */
public class AbstractGenAttUploadHandlerTest
{
    private static final String FIELD_NAME = "attribute1";
    private static final int NB_UPLOADS = 11;
    private static final int MAX_FILES = 1;

    @Test
    public void testParallelUploadsRespectTheMaximumNumberOfFiles( ) throws Exception
    {
        HttpSession session = new MockHttpSession( );
        TestUploadHandler handler = new TestUploadHandler( );
        // the session map of the files is created before the parallel uploads, as by the display of the form
        handler.getListUploadedFiles( FIELD_NAME, session );

        ExecutorService executor = Executors.newFixedThreadPool( NB_UPLOADS );
        try
        {
            List<Callable<Object>> listUploads = new ArrayList<>( );
            for ( int i = 0; i < NB_UPLOADS; i++ )
            {
                MultipartItem fileItem = new GenAttFileItem( ( "content" + i ).getBytes( ), "file" + i + ".txt" );
                listUploads.add( ( ) -> {
                    List<MultipartItem> listFiles = new ArrayList<>( );
                    listFiles.add( fileItem );
                    handler.process( newRequest( session ), null, new HashMap<>( ), listFiles );
                    return null;
                } );
            }
            for ( Future<Object> future : executor.invokeAll( listUploads ) )
            {
                future.get( );
            }
        }
        finally
        {
            executor.shutdownNow( );
        }

        assertEquals( MAX_FILES, handler.getListUploadedFiles( FIELD_NAME, session ).size( ) );
    }

    private static HttpServletRequest newRequest( HttpSession session )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( "fieldname", FIELD_NAME );

        return new HttpServletRequestWrapper( request )
        {
            @Override
            public HttpSession getSession( )
            {
                return session;
            }

            @Override
            public HttpSession getSession( boolean bCreate )
            {
                return session;
            }
        };
    }

    /**
     * Upload handler limited to MAX_FILES files, whose check takes some time as the lookup of the entry does
     */
    private static final class TestUploadHandler extends AbstractGenAttUploadHandler
    {
        @Override
        public String canUploadFiles( HttpServletRequest request, String strFieldName, List<MultipartItem> listFileItemsToUpload, Locale locale )
        {
            int nUploaded = getListUploadedFiles( strFieldName, request.getSession( ) ).size( );
            try
            {
                Thread.sleep( 20 );
            }
            catch( InterruptedException e )
            {
                Thread.currentThread( ).interrupt( );
            }
            return ( nUploaded + listFileItemsToUpload.size( ) > MAX_FILES ) ? "too many files" : null;
        }

        @Override
        public boolean hasAddFileFlag( HttpServletRequest request, String strFieldName )
        {
            return false;
        }

        @Override
        public boolean hasRemoveFlag( HttpServletRequest request, String strFieldName )
        {
            return false;
        }

        @Override
        public void doRemoveFile( HttpServletRequest request, String strFieldName )
        {
            // no-op
        }

        @Override
        public void addFilesUploadedSynchronously( HttpServletRequest request, String strFieldName )
        {
            // no-op
        }

        @Override
        public String getHandlerName( )
        {
            return "test";
        }
    }
}
