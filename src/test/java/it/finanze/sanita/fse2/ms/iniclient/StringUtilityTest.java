package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class StringUtilityTest {
	
	@Test
	void isIvaTest() {
		assertTrue(StringUtility.isIVA("12345"));
		assertFalse(StringUtility.isIVA(null));
		assertFalse(StringUtility.isIVA(""));
	}
	
}
