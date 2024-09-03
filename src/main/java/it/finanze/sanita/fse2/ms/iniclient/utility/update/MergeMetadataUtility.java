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
package it.finanze.sanita.fse2.ms.iniclient.utility.update;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildClassificationObject;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildInternationalStringType;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotCodingSchemeObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventCodeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MergeMetadataUtility {

	/**
	 * Merge healthcareFacilityTypeCode for extrinsic object
	 * 
	 * @param updateRequestBodyDTO
	 * @param extrinsicObject
	 */
	public static void mergeHealthcareFacilityTypeCode(PublicationMetadataReqDTO updateRequestBodyDTO,
			ExtrinsicObjectType extrinsicObject) {
		ClassificationEnum healthCare = ClassificationEnum.HEALTH_CARE_FACILITY_TYPE_CODE;
		Map<String, String> value = new HashMap<>();
		if (updateRequestBodyDTO.getAssettoOrganizzativo() != null) {
			String code = updateRequestBodyDTO.getTipologiaStruttura().getCode();
			String description = updateRequestBodyDTO.getTipologiaStruttura().getCode();
			value.put(code, description);
		}
		mergeClassification(healthCare.getCodingScheme(), healthCare.getClassificationScheme(), "Document1",
				"IdHealthcareFacilityTypeCode", extrinsicObject.getClassification(), value);
	}

	/**
	 * Merge classCode for extrinsic object
	 * 
	 * @param updateRequestBodyDTO
	 * @param extrinsicObject
	 */
	public static void mergeClassCode(PublicationMetadataReqDTO updateRequestBodyDTO,
			ExtrinsicObjectType extrinsicObject) {
		ClassificationEnum classCode = ClassificationEnum.CLASS_CODE;
		Map<String, String> value = new HashMap<>();
		if (updateRequestBodyDTO.getAssettoOrganizzativo() != null) {
			String code = updateRequestBodyDTO.getTipoDocumentoLivAlto().getCode();
			String description = updateRequestBodyDTO.getTipoDocumentoLivAlto().getDescription();
			value.put(code, description);
		}
		mergeClassification(classCode.getCodingScheme(), classCode.getClassificationScheme(), "Document1", "ClassCode",
				extrinsicObject.getClassification(), value);
	}

	/**
	 * Merge practiceSettingCode for extrinsic object
	 * 
	 * @param updateRequestBodyDTO
	 * @param extrinsicObject
	 */
	public static void mergePracticeSettingCode(PublicationMetadataReqDTO updateRequestBodyDTO,
			ExtrinsicObjectType extrinsicObject) {
		ClassificationEnum practiceSettingCode = ClassificationEnum.PRACTICE_SETTING_CODE;
		Map<String, String> value = new HashMap<>();
		if (updateRequestBodyDTO.getAssettoOrganizzativo() != null) {
			String code = updateRequestBodyDTO.getAssettoOrganizzativo().getCode();
			String description = updateRequestBodyDTO.getAssettoOrganizzativo().getDescription();
			value.put(code, description);
		}
		mergeClassification(practiceSettingCode.getCodingScheme(), practiceSettingCode.getClassificationScheme(),
				"Document1", "IdPracticeSettingCode", extrinsicObject.getClassification(), value);
	}

	/**
	 * Merge eventTypeCode for extrinsic object
	 * 
	 * @param updateRequestBodyDTO
	 * @param classificationObjectList
	 */
	public static void mergeEventTypeCode(PublicationMetadataReqDTO updateRequestBodyDTO,
			ExtrinsicObjectType extrinsicObject) {
		ClassificationEnum eventCode = ClassificationEnum.EVENT_CODE;

		Map<String, String> value = new HashMap<>();
		if (updateRequestBodyDTO.getAttiCliniciRegoleAccesso() != null) {
			for (String event : updateRequestBodyDTO.getAttiCliniciRegoleAccesso()) {
				EventCodeEnum eventCodeEnum = EventCodeEnum.fromValue(event);
				value.put(eventCodeEnum.getCode(), eventCodeEnum.getDescription());
			}
		}

		mergeClassification(eventCode.getCodingScheme(), eventCode.getClassificationScheme(), "Document1",
				"IdEventCodeList", extrinsicObject.getClassification(), value);
	}

	/**
	 * Merge service start/stop time for extrinsic object
	 * 
	 * @param updateRequestBodyDTO
	 * @param extrinsicObject
	 */
	public static void mergeServiceTime(PublicationMetadataReqDTO updateRequestBodyDTO,
			ExtrinsicObjectType extrinsicObject) {
		mergeSlot("serviceStartTime", extrinsicObject.getSlot(), updateRequestBodyDTO.getDataInizioPrestazione());
		mergeSlot("serviceStopTime", extrinsicObject.getSlot(), updateRequestBodyDTO.getDataFinePrestazione());
	}

	/**
	 * Merge description metadata
	 * 
	 * @param updateRequestBodyDTO
	 * @param slotList
	 */
	public static void mergeDescription(PublicationMetadataReqDTO updateRequestBodyDTO,
			ExtrinsicObjectType extrinsicObject) {
		String[] newValue = updateRequestBodyDTO.getDescription() == null ? null
				: new String[] { updateRequestBodyDTO.getDescription().toArray()[0].toString() };
		mergeSlot("urn:ita:2022:description", extrinsicObject.getSlot(), newValue);
	}

	public static void mergeAdministrativeRequest(PublicationMetadataReqDTO updateRequestBodyDTO,
			ExtrinsicObjectType extrinsicObject) {
		String[] newValues = updateRequestBodyDTO.getAdministrativeRequest() != null
				? new String[updateRequestBodyDTO.getAdministrativeRequest().size()]
				: null;
		if (newValues != null) {
			for (int i = 0; i < updateRequestBodyDTO.getAdministrativeRequest().size(); i++) {
				newValues[i] = updateRequestBodyDTO.getAdministrativeRequest().get(i).getCode() + "^"
						+ updateRequestBodyDTO.getAdministrativeRequest().get(i).getDescription();
			}
		}
		mergeSlot("urn:ita:2022:administrativeRequest", extrinsicObject.getSlot(), newValues);
	}

	private static void mergeSlot(String slotName, List<SlotType1> slotList, String... newValue) {
		try {
			SlotType1 editedSlot = slotList.stream().filter(slot -> slot.getName().equals(slotName)).findFirst()
					.orElse(null);

			if (editedSlot != null) {
				slotList.remove(editedSlot);
			}
			if (newValue != null) {
				editedSlot = new SlotType1();
				editedSlot.setName(slotName);
				ValueListType valueListTime = new ValueListType();
				valueListTime.getValue().addAll(Arrays.asList(newValue));
				editedSlot.setValueList(valueListTime);
				slotList.add(editedSlot);
			}

		} catch (Exception ex) {
			log.error("Error while performing merge for {}: {}", slotName, ex.getMessage());
			throw new BusinessException("Error while performing merge for " + slotName + ": ", ex);
		}
	}

	private static void mergeClassification(String codingScheme, String classificationSchemeName,
			String classifiedObject, String id,
			List<ClassificationType> classificationList, Map<String, String> value) {
		try {
			ClassificationType editedClassification = classificationList.stream()
					.filter(classification -> classification.getClassificationScheme().equals(classificationSchemeName))
					.findFirst().orElse(null);

			if (editedClassification != null) {
				classificationList.remove(editedClassification);
			}

			if (value != null && value.size() > 0) {
				for (Entry<String, String> entry : value.entrySet()) {
					SlotType1 classCodeSlot = buildSlotCodingSchemeObject(codingScheme);
					InternationalStringType nameClassCode = buildInternationalStringType(entry.getValue());
					editedClassification = buildClassificationObject(classificationSchemeName, classifiedObject, id,
							nameClassCode, classCodeSlot, entry.getKey());
					classificationList.add(editedClassification);
				}
			}

		} catch (Exception ex) {
			log.error("Error while performing merge for {}: {}", classificationSchemeName, ex.getMessage());
			throw new BusinessException("Error while performing merge for " + classificationSchemeName + ": ", ex);
		}
	}

}
