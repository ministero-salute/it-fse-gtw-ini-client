package it.finanze.sanita.fse2.ms.iniclient.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PublishOrReplaceIniReq {

    private String workflowInstanceId;
    @NotNull
    private String repositoryType;
}
