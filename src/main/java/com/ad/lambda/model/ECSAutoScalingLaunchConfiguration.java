package com.ad.lambda.model;

public class ECSAutoScalingLaunchConfiguration {

    private String autolaunchConfigurationName;
    private String amiImageId;
    private String instanceType;
    private String iamInstanceProfile;
    private String userData;
    private String keyName;
    
    public String getAutolaunchConfigurationName() {
        return autolaunchConfigurationName;
    }
    public void setAutolaunchConfigurationName(String autolaunchConfigurationName) {
        this.autolaunchConfigurationName = autolaunchConfigurationName;
    }
    public String getAmiImageId() {
        return amiImageId;
    }
    public void setAmiImageId(String amiImageId) {
        this.amiImageId = amiImageId;
    }
    public String getInstanceType() {
        return instanceType;
    }
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }
    public String getIamInstanceProfile() {
        return iamInstanceProfile;
    }
    public void setIamInstanceProfile(String iamInstanceProfile) {
        this.iamInstanceProfile = iamInstanceProfile;
    }
    public String getUserData() {
        return userData;
    }
    public void setUserData(String userData) {
        this.userData = userData;
    }
    public String getKeyName() {
        return keyName;
    }
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }    
}
