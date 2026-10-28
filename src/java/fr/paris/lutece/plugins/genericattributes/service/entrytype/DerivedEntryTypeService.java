/*
 * Copyright (c) 2002-2025, City of Paris
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
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.util.RegularExpressionUtils;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service managing the entry types derived from another one: they use the entry type service of their parent and add a regular expression control
 */
@ApplicationScoped
public class DerivedEntryTypeService
{
    public static final int MAX_LENGTH_TITLE = 255;
    public static final int MAX_LENGTH_REGEX_PATTERN = 1024;
    public static final int MAX_LENGTH_REGEX_ERROR_MESSAGE = 512;

    private static final String MESSAGE_ERROR_TITLE_MANDATORY = "genericattributes.manageEntryType.message.error.titleMandatory";
    private static final String MESSAGE_ERROR_TITLE_TOO_LONG = "genericattributes.manageEntryType.message.error.titleTooLong";
    private static final String MESSAGE_ERROR_REGEX_MANDATORY = "genericattributes.manageEntryType.message.error.regexMandatory";
    private static final String MESSAGE_ERROR_REGEX_TOO_LONG = "genericattributes.manageEntryType.message.error.regexTooLong";
    private static final String MESSAGE_ERROR_REGEX_MESSAGE_TOO_LONG = "genericattributes.manageEntryType.message.error.regexErrorMessageTooLong";
    private static final String MESSAGE_ERROR_REGEX_INVALID = "genericattributes.manageEntryType.message.error.regexInvalid";

    /**
     * An error found by the validation of an entry type: the key of its message, and its argument
     */
    public record ValidationError( String messageKey, String argument )
    {
    }

    /**
     * Tell whether the entry type service of an entry type checks the regular expression of its entry type
     * 
     * @param entryType
     *            the entry type
     * @return true if a regular expression can be set on a type derived from this entry type
     */
    public boolean isRegularExpressionSupported( EntryType entryType )
    {
        IEntryTypeService entryTypeService = EntryTypeServiceManager.findEntryTypeService( entryType );

        return entryTypeService != null && entryTypeService.isRegularExpressionSupported( );
    }

    /**
     * Validate an entry type before saving it
     * 
     * @param entryType
     *            the entry type
     * @return the errors, empty if the entry type is valid
     */
    public List<ValidationError> validate( EntryType entryType )
    {
        List<ValidationError> listErrors = new ArrayList<>( );

        if ( StringUtils.isBlank( entryType.getTitle( ) ) )
        {
            listErrors.add( new ValidationError( MESSAGE_ERROR_TITLE_MANDATORY, null ) );
        }
        else
            if ( entryType.getTitle( ).length( ) > MAX_LENGTH_TITLE )
            {
                listErrors.add( new ValidationError( MESSAGE_ERROR_TITLE_TOO_LONG, String.valueOf( MAX_LENGTH_TITLE ) ) );
            }

        if ( entryType.isDerived( ) )
        {
            if ( StringUtils.isBlank( entryType.getRegexPattern( ) ) )
            {
                listErrors.add( new ValidationError( MESSAGE_ERROR_REGEX_MANDATORY, null ) );
            }
            else
                if ( entryType.getRegexPattern( ).length( ) > MAX_LENGTH_REGEX_PATTERN )
                {
                    listErrors.add( new ValidationError( MESSAGE_ERROR_REGEX_TOO_LONG, String.valueOf( MAX_LENGTH_REGEX_PATTERN ) ) );
                }
                else
                {
                    String strSyntaxError = RegularExpressionUtils.getSyntaxError( entryType.getRegexPattern( ) );

                    if ( strSyntaxError != null )
                    {
                        listErrors.add( new ValidationError( MESSAGE_ERROR_REGEX_INVALID, strSyntaxError ) );
                    }
                }

            if ( entryType.getRegexErrorMessage( ) != null && entryType.getRegexErrorMessage( ).length( ) > MAX_LENGTH_REGEX_ERROR_MESSAGE )
            {
                listErrors.add( new ValidationError( MESSAGE_ERROR_REGEX_MESSAGE_TOO_LONG, String.valueOf( MAX_LENGTH_REGEX_ERROR_MESSAGE ) ) );
            }
        }

        return listErrors;
    }

