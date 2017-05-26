FROM python:2.7-slim

ENV RASA_NLU_DOCKER="YES" \
    RASA_NLU_HOME="/app"

# Run updates, install basics and cleanup
# - build-essential: Compile specific dependencies
# - git-core and ssh: Checkout git repos
RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends build-essential git-core && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

WORKDIR ${RASA_NLU_HOME}

RUN git clone https://github.com/golastmile/rasa_nlu.git ${RASA_NLU_HOME} && \
    git checkout tags/0.8.5

# Split into pre-requirements, so as to allow for Docker build caching
RUN pip install -r dev-requirements.txt

RUN python setup.py install

EXPOSE 5000

ENTRYPOINT ["./entrypoint.sh"]
CMD ["help"]