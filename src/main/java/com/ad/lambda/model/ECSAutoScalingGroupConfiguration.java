package com.ad.lambda.model;

import java.util.ArrayList;
import java.util.List;

public class ECSAutoScalingGroupConfiguration {
    private String autoScalingLaunchConfigurationName;
    private String autoScalingGroupName;
    private int desiredCapacity;
    private int maxSize;
    private int minSize;
    private List<String> availabilityZones = new ArrayList<String>();
    
    public String getAutoScalingLaunchConfigurationName() {
        return autoScalingLaunchConfigurationName;
    }
    public void setAutoScalingLaunchConfigurationName(String autoScalingLaunchConfigurationName) {
        this.autoScalingLaunchConfigurationName = autoScalingLaunchConfigurationName;
    }
    public String getAutoScalingGroupName() {
        return autoScalingGroupName;
    }
    public void setAutoScalingGroupName(String autoScalingGroupName) {
        this.autoScalingGroupName = autoScalingGroupName;
    }
    public int getDesiredCapacity() {
        return desiredCapacity;
    }
    public void setDesiredCapacity(int desiredCapacity) {
        this.desiredCapacity = desiredCapacity;
    }
    public int getMaxSize() {
        return maxSize;
    }
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    public int getMinSize() {
        return minSize;
    }
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
    public List<String> getAvailabilityZones() {
        return availabilityZones;
    }
    public void setAvailabilityZones(List<String> availabilityZones) {
        this.availabilityZones = availabilityZones;
    }
    
       
}
