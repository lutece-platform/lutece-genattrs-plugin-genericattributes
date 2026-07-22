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
import java.util.Optional;

import org.springframework.mock.web.MockHttpServletRequest;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;


public class AbstractEntryTypeGeolocationGetResponseDataTest extends LuteceTestCase
{
    private static final String ADDRESS_1 = "1 Rue de Test 75000 Paris";
    private static final String ID_ADDRESS_1 = "00000_0000_00001";
    private static final String X_1 = "2.000000";
    private static final String Y_1 = "48.000000";
    private static final String GEOMETRY_1 = "housenumber";

    private static final String ADDRESS_2 = "2 Rue de Test 75000 Paris";
    private static final String ID_ADDRESS_2 = "00000_0000_00002";
    private static final String X_2 = "2.100000";
    private static final String Y_2 = "48.100000";

    private TestEntryTypeGeolocation entryType;

    private static class TestEntryTypeGeolocation extends AbstractEntryTypeGeolocation
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

    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        entryType = new TestEntryTypeGeolocation( );
    }

    private Entry buildEntry( int idEntry, boolean mandatory )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( idEntry );
        entry.setMandatory( mandatory );
        entry.setTitle( "Question geoloc " + idEntry );

        List<Field> fields = new ArrayList<>( );
        fields.add( fieldWithCode( AbstractEntryTypeGeolocation.FIELD_ID_ADDRESS ) );
        fields.add( fieldWithCode( AbstractEntryTypeGeolocation.FIELD_ADDRESS ) );
        fields.add( fieldWithCode( AbstractEntryTypeGeolocation.FIELD_ADDITIONAL_ADDRESS ) );
        fields.add( fieldWithCode( AbstractEntryTypeGeolocation.FIELD_X ) );
        fields.add( fieldWithCode( AbstractEntryTypeGeolocation.FIELD_Y ) );
        fields.add( fieldWithCode( AbstractEntryTypeGeolocation.FIELD_GEOMETRY ) );
        entry.setFields( fields );

        return entry;
    }

    private Field fieldWithCode( String code )
    {
        Field field = new Field( );
        field.setCode( code );
        return field;
    }

    private void addGeolocParams( MockHttpServletRequest request, int idEntry, int iteration, String address, String idAddress, String x, String y,
                                  String geometry )
    {
        String prefix = AbstractEntryTypeGeolocation.PARAMETER_PREFIX_ITERATION + iteration + "_" + IEntryTypeService.PREFIX_ATTRIBUTE + idEntry;

        request.addParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_ADDRESS, address );
        request.addParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_ID_ADDRESS, idAddress );
        request.addParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_X, x );
        request.addParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_Y, y );
        request.addParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_GEOMETRY, geometry );
    }

    private Optional<String> responseValueForField( List<Response> listResponse, String fieldCode )
    {
        return listResponse.stream( )
                .filter( r -> r.getField( ) != null && fieldCode.equals( r.getField( ).getCode( ) ) )
                .map( Response::getResponseValue )
                .findFirst( );
    }

    public void testGetResponseData_singleEntry_firstCall_shouldSucceedAndReadCorrectParams( )
    {
        Entry entry = buildEntry( 1, true );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        addGeolocParams( request, 1, 0, ADDRESS_1, ID_ADDRESS_1, X_1, Y_1, GEOMETRY_1 );

        List<Response> listResponse = new ArrayList<>( );
        GenericAttributeError error = entryType.getResponseData( entry, request, listResponse, Locale.FRENCH );

        assertNull( "Aucune erreur attendue : adresse complète fournie pour l'itération 0", error );
        assertEquals( ADDRESS_1, responseValueForField( listResponse, AbstractEntryTypeGeolocation.FIELD_ADDRESS ).orElse( null ) );
        assertEquals( ID_ADDRESS_1, responseValueForField( listResponse, AbstractEntryTypeGeolocation.FIELD_ID_ADDRESS ).orElse( null ) );
    }

    public void testGetResponseData_multipleDistinctEntries_shouldNotShareIterationCounter( )
    {
        Entry entry1 = buildEntry( 1, true );
        Entry entry2 = buildEntry( 2, true );
        Entry entry3 = buildEntry( 3, true );

        MockHttpServletRequest sharedRequest = new MockHttpServletRequest( );
        addGeolocParams( sharedRequest, 1, 0, ADDRESS_1, ID_ADDRESS_1, X_1, Y_1, GEOMETRY_1 );
        addGeolocParams( sharedRequest, 2, 0, ADDRESS_2, ID_ADDRESS_2, X_2, Y_2, GEOMETRY_1 );
        addGeolocParams( sharedRequest, 3, 0, ADDRESS_1, ID_ADDRESS_1, X_1, Y_1, GEOMETRY_1 );

        List<Response> listResponse1 = new ArrayList<>( );
        List<Response> listResponse2 = new ArrayList<>( );
        List<Response> listResponse3 = new ArrayList<>( );

        GenericAttributeError error1 = entryType.getResponseData( entry1, sharedRequest, listResponse1, Locale.FRENCH );
        GenericAttributeError error2 = entryType.getResponseData( entry2, sharedRequest, listResponse2, Locale.FRENCH );
        GenericAttributeError error3 = entryType.getResponseData( entry3, sharedRequest, listResponse3, Locale.FRENCH );

        assertNull( "Question 1 : comportement inchangé, ne doit pas être en erreur", error1 );
        assertNull( "Question 2 distincte : ne doit PAS échouer (c'était le bug)", error2 );
        assertNull( "Question 3 distincte : ne doit PAS échouer (c'était le bug)", error3 );

        assertEquals( ADDRESS_1, responseValueForField( listResponse1, AbstractEntryTypeGeolocation.FIELD_ADDRESS ).orElse( null ) );
        assertEquals( ADDRESS_2, responseValueForField( listResponse2, AbstractEntryTypeGeolocation.FIELD_ADDRESS ).orElse( null ) );
        assertEquals( ADDRESS_1, responseValueForField( listResponse3, AbstractEntryTypeGeolocation.FIELD_ADDRESS ).orElse( null ) );
    }

    public void testGetResponseData_repeatableGroup_sameEntry_shouldStillIncrementIterationPerCall( )
    {
        Entry entry = buildEntry( 1, true );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        addGeolocParams( request, 1, 0, ADDRESS_1, ID_ADDRESS_1, X_1, Y_1, GEOMETRY_1 );
        addGeolocParams( request, 1, 1, ADDRESS_2, ID_ADDRESS_2, X_2, Y_2, GEOMETRY_1 );

        GenericAttributeError error0 = entryType.getResponseData( entry, request, new ArrayList<>( ), Locale.FRENCH );
        assertNull( "1ere occurrence du groupe répétable : ne doit pas être en erreur", error0 );

        GenericAttributeError error1 = entryType.getResponseData( entry, request, new ArrayList<>( ), Locale.FRENCH );
        assertNull( "2eme occurrence du groupe répétable : ne doit pas être en erreur (comportement préservé)", error1 );
    }

    public void testGetResponseData_mandatoryEntryWithoutAddress_returnsMandatoryError( )
    {
        Entry entry = buildEntry( 1, true );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        addGeolocParams( request, 1, 0, "", "", "", "", "" );

        GenericAttributeError error = entryType.getResponseData( entry, request, new ArrayList<>( ), Locale.FRENCH );

        assertTrue( "Une entrée obligatoire sans adresse doit renvoyer une MandatoryError", error instanceof MandatoryError );
    }

    public void testGetResponseData_onlyXProvidedWithoutAddress_returnsSpecifyBothXAndYError( )
    {
        Entry entry = buildEntry( 1, false );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        addGeolocParams( request, 1, 0, "", "", X_1, "", "" );

        GenericAttributeError error = entryType.getResponseData( entry, request, new ArrayList<>( ), Locale.FRENCH );

        assertFalse( "X renseigné sans Y et sans adresse doit renvoyer une erreur", error == null );
        assertFalse( "L'erreur attendue est 'préciser X et Y', pas une MandatoryError", error instanceof MandatoryError );
    }
}