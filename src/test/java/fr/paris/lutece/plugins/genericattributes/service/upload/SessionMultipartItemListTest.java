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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.upload.MultipartItem;

/**
 * Uploaded files kept in the session: the content follows a serialized session (another instance)
 */
public class SessionMultipartItemListTest
{
    private static final byte [ ] CONTENT = "file content".getBytes( StandardCharsets.UTF_8 );

    @Test
    public void testItemsAreWrappedWithoutCopy( )
    {
        CountingItem item = new CountingItem( );
        List<MultipartItem> list = new SessionMultipartItemList( );
        list.add( item );
        list.addAll( List.of( new CountingItem( ) ) );

        assertTrue( list.get( 0 ) instanceof SessionMultipartItem );
        assertTrue( list.get( 1 ) instanceof SessionMultipartItem );
        assertSame( list.get( 0 ), SessionMultipartItem.of( list.get( 0 ) ) );
        assertEquals( 0, item._nReads, "the content is not read while the session is not serialized" );
    }

    @Test
    public void testContentFollowsTheSerializedSession( ) throws Exception
    {
        List<MultipartItem> list = new SessionMultipartItemList( );
        list.add( new CountingItem( ) );

        ByteArrayOutputStream bytes = new ByteArrayOutputStream( );
        try ( ObjectOutputStream out = new ObjectOutputStream( bytes ) )
        {
            out.writeObject( list );
        }
        @SuppressWarnings( "unchecked" )
        List<MultipartItem> copy = (List<MultipartItem>) new ObjectInputStream( new ByteArrayInputStream( bytes.toByteArray( ) ) ).readObject( );

        MultipartItem item = copy.get( 0 );
        assertEquals( "field", item.getFieldName( ) );
        assertEquals( "file.txt", item.getName( ) );
        assertEquals( "text/plain", item.getContentType( ) );
        assertEquals( CONTENT.length, item.getSize( ) );
        assertArrayEquals( CONTENT, item.get( ) );
        assertArrayEquals( CONTENT, item.getInputStream( ).readAllBytes( ) );
        item.delete( );
        assertEquals( 0, item.get( ).length );
    }

    /**
     * In-memory item, not serializable, counting the reads of its content
     */
    private static class CountingItem implements MultipartItem
    {
        private int _nReads;

        @Override
        public String getFieldName( )
        {
            return "field";
        }

        @Override
        public String getName( )
        {
            return "file.txt";
        }

        @Override
        public String getContentType( )
        {
            return "text/plain";
        }

        @Override
        public long getSize( )
        {
            return CONTENT.length;
        }

        @Override
        public InputStream getInputStream( ) throws IOException
        {
            _nReads++;
            return new ByteArrayInputStream( CONTENT );
        }

        @Override
        public byte [ ] get( )
        {
            _nReads++;
            return CONTENT.clone( );
        }

        @Override
        public void delete( )
        {
            // nothing to delete
        }
    }
}
