package it.finanze.sanita.fse2.ms.iniclient.client;

import it.finanze.sanita.fse2.ms.iniclient.dto.IssuersDTO;

public interface ICrashProgramClient {

    String sendIssuerData(IssuersDTO issuers);

}
