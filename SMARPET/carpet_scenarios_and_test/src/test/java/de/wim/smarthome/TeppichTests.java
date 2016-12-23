package de.wim.smarthome;

import static org.junit.Assert.*;

import org.junit.Test;

import de.wim.smarthome.teppich.TeppichType;

public class TeppichTests {

	@Test
	public void testGrosserTeppich() {
		Messwert messwertGrosserTeppich = new Messwert( "{[253,2,240,5,2,88,0,17,0,129,127,128,128,127,152,128,127]}");
		assertEquals(TeppichType.GROSSER_TEPPICH, messwertGrosserTeppich.getTeppichIdentifier().getTeppichType());
	}
	
	@Test
	public void testKleinerTeppich() {
		Messwert messwertKleinerTeppich = new Messwert( "{[253,2,242,1,1,251,48,17,0,127,127,127,127,127,127,127,127]}");
		assertEquals(TeppichType.KLEINER_TEPPICH, messwertKleinerTeppich.getTeppichIdentifier().getTeppichType());
	}
	
	@Test
	public void testUnterschTeppichUnterschModul() {
		Messwert messwertGrosserTeppichModul_5_2 = new Messwert( "{[253,2,240,5,2,88,0,17,0,129,127,128,128,127,152,128,127]}");
		Messwert messwertKleinerTeppichModul_1_1 = new Messwert( "{[253,2,242,1,1,251,48,17,0,127,127,127,127,127,127,127,127]}");
		
		assertEquals( false, messwertGrosserTeppichModul_5_2.isGleicherTeppichUndGleichesModulWie( messwertKleinerTeppichModul_1_1));
	}
	
	@Test
	public void testUnterschTeppichGleichesModul() {
		Messwert messwertGrosserTeppichModul_1_1 = new Messwert( "{[253,2,240,1,1,88,0,17,0,129,127,128,128,127,152,128,127]}");
		Messwert messwertKleinerTeppichModul_1_1 = new Messwert( "{[253,2,242,1,1,251,48,17,0,127,127,127,127,127,127,127,127]}");
		
		assertEquals( false, messwertGrosserTeppichModul_1_1.isGleicherTeppichUndGleichesModulWie( messwertKleinerTeppichModul_1_1));
	}
	
	@Test
	public void testGleicherTeppichUnterschModul() {
		Messwert messwertGrosserTeppichModul_5_2 = new Messwert( "{[253,2,240,5,2,88,0,17,0,129,127,128,128,127,152,128,127]}");
		Messwert messwertGrosserTeppichModul_1_1 = new Messwert( "{[253,2,240,1,1,251,48,17,0,127,127,127,127,127,127,127,127]}");
		
		assertEquals( false, messwertGrosserTeppichModul_5_2.isGleicherTeppichUndGleichesModulWie( messwertGrosserTeppichModul_1_1));
	}

	@Test
	public void testGleicherTeppichGleichesModul() {
		Messwert messwertGrosserTeppichModul_1_1a = new Messwert( "{[253,2,240,1,1,88,0,17,0,129,127,128,128,127,152,128,127]}");
		Messwert messwertGrosserTeppichModul_1_1b = new Messwert( "{[253,2,240,1,1,251,48,17,0,127,127,127,127,127,127,127,127]}");
		
		assertEquals( true, messwertGrosserTeppichModul_1_1a.isGleicherTeppichUndGleichesModulWie( messwertGrosserTeppichModul_1_1b));
	}
	
}
