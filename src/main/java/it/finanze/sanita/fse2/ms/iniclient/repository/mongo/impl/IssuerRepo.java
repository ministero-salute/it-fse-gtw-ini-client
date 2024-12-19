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
package it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl;

import com.mongodb.client.result.DeleteResult;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIssuerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY.*;

@Repository
@Slf4j
public class IssuerRepo implements IIssuerRepo {

    @Autowired
    private MongoTemplate mongo;

    @Override
    public IssuerETY findByName(String name) {
        IssuerETY out = null;
        try{
            Query query = new Query();
            query.addCriteria(Criteria.where(ISSUER).is(name));
            out = mongo.findOne(query, IssuerETY.class);
        }catch (Exception ex){
            log.error("Error while perform findByName on issuer collection", ex);
            throw new BusinessException("Error while perform findByName on issuer collection", ex);
        }
        return out;
    }

    @Override
    public String createIssuer(IssuerETY issuerETY) {
        String out = null;
        try{ 
            IssuerETY issuer = mongo.insert(issuerETY);
            out = issuer.getId();
        }catch (Exception ex){
            log.error(ex.getMessage(), ex);
            throw new BusinessException(ex.getMessage(), ex);
        }
        return out;
    }

    @Override
    public Integer removeByName(String name) {
        Integer dCount = 0;
        try{
            Query query = new Query();
            query.addCriteria(Criteria.where(ISSUER).is(name));
            DeleteResult res = mongo.remove(query, IssuerETY.class);
            dCount = (int) res.getDeletedCount();
        }catch (Exception ex){
            log.error("Error while perform removeByName on issuer collection" , ex);
            throw new BusinessException("Error while perform removeByName on issuer collection" , ex);
        }
        return dCount;
    }

    @Override
    public IssuerETY findRegioneMiddleware(String etichettaRegione) {
        IssuerETY res = null;
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(ETICHETTA_REGIONE).is(etichettaRegione));
            query.addCriteria(Criteria.where(MIDDLEWARE).is(true));
            res = mongo.findOne(query, IssuerETY.class);
        } catch (Exception ex){
            log.error("Error while performing isMiddlewareByEtichettaRegione on issuer collection", ex);
            throw new BusinessException("Error while performing isMiddlewareByEtichettaRegione on issuer collection", ex);
        }
        return res;
    }

    @Override
    public String updateIssuer(IssuerETY issuer) {
        return mongo.save(issuer).getId();
    }


    @Override
    public List<IssuerETY> findIssuersCrashProgrm(){
        Query query = new Query();
        query.addCriteria(Criteria.where(MOCK).is(false).and("etichettaRegione").ne(null).and("pazienteCf").ne(null));
        return mongo.find(query, IssuerETY.class);
    }

    @Override
    public IssuerETY findByFiscalCode(String fiscalCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where(PAZIENTE_CF).is(fiscalCode));
        return mongo.findOne(query, IssuerETY.class);
    }

    @Override
    public IssuerETY findByNomeDocumentRepository(String nomeDocumentRepository) {
        Query query = new Query();
        query.addCriteria(Criteria.where(NOME_DOCUMENT_REPOSITORY).is(nomeDocumentRepository));
        return mongo.findOne(query, IssuerETY.class);
    }

}
