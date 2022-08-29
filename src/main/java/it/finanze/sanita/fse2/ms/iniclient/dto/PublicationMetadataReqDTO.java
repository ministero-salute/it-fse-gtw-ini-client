package it.finanze.sanita.fse2.ms.iniclient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.iniclient.enums.AttivitaClinicaEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.HealthcareFacilityEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.PracticeSettingCodeEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.TipoDocAltoLivEnum;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PublicationMetadataReqDTO extends AbstractDTO {

	private static final long serialVersionUID = 8736849367644751300L;

	@Schema(description = "Tipologia struttura che ha prodotto il documento", required = true)
	private HealthcareFacilityEnum tipologiaStruttura;

	@Schema(description = "Regole di accesso")
	@Size(min = 0, max = 100)
	private List<String> attiCliniciRegoleAccesso;

	@Schema(description = "Tipo documento alto livello", required = true)
	private TipoDocAltoLivEnum tipoDocumentoLivAlto;

	@Schema(description = "Assetto organizzativo che ha portato alla creazione del documento", required = true)
	private PracticeSettingCodeEnum assettoOrganizzativo;
	 
	@Schema(description = "Data inizio prestazione")
	@Size(min = 0, max = 100)
	private String dataInizioPrestazione;

	@Schema(description = "Data fine prestazione")
	@Size(min = 0, max = 100)
	private String dataFinePrestazione;

	@Schema(description = "Conservazione a norma")
	@Size(min = 0, max = 100)
	private String conservazioneANorma;

	@Schema(description = "Tipo attività clinica",required = true)
	private AttivitaClinicaEnum tipoAttivitaClinica;

	@Schema(description = "Identificativo sottomissione",required = true)
	@Size(min = 0, max = 100)
	private String identificativoSottomissione;

}
