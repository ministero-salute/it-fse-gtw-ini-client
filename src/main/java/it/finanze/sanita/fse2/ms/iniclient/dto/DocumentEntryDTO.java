package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntryDTO extends AbstractDTO {
    private String mimeType;
    private String entryUUID;
    private String creationTime;
    private String hash;
    private long size;
    private String status;
    private String languageCode;
    private String patientId;
    private String confidentialityCode;
    private String confidentialityCodeDisplayName;
    private String typeCode;
    private String typeCodeName;
    private String formatCode;
    private String formatCodeName;
    private String legalAuthenticator;
    private String sourcePatientInfo;
    private String author;
    private String representedOrganizationName;
    private String representedOrganizationCode;
    private String uniqueId;
    private List<String> referenceIdList;
    private String healthcareFacilityTypeCode;
    private String healthcareFacilityTypeCodeName;
    private List<String> eventCodeList;
    private String repositoryUniqueId;
    private String classCode;
    private String classCodeName;
    private String practiceSettingCode;
    private String practiceSettingCodeName;
    private String sourcePatientId;
    private String serviceStartTime;
    private String serviceStopTime;
}