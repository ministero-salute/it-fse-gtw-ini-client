package it.finanze.sanita.fse2.ms.iniclient.repository.mongo;

import java.util.List;

import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;

public interface IIssuerRepo {

    IssuerETY findByName(String name);
    String createIssuer(IssuerETY issuerETY);
    Integer removeByName(String name);
    IssuerETY findRegioneMiddleware(String etichettaRegione);
    List<IssuerETY> findIssuersCrashProgrm();

}
