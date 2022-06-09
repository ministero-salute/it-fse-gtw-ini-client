package it.finanze.sanita.fse2.ms.iniclient.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;
import org.bson.Document;

public final class SamlBodyBuilderUtilityNew {

	/**
	 * COMPLETE
	 *
	 * @param qname
	 * @return
	 */
	public static SubmitObjectsRequest buildSubmitObjectRequest(QName qname, DocumentEntryDTO documentEntryDTO, SubmissionSetEntryDTO submissionSetEntryDTO) {
		SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
		RegistryObjectListType registryObjectListType = buildRegistryObjectList(qname, documentEntryDTO, submissionSetEntryDTO);
		submitObjectsRequest.setRegistryObjectList(registryObjectListType);
		return submitObjectsRequest;
	}

	/**
	 * COMPLETE
	 *
	 * @param qname
	 * @param documentEntryDTO
	 * @return
	 */
	private static RegistryObjectListType buildRegistryObjectList(QName qname, DocumentEntryDTO documentEntryDTO, SubmissionSetEntryDTO submissionSetEntryDTO) {
		String requestUUID = Utilities.generateUUID();
		ExtrinsicObjectType extrinsicObject = buildExtrinsicObject(
				"urn:uuid:" + requestUUID,
				false,
				documentEntryDTO.getMimeType(),
				"urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1",
				"urn:oasis:names:tc:ebxml-regrep:StatusType:Approved",
				documentEntryDTO,
				requestUUID
				);
		RegistryPackageType registryPackageObject = buildRegistryPackageObject(
				"SubmissionSet01",    ///TODO: check -> FIXED VALUE
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage",
				"urn:oasis:names:tc:ebxml-regrep:StatusType:Approved",
				null, //buildInternationalStringType(Collections.singletonList("Prescrizione")),
				// /TODO: check FHIR mapping, RegistryPackage <Name> -> FIXED MAYBE USELESS
				null, 		//buildInternationalStringType(Collections.singletonList("Prescrizione SistemaTS")),
				// /TODO: check FHIR mapping, RegistryPackage <Description> -> FIXED MAYBE USELESS
				qname,
				documentEntryDTO,
				submissionSetEntryDTO
				);
		ClassificationType classificationObject = buildClassificationObject(
				"urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd",
				null,
				"SubmissionSet01",    ///TODO: check -> FIXED VALUE
				"SubmissionSet01_Submission",	  ///TODO: check -> FIXED VALUE
				null,
				null,
				null,
				null);
		List<SlotType1> associationObjectSlots = new ArrayList<>();
		SlotType1 associationObjSlot = buildSlotObject(
				"SubmissionSetStatus",  ///TODO: check -> FIXED VALUE
				null,
				Collections.singletonList("Original")); ///TODO: check -> FIXED VALUE
		associationObjectSlots.add(associationObjSlot);
		AssociationType1 associationObject = buildAssociationObject(
				"urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember",
				"SubmissionSet01_Association_1",    ///TODO: check -> FIXED VALUE
				"SubmissionSet01",					   ///TODO: checK -> FIXED VALUE
				"urn:uuid:" + requestUUID,
				associationObjectSlots);

		RegistryObjectListType registryObjectListType = new RegistryObjectListType();

		registryObjectListType.getIdentifiable().add(convertToJAXBElement(qname, ExtrinsicObjectType.class, extrinsicObject));
		registryObjectListType.getIdentifiable().add(convertToJAXBElement(qname, RegistryPackageType.class, registryPackageObject));
		registryObjectListType.getIdentifiable().add(convertToJAXBElement(qname, ClassificationType.class, classificationObject));
		registryObjectListType.getIdentifiable().add(convertToJAXBElement(qname, AssociationType1.class, associationObject));
		return registryObjectListType;
	}

