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
package it.finanze.sanita.fse2.ms.iniclient.utility.update;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildAssociationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildExternalIdentifierObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildInternationalStringType;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeAuthorClassificationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeClassCode;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeContentTypeCodeClassificationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeEventTypeCode;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeHealthcareFacilityTypeCode;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergePracticeSettingCode;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeServiceStartStopTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.springframework.util.CollectionUtils;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.FormatCodeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.MergeMetadatoNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@Slf4j
public final class UpdateBodyBuilderUtility {

	private UpdateBodyBuilderUtility(){}

	private static ObjectFactory objectFactory = new ObjectFactory();

	/**
	 *
	 * @param updateRequestDTO
	 * @param oldMetadata
	 * @param uuid
	 * @param jwtTokenDTO
	 * @return
	 */
	public static SubmitObjectsRequest buildSubmitObjectRequest(MergedMetadatiRequestDTO updateRequestDTO,RegistryObjectListType oldMetadata,
			String uuid,JWTTokenDTO jwtTokenDTO) {
		SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
		try {
			RegistryObjectListType registryObjectListType = buildRegistryObjectList(oldMetadata,updateRequestDTO,uuid,jwtTokenDTO);
			submitObjectsRequest.setRegistryObjectList(registryObjectListType);
		} catch(Exception ex) {
			log.error("Error while perform build submit object request : {}" , ex.getMessage());
			throw new BusinessException("Error while perform build submit object request : ", ex);
		}
		return submitObjectsRequest;
	}

	/**
	 *
	 * @param oldMetadata
	 * @param updateRequestDTO
	 * @param uuid
	 * @param jwtTokenDTO
	 * @return
	 */
	private static RegistryObjectListType buildRegistryObjectList(RegistryObjectListType oldMetadata,
			MergedMetadatiRequestDTO updateRequestDTO,String uuid,JWTTokenDTO jwtTokenDTO) {
		
		String generatedUUID = Constants.IniClientConstants.URN_UUID + StringUtility.generateUUID();
		String requestUUID = Constants.IniClientConstants.URN_UUID + StringUtility.generateUUID();
		try {
			List<JAXBElement<? extends IdentifiableType>> list = new ArrayList<>(oldMetadata.getIdentifiable());
			ExtrinsicObjectType oldExtrinsicObject = (ExtrinsicObjectType) list.stream()
					.filter(e -> e.getValue() instanceof ExtrinsicObjectType)
					.findFirst().orElseThrow(() -> new MergeMetadatoNotFoundException("ExtrinsicObject non trovato nei metadati di INI"))
					.getValue();

			RegistryPackageType registryPackageObject = buildBasicRegistryPackageObject(generatedUUID);

			// 1. extrinsic object
			ExtrinsicObjectType editedExtrinsicObject = rebuildExtrinsicObjectMetadata(oldExtrinsicObject, updateRequestDTO.getBody(),requestUUID,
					uuid);
			JAXBElement<ExtrinsicObjectType> jaxbEditedExtrinsicObject = objectFactory.createExtrinsicObject(editedExtrinsicObject);

			// 2. registry package object
			RegistryPackageType editedRegistryPackageObject = rebuildRegistryPackageObjectMetadata(
					oldExtrinsicObject,
					registryPackageObject,
					updateRequestDTO.getBody(),
					jwtTokenDTO,
					generatedUUID
			);
			JAXBElement<RegistryPackageType> jaxbEditedRegistryPackageObject = objectFactory.createRegistryPackage(editedRegistryPackageObject);

			// 3. Classification object
			JAXBElement<ClassificationType> classificationObject = buildClassificationObjectJax(
					"urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd",
					null,
					generatedUUID,
					uuid,
					null, null, null, null
			);
			oldMetadata.getIdentifiable().add(classificationObject);

			// 4. Association object
			List<SlotType1> associationObject1Slots = new ArrayList<>();
			SlotType1 associationObj1SlotSubmissionSetStatus = buildSlotObject(
					"SubmissionSetStatus",
					null,
					Collections.singletonList("Original")
			);
			SlotType1 associationObj1SlotPreviousVersion = buildSlotObject("PreviousVersion",null,
					Collections.singletonList(oldExtrinsicObject.getVersionInfo().getVersionName())
			);
			associationObject1Slots.add(associationObj1SlotSubmissionSetStatus);
			associationObject1Slots.add(associationObj1SlotPreviousVersion);

			JAXBElement<AssociationType1> jaxbAssociationObject1 = buildAssociationObject(
					"urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember",
					requestUUID,generatedUUID,uuid,associationObject1Slots);

			JAXBElement<AssociationType1> jaxbAssociationObject2 = buildAssociationObject(
					"urn:ihe:iti:2007:AssociationType:RPLC",
					"SubmissionSet01_Association_1",uuid,uuid,null);

		 
			// 6. merge metadata
			oldMetadata.getIdentifiable().clear();
			oldMetadata.getIdentifiable().add(jaxbEditedExtrinsicObject);
			oldMetadata.getIdentifiable().add(jaxbEditedRegistryPackageObject);
			oldMetadata.getIdentifiable().add(jaxbAssociationObject1);
			oldMetadata.getIdentifiable().add(jaxbAssociationObject2);
			
			return oldMetadata;

		} catch(Exception ex) {
			log.error("Error while perform build registry object list : " , ex);
			throw new BusinessException("Error while perform build registry object list : " , ex);
		}
	}

