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
     * interface to communicate with facebook
     *
     * @param json  data payload which contains all necessary data of the message (sender, content, etc.)
     * @param token the API token for Facebook messages
     * @return JAX RS Response representing the result of querying the facebook server
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    Response sendMessage(String json, @QueryParam("access_token") String token);

    /**
     * update the webhook location for facebook
     *
     * @param object       --> page because we use a facebook page for our bot
     * @param callback_url --> url of our server were the webhook of facebook goes to
     * @param fields       --> fields we want to subscribe to
     * @param verify_token --> webhook token
     * @param access_token --> app access token
     * @return answer of facebook
     */
    @POST
    @Consumes("text/plain")
    Response sendHook(@QueryParam("object") String object, @QueryParam("callback_url") String callback_url, @QueryParam("fields") String fields, @QueryParam("verify_token") String verify_token, @QueryParam("access_token") String access_token);
}
