# Telegram Adapter

Step by step Guide.
<!-- MarkdownTOC -->

- [Framework](#framework)
- [Structure](#structure)
- [Used online sources](#used-online-sources)

<!-- /MarkdownTOC -->

## Framework

- Using [Java-Telegram-Bot-API](https://github.com/pengrad/java-telegram-bot-api) Framework from github user pengrad.
    - Easy access to Telegram API via TelegramBot-Objects
    - Well maintained repository (June 2017)

## Structure

- Telegram Adapter is split in two basic classes:
    1. TelegramReceiveAdapter
    2. TelegramSendAdapter
- Receiving messages via webhook.
    
##### i. TelegramReceiveAdapter

- For receiving new messages from Users.

Using RESTEasy for HTTP Requests.
```java
@Path("/telegram")
public class TelegramReceiveAdapter {
    
    @POST
    @Path("/getUpdates")
    public void getUpdates(final String msg) {}
    
    ...
}
```


##### ii. TelegramSendAdapter

- For sending new messages to Users.

Asynchronus Webhook verification.
```java
SetWebhook webhook = new SetWebhook().url(properties.getProperty("WEB_URL") + properties.getProperty("TELEGRAM_WEBHOOK_URL"));

bot.execute(webhook, new Callback<SetWebhook, BaseResponse>() {
    @Override
    public void onResponse(final SetWebhook request, final BaseResponse response) {
        logger.debug("No errors while setting Telegram webhook.");
    }
    @Override
    public void onFailure(final SetWebhook request, final IOException e) {
        logger.warn("An Error occured while setting Telegram webhook. BOT_TOKEN: " + properties.getProperty("TELEGRAM_BOT_TOKEN") + " - WEBHOOK_URL: " + properties.getProperty("WEB_URL") + properties.getProperty("TELEGRAM_WEBHOOK_URL"));
    }
});
```

Send Text Request.
```java
private void sendMessage(final Long senderId, final String message) {
        SendMessage request = new SendMessage(senderId, message);
        SendResponse sendResponse = bot.execute(request);
        logger.debug("Send de.bht.chatbot.message: " + sendResponse.isOk());
    }
```

Send Audio Request.
```java
private void sendAudio(final BotMessage botMessage){
    SendAudio request;

    // check and send each attachement
    for (Attachment attachment : botMessage.getAttachements()) {
        request = new SendAudio(botMessage.getSenderID(), attachment.getFileURI());

        if (attachment.getCaption() != null)
            request.caption(attachment.getCaption());

        SendResponse sendResponse = bot.execute(request);
        logger.debug("Send Audio: " + sendResponse.isOk());
    }
}
```

## Used online sources

- [Telegram API overview](https://core.telegram.org/bots/api)
- [Java-Telegram-Bot-API](https://github.com/pengrad/java-telegram-bot-api) (github - user: pengrad)