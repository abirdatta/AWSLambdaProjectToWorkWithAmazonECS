package com.ad.lambda.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ad.lambda.handler.LambdaFunctionHandlerForEcs;
import com.ad.lambda.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerForEcsTest {

    private static ECSServiceRequest ecsServiceRequest;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        File jsonFile = new File("create-payload.json");
        String jsonString = FileUtils.readFileToString(jsonFile);
        
        ecsServiceRequest = new Gson().fromJson(jsonString, ECSServiceRequest.class);
        System.out.println("test output string"+ecsServiceRequest.getClusterName());
        
        /*PortMapping portMapping = new PortMapping();
        portMapping.setContainerPort(8080);
        portMapping.setHostPort(8888);
        portMapping.setProtocol("tcp");

        List<PortMapping> portMappings = new ArrayList<PortMapping>();
        portMappings.add(portMapping);

        ContainerDefinition containerDefinition = new ContainerDefinition();

        containerDefinition.setCpu(1);
        containerDefinition.setEssential(true);
        containerDefinition.setImage("abirdatta/poc:v45");
        containerDefinition.setMemory(500);
        containerDefinition.setName("ecs-test-container");
        containerDefinition.setPortMappings(portMappings);

        List<ContainerDefinition> containerDefinitions = new ArrayList<ContainerDefinition>();
        containerDefinitions.add(containerDefinition);

        ecsServiceRequest = new ECSServiceRequest();
        ecsServiceRequest.setContainerDefinitions(containerDefinitions);
        ecsServiceRequest.setDesiredCount(1);
        ecsServiceRequest.setFamily("ecs-test");
        ecsServiceRequest.setRegion("us-east-1");
        ecsServiceRequest.setServiceName("ecs-test-service");

        ecsServiceRequest.setUpdateOrCreate("create");

        ecsServiceRequest.setVpcCidr("10.0.0.0/16");
        List<Subnet> subnets = new ArrayList<Subnet>();
        Subnet subnet1 = new Subnet();
        subnet1.setCidrBlock("10.0.0.0/24");
        subnet1.setAvailabilityZone("us-east-1a");
        subnets.add(subnet1);
        ecsServiceRequest.setSubnets(subnets);
        ecsServiceRequest.setVpcName("ecs-test-vpc-from-lambda");
        ecsServiceRequest.setClusterName("ecs-test-cluster");

        ecsServiceRequest.setRouteTableDestinationCidr("0.0.0.0/0");

        ECSAutoScalingLaunchConfiguration autoScalingLaunchConfiguration = new ECSAutoScalingLaunchConfiguration();
        autoScalingLaunchConfiguration.setAutolaunchConfigurationName("ecs-instances-launch-configuration");
        autoScalingLaunchConfiguration.setAmiImageId("ami-b2df2ca4");
        autoScalingLaunchConfiguration.setInstanceType("t2.micro");
        autoScalingLaunchConfiguration.setIamInstanceProfile("ecsInstanceRole");
        autoScalingLaunchConfiguration.setUserData("#!/bin/bash" + System.lineSeparator() + "echo ECS_CLUSTER="
                + ecsServiceRequest.getClusterName() + " >> /etc/ecs/ecs.config");
        autoScalingLaunchConfiguration.setKeyName("ec2-jenkins");

        ecsServiceRequest.setAutoScalingLaunchConfiguration(autoScalingLaunchConfiguration);

        ECSAutoScalingGroupConfiguration autoScalingGroupConfiguration = new ECSAutoScalingGroupConfiguration();
        autoScalingGroupConfiguration.setAutoScalingGroupName("ecs-instances-auto-scaling-group");
        autoScalingGroupConfiguration.setAutoScalingLaunchConfigurationName("ecs-instances-launch-configuration");
        List<String> availabilityZones = new ArrayList<String>();
        String availabilityZone1 = "us-east-1a";
        availabilityZones.add(availabilityZone1);
        autoScalingGroupConfiguration.setAvailabilityZones(availabilityZones);
        autoScalingGroupConfiguration.setDesiredCapacity(2);
        autoScalingGroupConfiguration.setMinSize(1);
        autoScalingGroupConfiguration.setMaxSize(2);

        ecsServiceRequest.setAutoScalingGroupConfiguration(autoScalingGroupConfiguration);

        ECSLoadBalancerListener ecsLoadBalancerListener = new ECSLoadBalancerListener();
        ecsLoadBalancerListener.setInstancePort(8888);
        ecsLoadBalancerListener.setInstanceProtocol("HTTP");
        ecsLoadBalancerListener.setLoadBalancerPort(8888);
        ecsLoadBalancerListener.setLoadBalancerProtocol("HTTP");

        List<ECSLoadBalancerListener> loadBalancerListeners = new ArrayList<ECSLoadBalancerListener>();
        loadBalancerListeners.add(ecsLoadBalancerListener);

        ECSLoadBalancerConfiguration ecsLoadBalancerConfiguration = new ECSLoadBalancerConfiguration();
        ecsLoadBalancerConfiguration.setLoadBalancerListeners(loadBalancerListeners);
        ecsLoadBalancerConfiguration.setHealthCheckTarget("HTTP:8888/");
        ecsLoadBalancerConfiguration.setHealthCheckHealthyThreshold(10);
        ecsLoadBalancerConfiguration.setHealthCheckInterval(30);
        ecsLoadBalancerConfiguration.setHealthCheckUnHealthyThreshold(2);
        ecsLoadBalancerConfiguration.setLoadBalancerName("ecs-test-elb");
        ecsLoadBalancerConfiguration.setHealthCheckTimeout(5);

        ecsServiceRequest.setLoadBalancerConfiguration(ecsLoadBalancerConfiguration);

        ecsServiceRequest.setEcsServiceRole("ecsServiceRole");*/

    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("LamdaFunctionHandlerForEcsWithCustomContainerAndPortObjects");

        return ctx;
    }

    @Test
    public void testLambdaFunctionHandlerForEcs() {
        LambdaFunctionHandlerForEcs handler = new LambdaFunctionHandlerForEcs();
        Context ctx = createContext();

        String output = handler.handleRequest(ecsServiceRequest, ctx);

        // TODO: validate output here if needed.
        if (output != null) {
            System.out.println(output.toString());
        }
    }
}