    /**
     * Build an entry type derived from a parent one, not saved yet
     * 
     * @param entryTypeParent
     *            the parent entry type
     * @param strTitle
     *            the title
     * @param strIconName
     *            the icon
     * @param strRegexPattern
     *            the regular expression
     * @param strRegexErrorMessage
     *            the error message, null for the generic one
     * @return the derived entry type
     */
    public EntryType newDerivedEntryType( EntryType entryTypeParent, String strTitle, String strIconName, String strRegexPattern,
            String strRegexErrorMessage )
    {
        EntryType entryType = new EntryType( );
        entryType.setTitle( strTitle );
        entryType.setIconName( strIconName );
        entryType.setRegexPattern( strRegexPattern );
        entryType.setRegexErrorMessage( StringUtils.trimToNull( strRegexErrorMessage ) );
        entryType.setIdTypeParent( entryTypeParent.getIdType( ) );
        entryType.setBeanName( entryTypeParent.getBeanName( ) );
        entryType.setGroup( entryTypeParent.getGroup( ) );
        entryType.setComment( entryTypeParent.getComment( ) );
        entryType.setMyLuteceUser( entryTypeParent.getMyLuteceUser( ) );
        entryType.setPlugin( entryTypeParent.getPlugin( ) );
        entryType.setInactive( false );

        return entryType;
    }

    /**
     * Save a new derived entry type, last of the entry types of its plugin
     * 
     * @param entryType
     *            the derived entry type
     * @return the saved entry type
     */
    public EntryType create( EntryType entryType )
    {
        entryType.setOrder( EntryTypeHome.getMaxOrder( entryType.getPlugin( ) ) + 1 );

        return EntryTypeHome.create( entryType );
    }

    /**
     * Find the local entry type matching an entry type read from an export made on another instance. The ids of the derived entry types differ from one
     * instance to the other: the entry type is matched by its entry type service and its regular expression, and a derived entry type missing here is
     * created.
     * 
     * @param importedEntryType
     *            the entry type of the export
     * @return the local entry type, or the imported one when nothing local answers its entry type service
     */
    public EntryType resolveImportedEntryType( EntryType importedEntryType )
    {
        if ( importedEntryType == null || importedEntryType.getBeanName( ) == null || importedEntryType.getPlugin( ) == null )
        {
            return importedEntryType;
        }

        EntryType local = EntryTypeHome.findByPrimaryKey( importedEntryType.getIdType( ) );

        if ( local != null && isEquivalent( local, importedEntryType ) )
        {
            return local;
        }

        List<EntryType> listLocal = EntryTypeHome.getList( importedEntryType.getPlugin( ) );
        Optional<EntryType> equivalent = listLocal.stream( ).filter( et -> isEquivalent( et, importedEntryType ) ).findFirst( );

        if ( equivalent.isPresent( ) )
        {
            return equivalent.get( );
        }

        Optional<EntryType> base = listLocal.stream( )
                .filter( et -> !et.isDerived( ) && importedEntryType.getBeanName( ).equals( et.getBeanName( ) ) ).findFirst( );

        if ( base.isEmpty( ) )
        {
            AppLogService.error( "No entry type of the plugin {} uses the bean {} : the imported entry keeps the entry type {}", importedEntryType.getPlugin( ),
                    importedEntryType.getBeanName( ), importedEntryType.getIdType( ) );
            return importedEntryType;
        }

        if ( StringUtils.isBlank( importedEntryType.getRegexPattern( ) ) || RegularExpressionUtils.getSyntaxError( importedEntryType.getRegexPattern( ) ) != null )
        {
            return base.get( );
        }

        EntryType created = newDerivedEntryType( base.get( ), importedEntryType.getTitle( ), importedEntryType.getIconName( ),
                importedEntryType.getRegexPattern( ), importedEntryType.getRegexErrorMessage( ) );

        if ( !validate( created ).isEmpty( ) )
        {
            return base.get( );
        }

        AppLogService.info( "Entry type {} created to import an entry of the entry type {}", created.getTitle( ), importedEntryType.getIdType( ) );

        return create( created );
    }

    private static boolean isEquivalent( EntryType local, EntryType imported )
    {
        return Objects.equals( local.getBeanName( ), imported.getBeanName( ) ) && Objects.equals( local.getPlugin( ), imported.getPlugin( ) )
                && Objects.equals( StringUtils.trimToNull( local.getRegexPattern( ) ), StringUtils.trimToNull( imported.getRegexPattern( ) ) );
    }
}
