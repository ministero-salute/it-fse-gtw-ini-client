package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerDeleteResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIssuerRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IssuerSRV implements IIssuerSRV {

    @Autowired
    private IIssuerRepo issuerRepo;

    @Autowired
    private IKafkaSRV kafkaSRV;

    @Autowired
    private KafkaTopicCFG kafkaTopicCFG;
    
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
        entity.setMailResponsabile(issuerDTO.getMailResponsabile());
        entity.setMiddleware(issuerDTO.isMiddleware());
        entity.setEtichettaRegione(issuerDTO.getEtichettaRegione());
        if(!StringUtility.isNullOrEmpty(issuerDTO.getNomeDocumentRepository())) entity.setNomeDocumentRepository(issuerDTO.getNomeDocumentRepository());

        IssuerETY issuer = issuerRepo.findByName(entity.getIssuer());
        IssuerETY regione = issuerRepo.findRegioneMiddleware(entity.getEtichettaRegione());

        if (issuer != null){
            throw new BusinessException("Issuer già esistente nel database");
        }

        if(regione != null && issuerDTO.isMiddleware()) {
            throw new BusinessException("La regione indicata ha già un middleware");
        }


        String id = issuerRepo.createIssuer(entity);

        out.setEsito(!StringUtility.isNullOrEmpty(id));
        out.setId(id);
        if (!out.getEsito()){
            String message = String.format("Creazione dell'issuer %s nel database non riuscita", entity.getIssuer());
            throw new BusinessException(message);
        }

        kafkaSRV.sendMessage(kafkaTopicCFG.getCrashProgramValidatorTopic(), issuerDTO.getIssuer(), "Value"); 
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
