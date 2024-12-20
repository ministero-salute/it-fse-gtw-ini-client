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

import com.mongodb.client.result.DeleteResult;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IssuerRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IssuerRepoTest {

    @Autowired
    private IssuerRepo repo;

    @SpyBean
    private MongoTemplate mongo;

    static final String NAME = "issuerTest";
    static final Boolean MOCK = true;

    @BeforeEach
    void setup(){
        IssuerETY entity = new IssuerETY();
        entity.setIssuer(NAME);
        entity.setMock(MOCK);
        mongo.insert(entity);
    }

    @AfterEach
    void remove(){
        Query query = new Query();
        query.addCriteria(Criteria.where(IssuerETY.ISSUER).is(NAME));
        mongo.remove(query, IssuerETY.class);
    }

    @AfterAll
    void removeCollection(){
        mongo.dropCollection(IssuerETY.class);
    }

    @Test
    @DisplayName("Test when findByName find something")
    void findByNameTest(){
        IssuerETY actual = repo.findByName(NAME);

        assertEquals(NAME, actual.getIssuer());
        assertEquals(MOCK, actual.getMock());
    }

    @Test
    @DisplayName("Test when findByName crash")
    void findByNameExceptionTest(){
        doThrow(BusinessException.class).when(mongo).findOne(any(Query.class), eq(IssuerETY.class));
        assertThrows(BusinessException.class, () -> repo.findByName(NAME));
    }

    @Test
    @DisplayName("Test when createIssuer create a entity")
    void createIssuerTest(){
        IssuerETY entity = new IssuerETY();
        entity.setIssuer("test");
        entity.setMock(false);

        String id = repo.createIssuer(entity);

        assertNotNull(id);
    }

    @Test
    @DisplayName("Test when createIssuer crash")
    void createIssuerExceptionTest(){
        doThrow(BusinessException.class).when(mongo).insert(any(IssuerETY.class));
        assertThrows(BusinessException.class, () -> repo.createIssuer(new IssuerETY()));
    }

    @Test
    @DisplayName("Test when removeIssuer remove a entity")
    void removeIssuerTest(){
        Integer dCount = repo.removeByName(NAME);

        assertEquals(1, dCount);
    }

    @Test
    @DisplayName("Test when removeIssuer doesn't found entity to remove")
    void removeIssuerNotExistsTest(){
        Integer dCount = repo.removeByName("test");

        assertEquals(0, dCount);
    }

    @Test
    @DisplayName("Test findRegioneMiddleware - Successful scenario")
    void testFindRegioneMiddleware() {
        // Arrange
        IssuerETY entity = new IssuerETY();
        entity.setEtichettaRegione("RegionLabel");
        entity.setMiddleware(true);
        mongo.insert(entity);

        // Act
        IssuerETY result = repo.findRegioneMiddleware("RegionLabel");

        // Assert
        assertNotNull(result);
        assertEquals("RegionLabel", result.getEtichettaRegione());
        assertTrue(result.getMiddleware());
    }

    @Test
    @DisplayName("Test findRegioneMiddleware - Exception scenario")
    void testFindRegioneMiddlewareException() {
        // Arrange
        doThrow(new RuntimeException("Mongo error")).when(mongo).findOne(any(Query.class), eq(IssuerETY.class));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> repo.findRegioneMiddleware("InvalidRegion"));
        assertEquals("Error while performing isMiddlewareByEtichettaRegione on issuer collection", ex.getMessage());
    }

    @Test
    @DisplayName("Test updateIssuer - Successful update")
    void testUpdateIssuer() {
        // Arrange
        IssuerETY entity = new IssuerETY();
        entity.setIssuer("UpdatedIssuer");
        entity.setMock(false);

        // Act
        String updatedId = repo.updateIssuer(entity);

        // Assert
        assertNotNull(updatedId);
    }

    @Test
    @DisplayName("Test findIssuersCrashProgrm - Find valid issuers")
    void testFindIssuersCrashProgrm() {
        // Arrange
        IssuerETY entity = new IssuerETY();
        entity.setMock(false);
        entity.setEtichettaRegione("RegionLabel");
        entity.setPazienteCf("PazienteCF");
        mongo.insert(entity);

        // Act
        List<IssuerETY> results = repo.findIssuersCrashProgrm();

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals("RegionLabel", results.get(0).getEtichettaRegione());
        assertEquals("PazienteCF", results.get(0).getPazienteCf());
    }

    @Test
    @DisplayName("Test findByFiscalCode - Successful find")
    void testFindByFiscalCode() {
        // Arrange
        IssuerETY entity = new IssuerETY();
        entity.setPazienteCf("FiscalCode123");
        mongo.insert(entity);

        // Act
        IssuerETY result = repo.findByFiscalCode("FiscalCode123");

        // Assert
        assertNotNull(result);
        assertEquals("FiscalCode123", result.getPazienteCf());
    }

    @Test
    @DisplayName("Test findByNomeDocumentRepository - Successful find")
    void testFindByNomeDocumentRepository() {
        // Arrange
        IssuerETY entity = new IssuerETY();
        entity.setNomeDocumentRepository("RepositoryName123");
        mongo.insert(entity);

        // Act
        IssuerETY result = repo.findByNomeDocumentRepository("RepositoryName123");

        // Assert
        assertNotNull(result);
        assertEquals("RepositoryName123", result.getNomeDocumentRepository());
    }

}
