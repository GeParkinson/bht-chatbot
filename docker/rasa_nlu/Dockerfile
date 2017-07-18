FROM python:2.7-slim

ENV RASA_NLU_DOCKER="YES" \
    RASA_NLU_HOME="/app"

# Run updates, install basics and cleanup
# - build-essential: Compile specific dependencies
# - git-core and ssh: Checkout git repos
RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends build-essential git-core wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

WORKDIR ${RASA_NLU_HOME}

# Install rasa and its dependencies
RUN pip install -U spacy scikit-learn scipy sklearn-crfsuite
RUN python -m spacy download de
RUN pip install rasa_nlu==0.9.1

# train the model
COPY rasa_config.json config/chatbot_config.json
COPY volumes/data/api/ data/api
RUN python -m rasa_nlu.train -c config/chatbot_config.json

# Cleanup
RUN mv models/model_* models/default && rm -r data/api

EXPOSE 5000