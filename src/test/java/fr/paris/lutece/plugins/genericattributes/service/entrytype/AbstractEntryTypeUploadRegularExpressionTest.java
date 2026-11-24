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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractGenAttUploadHandler;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

/**
 * The regular expressions offered for a file question are the ones not yet attached to its configuration field
 */
public class AbstractEntryTypeUploadRegularExpressionTest extends LuteceTestCase
{
    @Test
    public void testAttachedExpressionIsNotOfferedAgain( )
    {
        RegularExpression expressionAttached = expression( 1, "attached" );
        RegularExpression expressionFree = expression( 2, "free" );

        Field fieldExportable = new Field( );
        fieldExportable.setCode( IEntryTypeService.FIELD_EXPORTABLE );
        fieldExportable.setRegularExpressionList( new ArrayList<>( ) );
        Field fieldConfig = new Field( );
        fieldConfig.setCode( IEntryTypeService.FIELD_FILE_CONFIG );
        fieldConfig.setRegularExpressionList( new ArrayList<>( List.of( expressionAttached ) ) );
        Entry entry = new Entry( );
        entry.setFields( new ArrayList<>( List.of( fieldExportable, fieldConfig ) ) );

        TestEntryTypeFile entryType = new TestEntryTypeFile( );
        entryType._regularExpressionService = new TestRegularExpressionService( List.of( expressionAttached, expressionFree ) );

        ReferenceList list = entryType.getReferenceListRegularExpression( entry, null );

        assertEquals( 1, list.size( ) );
        assertEquals( "2", list.get( 0 ).getCode( ) );
    }

    private static RegularExpression expression( int nId, String strTitle )
    {
        RegularExpression expression = new RegularExpression( );
        expression.setIdExpression( nId );
        expression.setTitle( strTitle );
        return expression;
    }

    /**
     * Regular expression service available, with a fixed list
     */
    private static final class TestRegularExpressionService extends RegularExpressionService
    {
        private final List<RegularExpression> _list;

        TestRegularExpressionService( List<RegularExpression> list )
        {
            _list = list;
        }

        @Override
        public boolean isAvailable( )
        {
            return true;
        }

        @Override
        public List<RegularExpression> getAllRegularExpression( )
        {
            return _list;
        }
    }

    private static final class TestEntryTypeFile extends AbstractEntryTypeFile
    {
        @Override
        public AbstractGenAttUploadHandler getAsynchronousUploadHandler( )
        {
            return null;
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
    }
}
