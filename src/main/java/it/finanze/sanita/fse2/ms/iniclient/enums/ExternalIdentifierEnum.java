package it.finanze.sanita.fse2.ms.iniclient.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExternalIdentifierEnum {


	PATIENT("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427","patientId_1"),
	UnIQUE_ID("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab","uniqueId_1");
	
	@Getter
	private final String classificationScheme;
	
	@Getter
	private final String id;


	public static ExternalIdentifierEnum getByClassificationScheme(String classificationScheme) {
		for (ExternalIdentifierEnum classificationEnum : ExternalIdentifierEnum.values()) {
			if (classificationEnum.getClassificationScheme().equals(classificationScheme)) {
				return classificationEnum;
			}
		}
		return null;
	}

}