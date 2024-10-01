package it.finanze.sanita.fse2.ms.iniclient.repository.entity;

import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import lombok.Data;
import lombok.Getter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "#{@auditIni}")
@Data
public class AuditIniETY {

    public static final String WORKFLOW_INSTANCE_ID = "workflow_instance_id";
    public static final String EVENT_TYPE = "eventType";
    public static final String EVENT_DATE = "eventDate";
    public static final String SOAP_REQUEST = "soapRequest";
    public static final String SOAP_RESPONSE = "soapResponse";

    @Id
    private String id;
    
    @Field(name = WORKFLOW_INSTANCE_ID)
    private final String workflowInstanceId;
    
    @Field(name = EVENT_TYPE)
    private final EventType eventType;
    
    @Field(name = EVENT_DATE)
    private final Date eventDate;
    
    @Field(name = SOAP_REQUEST)
    private String soapRequest;
    
    @Field(name = SOAP_RESPONSE)
    private String soapResponse;

    public AuditIniETY(String workflowInstanceId, EventType eventType, Date eventDate, String soapRequest, String soapResponse){
        this.workflowInstanceId = workflowInstanceId;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.soapRequest = soapRequest;
        this.soapResponse = soapResponse;
    }

}
