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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.IEntryAnonymizationType;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.fileupload.FileItem;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for entry type services
 */
public interface IEntryTypeService
{
    // parameters Entry
    String PARAMETER_MAX_FILES = "max_files";
    String PARAMETER_FILE_MAX_SIZE = "file_max_size";
    String PARAMETER_EXPORT_BINARY = "export_binary";
    String PARAMETER_ENTRY_CODE = "entry_code";
    String PARAMETER_TITLE = "title";
    String PARAMETER_HELP_MESSAGE = "help_message";
    String PARAMETER_COMMENT = "comment";
    String PARAMETER_MANDATORY = "mandatory";
    String PARAMETER_FIELD_IN_LINE = "field_in_line";
    String PARAMETER_HEIGHT = "height";
    String PARAMETER_WIDTH = "width";
    String PARAMETER_VALUE = "value";
    String PARAMETER_MAX_SIZE_ENTER = "max_size_enter";
    String PARAMETER_CONFIRM_FIELD = "confirm_field";
    String PARAMETER_CONFIRM_FIELD_TITLE = "confirm_field_title";
    String SUFFIX_CONFIRM_FIELD = "_confirm_field";
    String PREFIX_ATTRIBUTE = "attribute";
    String PREFIX_ITERATION_ATTRIBUTE = "nIt";
    String PARAMETER_UNIQUE = "unique_field";
    String PARAMETER_CSS_CLASS = "css_class";
    String PARAMETER_ERROR_MESSAGE = "errorMessage";
    String PARAMETER_NUMBER_ROWS = "num_row";
    String PARAMETER_NUMBER_COLUMNS = "num_column";
    String PARAMETER_ONLY_DISPLAY_IN_BACK = "only_display_in_back";
    String PARAMETER_MAX_IMAGE_SIZE = "maxImageSize";
    String PARAMETER_IMAGE_TYPE = "image_type";
    String PARAMETER_INDEXED = "is_indexed";
    String PARAMETER_EXPORTABLE = "exportable";
    String PARAMETER_EXPORTABLE_PDF = "exportable_pdf";
    String PARAMETER_PUBLISHED = "published";
    String PARAMETER_DISPLAY_BO = "display_in_bo";
    String PARAMETER_FILE = "file";
    String PARAMETER_MIN = "min";
    String PARAMETER_MAX = "max";
    String PARAMETER_ANONYMIZABLE = "anonymizable";
    String PARAMETER_ANONYMIZE_PATTERN = "anonymize_pattern";
    String PARAMETER_USE_REF_LIST = "use_ref_list";
    String PARAMETER_REF_LIST_SELECT = "select_ref_list";
    String PARAMETER_PLACEHOLDER = "placeholder";
    String PARAMETER_ILLUSTRATION_IMAGE = "illustration_image";
    String PARAMETER_DISABLED = "disabled";
    String PARAMETER_TODAY_DATE = "default_today_date";
    String PARAMETER_SORTABLE_LIST_TYPE = "sortable_list_type";
    String PARAMETER_AUTOCOMPLETE = "autocomplete";

