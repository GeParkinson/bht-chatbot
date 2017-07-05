package de.bht.beuthbot.attachments;

import de.bht.beuthbot.attachments.model.AttachmentStoreMode;
import org.apache.http.HttpStatus;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * @Author: Christopher Kümmel on 6/28/2017.
 */
@Path("/attachments")
public class AttachmentService {

    /** Injected AttachmentStore */
    @Inject
    private AttachmentStore attachmentStore;

    /**
     * filehandler for HTTP GET audio requests
     * @param fileId to identify file
     * @param ext file extension
     * @return .mpg audio file
     */
    @GET
    @Path("/{directory:(audio|voice)*}/{fileID}.{ext}")
    @Produces({"audio/mpeg"})
    public Response downloadAudioFile(@PathParam("fileID") final Long fileId, @PathParam("ext") final String ext) {
        File file = new File(attachmentStore.loadAttachmentPath(fileId, AttachmentStoreMode.LOCAL_PATH));
        if (file.exists()) {
            Response.ResponseBuilder response = Response.ok((Object) file);
            response.header("Content-Disposition", "attachment;filename=" + String.valueOf(fileId)); //+ "." + ext);
            return response.build();
        } else {
            return Response.status(HttpStatus.SC_NO_CONTENT).build();
        }
    }
}
