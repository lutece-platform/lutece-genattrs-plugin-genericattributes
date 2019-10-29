/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
package fr.paris.lutece.plugins.genericattributes.util;

import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.GenericAttributesPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Utility class of plugin generic attributes
 */
public final class GenericAttributesUtils
{
    /**
     * Equals constant
     */
    public static final String CONSTANT_EQUAL = "=";

    /**
     * Value to represent a null id
     */
    public static final int CONSTANT_ID_NULL = -1;

    /**
     * Value for anonymized responses
     */
    public static final String CONSTANT_RESPONSE_VALUE_ANONYMIZED = "anonymized";
    private static final String REGEX_ID = "^[\\d]+$";

    /**
     * Private constructor
     */
    private GenericAttributesUtils( )
    {
        // Do nothing
    }

    /**
     * Return the field which title is specified in parameter
     * 
     * @param strTitle
     *            the title
     * @param listFields
     *            the list of fields
     * @return the field which title is specified in parameter
     */
    public static Field findFieldByTitleInTheList( String strTitle, List<Field> listFields )
    {
        if ( ( listFields == null ) || listFields.isEmpty( ) )
        {
            return null;
        }

        for ( Field field : listFields )
        {
            if ( StringUtils.isNotBlank( strTitle ) )
            {
                if ( StringUtils.equals( StringUtils.trim( strTitle ), StringUtils.trim( field.getTitle( ) ) ) )
                {
                    return field;
                }
            }
            else
                if ( StringUtils.isBlank( field.getTitle( ) ) )
                {
                    return field;
                }
        }

        return null;
    }

    /**
     * return the field which key is specified in parameter
     * 
     * @param nIdField
     *            the id of the field who is search
     * @param listField
     *            the list of field
     * @return the field which key is specified in parameter
     */
    public static Field findFieldByIdInTheList( int nIdField, List<Field> listField )
    {
        for ( Field field : listField )
        {
            if ( field.getIdField( ) == nIdField )
            {
                return field;
            }
        }

        return null;
    }

    /**
     * Gets the generic attributes plugin
     * 
     * @return the plugin
     */
    public static Plugin getPlugin( )
    {
        return PluginService.getPlugin( GenericAttributesPlugin.PLUGIN_NAME );
    }

    /**
     * Convert a string to int
     * 
     * @param strParameter
     *            the string parameter to convert
     * @return the conversion
     */
    public static int convertStringToInt( String strParameter )
    {
        int nIdParameter = -1;

        try
        {
            if ( ( strParameter != null ) && strParameter.matches( REGEX_ID ) )
            {
                nIdParameter = Integer.parseInt( strParameter );
            }
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        return nIdParameter;
    }
}
