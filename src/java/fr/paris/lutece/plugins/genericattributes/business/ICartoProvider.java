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
package fr.paris.lutece.plugins.genericattributes.business;

import java.io.Serializable;

import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 *
 * IMapProvider : map provider for Geolocation <br >
 * <ul>
 * <li><code>getKey(  )</code> must return the unique key.</li>
 * <li><code>getDisplayedName(  )</code> will be displayed in reference lists.</li>
 * </ul>
 */
public interface ICartoProvider extends Serializable
{
    /**
     * Gets the key. This key <b>must be unique</b>.
     * 
     * @return the key;
     */
    String getKey( );

    /**
     * Gets the displayed name
     * 
     * @return the displayed name
     */
    String getDisplayedName( );

    /**
     * Gets the html template
     * 
     * @return the html template
     */
    String getHtmlCode( );

    /**
     * Builds a new {@link ReferenceItem} for the map provider.<br >
     * <code>key == getKey(  )</code>, <code>value == getDisplayedName(  )</code>
     * 
     * @return the item created.
     */
    ReferenceItem toRefItem( );

    /**
     * returns the Parameter class contains all the parameters of the map
     * 
     * @return the Parameter
     */
    Object getParameter( int nKey );
    
    /**
     * returns the reference list contains all the map
     * 
     * @return the referencelist
     */
    ReferenceList getMapProvidersRefList( );
    
    /**
     * returns the json of the geolocitemPoint
     * 
     * @return the json of the geolocitemPoint
     */
    String getGeolocItemPoint( Double x, Double y, String adresse );
    
    /**
     * returns the json of the geolocitemPolygon
     * 
     * @return the json of the geolocitemPolygon
     */
    String getGeolocItemPolygon( String coordinate );
    
    /**
     * returns the json of the geolocitemPolyline
     * 
     * @return the json of the geolocitemPolyline
     */
    String getGeolocItemPolyline( String coordinate );
    
    /**
     * returns the solr tag
     * 
     * @return the solr tag
     */
    String getSolrTag( String strIdLayer );
}
