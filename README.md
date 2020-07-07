# kafka-web-ui
### The only web UI for kafka messages which uses the SASL usersname/passwords directly as logins.
This allows your team to view messages using SASL accounts and Admins to assign appropriate ACLs to them.

## How to run
### Run as a docker container
You can run this as a docker container `docker run  -e ALLOWED_USERS=[SASL USERS] -e SECURITY_PROTOCOL=[SECURITY PROTOCOL] -e BOOTSTRAP_SERVERS=[BOOTSTRAP SERVERS] -e SCHEMA_REGISTRY_URL=[OPTIONAL SCHEMA REGISTRY URL] -p 8080:8080 rahulbats/kafka-web-ui:[LATEST TAG]` 

Then access it in browser as `http://localhost:8080/api/ui/index.html`


The `SASL USERS` is a string made of the SASL userid:passwords seperated by coma for example `test:test123,previleged:previleged123` 

If you prefer to use a file instead of environment variable you can create a properties file where the key is username and value is password.
You can pass the path of the properties file as `USERS_PROPERTIES` environment variable. Refer the [environment variables section](#list-of-environment-variables) for more information. 


### Run as standalone java application
* run `./build.sh`
* export the [environment variables](#list-of-environment-variables) 
* Run the jar using `java -jar server/build/libs/kafka-web-ui-*.jar`
* Open a browser and go to `http://localhost:8080/api/ui/index.html`

## List of environment variables
### Table below describes the environment variables. Refer the [application.properties file](server/src/main/resources/application.properties) on how these are used.
| Environment Variable | Optional | Description |
| --- | --- | --- |
| ALLOWED_USERS | Yes | Optional String Containing SASL username:password seperated by comas for example `test:test123,previleged:previleged123` |
| USERS_PROPERTIES | Yes | If you dont want to use the above variable you can point to the path of properties file, which has key as user and value as SASL password |
| BOOTSRAP_SERVERS | No | Broker bootstrap servers |
| SECURITY_PROTOCOL | No | Broker security protocol |
| SSL_TRUSTSTORE_LOCATION | Yes | Path of the truststore for Kafka broker |
| SSL_TRUSTSTORE_PASSWORD | Yes | Password of the truststore for Kafka broker |
| SSL_TRUSTSTORE_CREDENTIALS | Yes | if you want to use Credential instead of password |
| SSL_KEYSTORE_LOCATION | Yes | Path of the Keystore for Kafka broker |
| SSL_KEYSTORE_PASSWORD | Yes | Password of the Keystore for Kafka broker |
| SSL_KEY_PASSWORD | Yes | Password of the Key for Kafka broker |
| SASL_MECHANISM | Yes | SASL mechanism for Kafka broker. Can either be `PLAIN` or `SCRAM`. Default is `PLAIN`. |
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

### If you want to connect to LDAP use the following 
| Environment Variable | Optional | Description |
| --- | --- | --- |
| ldap.enabled | Yes | Enable LDAP integration, default false |
| ldap.role.prefix | Yes | LDAP group prefix |
| ldap.url | Yes | LDAP url |
| ldap.bind.dn| Yes | LDAP bind DN |
| ldap.bind.password | Yes | LDAP bind password |
| ldap.user.basedn | Yes | LDAP base DN to do user search |
| ldap.user.id.attribute | Yes | User id attribute, default uid |
| ldap.group.basedn | Yes | Base DN for group search |
| ldap.group.name.attribute | Yes | LDAP group name attribute, default value cn |
| ldap.group.member.attribute| Yes | LDAP group member attribute, default value uniqueMember|
