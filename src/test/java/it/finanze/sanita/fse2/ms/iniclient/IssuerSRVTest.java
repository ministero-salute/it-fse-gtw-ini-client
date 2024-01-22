package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerDeleteResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIssuerRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IssuerSRVTest {

    @Autowired
    private IIssuerSRV issuerSRV;

    @MockBean
    private IIssuerRepo repo;

    @Test
    @DisplayName("Test when is mocked")
    void isMockedTest(){
        IssuerETY entity = new IssuerETY();
        Boolean expected = true;
        entity.setIssuer("issuerProva");
        entity.setMock(expected);

        when(repo.findByName(anyString())).thenReturn(entity);

        Boolean actual = issuerSRV.isMocked("issuerProva");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test when issuer is already in the database")
    void createIssuerAlreadyExistsTest(){
        IssuerETY entity = new IssuerETY();
        String expected = "idProva";
        entity.setIssuer("issuerProva");
        entity.setMock(true);
        entity.setId(expected);

        IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
        request.setIssuer("issuerProva");
        request.setMock(true);

        when(repo.findByName(anyString())).thenReturn(entity);
        when(repo.createIssuer(Mockito.any(IssuerETY.class))).thenReturn(expected);

        assertThrows(BusinessException.class,() -> issuerSRV.createIssuer(request));
    }

    @Test
    @DisplayName("Test when id is null")
    void createIssuerIdIsNullTest(){
        IssuerETY entity = new IssuerETY();
        String expected = "idProva";
        entity.setIssuer("issuerProva");
        entity.setMock(true);
        entity.setId(expected);

        IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
        request.setIssuer("issuerProva");
        request.setMock(true);

        when(repo.findByName(anyString())).thenReturn(entity);
        when(repo.createIssuer(Mockito.any(IssuerETY.class))).thenReturn(null);

        assertThrows(BusinessException.class,() -> issuerSRV.createIssuer(request));
    }

    @Test
    @DisplayName("Test when createIssuer return id")
    void createIssuerTest(){
        IssuerETY entity = new IssuerETY();
        String expected = "idProva";
        entity.setIssuer("issuerProva");
        entity.setMock(true);
        entity.setId(expected);

        IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
        request.setIssuer("issuerProva2");
        request.setMock(true);

        when(repo.findByName(anyString())).thenReturn(null);
        when(repo.createIssuer(Mockito.any(IssuerETY.class))).thenReturn(expected);

        IssuerResponseDTO response = issuerSRV.createIssuer(request);
        String actual = response.getId();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test when remove return a count != 0")
    void removeIssuerTest(){
        String name = "test";
        Integer expected = 1;

        when(repo.removeByName(name)).thenReturn(expected);

        IssuerDeleteResponseDTO response = issuerSRV.removeIssuer(name);
        Integer actual = response.getCount();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test when remove return a count == 0")
    void removeIssuerCountIsZeroTest(){
        String name = "test";
        Integer expected = 0;

        when(repo.removeByName(name)).thenReturn(expected);

        assertThrows(NotFoundException.class, () -> issuerSRV.removeIssuer(name));
    }

}
