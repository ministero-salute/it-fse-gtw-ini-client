/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.utility.update;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventCodeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.*;

@Slf4j
public class MergeMetadataUtility {
    private MergeMetadataUtility() {}

    /**
     * Merge eventTypeCode for extrinsic object
     * @param updateRequestBodyDTO
     * @param classificationObjectList
     */
    public static void mergeEventTypeCode(PublicationMetadataReqDTO updateRequestBodyDTO, List<ClassificationType> classificationObjectList,
    		String uuid) {
        // Slots 8-N
        for (String eventCode : updateRequestBodyDTO.getAttiCliniciRegoleAccesso()) {
            SlotType1 classificationObjNSlot1 = buildSlotObject(Constants.IniClientConstants.CODING_SCHEME, null, Collections.singletonList("2.16.840.1.113883.2.9.3.3.6.1.3"));
            List<SlotType1> classificationObjNSlots = new ArrayList<>();
            classificationObjNSlots.add(classificationObjNSlot1);
            InternationalStringType nameN = buildInternationalStringType(Collections.singletonList(EventCodeEnum.fromValue(eventCode).getDescription()));
            ClassificationType classificationObjectN = buildClassificationObject(null,uuid, "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4",
                     "IdEventCodeList", nameN, classificationObjNSlots, Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN, eventCode);
            classificationObjectList.add(classificationObjectN);
        }
    }

