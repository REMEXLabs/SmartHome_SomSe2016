package hdm.csm.smarthome.phone.bus;

public class OpenHabSetupFailedEvent {
    private final String message;
    private final int code;

    public OpenHabSetupFailedEvent(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
