/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.utility.CfUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

        String eniInvalidRandomCF16 = "ENIDMA80A01A883I";
        Assertions.assertFalse(CfUtility.isValidCf(eniInvalidRandomCF16));

        String stpInvalidRandomCF16 = "STPDMA80A01A883I";
        Assertions.assertFalse(CfUtility.isValidCf(stpInvalidRandomCF16));
    }

    @Test
    @DisplayName("Fiscal code check test")
    void fiscalCodeTest () {

        final String fiscalCode16 = "RSSMRA72H26F941L";
        final String fiscalCode17 = "RSSMRA72H26F941LA"; // Should drop last char
        final String fiscalCode11 = "RSSMRA72H26";
        final String fiscalCodeEni = "ENI1234567891234";
        final String fiscalCodeStp = "STP1234567891234";
        final String fiscalCodeNull = null;

        assertEquals(CfUtility.CF_OK_16, CfUtility.validaCF(fiscalCode16));
        assertEquals(CfUtility.CF_OK_16, CfUtility.validaCF(fiscalCode17));
        assertNotEquals(CfUtility.CF_OK_11, CfUtility.validaCF(fiscalCode11));
        assertEquals(CfUtility.CF_ENI_OK, CfUtility.validaCF(fiscalCodeEni));
        assertEquals(CfUtility.CF_STP_OK, CfUtility.validaCF(fiscalCodeStp));
        assertEquals(CfUtility.CF_NON_CORRETTO, CfUtility.validaCF(fiscalCodeNull));

        final String fiscalCodeShort = "RSSMRA72H26F941";
        final String fiscalCodeLong = "RSSMRA72H26F941LAA";
        final String fiscalCodeImproper = "RSSMR172H26F941L";
        final String fcImproperEni = "ENI123456789123A";
        final String fcImproperStp = "STP123456789123A";

        assertEquals(0, CfUtility.validaCF(fiscalCodeShort));
        assertEquals(0, CfUtility.validaCF(fiscalCodeLong));
        assertEquals(0, CfUtility.validaCF(fiscalCodeImproper));
        assertEquals(0, CfUtility.validaCF(fcImproperEni));
        assertEquals(0, CfUtility.validaCF(fcImproperStp));
    }
}
