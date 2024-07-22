package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import it.finanze.sanita.fse2.ms.iniclient.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuersDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerDeleteResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BadRequestException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.InputValidationException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIssuerRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        entity.setPazienteCf(issuerDTO.getPazienteCf());

        IssuerETY asl = null;
        if(!StringUtility.isNullOrEmpty(issuerDTO.getNomeDocumentRepository())) {
            entity.setNomeDocumentRepository(issuerDTO.getNomeDocumentRepository());
            asl = issuerRepo.findByNomeDocumentRepository(entity.getNomeDocumentRepository());
        }

        IssuerETY issuer = issuerRepo.findByName(entity.getIssuer());
        IssuerETY regione = issuerRepo.findRegioneMiddleware(entity.getEtichettaRegione());
        IssuerETY paziente = issuerRepo.findByFiscalCode(entity.getPazienteCf());

        if (issuer != null) throw new InputValidationException("Issuer già esistente nel database");
        if(regione != null) throw new BadRequestException("La regione indicata ha già un middleware");
        if(paziente != null) throw new InputValidationException("Il codice fiscale del paziente inserito è già presente");
        if(asl!=null && entity.getMiddleware()) throw new BadRequestException("Sono già presenti documenti con asl. Impossibile caricare il middleware");

        String id = issuerRepo.createIssuer(entity);

        out.setEsito(!StringUtility.isNullOrEmpty(id));
        out.setId(id);
        if (!out.getEsito()){
            String message = String.format("Creazione dell'issuer %s nel database non riuscita", entity.getIssuer());
            throw new BusinessException(message);
        }
 
        IssuersDTO issuers = buildIssuersDtoJson(entity.getIssuer());
        kafkaSRV.sendMessage(kafkaTopicCFG.getCrashProgramValidatorTopic(), issuerDTO.getMailResponsabile(), StringUtility.toJSONJackson(issuers)); 
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

    private IssuersDTO buildIssuersDtoJson(String issuer){
        List<IssuerETY> issuersEty = issuerRepo.findIssuersCrashProgrm();
        IssuersDTO issuers = new IssuersDTO();
        if(issuersEty!=null && !issuersEty.isEmpty()){
            issuers.setDataAggiornamento(new Date());
            issuers.setCounter(issuersEty.size());
            List<IssuerDTO> issuerDto = new ArrayList<>();
            for(IssuerETY i : issuersEty){
                issuerDto.add(new IssuerDTO(i.getIssuer(),i.getEtichettaRegione(),i.getPazienteCf()));
            }
            issuers.setIssuers(issuerDto);
        }
        issuers.setActualIssuer(issuer);
        return issuers;
    }
}
