package it.finanze.sanita.fse2.ms.iniclient.utility;

import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

public class ResponseUtility {

    private ResponseUtility() {}

    /**
     * Check if error response
     * @param res
     * @return
     */
    public static boolean isErrorResponse(RegistryResponseType res) {
        return res != null && res.getRegistryErrorList() != null && res.getRegistryErrorList().getRegistryError() != null &&
                !res.getRegistryErrorList().getRegistryError().isEmpty();
    }

    public static boolean doesRecordGetResponseExist(AdhocQueryResponse response) {
        boolean doesRegistryObjectExist = response.getRegistryObjectList() != null;
        boolean doesIdentifiableExist = response.getRegistryObjectList().getIdentifiable() != null && !response.getRegistryObjectList().getIdentifiable().isEmpty();
        boolean doesValueExist = response.getRegistryObjectList().getIdentifiable().get(0).getValue() != null;
        boolean doesIdExist = response.getRegistryObjectList().getIdentifiable().get(0).getValue().getId() != null;
        return doesRegistryObjectExist && doesIdentifiableExist && doesValueExist && doesIdExist;
    }
}
