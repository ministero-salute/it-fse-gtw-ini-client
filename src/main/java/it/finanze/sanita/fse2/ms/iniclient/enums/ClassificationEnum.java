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
package it.finanze.sanita.fse2.ms.iniclient.enums;

import lombok.Getter;

public enum ClassificationEnum {

	CLASS_CODE("urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a","ClassCode","2.16.840.1.113883.2.9.3.3.6.1.5"),
	CONFIDENTIALITY_CODE("urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f","ConfidentialityCode01","2.16.840.1.113883.5.25"),
	FORMAT_CODE("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d","FormatCode_1","2.16.840.1.113883.2.9.3.3.6.1.6"),
	EVENT_CODE("urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4","IdEventCodeList","2.16.840.1.113883.2.9.3.3.6.1.3"),
	HEALTH_CARE_FACILITY_TYPE_CODE("urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1","IdHealthcareFacilityTypeCode","2.16.840.1.113883.2.9.3.3.6.1.1"),
	PRACTICE_SETTING_CODE("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead","IdPracticeSettingCode","2.16.840.1.113883.2.9.3.3.6.1.2"),
	TYPE_CODE("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983","IdTypeCode","2.16.840.1.113883.6.1"),
	AUTHOR("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d","Author_1","");

	@Getter
	private final String classificationScheme;
	
	@Getter
	private final String id;
	
	@Getter
	private final String codingScheme;

	ClassificationEnum(String inClassificationScheme, String inId, String inCodingScheme){
		classificationScheme = inClassificationScheme;
		id = inId;
		codingScheme = inCodingScheme;
	}

	public static ClassificationEnum getByClassificationScheme(String classificationScheme) {
		for (ClassificationEnum classificationEnum : ClassificationEnum.values()) {
			if (classificationEnum.getClassificationScheme().equals(classificationScheme)) {
				return classificationEnum;
			}
		}
		return null;
	}

}