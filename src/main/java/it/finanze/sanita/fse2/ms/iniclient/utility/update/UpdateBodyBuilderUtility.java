package it.finanze.sanita.fse2.ms.iniclient.utility.update;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.LowLevelDocEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.*;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.*;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.*;

@Slf4j
public final class UpdateBodyBuilderUtility {

	private UpdateBodyBuilderUtility(){}

	///TODO: check proper object factory for update
	private static ObjectFactory objectFactory = new ObjectFactory();

	/**
	 *
	 * @param documentEntryDTO
	 * @param submissionSetEntryDTO
	 * @param jwtPayloadDTO
	 * @return
	 */
	public static SubmitObjectsRequest buildSubmitObjectRequest(
			UpdateRequestDTO updateRequestDTO,
			RegistryObjectListType oldMetadata,
			String uuid,
			JWTTokenDTO jwtTokenDTO
	) {
		SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
		try {
			RegistryObjectListType registryObjectListType = buildRegistryObjectList(
					oldMetadata,
					updateRequestDTO,
					uuid,
					jwtTokenDTO
			);
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
	private static RegistryObjectListType buildRegistryObjectList(
			RegistryObjectListType oldMetadata,
			UpdateRequestDTO updateRequestDTO,
			String uuid,
			JWTTokenDTO jwtTokenDTO
	) {
		String requestUUID = Constants.IniClientConstants.URN_UUID + StringUtility.generateUUID();
		try {
			List<JAXBElement<? extends IdentifiableType>> list = oldMetadata.getIdentifiable();
			ExtrinsicObjectType oldExtrinsicObject = (ExtrinsicObjectType) list.stream()
					.filter(e -> e.getValue() instanceof ExtrinsicObjectType)
					.findFirst().orElseThrow(() -> new NoRecordFoundException("ExtrinsicObject non trovato nei metadati di INI"))
					.getValue();

			RegistryPackageType registryPackageObject = buildBasicRegistryPackageObject();

			// 1. extrinsic object
			ExtrinsicObjectType editedExtrinsicObject = rebuildExtrinsicObjectMetadata(oldExtrinsicObject, updateRequestDTO.getBody());
			JAXBElement<ExtrinsicObjectType> jaxbEditedExtrinsicObject = objectFactory.createExtrinsicObject(editedExtrinsicObject);

			// 2. registry package object
			RegistryPackageType editedRegistryPackageObject = rebuildRegistryPackageObjectMetadata(
					oldExtrinsicObject,
					registryPackageObject,
					updateRequestDTO.getBody(),
					jwtTokenDTO
			);
			JAXBElement<RegistryPackageType> jaxbEditedRegistryPackageObject = objectFactory.createRegistryPackage(editedRegistryPackageObject);

			// 3. Classification object
			JAXBElement<ClassificationType> classificationObject = buildClassificationObjectJax(
					objectFactory,
					"urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd",
					null,
					Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,	///TODO: verify: id of registry package object
					requestUUID,							///TODO: verify content
					null, null, null, null
			);
			oldMetadata.getIdentifiable().add(classificationObject);

			// 4. Association object
			List<SlotType1> associationObjectSlots = new ArrayList<>();
			SlotType1 associationObjSlotSubmissionSetStatus = buildSlotObject(
					"SubmissionSetStatus",
					null,
					Collections.singletonList("Original")
			);
			SlotType1 associationObjSlotPreviousVersion = buildSlotObject(
					"PreviousVersion",
					null,
					Collections.singletonList(oldExtrinsicObject.getVersionInfo().getVersionName())
			);
			associationObjectSlots.add(associationObjSlotSubmissionSetStatus);
			associationObjectSlots.add(associationObjSlotPreviousVersion);

			JAXBElement<AssociationType1> jaxbAssociationObject = buildAssociationObject(
					objectFactory,
					"urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember",
					requestUUID,			///TODO: unique id?
					Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,		///TODO: verify: id of registry package object
					uuid,		///TODO: id of extrinsic object
					associationObjectSlots
			);

			// 4. merge metadata
			oldMetadata.getIdentifiable().clear();
			oldMetadata.getIdentifiable().add(jaxbEditedExtrinsicObject);
			oldMetadata.getIdentifiable().add(jaxbEditedRegistryPackageObject);
			oldMetadata.getIdentifiable().add(jaxbAssociationObject);

			return oldMetadata;

		} catch(Exception ex) {
			log.error("Error while perform build registry object list : " , ex);
			throw new BusinessException("Error while perform build registry object list : " , ex);
		}
	}

	/**
	 * Merge old registry package object metadata with the ones received in the request
	 * @param registryPackageObject
	 * @param updateRequestBodyDTO
	 * @param documentEntryDTO
	 * @param submissionSetEntryDTO
	 * @param jwtPayloadDTO
	 * @return
	 */
	private static RegistryPackageType rebuildRegistryPackageObjectMetadata(
			ExtrinsicObjectType oldExtrinsicObject,
			RegistryPackageType registryPackageObject,
			PublicationMetadataReqDTO updateRequestBodyDTO,
			JWTTokenDTO jwtTokenDTO
	) {
		try {
			// Slots
			// 1. intendedRecipient
			List<String> slotValues = new ArrayList<>(Collections.singletonList(
					jwtTokenDTO.getPayload().getSubject_organization() +
							"^^^^^^^^^" +
							Constants.IniClientConstants.SOURCE_ID_OID +
							jwtTokenDTO.getPayload().getSubject_organization_id().replace("0", "")));
			JAXBElement<SlotType1> slotObject = buildSlotObjectJax(
					objectFactory,
					"intendedRecipient",
					null,
					slotValues
			);
			registryPackageObject.getSlot().add(slotObject.getValue());

			// 2. Name + Description
			ClassificationType formatCodeClassification = oldExtrinsicObject.getClassification().stream()
					.filter(classificationType -> classificationType.getClassificationScheme().equals("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d"))
					.findFirst()
					.orElseThrow(() -> new NoRecordFoundException("Format code non trovato nei metadati di INI"));
			String nodeRepresentation = formatCodeClassification.getNodeRepresentation();
			LowLevelDocEnum lowLevelDocumentName = Arrays.stream(LowLevelDocEnum.values())
					.filter(lowLevelDocEnum -> lowLevelDocEnum.getCode().equals(nodeRepresentation))
					.findFirst()
					.orElse(null);
			InternationalStringType formatCodeName = lowLevelDocumentName != null ? buildInternationalStringType(
					new ArrayList<>(Collections.singleton(lowLevelDocumentName.getDescription()))) : null;
			registryPackageObject.setName(formatCodeName);
			registryPackageObject.setDescription(formatCodeName);

			// Classification Objects
			List<ClassificationType> classificationList = oldExtrinsicObject.getClassification();

			// 1. merge author metadata for the update request
			JAXBElement<ClassificationType> classificationObjectAuthor = mergeAuthorClassificationObject(objectFactory, classificationList);
			registryPackageObject.getClassification().add(classificationObjectAuthor.getValue());

			// 2. merge contentTypeCode
			JAXBElement<ClassificationType> classificationObjectContentTypeCode = mergeContentTypeCodeClassificationObject(objectFactory, updateRequestBodyDTO);
			registryPackageObject.getClassification().add(classificationObjectContentTypeCode.getValue());


			// External Identifiers
			// 1. merge sourceId
			JAXBElement<ExternalIdentifierType> externalIdentifierObjectSourceId = buildExternalIdentifierObjectJax(
					objectFactory,
					"XDSSubmissionSet.sourceId",
					"ExampleSourceIdId_0001",    ///TODO: verify
					"urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
					Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
					Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,    ///TODO: verify: id of registry package object // uuid?
					Constants.IniClientConstants.SOURCE_ID_OID + jwtTokenDTO.getPayload().getSubject_organization_id().replace("0", "")
			);
			registryPackageObject.getExternalIdentifier().add(externalIdentifierObjectSourceId.getValue());

			// 2. merge patientId
			SlotType1 sourcePatientIdSlot = oldExtrinsicObject.getSlot().stream()
					.filter(slot -> slot.getName().equals("sourcePatientId"))
					.findFirst()
					.orElse(null);
			String patientId = "";
			if (sourcePatientIdSlot != null && sourcePatientIdSlot.getValueList() != null && !CollectionUtils.isEmpty(sourcePatientIdSlot.getValueList().getValue())) {
				patientId = sourcePatientIdSlot.getValueList().getValue().get(0);
			} else {
				patientId = jwtTokenDTO.getPayload().getPerson_id();
			}
			JAXBElement<ExternalIdentifierType> externalIdentifierObjectPatientIdd = buildExternalIdentifierObjectJax(
					objectFactory,
					"XDSSubmissionSet.patientId",
					"ExamplePatientIdId_0001",    ///TODO: verify
					"urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446",
					Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
					Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID,    ///TODO: verify: id of registry package object
					patientId
			);
			registryPackageObject.getExternalIdentifier().add(externalIdentifierObjectPatientIdd.getValue());

			// 3. merge uniqueId
			JAXBElement<ExternalIdentifierType> externalIdentifierObjectUniqueId = buildExternalIdentifierObjectJax(
					objectFactory,
					"XDSSubmissionSet.uniqueId",
					"ExampleUniqueIdId_0001",    ///TODO: verify
					"urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8",
					Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
					"registryObject",    ///TODO: verify: id of registry package object // uuid?
					updateRequestBodyDTO.getIdentificativoSottomissione()
			);
			registryPackageObject.getExternalIdentifier().add(externalIdentifierObjectUniqueId.getValue());

			return registryPackageObject;
		} catch (Exception ex) {
			log.error("Error while perform merge registry package object metadata : {}" , ex.getMessage());
			throw new BusinessException("Error while perform merge registry package object metadata : ", ex);
		}
	}

	/**
	 * Rebuild old extrinsic object metadata with the ones received in the request
	 * @param oldExtrinsicObject
	 * @param updateRequestBodyDTO
	 * @param documentEntryDTO
	 * @param submissionSetEntryDTO
	 * @param jwtPayloadDTO
	 * @return
	 */
	private static ExtrinsicObjectType rebuildExtrinsicObjectMetadata(ExtrinsicObjectType oldExtrinsicObject, PublicationMetadataReqDTO updateRequestBodyDTO) {
		try {
			// 1. Classification Objects
			List<ClassificationType> classificationObjectList = oldExtrinsicObject.getClassification();

			// 1.1 merge HealthcareFacilityTypeCode
			mergeHealthcareFacilityTypeCode(updateRequestBodyDTO, classificationObjectList);

			// 1.2 merge eventTypeCodeList -> suspended since attiCliniciRegoleAccesso not used in publication
			// TODO: skip for the moment
			if (!CollectionUtils.isEmpty(updateRequestBodyDTO.getAttiCliniciRegoleAccesso())) {
				mergeEventTypeCode(updateRequestBodyDTO, classificationObjectList);
			}

			// 1.3 merge classCode
			mergeClassCode(updateRequestBodyDTO, classificationObjectList);

			// 1.4 merge practiceSettingCode
			mergePracticeSettingCode(updateRequestBodyDTO, classificationObjectList);

			// 1.5 add all classification objects
			oldExtrinsicObject.getClassification().clear();
			oldExtrinsicObject.getClassification().addAll(classificationObjectList);

			// 2. Slot objects
			List<SlotType1> slotList = oldExtrinsicObject.getSlot();

			// 2.1 Reset slots in extrinsic object
			oldExtrinsicObject.getSlot().clear();

			// 2.2 merge service start / stop time
			mergeServiceStartStopTime(updateRequestBodyDTO, slotList);

			// 2.3 merge repository-type
			if (StringUtils.hasText(updateRequestBodyDTO.getConservazioneANorma())) {
				mergeRepositoryType(updateRequestBodyDTO, slotList);
			}

			///TODO: 2.5 merge referenceId list with urn urn:ihe:iti:xds:2013:referenceIdList da chiarire (paragrafo 2.14 affinity domain)

			///TODO: 2.6 merge sourcePatientInfo -> NON OBBLIGATORIO

			// 2.4 add all slot objects
			oldExtrinsicObject.getSlot().addAll(slotList);

			return oldExtrinsicObject;
		} catch (Exception ex) {
			log.error("Error while perform merge extrinsic object metadata : {}" , ex.getMessage());
			throw new BusinessException("Error while perform merge extrinsic object metadata : ", ex);
		}
	}

	/**
	 *
	 * @param documentEntryDTO
	 * @param submissionSetEntryDTO
	 * @param jwtPayloadDTO
	 * @return
	 */
	private static RegistryPackageType buildBasicRegistryPackageObject() {
		RegistryPackageType registryPackageObject = new RegistryPackageType();
		try {
			registryPackageObject.setId(Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID);	///TODO: verify
			registryPackageObject.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage");
			registryPackageObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
			registryPackageObject.setName(null);
			registryPackageObject.setDescription(null);

			List<String> slotValues = new ArrayList<>(Collections.singletonList(new Date().toString()));
			JAXBElement<SlotType1> slotObject = buildSlotObjectJax(
					objectFactory,
					"submissionTime",
					null,
					slotValues
			);
			registryPackageObject.getSlot().add(slotObject.getValue());
			return registryPackageObject;
		} catch (Exception e) {
			log.error("Error while creating basic registry package object: {}", e.getMessage());
			throw new BusinessException("Error while creating basic registry package object: " + e.getMessage());
		}
	}
}
