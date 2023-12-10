package it.finanze.sanita.fse2.ms.iniclient.service;

public interface IConfigSRV {

    Boolean isRemoveMetadataEnable();
    
    Boolean isSubjectPersistenceEnabled();
    
    Boolean isCfOnIssuerNotAllowed();
    
    Boolean isControlLogPersistenceEnable();
    
    Boolean isKpiLogPersistenceEnable();
}
