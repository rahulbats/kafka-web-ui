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

### Login through LDAP
<b>Kafka Web UI</b> also supports loging in using LDAP/AD. The users should be part of LDAP groups with group names matching any of the SASL accounts in the `ALLOWED_USERS` or `USERS_PROPERTIES`. The SASL account should be in lower case, the code lowercases the group name before looking it up for the SASL account. Following are the environment variables you will need to enable this.

| Environment Variable | Optional | Description |
| --- | --- | --- |
| LDAP_ENABLED | Yes | Enable LDAP integration, default false |
| LDAP_ROLE_PREFIX | Yes | LDAP group prefix |
| LDAP_URL | Yes | LDAP url |
| LDAP_BIND_DN| Yes | LDAP bind DN |
| LDAP_BIND_PASSWORD | Yes | LDAP bind password |
| LDAP_USER_BASEDN | Yes | LDAP base DN to do user search |
| LDAP_USER_ID_ATTRIBUTE | Yes | User id attribute, default uid |
| LDAP_GROUP_BASEDN | Yes | Base DN for group search |
| LDAP_GROUP_NAME_ATTRIBUTE | Yes | LDAP group name attribute, default value cn |
| LDAP_GROUP_MEMBER_ATTRIBUTE| Yes | LDAP group member attribute, default value uniqueMember|
| LDAP_GROUP_UPPERCASE | Yes | Convert LDAP groups to uppercase |
| LDAP_GROUP_LOWERCASE | Yes | Convert LDAP groups to lowercase |
### If you want the server to run on TLS use the following environment variables
| Environment Variable | Optional | Description |
| --- | --- | --- |
| UI_SSL_ENABLED | Yes | true or false |
| UI_SSL_TYPE | Yes | JKS or PKCS12 - default is JKS |
| UI_SSL_KEYSTORE_LOCATION | Yes | Location of Keystore |
| UI_SSL_KEYSTORE_PASSWORD | Yes | Keystore password |
| UI_SSL_REQUIRED | Yes | true or false. Whether SSL is required - Default true |