    // Fields codes
    String FIELD_DATE_VALUE = "default_date_value";
    String FIELD_PROVIDER = "provider";
    String FIELD_EDIT_MODE = "editMode";
    String FIELD_VIEW_NUMBER = "viewNumber";
    String FIELD_ID_ADDRESS = "idAddress";
    String FIELD_ADDRESS = "address";
    String FIELD_ADDITIONAL_ADDRESS = "additionalAddress";
    String FIELD_X = "X";
    String FIELD_Y = "Y";
    String FIELD_GEOMETRY = "geometry";
    String FIELD_PREFIX = "prefix";
    String FIELD_SUFFIX = "suffix";
    String FIELD_FILE_MAX_SIZE = "file_max_size";
    String FIELD_MAX_FILES = "max_files";
    String FIELD_FILE_BINARY = "export_binary";
    String FIELD_FILE_TYPE = "file_type";
    String FIELD_ANSWER_CHOICE = "answer_choice";
    String FIELD_ARRAY_CELL = "array_cell";
    String FIELD_ATTRIBUTE_NAME = "attribute_name";
    String FIELD_CONFIRM = "confirm_field";
    String FIELD_MYLUTECE_ATTRIBUTE_NAME_CODE = "attribute_name";
    String FIELD_ARRAY_ROW = "array_row";
    String FIELD_ARRAY_COLUMN = "array_column";
    String FIELD_IMAGE_TYPE = "image_type";
    String FIELD_WIDTH = "width";
    String FIELD_HEIGHT = "height";
    String FIELD_MAX = "max";
    String FIELD_MIN = "min";
    String FIELD_MAX_SIZE = "max_size";
    String FIELD_TEXT_CONF = "text_config";
    String FIELD_EXPORTABLE = "exportable";
    String FIELD_EXPORTABLE_PDF = "exportable_pdf";
    String FIELD_PUBLISHED = "published";
    String FIELD_RICHTEXT = "richtext";
    String FIELD_DISPLAY_BO = PARAMETER_DISPLAY_BO;
    String FIELD_DOWNLOADABLE_FILE = "downloadable_file";
    String FIELD_USE_REF_LIST = "use_ref_list";
    String FIELD_ANONYMIZABLE = "anonymizable";
    String FIELD_PLACEHOLDER = "placeholder";
    String FIELD_ILLUSTRATION_IMAGE = "illustration_image";
    String FIELD_GEOJSON = "coordinates_geojson";
    String FIELD_ID_LAYER = "DataLayer";
	String FIELD_DISABLED = "disabled";
    String FIELD_IS_UPDATABLE = "is_updatable";
    String FIELD_SORTABLE_LIST_TYPE = "sortable_list_type";
    String FIELD_AUTOCOMPLETE = "autocomplete";

    // attribute
    String ATTRIBUTE_RESPONSE_ITERATION_NUMBER = "response_iteration_number";

    // message
    String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryField";
    String MESSAGE_NUMERIC_FIELD = "genericattributes.message.numeric.field";
    String MESSAGE_CONFIRM_FIELD = "genericattributes.message.errorConfirmField";
    String MESSAGE_UNIQUE_FIELD = "genericattributes.message.errorUniqueField";
    String MESSAGE_XSS_FIELD = "genericattributes.message.errorXssField";
    String MESSAGE_MAXLENGTH = "genericattributes.message.maxLength";
    String MESSAGE_INVALID_SQL_QUERY = "genericattributes.message.invalidSqlQuery";
    String MESSAGE_MYLUTECE_AUTHENTIFICATION_REQUIRED = "genericattributes.message.myLuteceAuthentificationRequired";
    String ERROR_FIELD_TITLE = "genericattributes.createEntry.labelTitle";
    String ERROR_FIELD_MAX_FILES = "genericattributes.createEntry.labelMaxFiles";
    String ERROR_FIELD_FILE_MAX_SIZE = "genericattributes.createEntry.labelFileMaxSize";
    String FIELD_INSERT_GROUP = "genericattributes.labelInsertGroup";
    String FIELD_COMMENT = "genericattributes.createEntry.labelComment";
    String ERROR_FIELD_WIDTH = "genericattributes.createEntry.labelWidth";
    String ERROR_FIELD_HEIGHT = "genericattributes.createEntry.labelHeight";
    String FIELD_MAX_SIZE_ENTER = "genericattributes.createEntry.labelMaxSizeEnter";
    String FIELD_NUMBER_ROWS = "genericattributes.createEntry.labelNumberRows";
    String FIELD_NUMBER_COLUMNS = "genericattributes.createEntry.labelNumberColumns";
    String FIELD_CONFIRM_FIELD_TITLE = "genericattributes.createEntry.labelConfirmFieldTitle";
    String ERROR_FIELD_FILE_TYPE = "genericattributes.createEntry.fileType";
    String ERROR_FIELD_REF_LIST = "genericattributes.createEntry.labelUseRefListSelect";

    String MESSAGE_ERROR_IMPOSSIBLE_SLOT = "genericattributes.message.error.impossibleSlot";

    String MESSAGE_ERROR_SLOT = "genericattributes.message.error.slot";

