package com.ad.lambda.model;

import java.util.ArrayList;
import java.util.List;

public class ECSLoadBalancerConfiguration {
    private String loadBalancerName;
    private List<ECSLoadBalancerListener> loadBalancerListeners = new ArrayList<ECSLoadBalancerListener>();
    private String healthCheckTarget;
    private int healthCheckHealthyThreshold;
    private int healthCheckUnHealthyThreshold;
    private int healthCheckInterval;
    private int healthCheckTimeout;
    
    public String getLoadBalancerName() {
        return loadBalancerName;
    }
    public void setLoadBalancerName(String loadBalancerName) {
        this.loadBalancerName = loadBalancerName;
    }
    public List<ECSLoadBalancerListener> getLoadBalancerListeners() {
        return loadBalancerListeners;
    }
    public void setLoadBalancerListeners(List<ECSLoadBalancerListener> loadBalancerListeners) {
        this.loadBalancerListeners = loadBalancerListeners;
    }
    public String getHealthCheckTarget() {
        return healthCheckTarget;
    }
    public void setHealthCheckTarget(String healthCheckTarget) {
        this.healthCheckTarget = healthCheckTarget;
    }
    public int getHealthCheckHealthyThreshold() {
        return healthCheckHealthyThreshold;
    }
    public void setHealthCheckHealthyThreshold(int healthCheckHealthyThreshold) {
        this.healthCheckHealthyThreshold = healthCheckHealthyThreshold;
    }
    public int getHealthCheckUnHealthyThreshold() {
        return healthCheckUnHealthyThreshold;
    }
    public void setHealthCheckUnHealthyThreshold(int healthCheckUnHealthyThreshold) {
        this.healthCheckUnHealthyThreshold = healthCheckUnHealthyThreshold;
    }
    public int getHealthCheckInterval() {
        return healthCheckInterval;
    }
    public void setHealthCheckInterval(int healthCheckInterval) {
        this.healthCheckInterval = healthCheckInterval;
    }
    public int getHealthCheckTimeout() {
        return healthCheckTimeout;
    }
    public void setHealthCheckTimeout(int healthCheckTimeout) {
        this.healthCheckTimeout = healthCheckTimeout;
    }
}
