#!/bin/sh
cd client
ng build  --base-href /api/dist/ --prod
cd ../server
./gradlew clean build