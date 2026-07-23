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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractGenAttUploadHandler;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.MokeHttpServletRequest;

/**
 * Test for the response building of a file entry when the uploaded file does not pass the validation.
 *
 * When a validation fails, the physical file is not created and the response only holds the file name and size. That validation error must survive the building
 * loop, otherwise the response is kept and saved with a file that can never be downloaded.
 */
public class AbstractEntryTypeFileTest extends LuteceTestCase
{
    private static final int ID_ENTRY = 1;

    /**
     * A file that is too big must yield a validation error from getResponseData, so the response is not saved. The error is raised by checkResponseData and must
     * not be overwritten by the regular expression check that runs right after in the same loop.
     */
    public void testGetResponseDataKeepsValidationErrorWhenFileTooBig( )
    {
        Field fieldMaxSize = new Field( );
        fieldMaxSize.setCode( IEntryTypeService.FIELD_FILE_MAX_SIZE );
        fieldMaxSize.setValue( "2" );

        Entry entry = new Entry( );
        entry.setIdEntry( ID_ENTRY );
        entry.setTitle( "File entry" );
        entry.setFields( new ArrayList<>( ) );
        entry.getFields( ).add( fieldMaxSize );

        FileItem oversizedFile = new GenAttFileItem( "azerty".getBytes( ), "test.csv" );

        AbstractEntryTypeFile entryType = new TestEntryTypeFile( oversizedFile );

        MultipartHttpServletRequest request = new MultipartHttpServletRequest( new MokeHttpServletRequest( ), new HashMap<>( ), new HashMap<>( ) );

        List<Response> listResponse = new ArrayList<>( );
        GenericAttributeError error = entryType.getResponseData( entry, request, listResponse, Locale.FRANCE );

        assertNotNull( "The validation error must not be discarded : the response would be saved with a file without content", error );
    }

    /**
     * Minimal concrete file entry type whose upload handler returns a fixed list of files as if they had been uploaded asynchronously.
     */
    private static final class TestEntryTypeFile extends AbstractEntryTypeFile
    {
        private final transient AbstractGenAttUploadHandler _handler;

        TestEntryTypeFile( FileItem uploadedFile )
        {
            _handler = new TestUploadHandler( uploadedFile );
        }

        @Override
        public AbstractGenAttUploadHandler getAsynchronousUploadHandler( )
        {
            return _handler;
        }

        @Override
        public String getUrlDownloadFile( int nResponseId, String strBaseUrl )
        {
            return strBaseUrl;
        }

        @Override
        protected boolean checkForImages( )
        {
            return false;
        }

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
        public String getTemplateEntryReadOnly( )
        {
            return "";
        }

        @Override
        public String getTemplateEntryReadOnly( boolean bDisplayFront )
        {
            return "";
        }
    }

    /**
     * Upload handler that exposes the single provided file as an already uploaded file. Every other operation is a no-op : the test only needs the retrieval of
     * the uploaded files.
     */
    private static final class TestUploadHandler extends AbstractGenAttUploadHandler
    {
        private final transient List<FileItem> _listFiles;

        TestUploadHandler( FileItem uploadedFile )
        {
            _listFiles = new ArrayList<>( );
            _listFiles.add( uploadedFile );
        }

        @Override
        public List<FileItem> getListUploadedFiles( String strFieldName, HttpSession session )
        {
            return _listFiles;
        }

        @Override
        public List<FileItem> getListPartialUploadedFiles( String strFieldName, HttpSession session )
        {
            return new ArrayList<>( );
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
        public String doRemoveUploadedFile( HttpServletRequest request, String strFieldName, List<Integer> listIndexes )
        {
            return null;
        }

        @Override
        public void addFilesUploadedSynchronously( HttpServletRequest request, String strFieldName )
        {
            // no-op
        }

        @Override
        public void addFileItemToUploadedFilesList( FileItem fileItem, String strFieldName, HttpServletRequest request )
        {
            // no-op
        }

        @Override
        public void addFileItemToPartialUploadedFilesList( FileItem fileItem, String strFieldName, HttpServletRequest request )
        {
            // no-op
        }

        @Override
        public void removeFileItem( String strFieldName, HttpSession session, int nIndex )
        {
            // no-op
        }

        @Override
        public void removeAllFileItem( HttpSession session )
        {
            // no-op
        }

        @Override
        public byte [ ] doRetrieveUploadedFile( HttpServletRequest request )
        {
            return new byte [ 0];
        }

        @Override
        public String canUploadFiles( HttpServletRequest request, String strFieldName, List<FileItem> listFileItemsToUpload, Locale locale )
        {
            return null;
        }

        @Override
        public boolean isManagePartialContent( )
        {
            return false;
        }

        @Override
        public String getHandlerName( )
        {
            return "test";
        }

        @Override
        public String getUploadSubmitPrefix( )
        {
            return "";
        }

        @Override
        public String getUploadDeletePrefix( )
        {
            return "";
        }

        @Override
        public String getUploadCheckboxPrefix( )
        {
            return "";
        }
    }
}
