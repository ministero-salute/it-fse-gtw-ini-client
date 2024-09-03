package it.finanze.sanita.fse2.ms.iniclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class MicroservicesCFG {

    @Value("${ms.url.gtw-crash-program-validator}")
    private String urlCrashProgramValidator;

}
