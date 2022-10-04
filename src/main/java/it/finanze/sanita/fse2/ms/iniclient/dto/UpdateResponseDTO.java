package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateResponseDTO {
    private RegistryResponseType registryResponse;
    private AdhocQueryResponse oldMetadata;
}
