package de.bht.beuthbot.messenger.facebook;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by oliver on 15.06.2017.
 */
@Path("/")
public interface FacebookRESTServiceInterface {

    /**
     * interface to communicate with facebook
     * @param json data payload which contains all necessary data of the message (sender, content, etc.)
     * @param token the API token for Facebook messages
     * @return JAX RS Response representing the result of querying the facebook server
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    Response sendMessage(String json, @QueryParam("access_token") String token);
}
