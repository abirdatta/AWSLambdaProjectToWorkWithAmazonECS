package com.ad.lambda.model;

public class Subnet {
    
    private String cidrBlock;
    private String availabilityZone;    
    
    public String getCidrBlock() {
        return cidrBlock;
    }
    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }
    public String getAvailabilityZone() {
        return availabilityZone;
    }
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }
    
    

}
