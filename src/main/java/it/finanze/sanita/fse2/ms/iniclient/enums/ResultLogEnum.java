package it.finanze.sanita.fse2.ms.iniclient.enums;

public enum ResultLogEnum implements ILogEnum {

	OK("OK", "Operazione eseguita con successo"),
	KO("KO", "Errore nell'esecuzione dell'operazione"); 

	private String code;
	
	public String getCode() {
		return code;
	}

	private String description;

	private ResultLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}

	public String getDescription() {
		return description;
	}

}
