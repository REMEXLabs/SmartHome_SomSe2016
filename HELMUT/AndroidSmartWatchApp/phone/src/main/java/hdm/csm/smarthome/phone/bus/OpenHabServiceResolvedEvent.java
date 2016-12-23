package hdm.csm.smarthome.phone.bus;

import java.net.InetAddress;

public class OpenHabServiceResolvedEvent {
    private final String serviceName;
    private final String serviceType;
    private final InetAddress host;
    private final int port;

    public OpenHabServiceResolvedEvent(String serviceName, String serviceType, InetAddress host, int port) {
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getHost() {
        return host;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NsdServiceResolvedEvent{");
        sb.append("name='").append(serviceName).append('\'');
        sb.append(", serviceType='").append(serviceType).append('\'');
        sb.append(", host=").append(host);
        sb.append(", port=").append(port);
        sb.append('}');
        return sb.toString();
    }
}
