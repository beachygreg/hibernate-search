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
package org.hibernate.search.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * A wrapper class for Lucene parameters needed for indexing.
 *
 * @author Emmanuel Bernard
 * @author Sanne Grinovero
 * @author Hardy Ferentschik
 */
public interface LuceneOptions {

	/**
	 * Add a new field with the name {@code fieldName} to the Lucene Document {@code document} using the value
	 * {@code indexedString}.
	 *
	 * @param fieldName The field name
	 * @param indexedString The value to index
	 * @param document the document to which to add the the new field
	 */
	void addFieldToDocument(String fieldName, String indexedString, Document document);

	/**
	 * @return {@code true} if the field value is compressed, {@code false} otherwise.
	 */
	boolean isCompressed();

	/**
	 * Might be removed in version 3.3 to better support Lucene 3
	 * which is missing COMPRESS Store Type.
	 * To use compression either use #addFieldToDocument or refer
	 * to Lucene documentation to implement your own compression
	 * strategy.
	 *
	 * @deprecated use addToDocument to add fields to the Document if possible
	 */
	Field.Store getStore();

	/**
	 * @deprecated likely to be removed in version 3.3, use #addFieldToDocument
	 */
	Field.Index getIndex();

	/**
	 * @deprecated likely to be removed in version 3.3, use #addFieldToDocument
	 */
	Field.TermVector getTermVector();

	/**
	 * @deprecated likely to be removed in version 3.3, use #addFieldToDocument
	 */
	Float getBoost();
}
