@ECHO off

ECHO Building project...
call mvn clean package
timeout 3

ECHO Updating AWS Lambda function...
call aws lambda update-function-code ^
--region eu-central-1 ^
--function-name kempdnsscaler ^
--zip-file fileb://target/kempdnsscaler-1.0.jar ^


ECHO "Finished!"
