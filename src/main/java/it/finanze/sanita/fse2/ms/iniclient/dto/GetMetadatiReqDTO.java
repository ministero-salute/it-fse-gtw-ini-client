package it.finanze.sanita.fse2.ms.iniclient.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetMetadatiReqDTO {

	@NotNull
	private String iss;
	@NotNull
	private String sub;
	@NotNull
	private String subject_organization_id;
	@NotNull
	private String subject_organization;
	@NotNull
	private String locality;
	@NotNull
	private String subject_role;
	@NotNull
	private String person_id;
	@NotNull
    private boolean patient_consent;
	@NotNull
    private String purpose_of_use;
	@NotNull
    private String resource_hl7_type;
	@NotNull
    private String action_id;
}
