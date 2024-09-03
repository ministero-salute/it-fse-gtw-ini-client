package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import it.finanze.sanita.fse2.ms.iniclient.controller.IIssuerCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerDeleteResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static it.finanze.sanita.fse2.ms.iniclient.enums.ErrorClassEnum.ISSUER_MISSING;

@Slf4j
@RestController
public class IssuerCTL extends AbstractCTL implements IIssuerCTL {

    @Autowired
    private IIssuerSRV issuerSRV;

    @Override
    public IssuerResponseDTO create(IssuerCreateRequestDTO requestBody, HttpServletRequest request) {
        LogTraceInfoDTO traceInfo = getLogTraceInfo();

        if (StringUtility.isNullOrEmpty(requestBody.getIssuer())){
            String message = String.format("La stringa issuer deve essere valorizzata");
            log.error(message);
            throw new BusinessException(message);
        }
        if (!requestBody.isMiddleware() && StringUtility.isNullOrEmpty(requestBody.getNomeDocumentRepository())){
            String message = String.format("La stringa NomeDocumentRepository deve essere valorizzata se middleware è true");
            log.error(message);
            throw new BusinessException(message);
        }

        IssuerResponseDTO response = issuerSRV.createIssuer(requestBody);
        response.setTraceID(traceInfo.getTraceID());
        response.setSpanID(traceInfo.getSpanID());
        return response;
    }

    @Override
    public IssuerDeleteResponseDTO delete(String issuer, HttpServletRequest request) {
        LogTraceInfoDTO traceInfo = getLogTraceInfo();
        IssuerDeleteResponseDTO response = issuerSRV.removeIssuer(issuer);
        response.setTraceID(traceInfo.getTraceID());
        response.setSpanID(traceInfo.getSpanID());
        return response;
    }


    @Override
    public IssuerResponseDTO replace(IssuerCreateRequestDTO requestBody, String issuer, HttpServletRequest request) {
        LogTraceInfoDTO traceInfo = getLogTraceInfo();

        if (!requestBody.isMiddleware() && StringUtility.isNullOrEmpty(requestBody.getNomeDocumentRepository())){
            String message = String.format("La stringa NomeDocumentRepository deve essere valorizzata se middleware è true");
            log.error(message);
            throw new BusinessException(message);
        }

        IssuerETY ety = issuerSRV.findByIssuer(issuer);

        IssuerResponseDTO response;
        if(ety==null) {
            ErrorDTO error = new ErrorDTO(ISSUER_MISSING.getType(), ISSUER_MISSING.getTitle(), ISSUER_MISSING.getDetail(), ISSUER_MISSING.getInstance());
            throw new NotFoundException(error);
        }
        else response = issuerSRV.updateIssuer(ety, requestBody);


        response.setTraceID(traceInfo.getTraceID());
        response.setSpanID(traceInfo.getSpanID());
        return response;
    }
}
