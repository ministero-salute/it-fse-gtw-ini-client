package it.finanze.sanita.fse2.ms.iniclient.utility.common;

import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import org.bson.Document;

public class CommonUtility {
    /**
     *
     * @param documentEntry
     * @return
     */
    public static DocumentEntryDTO extractDocumentEntry(Document documentEntry) {
        return JsonUtility.clone(documentEntry, DocumentEntryDTO.class);
    }

    /**
     *
     * @param submissionSetEntry
     * @return
     */
    public static SubmissionSetEntryDTO extractSubmissionSetEntry(Document submissionSetEntry) {
        return JsonUtility.clone(submissionSetEntry, SubmissionSetEntryDTO.class);
    }
}
