package de.bht.beuthbot.attachments;

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

    /**
     * filehandler for HTTP GET requests
     * @param fileId
     * @return .mpg audio file
     */
    @GET
    @Path("/audio/{fileID}")
    @Produces({"audio/mpeg"})
    public Response downloadFile(@PathParam("fileID") final Long fileId) {
        File file = new File(String.valueOf(fileId) + ".mpg");
        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment;filename=" + String.valueOf(fileId) + ".mpg");
        return response.build();
    }

}
