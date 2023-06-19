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
package it.finanze.sanita.fse2.ms.iniclient.utility;

import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.List;

@Slf4j
public class RequestUtility {

    private RequestUtility() {}
    /**
     * Extract metadata from entity
     * @param metadata
     * @return
     */
    public static DocumentTreeDTO extractDocumentsFromMetadata(List<Document> metadata) {
    	DocumentTreeDTO documentTreeDTO = new DocumentTreeDTO();
    	for (Document meta : metadata) {
    		if (meta.get("documentEntry") != null) {
    			documentTreeDTO.setDocumentEntry((Document) meta.get("documentEntry"));
    		}

    		if (meta.get("tokenEntry") != null) {
    			documentTreeDTO.setTokenEntry((Document) meta.get("tokenEntry"));
    		}

    		if (meta.get("submissionSetEntry") != null) {
    			documentTreeDTO.setSubmissionSetEntry((Document) meta.get("submissionSetEntry"));
    		}
    	}

    	return documentTreeDTO;
    }

    public static JWTTokenDTO configureReadTokenPerAction(JWTTokenDTO jwtTokenDTO, ActionEnumType actionType) {
        log.debug("Reconfiguring token per action");
        JWTTokenDTO reconfiguredToken = new JWTTokenDTO();
        JWTPayloadDTO jwtPayloadDTO = jwtTokenDTO.getPayload();
        jwtPayloadDTO.setAction_id(actionType.getActionId());
        jwtPayloadDTO.setPurpose_of_use(actionType.getPurposeOfUse());
        reconfiguredToken.setPayload(jwtPayloadDTO);
        return jwtTokenDTO;
    }

    public static JWTPayloadDTO buildPayloadFromReq(final GetMetadatiReqDTO req) {
        return JWTPayloadDTO.builder().
                action_id(req.getAction_id()).
                iss(req.getIss()).
                locality(req.getLocality()).
                person_id(req.getPerson_id()).
                purpose_of_use(req.getPurpose_of_use()).
                resource_hl7_type(req.getResource_hl7_type()).
                sub(req.getSub()).
                subject_organization(req.getSubject_organization()).
                subject_organization_id(req.getSubject_organization_id()).
                subject_role(req.getSubject_role()).
                patient_consent(req.isPatient_consent()).
                build();
    }

    public static JWTPayloadDTO buildPayloadFromReq(final GetReferenceReqDTO req) {
        return JWTPayloadDTO.builder().
            action_id(req.getAction_id()).
            iss(req.getIss()).
            locality(req.getLocality()).
            person_id(req.getPerson_id()).
            purpose_of_use(req.getPurpose_of_use()).
            resource_hl7_type(req.getResource_hl7_type()).
            sub(req.getSub()).
            subject_organization(req.getSubject_organization()).
            subject_organization_id(req.getSubject_organization_id()).
            subject_role(req.getSubject_role()).
            patient_consent(req.isPatient_consent()).
            subject_application_id(req.getSubject_application_id()).
            subject_application_vendor(req.getSubject_application_vendor()).
            subject_application_version(req.getSubject_application_version()).
            build();
    }
}
