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
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.file.GenericAttributeFileService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The answer to a gallery image question is the id of a file of the gallery: it is loaded from the file store provider of the gallery images
 * (the entry type key was passed as provider name: AppException "No file service Available")
 */
public class AbstractEntryTypeGalleryImageTest extends LuteceTestCase
{
    private static final int ID_ENTRY = 4711;

    private static class TestEntryTypeGalleryImage extends AbstractEntryTypeGalleryImage
    {
        @Override
        public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
        {
            return "";
        }

        @Override
        public String getTemplateCreate( Entry entry, boolean bDisplayFront )
        {
            return "";
        }

        @Override
        public String getTemplateModify( Entry entry, boolean bDisplayFront )
        {
            return "";
        }
    }

    @Test
    public void testGetResponseDataLoadsTheGalleryFile( ) throws Exception
    {
        IFileStoreServiceProvider provider = CDI.current( ).select( FileService.class ).get( ).getFileStoreServiceProvider( );
        File file = new File( );
        file.setTitle( "gallery.png" );
        file.setMimeType( "image/png" );
        PhysicalFile physicalFile = new PhysicalFile( );
        physicalFile.setValue( new byte [ ] {
                1, 2, 3
        } );
        file.setPhysicalFile( physicalFile );
        file.setSize( 3 );
        String strIdFile = provider.storeFile( file );
        try
        {
            TestEntryTypeGalleryImage entryType = new TestEntryTypeGalleryImage( );
            entryType._genericAttributeFileService = CDI.current( ).select( GenericAttributeFileService.class ).get( );
            Entry entry = new Entry( );
            entry.setIdEntry( ID_ENTRY );

            MockHttpServletRequest request = new MockHttpServletRequest( );
            request.addParameter( IEntryTypeService.PREFIX_ATTRIBUTE + ID_ENTRY, strIdFile );
            List<Response> listResponse = new ArrayList<>( );

            assertNull( entryType.getResponseData( entry, request, listResponse, Locale.FRENCH ) );
            assertEquals( 1, listResponse.size( ) );
            assertNotNull( listResponse.get( 0 ).getFile( ) );
            assertEquals( "gallery.png", listResponse.get( 0 ).getFile( ).getTitle( ) );
        }
        finally
        {
            provider.delete( strIdFile );
        }
    }
}
