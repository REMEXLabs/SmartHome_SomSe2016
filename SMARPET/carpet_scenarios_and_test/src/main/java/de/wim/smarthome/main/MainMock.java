package de.wim.smarthome.main;

import java.io.BufferedReader;
import java.io.FileReader;

import de.wim.smarthome.Messwert;
import de.wim.smarthome.MesswerteManager;

public class MainMock {

	public static void main(String[] args) {
		int counter = 0;
		MesswerteManager manager = new MesswerteManager();
		String fileName = "D:\\dev\\uml_workspace\\TeppichSzenario\\raw_test_clean.txt";

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				Messwert messwert = new Messwert(line);
				System.out.println("add Messwert " + ++counter + messwert);
				manager.addNewMesswert(messwert);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
