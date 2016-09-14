#pigeonator

##Prerequisits

* Gradle 2.12 or newer
* Java 8 or newer

##Configuration

Copy and rename config.properties.template to config.properties and mailingList.json.template to mailingList.json to ensure proper functionality.

##Building jar

`gradle clean shadowJar`

This command builds a single uber-jar containing all dependencies that can be found here.

```
build/libs/pigeonator.jar
```