    /**
     * Merge practiceSettingCode for extrinsic object
     * @param updateRequestBodyDTO
     * @param classificationObjectList
     */
    public static void mergePracticeSettingCode(PublicationMetadataReqDTO updateRequestBodyDTO, List<ClassificationType> classificationObjectList) {
        try {
            ClassificationType practiceSettingCodeClassificationObject = classificationObjectList
                    .stream()
                    .filter(classificationType -> classificationType.getClassificationScheme().equals("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("PracticeSettingCodeEnum non trovato nei metadati di INI"));
            classificationObjectList.remove(practiceSettingCodeClassificationObject);
            practiceSettingCodeClassificationObject.setNodeRepresentation(updateRequestBodyDTO.getAssettoOrganizzativo().name());
            InternationalStringType practiceSettingCodeName = buildInternationalStringType(
                    new ArrayList<>(Collections.singleton(updateRequestBodyDTO.getAssettoOrganizzativo().getDescription())));
            practiceSettingCodeClassificationObject.setName(practiceSettingCodeName);
            classificationObjectList.add(practiceSettingCodeClassificationObject);
        } catch (Exception ex) {
            log.error("Error while perform merge practice setting code : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge practice setting code : ", ex);
        }
    }

    /**
     * Merge classCode for extrinsic object
     * @param updateRequestBodyDTO
     * @param classificationObjectList
     */
    public static void mergeClassCode(PublicationMetadataReqDTO updateRequestBodyDTO, List<ClassificationType> classificationObjectList) {
        try {
            ClassificationType classCodeClassificationObject = classificationObjectList
                    .stream()
                    .filter(classificationType -> classificationType.getClassificationScheme().equals("urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("TipoDocAltoLivEnum non trovato nei metadati di INI"));
            classificationObjectList.remove(classCodeClassificationObject);
            classCodeClassificationObject.setNodeRepresentation(updateRequestBodyDTO.getTipoDocumentoLivAlto().getCode());
            InternationalStringType classCodeName = buildInternationalStringType(
                    new ArrayList<>(Collections.singleton(updateRequestBodyDTO.getTipoDocumentoLivAlto().getDescription())));
            classCodeClassificationObject.setName(classCodeName);
            classificationObjectList.add(classCodeClassificationObject);
        } catch (Exception ex) {
            log.error("Error while perform merge class code : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge class code : ", ex);
        }
    }

    /**
     * Merge healthcareFacilityTypeCode for extrinsic object
     * @param updateRequestBodyDTO
     * @param classificationObjectList
     */
    public static void mergeHealthcareFacilityTypeCode(PublicationMetadataReqDTO updateRequestBodyDTO, List<ClassificationType> classificationObjectList) {
        try {
            ClassificationType healthCareFacilityClassificationObject = classificationObjectList
                    .stream()
                    .filter(classificationType -> classificationType.getClassificationScheme().equals("urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("HealthcareFacilityEnum non trovata nei metadati di INI"));
            classificationObjectList.remove(healthCareFacilityClassificationObject);
            healthCareFacilityClassificationObject.setNodeRepresentation(updateRequestBodyDTO.getTipologiaStruttura().getCode());
            InternationalStringType healthcareFacilityTypeCodeName = buildInternationalStringType(
                    new ArrayList<>(Collections.singleton(updateRequestBodyDTO.getTipologiaStruttura().getCode())));
            healthCareFacilityClassificationObject.setName(healthcareFacilityTypeCodeName);
            classificationObjectList.add(healthCareFacilityClassificationObject);
        } catch (Exception ex) {
            log.error("Error while perform merge healthcare facility type code : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge healthcare facility type code : ", ex);
        }
    }

    /**
     * Merge service start/stop time for extrinsic object
     * @param updateRequestBodyDTO
     * @param slotList
     */
    public static void mergeServiceStartStopTime(PublicationMetadataReqDTO updateRequestBodyDTO, List<SlotType1> slotList) {
        try {
            // start time
            SlotType1 editedStartTime = slotList.stream()
                    .filter(slot -> slot.getName().equals("serviceStartTime"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("serviceStartTime non trovato nei metadati di INI"));
            slotList.remove(editedStartTime);
            List<String> valuesSlotEditedStartTime = new ArrayList<>();
            valuesSlotEditedStartTime.add(updateRequestBodyDTO.getDataInizioPrestazione());
            ValueListType valueListStartTime = new ValueListType();
            for (String value : valuesSlotEditedStartTime) {
                valueListStartTime.getValue().add(value);
            }
            editedStartTime.setValueList(valueListStartTime);

            // stop time
            SlotType1 editedStopTime = slotList.stream()
                    .filter(slot -> slot.getName().equals("serviceStopTime"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("serviceStopTime non trovato nei metadati di INI"));
            slotList.remove(editedStopTime);
            List<String> valuesSlotEditedStopTime = new ArrayList<>();
            valuesSlotEditedStopTime.add(updateRequestBodyDTO.getDataFinePrestazione());
            ValueListType valueListStopTime = new ValueListType();
            for (String value : valuesSlotEditedStopTime) {
                valueListStopTime.getValue().add(value);
            }
            editedStopTime.setValueList(valueListStopTime);

            slotList.add(editedStartTime);
            slotList.add(editedStopTime);
        } catch (Exception ex) {
            log.error("Error while perform merge service start/stop time : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge service start/stop time : ", ex);
        }
    }

    /**
     * Merge repository-type metadata
     * @param updateRequestBodyDTO
     * @param slotList
     */
    public static void mergeRepositoryType(PublicationMetadataReqDTO updateRequestBodyDTO, List<SlotType1> slotList) {
        try {
            SlotType1 repositoryTypeSlot = buildSlotObject(
                    "urn:ita:2017:repository-type",
                    null,
                    Collections.singletonList(updateRequestBodyDTO.getConservazioneANorma())
            );
            slotList.add(repositoryTypeSlot);
        } catch (Exception ex) {
            log.error("Error while perform merge repository type : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge repository type : ", ex);
        }
    }

    /**
     * Merge administrative-request-type metadata
     * @param updateRequestBodyDTO
     * @param slotList
     */
    public static void mergeAdministrativeRequest(PublicationMetadataReqDTO updateRequestBodyDTO, List<SlotType1> slotList) {
        try {
            SlotType1 repositoryTypeSlot = buildSlotObject(
                "urn:ita:2022:administrativeRequest",
                null,
                Collections.singletonList(updateRequestBodyDTO.getAdministrativeRequest())
            );
            slotList.add(repositoryTypeSlot);
        } catch (Exception ex) {
            log.error("Error while perform merge repository type : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge repository type : ", ex);
        }
    }

    /**
     * Merge description metadata
     * @param updateRequestBodyDTO
     * @param slotList
     */
    public static void mergeDescription(PublicationMetadataReqDTO updateRequestBodyDTO, List<SlotType1> slotList) {
        try {
            SlotType1 repositoryTypeSlot = buildSlotObject(
                "urn:ita:2022:description",
                null,
                new ArrayList<>(updateRequestBodyDTO.getDescription())
            );
            slotList.add(repositoryTypeSlot);
        } catch (Exception ex) {
            log.error("Error while perform merge repository type : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge repository type : ", ex);
        }
    }

    /**
     * Merge author classificationObject metadata for registry package
     * @param classificationList
     * @return
     */
    public static JAXBElement<ClassificationType> mergeAuthorClassificationObject(
            ObjectFactory objectFactory,
            List<ClassificationType> classificationList,
            String generatedUUID
    ) {
        try {
            ClassificationType authorClassificationObject = classificationList.stream()
                    .filter(classificationType -> classificationType.getClassificationScheme().equals("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("ClassificationObject dell'autore non trovato nei metadati di INI"));

            List<SlotType1> authorSlots = authorClassificationObject.getSlot();
            SlotType1 authorInstitutionSlot = authorSlots.stream()
                    .filter(slot -> slot.getName().equals("authorInstitution"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("authorInstitution non trovato nei metadati di INI"));
            SlotType1 authorPersonSlot = authorSlots.stream()
                    .filter(slot -> slot.getName().equals("authorPerson"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("authorPerson non trovato nei metadati di INI"));
            SlotType1 authorRoleSlot = authorSlots.stream()
                    .filter(slot -> slot.getName().equals("authorRole"))
                    .findFirst()
                    .orElseThrow(() -> new NoRecordFoundException("authorRole non trovato nei metadati di INI"));

            SlotType1 classificationObjAuthorSlot1 = buildSlotObject("authorInstitution", authorInstitutionSlot.getSlotType(), authorInstitutionSlot.getValueList().getValue());
            SlotType1 classificationObjAuthorSlot2 = buildSlotObject("authorPerson", authorPersonSlot.getSlotType(), authorPersonSlot.getValueList().getValue());
            SlotType1 classificationObjAuthorSlot3 = buildSlotObject("authorRole", authorRoleSlot.getSlotType(), authorRoleSlot.getValueList().getValue());

            List<SlotType1> classificationObjAuthorSlots = new ArrayList<>();
            classificationObjAuthorSlots.add(classificationObjAuthorSlot1);
            classificationObjAuthorSlots.add(classificationObjAuthorSlot2);
            classificationObjAuthorSlots.add(classificationObjAuthorSlot3);

            return buildClassificationObjectJax(
                    null,
                    "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d",
                    generatedUUID,
                    "SubmissionSet01_ClassificationAuthor",
                    null,
                    classificationObjAuthorSlots,
                    Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,
                    ""
            );
        } catch (Exception ex) {
            log.error("Error while perform merge author classification object : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge author classification object : ", ex);
        }
    }

    /**
     * Merge contentTypeCode classificationObject metadata for registry package
     * @param updateRequestBodyDTO
     * @return
     */
    public static JAXBElement<ClassificationType> mergeContentTypeCodeClassificationObject(
            ObjectFactory objectFactory,
            PublicationMetadataReqDTO updateRequestBodyDTO,
            String generatedUUID
    ) {
        try {
            InternationalStringType contentTypeCodeName = buildInternationalStringType(Collections.singletonList(updateRequestBodyDTO.getTipoAttivitaClinica().getDescription()));
            SlotType1 classificationObjContentTypeCodeSlot1 = buildSlotObject(
                    Constants.IniClientConstants.CODING_SCHEME,
                    null,
                    Collections.singletonList("2.16.840.1.113883.2.9.3.3.6.1.4")
            );
            List<SlotType1> classificationObjContentTypeCodeSlots = new ArrayList<>();
            classificationObjContentTypeCodeSlots.add(classificationObjContentTypeCodeSlot1);

            return buildClassificationObjectJax(
                    null,
                    "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",
                    generatedUUID,
                    "SubmissionSet01_ClinicalActivity",
                    contentTypeCodeName,
                    classificationObjContentTypeCodeSlots,
                    Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,
                    updateRequestBodyDTO.getTipoAttivitaClinica().getCode()
            );
        } catch (Exception ex) {
            log.error("Error while perform merge content type code classification object : {}" , ex.getMessage());
            throw new BusinessException("Error while perform merge content type code classification object : ", ex);
        }
    }
}
