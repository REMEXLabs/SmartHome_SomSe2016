package hdm.csm.smarthome.phone.openhab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Thing {
    private String UID;
    private String thingTypeUID;
    private String label = "";
    private String bridgeUID;
    private Map<String, String> configuration;
    private Map<String, String> properties = new HashMap<>();
    private ArrayList<Channel> channels = new ArrayList<>();
    private StatusInfo statusInfo;

    public Thing(String UID, String thingTypeUID, String label, String bridgeUID, Map<String, String> configuration, Map<String, String> properties, ArrayList<Channel> channels, StatusInfo statusInfo) {
        this.UID = UID;
        this.thingTypeUID = thingTypeUID;
        this.label = label;
        this.bridgeUID = bridgeUID;
        this.configuration = configuration;
        this.properties = properties;
        this.channels = channels;
        this.statusInfo = statusInfo;
    }

    public Thing(String UID, String thingTypeUID, String label, Map<String, String> configuration, ArrayList<Channel> channels) {
        this.UID = UID;
        this.thingTypeUID = thingTypeUID;
        this.label = label;
        this.configuration = configuration;
        this.channels = channels;
        this.properties = new HashMap<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Thing{");
        sb.append("UID='").append(UID).append('\'');
        sb.append(", thingTypeUID='").append(thingTypeUID).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", bridgeUID='").append(bridgeUID).append('\'');
        sb.append(", configuration=").append(configuration);
        sb.append(", channels=").append(channels);
        sb.append(", statusInfo=").append(statusInfo);
        sb.append('}');
        return sb.toString();
    }
}
