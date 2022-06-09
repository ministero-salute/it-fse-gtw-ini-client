package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionSetEntryDTO {
    private String submissionTime;
    private String sourceId;
    private String contentTypeCode;
    private String contentTypeCodeName;
    private String uniqueID;
}
