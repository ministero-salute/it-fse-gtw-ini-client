
# Fascicolo Sanitario 2.0

# _it-fse-gtw-ini-client_

Il microservizio in oggetto ha la responsabilità di gestire la comunicazione con INI.

<br/>

## Repos
- [*it-fse-support*](https://github.com/ministero-salute/it-fse-support) - Pagina di supporto FSE 2.0
- [*it-fse-landing*](https://github.com/ministero-salute/it-fse-landing) - Landing page dei repository FSE 2.0

<br/>

## Test in isolamento

Con JDK 21 e Maven:

```sh
MAVEN_OPTS='-Xmx768m' mvn -PISOLATION clean test
```

Il profilo seleziona esclusivamente i test JUnit con tag `ISOLATION`, esclusi
dall'esecuzione Maven predefinita. I contesti Spring minimi usano il profilo
`ISOLATION`: nessun avvio dell'applicazione completa, MongoDB, Kafka, INI o altri
servizi esterni. Le dipendenze esterne sono mock; servizi, repository e controller
sono reali, con verifiche di regole di business, query, HTTP ed errori.

Report JaCoCo: `target/site/jacoco/index.html` (XML nella stessa directory).
Anche la JVM dei test è limitata a 768 MiB.

## License

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
