/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.utility.create;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildAssociationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildExternalIdentifierObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildExternalIdentifierObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildInternationalStringType;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CODING_SCHEME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.springframework.util.CollectionUtils;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventCodeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@Slf4j
public final class PublishReplaceBodyBuilderUtility {

	private PublishReplaceBodyBuilderUtility() {}

	@Getter
	@Setter
	private static ObjectFactory objectFactory = new ObjectFactory();

	/**
	 *
	 * @param documentEntryDTO
	 * @param submissionSetEntryDTO
	 * @param jwtPayloadDTO
	 * @return
	 */
	public static SubmitObjectsRequest buildSubmitObjectRequest(DocumentEntryDTO documentEntryDTO,SubmissionSetEntryDTO submissionSetEntryDTO,JWTPayloadDTO jwtPayloadDTO,String uuid) {
		SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
		try {
			RegistryObjectListType registryObjectListType = buildRegistryObjectList(documentEntryDTO, submissionSetEntryDTO, jwtPayloadDTO, uuid);
			submitObjectsRequest.setRegistryObjectList(registryObjectListType);
		} catch(Exception ex) {
			log.error("Error while perform build submit object request : {}" , ex.getMessage());
			throw new BusinessException("Error while perform build submit object request : ", ex);
		}
		return submitObjectsRequest;
	}

	/**
	 *
	 * @param documentEntryDTO
	 * @param submissionSetEntryDTO
	 * @param jwtPayloadDTO
	 * @return
	 */
	private static RegistryObjectListType buildRegistryObjectList(DocumentEntryDTO documentEntryDTO,SubmissionSetEntryDTO submissionSetEntryDTO,JWTPayloadDTO jwtPayloadDTO,String uuid) {
		RegistryObjectListType registryObjectListType = new RegistryObjectListType();

		try {
			String documentEntryUUID = StringUtility.generateUUID();
			String submissionEntryUUID = StringUtility.generateUUID();
			//ExtrinsicObject - DocumentEntry
			JAXBElement<ExtrinsicObjectType> extrinsicObject = buildExtrinsicObjectDocumentEntry(Constants.IniClientConstants.URN_UUID + documentEntryUUID,
				documentEntryDTO,documentEntryUUID,jwtPayloadDTO);
			registryObjectListType.getIdentifiable().add(extrinsicObject);

			//Registry package - SubmissionSetEntry
			JAXBElement<RegistryPackageType> registryPackageObject = buildRegistryPackageObjectSubmissionSet(submissionSetEntryDTO,jwtPayloadDTO,
					Constants.IniClientConstants.URN_UUID + submissionEntryUUID);
			registryObjectListType.getIdentifiable().add(registryPackageObject);
 

			JAXBElement<AssociationType1> associationObject = null;
			if (uuid == null) {
				//Create
				associationObject = buildAssociationObject(
					"urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember",Constants.IniClientConstants.URN_UUID + StringUtility.generateUUID(),
					Constants.IniClientConstants.URN_UUID +submissionEntryUUID,Constants.IniClientConstants.URN_UUID +documentEntryUUID);
			} else {
				//Replace
				List<SlotType1> associationObjectSlots = new ArrayList<>();
				SlotType1 associationObjSlot = buildSlotObject("SubmissionSetStatus", null,Collections.singletonList("Original"));
				associationObjectSlots.add(associationObjSlot);
				associationObject = buildAssociationObject(
					"urn:ihe:iti:2007:AssociationType:RPLC","SubmissionSet01_Association_1",
					Constants.IniClientConstants.URN_UUID + documentEntryUUID,uuid,associationObjectSlots);
			}
			registryObjectListType.getIdentifiable().add(associationObject);
		} catch(Exception ex) {
			log.error("Error while perform build registry object list : " , ex);
			throw new BusinessException("Error while perform build registry object list : " , ex);
		}

		return registryObjectListType;
	}

