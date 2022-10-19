/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JWTTokenDTO extends AbstractDTO {
    
    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -1047169868136987347L;
	
	private JWTPayloadDTO payload;
}
