//$Id$
package org.hibernate.search.event;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.AbstractEvent;
import org.hibernate.event.Initializable;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.search.backend.WorkType;
import org.hibernate.search.backend.Work;
import org.hibernate.search.engine.DocumentBuilder;
import org.hibernate.search.engine.SearchFactoryImplementor;
import org.hibernate.search.impl.SearchFactoryImpl;
import org.hibernate.search.Environment;
import org.hibernate.search.SearchException;

/**
 * This listener supports setting a parent directory for all generated index files.
 * It also supports setting the analyzer class to be used.
 *
 * @author Gavin King
 * @author Emmanuel Bernard
 * @author Mattias Arbin
 */
//TODO work on sharing the same indexWriters and readers across a single post operation...
//TODO implement and use a LockableDirectoryProvider that wraps a DP to handle the lock inside the LDP
public class FullTextIndexEventListener implements PostDeleteEventListener, PostInsertEventListener,
		PostUpdateEventListener, Initializable {

	protected boolean used;
	protected SearchFactoryImplementor searchFactoryImplementor;

	public void initialize(Configuration cfg) {
		searchFactoryImplementor = SearchFactoryImpl.getSearchFactory( cfg );
		String indexingStrategy = cfg.getProperties().getProperty( Environment.INDEXING_STRATEGY, "event" );
		if ( "event".equals( indexingStrategy ) ) {
			used = searchFactoryImplementor.getDocumentBuilders().size() != 0;
		}
		else if ( "manual".equals( indexingStrategy ) ) {
			used = false;
		}
		else {
			throw new SearchException(Environment.INDEXING_STRATEGY + " unknown: " + indexingStrategy);
		}
	}

	public SearchFactoryImplementor getSearchFactoryImplementor() {
		return searchFactoryImplementor;
	}

	public void onPostDelete(PostDeleteEvent event) {
		if ( used && searchFactoryImplementor.getDocumentBuilders().containsKey( event.getEntity().getClass() ) ) {
			processWork( event.getEntity(), event.getId(), WorkType.DELETE, event );
		}
	}

	public void onPostInsert(PostInsertEvent event) {
		if (used) {
			final Object entity = event.getEntity();
			DocumentBuilder<Object> builder = searchFactoryImplementor.getDocumentBuilders().get( entity.getClass() );
			//not strictly necessary but a smal optimization
			if ( builder != null ) {
				Serializable id = event.getId();
				processWork( entity, id, WorkType.ADD, event );
			}
		}
	}

	public void onPostUpdate(PostUpdateEvent event) {
		if (used) {
			final Object entity = event.getEntity();
			//not strictly necessary but a smal optimization
			DocumentBuilder<Object> builder = searchFactoryImplementor.getDocumentBuilders().get( entity.getClass() );
			if ( builder != null ) {
				Serializable id = event.getId();
				processWork( entity, id, WorkType.UPDATE, event );
			}
		}
	}

	protected void processWork(Object entity, Serializable id, WorkType workType, AbstractEvent event) {
		Work work = new Work(entity, id, workType);
		searchFactoryImplementor.getWorker().performWork( work, event.getSession() );
	}
}
