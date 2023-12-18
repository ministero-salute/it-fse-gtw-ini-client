package it.finanze.sanita.fse2.ms.iniclient.exceptions;

import static it.finanze.sanita.fse2.ms.iniclient.enums.ErrorClassEnum.ID_DOC_MISSING;

import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException;

public class IdDocumentNotFoundException extends NotFoundException {
	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 6437220661832499496L;

	public IdDocumentNotFoundException(final String msg) {
		super(new ErrorDTO(ID_DOC_MISSING.getType(), ID_DOC_MISSING.getTitle(), msg, ID_DOC_MISSING.getInstance()));
	}

}
