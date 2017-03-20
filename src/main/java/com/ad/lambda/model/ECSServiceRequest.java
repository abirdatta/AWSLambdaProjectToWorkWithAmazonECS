package com.ad.lambda.model;

import java.util.ArrayList;
import java.util.List;

import com.ad.lambda.model.ContainerDefinition;
import com.ad.lambda.model.Subnet;

public class ECSServiceRequest {
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String updateOrCreate;
    private String clusterName;
    private String serviceName;
    private int desiredCount;
    private String family;
    private List<ContainerDefinition> containerDefinitions;
    
    private String vpcCidr;
    private String vpcName;
    
    private List<Subnet> subnets;
    private String routeTableDestinationCidr;
    
    private ECSAutoScalingGroupConfiguration autoScalingGroupConfiguration;
    private ECSAutoScalingLaunchConfiguration autoScalingLaunchConfiguration;
    private ECSLoadBalancerConfiguration loadBalancerConfiguration;
    
    private String ecsServiceRole;    

    public List<Subnet> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<Subnet> subnets) {
        this.subnets = subnets;
    }

    public String getVpcName() {
        return vpcName;
    }

    public void setVpcName(String vpcName) {
        this.vpcName = vpcName;
    }

    public String getVpcCidr() {
        return vpcCidr;
    }

    public void setVpcCidr(String vpcCidr) {
        this.vpcCidr = vpcCidr;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }    

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUpdateOrCreate() {
        return updateOrCreate;
    }

    public void setUpdateOrCreate(String updateOrCreate) {
        this.updateOrCreate = updateOrCreate;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getDesiredCount() {
        return desiredCount;
    }

    public void setDesiredCount(int desiredCount) {
        this.desiredCount = desiredCount;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public ArrayList<ContainerDefinition> getContainerDefinitions() {
        return (ArrayList<ContainerDefinition>) containerDefinitions;
    }

    public void setContainerDefinitions(List<ContainerDefinition> containerDefinitions) {
        this.containerDefinitions = containerDefinitions;
    }

    public String getRouteTableDestinationCidr() {
        return routeTableDestinationCidr;
    }

    public void setRouteTableDestinationCidr(String routeTableDestinationCidr) {
        this.routeTableDestinationCidr = routeTableDestinationCidr;
    }

    public ECSAutoScalingGroupConfiguration getAutoScalingGroupConfiguration() {
        return autoScalingGroupConfiguration;
    }

    public void setAutoScalingGroupConfiguration(ECSAutoScalingGroupConfiguration autoScalingGroupConfiguration) {
        this.autoScalingGroupConfiguration = autoScalingGroupConfiguration;
    }

    public ECSAutoScalingLaunchConfiguration getAutoScalingLaunchConfiguration() {
        return autoScalingLaunchConfiguration;
    }

    public void setAutoScalingLaunchConfiguration(ECSAutoScalingLaunchConfiguration autoScalingLaunchConfiguration) {
        this.autoScalingLaunchConfiguration = autoScalingLaunchConfiguration;
    }

    public ECSLoadBalancerConfiguration getLoadBalancerConfiguration() {
        return loadBalancerConfiguration;
    }

    public void setLoadBalancerConfiguration(ECSLoadBalancerConfiguration loadBalancerConfiguration) {
        this.loadBalancerConfiguration = loadBalancerConfiguration;
    }

    public String getEcsServiceRole() {
        return ecsServiceRole;
    }

    public void setEcsServiceRole(String ecsServiceRole) {
        this.ecsServiceRole = ecsServiceRole;
    }
}
