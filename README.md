# kafka-web-ui
### The only web UI for kafka messages which uses the SASL usersname/passwords directly as logins.
This allows your team to view messages using SASL accounts and Admins to assign appropriate ACLs to them.

## How to run

You can run this as a docker container `docker run  -e ALLOWED_USERS=[SASL USERS] SECURITY_PROTOCOL=[SECURITY PROTOCOL] BOOTSTRAP_SERVERS=[BOOTSTRAP SERVERS] SCHEMA_REGISTRY_URL=[OPTIONAL SCHEMA REGISTRY URL] rahulbats/kafka-web-ui:1.0.12` 


The SASL USERS is a string made of the SASL userid:passwords seperated by coma for example `test:test123,previleged:previleged123` 

If you prefer to use a file instead of environment variable you can create a properties file where the key is username and value is password.
You can pass the path of the properties file as `USERS_PROPERTIES`.

Then expose it as a service and access it as in browser as `http://[Service URL]/api/ui/index.html`

To run locally cd into the server folder build jar using `./gradlew clean build`

Then run the jar using `java -jar build/libs/kafka-web-ui-1.0.0.jar`

Open a browser and go to `http://localhost:8080/api/ui/index.html`

## List of environment variables
### Table below describes the environment variables. Refer the [application.properties file](server/src/main/resources/application.properties) on how these are used.
| Environment Variable | Optional | Description |
| --- | --- | --- |
| ALLOWED_USERS | Yes | Optional String Containing SASL username:password seperated by comas |
| USERS_PROPERTIES | Yes | If you dont want to use the above variable you can point to the path of properties file, which has key as user and value as SASL password |
| BOOTSRAP_SERVERS | No | Broker bootstrap servers |
| SECURITY_PROTOCOL | No | Broker security protocol |
| SSL_TRUSTSTORE_LOCATION | Yes | Path of the truststore for Kafka broker |
| SSL_TRUSTSTORE_PASSWORD | Yes | Password of the truststore for Kafka broker |
| SSL_TRUSTSTORE_CREDENTIALS | Yes | if you want to use Credential instead of password |
| SSL_KEYSTORE_LOCATION | Yes | Path of the Keystore for Kafka broker |
| SSL_KEYSTORE_PASSWORD | Yes | Password of the Keystore for Kafka broker |
| SSL_KEY_PASSWORD | Yes | Password of the Key for Kafka broker |
| SASL_MECHANISM | Yes | SASL mechanism for Kafka broker |
| SCHEMA_REGISTRY_URL | Yes | URL for schema registry |
| SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO | Yes | Schema registry auth username:password | 


### If you want the server to run on TLS use the following environment variables
| Environment Variable | Optional | Description |
| --- | --- | --- |
| UI_SSL_ENABLED | Yes | true or false |
| UI_SSL_TYPE | Yes | JKS or PKCS12 - default is JKS |
| UI_SSL_KEYSTORE_LOCATION | Yes | Location of Keystore |
| UI_SSL_KEYSTORE_PASSWORD | Yes | Keystore password |
| UI_SSL_REQUIRED | Yes | true or false. Whether SSL is required - Default true |
