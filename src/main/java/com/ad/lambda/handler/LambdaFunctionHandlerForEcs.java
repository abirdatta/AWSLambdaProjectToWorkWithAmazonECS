package com.ad.lambda.handler;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

import com.ad.lambda.model.ECSAutoScalingGroupConfiguration;
import com.ad.lambda.model.ECSAutoScalingLaunchConfiguration;
import com.ad.lambda.model.ECSLoadBalancerConfiguration;
import com.ad.lambda.model.ECSLoadBalancerListener;
import com.ad.lambda.model.ECSServiceRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.BlockDeviceMapping;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationResult;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.Ebs;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AttachInternetGatewayRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateDhcpOptionsRequest;
import com.amazonaws.services.ec2.model.CreateDhcpOptionsResult;
import com.amazonaws.services.ec2.model.CreateInternetGatewayResult;
import com.amazonaws.services.ec2.model.CreateNetworkAclRequest;
import com.amazonaws.services.ec2.model.CreateNetworkAclResult;
import com.amazonaws.services.ec2.model.CreateRouteRequest;
import com.amazonaws.services.ec2.model.CreateRouteTableRequest;
import com.amazonaws.services.ec2.model.CreateRouteTableResult;
import com.amazonaws.services.ec2.model.CreateSubnetRequest;
import com.amazonaws.services.ec2.model.CreateSubnetResult;
import com.amazonaws.services.ec2.model.CreateVpcRequest;
import com.amazonaws.services.ec2.model.CreateVpcResult;
import com.amazonaws.services.ec2.model.DhcpConfiguration;
import com.amazonaws.services.ec2.model.ModifySubnetAttributeRequest;
import com.amazonaws.services.ec2.model.ModifyVpcAttributeRequest;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.NetworkAclAssociation;
import com.amazonaws.services.ec2.model.NetworkAclEntry;
import com.amazonaws.services.ec2.model.PortRange;
import com.amazonaws.services.ec2.model.Route;
import com.amazonaws.services.ec2.model.RouteState;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.RouteTableAssociation;
import com.amazonaws.services.ec2.model.RuleAction;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.ad.lambda.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Tenancy;
import com.amazonaws.services.ecs.AmazonECSClient;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionResult;
import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import com.amazonaws.services.ecs.model.UpdateServiceResult;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConnectionDraining;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.CrossZoneLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerAttributes;
import com.amazonaws.services.elasticloadbalancing.model.ModifyLoadBalancerAttributesRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.ecs.model.ContainerDefinition;
import com.amazonaws.services.ecs.model.CreateClusterRequest;
import com.amazonaws.services.ecs.model.CreateClusterResult;
import com.amazonaws.services.ecs.model.CreateServiceRequest;
import com.amazonaws.services.ecs.model.CreateServiceResult;
import com.amazonaws.services.ecs.model.LoadBalancer;
import com.amazonaws.services.ecs.model.PortMapping;

/**
 * @author abirdatta
 *
 */
public class LambdaFunctionHandlerForEcs implements RequestHandler<ECSServiceRequest, String> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String handleRequest(ECSServiceRequest eCSServiceRequest, Context context) {

