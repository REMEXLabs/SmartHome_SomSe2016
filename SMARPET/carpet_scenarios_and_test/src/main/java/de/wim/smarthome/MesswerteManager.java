package de.wim.smarthome;

import java.util.ArrayList;
import java.util.List;

import de.wim.smarthome.connectivity.LightState;
import de.wim.smarthome.connectivity.OpenHabConnector;
import de.wim.smarthome.teppich.TeppichType;

public class MesswerteManager {

	private static final int ANZAHL_MESSWERTE = 10;
	private OpenHabConnector openHabConnector = new OpenHabConnector();

	private List<Messwert> lastMesswerte = new ArrayList<>();

	private boolean isKleinerTeppichLightOn = false;
	private boolean isGrosserTeppichLightOn = false;

	public void addNewMesswert(Messwert newMesswert) {
		if (lastMesswerte.size() >= ANZAHL_MESSWERTE) {
			lastMesswerte.remove(0);
		}
		lastMesswerte.add(newMesswert);
		checkForStuff();
	}

	private void checkForStuff() {
		checkForKleinerTeppich();
		checkForGrosserTeppich();
		checkForSturz();
		checkForTorkeln();
	}

	private void checkForTorkeln() {
		// TODO implement algorithm
	}

	private void checkForSturz() {
		// TODO implement algorithm
	}

	private void checkForGrosserTeppich() {
		if (isGrosserTeppichActive()) {
			if (!isGrosserTeppichLightOn) {
				openHabConnector.setLight(OpenHabConnector.URL_LIGHT_GF_KITCHEN_TABLE, LightState.ON);
				isGrosserTeppichLightOn = true;
			}
		} else {
			if (isGrosserTeppichLightOn) {
				openHabConnector.setLight(OpenHabConnector.URL_LIGHT_GF_KITCHEN_TABLE, LightState.OFF);
				isGrosserTeppichLightOn = false;
			}
		}
	}

	private void checkForKleinerTeppich() {
		if (isKleinerTeppichActive()) {
			if (!isKleinerTeppichLightOn) {
				openHabConnector.setLight(OpenHabConnector.URL_LIGHT_GF_KITCHEN_CEILING, LightState.ON);
				isKleinerTeppichLightOn = true;
			}
		} else {
			if (isKleinerTeppichLightOn) {
				openHabConnector.setLight(OpenHabConnector.URL_LIGHT_GF_KITCHEN_CEILING, LightState.OFF);
				isKleinerTeppichLightOn = false;
			}
		}
	}

	private boolean isKleinerTeppichActive() {
		for (Messwert messwert : lastMesswerte) {
			if (messwert.getTeppichIdentifier().getTeppichType() == TeppichType.KLEINER_TEPPICH) {
				return true;
			}
		}
		return false;
	}

	private boolean isGrosserTeppichActive() {
		for (Messwert messwert : lastMesswerte) {
			if (messwert.getTeppichIdentifier().getTeppichType() == TeppichType.GROSSER_TEPPICH) {
				return true;
			}
		}
		return false;
	}
}