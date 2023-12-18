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
package it.finanze.sanita.fse2.ms.iniclient.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;

/**
 * The Class FileUtils.
 *
 *
 * Utility to manager file.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtility {
 
	/**
	 * Max size chunk.
	 */
	private static final int CHUNK_SIZE = 16384;

	
	/**
	 * Metodo per il recupero del contenuto di un file dalla folder interna "/src/main/resources".
	 *
	 * @param filename	nome del file
	 * @return			contenuto del file
	 */
	public static byte[] getFileFromInternalResources(final String filename) {
		byte[] b = null;
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)) {
			b = getByteFromInputStream(is);
		} catch (Exception e) {
			log.error("FILE UTILS getFileFromInternalResources(): Errore in fase di recupero del contenuto di un file dalla folder '/src/main/resources'. ", e);
		}
		return b;
	}


//	public static ByteArrayInputStream getFileFromGenericResource(final String path) {
//		InputStream is = null;
//		try {
//			if (path.contains("classpath:")) {
//				String sanitizedPath = path.replace("classpath:", "");
//				is = Thread.currentThread().getContextClassLoader().getResourceAsStream(sanitizedPath);
//			} else {
//				is = Files.newInputStream(Paths.get(path));
//			}
//			return new ByteArrayInputStream(FileUtility.getByteFromInputStream(is));
//		} catch (Exception e) {
//			throw new BusinessException("Errore in fase di recupero contenuto del file:" + e.getMessage());
//		} finally {
//			if (is != null) {
//				try {
//					is.close();
//				} catch (IOException e) {
//					log.error("Error: {}", e.getMessage());
//				}
//			}
//		}
//	}

	/**
	 * Recupero contenuto file da input stream.
	 *
	 * @param is
	 *            input stream
	 * @return contenuto file
	 */
	private static byte[] getByteFromInputStream(final InputStream is) {
		byte[] b;
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[CHUNK_SIZE];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			b = buffer.toByteArray();
		} catch (Exception e) {
			log.error("Errore durante il trasform da InputStream a byte[]: ", e);
			throw new BusinessException(e);
		}
		return b;
	}
}
