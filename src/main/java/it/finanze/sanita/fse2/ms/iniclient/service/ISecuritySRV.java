/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.service;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

public interface ISecuritySRV {
    SSLContext createSslCustomContext() throws NoSuchAlgorithmException;
}
