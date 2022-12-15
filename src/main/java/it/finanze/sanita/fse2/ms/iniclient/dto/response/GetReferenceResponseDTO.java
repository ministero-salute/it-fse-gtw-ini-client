/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetReferenceResponseDTO extends ResponseDTO {

	private String uuid;
	
	private String documentType;

	private String errorMessage;

	public GetReferenceResponseDTO() {
		super();
	}
	
}

