/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.dto.response;

import lombok.Getter;
import lombok.Setter;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

@Getter
@Setter
public class GetMetadatiResponseDTO extends ResponseDTO {


	private AdhocQueryResponse response;

	private String errorMessage;

	public GetMetadatiResponseDTO() {
		super();
	}

	public GetMetadatiResponseDTO(final LogTraceInfoDTO traceInfo, final AdhocQueryResponse inResponse, final String inErrorMessage) {
		super(traceInfo);
		response = inResponse;
		errorMessage = inErrorMessage;
	}

}

