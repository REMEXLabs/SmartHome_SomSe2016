package hdm.csm.smarthome.phone.openhab;

import java.util.ArrayList;
import java.util.HashMap;

public class Channel {
    private String uid;
    private String id;
    private String channelTypeUID;
    private String itemType;
    private String label = "";
    private String description = "";
    private ArrayList<String> linkedItems = new ArrayList<>();
    private ArrayList<String> defaultTags = new ArrayList<>();
    private HashMap<String, String> properties = new HashMap<>();
    private HashMap<String, String> configuration = new HashMap<>();

    public Channel(String uid, String id, String channelTypeUID, String itemType, String label, String description, ArrayList<String> linkedItems, ArrayList<String> defaultTags, HashMap<String, String> properties, HashMap<String, String> configuration) {
        this.uid = uid;
        this.id = id;
        this.channelTypeUID = channelTypeUID;
        this.itemType = itemType;
        this.label = label;
        this.description = description;
        this.linkedItems = linkedItems;
        this.defaultTags = defaultTags;
        this.properties = properties;
        this.configuration = configuration;
    }

    public Channel(String uid, String id, String channelTypeUID, String itemType, String label, String description) {
        this.uid = uid;
        this.id = id;
        this.channelTypeUID = channelTypeUID;
        this.itemType = itemType;
        this.label = label;
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Channel{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", channelTypeUID='").append(channelTypeUID).append('\'');
        sb.append(", itemType='").append(itemType).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", linkedItems=").append(linkedItems);
        sb.append(", defaultTags=").append(defaultTags);
        sb.append(", properties=").append(properties);
        sb.append(", configuration=").append(configuration);
        sb.append('}');
        return sb.toString();
    }
}
