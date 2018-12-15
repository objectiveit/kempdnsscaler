package de.objectiveit.kempdnsscaler.model;

import java.util.List;

public class VSRequest {

    private String loadBalancerURL;
    private Credentials credentials;
    private VirtualService vs;
    private List<String> rsIPs;
    private int rsPort;
    private String notificationTopicArn;

    public String getLoadBalancerURL() {
        return loadBalancerURL;
    }

    public void setLoadBalancerURL(String loadBalancerURL) {
        this.loadBalancerURL = loadBalancerURL;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public VirtualService getVs() {
        return vs;
    }

    public void setVs(VirtualService vs) {
        this.vs = vs;
    }

    public List<String> getRsIPs() {
        return rsIPs;
    }

    public void setRsIPs(List<String> rsIPs) {
        this.rsIPs = rsIPs;
    }

    public int getRsPort() {
        return rsPort;
    }

    public void setRsPort(int rsPort) {
        this.rsPort = rsPort;
    }

    public String getNotificationTopicArn() {
        return notificationTopicArn;
    }

    public void setNotificationTopicArn(String notificationTopicArn) {
        this.notificationTopicArn = notificationTopicArn;
    }

    @Override
    public String toString() {
        return "VSRequest{" +
                "loadBalancerURL='" + loadBalancerURL + '\'' +
                ", credentials=" + credentials +
                ", vs=" + vs +
                ", rsIPs=" + rsIPs +
                ", rsPort=" + rsPort +
                ", notificationTopicArn='" + notificationTopicArn + '\'' +
                '}';
    }

}
