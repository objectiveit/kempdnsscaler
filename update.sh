#!/bin/sh

echo Building project...
mvn clean package
sleep 3

echo Updating AWS Lambda function...
aws lambda update-function-code \
--region eu-central-1 \
--function-name kempdnsscaler \
--zip-file fileb://target/kempdnsscaler-1.0.jar

ECHO "Finished!"
