/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.enums;

public enum SearchTypeEnum {
	
	OBJECT_REF("ObjectRef"),
	LEAF_CLASS("LeafClass");

	private String searchKey;

	private SearchTypeEnum(String inSearchKey) {
		searchKey = inSearchKey;
	}

	public String getSearchKey() {
		return searchKey;
	}

	
}
