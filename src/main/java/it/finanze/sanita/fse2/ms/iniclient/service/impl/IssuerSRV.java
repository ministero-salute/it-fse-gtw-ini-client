package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerDeleteResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIssuerRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IssuerSRV implements IIssuerSRV {

    @Autowired
    private IIssuerRepo issuerRepo;
    
    @Override
    public boolean isMocked(final String issuer) {
    	boolean mocked = true;
        IssuerETY issuerETY = issuerRepo.findByName(issuer);
        if (issuerETY != null){
        	mocked = issuerETY.getMock();
        }
        return mocked;
    }

    @Override
    public IssuerResponseDTO createIssuer(IssuerCreateRequestDTO issuerDTO) {
        IssuerResponseDTO out = new IssuerResponseDTO();
        out.setEsito(false);

        IssuerETY entity = new IssuerETY();
        entity.setIssuer(issuerDTO.getIssuer());
        entity.setMock(issuerDTO.isMock());

        String id = issuerRepo.createIssuer(entity);

        out.setEsito(!StringUtility.isNullOrEmpty(id));
        out.setId(id);
        if (!out.getEsito()){
            String message = String.format("Creazione dell'issuer %s nel database non riuscita", entity.getIssuer());
            throw new BusinessException(message);
        }
        return out;
    }

    @Override
    public IssuerDeleteResponseDTO removeIssuer(String issuerName) {
        IssuerDeleteResponseDTO out = new IssuerDeleteResponseDTO();
        out.setEsito(false);

        Integer count = issuerRepo.removeByName(issuerName);
        if (count == 0){
            ErrorDTO error = new ErrorDTO();
            error.setTitle("Error in removeIssuer");
            error.setDetail(String.format("Nessun issuer da eliminare trovato con il nome %s", issuerName));
            throw new it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException(error);
        }
        out.setEsito(true);
        out.setCount(count);

        return out;
    }
}
