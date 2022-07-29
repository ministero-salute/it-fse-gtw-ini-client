package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTreeDTO {
    Document documentEntry;
    Document tokenEntry;
    Document submissionSetEntry;

    /**
     * Check object integrity
     * @return
     */
    public boolean checkIntegrity() {
        return documentEntry != null && tokenEntry != null && submissionSetEntry != null;
    }
}
