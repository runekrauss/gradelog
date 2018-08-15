package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Question}.
 * 
 * @author Marco Glander
 */
@Stateless
public class QuestionDAO extends JPADAO<Question> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = -777656017218825371L;

	/**
	 * Fügt {@code question} dem Datenbestand hinzu. Falls {@code question}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param question
	 *            Das zu speichernde {@link Question}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code question == null} oder {@code question} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(Question question) {
		assertNotNull(question);
		try {
			super.create(question);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code question} im Datenbestand. Falls
	 * {@code question} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param question
	 *            Das zu speichernde {@link Question}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code question == null} oder {@code question} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void update(Question question) {
		assertNotNull(question);
		try {
			super.update(question);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Question-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Question question) {
		super.delete(question);
	}

	public Class<Question> getClazz() {
		return Question.class;
	}

	public Question getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Question.class, id);
	}

	/**
	 * Gibt eine Liste mit allen innerhalb der Applikation bekannten Fragen
	 * zurück.
	 *
	 * @return Liste mit allen innerhalb der Applikation bekannten Fragen.
	 */
	public List<Question> getAllQuestions() {
		return getEntityManager().createNamedQuery("Question.findAll", getClazz()).getResultList();
	}
	
	/**
	 * Gibt eine Liste aller verbleibenden Fragen zurücj.
	 *
	 * @return Liste mit allen innerhalb der Applikation bekannten Fragen.
	 */
	public List<Question> getAllRemainingQuestions() {
		return getEntityManager().createNamedQuery("Question.findAllRemaining", getClazz()).getResultList();
	}
}
