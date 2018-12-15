#!/bin/sh

echo Building project...
mvn clean package
sleep 3

echo Fetching Account ID...
ACCOUNT_ID=$(aws sts get-caller-identity --output text --query "Account")
echo Account ID = ${ACCOUNT_ID}
sleep 3

echo Creating IAM role...
aws iam create-role \
--role-name kempdnsscaler-execution \
--assume-role-policy-document file://kempdnsscaler-execution.json
sleep 5

echo Attaching managed policy to the created IAM role...
aws iam attach-role-policy \
--policy-arn arn:aws:iam::aws:policy/AWSLambdaFullAccess \
--role-name kempdnsscaler-execution
sleep 3

echo Creating AWS Lambda function...
aws lambda create-function \
--region eu-central-1 \
--function-name kempdnsscaler \
--zip-file fileb://target/kempdnsscaler-1.0.jar \
--role arn:aws:iam::${ACCOUNT_ID}:role/kempdnsscaler-execution \
--handler de.objectiveit.kempdnsscaler.VSManager \
--runtime java8 \
--timeout 30 \
--memory-size 512

echo "Finished!"