    /**
     * Get the template to display the creation or modification form of an entry of this entry type
     * 
     * @param entry
     *            The entry
     * @param bDisplayFront
     *            True if the template will be used to display content in Front Office, false if it will be used to display content in back office
     * @return the HtmlCode of the entry
     */
    String getTemplateHtmlForm( Entry entry, boolean bDisplayFront );

    /**
     * Get template create URL of the entry
     * 
     * @param entry
     *            The entry
     * @param bDisplayFront
     *            True if the template will be used to display content in Front Office, false if it will be used to display content in back office
     * @return template create URL of the entry
     */
    String getTemplateCreate( Entry entry, boolean bDisplayFront );

    /**
     * Get the template modify URL of the entry
     * 
     * @param entry
     *            The entry
     * @param bDisplayFront
     *            True if the template will be used to display content in Front Office, false if it will be used to display content in back office
     * @return template modify URL of the entry
     */
    String getTemplateModify( Entry entry, boolean bDisplayFront );

    /**
     * Get the request data
     * 
     * @param entry
     *            The entry
     * @param request
     *            HttpRequest
     * @param locale
     *            the locale
     * @return null if all data required are in the request else the url of jsp error
     */
    String getRequestData( Entry entry, HttpServletRequest request, Locale locale );

    /**
     * Generate the list of responses associated with the given entry from the request, and saved it into the Entry object.
     * 
     * @param entry
     *            The entry
     * @param request
     *            HttpRequest
     * @param listResponse
     *            the list of response associate to the entry. Newly created response will be inserted in this list.
     * @param locale
     *            the locale
     * @return a GenericAttributeError object if there is an error in the response
     */
    GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale );

    /**
     * Get the list of regular expression who is use in the template modify of the entry
     * 
     * @param entry
     *            the entry
     * @param plugin
     *            the plugin
     * @return the regular expression list who is use in the template modify of the entry
     */
    ReferenceList getReferenceListRegularExpression( Entry entry, Plugin plugin );

    /**
     * Get the response value associate to the entry to export in the file export
     * 
     * @param entry
     *            The entry
     * @param response
     *            the response associate to the entry
     * @param locale
     *            the locale
     * @param request
     *            the request
     * @return the response value associate to the entry to export in the file export
     */
    String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale );

    /**
     * Get the response value associate to the entry to write in the recap
     * 
     * @param entry
     *            The entry
     * @param response
     *            the response associate to the entry
     * @param locale
     *            the locale
     * @param request
     *            the request
     * @return the response value associate to the entry to write in the recap
     */
    String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale );

    /**
     * Check if the file can be uploaded or not. This method will check the size of each file and the number max of files that can be uploaded.
     * 
     * @param entry
     *            The entry
     * @param listUploadedFileItems
     *            the list of uploaded files
     * @param listFileItemsToUpload
     *            the list of files to upload
     * @param locale
     *            the locale
     * @return The error if there is any
     */
    GenericAttributeError canUploadFiles( Entry entry, List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload, Locale locale );

    /**
     * Sets the string value of the response
     * 
     * @param entry
     *            The entry
     * @param response
     *            the response
     * @param locale
     *            the locale - will use a default one if not specified
     */
    void setResponseToStringValue( Entry entry, Response response, Locale locale );

    /**
     * Return the iteration number for a response from the request
     * 
     * @param request
     *            the request to retrieve the iteration number of the response from
     * @return the value of the iteration number
     */
    int getResponseIterationValue( HttpServletRequest request );

    /**
     * Return the template associated to an EntryType for read only uses
     * 
     * @return the template associated to an EntryType for read only uses
     */
    String getTemplateEntryReadOnly( );

    /**
     * Return the template associated to an EntryType for read only uses
     * 
     * @param bDisplayFront
     *            true
     * @return the template associated to an EntryType for read only uses
     */
    String getTemplateEntryReadOnly( boolean bDisplayFront );

    /**
     * Creates the help message for the enrty anonymisation config.
     * 
     * @param locale
     * @return
     */
    String getAnonymizationHelpMessage( Locale locale );

    /**
     * get the list of valids wildcard for the entry
     * 
     * @return
     */
    List<IEntryAnonymizationType> getValidWildcards( );
}
