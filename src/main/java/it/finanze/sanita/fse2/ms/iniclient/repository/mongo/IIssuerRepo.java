package it.finanze.sanita.fse2.ms.iniclient.repository.mongo;

import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;

import java.util.List;

public interface IIssuerRepo {

    IssuerETY findByName(String name);
    String createIssuer(IssuerETY issuerETY);
    Integer removeByName(String name);
    IssuerETY findRegioneMiddleware(String etichettaRegione);
    String updateIssuer(IssuerETY issuerETY);
    List<IssuerETY> findIssuersCrashProgrm();
    IssuerETY findByFiscalCode(String fiscalCode);
    IssuerETY findByNomeDocumentRepository(String nomeDocumentRepository);


}
