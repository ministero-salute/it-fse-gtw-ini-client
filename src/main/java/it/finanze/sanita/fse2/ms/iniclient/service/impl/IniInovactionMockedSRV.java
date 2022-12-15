package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMergedMetadatiDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

@Service
@ConditionalOnProperty(name="ini.client.mock-enable", havingValue="true")
public class IniInovactionMockedSRV implements IIniInvocationSRV  {

	@Autowired
	private LoggerHelper logger;
	
	@Override
	public IniResponseDTO publishOrReplaceOnIni(String workflowInstanceId,ProcessorOperationEnum operation) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		out.setErrorMessage("Regime di mock abilitato");
		logger.info("Regime di mock abilitato", ProcessorOperationEnum.PUBLISH.getOperation(), 
				ResultLogEnum.OK, startingDate, 
				Constants.IniClientConstants.JWT_MISSING_ISSUER_PLACEHOLDER, 
				Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER,  
				Constants.IniClientConstants.JWT_MISSING_ISSUER_PLACEHOLDER,
				Constants.IniClientConstants.JWT_MISSING_SUBJECT,  
				Constants.IniClientConstants.JWT_MISSING_LOCALITY,
				null, null, null);
		return out;
	}

	@Override
	public IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO) {
		return new IniResponseDTO();
	}

	@Override
	public IniResponseDTO updateByRequestBody(SubmitObjectsRequest submitObjectRequest, UpdateRequestDTO updateRequestDTO) {
		return new IniResponseDTO();
	}


	@Override
	public AdhocQueryResponse getMetadata(String oid, JWTTokenDTO tokenDTO) {
		AdhocQueryResponse out = new AdhocQueryResponse();
		out.setRequestId("Attenzione chiamata in regime di mock");
		return out;
	}

	@Override
	public GetReferenceResponseDTO getReference(String oid, JWTTokenDTO tokenDTO) {
		GetReferenceResponseDTO out = new GetReferenceResponseDTO();
		out.setUuid("UUID_MOCKATO");
		out.setDocumentType("DOCUMENT_TYPE MOCKATO");
		return out;
	}

	@Override
	public GetMergedMetadatiDTO getMergedMetadati(String oidToUpdate, MergedMetadatiRequestDTO updateRequestDTO) {
		return new GetMergedMetadatiDTO();
	}

}
