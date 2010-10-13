/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 *  Copyright (c) 2010, Red Hat, Inc. and/or its affiliates or third-party contributors as
 *  indicated by the @author tags or express copyright attribution
 *  statements applied by the authors.  All third-party contributions are
 *  distributed under license by Red Hat, Inc.
 *
 *  This copyrighted material is made available to anyone wishing to use, modify,
 *  copy, or redistribute it subject to the terms and conditions of the GNU
 *  Lesser General Public License, as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this distribution; if not, write to:
 *  Free Software Foundation, Inc.
 *  51 Franklin Street, Fifth Floor
 *  Boston, MA  02110-1301  USA
 */
package org.hibernate.search.engine;

import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.TermVector;

import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * A wrapper class for Lucene parameters needed for indexing.
 *
 * @author Hardy Ferentschik
 * @author Sanne Grinovero
 */
class LuceneOptionsImpl implements LuceneOptions {

	private final boolean storeCompressed;
	private final boolean storeUncompressed;
	private final Index indexMode;
	private final TermVector termVector;
	private final Float boost;
	private final Store storeType;

	public LuceneOptionsImpl(Store store, Index indexMode, TermVector termVector, Float boost) {
		this.indexMode = indexMode;
		this.termVector = termVector;
		this.boost = boost;
		this.storeType = store;
		this.storeCompressed = store.equals( Store.COMPRESS );
		this.storeUncompressed = store.equals( Store.YES );
	}

	public void addFieldToDocument(String name, String indexedString, Document document) {
		//Do not add fields on empty strings, seems a sensible default in most situations
		if ( StringHelper.isNotEmpty( indexedString ) ) {
			if ( !( indexMode.equals( Index.NO ) && storeCompressed ) ) {
				standardFieldAdd( name, indexedString, document );
			}
			if ( storeCompressed ) {
				compressedFieldAdd( name, indexedString, document );
			}
		}
	}

	private void standardFieldAdd(String name, String indexedString, Document document) {
		Field field = new Field(
				name, false, indexedString, storeUncompressed ? Field.Store.YES : Field.Store.NO, indexMode, termVector
		);
		if ( boost != null ) {
			field.setBoost( boost );
		}
		document.add( field );
	}

	private void compressedFieldAdd(String name, String indexedString, Document document) {
		byte[] compressedString = CompressionTools.compressString( indexedString );
		// indexed is implicitly set to false when using byte[]
		Field field = new Field( name, compressedString, Field.Store.YES );
		document.add( field );
	}

	public Float getBoost() {
		if ( boost != null ) {
			return boost;
		}
		else {
			return 1.0f;
		}
	}

	public boolean isCompressed() {
		return storeCompressed;
	}

	public Index getIndex() {
		return this.indexMode;
	}

	public org.apache.lucene.document.Field.Store getStore() {
		if ( storeUncompressed || storeCompressed ) {
			return org.apache.lucene.document.Field.Store.YES;
		}
		else {
			return org.apache.lucene.document.Field.Store.NO;
		}
	}

	public TermVector getTermVector() {
		return this.termVector;
	}

	/**
	 * Might be useful for a bridge implementation, but not currently part
	 * of LuceneOptions API as we are considering to remove the getters.
	 */
	public Store getStoreStrategy() {
		return storeType;
	}
}