	/**
	 * 8 slots
	 * 7 classifications
	 * 2 external identifiers
	 * COMPLETE
	 *
	 * @param id
	 * @param isOpaque
	 * @param mimeType
	 * @param objectType
	 * @param status
	 * @param documentEntryDTO
	 * @return
	 */
	private static ExtrinsicObjectType buildExtrinsicObject(
			String id,
			boolean isOpaque,
			String mimeType,
			String objectType,
			String status,
			DocumentEntryDTO documentEntryDTO,
			String requestUUID) {
		ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();
		extrinsicObject.setId(id);
		extrinsicObject.setIsOpaque(isOpaque);
		extrinsicObject.setMimeType(mimeType);
		extrinsicObject.setObjectType(objectType);
		extrinsicObject.setStatus(status);
		buildExtrinsicObjectSlots(extrinsicObject, documentEntryDTO);
		buildExtrinsicClassificationObjects(extrinsicObject, documentEntryDTO, requestUUID);
		String patientId = documentEntryDTO.getSourcePatientId();
		if (CfUtility.isValidCf(patientId)) {
			patientId += Constants.AppConstants.VALID_SSN_OID;
		}

		ExternalIdentifierType externalIdentifier1 = buildExternalIdentifierObject(
				"XDSDocumentEntry.patientId",      ///TODO: is it fixed value? -> FIXED VALUE
				"patientId_1",							///TODO: is it fixed value? -> FIXED VALUE
				"urn:uuid:" + requestUUID,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
				"urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",
				patientId);
		///TODO: check sourcePatientId + 2.16.840.1.113883.2.9.4.3.2 + ISO -> FIXED
		ExternalIdentifierType externalIdentifier2 = buildExternalIdentifierObject(
				"XDSDocumentEntry.uniqueId",		///TODO: is it fixed value? -> FIXED VALUE
				"uniqueId_1",							///TODO: is it fixed value? -> FIXED VALUE
				"urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab",
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
				"urn:uuid:" + requestUUID,
				documentEntryDTO.getUniqueId());
		///TODO: where? documentEntryDTO.getUniqueId() OR OID (check submissionSetEntry external id2) -> MAYBE FIXED
		extrinsicObject.getExternalIdentifier().add(externalIdentifier1);
		extrinsicObject.getExternalIdentifier().add(externalIdentifier2);
		return extrinsicObject;
	}

	/**
	 *
	 * @param extrinsicObject
	 * @param documentEntryDTO
	 */
	private static void buildExtrinsicObjectSlots(ExtrinsicObjectType extrinsicObject, DocumentEntryDTO documentEntryDTO) {
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
	}

