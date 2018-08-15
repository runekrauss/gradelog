package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Task}.
 * 
 * @author Rune Krauss
 * @author Marco Glander
 */
@Stateless
public class TaskDAO extends JPADAO<Task> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 2471602382222515387L;

	/**
	 * Fügt {@code task} dem Datenbestand hinzu. Falls {@code task} bereits im
	 * Datenbestand vorhanden ist (vgl. {@link JPADAO#create(JPAEntity)}, wird
	 * eine {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param task
	 *            Das zu speichernde {@link Task}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code task == null} oder {@code task} kein durch JPA
	 *             verwaltetes Objekt ist.
	 */
	public synchronized void create(Task task) {
		assertNotNull(task);
		try {
			super.create(task);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code task} im Datenbestand. Falls
	 * {@code task} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param task
	 *            Das zu speichernde {@link Task}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code task == null} oder {@code task} kein durch JPA
	 *             verwaltetes Objekt ist.
	 */
	public synchronized void update(Task task) {
		assertNotNull(task);
		try {
			super.update(task);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Task-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Task task) {
		super.delete(task);
	}

	public Class<Task> getClazz() {
		return Task.class;
	}

	public Task getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Task.class, id);
	}
}
