package com.ad.lambda.model;

import java.util.ArrayList;
import java.util.List;

import com.ad.lambda.model.ContainerDefinition;

public class ECSServiceRequest {
    private String accessKeyId;
    private String secretAccessKey;
    private String updateOrCreate;
    private String clusterName;
    private String serviceName;
    private int desiredCount;
    private String family;
    private List<ContainerDefinition> containerDefinitions;

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
}
