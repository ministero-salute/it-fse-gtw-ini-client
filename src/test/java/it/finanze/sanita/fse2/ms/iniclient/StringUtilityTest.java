/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class StringUtilityTest {

	@Test
	void isIvaTest() {
		assertTrue(StringUtility.isIVA("12345"));
		assertFalse(StringUtility.isIVA(null));
		assertFalse(StringUtility.isIVA(""));
		assertFalse(StringUtility.isIVA("123A5"));
	}

	@Test
	void isNullOrEmptyTest() {
		assertTrue(StringUtility.isNullOrEmpty(null));
		assertTrue(StringUtility.isNullOrEmpty(""));
		assertFalse(StringUtility.isNullOrEmpty("Test"));
	}

	@Test
	void generateUUIDTest() {
		String uuid1 = StringUtility.generateUUID();
		String uuid2 = StringUtility.generateUUID();

		assertNotNull(uuid1);
		assertNotNull(uuid2);
		assertNotEquals(uuid1, uuid2);
	}

	@Test
	void toJSONTest() {
		String json = StringUtility.toJSON(new TestObject("Test", 123));
		assertEquals("{\"name\":\"Test\",\"value\":123}", json);
	}

	@Test
	void toJSONJacksonTest() {
		String json = StringUtility.toJSONJackson(new TestObject("Test", 123));
		assertTrue(json.contains("\"name\":\"Test\""));
		assertTrue(json.contains("\"value\":123"));
	}

	@Test
	void toJSONJacksonExceptionTest() {
		assertThrows(BusinessException.class, () -> {
			StringUtility.toJSONJackson(new Object() {
				private final Object circularReference = this;
			});
		});
	}

	@Test
	void sanitizeSourceIdTest() {
		assertEquals("12345", StringUtility.sanitizeSourceId("012345"));
		assertEquals("12345", StringUtility.sanitizeSourceId("12345"));
	}

	@Test
	void trasformXonInOidTest() {
		String input = "123&ISO^^^^456";
		assertEquals("456", StringUtility.trasformXonInOid(input));

		input = "123&ISO123456";
		assertEquals("ISO123456", StringUtility.trasformXonInOid(input));

		input = "123";
		assertEquals("123", StringUtility.trasformXonInOid(input));
	}



	@Getter
	private static class TestObject {
		private final String name;
		private final int value;

		public TestObject(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}
}
