package it.finanze.sanita.fse2.ms.iniclient.service;

import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerDeleteResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerResponseDTO;

public interface IIssuerSRV {

	boolean isMocked(String issuer);
    IssuerResponseDTO createIssuer(IssuerCreateRequestDTO issuerDTO);
    IssuerDeleteResponseDTO removeIssuer(String issuerName);

}
