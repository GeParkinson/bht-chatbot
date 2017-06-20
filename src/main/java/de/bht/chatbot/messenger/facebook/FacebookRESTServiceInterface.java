package de.bht.chatbot.messenger.facebook;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by oliver on 15.06.2017.
 */
@Path("/")
public interface FacebookRESTServiceInterface {

    /**
     * Processing the given text for intent and entities
     * @return JAX RS Response representing the result of querying the facebook server
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    Response sendMessage(String json, @QueryParam("access_token") String token);
}
