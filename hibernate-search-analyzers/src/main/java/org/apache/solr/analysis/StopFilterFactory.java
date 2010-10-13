// original code from Apache Solr - ported to work with Lucene 3.x and reformatted to Search coding style
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.analysis;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.common.ResourceLoader;
import org.apache.solr.util.plugin.ResourceLoaderAware;

/**
 * @version $Id: StopFilterFactory.java 929782 2010-04-01 02:15:27Z rmuir $
 */
public class StopFilterFactory extends BaseTokenFilterFactory implements ResourceLoaderAware {

	@Override
	public void init(Map<String, String> args) {
		super.init( args );
		assureMatchVersion();
	}

	public void inform(ResourceLoader loader) {
		String stopWordFiles = args.get( "words" );
		ignoreCase = getBoolean( "ignoreCase", false );
		enablePositionIncrements = getBoolean( "enablePositionIncrements", false );

		if ( stopWordFiles != null ) {
			try {
				stopWords = getWordSet( loader, stopWordFiles, ignoreCase );
			}
			catch ( IOException e ) {
				throw new RuntimeException( e );
			}
		}
		else {
			stopWords = new CharArraySet( StopAnalyzer.ENGLISH_STOP_WORDS_SET, ignoreCase );
		}
	}

	private CharArraySet stopWords;
	private boolean ignoreCase;
	private boolean enablePositionIncrements;

	public boolean isEnablePositionIncrements() {
		return enablePositionIncrements;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public Set<?> getStopWords() {
		return stopWords;
	}

	public StopFilter create(TokenStream input) {
		StopFilter stopFilter = new StopFilter( enablePositionIncrements, input, stopWords, ignoreCase );
		return stopFilter;
	}
}