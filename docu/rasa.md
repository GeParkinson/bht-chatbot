# Rasa
The Rasa subproject is basically a simple RESTEasy client application calling the rasa backend REST API.

## Update and train the model
We are using the same training data as used by Api.ai. Just extract the exported agent zip to `docker/rasa_nlu/volumes/data/api/` and rebuild the container.
You can login to the running container and execute `python -m rasa_nlu.train -c config/chatbot_config.json` also (cf. [rasa Dockerfile](../docker/rasa_nlu/Dockerfile)).
Be aware of that training the model can take some time.

## Used online sources
 - [rasa documentation](https://rasa-nlu.readthedocs.io/en/latest/tutorial.html)
 - [RESTEasy 3.1.3](https://docs.jboss.org/resteasy/docs/3.1.3.Final/userguide/html/)