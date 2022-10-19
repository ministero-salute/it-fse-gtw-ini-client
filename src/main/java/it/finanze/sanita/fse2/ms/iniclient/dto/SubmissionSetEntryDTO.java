/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionSetEntryDTO extends AbstractDTO {
    private String submissionTime;
    private String sourceId;
    private String contentTypeCode;
    private String contentTypeCodeName;
    private String uniqueID;
}
