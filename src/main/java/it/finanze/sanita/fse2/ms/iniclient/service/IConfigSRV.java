package it.finanze.sanita.fse2.ms.iniclient.service;

public interface IConfigSRV {

    Boolean isRemoveMetadataEnable();
    Integer getExpirationDate();
    Boolean isSubjectNotAllowed();
    Boolean isCfOnIssuerNotAllowed();
    Boolean isControlLogPersistenceEnable();
    Boolean isKpiLogPersistenceEnable();
    Boolean isAuditIniEnable();
    long getRefreshRate();
    
}
