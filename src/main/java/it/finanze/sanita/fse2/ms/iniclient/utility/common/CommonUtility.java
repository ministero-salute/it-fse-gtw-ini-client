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
package it.finanze.sanita.fse2.ms.iniclient.utility.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.bson.Document;
import org.springframework.util.CollectionUtils;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentTreeDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.DocumentTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@Slf4j
public class CommonUtility {

    private CommonUtility() {}

    /**
     *
     * @param documentEntry
     * @return
     */
    public static DocumentEntryDTO extractDocumentEntry(Document documentEntry) {
        return JsonUtility.clone(documentEntry, DocumentEntryDTO.class);
    }

    /**
     *
     * @param submissionSetEntry
     * @return
     */
    public static SubmissionSetEntryDTO extractSubmissionSetEntry(Document submissionSetEntry) {
        return JsonUtility.clone(submissionSetEntry, SubmissionSetEntryDTO.class);
    }

    /**
     * Build JWT payload for delete request
     * @param deleteRequestDTO
     * @return
     */
    public static JWTPayloadDTO buildJwtPayloadFromDeleteRequest(DeleteRequestDTO deleteRequestDTO) {
		log.debug("Build payload information");
		return JWTPayloadDTO.builder()
				.attachment_hash(null)
				.aud(null)
				.exp(0)
				.iat(0)
				.jti(null)
				.action_id(deleteRequestDTO.getAction_id())
				.patient_consent(deleteRequestDTO.getPatient_consent())
				.iss(deleteRequestDTO.getIss())
				.locality(deleteRequestDTO.getLocality())
				.person_id(deleteRequestDTO.getPerson_id())
				.purpose_of_use(deleteRequestDTO.getPurpose_of_use())
				.resource_hl7_type(deleteRequestDTO.getResource_hl7_type())
				.sub(deleteRequestDTO.getSub())
				.subject_organization(deleteRequestDTO.getSubject_organization())
				.subject_organization_id(deleteRequestDTO.getSubject_organization_id())
				.subject_role(deleteRequestDTO.getSubject_role())
				.subject_application_id(deleteRequestDTO.getSubject_application_id())
				.subject_application_vendor(deleteRequestDTO.getSubject_application_vendor())
				.subject_application_version(deleteRequestDTO.getSubject_application_version())
				.build();
	}
 
  
    /**
     * Extract subject Role from token
     * 
     * @param documentTreeDTO
     * @return subject fiscal code of JWT
     */
    public static String extractFiscalCodeFromDocumentTree(DocumentTreeDTO documentTreeDTO) {
        String subjectFiscalCode = Constants.IniClientConstants.JWT_MISSING_SUBJECT;
        if (documentTreeDTO != null) {
            Document payload = (Document) documentTreeDTO.getTokenEntry().get("payload");
            if (payload != null) {
                subjectFiscalCode = extractFiscalCodeFromJwtSub(payload.getString("sub"));
            }
        }

        return subjectFiscalCode;
    }

    public static String extractFiscalCodeFromJwtSub(final String sub) {
		String subjectFiscalCode = Constants.IniClientConstants.JWT_MISSING_SUBJECT;
        try {
            final String [] chunks = sub.split("&");
    
            // Checking if the system is MEF, in that case the fiscal code is the first element of the array
            if (chunks.length > 1 && Constants.OIDS.OID_MEF.equals(chunks[1])) {
                subjectFiscalCode = chunks[0].split("\\^\\^\\^")[0];
            }
        } catch (Exception e) {
            log.warn("Error extracting fiscal code from JWT sub: {}", e.getMessage());
            subjectFiscalCode = Constants.IniClientConstants.JWT_MISSING_SUBJECT;
        }
		return subjectFiscalCode;
	}

    /**
     * Extract document type from db entity
     * @param documentTreeDTO
     * @return
     */
    public static String extractDocumentType(DocumentTreeDTO documentTreeDTO) {
    	String documentType = Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER;

    	if (documentTreeDTO != null) {
    		Optional<Document> documentEntry = Optional.of(documentTreeDTO.getDocumentEntry());
    		if (documentEntry.get().getString("typeCode") != null) {
    			DocumentTypeEnum normalizedDocumentType = DocumentTypeEnum.getByCode(documentEntry.get().getString("typeCode"));
    			if (normalizedDocumentType != null) {
    				documentType = normalizedDocumentType.getDocumentType();
    			} else {
    				documentType = documentEntry.get().getString("typeCodeName");
    			}
    		}
    	}

    	return documentType;
    }

    /**
     * Check metadata existence
     * @param queryResponse
     * @return
     */
    public static boolean checkMetadata(AdhocQueryResponse queryResponse) {
        return queryResponse != null && queryResponse.getRegistryObjectList() != null && !CollectionUtils.isEmpty(queryResponse.getRegistryObjectList().getIdentifiable());
    }

