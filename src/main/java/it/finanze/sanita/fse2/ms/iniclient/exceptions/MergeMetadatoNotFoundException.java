package it.finanze.sanita.fse2.ms.iniclient.exceptions;

import static it.finanze.sanita.fse2.ms.iniclient.enums.ErrorClassEnum.METADATO_MISSING;

import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException;

public class MergeMetadatoNotFoundException extends NotFoundException {
	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 6437220661832499496L;

	public MergeMetadatoNotFoundException(final String msg) {
		super(new ErrorDTO(METADATO_MISSING.getType(), METADATO_MISSING.getTitle(), msg, METADATO_MISSING.getInstance()));
	}

}
