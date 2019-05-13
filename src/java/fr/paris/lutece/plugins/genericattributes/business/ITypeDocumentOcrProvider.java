/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.io.Serializable;

import java.util.List;

/**
 *
 * ITypeDocumentOcrProvider : type document provider for automatic reading of documents<br/>
 * <ul>
 * <li><code>getKey(  )</code> must return the unique key.</li>
 * <li><code>getDisplayedName(  )</code> will be displayed in reference lists.</li>
 * </ul>
 */
public interface ITypeDocumentOcrProvider extends Serializable
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
     * Builds a new {@link ReferenceItem} for the type document provider.<br />
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
     * Gets the list field.
     *
     * @return the list field
     */
    ReferenceList getListField();
    
    /**
     * Gets the field by id.
     *
     * @param idField the id field
     * @return the field by id
     */
    ReferenceItem getFieldById(int idField);
    
    /**
     * Gets the authorized entry type for the document type.
     *
     * @return the authorized type
     */
    List<Integer> getAuthorizedEntryType();
}