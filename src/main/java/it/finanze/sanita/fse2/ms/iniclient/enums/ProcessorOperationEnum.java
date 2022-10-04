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
