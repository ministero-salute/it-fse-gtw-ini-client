package it.finanze.sanita.fse2.ms.iniclient;

import com.mongodb.client.result.DeleteResult;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IssuerRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IssuerRepoTest {

    @Autowired
    private IssuerRepo repo;

    @Autowired
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
    @DisplayName("Test when createIssuer create a entity")
    void createIssuerTest(){
        IssuerETY entity = new IssuerETY();
        entity.setIssuer("test");
        entity.setMock(false);

        String id = repo.createIssuer(entity);

        assertNotNull(id);
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
}
