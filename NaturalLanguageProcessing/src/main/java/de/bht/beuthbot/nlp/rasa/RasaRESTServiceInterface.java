package de.bht.beuthbot.nlp.rasa;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 *
 * Proxy interface to query rasa_nlu REST api
 */
@Path("/parse")
public interface RasaRESTServiceInterface {

    /**
     * Processing the given text for intent and entities
     * @param text Text to get intent and entities from, using rasa_nlu
     * @return JAX RS Response representing the result of querying the rasa_nlu server
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    Response processText(@QueryParam("q") String text);
}
