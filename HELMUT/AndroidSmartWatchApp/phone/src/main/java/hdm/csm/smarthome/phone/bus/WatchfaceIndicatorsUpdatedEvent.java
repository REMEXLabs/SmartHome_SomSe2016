package hdm.csm.smarthome.phone.bus;

import java.util.Arrays;

public class WatchfaceIndicatorsUpdatedEvent {
    private final Integer[] colors;

    public WatchfaceIndicatorsUpdatedEvent(Integer[] colors) {
        this.colors = colors;
    }

    public Integer[] getColors() {
        return colors;
    }

    @Override
    public String toString() {
        return "WatchfaceIndicatorsUpdatedEvent{" +
                "colors=" + Arrays.toString(colors) +
                '}';
    }
}
