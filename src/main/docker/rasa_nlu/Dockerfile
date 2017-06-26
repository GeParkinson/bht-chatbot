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

RUN pip install git+https://github.com/mit-nlp/MITIE.git
RUN pip install rasa_nlu

# RUN wget https://github.com/mit-nlp/MITIE/releases/download/v0.4/MITIE-models-v0.2-German.tar.bz2
# RUN tar xvjf MITIE-models-v0.2-German.tar.bz2
# RUN mkdir -p data/de && mv MITIE-models/german/total_word_feature_extractor.dat data/de/


RUN wget https://github.com/mit-nlp/MITIE/releases/download/v0.4/MITIE-models-v0.2.tar.bz2
RUN tar xvjf MITIE-models-v0.2.tar.bz2
RUN mkdir -p data/en && mv MITIE-models/english/total_word_feature_extractor.dat data/en/

# train the model
COPY rasa_config.json config/chatbot_config.json
COPY volumes/data/api/ data/api
RUN python -m rasa_nlu.train -c config/chatbot_config.json
RUN mv models/model_* models/default && rm -r data/api

EXPOSE 5000