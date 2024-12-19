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

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CODING_SCHEME;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.SUBMISSION_ENTRY_ID;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildExternalIdentifierObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildInternationalStringType;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObjectJax;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.AuthorSlotDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmissionSetEntryBuilderUtility {

	@Getter
	@Setter
	private static ObjectFactory objectFactory = new ObjectFactory();
	
	public static JAXBElement<RegistryPackageType> buildRegistryPackageObjectSubmissionSet(PublicationMetadataReqDTO updateRequestDto,JWTPayloadDTO jwtPayloadDTO, String id,
	ClassificationType classificationAuthorType) {

		String sourceId = Constants.IniClientConstants.SOURCE_ID_PREFIX + StringUtility.sanitizeSourceId(jwtPayloadDTO.getSubject_organization_id());
		JAXBElement<RegistryPackageType> registryPackage = buildRegistryPackageObjectSubmissionSet("",sourceId, updateRequestDto.getIdentificativoSottomissione(), jwtPayloadDTO.getPerson_id(),
				updateRequestDto.getTipoAttivitaClinica().getDescription(),updateRequestDto.getTipoAttivitaClinica().getCode(),
				"", "" , "",classificationAuthorType);
		
		String submissionSetTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		List<String> slotValues = new ArrayList<>(Collections.singletonList(submissionSetTime));
		JAXBElement<SlotType1> slotObject = buildSlotObjectJax(
				"submissionTime",
				null,
				slotValues
		);
		registryPackage.getValue().getSlot().add(slotObject.getValue());
		return registryPackage;
	}
	
	public static JAXBElement<RegistryPackageType> buildRegistryPackageObjectSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO,JWTPayloadDTO jwtPayloadDTO, String id) {
		return buildRegistryPackageObjectSubmissionSet(submissionSetEntryDTO.getSubmissionTime(),submissionSetEntryDTO.getSourceId(), submissionSetEntryDTO.getUniqueID(), submissionSetEntryDTO.getPatientId(),
				submissionSetEntryDTO.getContentTypeCodeName(),submissionSetEntryDTO.getContentTypeCode(),
				submissionSetEntryDTO.getAuthorRole(), submissionSetEntryDTO.getAuthorInstitution() , submissionSetEntryDTO.getAuthor(),null);
	}

	private static JAXBElement<RegistryPackageType> buildRegistryPackageObjectSubmissionSet(final String submissionTime,final String sourceId, final String uniqueId, final String patientId,
			String contentTypeCodeName,String contentTypeCode,
			String authorRole, String authorInstitution, String author,ClassificationType classificationAuthorType) {

		RegistryPackageType registryPackageObject = new RegistryPackageType();

		String id = SUBMISSION_ENTRY_ID;
		registryPackageObject.setId(id);
		registryPackageObject.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage");
		registryPackageObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
		registryPackageObject.setName(null);
		registryPackageObject.setDescription(null);
		registryPackageObject.getSlot().addAll(buildSlotSubmissionSet(submissionTime));
		registryPackageObject.getClassification().addAll(buildClassificationSubmissionSet(contentTypeCodeName,contentTypeCode,authorRole,authorInstitution,author,id,classificationAuthorType));
		registryPackageObject.getExternalIdentifier().addAll(buildExternalIdentifierSubmissionSet(sourceId, uniqueId,patientId, id));
		return objectFactory.createRegistryPackage(registryPackageObject);
	}
	
	private static List<SlotType1> buildSlotSubmissionSet(String submissionTime){
		List<SlotType1> out = new ArrayList<>();
		if(!StringUtility.isNullOrEmpty(submissionTime)) {
			out.add(buildSlotObjectJax("submissionTime",null,Arrays.asList(submissionTime)).getValue());	
		}
		
		return out;
	}
	
	private static List<ExternalIdentifierType> buildExternalIdentifierSubmissionSet(final String sourceId, final String uniqueId, final String patientId, final String id) {
		List<ExternalIdentifierType> out = new ArrayList<>();

		// Build external identifiers
		if(!StringUtility.isNullOrEmpty(sourceId)) {
			JAXBElement<ExternalIdentifierType> externalIdentifierObject1 = buildExternalIdentifierObjectJax(
					"XDSSubmissionSet.sourceId","SubmissionSet1_SourceId",
					"urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
					EXTERNAL_IDENTIFIER_URN, id, sourceId);
			out.add(externalIdentifierObject1.getValue());	
		}

		if(!StringUtility.isNullOrEmpty(uniqueId)) {
			JAXBElement<ExternalIdentifierType> externalIdentifierObject2 = buildExternalIdentifierObjectJax(
					"XDSSubmissionSet.uniqueId", "SubmissionSet1_UniqueId",
					"urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8",
					EXTERNAL_IDENTIFIER_URN, id, uniqueId);
			out.add(externalIdentifierObject2.getValue());	
		}

		if(!StringUtility.isNullOrEmpty(patientId)) {
			JAXBElement<ExternalIdentifierType> externalPatientIdentifierObject = buildExternalIdentifierObjectJax(
					"XDSSubmissionSet.patientId","SubmissionSet1_PatientId","urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446",
					EXTERNAL_IDENTIFIER_URN, id, patientId);
			out.add(externalPatientIdentifierObject.getValue());	
		}

		return out;
	}
	
	private static List<ClassificationType> buildClassificationSubmissionSet(String contentTypeCodeName,String contentTypeCode,
			String authorRole, String authorInstitution, String author,String id,ClassificationType classificationAuthorType) {
		
		List<ClassificationType> out = new ArrayList<>();
		if(!StringUtility.isNullOrEmpty(contentTypeCodeName)) {
			//Content Type
			InternationalStringType nameContentTypeCode = buildInternationalStringType(Collections.singletonList(contentTypeCodeName));
			SlotType1 nameContentTypeSlot = buildSlotObject(CODING_SCHEME,"2.16.840.1.113883.2.9.3.3.6.1.4");

			JAXBElement<ClassificationType> contentTypeCodeClassification = buildClassificationObjectJax(
				null,"urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",id, "IdContentTypeCode",nameContentTypeCode,Arrays.asList(nameContentTypeSlot),
				CLASSIFICATION_OBJECT_URN,contentTypeCode
			);
			out.add(contentTypeCodeClassification.getValue());
	
		}

		//Author
		if(classificationAuthorType!=null){
			classificationAuthorType.setClassifiedObject(SUBMISSION_ENTRY_ID);			
			classificationAuthorType.setId("SubmissionSet1_ClassificationAuthor");
			out.add(classificationAuthorType);
		} else if(!StringUtility.isNullOrEmpty(author)) {
		 	AuthorSlotDTO authorSlotDto = CommonUtility.buildAuthorSlot(authorRole, authorInstitution, author);
		 	JAXBElement<ClassificationType> authorClassification = buildClassificationObjectJax(null,"urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d",id,
		 			"SubmissionSet1_ClassificationAuthor",
		 			null,
		 			Arrays.asList(authorSlotDto.getAuthorRoleSlot(), authorSlotDto.getAuthorInstitutionSlot(), authorSlotDto.getAuthorPersonSlot()),
		 			"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
		 			"");

		 	out.add(authorClassification.getValue());	
		}
		
		return out;
	}
}
