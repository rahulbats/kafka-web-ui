# kafka-web-ui
The only web UI for kafka messages which uses the SASL usersname/passwords directly as logins.
This your team to view messages and admins to assign appropriate ACLs to them.

You can run this as a docker container `docker run rahulbats/kafka-web-ui:1.0.0 -e ALLOWED_USERS=[SASL USERS] SECURITY_PROTOCOL=[SECURITY PROTOCOL] BOOTSTRAP_SERVERS=[BOOTSTRAP SERVERS] SCHEMA_REGISTRY_URL=[OPTIONAL SCHEMA REGISTRY URL]` 
