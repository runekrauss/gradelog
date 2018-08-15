package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TransactionRequiredException;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.model.JPAEntity;

/**
 * Ein DAO das JPA verwendet, um einen über die Laufzeit der Applikation hinaus
 * gültigen Datenbestand zu realisieren.
 *
 * @param <T>
 *            Das durch JPA zu persitierende Datum.
 * 
 * @author Rune Krauss
 */
public abstract class JPADAO<T extends JPAEntity> implements GenericDAO<T> {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -2225632806636989857L;

	/**
	 * Der für den Zugriff auf die Datenquelle verwendete Persistenzkontext.
	 * Kann in der Datei 'resources/META-INF/persistence.xml' konfiguriert
	 * werden. Zudem ist es möglich, mehrere Konfigurationen zu spezifizieren
	 * und die gewünschten Konfiguration über 'unitName' auszuwählen.
	 */
	@PersistenceContext(unitName = "user")
	private EntityManager em;

	/**
	 * Gibt das zu {@link T} zugehörige {@link Class} Objekt zurück. Dieses
	 * Objekt wird benötigt, um einen typsicheren Query mit JPA absetzen zu
	 * können und darf niemals {@code null} sein. Diese Methode wird
	 * beispielsweise von {@link #getById(int)} verwendet.
	 *
	 * @return Das zu {@link T} zugehörige {@link Class} Objekt, niemals
	 *         {@code null}.
	 */
	abstract Class<T> getClazz();

	/**
	 * Gibt den verwendeten Persistenzkontext zurück und erlaubt somit
	 * Unterklassen eigene Queries auf die Datenquelle abzusezten. Darf niemals
	 * {@code null} sein.
	 *
	 * @return Der verwendete Persistenzkontext, niemals {@code null}.
	 */
	EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Gibt das Objekt mit der Id {@code theId} zurück. Da die Id von Objekten
	 * der selben Klasse eindeutig ist, kann es höchstens ein Objekt mit der
	 * gegebenen Id geben. Falls kein Objekt mit der gegebenen Id existiert,
	 * wird {@code null} zurückgegeben.
	 *
	 * @param id
	 *            Die Id des gesuchten Objektes.
	 * @return Das Objekt mit der Id {@code theId} oder {@code null}, falls ein
	 *         solches Objekt im Datenbestand nicht vorhanden ist.
	 * @throws IllegalArgumentException
	 *             Falls {@code theId == null}.
	 */
	public T getById(final int id) {
		return em.find(getClazz(), id);
	}

	/**
	 * Fügt {@code theT} dem Datenbestand hinzu. Falls {@code theT} bereits im
	 * Datenbestand vorhanden ist ({@code theT.getId() != null}), wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 *
	 * @param t
	 *            Das zu persistierende Objekt.
	 * @throws IllegalArgumentException
	 *             Falls {@code theT == null}, {@code theT.getId() != null} oder
	 *             {@code theT} kein durch JPA verwaltetes Objekt ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vgl. {@link EntityManager#persist(Object)}).
	 */
	@Override
	public void create(final T t) throws DuplicateUniqueFieldException {
		assertNotNull(t);
		em.persist(t);
	}

	/**
	 * Aktualisiert den Eintrag von {@code theT} im Datenbestand. Falls
	 * {@code theT} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 *
	 * @param t
	 *            Das zu aktualisierende Objekt.
	 * @throws DuplicateUniqueFieldException
	 *             Falls durch das Aktualisieren von {@code theT} ein als
	 *             `unique` deklariertes Attribut doppelt vorkommen würde.
	 * @throws IllegalArgumentException
	 *             Falls {@code theT == null}, {@code theT.getId() == null}, es
	 *             noch keinen Eintrag für {@code theT} im Datenbestand gibt
	 *             oder {@code theT} kein durch JPA verwaltetes Objekt ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vlg. {@link EntityManager#merge(Object)}).
	 */
	public void update(final T t) throws DuplicateUniqueFieldException {
		assertNotNull(t);
		final int tId = t.getId();
		if(tId <= 0) throw new IllegalArgumentException("The id of the parameter must not be zero!");
		else
		{
			if(getById(tId) != null) em.merge(t);
			else throw new IllegalArgumentException("The specified object does not exist!");
		}
	}

	/**
	 * Entfernt {@code theT} aus dem Datenbestand. Falls {@code theT} nicht im
	 * Datenbestand vorhanden ist, wird *keine* Exception ausglöst. In jedem
	 * Fall ist nach Aufruf dieser Methode die Id von {@code theT} gleich
	 * {@code null}.
	 *
	 * Hinweis: Die Methode ist synchronisiert!
	 *
	 * @param t
	 *            Das zu entfernende Objekt.
	 * @throws IllegalArgumentException
	 *             Falls {@code theT == null} ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vlg. {@link EntityManager#remove(Object)}).
	 */
	@Override
	public synchronized void delete(final T t) {
		assertNotNull(t);
		if (t.getId() > 0) {
			final T entity = getById(t.getId());
			if (entity != null) {
				em.remove(entity);
			}
			t.clearId();
		}
		else throw new IllegalArgumentException("The id of the parameter must not be zero!");
	}

}
