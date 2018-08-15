package de.unibremen.gradelog.persistence;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Message}.
 * 
 * @author Marco Glander
 */
@Stateless
public class MessageDAO extends JPADAO<Message> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = -7755086023909641999L;

	/**
	 * Fügt {@code message} dem Datenbestand hinzu. Falls {@code message}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param message
	 *            Das zu speichernde {@link Message}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code message == null} oder {@code message} kein durch
	 *             JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(Message message) {
		try {
			super.create(message);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code message} im Datenbestand. Falls
	 * {@code message} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param message
	 *            Das zu speichernde {@link Message}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code message == null} oder {@code message} kein durch
	 *             JPA verwaltetes Objekt ist.
	 */
	public synchronized void update(Message message) {
		try {
			super.update(message);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Message-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Message message) {
		super.delete(message);
	}

	public Class<Message> getClazz() {
		return Message.class;
	}

	public Message getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Message.class, id);
	}
}