    /**
     * Extract document type from query response
     * @param queryResponse
     * @return
     */
    public static String extractDocumentTypeFromQueryResponse(AdhocQueryResponse queryResponse) {
        if (checkMetadata(queryResponse)) {
            List<JAXBElement<? extends IdentifiableType>> identifiableList = new ArrayList<>(queryResponse.getRegistryObjectList().getIdentifiable());
            Optional<JAXBElement<? extends IdentifiableType>> optExtrinsicObject = identifiableList.stream()
                    .filter(e -> e.getValue() instanceof ExtrinsicObjectType)
                    .findFirst();
            if (optExtrinsicObject.isPresent()) {
                ExtrinsicObjectType extrinsicObject = (ExtrinsicObjectType) optExtrinsicObject.get().getValue();
                List<ClassificationType> classificationObjectList = extrinsicObject.getClassification();
                Optional<ClassificationType> optTypeCodeClassificationObject = classificationObjectList
                        .stream()
                        .filter(classificationType -> classificationType.getClassificationScheme().equals("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983"))
                        .findFirst();
                if (optTypeCodeClassificationObject.isPresent()) {
                    Optional<LocalizedStringType> typeCodeName = optTypeCodeClassificationObject.get()
                            .getName().getLocalizedString().stream().findFirst();
                    return typeCodeName.isPresent()? typeCodeName.get().getValue() : Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER;
                }
            }
        }
        return Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER;
    }
    
    // TODO - Verificare correttezza estrazione authorInstitution
    
    /**
     * Extract author institution from query response
     * @param queryResponse
     * @return
     */
    public static String extractAuthorInstitutionFromQueryResponse(AdhocQueryResponse queryResponse) {
        if (checkMetadata(queryResponse)) {
            List<JAXBElement<? extends IdentifiableType>> identifiableList = new ArrayList<>(queryResponse.getRegistryObjectList().getIdentifiable());
            Optional<JAXBElement<? extends IdentifiableType>> optExtrinsicObject = identifiableList.stream()
                    .filter(e -> e.getValue() instanceof ExtrinsicObjectType)
                    .findFirst();
            if (optExtrinsicObject.isPresent()) {
                ExtrinsicObjectType extrinsicObject = (ExtrinsicObjectType) optExtrinsicObject.get().getValue();
                List<ClassificationType> classificationObjectList = extrinsicObject.getClassification();
                Optional<ClassificationType> optAuthorClassificationObject = classificationObjectList.stream()
                        .filter(classificationType -> classificationType.getClassificationScheme().equals("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d"))
                        .findFirst();
                if(optAuthorClassificationObject.isPresent()) {
                	List<SlotType1> authorSlots = optAuthorClassificationObject.get().getSlot();
                    Optional<SlotType1> authorInstitutionSlot = authorSlots.stream()
                            .filter(slot -> slot.getName().equals("authorInstitution"))
                            .findFirst();
                    if(authorInstitutionSlot.isPresent()) {
                    	return authorInstitutionSlot.get().getValueList().getValue().get(0);
                    }
                }
            }
        }
        return Constants.IniClientConstants.MISSING_AUTHOR_INSTITUTION_PLACEHOLDER;
    }
    
    // TODO - Verificare correttezza slot e classificationScheme per l'estrazione dell'administrativeRequest
    // da AffinityDomain sembra non faccia parte di ClassificationScheme, quindi potrebbe essere uno slot esterno generico
    /**
     * Extract administrative request from query response
     * @param queryResponse
     * @return
     */
    public static String extractAdministrativeRequestFromQueryResponse(AdhocQueryResponse queryResponse) {
        if (checkMetadata(queryResponse)) {
            List<JAXBElement<? extends IdentifiableType>> identifiableList = new ArrayList<>(queryResponse.getRegistryObjectList().getIdentifiable());
            Optional<JAXBElement<? extends IdentifiableType>> optExtrinsicObject = identifiableList.stream()
                    .filter(e -> e.getValue() instanceof ExtrinsicObjectType)
                    .findFirst();
            if (optExtrinsicObject.isPresent()) {
                ExtrinsicObjectType extrinsicObject = (ExtrinsicObjectType) optExtrinsicObject.get().getValue();
                Optional<SlotType1> administrativeRequestSlot = extrinsicObject.getSlot().stream()
                		.filter(slot -> slot.getName().contains("administrativeRequest"))
                		.findFirst();
                if(administrativeRequestSlot.isPresent()) {
                	return administrativeRequestSlot.get().getValueList().getValue().get(0);
                }
            }
        }
        return Constants.IniClientConstants.MISSING_ADMINISTRATIVE_REQUEST_PLACEHOLDER;
    }
}