	/**
	 * 8 slots
	 * 7 classifications
	 * 2 external identifiers
	 *
	 * @param id
	 * @param mimeType
	 * @param documentEntryDTO
	 * @param requestUUID
	 * @param jwtPayloadDTO
	 * @return
	 */
	private static JAXBElement<ExtrinsicObjectType> buildExtrinsicObjectDocumentEntry(String id,DocumentEntryDTO documentEntryDTO,
			String requestUUID,JWTPayloadDTO jwtPayloadDTO) {

		JAXBElement<ExtrinsicObjectType> output = null;
		try {
			ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();
			extrinsicObject.setId(id);
			extrinsicObject.setIsOpaque(false);
			extrinsicObject.setMimeType(documentEntryDTO.getMimeType());
			extrinsicObject.setObjectType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
			extrinsicObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
			extrinsicObject.getSlot().addAll(buildExtrinsicObjectSlotsDocEntry(documentEntryDTO,jwtPayloadDTO));
			extrinsicObject.getClassification().addAll(buildExtrinsicClassificationObjectsDocEntry(documentEntryDTO, requestUUID));
			extrinsicObject.getExternalIdentifier().addAll(buildExternalIdentifierDocEntry(documentEntryDTO, requestUUID, jwtPayloadDTO));
			output = objectFactory.createExtrinsicObject(extrinsicObject);
		} catch(Exception ex) {
			log.error("Error while perform build extrinsic object : " , ex);
			throw new BusinessException("Error while perform build extrinsic object : " , ex);
		}
		return output;
	}

	/**
	 *
	 * @param extrinsicObject
	 * @param documentEntryDTO
	 */
	private static List<SlotType1> buildExtrinsicObjectSlotsDocEntry(DocumentEntryDTO documentEntryDTO, JWTPayloadDTO jwtPayloadDTO) {
		List<SlotType1> slotType1 = new ArrayList<>();
		try {
			//TODO
			// Populate slot
			slotType1.add(buildSlotObject("authorRole", jwtPayloadDTO.getSubject_role())); 
			slotType1.add(buildSlotObject("authorInstitution",  "ULSS 9 - TREVISO^^^^^&2.16.840.1.113883.2.9.4.1.1&ISO^^^^050109")); 
			slotType1.add(buildSlotObject("authorPerson", "ZNRMRA86L11B157N^^^^^^^^&2.16.840.1.113883.2.9.4.3.2&ISO")); 
			slotType1.add(buildSlotObject("languageCode", documentEntryDTO.getLanguageCode()));
			slotType1.add(buildSlotObject("repositoryUniqueId", documentEntryDTO.getRepositoryUniqueId()));
			slotType1.add(buildSlotObject("sourcePatientId", documentEntryDTO.getSourcePatientId()));
			slotType1.add(buildSlotObject("urn:ita:2017:repository-type", "CONS^^^&2.16.840.1.113883.2.9.3.3.6.1.7&ISO"));
			slotType1.add(buildSlotObject("urn:ita:2022:documentSigned", "true^Documento firmato"));
			slotType1.add(buildSlotObject("urn:ita:2022:description", null, documentEntryDTO.getDescription()));
			slotType1.add(buildSlotObject("urn:ita:2022:administrativeRequest", documentEntryDTO.getAdministrativeRequest()));
			slotType1.add(buildSlotObject("size", String.valueOf(documentEntryDTO.getSize())));
			slotType1.add(buildSlotObject("hash", documentEntryDTO.getHash()));
			slotType1.add(buildSlotObject("creationTime", documentEntryDTO.getCreationTime()));
			slotType1.add(buildSlotObject("serviceStartTime", documentEntryDTO.getServiceStartTime()));
			slotType1.add(buildSlotObject("serviceStopTime", documentEntryDTO.getServiceStopTime()));
		} catch (Exception e) {
			log.error("Error while invoking buildExtrinsicObjectSlotsDocEntry: {}", e.getMessage());
			throw new BusinessException("Error while invoking buildExtrinsicObjectSlotsDocEntry: " + e.getMessage());
		}
		return slotType1;
	}

	private static List<ExternalIdentifierType> buildExternalIdentifierDocEntry(DocumentEntryDTO documentEntryDTO, String requestUUID, JWTPayloadDTO jwtPayloadDTO) {
		List<ExternalIdentifierType> out = new ArrayList<>();
		try {
			ExternalIdentifierType externalIdentifier1 = buildExternalIdentifierObject("XDSDocumentEntry.patientId",
					"patientId_1",Constants.IniClientConstants.URN_UUID + requestUUID,Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
					"urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",documentEntryDTO.getSourcePatientId());
			out.add(externalIdentifier1);
			ExternalIdentifierType externalIdentifier2 = buildExternalIdentifierObject("XDSDocumentEntry.uniqueId","uniqueId_1",
					"urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab",Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
					Constants.IniClientConstants.URN_UUID + requestUUID,documentEntryDTO.getUniqueId());

			out.add(externalIdentifier2);
		} catch(Exception ex) {
			log.error("Error while perform buildExtrinsicClassificationObjects : " , ex);
			throw new BusinessException("Error while perform buildExtrinsicClassificationObjects : " , ex);
		}

		return out;
	}
	
