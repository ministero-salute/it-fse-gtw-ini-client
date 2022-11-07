/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.Data;

@Data
public class UpdateRequestDTO {
	
    private JWTPayloadDTO token;
    
    private String marshallData;
}
