package nlp;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import messenger.utils.MessengerUtils;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by oliver on 06.06.2017.
 */

//test API.ai
//String[] contexts= new String[1];
//contexts[0]="weather";
//System.out.println(APIai.getResult("Boston",contexts,"12345" ).toString());

public class APIai {
    public static JSONObject getResult(String query, String[] contexts,String sessionID) throws IOException, UnirestException {
        Properties properties = MessengerUtils.getProperties();
        String token = properties.getProperty("API_AI_TOKEN");

        String language="en";

        String reqURL="https://api.api.ai/api/query?query="+query+"&lang="+language+"&sessionId="+sessionID;

        for (String context: contexts) {
            reqURL=reqURL+"&contexts="+context;
        }

        //TODO: Replace Unirest with RESTeasy
        JSONObject resultJson =  Unirest.get(reqURL).header("Authorization","Bearer "+token).asJson().getBody().getObject();

        return resultJson.getJSONObject("result");
    }
}
