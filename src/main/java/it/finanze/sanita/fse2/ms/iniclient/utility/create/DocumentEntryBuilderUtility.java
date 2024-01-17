package it.finanze.sanita.fse2.ms.iniclient.utility.create;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CODING_SCHEME;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.DOCUMENT_SIGNED;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.LANGUAGE_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildExternalIdentifierObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildInternationalStringType;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.springframework.util.CollectionUtils;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventCodeEnum;
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
	
	public static JAXBElement<ExtrinsicObjectType> buildExtrinsicObjectDocumentEntry(String id,DocumentEntryDTO documentEntryDTO,
			String requestUUID,JWTPayloadDTO jwtPayloadDTO) {

		ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();
		extrinsicObject.setId(id);
		extrinsicObject.setIsOpaque(false);
		extrinsicObject.setMimeType(documentEntryDTO.getMimeType());
		extrinsicObject.setObjectType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
		extrinsicObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
		extrinsicObject.getSlot().addAll(buildExtrinsicObjectSlotsDocEntry(documentEntryDTO,jwtPayloadDTO));
		extrinsicObject.getClassification().addAll(buildExtrinsicClassificationObjectsDocEntry(documentEntryDTO, requestUUID));
		extrinsicObject.getExternalIdentifier().addAll(buildExternalIdentifierDocEntry(documentEntryDTO, requestUUID, jwtPayloadDTO));
		return objectFactory.createExtrinsicObject(extrinsicObject);
	}

	/**
	 *
	 * @param extrinsicObject
	 * @param documentEntryDTO
	 */
	private static List<SlotType1> buildExtrinsicObjectSlotsDocEntry(DocumentEntryDTO documentEntryDTO, JWTPayloadDTO jwtPayloadDTO) {
		List<SlotType1> slotType1 = new ArrayList<>();
		slotType1.add(buildSlotObject("authorRole", documentEntryDTO.getAuthorRole())); 
		slotType1.add(buildSlotObject("authorInstitution", documentEntryDTO.getAuthorInstitution())); 
		slotType1.add(buildSlotObject("authorPerson", documentEntryDTO.getAuthor()));  
		slotType1.add(buildSlotObject("languageCode", LANGUAGE_CODE));
		slotType1.add(buildSlotObject("repositoryUniqueId", documentEntryDTO.getRepositoryUniqueId()));
		slotType1.add(buildSlotObject("sourcePatientId", documentEntryDTO.getPatientId() + "^^^&2.16.840.1.113883.2.9.4.3.2&ISO"));
		slotType1.add(buildSlotObject("urn:ita:2017:repository-type", "CONS^^^&2.16.840.1.113883.2.9.3.3.6.1.7&ISO"));
		slotType1.add(buildSlotObject("urn:ita:2022:documentSigned", DOCUMENT_SIGNED));
		slotType1.add(buildSlotObject("urn:ita:2022:description", null, documentEntryDTO.getDescription()));
		slotType1.add(buildSlotObject("urn:ita:2022:administrativeRequest", documentEntryDTO.getAdministrativeRequest()));
		slotType1.add(buildSlotObject("size", String.valueOf(documentEntryDTO.getSize())));
		slotType1.add(buildSlotObject("hash", documentEntryDTO.getHash()));
		slotType1.add(buildSlotObject("creationTime", documentEntryDTO.getCreationTime()));
		slotType1.add(buildSlotObject("serviceStartTime", documentEntryDTO.getServiceStartTime()));
		slotType1.add(buildSlotObject("serviceStopTime", documentEntryDTO.getServiceStopTime()));
		return slotType1;
	}
	
	private static List<ClassificationType> buildExtrinsicClassificationObjectsDocEntry(DocumentEntryDTO documentEntryDTO, String requestUUID) {
		List<ClassificationType> out = new ArrayList<>();
		//Class code
		SlotType1 classCodeSlot = buildSlotObject(CODING_SCHEME, "2.16.840.1.113883.2.9.3.3.6.1.5");
		InternationalStringType nameClassCode = buildInternationalStringType(documentEntryDTO.getClassCodeName());
		ClassificationType classCodeClassification = buildClassificationObject(
				"urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a","Documento00","ClassCode",nameClassCode,
				classCodeSlot,documentEntryDTO.getClassCode());
		out.add(classCodeClassification);

		//Confidentiality Code
		SlotType1 confidentialityCodeSlot = buildSlotObject(CODING_SCHEME,"2.16.840.1.113883.5.25");
		InternationalStringType nameConfCode = buildInternationalStringType(documentEntryDTO.getConfidentialityCodeDisplayName()); 
		ClassificationType confidentialityCodeClassification = buildClassificationObject(
				"urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f","Documento01","ConfidentialityCode01",nameConfCode,
				confidentialityCodeSlot,documentEntryDTO.getConfidentialityCode());
		out.add(confidentialityCodeClassification);

		//Format Code
		SlotType1 formatCodeSlot = buildSlotObject(CODING_SCHEME,"2.16.840.1.113883.2.9.3.3.6.1.6");
		InternationalStringType nameFormatCode = buildInternationalStringType(documentEntryDTO.getFormatCodeName());
		ClassificationType formatCodeClassification = buildClassificationObject(
				"urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d","Documento01","FormatCode_1",nameFormatCode,
				formatCodeSlot,documentEntryDTO.getFormatCode());
		out.add(formatCodeClassification);

		//Event code list
		if (!CollectionUtils.isEmpty(documentEntryDTO.getEventCodeList())) {
			for (String eventCode : documentEntryDTO.getEventCodeList()) {
				SlotType1 eventCodeSlot = buildSlotObject(CODING_SCHEME, "2.16.840.1.113883.2.9.3.3.6.1.3");
				InternationalStringType nameEventCode = buildInternationalStringType(EventCodeEnum.fromValue(eventCode).getDescription());
				ClassificationType eventCodeClassification = buildClassificationObject("urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4",
						Constants.IniClientConstants.URN_UUID + requestUUID, "IdEventCodeList", nameEventCode, eventCodeSlot, eventCode);
				out.add(eventCodeClassification);
			}
		}

		//Health care facility type code
		SlotType1 healthcareFacilityTypeCodeSlot = buildSlotObject(CODING_SCHEME,"2.16.840.1.113883.2.9.3.3.6.1.1");
		InternationalStringType nameHealthcareFacilityTypeCode = buildInternationalStringType(documentEntryDTO.getHealthcareFacilityTypeCodeName());
		ClassificationType healthcareFacilityTypeCodeClassification = buildClassificationObject(
				"urn:uuid:f33fb8ac-18af-42cc-ae0eed0b0bdb91e1",Constants.IniClientConstants.URN_UUID + requestUUID,"IdHealthcareFacilityTypeCode",nameHealthcareFacilityTypeCode,
				healthcareFacilityTypeCodeSlot,documentEntryDTO.getHealthcareFacilityTypeCode());
		out.add(healthcareFacilityTypeCodeClassification);

		//Practice Setting Code
		SlotType1 practiceSettingCodeSlot = buildSlotObject(CODING_SCHEME,"2.16.840.1.113883.2.9.3.3.6.1.2");
		InternationalStringType namePracticeSettingCode = buildInternationalStringType(documentEntryDTO.getPracticeSettingCodeName());
		ClassificationType practiceSettingCodeClassification = buildClassificationObject("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",Constants.IniClientConstants.URN_UUID + requestUUID,
				"IdPracticeSettingCode",namePracticeSettingCode,practiceSettingCodeSlot,documentEntryDTO.getPracticeSettingCode());
		out.add(practiceSettingCodeClassification);

		//Type Code
		SlotType1 typeCodeSlot = buildSlotObject(CODING_SCHEME,"2.16.840.1.113883.6.1");
		InternationalStringType nameTypeCode = buildInternationalStringType(documentEntryDTO.getTypeCodeName());
		ClassificationType typeCodeClassification = buildClassificationObject(
				"urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",Constants.IniClientConstants.URN_UUID + requestUUID,
				"IdTypeCode",nameTypeCode,
				typeCodeSlot,documentEntryDTO.getTypeCode()
				);
		out.add(typeCodeClassification);
		
		//Author
		SlotType1 authorRoleSlot = buildSlotObject("authorRole", documentEntryDTO.getAuthorRole());
		SlotType1 authorInstitutionSlot = buildSlotObject("authorInstitution", documentEntryDTO.getAuthorInstitution());
		SlotType1 authorPersonSlot = buildSlotObject("authorPerson", documentEntryDTO.getAuthor()+"^^^^^^^^&2.16.840.1.113883.2.9.4.3.2&ISO");
//		InternationalStringType nameAuthor = buildInternationalStringType(documentEntryDTO.getAuthor());
		ClassificationType authorClassification = buildClassificationObject("",
	            "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d","urn:uuid:3c86e9e9-6ae0-425d-9c42-93afa1d00db3","Author_1",
	            null,Arrays.asList(authorRoleSlot,authorInstitutionSlot,authorPersonSlot),"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",""); 
		out.add(authorClassification);

		return out;
	}

	private static List<ExternalIdentifierType> buildExternalIdentifierDocEntry(DocumentEntryDTO documentEntryDTO, String requestUUID, JWTPayloadDTO jwtPayloadDTO) {
		List<ExternalIdentifierType> out = new ArrayList<>();
		ExternalIdentifierType externalIdentifier1 = buildExternalIdentifierObject("XDSDocumentEntry.patientId",
				"patientId_1","urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427","urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
				"urn:uuid:3c86e9e9-6ae0-425d-9c42-93afa1d00db3",documentEntryDTO.getPatientId()+ "^^^&2.16.840.1.113883.2.9.4.3.2&ISO");
		out.add(externalIdentifier1);
		ExternalIdentifierType externalIdentifier2 = buildExternalIdentifierObject("XDSDocumentEntry.uniqueId","uniqueId_1",
				"urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab",Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
				Constants.IniClientConstants.URN_UUID + requestUUID,documentEntryDTO.getUniqueId());

		out.add(externalIdentifier2);
		return out;
	}
}
