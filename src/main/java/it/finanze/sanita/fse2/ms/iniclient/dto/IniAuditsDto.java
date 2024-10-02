package it.finanze.sanita.fse2.ms.iniclient.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class IniAuditsDto {

	private List<IniAuditDto> audit;
}
