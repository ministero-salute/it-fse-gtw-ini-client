package it.finanze.sanita.fse2.ms.iniclient.utility.create;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.DOCUMENT_SIGNED;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.LANGUAGE_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum.AUTHOR;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum.CLASS_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum.CONFIDENTIALITY_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum.EVENT_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum.FORMAT_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum.HEALTH_CARE_FACILITY_TYPE_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum.PRACTICE_SETTING_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum.TYPE_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildExternalIdentifierObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildInternationalStringType;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotCodingSchemeObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.springframework.util.CollectionUtils;

import it.finanze.sanita.fse2.ms.iniclient.dto.AuthorSlotDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventCodeEnum;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentEntryBuilderUtility {

	@Getter
	@Setter
	private static ObjectFactory objectFactory = new ObjectFactory();
	
	public static JAXBElement<ExtrinsicObjectType> buildExtrinsicObjectDocumentEntry(String id,DocumentEntryDTO documentEntryDTO,JWTPayloadDTO jwtPayloadDTO) {

		ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();
		extrinsicObject.setId(id);
		extrinsicObject.setIsOpaque(false);
		extrinsicObject.setMimeType(documentEntryDTO.getMimeType());
		extrinsicObject.setObjectType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
		extrinsicObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
		extrinsicObject.getSlot().addAll(buildExtrinsicObjectSlotsDocEntry(documentEntryDTO,jwtPayloadDTO));
		extrinsicObject.getClassification().addAll(buildExtrinsicClassificationObjectsDocEntry(documentEntryDTO,id));
		extrinsicObject.getExternalIdentifier().addAll(buildExternalIdentifierDocEntry(documentEntryDTO, id, jwtPayloadDTO));
		return objectFactory.createExtrinsicObject(extrinsicObject);
	}

	/**
	 *
	 * @param extrinsicObject
	 * @param documentEntryDTO
	 */
	private static List<SlotType1> buildExtrinsicObjectSlotsDocEntry(DocumentEntryDTO documentEntryDTO, JWTPayloadDTO jwtPayloadDTO) {
		List<SlotType1> slotType1 = new ArrayList<>();
		slotType1.add(buildSlotObject("languageCode", LANGUAGE_CODE));
		slotType1.add(buildSlotObject("repositoryUniqueId", documentEntryDTO.getRepositoryUniqueId()));
		slotType1.add(buildSlotObject("sourcePatientId", documentEntryDTO.getPatientId())); 
		slotType1.add(buildSlotObject("urn:ita:2017:repository-type", "CONS^^^&2.16.840.1.113883.2.9.3.3.6.1.7&ISO"));
		slotType1.add(buildSlotObject("urn:ita:2022:documentSigned", DOCUMENT_SIGNED));
		slotType1.add(buildSlotObject("urn:ita:2022:description", null, documentEntryDTO.getDescription()));
		slotType1.add(buildSlotObject("urn:ita:2022:administrativeRequest",null, documentEntryDTO.getAdministrativeRequest()));
		slotType1.add(buildSlotObject("size", String.valueOf(documentEntryDTO.getSize())));
		slotType1.add(buildSlotObject("hash", documentEntryDTO.getHash()));
		slotType1.add(buildSlotObject("creationTime", documentEntryDTO.getCreationTime()));
		slotType1.add(buildSlotObject("serviceStartTime", documentEntryDTO.getServiceStartTime()));
		slotType1.add(buildSlotObject("serviceStopTime", documentEntryDTO.getServiceStopTime()));
		slotType1.add(buildSlotObject("urn:ihe:iti:xds:2013:referenceIdList",null, documentEntryDTO.getReferenceIdList()));
		return slotType1;
	}
	
	private static List<ClassificationType> buildExtrinsicClassificationObjectsDocEntry(DocumentEntryDTO documentEntryDTO,String id) {
		List<ClassificationType> out = new ArrayList<>();
		//Class code
		SlotType1 classCodeSlot = buildSlotCodingSchemeObject("2.16.840.1.113883.2.9.3.3.6.1.5");
		InternationalStringType nameClassCode = buildInternationalStringType(documentEntryDTO.getClassCodeName());
		ClassificationType classCodeClassification = buildClassificationObject(
				CLASS_CODE.getClassificationScheme(),id,CLASS_CODE.getId(),nameClassCode,
				classCodeSlot,documentEntryDTO.getClassCode());
		out.add(classCodeClassification);

		//Confidentiality Code
		SlotType1 confidentialityCodeSlot = buildSlotCodingSchemeObject("2.16.840.1.113883.5.25");
		InternationalStringType nameConfCode = buildInternationalStringType(documentEntryDTO.getConfidentialityCodeDisplayName()); 
		ClassificationType confidentialityCodeClassification = buildClassificationObject(
				CONFIDENTIALITY_CODE.getClassificationScheme(),id,CONFIDENTIALITY_CODE.getId(),nameConfCode,
				confidentialityCodeSlot,documentEntryDTO.getConfidentialityCode());
		out.add(confidentialityCodeClassification);

		//Format Code
		SlotType1 formatCodeSlot = buildSlotCodingSchemeObject("2.16.840.1.113883.2.9.3.3.6.1.6");
		InternationalStringType nameFormatCode = buildInternationalStringType(documentEntryDTO.getFormatCodeName());
		ClassificationType formatCodeClassification = buildClassificationObject(
				FORMAT_CODE.getClassificationScheme(),id,FORMAT_CODE.getId(),nameFormatCode,
				formatCodeSlot,documentEntryDTO.getFormatCode());
		out.add(formatCodeClassification);

		//Event code list
		if (!CollectionUtils.isEmpty(documentEntryDTO.getEventCodeList())) {
			for (String eventCode : documentEntryDTO.getEventCodeList()) {
				SlotType1 eventCodeSlot = buildSlotCodingSchemeObject("2.16.840.1.113883.2.9.3.3.6.1.3");
				InternationalStringType nameEventCode = buildInternationalStringType(EventCodeEnum.fromValue(eventCode).getDescription());
				ClassificationType eventCodeClassification = buildClassificationObject(EVENT_CODE.getClassificationScheme(),
						id, EVENT_CODE.getId(), nameEventCode, eventCodeSlot, eventCode);
				out.add(eventCodeClassification);
			}
		}

		//Health care facility type code
		SlotType1 healthcareFacilityTypeCodeSlot = buildSlotCodingSchemeObject("2.16.840.1.113883.2.9.3.3.6.1.1");
		InternationalStringType nameHealthcareFacilityTypeCode = buildInternationalStringType(documentEntryDTO.getHealthcareFacilityTypeCodeName());
		ClassificationType healthcareFacilityTypeCodeClassification = buildClassificationObject(
				HEALTH_CARE_FACILITY_TYPE_CODE.getClassificationScheme(), id, HEALTH_CARE_FACILITY_TYPE_CODE.getId(),
				nameHealthcareFacilityTypeCode, healthcareFacilityTypeCodeSlot,documentEntryDTO.getHealthcareFacilityTypeCode());
		out.add(healthcareFacilityTypeCodeClassification);

		//Practice Setting Code
		SlotType1 practiceSettingCodeSlot = buildSlotCodingSchemeObject("2.16.840.1.113883.2.9.3.3.6.1.2");
		InternationalStringType namePracticeSettingCode = buildInternationalStringType(documentEntryDTO.getPracticeSettingCodeName());
		ClassificationType practiceSettingCodeClassification = buildClassificationObject(PRACTICE_SETTING_CODE.getClassificationScheme(),
				id, PRACTICE_SETTING_CODE.getId(), namePracticeSettingCode,practiceSettingCodeSlot,documentEntryDTO.getPracticeSettingCode());
		out.add(practiceSettingCodeClassification);

		//Type Code
		SlotType1 typeCodeSlot = buildSlotCodingSchemeObject("2.16.840.1.113883.6.1");
		InternationalStringType nameTypeCode = buildInternationalStringType(documentEntryDTO.getTypeCodeName());
		ClassificationType typeCodeClassification = buildClassificationObject(
				TYPE_CODE.getClassificationScheme(), id,
				TYPE_CODE.getId(),nameTypeCode, typeCodeSlot,documentEntryDTO.getTypeCode());
		out.add(typeCodeClassification);
		
		//Author
		AuthorSlotDTO author = CommonUtility.buildAuthorSlot(documentEntryDTO.getAuthorRole(),documentEntryDTO.getAuthorInstitution() , documentEntryDTO.getAuthor());
		ClassificationType authorClassification = buildClassificationObject("",
	            AUTHOR.getClassificationScheme(),id,AUTHOR.getId(),
	            null,Arrays.asList(author.getAuthorRoleSlot(),author.getAuthorInstitutionSlot(),author.getAuthorPersonSlot()),
	            CLASSIFICATION_OBJECT_URN,""); 
		out.add(authorClassification);

		return out;
	}

	private static List<ExternalIdentifierType> buildExternalIdentifierDocEntry(DocumentEntryDTO documentEntryDTO, String id, JWTPayloadDTO jwtPayloadDTO) {
		List<ExternalIdentifierType> out = new ArrayList<>();
		ExternalIdentifierType externalIdentifier1 = buildExternalIdentifierObject("XDSDocumentEntry.patientId",
				"patientId_1","urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",EXTERNAL_IDENTIFIER_URN,id,
				documentEntryDTO.getPatientId());
		out.add(externalIdentifier1);
		ExternalIdentifierType externalIdentifier2 = buildExternalIdentifierObject("XDSDocumentEntry.uniqueId","uniqueId_1",
				"urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab", EXTERNAL_IDENTIFIER_URN, id,documentEntryDTO.getUniqueId());

		out.add(externalIdentifier2);
		return out;
	}
}
