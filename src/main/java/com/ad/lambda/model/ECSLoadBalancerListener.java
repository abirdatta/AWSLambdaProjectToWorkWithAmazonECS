package com.ad.lambda.model;

public class ECSLoadBalancerListener {
    private int instancePort;
    private String instanceProtocol;
    private int loadBalancerPort;
    private String loadBalancerProtocol;
    
    public int getInstancePort() {
        return instancePort;
    }
    public void setInstancePort(int instancePort) {
        this.instancePort = instancePort;
    }
    public String getInstanceProtocol() {
        return instanceProtocol;
    }
    public void setInstanceProtocol(String instanceProtocol) {
        this.instanceProtocol = instanceProtocol;
    }
    public int getLoadBalancerPort() {
        return loadBalancerPort;
    }
    public void setLoadBalancerPort(int loadBalancerPort) {
        this.loadBalancerPort = loadBalancerPort;
    }
    public String getLoadBalancerProtocol() {
        return loadBalancerProtocol;
    }
    public void setLoadBalancerProtocol(String loadBalancerProtocol) {
        this.loadBalancerProtocol = loadBalancerProtocol;
    }
    
    
}
