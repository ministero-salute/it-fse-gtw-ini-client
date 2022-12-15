/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMergedMetadatiResponseDTO extends ResponseDTO {

	private String errorMessage;
	
	private String marshallResponse;
	
	private String documentType;

	public GetMergedMetadatiResponseDTO() {
		super();
	}

	public GetMergedMetadatiResponseDTO(final LogTraceInfoDTO traceInfo, final String inErrorMessage, String inMarshallResponse,
			String inDocumentType) {
		super(traceInfo);
		errorMessage = inErrorMessage;
		marshallResponse = inMarshallResponse;
		documentType = inDocumentType;
	}

}

