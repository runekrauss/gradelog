package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.log4j.Logger;

import de.unibremen.gradelog.model.JPAEntity;

/**
 * Listener, der an alle JPA-Entities, die
 * {@link de.unibremen.gradelog.model.JPAEntity} erweitern, angehängt wird um
 * über Ereignisse informiert zu werden. Wird aktuell zum Loggen mit log4j
 * verwendet.
 * 
 * @author Rune Krauss
 *
 */
public class JPAEntityListener {

	/**
	 * Der Logger für diese Klasse.
	 */
	private static final Logger logger = Logger.getLogger(JPAEntityListener.class);

	/**
	 * Wird aufgerufen, bevor eine Entität durch JPA persistiert wird.
	 * 
	 * @param entity
	 *            die zu persistierende Entität
	 */
	@PrePersist
	public void prePersist(final JPAEntity entity) {
		if (logger.isDebugEnabled())
			log("Pre-persist of entity: ", entity);
	}

	/**
	 * Wird aufgerufen, wenn eine Entität durch JPA persistiert wurde.
	 * 
	 * @param entity
	 *            die persistierte Entität
	 */
	@PostPersist
	public void postPersist(final JPAEntity entity) {
		if (logger.isDebugEnabled())
			log("Post-persist of entity: ", entity);
	}

	/**
	 * Wird aufgerufen, bevor eine Entität durch JPA entfernt wird.
	 * 
	 * @param entity
	 *            die zu entfernende Entität
	 */
	@PreRemove
	public void preRemove(final JPAEntity entity) {
		if (logger.isDebugEnabled())
			log("Pre-remove of entity: ", entity);
	}

	/**
	 * Wird aufgerufen, nachdem eine Entität durch JPA entfernt wurde.
	 * 
	 * @param entity
	 *            die entfernte Entität
	 */
	@PostRemove
	public void postRemove(final JPAEntity entity) {
		if (logger.isDebugEnabled())
			log("Post-remove of entity: ", entity);
	}

	/**
	 * Wird aufgerufen, bevor eine Entität durch JPA aktualisiert wird.
	 * 
	 * @param entity
	 *            die zu aktualisierende Entität
	 */
	@PreUpdate
	public void preUpdate(final JPAEntity entity) {
		if (logger.isDebugEnabled())
			log("Pre-Update of entity: ", entity);
	}

	/**
	 * Wird aufgerufen, nachdem eine Entität durch JPA aktualisiert wurde.
	 * 
	 * @param entity
	 *            die aktualisierte Entität
	 */
	@PostUpdate
	public void postUpdate(final JPAEntity entity) {
		if (logger.isDebugEnabled())
			log("Post-update of entity: ", entity);
	}

	/**
	 * Wird aufgerufen, nachdem eine Entität durch JPA eingelesen wurde.
	 * 
	 * @param entity
	 *            die eingelesene Entität
	 */
	@PostLoad
	public void postLoad(final JPAEntity entity) {
		if (logger.isDebugEnabled())
			log("Post-load of entity: ", entity);
	}

	/**
	 * Komfort-Methode zum Erstellen von Log-Einträgen.
	 * 
	 * @param context
	 *            der Kontext des Log-Eintrages; dieser wird vorne an den
	 *            Log-Eintrag angehängt.
	 * @param entity
	 *            die Entität, auf welcher operiert wird.
	 */
	private void log(final String context, final JPAEntity entity) {
		if (logger.isDebugEnabled())
			logger.debug(context + assertNotNull(entity).toString());
	}

}
