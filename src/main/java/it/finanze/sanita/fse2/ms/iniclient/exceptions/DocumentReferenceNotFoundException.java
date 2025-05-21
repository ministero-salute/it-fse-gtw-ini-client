package it.finanze.sanita.fse2.ms.iniclient.exceptions;

import static it.finanze.sanita.fse2.ms.iniclient.enums.ErrorClassEnum.REFERENCE_DATA_MISSING;

import it.finanze.sanita.fse2.ms.iniclient.dto.ErrorDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException;

public class DocumentReferenceNotFoundException extends NotFoundException {

    public DocumentReferenceNotFoundException() {
        super(new ErrorDTO(REFERENCE_DATA_MISSING.getType(), REFERENCE_DATA_MISSING.getTitle(),
                REFERENCE_DATA_MISSING.getDetail(), REFERENCE_DATA_MISSING.getInstance()));
    }

}
