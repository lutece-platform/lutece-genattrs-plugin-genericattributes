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
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.IEntryAnonymizationType;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.plugins.genericattributes.util.RegularExpressionUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.util.ReferenceList;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Abstract implementation of IEntryTypeService
 */
public abstract class EntryTypeService implements IEntryTypeService
{
    private List<IEntryAnonymizationType> _anonymizationTypes = new ArrayList<>( );

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale, String errorReturnUrl )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getReferenceListRegularExpression( Entry entry, Plugin plugin )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponseToStringValue( Entry entry, Response response, Locale locale )
    {
        response.setToStringValueResponse( response.getResponseValue( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError canUploadFiles( Entry entry, List<MultipartItem> listUploadedFileItems, List<MultipartItem> listFileItemsToUpload, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResponseIterationValue( HttpServletRequest request )
    {
        int nIterationValue = NumberUtils.INTEGER_MINUS_ONE;

        Object objAttributeResponseValue = request.getAttribute( ATTRIBUTE_RESPONSE_ITERATION_NUMBER );
        if ( objAttributeResponseValue != null )
        {
            nIterationValue = (int) objAttributeResponseValue;
        }

        return nIterationValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateEntryReadOnly( )
    {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateEntryReadOnly( boolean bIsDisplayFront )
    {
        return StringUtils.EMPTY;
    }

    /**
     * Initialize the Fields common to every Generic Attribute Entry
     * 
     * @param entry
     *            The Entry being processed
     * @param request
     *            The HTTP request
     */
    protected void initCommonRequestData( Entry entry, HttpServletRequest request )
    {
        if ( entry.getFields( ) == null )
        {
            entry.setFields( new ArrayList<>( ) );
        }
        String strExportable = request.getParameter( PARAMETER_EXPORTABLE );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_EXPORTABLE, null, String.valueOf( strExportable != null ) );

        String strExportablePDF = request.getParameter( PARAMETER_EXPORTABLE_PDF );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_EXPORTABLE_PDF, null, String.valueOf( strExportablePDF != null ) );

        String strAnonymizable = request.getParameter( PARAMETER_ANONYMIZABLE );
        String strAnonymizePattern = request.getParameter( PARAMETER_ANONYMIZE_PATTERN );

        GenericAttributesUtils.createOrUpdateField( entry, FIELD_ANONYMIZABLE, strAnonymizePattern, String.valueOf( strAnonymizable != null ) );

        String strDisabled = request.getParameter( PARAMETER_DISABLED );
        GenericAttributesUtils.createOrUpdateField( entry, IEntryTypeService.FIELD_DISABLED, null, String.valueOf( strDisabled != null ) );

        String strToDayDate = request.getParameter( PARAMETER_TODAY_DATE );
        GenericAttributesUtils.createOrUpdateField( entry, PARAMETER_TODAY_DATE, null, String.valueOf( strToDayDate != null ) );


    }

    /**
     * @param anonymizationTypes
     *            the anonymizationTypes to set
     */
    public void setAnonymizationTypes( List<IEntryAnonymizationType> anonymizationTypes )
    {
        _anonymizationTypes = new ArrayList<>( anonymizationTypes );
    }

    @Override
    public List<IEntryAnonymizationType> getValidWildcards( )
    {
        return new ArrayList<>( _anonymizationTypes );
    }

    @Override
    public String getAnonymizationHelpMessage( Locale locale )
    {
        return getValidWildcards( ).stream( ).map( w -> w.getHelpMessage( locale ) ).collect( Collectors.joining( "," ) );
    }

    /**
     * Check a response value against the regular expression of the entry type of the entry. A blank value, or an entry type without regular expression, is
     * always valid: the mandatory control is the job of the entry type service. The value is matched without its surrounding spaces, as the front office
     * does. A value the expression cannot settle in time is refused: the answer comes from an anonymous user, the expression must not let it through
     * by being slow.
     * 
     * @param entry
     *            the entry
     * @param strValue
     *            the response value
     * @param locale
     *            the locale
     * @return the error if the value does not match the regular expression, null otherwise
     */
    protected GenericAttributeError checkEntryTypeRegularExpression( Entry entry, String strValue, Locale locale )
    {
        EntryType entryType = entry.getEntryType( );

        if ( entryType == null || StringUtils.isBlank( entryType.getRegexPattern( ) ) || StringUtils.isBlank( strValue ) )
        {
            return null;
        }

        if ( RegularExpressionUtils.matches( entryType.getRegexPattern( ), strValue.trim( ) ) == RegularExpressionUtils.MatchResult.MATCH )
        {
            return null;
        }

        GenericAttributeError error = new GenericAttributeError( );
        error.setMandatoryError( false );
        error.setTitleQuestion( entry.getTitle( ) );

        if ( StringUtils.isNotBlank( entryType.getRegexErrorMessage( ) ) )
        {
            // The templates display the error message as HTML: the message an administrator typed is text
            error.setErrorMessage( StringEscapeUtils.escapeHtml4( entryType.getRegexErrorMessage( ) ) );
        }
        else
        {
            error.setErrorMessage( I18nService.getLocalizedString( MESSAGE_REGEX_FIELD, new String [ ] {
                    entry.getTitle( )
            }, locale ) );
        }

        return error;
    }
}
