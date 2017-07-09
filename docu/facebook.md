# Facebook Adapter

## Framework

- Contrary to the Telegram Adapter, we did not use a Framework for Facebook and implemented the communication with Facebook by ourselves

## Structure

- Facebook Adapter is split into three basic classes:
    1. FacebookReceiveAdapter
    2. FacebookSendAdapter
    3. FacebookUtils
    
##### i. FacebookReceiveAdapter

- For receiving new messages from Users and webhook requests.

Using RESTEasy for HTTP Requests.
```java
@Path("/facebook")
public class FacebookReceiveAdapter {
```
POST Requests for incoming Facebook messages:
```java
    @POST
    @Path("/getUpdates")
    @Consumes("application/json")
    public String ReceiveMessage(String InputMessage) {}
```
GET Requests for webhook verification:
```java
    @GET
    @Path("/getUpdates")
    @Produces("text/plain")
    public String verification(@Context HttpServletRequest request){}
```
GET Requests for webhook initialization (if user opens http://yourdomain.com/rest/facebook/setWebhook to set the Facebook webhook to your current server address):
```java
    @GET
    @Path("/setWebhook")
    @Produces("text/plain")
    public String setWebhook(@Context HttpServletRequest request){}
```
```java
    }
```

##### ii. FacebookSendAdapter

- For sending new messages to Users.

Send Text Request.
```java
    private void sendMessage(Long recipient, String messageJson) {
        String payload = "{\"recipient\": {\"id\": \"" + recipient + "\"}, \"message\": { \"text\": \""+messageJson+"\"}}";
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages" ;
        facebookUtils.sendPostRequest(requestUrl, payload, facebookUtils.token());
    }
```

Send Media Request.
```java
    private void sendMedia(BotMessage message,String mediaType){
        String fileURL=attachmentStore.loadAttachmentPath(message.getAttachments()[0].getId(), AttachmentStoreMode.FILE_URI);
        String payload = "{recipient: { id: "+message.getSenderID()+" }, message: { attachment: { type: \""+mediaType+"\", payload: { url: \""+fileURL+"\"  } }   }} ";
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages" ;
        facebookUtils.sendPostRequest(requestUrl, payload,facebookUtils.token());
    }
```

##### iii. FacebookUtils

- Contains methods which are used by both, sender and receiver.
- While some methods just return property configs, these two are more important:

Webhook activation:
- after the webhook is verified, it needs to be activated to subscribe to the incoming messages
- this has to be done after a short delay to ensure that the webhook is registered by Facebook
```java
public void activateWebhook() {

        Runnable activation = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendPostRequest("https://graph.facebook.com/v2.9/me/subscribed_apps","",token());
            }
        };
        new Thread(activation).start();
    }
```
Send requests to Facebook via our FacebookRESTServiceInterface:
```java
    public String sendPostRequest(String requestUrl, String payload, String token) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(requestUrl));
        FacebookRESTServiceInterface facebookProxy = target.proxy(FacebookRESTServiceInterface.class);

        Response response = facebookProxy.sendMessage(payload, token);

        String responseAsString = response.readEntity(String.class);


        return responseAsString;

    }
```

## Used online sources

- [Facebook API overview](https://developers.facebook.com/docs/)