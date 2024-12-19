
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
package it.finanze.sanita.fse2.ms.iniclient.utility.create;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.URN_UUID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.DOCUMENT_ENTRY_ID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.SUBMISSION_ENTRY_ID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CLASSIFICATION_ID;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;

import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PublishReplaceBodyBuilderUtility {

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
		if (documentEntryDTO == null) throw new BusinessException("DocumentEntryDTO is null");
		SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
		RegistryObjectListType registryObjectListType = buildRegistryObjectList(documentEntryDTO, submissionSetEntryDTO, jwtPayloadDTO, uuid);
		submitObjectsRequest.setRegistryObjectList(registryObjectListType);
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
		 
		
		//ExtrinsicObject - DocumentEntry
		JAXBElement<ExtrinsicObjectType> extrinsicObject = DocumentEntryBuilderUtility.buildExtrinsicObjectDocumentEntry(DOCUMENT_ENTRY_ID, documentEntryDTO,jwtPayloadDTO);
		registryObjectListType.getIdentifiable().add(extrinsicObject);

		//Registry package - SubmissionSetEntry
		JAXBElement<RegistryPackageType> registryPackageObject = SubmissionSetEntryBuilderUtility.buildRegistryPackageObjectSubmissionSet(submissionSetEntryDTO,jwtPayloadDTO, SUBMISSION_ENTRY_ID);
		registryObjectListType.getIdentifiable().add(registryPackageObject);

		String reference = "Original";
		List<SlotType1> associationObjectSlots = new ArrayList<>();
		SlotType1 associationObjSlot = buildSlotObject("SubmissionSetStatus", null,Collections.singletonList(reference));
		associationObjectSlots.add(associationObjSlot);
		JAXBElement<AssociationType1> associationObject = SamlBodyBuilderCommonUtility.buildAssociationObject("urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember",URN_UUID + StringUtility.generateUUID(), SUBMISSION_ENTRY_ID,DOCUMENT_ENTRY_ID,associationObjectSlots);
		registryObjectListType.getIdentifiable().add(associationObject);
		
		JAXBElement<AssociationType1> associationObjectRep = null;
		if(!StringUtility.isNullOrEmpty(uuid)) {
			//Replace
			List<SlotType1> associationObjectSlotsRep = new ArrayList<>();
			associationObjectRep = SamlBodyBuilderCommonUtility.buildAssociationObject(
					"urn:ihe:iti:2007:AssociationType:RPLC","SubmissionSet1_Association_1", DOCUMENT_ENTRY_ID,uuid,associationObjectSlotsRep);
			registryObjectListType.getIdentifiable().add(associationObjectRep);
		}

		JAXBElement<ClassificationType> c = buildClassificationObject();
		registryObjectListType.getIdentifiable().add(c);
		
		return registryObjectListType;
	}

    
	public static JAXBElement<ClassificationType> buildClassificationObject() {
		ClassificationType classificationObject = new ClassificationType();
		classificationObject.setClassificationNode("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd");
		classificationObject.setClassifiedObject(SUBMISSION_ENTRY_ID);
		classificationObject.setId(CLASSIFICATION_ID);
		classificationObject.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification");
		
		return objectFactory.createClassification(classificationObject);
	}
	
  
}
