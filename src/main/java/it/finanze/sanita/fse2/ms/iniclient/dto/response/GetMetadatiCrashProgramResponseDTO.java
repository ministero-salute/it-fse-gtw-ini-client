package it.finanze.sanita.fse2.ms.iniclient.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMetadatiCrashProgramResponseDTO extends ResponseDTO {
 
	private GetMetadatiCrashProgramDTO metadati;

	private Boolean foundDocument;

	public GetMetadatiCrashProgramResponseDTO() {
		super();
	}

	public GetMetadatiCrashProgramResponseDTO(final LogTraceInfoDTO traceInfo, final GetMetadatiCrashProgramDTO inMetadati, final Boolean inFoundDocument) {
		super(traceInfo);
		metadati = inMetadati;
		foundDocument = inFoundDocument;
	}

}

