# AWS Lambda Function to Create or Update ECS service
This is a AWS Lambda handler function to create or update ECS services. This function will take an input JSON payload. Sample of create and update json payloads are there in the codebase.
When you create this LambdaFunction, use a role having full access to VPC, EC2 CS, AutoScaling Full Acess, S3 full acess, Cloudwatch logs full access.
