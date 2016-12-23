package hdm.csm.smarthome.phone.bus;


public class SetupCompleteEvent {
    private final boolean success;
    private final String message;

    public SetupCompleteEvent(boolean success) {
        this(success, "");
    }

    public SetupCompleteEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
