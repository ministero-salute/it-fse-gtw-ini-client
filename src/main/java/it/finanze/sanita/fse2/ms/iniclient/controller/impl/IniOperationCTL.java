package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.iniclient.controller.IIniOperationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMetadatiReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ReplaceRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

/**
 *
 *	INI Publication controller.
 */
@Slf4j
@RestController
public class IniOperationCTL extends AbstractCTL implements IIniOperationCTL {

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 8414558048109050743L;
	
	@Autowired
	private IIniInvocationSRV iniInvocationSRV;
    
    
    @Override
    public IniTraceResponseDTO create(final String workflowInstanceId, HttpServletRequest request) {
        log.info("Workflow instance id received:" + workflowInstanceId +", calling ini invocation client...");
        IniResponseDTO res = iniInvocationSRV.publishByWorkflowInstanceId(workflowInstanceId);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO delete(final DeleteRequestDTO requestBody, HttpServletRequest request) {
        log.info("document id received: " + requestBody.getIdentificativoDocUpdate() + ", calling ini delete client...");
        IniResponseDTO res = iniInvocationSRV.deleteByDocumentId(requestBody);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO update(final UpdateRequestDTO requestBody, HttpServletRequest request) {
        log.info("Metadata received: {}, calling ini update client...", JsonUtility.objectToJson(requestBody));
        IniResponseDTO res = iniInvocationSRV.updateByRequestBody(requestBody);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO replace(final ReplaceRequestDTO requestDTO, HttpServletRequest request) {
        log.info("identificativoDocUpdate received: " + requestDTO.getIdentificativoDocUpdate() + " - workflowInstanceId: " + requestDTO.getWorkflowInstanceId() + ", calling ini replace client...");
        IniResponseDTO res = iniInvocationSRV.replaceByWorkflowInstanceId(requestDTO);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

	@Override
	public AdhocQueryResponse getMetadati(String oid,GetMetadatiReqDTO req, HttpServletRequest request) {
		log.warn("Get metadati - Attenzione il token usato è configurabile dalle properties. Non usare in ambiente di produzione");
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(buildPayloadFromReq(req));
		return iniInvocationSRV.getMetadati(oid, token);
	}
	
	private JWTPayloadDTO buildPayloadFromReq(final GetMetadatiReqDTO req) {
		return JWTPayloadDTO.builder().
				action_id(req.getAction_id()).
				iss(req.getIss()).
				locality(req.getLocality()).
				person_id(req.getPerson_id()).
				purpose_of_use(req.getPurpose_of_use()).
				resource_hl7_type(req.getResource_hl7_type()).
				sub(req.getSub()).
				subject_organization(req.getSubject_organization()).
				subject_organization_id(req.getSubject_organization_id()).
				subject_role(req.getSubject_role()).
				patient_consent(req.isPatient_consent()).
				build();
	}
}
