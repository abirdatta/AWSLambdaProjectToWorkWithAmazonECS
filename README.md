# AWS Lambda Function To Update ECS service
This is a AWS Lambda handler function to update ECS services. This function will take an input JSON payload in the following form and update the EC2 container service and associated tasks mentioned in the payload.
Example payload - 

{
  "accessKeyId": "your access key ID which has the privilege to update ECS",
  "secretAccessKey": "secret access key for the id mentioned above",
  "updateOrCreate": "update",
  "clusterName": "default",
  "serviceName": "simple-webapp-service",
  "desiredCount": 1,
  "containerDefinitions": [
    {
      "memory": 500,
      "portMappings": [
        {
          "hostPort": 8888,
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "name": "simple-webapp-container",
      "image": "abirdatta/poc:v59",
	  "essential": true,
      "cpu": 1
    }
  ],
  "family": "task-name"
}
