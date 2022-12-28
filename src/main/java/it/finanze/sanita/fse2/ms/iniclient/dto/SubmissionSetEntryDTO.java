/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionSetEntryDTO {
    private String submissionTime;
    private String sourceId;
    private String contentTypeCode;
    private String contentTypeCodeName;
    private String uniqueID;
}
