package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTTokenDTO extends AbstractDTO {
    
    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -1047169868136987347L;
	
	private JWTPayloadDTO payload;
}
