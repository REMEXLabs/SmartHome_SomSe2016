package de.wim.smarthome.teppich;

import java.util.Arrays;

public class Modul {

	private short[] values = new short[2];

	public Modul(short[] values) {
		this.values[0] = values[0];
		this.values[1] = values[1];
	}

	public short[] getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "ModulIdentifier [values=" + Arrays.toString(values) + "]";
	}

	public boolean isGleichesModul(Modul other) {
		return Arrays.equals(this.getValues(), other.getValues());
	}

	public String getModulString() {
		return "" + this.values[0] + this.values[1];
	}

}
