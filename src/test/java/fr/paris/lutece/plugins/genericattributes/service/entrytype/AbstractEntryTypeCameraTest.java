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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.asynchronousupload.service.IAsyncUploadHandler;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Camera entry type in an iterable group
 */
public class AbstractEntryTypeCameraTest extends LuteceTestCase
{
    private static final int ID_ENTRY = 643;

    private static class TestEntryTypeCamera extends AbstractEntryTypeCamera
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

        @Override
        public IAsyncUploadHandler getAsynchronousUploadHandler( )
        {
            return null;
        }

        @Override
        public String getUrlDownloadFile( int nResponseId, String strBaseUrl )
        {
            return "";
        }

        @Override
        protected boolean checkForImages( )
        {
            return true;
        }
    }

    private static String photo( int nSize ) throws IOException
    {
        BufferedImage image = new BufferedImage( nSize, nSize, BufferedImage.TYPE_INT_RGB );
        ByteArrayOutputStream out = new ByteArrayOutputStream( );
        ImageIO.write( image, "png", out );
        return "data:image/png;base64," + Base64.getEncoder( ).encodeToString( out.toByteArray( ) );
    }

    private static Response answer( Map<String, String [ ]> mapParameters, int nIteration )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( ID_ENTRY );
        entry.setTitle( "Camera" );
        Field fieldMaxSize = new Field( );
        fieldMaxSize.setCode( IEntryTypeService.FIELD_MAX_SIZE );
        fieldMaxSize.setValue( "-1" );
        List<Field> listFields = new ArrayList<>( );
        listFields.add( fieldMaxSize );
        entry.setFields( listFields );
        MultipartHttpServletRequest request = new MultipartHttpServletRequest( new MockHttpServletRequest( ), new HashMap<>( ), mapParameters );
        request.setAttribute( IEntryTypeService.ATTRIBUTE_RESPONSE_ITERATION_NUMBER, nIteration );
        List<Response> listResponse = new ArrayList<>( );
        assertNull( new TestEntryTypeCamera( ).getResponseData( entry, request, listResponse, Locale.ENGLISH ) );
        return listResponse.isEmpty( ) ? null : listResponse.get( 0 );
    }

    /**
     * Each iteration reads its own field: attribute<id> for the first one, nIt<n>_attribute<id> for the next ones
     */
    @Test
    public void testEachIterationReadsItsOwnPhoto( ) throws IOException
    {
        String strPhoto0 = photo( 1 );
        String strPhoto1 = photo( 2 );
        Map<String, String [ ]> mapParameters = new HashMap<>( );
        mapParameters.put( "attribute" + ID_ENTRY, new String [ ] {
                strPhoto0
        } );
        mapParameters.put( "nIt1_attribute" + ID_ENTRY, new String [ ] {
                strPhoto1
        } );

        assertEquals( strPhoto0, answer( mapParameters, 0 ).getToStringValueResponse( ) );
        Response response1 = answer( mapParameters, 1 );
        assertEquals( strPhoto1, response1.getToStringValueResponse( ) );
        assertEquals( 1, response1.getIterationNumber( ) );
    }
}
