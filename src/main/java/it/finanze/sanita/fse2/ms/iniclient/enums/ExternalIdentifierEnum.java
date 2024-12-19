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
package it.finanze.sanita.fse2.ms.iniclient.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExternalIdentifierEnum {


	PATIENT("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427","patientId_1"),
	UnIQUE_ID("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab","uniqueId_1");
	
	@Getter
	private final String classificationScheme;
	
	@Getter
	private final String id;


	public static ExternalIdentifierEnum getByClassificationScheme(String classificationScheme) {
		for (ExternalIdentifierEnum classificationEnum : ExternalIdentifierEnum.values()) {
			if (classificationEnum.getClassificationScheme().equals(classificationScheme)) {
				return classificationEnum;
			}
		}
		return null;
	}

}