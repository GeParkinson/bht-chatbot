package de.bht.chatbot.nlu.apiai;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 *
 * Proxy interface to query api.ai REST api
 */
@Path("/")
public interface ApiAiRESTServiceInterface {

    /**
     * Processing the given text for intent and entities
     * @return JAX RS Response representing the result of querying the api.ai server
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    Response processText(@QueryParam("query") String text,@QueryParam("lang") String language,@QueryParam("sessionId") String sessionID,@QueryParam("contexts") String context,@HeaderParam("Authorization") String auth);
}
