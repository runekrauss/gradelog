package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Evaluation}.
 * 
 * @author Marco Glander
 */
@Stateless
public class EvaluationDAO extends JPADAO<Evaluation> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 8919168583240893882L;

	/**
	 * Fügt {@code evaluation} dem Datenbestand hinzu. Falls {@code evaluation}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param evaluation
	 *            Das zu speichernde {@link Evaluation}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code evaluation == null} oder {@code evaluation} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(Evaluation evaluation) {
		assertNotNull(evaluation);
		try {
			super.create(evaluation);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code evaluation} im Datenbestand. Falls
	 * {@code evaluation} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param evaluation
	 *            Das zu speichernde {@link Evaluation}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code evaluation == null} oder {@code evaluation} kein durch JPA
	 *             verwaltetes Objekt ist.
	 */
	public synchronized void update(Evaluation evaluation) {
		assertNotNull(evaluation);
		try {
			super.update(evaluation);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Evaluation-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Evaluation evaluation) {
		super.delete(evaluation);
	}

	public Class<Evaluation> getClazz() {
		return Evaluation.class;
	}

	public Evaluation getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Evaluation.class, id);
	}
}
