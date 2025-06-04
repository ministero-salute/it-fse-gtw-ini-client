package it.finanze.sanita.fse2.ms.iniclient.exceptions;

import static it.finanze.sanita.fse2.ms.iniclient.enums.ErrorClassEnum.MISSING_METADATA;

import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException;

public class MergeMetadatoNotFoundException extends NotFoundException {
	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 6437220661832499496L;

	public MergeMetadatoNotFoundException(final String msg) {
		super(new ErrorDTO(MISSING_METADATA.getType(), MISSING_METADATA.getTitle(), msg, MISSING_METADATA.getInstance()));
	}

}
