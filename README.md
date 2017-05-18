# BHT-Chatbot
Master-Projekt SS2017 Medieninformatik
<!-- MarkdownTOC -->

- [Requirements](#requirements)
- [Main TechStack](#main-techstack)
- [Project documention](#project-documention)
- [Run and stop the application](#run-and-stop-the-application)
- [Used online sources](#used-online-sources)

<!-- /MarkdownTOC -->

## Requirements
- Java 8  to build the application
- a running docker daemon to start application server

## Main TechStack
- Gradle 3.5
- Docker
- WildFly 10

## Project documention
You can find the whole project documentation under [project_documentation](docu/project_documentation.md).

## Run and stop the application
Go to project path and executes the following gradle tasks to start/stop the application:
 ```bash
    # Unix based
    ./gradlew chatbotRun
    ./gradlew chatbotStop

    # Windows
    gradlew.bat chatbotRun
    gradlew.bat chatbotStop
 ```

The following ports are mapped to host:
- 8080 -> 8080
- 8787 -> 8787 (Remote Debug Port)
- 9990 -> 9990 (WildFly Server Manager)

If all went well, you should see the application appearing at [localhost:8080/bht-chatbot](http://localhost:8080/bht-chatbot) 

## Known issues
- The usage of `./gradlew chatbotRun` is not working under Mac OSX

## Used online sources
- [Markdown CheetSheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)
- [Docker base image for WildFly application server](https://hub.docker.com/r/jboss/wildfly/)
- [Gradle 3.5 User Guide](https://docs.gradle.org/3.5/userguide/userguide.html)

