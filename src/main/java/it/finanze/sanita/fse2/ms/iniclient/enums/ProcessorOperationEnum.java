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

import lombok.Getter;

public enum ProcessorOperationEnum {

	PUBLISH(OperationLogEnum.PUB_CDA2, ErrorLogEnum.KO_PUBLISH),
	DELETE(OperationLogEnum.DELETE_CDA2, ErrorLogEnum.KO_DELETE),
	REPLACE(OperationLogEnum.REPLACE_CDA2, ErrorLogEnum.KO_REPLACE),
	UPDATE(OperationLogEnum.UPDATE_CDA, ErrorLogEnum.KO_UPDATE);

	@Getter
	private final ErrorLogEnum errorType;

	@Getter
	private final OperationLogEnum operation;

	ProcessorOperationEnum(OperationLogEnum operation, ErrorLogEnum errorType) {
		this.errorType = errorType;
		this.operation = operation;
	}
}