	/**
	 *
	 * @param documentEntryDTO
	 * @param requestUUID
	 * @param jwtPayloadDTO
	 * @return
	 */
	private static List<ClassificationType> buildExtrinsicClassificationObjectsDocEntry(DocumentEntryDTO documentEntryDTO, String requestUUID) {
		List<ClassificationType> out = new ArrayList<>();
		try {
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
				"urn:uuid:a09d5840-386c-46f2-b5ad9c3699a4309d","Documento01","IdFormatCode01",nameFormatCode,
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
				"urn:uuid:f0306f51-975f-434e-a61cc59651d33983",Constants.IniClientConstants.URN_UUID + requestUUID,
				"IdTypeCode",nameTypeCode,
				typeCodeSlot,documentEntryDTO.getTypeCode()
			);
			out.add(typeCodeClassification);
 
		} catch(Exception ex) {
			log.error("Error while perform buildExtrinsicClassificationObjects : " , ex);
			throw new BusinessException("Error while perform buildExtrinsicClassificationObjects : " , ex);
		}

		return out;
	}

	/**
	 *
	 * @param documentEntryDTO
	 * @param submissionSetEntryDTO
	 * @param jwtPayloadDTO
	 * @return
	 */
	private static JAXBElement<RegistryPackageType> buildRegistryPackageObjectSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO,JWTPayloadDTO jwtPayloadDTO,
			String uuid) {

		RegistryPackageType registryPackageObject = new RegistryPackageType();

		try {
			registryPackageObject.setId(uuid);
			registryPackageObject.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage");
			registryPackageObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
			registryPackageObject.setName(null);
			registryPackageObject.setDescription(null);
			registryPackageObject.getSlot().addAll(buildSlotSubmissionSet(submissionSetEntryDTO));
			registryPackageObject.getClassification().addAll(buildClassificationSubmissionSet(submissionSetEntryDTO));
			registryPackageObject.getExternalIdentifier().addAll(buildExternalIdentifierSubmissionSet(submissionSetEntryDTO));
			return objectFactory.createRegistryPackage(registryPackageObject);
		} catch (Exception e) {
			log.error("Error while invoking buildRegistryPackageObject: {}", e.getMessage());
			throw new BusinessException("Error while invoking buildRegistryPackageObject: " + e.getMessage());
		}
	}
	
	private static List<SlotType1> buildSlotSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO){
		List<SlotType1> out = new ArrayList<>();
		out.add(buildSlotObjectJax(objectFactory,"submissionTime",null,Arrays.asList(submissionSetEntryDTO.getSubmissionTime())).getValue());
		return out;
	}
	
	private static List<ClassificationType> buildClassificationSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO) {
		List<ClassificationType> out = new ArrayList<>();
		//Content Type
		InternationalStringType nameContentTypeCode = buildInternationalStringType(Collections.singletonList(submissionSetEntryDTO.getContentTypeCodeName()));
		SlotType1 nameContentTypeSlot = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME,"2.16.840.1.113883.2.9.3.3.6.1.4");

		JAXBElement<ClassificationType> contentTypeCodeClassification = buildClassificationObjectJax(
			null,"urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,
			"IdContentTypeCode",nameContentTypeCode,Arrays.asList(nameContentTypeSlot),
			Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,submissionSetEntryDTO.getContentTypeCode()
		);
		out.add(contentTypeCodeClassification.getValue());
		return out;
	}
	
	private static List<ExternalIdentifierType> buildExternalIdentifierSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO) {
		List<ExternalIdentifierType> out = new ArrayList<>();

		// Build external identifiers
		JAXBElement<ExternalIdentifierType> externalIdentifierObject1 = buildExternalIdentifierObjectJax(
			objectFactory,"XDSSubmissionSet.sourceId","SubmissionSet01_SourceId",
			"urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
			Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
			Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,
			submissionSetEntryDTO.getSourceId());
		out.add(externalIdentifierObject1.getValue());

		JAXBElement<ExternalIdentifierType> externalIdentifierObject2 = buildExternalIdentifierObjectJax(
			objectFactory,"XDSSubmissionSet.uniqueId",
			"SubmissionSet01_UniqueId",
			"urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8",
			Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
			Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,
			submissionSetEntryDTO.getUniqueID());
		out.add(externalIdentifierObject2.getValue());
		return out;
	}
}
