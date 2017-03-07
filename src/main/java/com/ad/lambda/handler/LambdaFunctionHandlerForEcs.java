package com.ad.lambda.handler;

import java.util.ArrayList;
import java.util.List;

import com.ad.lambda.model.ECSServiceRequest;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ecs.AmazonECSClient;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionResult;
import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import com.amazonaws.services.ecs.model.UpdateServiceResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.ecs.model.ContainerDefinition;
import com.amazonaws.services.ecs.model.PortMapping;

public class LambdaFunctionHandlerForEcs implements RequestHandler<ECSServiceRequest, String> {

    public String handleRequest(ECSServiceRequest eCSServiceRequest, Context context) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(eCSServiceRequest.getAccessKeyId(),
                eCSServiceRequest.getSecretAccessKey());

        AmazonECSClient ecsClient = (AmazonECSClient) AmazonECSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();

        RegisterTaskDefinitionResult registerTaskDefinitionResult = ecsClient
                .registerTaskDefinition(createRegisterTaskDefinitionRequest(eCSServiceRequest));

        UpdateServiceRequest updateServiceRequest = createUpdateServiceRequest(
                registerTaskDefinitionResult.getTaskDefinition().getFamily() + ":"
                        + registerTaskDefinitionResult.getTaskDefinition().getRevision(),
                eCSServiceRequest);

        UpdateServiceResult updateServiceResult = ecsClient.updateService(updateServiceRequest);

        return updateServiceResult.getService().getStatus();
    }

    private RegisterTaskDefinitionRequest createRegisterTaskDefinitionRequest(ECSServiceRequest eCSServiceRequest) {
        RegisterTaskDefinitionRequest regTaskDefReq = new RegisterTaskDefinitionRequest();
        regTaskDefReq.setFamily(eCSServiceRequest.getFamily());
        regTaskDefReq.setContainerDefinitions(createContainerDefinitions(eCSServiceRequest));
        return regTaskDefReq;
    }

    private List<ContainerDefinition> createContainerDefinitions(ECSServiceRequest eCSServiceRequest) {
        List<ContainerDefinition> containerDefinitions = new ArrayList<ContainerDefinition>();
        for (com.ad.lambda.model.ContainerDefinition contdef : eCSServiceRequest.getContainerDefinitions()) {
            ContainerDefinition containerDefinition = new ContainerDefinition();
            containerDefinition.setMemory(contdef.getMemory());
            containerDefinition.setEssential(contdef.isEssential());
            
            List<PortMapping> portMappings = new ArrayList<PortMapping>();
            
            for (com.ad.lambda.model.PortMapping portMappingTemp: contdef.getPortMappings()) {
                PortMapping portMapping = new PortMapping();
                portMapping.setContainerPort(portMappingTemp.getContainerPort());
                portMapping.setHostPort(portMappingTemp.getHostPort());
                portMapping.setProtocol(portMappingTemp.getProtocol());
                portMappings.add(portMapping);
            }
            
            containerDefinition.setPortMappings(portMappings);
            containerDefinition.setName(contdef.getName());
            containerDefinition.setImage(contdef.getImage());
            containerDefinition.setCpu(contdef.getCpu());

            containerDefinitions.add(containerDefinition);
        }
        return containerDefinitions;
    }

    private UpdateServiceRequest createUpdateServiceRequest(String taskDefQualifiedName,
            ECSServiceRequest eCSServiceRequest) {
        UpdateServiceRequest updateServiceRequest = new UpdateServiceRequest();
        updateServiceRequest.setCluster(eCSServiceRequest.getClusterName());
        updateServiceRequest.setService(eCSServiceRequest.getServiceName());
        updateServiceRequest.setTaskDefinition(taskDefQualifiedName);
        updateServiceRequest.setDesiredCount(eCSServiceRequest.getDesiredCount());
        return updateServiceRequest;
    }
}
