@ECHO off

ECHO Building project...
call mvn clean package
timeout 3

ECHO Fetching Account ID...
FOR /F "tokens=*" %%g IN ('aws sts get-caller-identity --output text --query "Account"') do (SET ACCOUNT_ID=%%g)
ECHO Account ID = %ACCOUNT_ID%
timeout 3

ECHO Creating IAM role...
call aws iam create-role ^
--role-name kempdnsscaler-execution ^
--assume-role-policy-document file://kempdnsscaler-execution.json
timeout 5

ECHO Attaching managed policy to the created IAM role...
call aws iam attach-role-policy ^
--policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole ^
--role-name kempdnsscaler-execution
timeout 3

ECHO Creating AWS Lambda function...
call aws lambda create-function ^
--region eu-west-2 ^
--function-name kempdnsscaler ^
--zip-file fileb://target/kempdnsscaler-1.0.jar ^
--role arn:aws:iam::%ACCOUNT_ID%:role/kempdnsscaler-execution ^
--handler de.objectiveit.kempdnsscaler.VSManager ^
--runtime java8 ^
--timeout 30 ^
--memory-size 512

ECHO "Finished!"
