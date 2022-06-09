package it.finanze.sanita.fse2.ms.iniclient.utility;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestBuilderUtilityOld {
    /**
     * COMPLETE
     *
     * @param qname
     * @return
     */
    public static SubmitObjectsRequest buildSubmitObjectRequest(QName qname) {
        /// TODO: use workflowInstanceId to recover data on mongo
        SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
        RegistryObjectListType registryObjectListType = buildRegistryObjectList(qname);
        submitObjectsRequest.setRegistryObjectList(registryObjectListType);
        return submitObjectsRequest;
    }

    /**
     * COMPLETE
     *
     * @param qname
     * @return
     */
    private static RegistryObjectListType buildRegistryObjectList(QName qname) {
        ExtrinsicObjectType extrinsicObject = buildExtrinsicObject(
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                false,
                "text/x-cda-r2+xml",
                "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1",
                "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved"
        );
        RegistryPackageType registryPackageObject = buildRegistryPackageObject(
                "SubmissionSet01",
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage",
                "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved",
                buildInternationalStringType(Collections.singletonList("Prescrizione")),
                buildInternationalStringType(Collections.singletonList("Prescrizione SistemaTS")),
                qname
        );
        ClassificationType classificationObject = buildClassificationObject(
                "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd",
                null,
                "SubmissionSet01",
                "SubmissionSet01_Submission",
                null,
                null,
                null,
                null);
        List<SlotType1> associationObjectSlots = new ArrayList<>();
        SlotType1 associationObjSlot = buildSlotObject(
                "SubmissionSetStatus",
                null,
                Collections.singletonList("Original"));
        associationObjectSlots.add(associationObjSlot);
        AssociationType1 associationObject = buildAssociationObject(
                "urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember",
                "SubmissionSet01_Association_1",
                "SubmissionSet01",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                associationObjectSlots);

        RegistryObjectListType registryObjectListType = new RegistryObjectListType();

        registryObjectListType.getIdentifiable().add(convertToJAXBElement(qname, ExtrinsicObjectType.class, extrinsicObject));
        registryObjectListType.getIdentifiable().add(convertToJAXBElement(qname, RegistryPackageType.class, registryPackageObject));
        registryObjectListType.getIdentifiable().add(convertToJAXBElement(qname, ClassificationType.class, classificationObject));
        registryObjectListType.getIdentifiable().add(convertToJAXBElement(qname, AssociationType1.class, associationObject));
        return registryObjectListType;
    }

    /**
     * ///TODO: Classifications number is variable?
     * ///TODO: External IDs number is variable?
     * 8 slots
     * 7 classifications
     * 2 external identifiers
     * COMPLETE
     *
     * @param id
     * @param isOpaque
     * @param mimeType
     * @param objectType
     * @param status
     * @return
     */
    private static ExtrinsicObjectType buildExtrinsicObject(
            String id,
            boolean isOpaque,
            String mimeType,
            String objectType,
            String status
    ) {
        ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();
        extrinsicObject.setId(id);
        extrinsicObject.setIsOpaque(isOpaque);
        extrinsicObject.setMimeType(mimeType);
        extrinsicObject.setObjectType(objectType);
        extrinsicObject.setStatus(status);
        buildExtrinsicObjectSlots(extrinsicObject);
        buildExtrinsicClassificationObjects(extrinsicObject);

        ExternalIdentifierType externalIdentifier1 = buildExternalIdentifierObject(
                "XDSDocumentEntry.patientId",
                "patientId_1",
                "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "XXXRND78C60Z222F^^^&amp;2.16.840.1.113883.2.9.4.3.2&amp;ISO");
        ExternalIdentifierType externalIdentifier2 = buildExternalIdentifierObject(
                "XDSDocumentEntry.uniqueId",
                "uniqueId_1",
                "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab",
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "2.16.840.1.113883.2.9.4.3.8^0700A4005044661_PRESPEC");
        extrinsicObject.getExternalIdentifier().add(externalIdentifier1);
        extrinsicObject.getExternalIdentifier().add(externalIdentifier2);
        return extrinsicObject;
    }

    /**
     * COMPLETE
     *
     * @param extrinsicObject
     */
    private static void buildExtrinsicObjectSlots(ExtrinsicObjectType extrinsicObject) {
        // list of slots
        List<String> valuesSlot1 = new ArrayList<>();
        List<String> valuesSlot2 = new ArrayList<>();
        List<String> valuesSlot3 = new ArrayList<>();
        List<String> valuesSlot4 = new ArrayList<>();
        List<String> valuesSlot5 = new ArrayList<>();
        List<String> valuesSlot6 = new ArrayList<>();
        List<String> valuesSlot7 = new ArrayList<>();
        List<String> valuesSlot8 = new ArrayList<>();
        // Adding values
        valuesSlot1.add("20181204070000");
        valuesSlot2.add("20181204070000");
        valuesSlot3.add("20181204070000");
        valuesSlot4.add("b7845979bbc771cb2f326458a878dcd3edb92373");
        valuesSlot5.add("it-IT");
        valuesSlot6.add("2.16.840.1.113883.2.9.2.0.4.5.2");
        valuesSlot7.add("10724");
        valuesSlot8.add("XXXRND78C60Z222F^^^&amp;2.16.840.1.113883.2.9.4.3.2&amp;ISO");
        // Populate extrinsicObject
        extrinsicObject.getSlot().add(buildSlotObject("creationTime", null, valuesSlot1));
        extrinsicObject.getSlot().add(buildSlotObject("serviceStartTime", null, valuesSlot2));
        extrinsicObject.getSlot().add(buildSlotObject("serviceStopTime", null, valuesSlot3));
        extrinsicObject.getSlot().add(buildSlotObject("hash", null, valuesSlot4));
        extrinsicObject.getSlot().add(buildSlotObject("languageCode", null, valuesSlot5));
        extrinsicObject.getSlot().add(buildSlotObject("repositoryUniqueId", null, valuesSlot6));
        extrinsicObject.getSlot().add(buildSlotObject("size", null, valuesSlot7));
        extrinsicObject.getSlot().add(buildSlotObject("sourcePatientId", null, valuesSlot8));
    }

    /**
     * ///TODO: check if num of objects is variable
     *
     * @param extrinsicObject
     */
    private static void buildExtrinsicClassificationObjects(ExtrinsicObjectType extrinsicObject) {
        // Slots 1
        SlotType1 classificationObj1Slot1 = buildSlotObject(
                "authorInstitution",
                null,
                new ArrayList<>(
                        Collections.singleton("GENOVESE^^^^^&amp;2.16.840.1.113883.2.9.4.1.3&amp;ISO^^^^070103")));
        SlotType1 classificationObj1Slot2 = buildSlotObject(
                "authorPerson",
                null,
                new ArrayList<>(
                        Collections.singleton("LRSMRA51P54F205D^^^^^^^^&amp;2.16.840.1.113883.2.9.4.3.2&amp;ISO")));
        SlotType1 classificationObj1Slot3 = buildSlotObject(
                "authorRole",
                null,
                new ArrayList<>(Collections.singleton("APR")));
        List<SlotType1> classificationObj1Slots = new ArrayList<>();
        classificationObj1Slots.add(classificationObj1Slot1);
        classificationObj1Slots.add(classificationObj1Slot2);
        classificationObj1Slots.add(classificationObj1Slot3);
        ClassificationType classificationObject1 = buildClassificationObject(
                null,
                "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "Author_1",
                null,
                classificationObj1Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "");

        // Slots 2
        SlotType1 classificationObj2Slot1 = buildSlotObject(
                "codingScheme",
                null,
                new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.5")));
        List<SlotType1> classificationObj2Slots = new ArrayList<>();
        classificationObj2Slots.add(classificationObj2Slot1);
        InternationalStringType name2 = buildInternationalStringType(new ArrayList<>(Collections.singleton("Prescrizione")));
        ClassificationType classificationObject2 = buildClassificationObject(
                null,
                "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "ClassCodeId_1",
                name2,
                classificationObj2Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "PRS");

        // Slots 3
        SlotType1 classificationObj3Slot1 = buildSlotObject(
                "codingScheme",
                null,
                new ArrayList<>(Collections.singleton("2.16.840.1.113883.5.25")));
        List<SlotType1> classificationObj3Slots = new ArrayList<>();
        classificationObj3Slots.add(classificationObj3Slot1);
        InternationalStringType name3 = buildInternationalStringType(new ArrayList<>(Collections.singleton("Normal")));
        ClassificationType classificationObject3 = buildClassificationObject(
                "",
                "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "ConfidentialityLevel_1",
                name3,
                classificationObj3Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "N");

        // Slots 4
        SlotType1 classificationObj4Slot1 = buildSlotObject(
                "codingScheme",
                null,
                new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.6")));
        List<SlotType1> classificationObj4Slots = new ArrayList<>();
        classificationObj4Slots.add(classificationObj4Slot1);
        InternationalStringType name4 = buildInternationalStringType(new ArrayList<>(Collections.singleton("Prescrizione SistemaTS")));
        ClassificationType classificationObject4 = buildClassificationObject(
                null,
                "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "FormatCode_1",
                name4,
                classificationObj4Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "SistemaTS-Prescrizione");

        // Slots 5
        SlotType1 classificationObj5Slot1 = buildSlotObject(
                "codingScheme",
                null,
                new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.1")));
        List<SlotType1> classificationObj5Slots = new ArrayList<>();
        classificationObj5Slots.add(classificationObj5Slot1);
        InternationalStringType name5 = buildInternationalStringType(new ArrayList<>(Collections.singleton("SistemaTS")));
        ClassificationType classificationObject5 = buildClassificationObject(
                null,
                "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "healthcareFacilityTypeCode_1",
                name5,
                classificationObj5Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "SistemaTS");

        // Slots 6
        SlotType1 classificationObj6Slot1 = buildSlotObject(
                "codingScheme",
                null,
                new ArrayList<>(Collections.singleton("2.16.840.1.113883.2.9.3.3.6.1.2")));
        List<SlotType1> classificationObj6Slots = new ArrayList<>();
        classificationObj6Slots.add(classificationObj6Slot1);
        InternationalStringType name6 = buildInternationalStringType(new ArrayList<>(Collections.singleton("Medicina di Base")));
        ClassificationType classificationObject6 = buildClassificationObject(
                null,
                "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "practiceSettingCode_1",
                name6,
                classificationObj6Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "AD_PSC130");

        // Slots 7
        SlotType1 classificationObj7Slot1 = buildSlotObject(
                "codingScheme",
                null,
                new ArrayList<>(Collections.singleton("2.16.840.1.113883.6.1")));
        List<SlotType1> classificationObj7Slots = new ArrayList<>();
        classificationObj7Slots.add(classificationObj7Slot1);
        InternationalStringType name7 = buildInternationalStringType(
                new ArrayList<>(Collections.singleton("Prescrizione diagnostica o specialistica")));
        ClassificationType classificationObject7 = buildClassificationObject(
                null,
                "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",
                "urn:uuid:c2cd598a-da40-4e2c-b879-9dd54e0592ba",
                "typeCode_1",
                name7,
                classificationObj7Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "57832-8");

        extrinsicObject.getClassification().add(classificationObject1);
        extrinsicObject.getClassification().add(classificationObject2);
        extrinsicObject.getClassification().add(classificationObject3);
        extrinsicObject.getClassification().add(classificationObject4);
        extrinsicObject.getClassification().add(classificationObject5);
        extrinsicObject.getClassification().add(classificationObject6);
        extrinsicObject.getClassification().add(classificationObject7);
    }

    /**
     * COMPLETE
     *
     * @param names
     * @return
     */
    private static InternationalStringType buildInternationalStringType(List<String> names) {
        InternationalStringType internationalStringObject = new InternationalStringType();
        List<LocalizedStringType> localizedStringsList = new ArrayList<>();
        for (String name : names) {
            LocalizedStringType localizedStringObject = new LocalizedStringType();
            localizedStringObject.setValue(name);
            localizedStringsList.add(localizedStringObject);
        }
        internationalStringObject.getLocalizedString().addAll(localizedStringsList);
        return internationalStringObject;
    }

    /**
     * COMPLETE
     *
     * @param id
     * @param objectType
     * @param status
     * @param name
     * @param description
     * @param qname
     * @return
     */
    private static RegistryPackageType buildRegistryPackageObject(
            String id,
            String objectType,
            String status,
            InternationalStringType name,
            InternationalStringType description,
            QName qname) {
        RegistryPackageType registryPackageObject = new RegistryPackageType();
        registryPackageObject.setId(id);
        registryPackageObject.setObjectType(objectType);
        registryPackageObject.setStatus(status);
        registryPackageObject.setName(name);
        registryPackageObject.setDescription(description);
        RegistryObjectListType registryPackageObjectList = buildRegistryPackageObjectList(qname);
        registryPackageObject.setRegistryObjectList(registryPackageObjectList);
        return registryPackageObject;
    }

    /**
     * COMPLETE
     * ///TODO: Check if number of nested object is variable
     * @param qname
     * @return
     */
    private static RegistryObjectListType buildRegistryPackageObjectList(QName qname) {
        RegistryObjectListType registryPackageObjectList = new RegistryObjectListType();
        // Slot
        String slotName = "submissionTime";
        String slotType = null;
        List<String> slotValues = new ArrayList<>();
        SlotType1 slotObject = buildSlotObject(slotName, slotType, slotValues);

        ///TODO: Check if number of nested object is variable
        // Classification objects
        SlotType1 classificationObj1Slot1 = buildSlotObject("authorInstitution", null, new ArrayList<>(Collections.singleton("GENOVESE^^^^^&amp;2.16.840.1.113883.2.9.4.1.3&amp;ISO^^^^070103")));
        SlotType1 classificationObj1Slot2 = buildSlotObject("authorPerson", null, Collections.singletonList("LRSMRA51P54F205D^^^^^^^^&amp;2.16.840.1.113883.2.9.4.3.2&amp;ISO"));
        SlotType1 classificationObj1Slot3 = buildSlotObject("authorRole", null, Collections.singletonList("APR"));
        List<SlotType1> classificationObj1Slots = new ArrayList<>();
        classificationObj1Slots.add(classificationObj1Slot1);
        classificationObj1Slots.add(classificationObj1Slot2);
        classificationObj1Slots.add(classificationObj1Slot3);
        ClassificationType classificationObject1 = buildClassificationObject(
                null,
                "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d",
                "SubmissionSet01",
                "SubmissionSet01_ClassificationAuthor",
                null,
                classificationObj1Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "");
        InternationalStringType name2 = buildInternationalStringType(Collections.singletonList("Documenti sistema TS"));
        SlotType1 classificationObj2Slot1 = buildSlotObject("codingScheme", null, Collections.singletonList("2.16.840.1.113883.2.9.3.3.6.1.4"));
        List<SlotType1> classificationObj2Slots = new ArrayList<>();
        classificationObj2Slots.add(classificationObj2Slot1);
        ClassificationType classificationObject2 = buildClassificationObject(
                null,
                "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",
                "SubmissionSet01",
                "SubmissionSet01_ClinicalActivity",
                name2,
                classificationObj2Slots,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification",
                "SistemaTS");

        // External identifier objects
        ExternalIdentifierType externalIdentifierObject1 = buildExternalIdentifierObject(
                "XDSSubmissionSet.patientId",
                "SubmissionSet01_PatientId",
                "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446",
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
                "SubmissionSet01",
                "XXXRND78C60Z222F^^^&amp;2.16.840.1.113883.2.9.4.3.2&amp;ISO"
        );
        ExternalIdentifierType externalIdentifierObject2 = buildExternalIdentifierObject(
                "XDSSubmissionSet.sourceId",
                "SubmissionSet01_SourceId",
                "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
                "SubmissionSet01",
                "2.16.840.1.113883.2.9.2.0"
        );
        ExternalIdentifierType externalIdentifierObject3 = buildExternalIdentifierObject(
                "XDSSubmissionSet.uniqueId",
                "SubmissionSet01_UniqueId",
                "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8",
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier",
                "SubmissionSet01",
                "2.16.840.1.113883.2.9.2.0.4.3.967979"
        );

        // Add to package list
        registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, SlotType1.class, slotObject));
        registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ClassificationType.class, classificationObject1));
        registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ClassificationType.class, classificationObject2));
        registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ExternalIdentifierType.class, externalIdentifierObject1));
        registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ExternalIdentifierType.class, externalIdentifierObject2));
        registryPackageObjectList.getIdentifiable().add(convertToJAXBElement(qname, ExternalIdentifierType.class, externalIdentifierObject3));
        return registryPackageObjectList;
    }

    /**
     * COMPLETE
     *
     * @param name
     * @param id
     * @param identificationScheme
     * @param objectType
     * @param registryObject
     * @param value
     * @return
     */
    private static ExternalIdentifierType buildExternalIdentifierObject(
            String name,
            String id,
            String identificationScheme,
            String objectType,
            String registryObject,
            String value) {
        ExternalIdentifierType externalIdentifierObject = new ExternalIdentifierType();
        InternationalStringType nameType = new InternationalStringType();
        LocalizedStringType localizedString = new LocalizedStringType();
        localizedString.setValue(name);
        nameType.getLocalizedString().add(localizedString);
        externalIdentifierObject.setName(nameType);
        externalIdentifierObject.setId(id);
        externalIdentifierObject.setIdentificationScheme(identificationScheme);
        externalIdentifierObject.setObjectType(objectType);
        externalIdentifierObject.setRegistryObject(registryObject);
        externalIdentifierObject.setValue(value);
        return externalIdentifierObject;
    }

    /**
     * COMPLETE
     *
     * @param associationType
     * @param id
     * @param sourceObject
     * @param targetObject
     * @param slots
     * @return
     */
    private static AssociationType1 buildAssociationObject(
            String associationType,
            String id,
            String sourceObject,
            String targetObject,
            List<SlotType1> slots) {
        AssociationType1 associationObject = new AssociationType1();
        associationObject.setAssociationType(associationType);
        associationObject.setId(id);
        associationObject.setSourceObject(sourceObject);
        associationObject.setTargetObject(targetObject);
        if (slots != null) {
            associationObject.getSlot().addAll(slots);
        }
        return associationObject;
    }

    /**
     * COMPLETE
     *
     * @param classificationNode
     * @param classificationScheme
     * @param classifiedObject
     * @param id
     * @param name
     * @param slots
     * @param objectType
     * @param nodeRepresentation
     * @return
     */
    private static ClassificationType buildClassificationObject(
            String classificationNode,
            String classificationScheme,
            String classifiedObject,
            String id,
            InternationalStringType name,
            List<SlotType1> slots,
            String objectType,
            String nodeRepresentation) {
        ClassificationType classificationObject = new ClassificationType();
        classificationObject.setClassificationNode(classificationNode);
        classificationObject.setClassificationScheme(classificationScheme);
        classificationObject.setClassifiedObject(classifiedObject);
        classificationObject.setId(id);
        classificationObject.setName(name);
        if (slots != null) {
            classificationObject.getSlot().addAll(slots);
        }
        classificationObject.setObjectType(objectType);
        classificationObject.setNodeRepresentation(nodeRepresentation);
        return classificationObject;
    }

    /**
     * COMPLETE
     *
     * @param name
     * @param type
     * @param values
     * @return
     */
    private static SlotType1 buildSlotObject(
            String name,
            String type,
            List<String> values
    ) {
        SlotType1 slotObject = new SlotType1();
        slotObject.setName(name);
        slotObject.setSlotType(type);
        ValueListType valueList = new ValueListType();
        for (String value : values) {
            valueList.getValue().add(value);
        }
        slotObject.setValueList(valueList);
        return slotObject;
    }

    /**
     * COMPLETE
     *
     * @param inputClass
     * @param inputObject
     * @return
     * @param <T>
     */
    public static <T> JAXBElement convertToJAXBElement(QName qName, Class<?> inputClass, T inputObject) {
        JAXBElement jaxbElement = new JAXBElement(qName, inputClass, inputObject);
        return jaxbElement;
    }
}
