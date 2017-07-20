# Facebook Adapter

<!-- MarkdownTOC -->

- [Framework](#framework)
- [Setup](#setup)
- [Structure](#structure)
- [Used online sources](#used-online-sources)

<!-- /MarkdownTOC -->

## Framework

- Contrary to the Telegram Adapter, we did not use a Framework for Facebook and implemented the communication with Facebook by ourselves

## Setup

- You need:
    1. Facebook Page (https://www.facebook.com/pages/create/)
    2. Facebook App (https://developers.facebook.com/apps/)
    3. A Server with ssl certificate

##### i. Facebook Page

- your users communicate with this page while the App is working in the background
- use an existing one or create a new page

##### ii. Facebook App

- after you created the app, add the 'messenger product' in the control panel on the left side
- generate an messaging key for your Facebook page and store it under 'FACEBOOK_BOT_TOKEN' in the config.properties
- setup a webhook by using your webhook address (https://yourdomain.com/rest/facebook/getUpdates) and the identification key set in 'FACEBOOK_WEBHOOK_TOKEN' in the config.properties
- subscribe the webhook to your Facebook page
- NOTE: if you want to change the address of the webhook (for example if your server address changes) there are two possibilities:
    1. navigate to the webhook product on the left and select 'Edit Subscription'
    2. you can set the webhook to your current address by opening 'http(s)://yourdomain.com/rest/facebook/setWebhook', but this requires to set 'FACEBOOK_ACCESS_TOKEN' in the config.properties. You can get your access token at 'https://developers.facebook.com/tools/explorer/' by selecting your app. Be sure you select 'app access token' in the dropdown menu, otherwise it generates an user access token. The app token should look like this: '00000000000|XXXXXXXXX-XXXXXXX'. This possibility of changing the webhook path needs more time in setting it up at the beginning, but it's more comfortable in the end, especially when the amount of Messengers grows (e.g you set your Telegram webhook at 'http(s)://yourdomain.com/rest/telegram/setWebhook').

##### iii. ssl certificate

- if you don't have a valid sll crtificate, there are several ways of setting up https for your domain for free. While we were in development, we used localtunnel to generate a https-URL which tunneled the port 8080 to our PCs. If you own a domain, you can also use Cloudflare as nameserver which makes it possible to use free SSL-certificates as well.

## Structure

- Facebook Adapter is split into three basic classes:
    1. FacebookReceiveAdapter
    2. FacebookSendAdapter
    3. FacebookUtils
    
##### i. FacebookReceiveAdapter

- For receiving new messages from Users and webhook requests.

Using RESTEasy for HTTP Requests
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

Send Text Request:
- Facebook limits the character amount per message to 640, that's why we have to split messages if they are to long
- if the messages contain multiple entries (e.g. dishes), it makes sense to split them to avoid message-splits within entries
```java
    private void sendMessage(Long recipient, String messageJson) {
        Boolean seperateMessages = true;
        String seperator = ", --------------------------";

        for( String currentMessage : facebookUtils.splitIntoMultipleMessages(messageJson,600,seperateMessages,seperator) ) {
            String payload = "{\"recipient\": {\"id\": \"" + recipient + "\"}, \"message\": { \"text\": \"" + currentMessage + "\"}}";
            facebookUtils.sendPostRequest(requestUrl, payload, facebookUtils.token());
        }
    }
```

Send Media Request:
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
- While some methods just return property configs, these three are more important:

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
Split the message into multiple messages with sendable size:
- split at newlines and store message before char limit reached or between entries (if enabled)
- return list of messages which FacebookSend iterates through to send everything
```java
public List<String> splitIntoMultipleMessages(String messageJson, int charLimit, Boolean seperateMessages, String seperator){
        List<String> messages = new ArrayList<String>();

        String linesOfMessage[] = messageJson.split("\\r?\\n");

        String currentOutput = "";
        for (int i = 0; i < linesOfMessage.length; i++) {
            String line = linesOfMessage[i];
            if ((currentOutput + "\\n" + line).length() > charLimit || (line.contains(seperator)&&seperateMessages)) {
                messages.add(currentOutput);

                if(line.contains(seperator) && seperateMessages) {
                    line="";
                }

                currentOutput = line;
            } else {
                currentOutput = currentOutput + "\\n" + line;
            }


        }
        messages.add(currentOutput);


        return messages;
    }
```
## Used online sources

- [Facebook API overview](https://developers.facebook.com/docs/)