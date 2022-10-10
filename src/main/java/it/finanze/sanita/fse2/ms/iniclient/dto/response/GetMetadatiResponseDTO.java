package it.finanze.sanita.fse2.ms.iniclient.dto.response;

import lombok.Getter;
import lombok.Setter;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

@Getter
@Setter
public class GetMetadatiResponseDTO extends ResponseDTO {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 5457503502983726876L;

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

