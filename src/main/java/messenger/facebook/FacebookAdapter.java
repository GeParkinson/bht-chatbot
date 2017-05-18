package messenger.facebook;

import messenger.utils.MessengerUtils;
import org.json.JSONObject;

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

/**
 * Created by Oliver on 5/14/2017.
 */

@Path("/webhook")
public class FacebookAdapter {


    //---------------------------------------
    //Testing:
    //install localtunnel (npm install -g localtunnel) and connect via 'lt --port 8080' *
    //*doesnt work in some networks like eduroam
    //
    //get your accessToken in the messenger product of your facebook app
    //
    //create webhook with:
    //URL:  https://XXXXXXXXXXXXX.localtunnel.me/bht-chatbot/rest/webhook/facebook
    //token: set in config.properties
    //---------------------------------------
    String accessToken= MessengerUtils.getProperties().getProperty("FACEBOOK_BOT_TOKEN");
    String webhookToken = MessengerUtils.getProperties().getProperty("FACEBOOK_WEBHOOK_TOKEN");

    @POST
    @Path("/facebook")
    @Consumes("application/json")
    public String ReceiveMessage(String InputMessage) throws IOException {

        System.out.println(InputMessage);
        System.out.println(accessToken);

        String sender ;
        String message;
        Boolean isEcho;
        JSONObject obj;
        try {
            obj = new JSONObject(InputMessage);
            sender = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id");
            message = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getString("text");
            }
        catch(Exception ex){
            message="";
            sender="";
        }
        try{
            obj = new JSONObject(InputMessage);
            isEcho=obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getBoolean("is_echo");
        }
        catch(Exception ex){
            isEcho=false;
        }

        if(message.length()>0&&isEcho==false) {
            sendTextMessage(sender,message,accessToken);
        }
        return "\nReceived\n";

    }

    @GET
    @Path("/facebook")
    @Produces("text/plain")
    public String verification(@Context HttpServletRequest request){


        System.out.println("request: " + request);
        Map<String, String[]> parametersMap = request.getParameterMap();
        if (parametersMap.size() > 0) {
            System.out.println("HUB_MODE: " + request.getParameter("hub.mode"));
            System.out.println("HUB_VERIFY_TOKEN: " + request.getParameter("hub.verify_token"));
            System.out.println("HUB_CHALLENGE: " + request.getParameter("hub.challenge"));

            if("subscribe".equals(request.getParameter("hub.mode")) &&
                    webhookToken.equals(request.getParameter("hub.verify_token"))){
                System.out.println("VERIFIED");

                //Start function to activate webhook
                Runnable activation = new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        sendPostRequest("https://graph.facebook.com/v2.9/me/subscribed_apps?access_token="+accessToken,"");
                    }
                };

                new Thread(activation).start();//Call it when you need to run the function

                return request.getParameter("hub.challenge");
            }
        }else{
            System.out.println("No request parameters were given.");
        }

        return "Webhook FAILED";
    }



    public static void sendTextMessage(String recipient, String message, String accessToken){
        String payload = "{\"recipient\": {\"id\": \"" + recipient + "\"}, \"message\": { \"text\": \""+message+"\"}}";
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages?access_token=" + accessToken;
        try {
            sendPostRequest(requestUrl, payload);
        }
        catch(Exception ex){
            //System.out.println(ex.getMessage()+" ERROR");
        }
        //System.out.println(payload+"------"+requestUrl);
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

            //System.out.println(connection.getResponseMessage());

            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return jsonString.toString();
    }

}
