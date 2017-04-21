# bht-chatbot
Master-Projekt SS2017 Medieninformatik

## Requirements
You need a running docker daemon to start the application.

## Run and stop the application

Just executes the following maven commands to start/stop the application:
```bash
    # build and start
    mvn compile war:war docker:start
    
    # show and follow logs
    mvn compile war:war docker:start docker:logs -Ddocker.follow
    
    # stop running container
    mvn docker:stop
```
The following ports are mapped to host: 
* 8080 -> 8080

If all went well, you should see the application appearing at [localhost:8080/bht-chatbot](http://localhost:8080/bht-chatbot) 

## Used online sources
* [Markdown CheetSheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)
* [base WildFly application server image](https://hub.docker.com/r/jboss/wildfly/)
* [docker-maven-plugin documentation](https://dmp.fabric8.io/#start-logging)
