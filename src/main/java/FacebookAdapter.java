import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;



@Path("/webhook")
public class FacebookAdapter {


    @POST
    @Path("/Facebook")
    @Consumes("application/json")
    public String consumeJSON(String test) throws IOException {

        String accessToken="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

        String pageName ;
        String message;
        Boolean isEcho;
        try {
            JSONObject obj = new JSONObject(test);
            pageName = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id");
            message = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getString("text");
            isEcho=obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getBoolean("is_echo");
        }
        catch(Exception ex){
            message="";
            pageName="";
            isEcho=true;
        }

        if(message.length()>0&&isEcho==false) {
            String payload = "{\"recipient\": {\"id\": \"" + pageName + "\"}, \"message\": { \"text\": \""+message+"\"}}";
            String requestUrl = "https://graph.facebook.com/v2.6/me/messages?access_token=" + accessToken;
            try {
                sendPostRequest(requestUrl, payload);
            }
            catch(Exception ex){}
            System.out.println(payload+"------"+test);
        }
        return "";

    }

    @GET
    @Path("/Facebook")
    @Produces("text/plain")
    public String verification(@Context HttpServletRequest request){

        //URL: adressToYourPC:8080/bht-chatbot/rest/webhook/Facebook
        String webhookToken = "test";

        System.out.println("request: " + request);
        Map<String, String[]> parametersMap = request.getParameterMap();
        if (parametersMap.size() > 0) {
            System.out.println("HUB_MODE: " + request.getParameter("hub.mode"));
            System.out.println("HUB_VERIFY_TOKEN: " + request.getParameter("hub.verify_token"));
            System.out.println("HUB_CHALLENGE: " + request.getParameter("hub.challenge"));

            if("subscribe".equals(request.getParameter("hub.mode")) &&
                    webhookToken.equals(request.getParameter("hub.verify_token"))){
                System.out.println("VERIFIED");
                return request.getParameter("hub.challenge");
            }
        }else{
            System.out.println("No request parameters were given.");
        }

        return "Webhook FAILED";
    }




    public static String sendPostRequest(String requestUrl, String payload) {
        StringBuffer jsonString = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return jsonString.toString();
    }

}
