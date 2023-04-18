/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.utility.create;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventCodeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.CfUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.*;

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
	public static SubmitObjectsRequest buildSubmitObjectRequest(DocumentEntryDTO documentEntryDTO,
																SubmissionSetEntryDTO submissionSetEntryDTO,JWTPayloadDTO jwtPayloadDTO,String uuid) {
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
	private static RegistryObjectListType buildRegistryObjectList(DocumentEntryDTO documentEntryDTO,SubmissionSetEntryDTO submissionSetEntryDTO,
																  JWTPayloadDTO jwtPayloadDTO,String uuid) {
		RegistryObjectListType registryObjectListType = new RegistryObjectListType();

		try {
			String requestUUID = StringUtility.generateUUID();
			//ExtrinsicObject
			JAXBElement<ExtrinsicObjectType> extrinsicObject = buildExtrinsicObject(Constants.IniClientConstants.URN_UUID + requestUUID, documentEntryDTO.getMimeType(),
				documentEntryDTO,requestUUID,jwtPayloadDTO);
			registryObjectListType.getIdentifiable().add(extrinsicObject);

			//Registry package
			JAXBElement<RegistryPackageType> registryPackageObject = buildRegistryPackageObject(
				documentEntryDTO,submissionSetEntryDTO,jwtPayloadDTO);
			registryObjectListType.getIdentifiable().add(registryPackageObject);

			//Classification Object
			JAXBElement<ClassificationType> classificationObject = buildClassificationObjectJax(
				objectFactory,"urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd",
				null,Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,"SubmissionSet01_Submission",
				null, null, null, null);
			registryObjectListType.getIdentifiable().add(classificationObject);

			//Association object
			List<SlotType1> associationObjectSlots = new ArrayList<>();
			SlotType1 associationObjSlot = buildSlotObject("SubmissionSetStatus", null,Collections.singletonList("Original"));
			associationObjectSlots.add(associationObjSlot);

			JAXBElement<AssociationType1> associationObject = null;
			if (uuid == null) {
				associationObject = buildAssociationObject(objectFactory,
					"urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember","SubmissionSet01_Association_1",
					Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,
					Constants.IniClientConstants.URN_UUID + requestUUID,associationObjectSlots);
			} else {
				associationObject = buildAssociationObject(objectFactory,
					"urn:ihe:iti:2007:AssociationType:RPLC","SubmissionSet01_Association_1",
					Constants.IniClientConstants.URN_UUID + requestUUID,uuid,associationObjectSlots);
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
	private static JAXBElement<ExtrinsicObjectType> buildExtrinsicObject(String id,String mimeType,
																		 DocumentEntryDTO documentEntryDTO,String requestUUID,JWTPayloadDTO jwtPayloadDTO) {

		JAXBElement<ExtrinsicObjectType> output = null;
		try {
			ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();
			extrinsicObject.setId(id);
			extrinsicObject.setIsOpaque(false);
			extrinsicObject.setMimeType(mimeType);
			extrinsicObject.setObjectType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
			extrinsicObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
			buildExtrinsicObjectSlots(extrinsicObject, documentEntryDTO);
			List<ClassificationType> classifications = buildExtrinsicClassificationObjects(documentEntryDTO, requestUUID, jwtPayloadDTO);
			extrinsicObject.getClassification().addAll(classifications);

			String patientId = documentEntryDTO.getSourcePatientId();
			if (CfUtility.isValidCf(patientId)) {
				patientId += Constants.IniClientConstants.VALID_SSN_OID;
			}
			ExternalIdentifierType externalIdentifier1 = buildExternalIdentifierObject("XDSDocumentEntry.patientId",
				"patientId_1",Constants.IniClientConstants.URN_UUID + requestUUID,Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
				"urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",patientId);

			ExternalIdentifierType externalIdentifier2 = buildExternalIdentifierObject("XDSDocumentEntry.uniqueId","uniqueId_1",
				"urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab",Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
				Constants.IniClientConstants.URN_UUID + requestUUID,documentEntryDTO.getUniqueId());
			extrinsicObject.getExternalIdentifier().add(externalIdentifier1);
			extrinsicObject.getExternalIdentifier().add(externalIdentifier2);
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
	private static void buildExtrinsicObjectSlots(ExtrinsicObjectType extrinsicObject, DocumentEntryDTO documentEntryDTO) {
		try {
			// list of slots
			List<String> valuesSlot1 = new ArrayList<>();
			List<String> valuesSlot2 = new ArrayList<>();
			List<String> valuesSlot3 = new ArrayList<>();
			List<String> valuesSlot4 = new ArrayList<>();
			List<String> valuesSlot5 = new ArrayList<>();
			List<String> valuesSlot6 = new ArrayList<>();
			List<String> valuesSlot7 = new ArrayList<>();
			List<String> valuesSlot8 = new ArrayList<>();
			// Adding values
			valuesSlot1.add(documentEntryDTO.getCreationTime());
			valuesSlot2.add(documentEntryDTO.getServiceStartTime());
			valuesSlot3.add(documentEntryDTO.getServiceStopTime());
			valuesSlot4.add(documentEntryDTO.getHash());
			valuesSlot5.add(documentEntryDTO.getLanguageCode());
			valuesSlot6.add(documentEntryDTO.getRepositoryUniqueId());
			valuesSlot7.add(String.valueOf(documentEntryDTO.getSize()));
			valuesSlot8.add(documentEntryDTO.getSourcePatientId());
			// Populate extrinsicObject
			extrinsicObject.getSlot().add(buildSlotObject("creationTime", null, valuesSlot1));
			extrinsicObject.getSlot().add(buildSlotObject("serviceStartTime", null, valuesSlot2));
			extrinsicObject.getSlot().add(buildSlotObject("serviceStopTime", null, valuesSlot3));
			extrinsicObject.getSlot().add(buildSlotObject("hash", null, valuesSlot4));
			extrinsicObject.getSlot().add(buildSlotObject("languageCode", null, valuesSlot5));
			extrinsicObject.getSlot().add(buildSlotObject("repositoryUniqueId", null, valuesSlot6));
			extrinsicObject.getSlot().add(buildSlotObject("size", null, valuesSlot7));
			extrinsicObject.getSlot().add(buildSlotObject("sourcePatientId", null, valuesSlot8));
		} catch (Exception e) {
			log.error("Error while invoking buildInternationalStringType: {}", e.getMessage());
			throw new BusinessException("Error while invoking buildInternationalStringType: " + e.getMessage());
		}
	}

	/**
	 *
	 * @param documentEntryDTO
	 * @param requestUUID
	 * @param jwtPayloadDTO
	 * @return
	 */
	private static List<ClassificationType> buildExtrinsicClassificationObjects(DocumentEntryDTO documentEntryDTO, String requestUUID, JWTPayloadDTO jwtPayloadDTO) {
		List<ClassificationType> out = new ArrayList<>();

		try {
			// Slots 1
			String authorInstitution = documentEntryDTO.getRepresentedOrganizationName() + Constants.IniClientConstants.AUTHOR_INSTITUTION_OID + documentEntryDTO.getRepresentedOrganizationCode();
			SlotType1 classificationObj1Slot1 = buildSlotObject("authorInstitution",null,new ArrayList<>(Collections.singleton(authorInstitution)));
			String enhancedAuthorPerson = documentEntryDTO.getAuthor();
			if (StringUtility.isIVA(enhancedAuthorPerson)) {
				enhancedAuthorPerson+= Constants.IniClientConstants.AUTHOR_IVA_OID;
			} else {
				enhancedAuthorPerson+=Constants.IniClientConstants.GENERIC_SSN_OID;
			}

			SlotType1 classificationObj1Slot2 = buildSlotObject("authorPerson",null,new ArrayList<>(Collections.singleton(enhancedAuthorPerson)));
			SlotType1 classificationObj1Slot3 = buildSlotObject("authorRole",null,new ArrayList<>(Collections.singleton(jwtPayloadDTO.getSubject_role())));
			List<SlotType1> classificationObj1Slots = new ArrayList<>();
			classificationObj1Slots.add(classificationObj1Slot1);
			classificationObj1Slots.add(classificationObj1Slot2);
			classificationObj1Slots.add(classificationObj1Slot3);

			if(documentEntryDTO.getDescription() != null) {
				SlotType1 classificationObj1Slot5 = buildSlotObject("urn:ita:2022:description", null, new ArrayList<>(documentEntryDTO.getDescription()));
				classificationObj1Slots.add(classificationObj1Slot5);
			}

			if(documentEntryDTO.getAdministrativeRequest() != null) {
				SlotType1 classificationObj1Slot4 = buildSlotObject("urn:ita:2022:administrativeRequest", null, new ArrayList<>(Collections.singleton(documentEntryDTO.getAdministrativeRequest())));
				classificationObj1Slots.add(classificationObj1Slot4);
			}

			ClassificationType classificationObject1 = buildClassificationObject(null,"urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d",
				Constants.IniClientConstants.URN_UUID + requestUUID,"Author_1",null,classificationObj1Slots,Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,null);

			out.add(classificationObject1);

			// Slots 2
			SlotType1 classificationObj2Slot1 = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME,null,new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.5")));
			List<SlotType1> classificationObj2Slots = new ArrayList<>();
			classificationObj2Slots.add(classificationObj2Slot1);
			InternationalStringType name2 = buildInternationalStringType(new ArrayList<>(Collections.singleton(documentEntryDTO.getClassCodeName())));
			ClassificationType classificationObject2 = buildClassificationObject(
				null,"urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a",Constants.IniClientConstants.URN_UUID + requestUUID,"ClassCodeId_1",name2,
				classificationObj2Slots,Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,documentEntryDTO.getClassCode());
			out.add(classificationObject2);

			// Slots 3
			SlotType1 classificationObj3Slot1 = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME,null,new ArrayList<>(Collections.singleton("2.16.840.1.113883.5.25")));
			List<SlotType1> classificationObj3Slots = new ArrayList<>();
			classificationObj3Slots.add(classificationObj3Slot1);
			InternationalStringType name3 = buildInternationalStringType(new ArrayList<>(Collections.singleton(documentEntryDTO.getConfidentialityCodeDisplayName())));
			ClassificationType classificationObject3 = buildClassificationObject("",
				"urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f",Constants.IniClientConstants.URN_UUID + requestUUID,"ConfidentialityLevel_1",
				name3,classificationObj3Slots,Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,documentEntryDTO.getConfidentialityCode());
			out.add(classificationObject3);

			// Slots 4
			SlotType1 classificationObj4Slot1 = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME,null,new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.6")));
			List<SlotType1> classificationObj4Slots = new ArrayList<>();
			classificationObj4Slots.add(classificationObj4Slot1);
			InternationalStringType name4 = buildInternationalStringType(new ArrayList<>(Collections.singleton(documentEntryDTO.getFormatCodeName())));
			ClassificationType classificationObject4 = buildClassificationObject(null,"urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",Constants.IniClientConstants.URN_UUID + requestUUID,
				"FormatCode_1",name4,classificationObj4Slots,Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,documentEntryDTO.getFormatCode());
			out.add(classificationObject4);

			// Slots 5
			SlotType1 classificationObj5Slot1 = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME,null,new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.1")));
			List<SlotType1> classificationObj5Slots = new ArrayList<>();
			classificationObj5Slots.add(classificationObj5Slot1);
			InternationalStringType name5 = buildInternationalStringType(
				new ArrayList<>(Collections.singleton(documentEntryDTO.getHealthcareFacilityTypeCodeName())));
			ClassificationType classificationObject5 = buildClassificationObject(
				null,"urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1",Constants.IniClientConstants.URN_UUID + requestUUID,
				"healthcareFacilityTypeCode_1",name5,
				classificationObj5Slots,Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,documentEntryDTO.getHealthcareFacilityTypeCode()
			);
			out.add(classificationObject5);

			// Slots 6
			SlotType1 classificationObj6Slot1 = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME,null, Collections.singletonList("2.16.840.1.113883.2.9.3.3.6.1.2"));
			List<SlotType1> classificationObj6Slots = new ArrayList<>();
			classificationObj6Slots.add(classificationObj6Slot1);
			InternationalStringType name6 = buildInternationalStringType(new ArrayList<>(Collections.singleton(documentEntryDTO.getPracticeSettingCodeName())));
			ClassificationType classificationObject6 = buildClassificationObject(null,"urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",
				Constants.IniClientConstants.URN_UUID + requestUUID,"practiceSettingCode_1",name6,classificationObj6Slots,Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,
				documentEntryDTO.getPracticeSettingCode());
			out.add(classificationObject6);

			// Slots 7
			SlotType1 classificationObj7Slot1 = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME,null,Collections.singletonList("2.16.840.1.113883.6.1"));
			List<SlotType1> classificationObj7Slots = new ArrayList<>();
			classificationObj7Slots.add(classificationObj7Slot1);
			InternationalStringType name7 = buildInternationalStringType(Collections.singletonList(documentEntryDTO.getTypeCodeName()));
			ClassificationType classificationObject7 = buildClassificationObject(null,"urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",
				Constants.IniClientConstants.URN_UUID + requestUUID,"typeCode_1",name7,classificationObj7Slots,Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,documentEntryDTO.getTypeCode());
			out.add(classificationObject7);

			// Slots 8-N
			if (!CollectionUtils.isEmpty(documentEntryDTO.getEventCodeList())) {
				for (String eventCode : documentEntryDTO.getEventCodeList()) {
					SlotType1 classificationObjNSlot1 = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME, null, Collections.singletonList("2.16.840.1.113883.2.9.3.3.6.1.3"));
					List<SlotType1> classificationObjNSlots = new ArrayList<>();
					classificationObjNSlots.add(classificationObjNSlot1);
					InternationalStringType nameN = buildInternationalStringType(Collections.singletonList(EventCodeEnum.fromValue(eventCode).getDescription()));
					ClassificationType classificationObjectN = buildClassificationObject(null, "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4",
						Constants.IniClientConstants.URN_UUID + requestUUID, "IdEventCodeList", nameN, classificationObjNSlots, Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN, eventCode);
					out.add(classificationObjectN);
				}
			}
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
	private static JAXBElement<RegistryPackageType> buildRegistryPackageObject(DocumentEntryDTO documentEntryDTO,
																			   SubmissionSetEntryDTO submissionSetEntryDTO,JWTPayloadDTO jwtPayloadDTO) {

		RegistryPackageType registryPackageObject = new RegistryPackageType();

		try {
			registryPackageObject.setId(Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID);
			registryPackageObject.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage");
			registryPackageObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
			registryPackageObject.setName(null);
			registryPackageObject.setDescription(null);

			List<String> slotValues = new ArrayList<>(Collections.singletonList(submissionSetEntryDTO.getSubmissionTime()));
			JAXBElement<SlotType1> slotObject = buildSlotObjectJax(objectFactory,"submissionTime",null,slotValues);
			registryPackageObject.getSlot().add(slotObject.getValue());

			String authorInstitution = documentEntryDTO.getRepresentedOrganizationName() + Constants.IniClientConstants.AUTHOR_INSTITUTION_OID + documentEntryDTO.getRepresentedOrganizationCode();
			String authorPerson = documentEntryDTO.getAuthor();
			String enhancedAuthorPerson = authorPerson;
			if (StringUtility.isIVA(authorPerson)) {
				enhancedAuthorPerson += Constants.IniClientConstants.AUTHOR_IVA_OID;
			} else {
				enhancedAuthorPerson += Constants.IniClientConstants.GENERIC_SSN_OID;
			}
			// Classification objects
			SlotType1 classificationObj1Slot1 = buildSlotObject("authorInstitution", null, new ArrayList<>(Collections.singleton(authorInstitution)));
			SlotType1 classificationObj1Slot2 = buildSlotObject("authorPerson", null, Collections.singletonList(enhancedAuthorPerson));
			SlotType1 classificationObj1Slot3 = buildSlotObject("authorRole", null, Collections.singletonList(jwtPayloadDTO.getSubject_role()));
			List<SlotType1> classificationObj1Slots = new ArrayList<>();
			classificationObj1Slots.add(classificationObj1Slot1);
			classificationObj1Slots.add(classificationObj1Slot2);
			classificationObj1Slots.add(classificationObj1Slot3);

			JAXBElement<ClassificationType> classificationObject1 = buildClassificationObjectJax(objectFactory,
				null,"urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d",Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,
				"SubmissionSet01_ClassificationAuthor",null,classificationObj1Slots,Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,"");
			registryPackageObject.getClassification().add(classificationObject1.getValue());

			InternationalStringType name2 = buildInternationalStringType(Collections.singletonList(submissionSetEntryDTO.getContentTypeCodeName()));
			SlotType1 classificationObj2Slot1 = buildSlotObject(
				Constants.IniClientConstants.CODING_SCHEME,
				null,
				Collections.singletonList("2.16.840.1.113883.2.9.3.3.6.1.4")
			);
			List<SlotType1> classificationObj2Slots = new ArrayList<>();
			classificationObj2Slots.add(classificationObj2Slot1);

			JAXBElement<ClassificationType> classificationObject2 = buildClassificationObjectJax(objectFactory,
				null,"urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,
				"SubmissionSet01_ClinicalActivity",name2,classificationObj2Slots,
				Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,submissionSetEntryDTO.getContentTypeCode()
			);
			registryPackageObject.getClassification().add(classificationObject2.getValue());

			// Build external identifiers
			JAXBElement<ExternalIdentifierType> externalIdentifierObject1 = buildExternalIdentifierObjectJax(
				objectFactory,"XDSSubmissionSet.sourceId","SubmissionSet01_SourceId",
				"urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
				Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
				Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,
				submissionSetEntryDTO.getSourceId());
			registryPackageObject.getExternalIdentifier().add(externalIdentifierObject1.getValue());

			JAXBElement<ExternalIdentifierType> externalIdentifierObject2 = buildExternalIdentifierObjectJax(
				objectFactory,"XDSSubmissionSet.uniqueId",
				"SubmissionSet01_UniqueId",
				"urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8",
				Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
				Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,
				submissionSetEntryDTO.getUniqueID());
			registryPackageObject.getExternalIdentifier().add(externalIdentifierObject2.getValue());

			return objectFactory.createRegistryPackage(registryPackageObject);
		} catch (Exception e) {
			log.error("Error while invoking buildRegistryPackageObject: {}", e.getMessage());
			throw new BusinessException("Error while invoking buildRegistryPackageObject: " + e.getMessage());
		}
	}
}
