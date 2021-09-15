package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * {@link IEntryTypeAnonymisationService} that replace the file with an empty file.
 */
public class FileReplaceAnonymizationService extends AbstractAnonymizationService
{

    private static final String FILES_DIRECTORY = "/WEB-INF/anonymization/";
    
    @Override
    public void anonymizeResponse( Entry entry, Response response, boolean first )
    {
        if ( isAnonymizable( entry ) )
        {
            String pattern = getPattern( entry );
            if ( pattern.contains( _wildcard ) && response.getFile( ) != null )
            {
                File responseFile = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );
                String fileType = FilenameUtils.getExtension( responseFile.getTitle( ) );
                
                try
                {
                    java.io.File emptyFile = getEmptyFile( "empty." + fileType );
                    responseFile.setTitle( emptyFile.getName( ) );
                    responseFile.setSize( (int) emptyFile.length( ) );
                    responseFile.getPhysicalFile( ).setValue( FileUtils.readFileToByteArray( emptyFile ) );
                    response.setFile( responseFile );
                }
                catch (IOException e)
                {
                    AppLogService.error( "Error while replacing file", e );
                    FileHome.remove( response.getFile( ).getIdFile( ) );
                    response.setFile( null );
                }
            }
        }
    }
    
    
    private java.io.File getEmptyFile( String filename )
    {
        Path path = Paths.get( AppPathService.getWebAppPath( ), FILES_DIRECTORY, filename );
        
        if ( !path.toFile( ).exists( ) )
        {
            path = Paths.get( AppPathService.getWebAppPath( ), FILES_DIRECTORY, "empty.txt" );
        }
        return path.toFile( );
    }
}
