/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.controller.handler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

/**
 *	Exceptions Handler.
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Tracker log.
	 */
	@Autowired
	private Tracer tracer;
     

	/**
	 * Management record not found exception received by clients.
	 * 
	 * @param ex		exception
	 * @param request	request
	 * @return			
	 */
	@ExceptionHandler(value = {NoRecordFoundException.class})
	protected ResponseEntity<ErrorResponseDTO> handleRecordNotFoundException(NoRecordFoundException ex, final WebRequest request) {
		log.error("" , ex);  
		Integer status = 404;

		String detail = ExceptionUtils.getMessage(ex);
		
		ErrorResponseDTO out = new ErrorResponseDTO(getLogTraceInfo(), "/msg/record-not-found", "Record not found", detail , status, "");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

		return new ResponseEntity<>(out, headers, status);
	}
	

	/**
	 * Management generic exception.
	 * 
	 * @param ex		exception
	 * @param request	request
	 * @return			
	 */
	@ExceptionHandler(value = {Exception.class})
	protected ResponseEntity<ErrorResponseDTO> handleGenericException(final Exception ex, final WebRequest request) {
		Integer status = 500;

		String msg = StringUtility.isNullOrEmpty(ex.getMessage()) ? "Errore generico" : ex.getMessage();
		ErrorResponseDTO out = new ErrorResponseDTO(getLogTraceInfo(), "/msg/generic-error", "Generic error", msg , status, "");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

		return new ResponseEntity<>(out, headers, status);
	}
 

	private LogTraceInfoDTO getLogTraceInfo() {
		return new LogTraceInfoDTO(
				tracer.currentSpan().context().spanIdString(), 
				tracer.currentSpan().context().traceIdString());
	}
 
}