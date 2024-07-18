/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient.controller.handler;

import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.IdDocumentNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.InputValidationException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static it.finanze.sanita.fse2.ms.iniclient.enums.ErrorClassEnum.GENERIC;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ErrorClassEnum.INVALID_INPUT;

/**
 *	Exceptions Handler.
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private org.springframework.cloud.sleuth.Tracer tracer;
	protected LogTraceInfoDTO getLogTraceInfo() {
		LogTraceInfoDTO out = new LogTraceInfoDTO(null, null);
		if (tracer.currentSpan() != null) {
			out = new LogTraceInfoDTO(
					tracer.currentSpan().context().spanId(),
					tracer.currentSpan().context().traceId());
		}
		return out;
	}
     

	/**
	 * Management record not found exception received by clients.
	 *
	 * @param ex		exception
	 * @param request	request
	 * @return
	 */
	@ExceptionHandler(value = {NotFoundException.class, IdDocumentNotFoundException.class})
	protected ResponseEntity<ErrorResponseDTO> handleNotFoundException(final NotFoundException ex, final WebRequest request) {

		LogTraceInfoDTO traceInfo = getLogTraceInfo();
		ErrorResponseDTO response = new ErrorResponseDTO(traceInfo, ex.getError());

		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = InputValidationException.class)
	protected ResponseEntity<ErrorResponseDTO> handleInputValidationException(final InputValidationException ex){

		LogTraceInfoDTO traceInfo = getLogTraceInfo();
		ErrorDTO inError = new ErrorDTO(INVALID_INPUT.getType(), INVALID_INPUT.getTitle(), ex.getMessage(), INVALID_INPUT.getInstance());
		ErrorResponseDTO response = new ErrorResponseDTO(traceInfo, inError);

		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	

	/**
	 * Management generic exception.
	 * 
	 * @param ex		exception
	 * @param request	request
	 * @return			
	 */
	@ExceptionHandler(value = {BusinessException.class})
	protected ResponseEntity<ErrorResponseDTO> handleGenericException(final Exception ex, final WebRequest request) {

		LogTraceInfoDTO traceInfo = getLogTraceInfo();

		ErrorDTO error = new ErrorDTO(GENERIC.getType(), GENERIC.getTitle(), ex.getMessage(), "error/generic");
		ErrorResponseDTO response = new ErrorResponseDTO(traceInfo, error);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
		LogTraceInfoDTO traceInfo = getLogTraceInfo();

		ErrorDTO error = new ErrorDTO(INVALID_INPUT.getType(), INVALID_INPUT.getTitle(), ex.getMessage(), INVALID_INPUT.getInstance());
		ErrorResponseDTO response = new ErrorResponseDTO(traceInfo, error);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}