	/**
	 *
	 * @param extrinsicObject
	 * @param documentEntryDTO
	 */
	private static void buildExtrinsicClassificationObjects(ExtrinsicObjectType extrinsicObject, DocumentEntryDTO documentEntryDTO, String requestUUID) {
		// Slots 1
		String authorInstitution = documentEntryDTO.getRepresentedOrganizationName() + Constants.AppConstants.AUTHOR_INSTITUTION_OID + documentEntryDTO.getRepresentedOrganizationCode();
		SlotType1 classificationObj1Slot1 = buildSlotObject(
				"authorInstitution",
				null,
				new ArrayList<>(Collections.singleton(authorInstitution)));
		///TODO: where? -> Dynamic value -> FIXED
		String authorPerson = documentEntryDTO.getAuthor();
		String enhancedAuthorPerson = authorPerson;
		if (Utilities.isIVA(authorPerson)) {
			enhancedAuthorPerson+= Constants.AppConstants.AUTHOR_IVA_OID;
		} else {
			enhancedAuthorPerson+=Constants.AppConstants.AUTHOR_SSN_OID;
		}
		SlotType1 classificationObj1Slot2 = buildSlotObject(
				"authorPerson",
				null,
				new ArrayList<>(
						Collections.singleton(enhancedAuthorPerson)));
		///TODO: where? documentEntryDTO.getAuthor() -> FIXED
		SlotType1 classificationObj1Slot3 = buildSlotObject(
				"authorRole",
				null,
				new ArrayList<>(Collections.singleton("APR")));
		///TODO: where?
		List<SlotType1> classificationObj1Slots = new ArrayList<>();
		classificationObj1Slots.add(classificationObj1Slot1);
		classificationObj1Slots.add(classificationObj1Slot2);
		classificationObj1Slots.add(classificationObj1Slot3);
		ClassificationType classificationObject1 = buildClassificationObject(
				null,
				"urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d",
				"urn:uuid:" + requestUUID,
				"Author_1",	///TODO: is it fixed value? -> FIXED VALUE
				null,
				classificationObj1Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				null	///TODO where? documentEntryDTO.getAuthor() ? -> FIXED MAYBE USELESS (NO MENTION IN PDF)
			);

		// Slots 2
		SlotType1 classificationObj2Slot1 = buildSlotObject(
				"codingScheme",
				null,
				new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.5")));	///TODO: where? -> FIXED VALUE
		List<SlotType1> classificationObj2Slots = new ArrayList<>();
		classificationObj2Slots.add(classificationObj2Slot1);
		InternationalStringType name2 = buildInternationalStringType(new ArrayList<>(Collections.singleton(documentEntryDTO.getClassCodeName())));
		/// TODO: where? IN PDF BUT NOT ON DB -> documentEntry.classCodeName -> FIXED
		ClassificationType classificationObject2 = buildClassificationObject(
				null,
				"urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a",
				"urn:uuid:" + requestUUID,
				"ClassCodeId_1",
				name2,
				classificationObj2Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				documentEntryDTO.getClassCode()
				///TODO: where? documentEntry.classCode -> FIXED VALUE
		);

		// Slots 3
		SlotType1 classificationObj3Slot1 = buildSlotObject(
				"codingScheme",
				null,
				new ArrayList<>(Collections.singleton("2.16.840.1.113883.5.25")));
		///TODO: where? -> FIXED VALUE
		List<SlotType1> classificationObj3Slots = new ArrayList<>();
		classificationObj3Slots.add(classificationObj3Slot1);
		InternationalStringType name3 = buildInternationalStringType(new ArrayList<>(Collections.singleton(documentEntryDTO.getConfidentialityCodeDisplayName())));
		///TODO: where? -> IN PDF BUT NOT ON DB -> documentEntry.confidentialityCodeName -> FIXED
		ClassificationType classificationObject3 = buildClassificationObject(
				"",
				"urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f",
				"urn:uuid:" + requestUUID,
				"ConfidentialityLevel_1",
				null, //name3,
				classificationObj3Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				documentEntryDTO.getConfidentialityCode());
		///TODO: where? documentEntryDTO.getConfidentialityCode()? -> FIXED

		// Slots 4
		SlotType1 classificationObj4Slot1 = buildSlotObject(
				"codingScheme",
				null,
				new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.6")));	  ///TODO: where? -> FIXED VALUE
		List<SlotType1> classificationObj4Slots = new ArrayList<>();
		classificationObj4Slots.add(classificationObj4Slot1);
		InternationalStringType name4 = buildInternationalStringType(new ArrayList<>(Collections.singleton(documentEntryDTO.getFormatCodeName())));
		///TODO: where? -> IN PDF BUT NOT ON DB -> documentEntry.formatCodeName -> FIXED
		ClassificationType classificationObject4 = buildClassificationObject(
				null,
				"urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",
				"urn:uuid:" + requestUUID,
				"FormatCode_1",
				name4,
				classificationObj4Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				documentEntryDTO.getFormatCode());
		///TODO: where? -> FIXED documentEntry.formatCode

		// Slots 5
		SlotType1 classificationObj5Slot1 = buildSlotObject(
				"codingScheme",
				null,
				new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.1")));		///TODO: where? FIXED
		List<SlotType1> classificationObj5Slots = new ArrayList<>();
		classificationObj5Slots.add(classificationObj5Slot1);
		InternationalStringType name5 = buildInternationalStringType(
				new ArrayList<>(Collections.singleton(documentEntryDTO.getHealthcareFacilityTypeCodeName())));
		///TODO: where? documentEntryDTO.getHealthCareFacilityTypeCode(); -> FIXED -> CODE = NAME
		ClassificationType classificationObject5 = buildClassificationObject(
				null,
				"urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1",
				"urn:uuid:" + requestUUID,
				"healthcareFacilityTypeCode_1",
				null,
				classificationObj5Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				documentEntryDTO.getHealthcareFacilityTypeCode());
				///TODO: where? documentEntryDTO.getHealthCareFacilityTypeCode() -> FIXED

		// Slots 6
		SlotType1 classificationObj6Slot1 = buildSlotObject(
				"codingScheme",
				null,
				Collections.singletonList("2.16.840.1.113883.2.9.3.3.6.1.2"));	///TODO: where? -> FIXED
		List<SlotType1> classificationObj6Slots = new ArrayList<>();
		classificationObj6Slots.add(classificationObj6Slot1);
		InternationalStringType name6 = buildInternationalStringType(
				new ArrayList<>(Collections.singleton(documentEntryDTO.getPracticeSettingCodeName())));
		///	TODO: where? documentEntryDto.getPracticeSettingCode()? -> FIXED
		ClassificationType classificationObject6 = buildClassificationObject(
				null,
				"urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",
				"urn:uuid:" + requestUUID,
				"practiceSettingCode_1",
				name6,
				classificationObj6Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				documentEntryDTO.getPracticeSettingCode());
		///TODO: where? -> IN PDF BUT NOT ON DB -> documentEntry.practiceSettingCodeName -> FIXED

		// Slots 7
		SlotType1 classificationObj7Slot1 = buildSlotObject(
				"codingScheme",
				null,
				Collections.singletonList("2.16.840.1.113883.6.1")); 	///TODO: where? -> FIXED
		List<SlotType1> classificationObj7Slots = new ArrayList<>();
		classificationObj7Slots.add(classificationObj7Slot1);
		InternationalStringType name7 = buildInternationalStringType(
				Collections.singletonList(documentEntryDTO.getTypeCodeName()));
		///TODO: where? -> IN PDF BUT NOT ON DB -> documentEntry.typeCodeName -> FIXED
		ClassificationType classificationObject7 = buildClassificationObject(
				null,
				"urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",
				"urn:uuid:" + requestUUID,
				"typeCode_1",
				name7,
				classificationObj7Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				documentEntryDTO.getTypeCode());
		///TODO: where? documentEntryDTO.getTypeCode()? -> FIXED

		extrinsicObject.getClassification().add(classificationObject1);
		extrinsicObject.getClassification().add(classificationObject2);
		extrinsicObject.getClassification().add(classificationObject3);
		extrinsicObject.getClassification().add(classificationObject4);
		extrinsicObject.getClassification().add(classificationObject5);
		extrinsicObject.getClassification().add(classificationObject6);
		extrinsicObject.getClassification().add(classificationObject7);
	}

	/**
	 * COMPLETE
	 *
	 * @param names
	 * @return
	 */
	private static InternationalStringType buildInternationalStringType(List<String> names) {
		InternationalStringType internationalStringObject = new InternationalStringType();
		List<LocalizedStringType> localizedStringsList = new ArrayList<>();
		for (String name : names) {
			LocalizedStringType localizedStringObject = new LocalizedStringType();
			localizedStringObject.setValue(name);
			localizedStringsList.add(localizedStringObject);
		}
		internationalStringObject.getLocalizedString().addAll(localizedStringsList);
		return internationalStringObject;
	}

	/**
	 * COMPLETE
	 *
	 * @param id
	 * @param objectType
	 * @param status
	 * @param name
	 * @param description
	 * @param qname
	 * @return
	 */
	private static RegistryPackageType buildRegistryPackageObject(
			String id,
			String objectType,
			String status,
			InternationalStringType name,
			InternationalStringType description,
			QName qname,
			DocumentEntryDTO documentEntryDTO,
			SubmissionSetEntryDTO submissionSetEntryDTO) {
		RegistryPackageType registryPackageObject = new RegistryPackageType();
		registryPackageObject.setId(id);
		registryPackageObject.setObjectType(objectType);
		registryPackageObject.setStatus(status);
		registryPackageObject.setName(name);
		registryPackageObject.setDescription(description);
		RegistryObjectListType registryPackageObjectList = buildRegistryPackageObjectList(qname, documentEntryDTO, submissionSetEntryDTO);
		registryPackageObject.setRegistryObjectList(registryPackageObjectList);
		return registryPackageObject;
	}

	/**
	 * COMPLETE
	 * @param qname
	 * @return
	 */
	private static RegistryObjectListType buildRegistryPackageObjectList(QName qname, DocumentEntryDTO documentEntryDTO, SubmissionSetEntryDTO submissionSetEntryDTO) {
		RegistryObjectListType registryPackageObjectList = new RegistryObjectListType();
		// Slot
		String slotName = "submissionTime";
		String slotType = null;
		List<String> slotValues = new ArrayList<>(Collections.singletonList(submissionSetEntryDTO.getSubmissionTime()));
		SlotType1 slotObject = buildSlotObject(slotName, slotType, slotValues);
		String authorInstitution = documentEntryDTO.getRepresentedOrganizationName() + Constants.AppConstants.AUTHOR_INSTITUTION_OID + documentEntryDTO.getRepresentedOrganizationCode();
		String authorPerson = documentEntryDTO.getAuthor();
		String enhancedAuthorPerson = authorPerson;
		if (Utilities.isIVA(authorPerson)) {
			enhancedAuthorPerson+= Constants.AppConstants.AUTHOR_IVA_OID;
		} else {
			enhancedAuthorPerson+=Constants.AppConstants.AUTHOR_SSN_OID;
		}
		// Classification objects
		SlotType1 classificationObj1Slot1 = buildSlotObject(
				"authorInstitution",
				null,
				new ArrayList<>(Collections.singleton(authorInstitution)));
		///TODO: where? -> FIXED
		SlotType1 classificationObj1Slot2 = buildSlotObject(
				"authorPerson",
				null,
				Collections.singletonList(enhancedAuthorPerson));
		///TODO: where? documentEntryDTO.author + something? -> FIXED
		SlotType1 classificationObj1Slot3 = buildSlotObject(
				"authorRole",
				null,
				Collections.singletonList("APR"));
		///TODO: where?
		List<SlotType1> classificationObj1Slots = new ArrayList<>();
		classificationObj1Slots.add(classificationObj1Slot1);
		classificationObj1Slots.add(classificationObj1Slot2);
		classificationObj1Slots.add(classificationObj1Slot3);
		ClassificationType classificationObject1 = buildClassificationObject(
				null,
				"urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d",
				"SubmissionSet01",
				"SubmissionSet01_ClassificationAuthor",
				null,
				classificationObj1Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				"");

		InternationalStringType name2 = buildInternationalStringType(Collections.singletonList(submissionSetEntryDTO.getContentTypeCodeName()));
		///TODO: where? -> IN PDF BUT NOT ON DB -> submissionSetEntry.contentTypeCodeName -> FIXED
		SlotType1 classificationObj2Slot1 = buildSlotObject(
				"codingScheme",
				null,
				Collections.singletonList("2.16.840.1.113883.2.9.3.3.6.1.4"));
		///TODO: where? -> FIXED VALUE
		List<SlotType1> classificationObj2Slots = new ArrayList<>();
		classificationObj2Slots.add(classificationObj2Slot1);
		ClassificationType classificationObject2 = buildClassificationObject(
				null,
				"urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",
				"SubmissionSet01",
				"SubmissionSet01_ClinicalActivity",
				name2,
				classificationObj2Slots,
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
				submissionSetEntryDTO.getContentTypeCode());
		///TODO: where? submissionSetEntryDTO.getContentTypeCode()? -> FIXED VALUE

		String patientId = documentEntryDTO.getSourcePatientId();
		if (CfUtility.isValidCf(patientId)) {
			patientId += Constants.AppConstants.VALID_SSN_OID;
		}

		// External identifier objects
		ExternalIdentifierType externalIdentifierObject1 = buildExternalIdentifierObject(
				"XDSSubmissionSet.patientId",
				"SubmissionSet01_PatientId",
				"urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446",
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
				"SubmissionSet01",
				patientId
		///TODO: where? > IS IT DYNAMIC VALUE ? -> MAYBE FIXED
				);
		ExternalIdentifierType externalIdentifierObject2 = buildExternalIdentifierObject(
				"XDSSubmissionSet.sourceId",
				"SubmissionSet01_SourceId",
				"urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
				"SubmissionSet01",
				submissionSetEntryDTO.getSourceId()
		///TODO: where? submissionSetEntryDTO.getSourceId()? -> IS IT DYNAMIC VALUE? YES ->
				// using OID of RDE/RCD o del Sistema TS
				);
		ExternalIdentifierType externalIdentifierObject3 = buildExternalIdentifierObject(
				"XDSSubmissionSet.uniqueId",
				"SubmissionSet01_UniqueId",
				"urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8",
				"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
				"SubmissionSet01",
				submissionSetEntryDTO.getUniqueID()
		///TODO: where? submissionSetEntryDTO.getUniqueId()? -> FIXED FROM DB
			);

		// Add to package list
		registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, SlotType1.class, slotObject));
		registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ClassificationType.class, classificationObject1));
		registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ClassificationType.class, classificationObject2));
		registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ExternalIdentifierType.class, externalIdentifierObject1));
		registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ExternalIdentifierType.class, externalIdentifierObject2));
		registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ExternalIdentifierType.class, externalIdentifierObject3));
		return registryPackageObjectList;
	}

	/**
	 * COMPLETE
	 *
	 * @param name
	 * @param id
	 * @param identificationScheme
	 * @param objectType
	 * @param registryObject
	 * @param value
	 * @return
	 */
	private static ExternalIdentifierType buildExternalIdentifierObject(
			String name,
			String id,
			String identificationScheme,
			String objectType,
			String registryObject,
			String value) {
		ExternalIdentifierType externalIdentifierObject = new ExternalIdentifierType();
		InternationalStringType nameType = new InternationalStringType();
		LocalizedStringType localizedString = new LocalizedStringType();
		localizedString.setValue(name);
		nameType.getLocalizedString().add(localizedString);
		externalIdentifierObject.setName(nameType);
		externalIdentifierObject.setId(id);
		externalIdentifierObject.setIdentificationScheme(identificationScheme);
		externalIdentifierObject.setObjectType(objectType);
		externalIdentifierObject.setRegistryObject(registryObject);
		externalIdentifierObject.setValue(value);
		return externalIdentifierObject;
	}

	/**
	 * COMPLETE
	 *
	 * @param associationType
	 * @param id
	 * @param sourceObject
	 * @param targetObject
	 * @param slots
	 * @return
	 */
	private static AssociationType1 buildAssociationObject(
			String associationType,
			String id,
			String sourceObject,
			String targetObject,
			List<SlotType1> slots) {
		AssociationType1 associationObject = new AssociationType1();
		associationObject.setAssociationType(associationType);
		associationObject.setId(id);
		associationObject.setSourceObject(sourceObject);
		associationObject.setTargetObject(targetObject);
		if (slots != null) {
			associationObject.getSlot().addAll(slots);
		}
		return associationObject;
	}

	/**
	 * COMPLETE
	 *
	 * @param classificationNode
	 * @param classificationScheme
	 * @param classifiedObject
	 * @param id
	 * @param name
	 * @param slots
	 * @param objectType
	 * @param nodeRepresentation
	 * @return
	 */
	private static ClassificationType buildClassificationObject(
			String classificationNode,
			String classificationScheme,
			String classifiedObject,
			String id,
			InternationalStringType name,
			List<SlotType1> slots,
			String objectType,
			String nodeRepresentation) {
		ClassificationType classificationObject = new ClassificationType();
		classificationObject.setClassificationNode(classificationNode);
		classificationObject.setClassificationScheme(classificationScheme);
		classificationObject.setClassifiedObject(classifiedObject);
		classificationObject.setId(id);
		classificationObject.setName(name);
		if (slots != null) {
			classificationObject.getSlot().addAll(slots);
		}
		classificationObject.setObjectType(objectType);
		classificationObject.setNodeRepresentation(nodeRepresentation);
		return classificationObject;
	}

	/**
	 * COMPLETE
	 *
	 * @param name
	 * @param type
	 * @param values
	 * @return
	 */
	private static SlotType1 buildSlotObject(
			String name,
			String type,
			List<String> values
			) {
		SlotType1 slotObject = new SlotType1();
		slotObject.setName(name);
		slotObject.setSlotType(type);
		ValueListType valueList = new ValueListType();
		for (String value : values) {
			valueList.getValue().add(value);
		}
		slotObject.setValueList(valueList);
		return slotObject;
	}

	/**
	 * COMPLETE
	 *
	 * @param inputClass
	 * @param inputObject
	 * @return
	 * @param <T>
	 */
	public static <T> JAXBElement convertToJAXBElement(QName qName, Class<?> inputClass, T inputObject) {
		JAXBElement jaxbElement = new JAXBElement(qName, inputClass, inputObject);
		return jaxbElement;
	}

	public static DocumentEntryDTO extractDocumentEntry(Document documentEntry) {
		return Utilities.clone(documentEntry, DocumentEntryDTO.class);
	}

	public static SubmissionSetEntryDTO extractSubmissionSetEntry(Document submissionSetEntry) {
		return Utilities.clone(submissionSetEntry, SubmissionSetEntryDTO.class);
	}
}
