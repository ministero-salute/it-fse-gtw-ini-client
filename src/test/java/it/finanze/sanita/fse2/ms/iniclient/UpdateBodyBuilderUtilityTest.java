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
package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ExternalIdentifierEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.MergeMetadatoNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.SubmissionSetEntryBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.UpdateBodyBuilderUtility;
import lombok.var;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateBodyBuilderUtilityTest {

    private RegistryObjectListType oldMetadata;
    private MergedMetadatiRequestDTO newMetadataDTO;
    private PublicationMetadataReqDTO publicationMetadata;
    private JWTTokenDTO jwtTokenDTO;
    private String uuid;
    private String idDocumento;

    private static MockedStatic<StringUtility> stringUtilityMock;
    private static MockedStatic<SamlBodyBuilderCommonUtility> samlBodyMock;
    private static MockedStatic<SubmissionSetEntryBuilderUtility> submissionSetMock;

    @BeforeAll
    static void setUpAll() {
        stringUtilityMock = mockStatic(StringUtility.class);
        samlBodyMock = mockStatic(SamlBodyBuilderCommonUtility.class, CALLS_REAL_METHODS);
        submissionSetMock = mockStatic(SubmissionSetEntryBuilderUtility.class, CALLS_REAL_METHODS);
    }

    @AfterAll
    static void tearDownAll() {
        stringUtilityMock.close();
        samlBodyMock.close();
        submissionSetMock.close();
    }

    @BeforeEach
    void setUp() {
        oldMetadata = new RegistryObjectListType();
        newMetadataDTO = mock(MergedMetadatiRequestDTO.class);
        publicationMetadata = mock(PublicationMetadataReqDTO.class);
        jwtTokenDTO = mock(JWTTokenDTO.class);
        uuid = "uuid-test";
        idDocumento = "doc-test";

        when(newMetadataDTO.getBody()).thenReturn(publicationMetadata);

        stringUtilityMock.when(StringUtility::generateUUID).thenReturn("generated-uuid");

        RegistryPackageType registrypackage = new RegistryPackageType();
        JAXBElement<RegistryPackageType> registrypackageElement = mock(JAXBElement.class);
        when(registrypackageElement.getValue()).thenReturn(registrypackage );
        submissionSetMock.when(() ->
                SubmissionSetEntryBuilderUtility.buildRegistryPackageObjectSubmissionSet(eq(publicationMetadata), any(), anyString(), any())
        ).thenReturn(registrypackageElement);
    }

    @Test
    void testFindClassificationById() {
        ExtrinsicObjectType extrinsic = new ExtrinsicObjectType();
        ClassificationType classif = new ClassificationType();
        classif.setClassificationScheme("urn:uuid:test");
        extrinsic.getClassification().add(classif);

        Optional<ClassificationType> found = UpdateBodyBuilderUtility.findClassificationById(extrinsic, "urn:uuid:test");
        assertTrue(found.isPresent());
        assertEquals("urn:uuid:test", found.get().getClassificationScheme());
    }

    @Test
    void testFindClassificationByIdNotFound() {
        ExtrinsicObjectType extrinsic = new ExtrinsicObjectType();
        Optional<ClassificationType> found = UpdateBodyBuilderUtility.findClassificationById(extrinsic, "urn:uuid:notfound");
        assertFalse(found.isPresent());
    }

    @Test
    void testBuildAssociationWithVersionInfo() {
        VersionInfoType versionInfo = new VersionInfoType();
        versionInfo.setVersionName("1.0");
        JAXBElement<AssociationType1> association = callBuildAssociationReflective(versionInfo, "req-uuid");
        assertNotNull(association.getValue());
        assertEquals("urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember", association.getValue().getAssociationType());
    }

    @Test
    void testBuildAssociationWithoutVersionInfo() {
        JAXBElement<AssociationType1> association = callBuildAssociationReflective(null, "req-uuid");
        assertNotNull(association.getValue());
        assertEquals("urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember", association.getValue().getAssociationType());
        assertEquals(1, association.getValue().getSlot().size());
        assertEquals("SubmissionSetStatus", association.getValue().getSlot().get(0).getName());
    }

    @Test
    void testMergeExtrinsicObjectMetadataThrowsExceptionWhenNotFound() {
        assertThrows(MergeMetadatoNotFoundException.class, () -> {
            callMergeExtrinsicObjectMetadataReflective(oldMetadata.getIdentifiable(), publicationMetadata, "some-uuid");
        });
    }

    @Test
    void testPrivateConstructor() throws Exception {
        var constructor = UpdateBodyBuilderUtility.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        assertNotNull(instance);
    }

    private JAXBElement<AssociationType1> callBuildAssociationReflective(VersionInfoType versionInfo, String requestUUID) {
        try {
            var method = UpdateBodyBuilderUtility.class.getDeclaredMethod("buildAssociation", VersionInfoType.class, String.class);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            JAXBElement<AssociationType1> result = (JAXBElement<AssociationType1>) method.invoke(null, versionInfo, requestUUID);
            return result;
        } catch (Exception e) {
            fail("Reflection call failed: " + e.getMessage());
            return null;
        }
    }

    private ExtrinsicObjectType callMergeExtrinsicObjectMetadataReflective(List<JAXBElement<? extends IdentifiableType>> list, PublicationMetadataReqDTO updateRequestBodyDTO, String uuid) {
        try {
            var method = UpdateBodyBuilderUtility.class.getDeclaredMethod("mergeExtrinsicObjectMetadata", List.class, PublicationMetadataReqDTO.class, String.class);
            method.setAccessible(true);
            return (ExtrinsicObjectType) method.invoke(null, list, updateRequestBodyDTO, uuid);
        } catch (Exception e) {
            if (e.getCause() instanceof MergeMetadatoNotFoundException) {
                throw (MergeMetadatoNotFoundException) e.getCause();
            }
            fail("Reflection call failed: " + e.getMessage());
            return null;
        }
    }

}
