package it.finanze.sanita.fse2.ms.iniclient.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.iniclient.client.ICrashProgramClient;
import it.finanze.sanita.fse2.ms.iniclient.config.MicroservicesCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuersDTO;

@Component
public class CrashProgramClient implements ICrashProgramClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MicroservicesCFG msConfig;

    @Override
    public String sendIssuerData(IssuersDTO issuers) {
        return restTemplate.postForObject(msConfig.getUrlCrashProgramValidator() + "/v1/issuer-create", issuers,
                String.class);
    }

}
