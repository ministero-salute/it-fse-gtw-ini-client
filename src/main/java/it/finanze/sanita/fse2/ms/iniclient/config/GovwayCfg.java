package it.finanze.sanita.fse2.ms.iniclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
 

@Configuration
@Slf4j
@Data
public class GovwayCfg {

	@Value("${govway-url}")
	private String govwayUrl;

	@Value("${govway-port}")
	private Integer govwayPort;

	@Value("${govway-user}")
	private String govwayUser;

	@Value("${govway-pass}")
	private String govwayPass;

}
