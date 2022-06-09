package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JWTHeaderDTO {

	/**
	 * Algoritmo utilizzato per la firma del token. Valori ammessi: RS256, RS383, RS512.
	 */
	private String alg;

	/**
	 * Tipologia di token. DEVE essere valorizzato con il valore 'JWT'.
	 */
	private String typ;

	/**
	 * Un riferimento opzionale alla chiave usata per la firma del token. Anche se valorizzato non viene utilizzato nella fase di verifica.
	 */
	private String kid;

	/**
	 * certificato X.509 utilizzato per la firma del token. 
	 * Valore in formato DER, codificato in base64.
	 */
	private String x5c;

	 
}
