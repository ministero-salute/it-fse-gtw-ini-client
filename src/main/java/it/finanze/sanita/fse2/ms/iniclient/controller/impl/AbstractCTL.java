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
package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.Properties.MS_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;

/**
 *	Abstract controller.
 */
public abstract class AbstractCTL {

	@Autowired
	private Tracer tracer;

	@Value("${uar.client.mock-enable}")
	private boolean enableMockUar;

	@Autowired
	private IIssuerSRV issuserSRV;


	protected LogTraceInfoDTO getLogTraceInfo() {
		LogTraceInfoDTO out = new LogTraceInfoDTO(null, null);
		SpanBuilder spanbuilder = tracer.spanBuilder(MS_NAME);

		if (spanbuilder != null) {
			out = new LogTraceInfoDTO(
					spanbuilder.startSpan().getSpanContext().getSpanId(), 
					spanbuilder.startSpan().getSpanContext().getTraceId());
		}
		return out;
	}

	protected boolean isMockUar(final String issuer, IssuerETY issuerEty) {
		boolean mockUar = false;
		
		if(!enableMockUar) {
			return mockUar;
		}
		
		if(issuerEty == null) {
			mockUar = true;
			issuerEty = issuserSRV.findByIssuer(issuer);
			if(issuerEty != null) {
				mockUar = issuerEty.getMockUar()!=null ? issuerEty.getMockUar() : true; 	
			}
		} else {
			mockUar = issuerEty.getMockUar()!=null ? issuerEty.getMockUar() : true; 
		}
		 
		return mockUar; 
	}

}
