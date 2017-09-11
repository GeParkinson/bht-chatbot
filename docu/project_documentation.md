# BHT-Chatbot Project
The hole project is build by gradle and divided into a couple of subprojects (cf. [Subprojects section](#subprojects) ).
Each module is loosely connected through a Java Message Service. The application is running on a Jboss Wildfly
inside of a docker container. Another docker container is used for the Rasa backend.

## Infrastructure
- see [docker compose file](../docker/docker-compose.yml)

The productive project is represented by a separate Git repository in absent of a continuous integration server.
Pushing into this repository will automatically trigger a rebuild of the productive environment.
- confer [post-receive](../scripts/post-receive) - Git hook for auto deploying the application

### Subprojects

#### MainBot
- [Canteen Parser](canteenParser.md) - web crawler for collecting data of the beuth university canteen
- Common - holding common classes used by all other subprojects
- [Drools](drools.md) - Business Rules Management System used to generate the right answer
- Global - global available services

#### Natural Language Processing
- [ApiAi](apiai.md) - simple RESTEasy client application calling googles Api.ai API
- [Rasa](rasa.md) - simple RESTEasy client application calling the rasa backend rest API

#### Messenger
- [Facebook](facebook.md) - Facebook Messenger connector
- [Telegram](telegram.md) - Telegram Messenger connector

#### Text <-> Speech Processing
- [Bing Speech](binspeechapi.md) - REST client for Microsofts Bing Speech API