	/**
	 * Merge old registry package object metadata with the ones received in the request
	 * @param oldExtrinsicObject
	 * @param registryPackageObject
	 * @param updateRequestBodyDTO
	 * @param jwtTokenDTO
	 * @param generatedUUID
	 * @return
	 */
	private static RegistryPackageType rebuildRegistryPackageObjectMetadata(
			ExtrinsicObjectType oldExtrinsicObject,
			RegistryPackageType registryPackageObject,
			PublicationMetadataReqDTO updateRequestBodyDTO,
			JWTTokenDTO jwtTokenDTO,
			String generatedUUID
	) {
		try {
			// Slots
			// 1. intendedRecipient
			List<String> slotValues = new ArrayList<>(Collections.singletonList(
					jwtTokenDTO.getPayload().getSubject_organization() +
							"^^^^^^^^^" +
							Constants.IniClientConstants.SOURCE_ID_OID +
							jwtTokenDTO.getPayload().getSubject_organization_id()));
			JAXBElement<SlotType1> slotObject = buildSlotObjectJax(
					
					"intendedRecipient",
					null,
					slotValues
			);
			registryPackageObject.getSlot().add(slotObject.getValue());

			// 2. Name + Description
			ClassificationType formatCodeClassification = oldExtrinsicObject.getClassification().stream()
					.filter(classificationType -> classificationType.getClassificationScheme().equals("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d"))
					.findFirst()
					.orElseThrow(() -> new MergeMetadatoNotFoundException("Format code non trovato nei metadati di INI"));
			String nodeRepresentation = formatCodeClassification.getNodeRepresentation();
			FormatCodeEnum formatCodeEnum = Arrays.stream(FormatCodeEnum.values())
					.filter(formatCode -> formatCode.getDocumentType().equals(nodeRepresentation))
					.findFirst()
					.orElse(null);
			InternationalStringType formatCodeName = formatCodeEnum != null ? buildInternationalStringType(
					new ArrayList<>(Collections.singleton(formatCodeEnum.getDocumentType()))) : null;
			registryPackageObject.setName(formatCodeName);
			registryPackageObject.setDescription(formatCodeName);

			// Classification Objects
			List<ClassificationType> classificationList = oldExtrinsicObject.getClassification();

			// 1. merge author metadata for the update request
			JAXBElement<ClassificationType> classificationObjectAuthor = mergeAuthorClassificationObject(objectFactory, classificationList, generatedUUID);
			registryPackageObject.getClassification().add(classificationObjectAuthor.getValue());

			// 2. merge contentTypeCode
			JAXBElement<ClassificationType> classificationObjectContentTypeCode = mergeContentTypeCodeClassificationObject(objectFactory, updateRequestBodyDTO, generatedUUID);
			registryPackageObject.getClassification().add(classificationObjectContentTypeCode.getValue());
			
			// 3. merge uniqueId
			JAXBElement<ExternalIdentifierType> externalIdentifierObjectUniqueId = buildExternalIdentifierObjectJax(
					"XDSSubmissionSet.uniqueId",
					"SubmissionSet01_UniqueId",
					"urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8",
					Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
					"registryObject",
					updateRequestBodyDTO.getIdentificativoSottomissione()
			);
			registryPackageObject.getExternalIdentifier().add(externalIdentifierObjectUniqueId.getValue());

			// 4. merge administrative-request
			// mergeAdministrativeRequest(updateRequestBodyDTO, classificationList);

//			// 1. merge sourceId
			JAXBElement<ExternalIdentifierType> externalIdentifierObjectSourceId = buildExternalIdentifierObjectJax(
					"XDSSubmissionSet.sourceId",
					"SubmissionSet01_SourceId",
					"urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
					Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN,
					generatedUUID,
					Constants.IniClientConstants.SOURCE_ID_OID + jwtTokenDTO.getPayload().getSubject_organization_id()
			);
			registryPackageObject.getExternalIdentifier().add(externalIdentifierObjectSourceId.getValue());
			
			
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
	 * @param uuid
	 * @param olduuid
	 * @return
	 */
	private static ExtrinsicObjectType rebuildExtrinsicObjectMetadata(ExtrinsicObjectType oldExtrinsicObject, PublicationMetadataReqDTO updateRequestBodyDTO,
			String uuid, String olduuid) {
		try {
			// 1. Classification Objects
			List<ClassificationType> classificationObjectList = new ArrayList<>(oldExtrinsicObject.getClassification());

			// 1.1 merge HealthcareFacilityTypeCode
			mergeHealthcareFacilityTypeCode(updateRequestBodyDTO, classificationObjectList);

			// 1.2 merge eventTypeCodeList -> suspended since attiCliniciRegoleAccesso not used in publication
			if (!CollectionUtils.isEmpty(updateRequestBodyDTO.getAttiCliniciRegoleAccesso())) {
				mergeEventTypeCode(updateRequestBodyDTO, classificationObjectList,uuid);
			}

			// 1.3 merge classCode
			mergeClassCode(updateRequestBodyDTO, classificationObjectList);

			// 1.4 merge practiceSettingCode
			mergePracticeSettingCode(updateRequestBodyDTO, classificationObjectList);

			// 1.5 add all classification objects
			oldExtrinsicObject.getClassification().clear();
			oldExtrinsicObject.getClassification().addAll(classificationObjectList);

			// 2. Slot objects
			List<SlotType1> slotList = new ArrayList<>(oldExtrinsicObject.getSlot());

			// 2.1 Reset slots in extrinsic object
			oldExtrinsicObject.getSlot().clear();

			// 2.2 merge service start / stop time
			mergeServiceStartStopTime(updateRequestBodyDTO, slotList);

			// 2.3 merge repository-type
			if (updateRequestBodyDTO.getConservazioneANorma()!=null) {
				MergeMetadataUtility.mergeRepositoryType(updateRequestBodyDTO, slotList);
			}

			mergeRefType(updateRequestBodyDTO, slotList, olduuid);
			
			// 2.4 add all slot objects
			oldExtrinsicObject.getSlot().addAll(slotList);

			return oldExtrinsicObject;
		} catch (Exception ex) {
			log.error("Error while perform merge extrinsic object metadata : {}" , ex.getMessage());
			throw new BusinessException("Error while perform merge extrinsic object metadata : ", ex);
		}
	}

	  /**
     * Merge repository-type metadata
     * @param updateRequestBodyDTO
     * @param slotList
     */
    public static void mergeRefType(PublicationMetadataReqDTO updateRequestBodyDTO, List<SlotType1> slotList,
    		String uuid) {
        try {
        	List<SlotType1> temp = new ArrayList<>(slotList);
        	for(SlotType1 slot : temp) {
        		if(slot.getName().contains("referenceIdList")) {
        			slotList.remove(slot);
        			break;
        		}
        	}

//        		SlotType1 repositoryTypeSlot = buildSlotObject(
//        				"urn:ihe:iti:xds:2013:referenceIdList",
//        				null,
//        				Collections.singletonList(uuid+"^^^&2.16.840.1.113883.2.9.2.190&ISO^urn:ihe:iti:2007:AssociationType:RPLC")
////                    Collections.singletonList(slotList.get(5).getValueList().getValue().get(0))
//        				);
//        		slotList.add(repositoryTypeSlot);
        } catch (Exception ex) {
            log.error("Error while perform merge repository type : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge repository type : ", ex);
        }
    }


	/**
	 *
	 * @param generatedUUID
	 * @return
	 */
	private static RegistryPackageType buildBasicRegistryPackageObject(String generatedUUID) {
		RegistryPackageType registryPackageObject = new RegistryPackageType();
		try {
			registryPackageObject.setId(generatedUUID);
			registryPackageObject.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage");
			registryPackageObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
			registryPackageObject.setName(null);
			registryPackageObject.setDescription(null);
			String submissionSetTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			List<String> slotValues = new ArrayList<>(Collections.singletonList(submissionSetTime));
			JAXBElement<SlotType1> slotObject = buildSlotObjectJax(
					
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
