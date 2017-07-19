# api.ai API

<!-- MarkdownTOC -->

- [api.ai Setup](#api.ai-setup)
- [Bot Setup](#bot-setup)
- [About apiai package](#about-apiai-package)
- [Used online sources](#used-online-sources)

<!-- /MarkdownTOC -->

## api.ai Setup 

- Create new [api.ai Account](https://console.api.ai/api-client)
- Add a new agent "BHT-Chatbot"
- You can import our agent settings(which include all intents and entities we created) under 'Settings'->'Export and Import', search agents in the agent list or create an own one
- Our settings are located under src\main\docker\rasa_nlu\volumes\data\api ... put them into a zip and you are able to upload them as described above
- The 'Training'-tab helps you to improve your bot in understanding requests

## Bot Setup
- Get your 'Client access token' in the api.ai agent settings and place it under 'API_AI_TOKEN' in the 'config.properties'
- If necessary, change the context and language in 'ApiAiConnector'->'onMessage'

## About apiai package

- The 'model' directory contains java classes which represent the json-response we get from api.ai requests, which makes it possible to parse these jsons into java classes
- ApiAiResponse implements NLUResponse and makes it possible to access the intents and entities of api.ai's answer
- <b>IMPORTANT:</b> 'Parameters' contains the entities given by api.ai and has to be changed on changes of the entities at your api.ai agent
- ApiAiMessage contains the given BotMessage but also the ApiAiResponse to send both to Drools

## Used online sources
- [api.ai Docs](https://api.ai/docs/getting-started/basics)