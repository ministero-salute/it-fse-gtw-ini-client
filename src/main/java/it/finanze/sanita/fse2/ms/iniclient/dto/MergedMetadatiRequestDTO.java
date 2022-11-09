/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergedMetadatiRequestDTO {
	
    private String idDoc;
    
    private JWTPayloadDTO token;
    
    private PublicationMetadataReqDTO body;
}