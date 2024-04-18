package it.finanze.sanita.fse2.ms.iniclient.enums;

import lombok.Getter;

public enum ClassificationEnum {

	CLASS_CODE("urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a","ClassCode"),
	CONFIDENTIALITY_CODE("urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f","ConfidentialityCode01"),
	FORMAT_CODE("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d","FormatCode_1"),
	EVENT_CODE("urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4","IdEventCodeList"),
	HEALTH_CARE_FACILITY_TYPE_CODE("urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1","IdHealthcareFacilityTypeCode"),
	PRACTICE_SETTING_CODE("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead","IdPracticeSettingCode"),
	TYPE_CODE("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983","IdTypeCode"),
	AUTHOR("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d","Author_1");

	@Getter
	private final String classificationScheme;
	
	@Getter
	private final String id;

	ClassificationEnum(String inClassificationScheme, String inId){
		classificationScheme = inClassificationScheme;
		id = inId;
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