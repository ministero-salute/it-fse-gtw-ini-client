/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient.utility.common;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SamlBodyBuilderCommonUtility {

    private SamlBodyBuilderCommonUtility() {}
    
    private static ObjectFactory objectFactory = new ObjectFactory();

    /**
    *
    * @param names
    * @return
    */
   public static InternationalStringType buildInternationalStringType(String name) {
      return buildInternationalStringType(Arrays.asList(name));
   }
   
    /**
     *
     * @param names
     * @return
     */
    public static InternationalStringType buildInternationalStringType(List<String> names) {
        InternationalStringType internationalStringObject = new InternationalStringType();
        try {
            List<LocalizedStringType> localizedStringsList = new ArrayList<>();
            for (String name : names) {
                LocalizedStringType localizedStringObject = new LocalizedStringType();
                localizedStringObject.setValue(name);
                localizedStringsList.add(localizedStringObject);
            }
            internationalStringObject.getLocalizedString().addAll(localizedStringsList);
            return internationalStringObject;
        } catch (Exception e) {
            log.error("Error while invoking buildInternationalStringType: {}", e.getMessage());
            throw new BusinessException("Error while invoking buildInternationalStringType: " + e.getMessage());
        }
    }

    /**
     *
     * @param name
     * @param id
     * @param identificationScheme
     * @param objectType
     * @param registryObject
     * @param value
     * @return
     */
    public static ExternalIdentifierType buildExternalIdentifierObject(
            String name,
            String id,
            String identificationScheme,
            String objectType,
            String registryObject,
            String value
    ) {
        ExternalIdentifierType externalIdentifierObject = new ExternalIdentifierType();

        try {
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
        } catch (Exception e) {
            log.error("Error while invoking buildExternalIdentifierObject: {}", e.getMessage());
            throw new BusinessException("Error while invoking buildExternalIdentifierObject: " + e.getMessage());
        }
    }
    
    /**
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
   public static ClassificationType buildClassificationObject(String classificationScheme,String classifiedObject,String id,
           InternationalStringType name,String nodeRepresentation,SlotType1... slot) {
	   
	   return buildClassificationObject(null,classificationScheme,classifiedObject,id,
	            name,Arrays.asList(slot),Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,nodeRepresentation);
   }

    /**
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
   public static ClassificationType buildClassificationObject(String classificationScheme,String classifiedObject,String id,
           InternationalStringType name,SlotType1 slot,String nodeRepresentation) {
	   
	   return buildClassificationObject(null,classificationScheme,classifiedObject,id,
	            name,Arrays.asList(slot),Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN,nodeRepresentation);
   }
    /**
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
    public static ClassificationType buildClassificationObject(String classificationNode,
            String classificationScheme,String classifiedObject,String id,
            InternationalStringType name,List<SlotType1> slots,String objectType,String nodeRepresentation) {
        ClassificationType classificationObject = new ClassificationType();
        try {
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
        } catch (Exception e) {
            log.error("Error while invoking buildClassificationObject: {}", e.getMessage());
            throw new BusinessException("Error while invoking buildClassificationObject: " + e.getMessage());
        }
    }

    public static SlotType1 buildSlotObject(String name,String value) {
    	return buildSlotObject(name,null, Arrays.asList(value));
    }
    
    /**
     *
     * @param name
     * @param type
     * @param values
     * @return
     */
    public static SlotType1 buildSlotObject(String name,String type,List<String> values) {
        SlotType1 slotObject = new SlotType1();
        try {
            slotObject.setName(name);
            slotObject.setSlotType(type);
            ValueListType valueList = new ValueListType();
            if(values!=null) {
            	for (String value : values) {
            		valueList.getValue().add(value);
            	}
            }
            slotObject.setValueList(valueList);

            return slotObject;
        } catch (Exception e) {
            log.error("Error while invoking buildSlotObject: {}", e.getMessage());
            throw new BusinessException("Error while invoking buildSlotObject: " + e.getMessage());
        }
    }

    /**
     *
     * @param uuid
     * @return
     */
    public static ObjectRefListType buildObjectRefList(String uuid) {
        ObjectRefListType objectRefListType = new ObjectRefListType();

        try {
            ObjectRefType objectRef = new ObjectRefType();
            objectRef.setId(uuid);
            objectRefListType.getObjectRef().add(objectRef);
            return objectRefListType;
        } catch(Exception ex) {
            log.error("Error while perform build registry object list : " , ex);
            throw new BusinessException("Error while perform build registry object list : " , ex);
        }
    }

    /**
     * @param objectFactory
     * @param name
     * @param id
     * @param identificationScheme
     * @param objectType
     * @param registryObject
     * @param value
     * @return
     */
    public static JAXBElement<ExternalIdentifierType> buildExternalIdentifierObjectJax(
            ObjectFactory objectFactory,
            String name,
            String id,
            String identificationScheme,
            String objectType,
            String registryObject,
            String value
    ) {
        try {
            return objectFactory.createExternalIdentifier(buildExternalIdentifierObject(name, id, identificationScheme, objectType, registryObject, value));
        } catch (Exception e) {
            log.error("Error while invoking buildExternalIdentifierObjectJax: {}", e.getMessage());
            throw new BusinessException("Error while invoking buildExternalIdentifierObjectJax: " + e.getMessage());
        }
    }

    /**
     * @param objectFactory
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
    public static JAXBElement<ClassificationType> buildClassificationObjectJax(
            String classificationNode,
            String classificationScheme,
            String classifiedObject,
            String id,
            InternationalStringType name,
            List<SlotType1> slots,
            String objectType,
            String nodeRepresentation) {
        try {
            return objectFactory.createClassification(buildClassificationObject(classificationNode, classificationScheme,
                    classifiedObject, id, name, slots, objectType, nodeRepresentation));
        } catch (Exception e) {
            log.error("Error while invoking buildClassificationObjectJax: {}", e.getMessage());
            throw new BusinessException("Error while invoking buildClassificationObjectJax: " + e.getMessage());
        }
    }

    /**
     * @param objectFactory
     * @param name
     * @param type
     * @param values
     * @return
     */
    public static JAXBElement<SlotType1> buildSlotObjectJax(
            ObjectFactory objectFactory,
            String name,
            String type,
            List<String> values
    ) {
        try {
            return objectFactory.createSlot(buildSlotObject(name, type, values));
        } catch (Exception e) {
            log.error("Error while invoking buildSlotObjectJax: {}", e.getMessage());
            throw new BusinessException("Error while invoking buildSlotObjectJax: " + e.getMessage());
        }
    }

    /**
    *
    * @param associationType
    * @param id
    * @param sourceObject
    * @param targetObject
    * @return
    */
   public static JAXBElement<AssociationType1> buildAssociationObject(String associationType,String id,String sourceObject,String targetObject) {
	   return buildAssociationObject(associationType,id,sourceObject,targetObject,null);
   }
    /**
     *
     * @param associationType
     * @param id
     * @param sourceObject
     * @param targetObject
     * @param slots
     * @return
     */
    public static JAXBElement<AssociationType1> buildAssociationObject(
            String associationType,
            String id,
            String sourceObject,
            String targetObject,
            List<SlotType1> slots
    ) {
        AssociationType1 associationObject = new AssociationType1();
        try {
            associationObject.setAssociationType(associationType);
            associationObject.setId(id);
            associationObject.setSourceObject(sourceObject);
            associationObject.setTargetObject(targetObject);
            if (slots != null) {
                associationObject.getSlot().addAll(slots);
            }

            return objectFactory.createAssociation(associationObject);
        } catch (Exception e) {
            log.error("Error while invoking buildAssociationObject: {}", e.getMessage());
            throw new BusinessException("Error while invoking buildAssociationObject: " + e.getMessage());
        }
    }
}
