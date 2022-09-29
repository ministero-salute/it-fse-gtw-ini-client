package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.utility.CfUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CfUtilityTest {
    @Test
    void successTest() {
        String invalidRandomCF16 = "33NDMA80A01A883I";
        Assertions.assertFalse(CfUtility.isValidCf(invalidRandomCF16));

        String validRandomCF16 = "DKNDMA80A01A883I";
        Assertions.assertTrue(CfUtility.isValidCf(validRandomCF16));

        String invalidRandomCF11 = "DKNDMA80A01";
        Assertions.assertFalse(CfUtility.isValidCf(invalidRandomCF11));

        String validRandomCF11 = "29259870359";
        Assertions.assertTrue(CfUtility.isValidCf(validRandomCF11));
    }
}