        AWSCredentials awsCreds = new EnvironmentVariableCredentialsProvider().getCredentials();

        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCreds);

        AmazonECSClient ecsClient = (AmazonECSClient) AmazonECSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider).withRegion(eCSServiceRequest.getRegion()).build();

        AmazonEC2Client ec2Client = (AmazonEC2Client) AmazonEC2ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider).withRegion(eCSServiceRequest.getRegion()).build();

        AmazonAutoScalingClient amazonAutoScalingClient = (AmazonAutoScalingClient) AmazonAutoScalingClientBuilder
                .standard().withCredentials(awsCredentialsProvider).withRegion(eCSServiceRequest.getRegion()).build();

        AmazonElasticLoadBalancingClient elasticLoadBalancingClient = (AmazonElasticLoadBalancingClient) AmazonElasticLoadBalancingClientBuilder
                .standard().withCredentials(awsCredentialsProvider).withRegion(eCSServiceRequest.getRegion()).build();
        
        AmazonCloudFormationClient cloudformationClient = (AmazonCloudFormationClient) AmazonCloudFormationClientBuilder
                .standard().withCredentials(awsCredentialsProvider).withRegion(eCSServiceRequest.getRegion()).build();

        if (eCSServiceRequest.getUpdateOrCreate().equalsIgnoreCase("update")) {

            RegisterTaskDefinitionResult registerTaskDefinitionResult = ecsClient
                    .registerTaskDefinition(createRegisterTaskDefinitionRequest(eCSServiceRequest));

            UpdateServiceRequest updateServiceRequest = createUpdateServiceRequest(
                    registerTaskDefinitionResult.getTaskDefinition().getFamily() + ":"
                            + registerTaskDefinitionResult.getTaskDefinition().getRevision(),
                    eCSServiceRequest);

            UpdateServiceResult updateServiceResult = ecsClient.updateService(updateServiceRequest);
        } else {

            CreateVpcResult createVpcResult = createVPC(ec2Client, eCSServiceRequest);
            logger.info("New VPC Created with id --" + createVpcResult.getVpc().getVpcId());
            modifyVpcAttributes(ec2Client, createVpcResult);

            CreateInternetGatewayResult createInternetGatewayResult = createInternetGateway(ec2Client);
            logger.info("New IG Created with id --"
                    + createInternetGatewayResult.getInternetGateway().getInternetGatewayId());

            attachInternetGatewayWIthVpc(createVpcResult, ec2Client, createInternetGatewayResult);
            logger.info("Internet Gateway - " + createInternetGatewayResult.getInternetGateway().getInternetGatewayId()
                    + " attached to VPC - " + createVpcResult.getVpc().getVpcId());

            attachInternetGatewayToTheRouteTable(ec2Client, createVpcResult, createInternetGatewayResult,
                    eCSServiceRequest);

            List<String> subnetIds = new ArrayList<String>();

            for (Subnet subnet : eCSServiceRequest.getSubnets()) {
                CreateSubnetResult createSubnetResult = createVpcSubnet(createVpcResult, ec2Client, subnet);
                logger.info("New Subnet Created with id --" + createSubnetResult.getSubnet().getSubnetId());

                subnetIds.add(createSubnetResult.getSubnet().getSubnetId());
            }

            List<String> securityGroupIdsInVpc = getSecurityGroupsInVpcAndUpdateIngress(ec2Client, createVpcResult);

            CreateClusterResult createClusterResult = createEcsCluster(eCSServiceRequest, ecsClient);

            CreateLaunchConfigurationResult createLaunchConfigurationResult = createAutoLaunchConfigurationForEcs(
                    ec2Client, amazonAutoScalingClient, createClusterResult, securityGroupIdsInVpc,
                    eCSServiceRequest.getAutoScalingLaunchConfiguration());

            List<Instance> instances = createAutoScalingGroupForEcs(amazonAutoScalingClient, subnetIds,
                    eCSServiceRequest.getAutoScalingGroupConfiguration());

            createLoadBalancersAndRegisterWithEcsInstances(elasticLoadBalancingClient, securityGroupIdsInVpc, subnetIds,
                    instances, eCSServiceRequest.getLoadBalancerConfiguration());

            RegisterTaskDefinitionResult registerTaskDefinitionResult = ecsClient
                    .registerTaskDefinition(createRegisterTaskDefinitionRequest(eCSServiceRequest));

            CreateServiceResult createServiceResult = createEcsService(eCSServiceRequest, ecsClient,
                    registerTaskDefinitionResult);

            logger.info("ECS service created" + "\n name - " + createServiceResult.getService().getServiceName()
                    + "\n desired count - " + createServiceResult.getService().getDesiredCount() + "\n status - "
                    + createServiceResult.getService().getStatus());

            logger.info(createServiceResult.getService().toString());
        }
        return "Sucess";
    }

    private CreateServiceResult createEcsService(ECSServiceRequest eCSServiceRequest, AmazonECSClient ecsClient,
            RegisterTaskDefinitionResult registerTaskDefinitionResult) {
        LoadBalancer ecsLoadbalancer = new LoadBalancer()
                .withLoadBalancerName(eCSServiceRequest.getLoadBalancerConfiguration().getLoadBalancerName())
                .withContainerName(eCSServiceRequest.getContainerDefinitions().get(0).getName()).withContainerPort(
                        eCSServiceRequest.getContainerDefinitions().get(0).getPortMappings().get(0).getContainerPort());
        List<LoadBalancer> ecsLoadBalancers = new ArrayList<LoadBalancer>();
        ecsLoadBalancers.add(ecsLoadbalancer);
        CreateServiceRequest createServiceRequest = new CreateServiceRequest()
                .withCluster(eCSServiceRequest.getClusterName()).withDesiredCount(eCSServiceRequest.getDesiredCount()).withLoadBalancers(ecsLoadBalancers)
                .withTaskDefinition(registerTaskDefinitionResult.getTaskDefinition().getFamily())
                .withServiceName(eCSServiceRequest.getServiceName()).withRole(eCSServiceRequest.getEcsServiceRole());

        CreateServiceResult createServiceResult = ecsClient.createService(createServiceRequest);
        return createServiceResult;
    }

    private void createLoadBalancersAndRegisterWithEcsInstances(
            AmazonElasticLoadBalancingClient elasticLoadBalancingClient, List<String> securityGroupIdsInVpc,
            List<String> subnetIds, List<Instance> ecsInstances,
            ECSLoadBalancerConfiguration ecsLoadBalancerConfiguration) {

        List<Listener> listeners = new ArrayList<Listener>();
        for (ECSLoadBalancerListener ecsLbListener : ecsLoadBalancerConfiguration.getLoadBalancerListeners()) {
            Listener listener = new Listener().withInstancePort(ecsLbListener.getInstancePort())
                    .withInstanceProtocol(ecsLbListener.getInstanceProtocol())
                    .withLoadBalancerPort(ecsLbListener.getLoadBalancerPort())
                    .withProtocol(ecsLbListener.getLoadBalancerProtocol());
            listeners.add(listener);
        }

        CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest()
                .withLoadBalancerName(ecsLoadBalancerConfiguration.getLoadBalancerName())
                .withSecurityGroups(securityGroupIdsInVpc).withSubnets(subnetIds).withListeners(listeners);

        CreateLoadBalancerResult createLoadBalancerResult = elasticLoadBalancingClient
                .createLoadBalancer(createLoadBalancerRequest);

        ConnectionDraining connectionDraining = new ConnectionDraining().withEnabled(true).withTimeout(300);
        CrossZoneLoadBalancing crossZoneLoadBalancing = new CrossZoneLoadBalancing().withEnabled(true);
        LoadBalancerAttributes loadBalancerAttributes = new LoadBalancerAttributes()
                .withConnectionDraining(connectionDraining).withCrossZoneLoadBalancing(crossZoneLoadBalancing);
        ModifyLoadBalancerAttributesRequest modifyLoadBalancerAttributesRequest = new ModifyLoadBalancerAttributesRequest()
                .withLoadBalancerName(ecsLoadBalancerConfiguration.getLoadBalancerName())
                .withLoadBalancerAttributes(loadBalancerAttributes);
        elasticLoadBalancingClient.modifyLoadBalancerAttributes(modifyLoadBalancerAttributesRequest);

        logger.info("Load Balancer created with DNS Name -- ***** " + createLoadBalancerResult.getDNSName() + "*****");

        registerLoadBalancerWithEc2Instances(elasticLoadBalancingClient, ecsInstances, ecsLoadBalancerConfiguration);
    }

    private void registerLoadBalancerWithEc2Instances(AmazonElasticLoadBalancingClient elasticLoadBalancingClient,
            List<Instance> ecsInstances, ECSLoadBalancerConfiguration ecsLoadBalancerConfiguration) {
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> lbInstances = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
        com.amazonaws.services.elasticloadbalancing.model.Instance lbInstance;
        for (Instance instance : ecsInstances) {
            logger.info("Instance id of Auto Scaling Group - " + instance.getInstanceId());
            logger.info("Health of instance - " + instance.getInstanceId() + " is - " + instance.getHealthStatus());
            lbInstance = new com.amazonaws.services.elasticloadbalancing.model.Instance()
                    .withInstanceId(instance.getInstanceId());
            lbInstances.add(lbInstance);
        }

        RegisterInstancesWithLoadBalancerRequest registerInstancesWithLoadBalancerRequest = new RegisterInstancesWithLoadBalancerRequest()
                .withLoadBalancerName(ecsLoadBalancerConfiguration.getLoadBalancerName()).withInstances(lbInstances);

        RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancerResult = elasticLoadBalancingClient
                .registerInstancesWithLoadBalancer(registerInstancesWithLoadBalancerRequest);

        logger.info("Load Balancer ecs-test-elb registered with "
                + registerInstancesWithLoadBalancerResult.getInstances().size() + " instances");

        HealthCheck healthCheck = new HealthCheck().withTarget(ecsLoadBalancerConfiguration.getHealthCheckTarget())
                .withHealthyThreshold(ecsLoadBalancerConfiguration.getHealthCheckHealthyThreshold())
                .withUnhealthyThreshold(ecsLoadBalancerConfiguration.getHealthCheckUnHealthyThreshold())
                .withInterval(ecsLoadBalancerConfiguration.getHealthCheckInterval())
                .withTimeout(ecsLoadBalancerConfiguration.getHealthCheckTimeout());
        ConfigureHealthCheckRequest configureHealthCheckRequest = new ConfigureHealthCheckRequest()
                .withLoadBalancerName(ecsLoadBalancerConfiguration.getLoadBalancerName()).withHealthCheck(healthCheck);

        elasticLoadBalancingClient.configureHealthCheck(configureHealthCheckRequest);
        logger.info("Configured health Check for Load Balancer ecs-test-elb.");
    }

    private List<Instance> createAutoScalingGroupForEcs(AmazonAutoScalingClient amazonAutoScalingClient,
            List<String> subnetIds, ECSAutoScalingGroupConfiguration autoScalingGroupConfiguration) {
        CreateAutoScalingGroupRequest createAutoScalingGroupRequest = new CreateAutoScalingGroupRequest()
                .withLaunchConfigurationName(autoScalingGroupConfiguration.getAutoScalingLaunchConfigurationName())
                .withAutoScalingGroupName(autoScalingGroupConfiguration.getAutoScalingGroupName())
                .withDesiredCapacity(autoScalingGroupConfiguration.getDesiredCapacity())
                .withVPCZoneIdentifier(String.join(",", subnetIds))
                .withMinSize(autoScalingGroupConfiguration.getMinSize())
                .withMaxSize(autoScalingGroupConfiguration.getMaxSize())
                .withAvailabilityZones(autoScalingGroupConfiguration.getAvailabilityZones());

        amazonAutoScalingClient.createAutoScalingGroup(createAutoScalingGroupRequest);
        logger.info("Auto scaling group named ecs-instances-auto-scaling-group created.");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<Instance> instances = getInstancesLaunchedByAutoScalingGroup(amazonAutoScalingClient,
                autoScalingGroupConfiguration.getAutoScalingGroupName());
        return instances;
    }

    private List<Instance> getInstancesLaunchedByAutoScalingGroup(AmazonAutoScalingClient amazonAutoScalingClient,
            String autoScalingGroupName) {
        List<Instance> instances = new ArrayList<Instance>();

        List<String> autoScalingGroupnames = new ArrayList<String>();
        autoScalingGroupnames.add(autoScalingGroupName);
        DescribeAutoScalingGroupsRequest describeAutoScalingGroupsRequest = new DescribeAutoScalingGroupsRequest()
                .withAutoScalingGroupNames(autoScalingGroupnames);

        for (AutoScalingGroup autoScalingGroup : amazonAutoScalingClient
                .describeAutoScalingGroups(describeAutoScalingGroupsRequest).getAutoScalingGroups()) {
            instances = autoScalingGroup.getInstances();
            logger.info("Count of instances in the auto scaling Group - ecs-instances-auto-scaling-group is "
                    + instances.size());
            break;
        }
        return instances;
    }

    private CreateLaunchConfigurationResult createAutoLaunchConfigurationForEcs(AmazonEC2Client ec2Client,
            AmazonAutoScalingClient amazonAutoScalingClient, CreateClusterResult createClusterResult,
            List<String> securityGroupIdsInVpc, ECSAutoScalingLaunchConfiguration ecsAutoScalingLaunchConfiguration) {

        String userDataText = "#!/bin/bash" + System.lineSeparator() + "echo ECS_CLUSTER="
                + createClusterResult.getCluster().getClusterName() + " >> /etc/ecs/ecs.config";

        String base64encodedUserData = Base64.getEncoder().encodeToString(userDataText.getBytes());

        CreateLaunchConfigurationRequest createLaunchConfigurationRequest = new CreateLaunchConfigurationRequest()
                .withLaunchConfigurationName(ecsAutoScalingLaunchConfiguration.getAutolaunchConfigurationName())
                .withImageId(ecsAutoScalingLaunchConfiguration.getAmiImageId())
                .withInstanceType(ecsAutoScalingLaunchConfiguration.getInstanceType())
                .withIamInstanceProfile(ecsAutoScalingLaunchConfiguration.getIamInstanceProfile())
                .withUserData(base64encodedUserData).withSecurityGroups(securityGroupIdsInVpc)
                .withKeyName(ecsAutoScalingLaunchConfiguration.getKeyName());

        CreateLaunchConfigurationResult createLaunchConfigurationResult = amazonAutoScalingClient
                .createLaunchConfiguration(createLaunchConfigurationRequest);
        logger.info("Auto Launch Configuration created with name - ecs-instances-launch-configuration");
        return createLaunchConfigurationResult;
    }

    /**
     * This method is used to get the security group associated with the VPC and
     * update the inbound mapping in the security group
     * 
     * @param ec2Client
     * @param createVpcResult
     * @return
     */
    private List<String> getSecurityGroupsInVpcAndUpdateIngress(AmazonEC2Client ec2Client,
            CreateVpcResult createVpcResult) {
        List<String> securityGroupIdsForVpc = new ArrayList<String>();
        for (SecurityGroup securityGroup : ec2Client.describeSecurityGroups().getSecurityGroups()) {
            if (securityGroup.getVpcId().equalsIgnoreCase(createVpcResult.getVpc().getVpcId())) {
                securityGroupIdsForVpc.add(securityGroup.getGroupId());
                logger.info("Security Group - " + securityGroup.getGroupId() + " in the vpc "
                        + createVpcResult.getVpc().getVpcId() + " to be added to the launch configuration.");

                updateSecurityGroup(ec2Client, securityGroup);
            }
        }
        return securityGroupIdsForVpc;
    }

    /**
     * This method is used to add a all traffic from everywhere ingress to
     * security group
     * 
     * @param ec2Client
     * @param securityGroup
     */
    private void updateSecurityGroup(AmazonEC2Client ec2Client, SecurityGroup securityGroup) {
        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest();
        authorizeSecurityGroupIngressRequest.setGroupId(securityGroup.getGroupId());
        authorizeSecurityGroupIngressRequest.setIpProtocol("-1");
        authorizeSecurityGroupIngressRequest.setFromPort(1);
        authorizeSecurityGroupIngressRequest.setToPort(65535);
        authorizeSecurityGroupIngressRequest.setCidrIp("0.0.0.0/0");

        ec2Client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
    }

    /**
     * Gets the route table associated with the vpc and attach internet gateway
     * with it
     * 
     * @param ec2Client
     * @param createVpcResult
     * @param createInternetGatewayResult
     */
    private void attachInternetGatewayToTheRouteTable(AmazonEC2Client ec2Client, CreateVpcResult createVpcResult,
            CreateInternetGatewayResult createInternetGatewayResult, ECSServiceRequest eCSServiceRequest) {
        for (RouteTable routeTable : ec2Client.describeRouteTables().getRouteTables()) {
            if (routeTable.getVpcId().equalsIgnoreCase(createVpcResult.getVpc().getVpcId())) {
                logger.info("Route Table for the new created Vpc is - " + routeTable.getRouteTableId());
                CreateRouteRequest createRouteRequest = new CreateRouteRequest()
                        .withGatewayId(createInternetGatewayResult.getInternetGateway().getInternetGatewayId())
                        .withRouteTableId(routeTable.getRouteTableId())
                        .withDestinationCidrBlock(eCSServiceRequest.getRouteTableDestinationCidr());
                ec2Client.createRoute(createRouteRequest);

                logger.info("Internet gateway route associated with the route table");
                break;
            }
        }
    }

    /**
     * Creates and modifies subnets in the VPC with the passed in cidr block and
     * availability zone
     * 
     * @param createVpcResult
     * @param ec2Client
     * @param subnet
     * @return
     */
    private CreateSubnetResult createVpcSubnet(CreateVpcResult createVpcResult, AmazonEC2Client ec2Client,
            Subnet subnet) {
        CreateSubnetRequest createSubnetRequest = new CreateSubnetRequest(createVpcResult.getVpc().getVpcId(),
                subnet.getCidrBlock()).withAvailabilityZone(subnet.getAvailabilityZone());

        CreateSubnetResult createSubnetResult = ec2Client.createSubnet(createSubnetRequest);

        ModifySubnetAttributeRequest modifySubnetAttributeRequest = new ModifySubnetAttributeRequest()
                .withMapPublicIpOnLaunch(true).withSubnetId(createSubnetResult.getSubnet().getSubnetId());

        ec2Client.modifySubnetAttribute(modifySubnetAttributeRequest);

        return createSubnetResult;
    }

    /**
     * This method is used to create internet gateway
     * 
     * @param ec2Client
     * @return
     */
    private CreateInternetGatewayResult createInternetGateway(AmazonEC2Client ec2Client) {

        CreateInternetGatewayResult createInternetGatewayResult = ec2Client.createInternetGateway();
        return createInternetGatewayResult;
    }

    /**
     * This method is used to attach internet gateway to the VPC
     * 
     * @param createVpcResult
     * @param ec2Client
     * @param createInternetGatewayResult
     */
    private void attachInternetGatewayWIthVpc(CreateVpcResult createVpcResult, AmazonEC2Client ec2Client,
            CreateInternetGatewayResult createInternetGatewayResult) {
        AttachInternetGatewayRequest attachInternetGatewayRequest = new AttachInternetGatewayRequest()
                .withInternetGatewayId(createInternetGatewayResult.getInternetGateway().getInternetGatewayId())
                .withVpcId(createVpcResult.getVpc().getVpcId());

        ec2Client.attachInternetGateway(attachInternetGatewayRequest);
    }

    /**
     * This method is used to create a VPC with the passed in CIDR
     * 
     * @param ec2Client
     * @param eCSServiceRequest
     * @return
     */
    private CreateVpcResult createVPC(AmazonEC2Client ec2Client, ECSServiceRequest eCSServiceRequest) {

        CreateVpcRequest createVpcRequest = new CreateVpcRequest(eCSServiceRequest.getVpcCidr())
                .withInstanceTenancy(Tenancy.Default);
        CreateVpcResult createVpcResult = ec2Client.createVpc(createVpcRequest);

        return createVpcResult;
    }

    /**
     * This method is used to modify vpc, i.e. enable DNS Hostnames in VPC
     * 
     * @param ec2Client
     * @param createVpcResult
     */
    private void modifyVpcAttributes(AmazonEC2Client ec2Client, CreateVpcResult createVpcResult) {
        ModifyVpcAttributeRequest modifyVpcAttributeRequest = new ModifyVpcAttributeRequest()
                .withVpcId(createVpcResult.getVpc().getVpcId()).withEnableDnsHostnames(true);

        ec2Client.modifyVpcAttribute(modifyVpcAttributeRequest);
    }

    private CreateClusterResult createEcsCluster(ECSServiceRequest eCSServiceRequest, AmazonECSClient ecsClient) {
        CreateClusterRequest createClusterRequest = new CreateClusterRequest();
        createClusterRequest.setClusterName(eCSServiceRequest.getClusterName());
        CreateClusterResult createClusterResult = ecsClient.createCluster(createClusterRequest);
        logger.info("Cluster created with name - " + createClusterResult.getCluster().getClusterName());
        return createClusterResult;
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

            for (com.ad.lambda.model.PortMapping portMappingTemp : contdef.getPortMappings()) {
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

    /**
     * This method is used to create the update service request for updating ecs
     * service.
     * 
     * @param taskDefQualifiedName
     * @param eCSServiceRequest
     * @return
     */
    private UpdateServiceRequest createUpdateServiceRequest(String taskDefQualifiedName,
            ECSServiceRequest eCSServiceRequest) {
        UpdateServiceRequest updateServiceRequest = new UpdateServiceRequest();
        updateServiceRequest.setCluster(eCSServiceRequest.getClusterName());
        updateServiceRequest.setService(eCSServiceRequest.getServiceName());
        updateServiceRequest.setTaskDefinition(taskDefQualifiedName);
        updateServiceRequest.setDesiredCount(eCSServiceRequest.getDesiredCount());
        return updateServiceRequest;
    }

    /**
     * method to create
     * 
     * @param ec2Client
     * @return CreateDhcpOptionsResult Not used, but kept for reference
     */
    private CreateDhcpOptionsResult createDhcpConfigurations(AmazonEC2Client ec2Client) {
        DhcpConfiguration dhcpConfigurationDomainName = new DhcpConfiguration();
        dhcpConfigurationDomainName.setKey("domain-name");
        List<String> domainNameValues = new ArrayList<String>();
        domainNameValues.add("ec2.internal");
        dhcpConfigurationDomainName.setValues(domainNameValues);

        DhcpConfiguration dhcpConfigurationDomainNameServers = new DhcpConfiguration();
        dhcpConfigurationDomainNameServers.setKey("domain-name-servers");
        List<String> domainNameServerValues = new ArrayList<String>();
        domainNameServerValues.add("AmazonProvidedDNS");
        dhcpConfigurationDomainNameServers.setValues(domainNameServerValues);

        List<DhcpConfiguration> dhcpConfigurations = new ArrayList<DhcpConfiguration>();
        dhcpConfigurations.add(dhcpConfigurationDomainName);
        dhcpConfigurations.add(dhcpConfigurationDomainNameServers);

        CreateDhcpOptionsRequest createDhcpOptionsRequest = new CreateDhcpOptionsRequest()
                .withDhcpConfigurations(dhcpConfigurations);
        CreateDhcpOptionsResult createDhcpOptionsResult = ec2Client.createDhcpOptions(createDhcpOptionsRequest);

        return createDhcpOptionsResult;
    }

    /**
     * Unused method. Kept for future reference. Used to create Route Table
     * 
     * @param createVpcResult
     * @param ec2Client
     * @param createSubnetResult
     * @param createInternetGatewayResult
     * @return
     */
    private CreateRouteTableResult createRouteTable(CreateVpcResult createVpcResult, AmazonEC2Client ec2Client,
            CreateSubnetResult createSubnetResult, CreateInternetGatewayResult createInternetGatewayResult) {
        CreateRouteTableRequest createRouteTableRequest = new CreateRouteTableRequest()
                .withVpcId(createVpcResult.getVpc().getVpcId());

        RouteTableAssociation routeTableAssociation = new RouteTableAssociation()
                .withSubnetId(createSubnetResult.getSubnet().getSubnetId());
        List<RouteTableAssociation> routeTableAssociations = new ArrayList<RouteTableAssociation>();
        routeTableAssociations.add(routeTableAssociation);

        Route route = new Route();
        route.setGatewayId(createInternetGatewayResult.getInternetGateway().getInternetGatewayId());
        route.setState(RouteState.Active);
        List<Route> routes = new ArrayList<Route>();
        routes.add(route);

        Tag tag = new Tag("name", "ecs-lambda-route-table");
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(tag);

        RouteTable routeTable = new RouteTable();
        routeTable.setAssociations(routeTableAssociations);
        routeTable.setRoutes(routes);
        routeTable.setTags(tags);

        CreateRouteTableResult createRouteTableResult = ec2Client.createRouteTable(createRouteTableRequest)
                .withRouteTable(routeTable);
        return createRouteTableResult;
    }

    /**
     * Unused method. Kept for future reference. Used to create Netwrok ACL
     * 
     * @param createVpcResult
     * @param ec2Client
     * @param createSubnetResult
     * @return
     */
    private CreateNetworkAclResult createNetworkAcl(CreateVpcResult createVpcResult, AmazonEC2Client ec2Client,
            CreateSubnetResult createSubnetResult) {
        CreateNetworkAclRequest createNetworkAclRequest = new CreateNetworkAclRequest()
                .withVpcId(createVpcResult.getVpc().getVpcId());

        NetworkAclAssociation networkAclAssociation = new NetworkAclAssociation()
                .withSubnetId(createSubnetResult.getSubnet().getSubnetId());
        List<NetworkAclAssociation> networkAclAssociations = new ArrayList<NetworkAclAssociation>();
        networkAclAssociations.add(networkAclAssociation);

        NetworkAcl networkAcl = new NetworkAcl();
        networkAcl.setAssociations(networkAclAssociations);

        PortRange portRange = new PortRange().withFrom(0).withTo(65535);
        NetworkAclEntry aclEntry = new NetworkAclEntry().withRuleNumber(100).withPortRange(portRange)
                .withProtocol("ALL").withCidrBlock("0.0.0.0/0").withRuleAction(RuleAction.Allow);
        List<NetworkAclEntry> aclEntries = new ArrayList<NetworkAclEntry>();
        aclEntries.add(aclEntry);
        networkAcl.setEntries(aclEntries);

        CreateNetworkAclResult createNetworkAclResult = ec2Client.createNetworkAcl(createNetworkAclRequest)
                .withNetworkAcl(networkAcl);

        return createNetworkAclResult;
    }

    /**
     * not used. create block device mapping for auto scaling group
     * 
     * @return
     */
    private List<BlockDeviceMapping> createEbsAndBlockDeviceMappingsForEcsAutoScalingGroup() {
        Ebs ebs1 = new Ebs();
        ebs1.setVolumeSize(22);
        ebs1.setVolumeType("gp2");
        ebs1.setEncrypted(false);
        ebs1.setDeleteOnTermination(true);

        Ebs ebs2 = new Ebs();
        ebs2.setVolumeSize(8);
        ebs2.setVolumeType("gp2");
        ebs2.setEncrypted(false);
        ebs2.setDeleteOnTermination(true);

        BlockDeviceMapping blockDeviceMapping1 = new BlockDeviceMapping();
        blockDeviceMapping1.setDeviceName("/dev/xvdcz");
        blockDeviceMapping1.setEbs(ebs1);

        BlockDeviceMapping blockDeviceMapping2 = new BlockDeviceMapping();
        blockDeviceMapping2.setDeviceName("/dev/xvda");
        blockDeviceMapping2.setEbs(ebs2);

        List<BlockDeviceMapping> blockDeviceMappings = new ArrayList<BlockDeviceMapping>();
        blockDeviceMappings.add(blockDeviceMapping1);
        blockDeviceMappings.add(blockDeviceMapping2);

        return blockDeviceMappings;
    }
}
