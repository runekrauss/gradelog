package de.unibremen.gradelog.persistence;

import java.util.List;

import javax.ejb.Stateless;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Representation}.
 * 
 * @author Marco Glander
 */
@Stateless
public class RepresentationDAO extends JPADAO<Representation> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 1978902975092614311L;

	/**
	 * Fügt {@code representation} dem Datenbestand hinzu. Falls
	 * {@code representation} bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param representation
	 *            Das zu speichernde {@link Representation}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code representation == null} oder
	 *             {@code representation} kein durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(Representation representation) {
		try {
			super.create(representation);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code representation} im Datenbestand.
	 * Falls {@code representation} noch nicht im Datenbestand vorhanden ist,
	 * wird eine {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param representation
	 *            Das zu speichernde {@link Representation}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code representation == null} oder
	 *             {@code representation} kein durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void update(Representation representation) {
		try {
			super.update(representation);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Representation-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Representation representation) {
		super.delete(representation);
	}

	public Class<Representation> getClazz() {
		return Representation.class;
	}

	/**
	 * Liefert eine Liste mit allen Representations, die in der Datenbank
	 * existieren.
	 * 
	 * @return Liste aller bekannten Representations
	 */
	public List<Representation> getAllRepresentations() {
		return getEntityManager()
				.createNamedQuery("Representation.findAll", getClazz())
				.getResultList();
	}

	/**
	 * Leert die Liste der Vertretungsplaneinträge. Wird verwendet, wenn ein
	 * neuer Vertretungsplan importiert wird.
	 */
	public void clearRepresentations() {
		getEntityManager().createNamedQuery("Representation.clear", getClazz()).executeUpdate();
	}
}
