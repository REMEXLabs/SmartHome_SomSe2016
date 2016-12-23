package hdm.csm.smarthome.phone.bus;

public class GcmIdReceivedEvent {
    private final String gcmId;

    public GcmIdReceivedEvent(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getGcmId() {
        return gcmId;
    }
}
