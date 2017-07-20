#Drools

<!-- MarkdownTOC -->

- [examples](#examples)
- [preparation](#preparation)
- [kmodule](#kmodule)
- [rule](#rule)
- [usage](#usage)

<!-- /MarkdownTOC -->

Official documentation:
[Drools documentation](https://docs.jboss.org/drools/release/7.0.0.Final/drools-docs/html_single/index.html)

## examples
On the bottom of the [drools website](https://www.drools.org/) you can find a ZIP with many examples.
Just download it and follow the instructions to execute the instructions given on this page.

###preparation
Befor we can start, we need to edit our build.gradle as follows:
```gradle
repositories {
    mavenCentral()
    maven {
        url 'https://repository.jboss.org/nexus/content/groups/public/'
    }
}

dependencies {
    compile "org.drools:drools-compiler:7.0.0.Final"
}
```
We have to set a dependency and declare another maven repository. The installation doesn't work with the standard mavenCentral() repository.

###kmodule
Another thing what we need is the `kmodule.xml` file. A config file for drools to know, which rules-file to use.
This file has to be saved under `resources/META-INF/`.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<kmodule xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://www.drools.org/xsd/kmodule">
    <kbase name="TestKB" packages="my.example.de">
        <ksession name="TestKS"/>
    </kbase>
</kmodule>
```

###rule
Rules are defined in Drools via .drl files and has to be saved in the `resources` folder. Such a file could look like this:
```drools
import my.example.de.TestObject
import my.example.de.TextEnum;

global my.example.de.TestKlasse testKlasse

rule "Test Rule"
    dialect "java"
    when
        m : TestObject( attribute.equals("Test") )
    then
        modify ( m ) { setAnotherAttribute("Test successful" )};
end

rule "Greeting"
    dialect "java"
    when
        m : TestObject( attribute.equals(TestEnum.GREETING.getText()))
    then
        modify ( m ) { setAnotherAttribute(testKlasse.getGreetingMsg())};
end
```
If we want to use caches or similar objects, we have to set them as global variables.
This is shown by the line `global my.example.de.TestKlasse testKlasse`.

All rules in a drools-file are processed on an object, if the `when`-block fits for the given object.

After `rule` we give our rules names.

There are two possible dialects in drools-files. Default is `java` and the other one is `mvel`.

The `when`-block checks if a given object fits for this rule and in the `then`-block you can do stuff if conditions are met.

###usage
Now, that we've set the necessary dependencies and defined some rules, we can start and use our rules on some objects.
```
    // KieServices is the factory for all KIE services
    KieServices ks = KieServices.Factory.get();

    // From the kie services, a container is created from the classpath
    KieContainer kc = ks.getKieClasspathContainer();

    // From the container, a session is created based on
    // its definition and configuration in the META-INF/kmodule.xml file
    KieSession ksession = kc.newKieSession("TestKS");

    // Once the session is created, the application can interact with it
    // In this case it is setting a global as defined in the
    // org/drools/examples/helloworld/HelloWorld.drl file
    Testklasse testKlasse = new TestKlasse();
    ksession.setGlobal("testKlasse", testKlasse);

    // Object, to fire rules on
    TestObject testObject = new TestObject();

    // The application can insert facts into the session
    ksession.insert(testObject);

    // Test output before
    System.out.println(testObject.getAnotherAttribute());

    // and fire the rules
    ksession.fireAllRules();

    // Test output after
    System.out.println(testObject.getAnotherAttribute());

    // and then dispose the session
    ksession.dispose();
```
The first line are just standard to create a `KieSession`.
On this `KieSession` we can set globals as mentioned in rules section.
Objects, on which we want to process the rules have to be inserted in this sessoin `ksession.insert(testObject);`.

After we have inserted all objects we want to process, we start the procession by `kieSession.fireAllRules();`.

This is it, after the last command is finished, all objects should be processed.