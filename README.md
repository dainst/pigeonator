#pigeonator

##Configuration

Copy and rename config.properties.template to config.properties and mailingList.json.template to mailingList.json to ensure proper functionality.

##Building jar

`gradle clean`
`gradle shadowJar`

This command builds a single uber-jar containing all dependencies that can be found here.

```
build/libs/pigeonator-all.jar
```
