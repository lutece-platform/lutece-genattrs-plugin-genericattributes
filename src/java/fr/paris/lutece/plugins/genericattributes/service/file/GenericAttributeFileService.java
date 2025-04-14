/*
 * Copyright (c) 2002-2024, City of Paris
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
package fr.paris.lutece.plugins.genericattributes.service.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import jakarta.enterprise.inject.spi.CDI;

public class GenericAttributeFileService
{
	private static final String PROPERTY_FILESTORESERVICE_PREFIX = "genericattributes.filestoreservice";
	private static final String PROPERTY_FILESTORESERVICE_DEFAULT_SUFFIX = "default";

    private static final GenericAttributeFileService _instance = new GenericAttributeFileService( );
    private static Map<String,String> _entryTypeFileServices;
    private FileService _fileService;
    
    /**
     * Constructor
     */
    private GenericAttributeFileService( )
    {
        _fileService = CDI.current( ).select( FileService.class ).get( );
    	List<String> keyList = AppPropertiesService.getKeys( PROPERTY_FILESTORESERVICE_PREFIX);
    	
    	 _entryTypeFileServices = new HashMap<>();
    	 
    	// init specific entryType fileStoreService names if exists
    	 if (keyList != null ) 
    	 {
	    	keyList.stream().forEach( s -> { 
	    		if ( !StringUtils.isAllBlank(  AppPropertiesService.getProperty(s) ) )
	    		{
	    			_entryTypeFileServices.put(s, AppPropertiesService.getProperty(s)); 
	    		}
	    	} );
    	 }
    }
    
    /**
     * get FileStoreServiceProvider name for entry type :
     * - returns the entry type FileService (if set)
     * - otherwise, returns the GenAttr default FileService (if set)
     * - otherwise, returns the lutece default FileService 
     * 
     * @param strEntryType
     * @return the name of the FileStoreServiceProvider
     */
    public String getFileStoreProviderName( String strEntryType )
    {
    	if (strEntryType != null ) 
    	{
    		if ( _entryTypeFileServices.containsKey( strEntryType ))
    		{
    			return _entryTypeFileServices.get( strEntryType );
    		}
    	}
    	
    	if ( _entryTypeFileServices.containsKey( PROPERTY_FILESTORESERVICE_DEFAULT_SUFFIX ))
		{
			return _entryTypeFileServices.get( PROPERTY_FILESTORESERVICE_DEFAULT_SUFFIX );
		}
    	
    	return _fileService.getFileStoreServiceProvider( ).getName( );
    }
    
    /**
     * get FileStoreServiceProvider name  :
     * - returns the GenAttr default FileService (if set)
     * - otherwise, returns the lutece default FileService 
     * 
     * Use getFileStoreProviderName( String strEntryType ) to get entryType specific File service
     * 
     * @return the name of the FileStoreServiceProvider
     */
    public String getFileStoreProviderName(  )
    {
    	return getFileStoreProviderName( null );
    }

    /**
     * get instance of service
     * 
     * @return the instance
     */
    public static GenericAttributeFileService getInstance( )
    {
        return _instance;
    }

    /**
     * Save a file
     * 
     * @param file The lutece file in default generic file Service
     * @return The key of the file
     */
    public String save( File file)
    {
    	return save( file, null);
    }
    /**
     * Save a file
     * 
     * @param file The lutece file
     * @param strEntryType the entry type
     * @return The key of the file
     */
    public String save( File file, String strEntryType)
    {
        try
        {
        	if ( file.getOrigin( ) == null )
        	{
        		file.setOrigin( getFileStoreProviderName( strEntryType ) );
        	}
        	
        	return _fileService.getFileStoreServiceProvider( file.getOrigin( ) ).storeFile( file );
        }
        catch( FileServiceException e )
        {
            AppLogService.error( e );
            return null;
        }
    }

    /**
     * Save a file
     * 
     * @param file The multipartItem
     * @param strEntryType the entry type
     * @return The key of the file
     */
    public String save( MultipartItem file, String strEntryType)
    {
        try
        {        	
        	return _fileService.getFileStoreServiceProvider( getFileStoreProviderName( strEntryType )  ).storeFileItem( file );
        }
        catch( FileServiceException e )
        {
            AppLogService.error( e );
            return null;
        }
    }
    
    /**
     * Load a file
     * 
     * @param strKey
     *            The key of the file
     * @return The file
     */
    public File load( String strKey, String strOrigin )
    {
        try
        {
        	if ( StringUtils.isEmpty( strOrigin ) )
        	{
        		strOrigin =  getFileStoreProviderName( ) ;
        	}
        	
            return _fileService.getFileStoreServiceProvider( strOrigin ).getFile( strKey );
        }
        catch( FileServiceException e )
        {
            AppLogService.error( e );
            return null;
        }
    }

    /**
     * Delete a file
     * 
     * @param strKey
     *            The key of the file
     */
    public void delete( String strKey, String strOrigin )
    {
        try
        {
        	if ( StringUtils.isEmpty( strOrigin ) )
        	{
        		strOrigin =  getFileStoreProviderName( );
        	}
        	
            _fileService.getFileStoreServiceProvider( strOrigin ).delete( strKey );
        }
        catch( FileServiceException e )
        {
            AppLogService.error( e );
        }
    }

    /**
     * Update a file
     *
     * @param file The lutece file
     * @param strEntryType the entry type
     * @return The key of the file
     */
    public String update( File file, String strEntryType )
    {
        try
        {
            if ( file.getOrigin( ) == null )
            {
                file.setOrigin( getFileStoreProviderName( strEntryType ) );
            }

            String strOldFileKey = file.getFileKey( );
            String strNewFileKey = _fileService.getFileStoreServiceProvider( file.getOrigin( ) ).storeFile( file );

            _fileService.getFileStoreServiceProvider( file.getOrigin( ) ).delete( strOldFileKey );

            return strNewFileKey;
        }
        catch( FileServiceException e )
        {
            AppLogService.error( e );
            return null;
        }
    }

}
