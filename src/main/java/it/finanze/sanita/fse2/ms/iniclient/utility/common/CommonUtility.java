package it.finanze.sanita.fse2.ms.iniclient.utility.common;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
				.build();
	}

    /**
     * Extract issuer from token
     * @param documentTreeDTO
     * @return
     */
    public static String extractIssuer(DocumentTreeDTO documentTreeDTO) {
        if (documentTreeDTO == null) {
            return Constants.IniClientConstants.JWT_MISSING_ISSUER_PLACEHOLDER;
        }
        Document payload = (Document) Optional.of(documentTreeDTO.getTokenEntry().get("payload")).orElse(null);
        return payload.containsKey("iss") ? (String) payload.get("iss") : Constants.IniClientConstants.JWT_MISSING_ISSUER_PLACEHOLDER;
    }

    /**
     * Extract document type from db entity
     * @param documentTreeDTO
     * @return
     */
    public static String extractDocumentType(DocumentTreeDTO documentTreeDTO) {
        if (documentTreeDTO == null) {
            return Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER;
        }
        Document documentEntry = Optional.of(documentTreeDTO.getDocumentEntry()).orElse(null);
        return documentEntry.containsKey("typeCodeName") ? (String) documentEntry.get("typeCodeName") : Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER;
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
}
