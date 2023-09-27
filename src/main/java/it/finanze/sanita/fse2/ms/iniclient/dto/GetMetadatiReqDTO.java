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
package it.finanze.sanita.fse2.ms.iniclient.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetMetadatiReqDTO {

	@NotNull
	private String iss;
	@NotNull
	private String sub;
	@NotNull
	private String subject_organization_id;
	@NotNull
	private String subject_organization;
	@NotNull
	private String locality;
	@NotNull
	private String subject_role;
	@NotNull
	private String person_id;
	@NotNull
    private boolean patient_consent;
	@NotNull
    private String purpose_of_use;
	@NotNull
    private String resource_hl7_type;
	@NotNull
    private String action_id;
	@NotNull
    private String subject_application_id;
	@NotNull
    private String subject_application_version;
	@NotNull
    private String subject_application_vendor;
}
