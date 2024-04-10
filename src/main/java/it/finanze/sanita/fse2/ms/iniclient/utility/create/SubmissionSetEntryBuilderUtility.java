package it.finanze.sanita.fse2.ms.iniclient.utility.create;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CODING_SCHEME;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.SUBMISSION_SET_DEFAULT_ID;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildExternalIdentifierObjectJax;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildInternationalStringType;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObjectJax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;

import it.finanze.sanita.fse2.ms.iniclient.dto.AuthorSlotDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmissionSetEntryBuilderUtility extends EntryBuilderUility {

	@Getter
	@Setter
	private static ObjectFactory objectFactory = new ObjectFactory();
	
	public static JAXBElement<RegistryPackageType> buildRegistryPackageObjectSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO,JWTPayloadDTO jwtPayloadDTO, String id) {

		RegistryPackageType registryPackageObject = new RegistryPackageType();

		registryPackageObject.setId(id);
		registryPackageObject.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage");
		registryPackageObject.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
		registryPackageObject.setName(null);
		registryPackageObject.setDescription(null);
		registryPackageObject.getSlot().addAll(buildSlotSubmissionSet(submissionSetEntryDTO));
		registryPackageObject.getClassification().addAll(buildClassificationSubmissionSet(submissionSetEntryDTO,id));
		registryPackageObject.getExternalIdentifier().addAll(buildExternalIdentifierSubmissionSet(submissionSetEntryDTO));
		return objectFactory.createRegistryPackage(registryPackageObject);
	}
	
	private static List<SlotType1> buildSlotSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO){
		List<SlotType1> out = new ArrayList<>();
		out.add(buildSlotObjectJax("submissionTime",null,Arrays.asList(submissionSetEntryDTO.getSubmissionTime())).getValue());
		return out;
	}
	
	private static List<ExternalIdentifierType> buildExternalIdentifierSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO) {
		List<ExternalIdentifierType> out = new ArrayList<>();

		// Build external identifiers
		JAXBElement<ExternalIdentifierType> externalIdentifierObject1 = buildExternalIdentifierObjectJax(
			"XDSSubmissionSet.sourceId","SubmissionSet01_SourceId",
			"urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
			EXTERNAL_IDENTIFIER_URN, SUBMISSION_SET_DEFAULT_ID, submissionSetEntryDTO.getSourceId());
		out.add(externalIdentifierObject1.getValue());

		JAXBElement<ExternalIdentifierType> externalIdentifierObject2 = buildExternalIdentifierObjectJax(
			"XDSSubmissionSet.uniqueId", "SubmissionSet01_UniqueId",
			"urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8",
			EXTERNAL_IDENTIFIER_URN, SUBMISSION_SET_DEFAULT_ID, submissionSetEntryDTO.getUniqueID());
		out.add(externalIdentifierObject2.getValue());
		
		 
		JAXBElement<ExternalIdentifierType> externalPatientIdentifierObject = buildExternalIdentifierObjectJax(
				"XDSSubmissionSet.patientId",
				"SubmissionSet01_PatientId",
				"urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446",
				EXTERNAL_IDENTIFIER_URN, SUBMISSION_SET_DEFAULT_ID, submissionSetEntryDTO.getPatientId());
			out.add(externalPatientIdentifierObject.getValue());
		return out;
	}
	
	private static List<ClassificationType> buildClassificationSubmissionSet(SubmissionSetEntryDTO submissionSetEntryDTO, String id) {
		List<ClassificationType> out = new ArrayList<>();
		//Content Type
		InternationalStringType nameContentTypeCode = buildInternationalStringType(Collections.singletonList(submissionSetEntryDTO.getContentTypeCodeName()));
		SlotType1 nameContentTypeSlot = buildSlotObject(CODING_SCHEME,"2.16.840.1.113883.2.9.3.3.6.1.4");

		JAXBElement<ClassificationType> contentTypeCodeClassification = buildClassificationObjectJax(
			null,"urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",id,
			"IdContentTypeCode",nameContentTypeCode,Arrays.asList(nameContentTypeSlot),
			CLASSIFICATION_OBJECT_URN,submissionSetEntryDTO.getContentTypeCode()
		);
		out.add(contentTypeCodeClassification.getValue());

		//Author
		AuthorSlotDTO author = buildAuthorSlot(submissionSetEntryDTO.getAuthorRole(), submissionSetEntryDTO.getAuthorInstitution(), submissionSetEntryDTO.getAuthor());
		JAXBElement<ClassificationType> authorClassification = buildClassificationObjectJax(null,"urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d",id,
	            "SubmissionSet01_ClassificationAuthor",
	            null,
	            Arrays.asList(author.getAuthorRoleSlot(), author.getAuthorInstitutionSlot(), author.getAuthorPersonSlot()),
	            "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
	            "");
		
		out.add(authorClassification.getValue());
		return out;
	}
}
