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

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CLASSIFICATION_ID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.DOCUMENT_ENTRY_ID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.SUBMISSION_ENTRY_ID;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildAssociationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeAdministrativeRequest;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeClassCode;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeDescription;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeEventTypeCode;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeHealthcareFacilityTypeCode;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergePracticeSettingCode;
import static it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility.mergeServiceTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ExternalIdentifierEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.MergeMetadatoNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.SubmissionSetEntryBuilderUtility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.VersionInfoType;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateBodyBuilderUtility {

	/**
	 *
	 * @param updateRequestDTO
	 * @param oldMetadata
	 * @param uuid
	 * @param jwtTokenDTO
	 * @return
	 */
	public static SubmitObjectsRequest buildSubmitObjectRequest(RegistryObjectListType oldMetadata,MergedMetadatiRequestDTO newMetadataDTO,
			String uuid,JWTTokenDTO jwtTokenDTO, String idDocumento) {
		SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
		RegistryObjectListType registryObjectListType = buildRegistryObjectList(oldMetadata,newMetadataDTO,uuid,jwtTokenDTO,idDocumento);
		submitObjectsRequest.setRegistryObjectList(registryObjectListType);
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
	private static RegistryObjectListType buildRegistryObjectList(RegistryObjectListType oldMetadata, MergedMetadatiRequestDTO updateRequestDTO,String uuid,JWTTokenDTO jwtTokenDTO,String idDocumento) {
		
		RegistryObjectListType out = new RegistryObjectListType();
		
		String requestUUID = Constants.IniClientConstants.URN_UUID + StringUtility.generateUUID();
		List<JAXBElement<? extends IdentifiableType>> list = new ArrayList<>(oldMetadata.getIdentifiable());

		PublicationMetadataReqDTO updateReq = updateRequestDTO.getBody();
		
		// 1. extrinsic object
		ExtrinsicObjectType extrinsicObject = mergeExtrinsicObjectMetadata(list, updateReq,requestUUID);
		extrinsicObject.setLid(uuid);
		
        Optional<ClassificationType> classificationAuthor = findClassificationById(extrinsicObject, "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d");
		ClassificationType classificationAuthorType = null;
		if(classificationAuthor.isPresent()){
			ClassificationType extrinsicObjAuthor = classificationAuthor.get();
			classificationAuthorType = buildClassificationObjectJax(extrinsicObjAuthor.getClassificationNode(), "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d",
			extrinsicObjAuthor.getClassifiedObject(), extrinsicObjAuthor.getId(), extrinsicObjAuthor.getName(), extrinsicObjAuthor.getSlot(), extrinsicObjAuthor.getObjectType(), extrinsicObjAuthor.getNodeRepresentation()).getValue();
		}

		// 2. registry package
		JAXBElement<RegistryPackageType> registryPackage = SubmissionSetEntryBuilderUtility.buildRegistryPackageObjectSubmissionSet(updateReq, jwtTokenDTO.getPayload(), requestUUID,classificationAuthorType);
		list.add(registryPackage);
		
		// 3. Association
		list.add(buildAssociation(extrinsicObject.getVersionInfo(), requestUUID));
		
		// 4. Classification object
		String classificationObjectType = "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification";
		JAXBElement<ClassificationType> classificationObject = buildClassificationObjectJax("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd",null,SUBMISSION_ENTRY_ID,CLASSIFICATION_ID,null, null, classificationObjectType, null);
		list.add(classificationObject);
		
		out.getIdentifiable().addAll(list);
		return out;
	}
	
	public static Optional<ClassificationType> findClassificationById(ExtrinsicObjectType extrinsicObject, String classificationScheme) {
        return extrinsicObject.getClassification().stream().filter(c -> c.getClassificationScheme().equals(classificationScheme)).findFirst();
    }

	private static JAXBElement<AssociationType1> buildAssociation(VersionInfoType versionInfoType, String requestUUID) {
		List<SlotType1> associationObject1Slots = new ArrayList<>();
		SlotType1 associationObj1SlotSubmissionSetStatus = buildSlotObject("SubmissionSetStatus",null,Collections.singletonList("Original"));
		associationObject1Slots.add(associationObj1SlotSubmissionSetStatus);
		if(versionInfoType!=null) {
			SlotType1 associationObj1SlotPreviousVersion = buildSlotObject("PreviousVersion",null,Collections.singletonList(versionInfoType.getVersionName()));
			associationObject1Slots.add(associationObj1SlotPreviousVersion);
		}

		return buildAssociationObject("urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember",requestUUID,SUBMISSION_ENTRY_ID, DOCUMENT_ENTRY_ID,associationObject1Slots);
	}
	 
 
	/**
	 * Rebuild old extrinsic object metadata with the ones received in the request
	 * @param oldExtrinsicObject
	 * @param updateRequestBodyDTO
	 * @param uuid
	 * @return
	 */
	private static ExtrinsicObjectType mergeExtrinsicObjectMetadata(List<JAXBElement<? extends IdentifiableType>> list, PublicationMetadataReqDTO updateRequestBodyDTO, String uuid) {

		ExtrinsicObjectType extrinsicObject = (ExtrinsicObjectType) list.stream()
				.filter(e -> e.getValue() instanceof ExtrinsicObjectType)
				.findFirst().orElseThrow(() -> new MergeMetadatoNotFoundException("ExtrinsicObject non trovato nei metadati di INI"))
				.getValue();

		// 1 Merge classification extrinsic object
		mergeHealthcareFacilityTypeCode(updateRequestBodyDTO, extrinsicObject);
		mergeClassCode(updateRequestBodyDTO, extrinsicObject);
		mergePracticeSettingCode(updateRequestBodyDTO, extrinsicObject);
		mergeEventTypeCode(updateRequestBodyDTO, extrinsicObject);

		// 2 Merge slot extrinsic object
		mergeServiceTime(updateRequestBodyDTO, extrinsicObject);
		mergeDescription(updateRequestBodyDTO, extrinsicObject);
		mergeAdministrativeRequest(updateRequestBodyDTO, extrinsicObject);
		
		extrinsicObject.setId(DOCUMENT_ENTRY_ID);
		for(ClassificationType classification : extrinsicObject.getClassification()) {
			classification.setClassifiedObject(DOCUMENT_ENTRY_ID);
			for(ClassificationEnum val : ClassificationEnum.values()) {
				if(classification.getClassificationScheme().equals(val.getClassificationScheme())){
					classification.setId(val.getId());
					break;
				}	
			}
 		}
		
		for(ExternalIdentifierType external : extrinsicObject.getExternalIdentifier()) {
			external.setRegistryObject(DOCUMENT_ENTRY_ID);

			for(ExternalIdentifierEnum externalIdentifierEnum : ExternalIdentifierEnum.values()) {
				if(external.getIdentificationScheme().equals(externalIdentifierEnum.getClassificationScheme())) {
					external.setId(externalIdentifierEnum.getId());		
				}
			}
		}
		
		return extrinsicObject;
	}
  
}
