package de.wim.smarthome.teppich;

import java.util.Arrays;

public class Teppich {
	
	private short[] values = new short[3];
	
	public Teppich( short[] values) {
		this.values[0] = values[0];
		this.values[1] = values[1];
		this.values[2] = values[2];
	}
	
	/**
	 * @return
	 */
	public TeppichType getTeppichType() {
		if( values[0] == 253 && values[1] == 2 && values[2] == 242) {
			return TeppichType.KLEINER_TEPPICH;
		} else {
			return TeppichType.GROSSER_TEPPICH;
		}
	}

	@Override
	public String toString() {
		return "TeppichIdentifier [values=" + Arrays.toString(values) + "]";
	}
}