package fr.paris.lutece.plugins.genericattributes.service.listener;

import java.util.List;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.service.listener.IFileTransferListener;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;

public class FileTransferListenerService implements IFileTransferListener
{

    private static String contextValue = AppPropertiesService.getProperty( "genericattributes.filetransfercontext" );
    @Override
    public void changeFileService( FileTransferRequest fileTransferRequest )
    {
        if ( fileTransferRequest == null )
        {
            return;
        }
        else if( !fileTransferRequest.getRequestContext().equals(contextValue) )
        {
            return;
        }
        else {
            ResponseFilter responseFilter = new ResponseFilter();
            List<Response> responseList = ResponseHome.getResponseList( responseFilter );

            Response response = responseList.stream().filter( r -> r.getFile().getFileKey().equals( fileTransferRequest.getOldFileKey() ) ).findFirst().orElse( null );

            
            if ( response != null )
            {
                response.getFile().setFileKey( fileTransferRequest.getNewFileKey() );
                response.getFile().setOrigin( fileTransferRequest.getTargetFileserviceproviderName() );

                ResponseHome.update( response );
            }
        }
    }

    @Override
    public String getName()
    {
        return "FileTransferListenerService";
    }
}