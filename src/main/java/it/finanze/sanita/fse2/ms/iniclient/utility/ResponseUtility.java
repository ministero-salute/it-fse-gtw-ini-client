/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.utility;

import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

public class ResponseUtility {

    private ResponseUtility() {}


    public static boolean doesRecordGetResponseExist(AdhocQueryResponse response) {
        boolean doesRegistryObjectExist = response.getRegistryObjectList() != null;
        boolean doesIdentifiableExist = response.getRegistryObjectList().getIdentifiable() != null && !response.getRegistryObjectList().getIdentifiable().isEmpty();
        boolean doesValueExist = response.getRegistryObjectList().getIdentifiable().get(0).getValue() != null;
        boolean doesIdExist = response.getRegistryObjectList().getIdentifiable().get(0).getValue().getId() != null;
        return doesRegistryObjectExist && doesIdentifiableExist && doesValueExist && doesIdExist;
    }
}
