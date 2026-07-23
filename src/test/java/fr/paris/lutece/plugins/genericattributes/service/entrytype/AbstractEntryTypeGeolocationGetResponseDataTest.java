package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Tests unitaires (JUnit 5) pour AbstractEntryTypeGeolocation#getResponseData.
 *
 * Pas de Spring / Mockito : Lutece v8 est full Jakarta et ne fournit plus
 * spring-test. On utilise donc un double de test écrit à la main pour
 * HttpServletRequest, ne couvrant que ce dont getResponseData a besoin
 * (getParameter / getAttribute / setAttribute / removeAttribute).
 *
 * IMPORTANT : FakeHttpServletRequest ci-dessous n'implémente QUE les
 * méthodes utiles au test. Selon la version exacte de la Jakarta Servlet
 * API utilisée (5.0 vs 6.0/6.1), il manquera probablement d'autres
 * méthodes abstraites à surcharger pour que la classe compile (ex:
 * getRequestId(), getServletConnection() sur les versions récentes).
 * Utilise "Implement Methods" (IntelliJ) sur la classe pour générer les
 * stubs manquants automatiquement (ils peuvent tous lever
 * UnsupportedOperationException, seuls getParameter/getAttribute/
 * setAttribute/removeAttribute doivent garder leur vraie implémentation
 * ci-dessous).
 */
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

    @BeforeEach
    void initEntryType( )
    {
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

    private void addGeolocParams( FakeHttpServletRequest request, int idEntry, int iteration, String address, String idAddress, String x, String y,
                                  String geometry )
    {
        String prefix = AbstractEntryTypeGeolocation.PARAMETER_PREFIX_ITERATION + iteration + "_" + IEntryTypeService.PREFIX_ATTRIBUTE + idEntry;

        request.setParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_ADDRESS, address );
        request.setParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_ID_ADDRESS, idAddress );
        request.setParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_X, x );
        request.setParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_Y, y );
        request.setParameter( prefix + AbstractEntryTypeGeolocation.PARAMETER_SUFFIX_GEOMETRY, geometry );
    }

    private Optional<String> responseValueForField( List<Response> listResponse, String fieldCode )
    {
        return listResponse.stream( )
                .filter( r -> r.getField( ) != null && fieldCode.equals( r.getField( ).getCode( ) ) )
                .map( Response::getResponseValue )
                .findFirst( );
    }

    @Test
    void testGetResponseData_singleEntry_firstCall_shouldSucceedAndReadCorrectParams( )
    {
        Entry entry = buildEntry( 1, true );
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        addGeolocParams( request, 1, 0, ADDRESS_1, ID_ADDRESS_1, X_1, Y_1, GEOMETRY_1 );

        List<Response> listResponse = new ArrayList<>( );
        GenericAttributeError error = entryType.getResponseData( entry, request, listResponse, Locale.FRENCH );

        assertNull( error, "Aucune erreur attendue : adresse complète fournie pour l'itération 0" );
        assertEquals( ADDRESS_1, responseValueForField( listResponse, AbstractEntryTypeGeolocation.FIELD_ADDRESS ).orElse( null ) );
        assertEquals( ID_ADDRESS_1, responseValueForField( listResponse, AbstractEntryTypeGeolocation.FIELD_ID_ADDRESS ).orElse( null ) );
    }

    @Test
    void testGetResponseData_multipleDistinctEntries_shouldNotShareIterationCounter( )
    {
        Entry entry1 = buildEntry( 1, true );
        Entry entry2 = buildEntry( 2, true );
        Entry entry3 = buildEntry( 3, true );

        FakeHttpServletRequest sharedRequest = new FakeHttpServletRequest( );
        addGeolocParams( sharedRequest, 1, 0, ADDRESS_1, ID_ADDRESS_1, X_1, Y_1, GEOMETRY_1 );
        addGeolocParams( sharedRequest, 2, 0, ADDRESS_2, ID_ADDRESS_2, X_2, Y_2, GEOMETRY_1 );
        addGeolocParams( sharedRequest, 3, 0, ADDRESS_1, ID_ADDRESS_1, X_1, Y_1, GEOMETRY_1 );

        List<Response> listResponse1 = new ArrayList<>( );
        List<Response> listResponse2 = new ArrayList<>( );
        List<Response> listResponse3 = new ArrayList<>( );

        GenericAttributeError error1 = entryType.getResponseData( entry1, sharedRequest, listResponse1, Locale.FRENCH );
        GenericAttributeError error2 = entryType.getResponseData( entry2, sharedRequest, listResponse2, Locale.FRENCH );
        GenericAttributeError error3 = entryType.getResponseData( entry3, sharedRequest, listResponse3, Locale.FRENCH );

        assertNull( error1, "Question 1 : comportement inchangé, ne doit pas être en erreur" );
        assertNull( error2, "Question 2 distincte : ne doit PAS échouer (c'était le bug)" );
        assertNull( error3, "Question 3 distincte : ne doit PAS échouer (c'était le bug)" );

        assertEquals( ADDRESS_1, responseValueForField( listResponse1, AbstractEntryTypeGeolocation.FIELD_ADDRESS ).orElse( null ) );
        assertEquals( ADDRESS_2, responseValueForField( listResponse2, AbstractEntryTypeGeolocation.FIELD_ADDRESS ).orElse( null ) );
        assertEquals( ADDRESS_1, responseValueForField( listResponse3, AbstractEntryTypeGeolocation.FIELD_ADDRESS ).orElse( null ) );
    }

    @Test
    void testGetResponseData_repeatableGroup_sameEntry_shouldStillIncrementIterationPerCall( )
    {
        Entry entry = buildEntry( 1, true );
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        addGeolocParams( request, 1, 0, ADDRESS_1, ID_ADDRESS_1, X_1, Y_1, GEOMETRY_1 );
        addGeolocParams( request, 1, 1, ADDRESS_2, ID_ADDRESS_2, X_2, Y_2, GEOMETRY_1 );

        GenericAttributeError error0 = entryType.getResponseData( entry, request, new ArrayList<>( ), Locale.FRENCH );
        assertNull( error0, "1ere occurrence du groupe répétable : ne doit pas être en erreur" );

        GenericAttributeError error1 = entryType.getResponseData( entry, request, new ArrayList<>( ), Locale.FRENCH );
        assertNull( error1, "2eme occurrence du groupe répétable : ne doit pas être en erreur (comportement préservé)" );
    }

    @Test
    void testGetResponseData_mandatoryEntryWithoutAddress_returnsMandatoryError( )
    {
        Entry entry = buildEntry( 1, true );
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        addGeolocParams( request, 1, 0, "", "", "", "", "" );

        GenericAttributeError error = entryType.getResponseData( entry, request, new ArrayList<>( ), Locale.FRENCH );

        assertTrue( error instanceof MandatoryError, "Une entrée obligatoire sans adresse doit renvoyer une MandatoryError" );
    }

    @Test
    void testGetResponseData_onlyXProvidedWithoutAddress_returnsSpecifyBothXAndYError( )
    {
        Entry entry = buildEntry( 1, false );
        FakeHttpServletRequest request = new FakeHttpServletRequest( );
        addGeolocParams( request, 1, 0, "", "", X_1, "", "" );

        GenericAttributeError error = entryType.getResponseData( entry, request, new ArrayList<>( ), Locale.FRENCH );

        assertFalse( error == null, "X renseigné sans Y et sans adresse doit renvoyer une erreur" );
        assertFalse( error instanceof MandatoryError, "L'erreur attendue est 'préciser X et Y', pas une MandatoryError" );
    }

    /**
     * Double de test minimal pour jakarta.servlet.http.HttpServletRequest.
     * Seules getParameter/getAttribute/setAttribute/removeAttribute sont
     * réellement implémentées (nécessaires pour getResponseData). Les
     * autres méthodes de l'interface ne sont PAS incluses ici : utilise
     * "Implement Methods" dans ton IDE pour les générer automatiquement,
     * chacune pouvant lever UnsupportedOperationException.
     */
    private static class FakeHttpServletRequest implements HttpServletRequest
    {
        private final Map<String, String> parameters = new HashMap<>( );
        private final Map<String, Object> attributes = new HashMap<>( );

        void setParameter( String name, String value )
        {
            parameters.put( name, value );
        }

        @Override
        public String getParameter( String name )
        {
            return parameters.get( name );
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return new String[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Map.of();
        }

        @Override
        public String getProtocol() {
            return "";
        }

        @Override
        public String getScheme() {
            return "";
        }

        @Override
        public String getServerName() {
            return "";
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return "";
        }

        @Override
        public String getRemoteHost() {
            return "";
        }

        @Override
        public Object getAttribute( String name )
        {
            return attributes.get( name );
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return "";
        }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return "";
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void setAttribute( String name, Object o )
        {
            attributes.put( name, o );
        }

        @Override
        public void removeAttribute( String name )
        {
            attributes.remove( name );
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return "";
        }

        @Override
        public String getLocalAddr() {
            return "";
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }

        @Override
        public String getRequestId() {
            return "";
        }

        @Override
        public String getProtocolRequestId() {
            return "";
        }

        @Override
        public ServletConnection getServletConnection() {
            return null;
        }

        @Override
        public String getAuthType() {
            return "";
        }

        @Override
        public Cookie[] getCookies() {
            return new Cookie[0];
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return "";
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return "";
        }

        @Override
        public String getPathInfo() {
            return "";
        }

        @Override
        public String getPathTranslated() {
            return "";
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public String getQueryString() {
            return "";
        }

        @Override
        public String getRemoteUser() {
            return "";
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return "";
        }

        @Override
        public String getRequestURI() {
            return "";
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getServletPath() {
            return "";
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return "";
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
            return false;
        }

        @Override
        public void login(String username, String password) throws ServletException {

        }

        @Override
        public void logout() throws ServletException {

        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return List.of();
        }

        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
            return null;
        }

        // TODO: laisser l'IDE générer ici les autres méthodes de
        // jakarta.servlet.http.HttpServletRequest (Implement Methods),
        // chacune pouvant simplement lever UnsupportedOperationException.
    }
}