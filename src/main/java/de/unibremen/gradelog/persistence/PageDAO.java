package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotEmpty;
import static de.unibremen.gradelog.util.Assertion.assertNotNull;
import static java.lang.String.format;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TransactionRequiredException;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicatePageNameException;
import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.JPAEntity;
import de.unibremen.gradelog.model.Page;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Page}.
 * 
 * @author Rune Krauss
 */
@Stateless
public class PageDAO extends JPADAO<Page> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 1543383068333720238L;

	/**
	 * Aktualisiert den Eintrag von {@code page} im Datenbestand. Falls
	 * {@code page} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 *
	 * @param page
	 *            Das zu aktualisierende {@link Page}-Objekt.
	 * @throws DuplicateUsernameException
	 *             Falls der zu aktualisierende Benutzername bereits an ein
	 *             anderes Objekt vergeben ist.
	 * @throws DuplicateEmailException
	 *             Falls die zu aktualisierende E-Mail-Adresse bereits an ein
	 *             anderes Objekt vergeben ist.
	 * @throws UnexpectedUniqueViolationException
	 *             Falls der Aufruf der Oberklassenmethode unerwarteterweise
	 *             eine {@link DuplicateUniqueFieldException} ausgelöst hat.
	 * @throws IllegalArgumentException
	 *             Falls {@code theUser == null},
	 *             {@code theUser.getId() == null}, es noch keinen Eintrag für
	 *             {@code theUser} im Datenbestand gibt,
	 *             {@code theUser.getUsername() == null},
	 *             {@code theUser.getEmail() == null} oder {@code theUser} kein
	 *             durch JPA verwaltetes Objekt ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vlg.
	 *             {@link javax.persistence.EntityManager#merge(Object)}).
	 */
	@Override
	public synchronized void update(final Page page) throws DuplicatePageNameException {
		final int pageId = assertNotNull(page.getId(), "The id of the parameter must not be null!");
		assertNotNull(getById(pageId), "The parameter is not yet registered!");

		final String englishName = assertNotNull(page.getEnglishName(), "The name of the parameter must not be null!");
		final String germanName = assertNotNull(page.getGermanName(), "The name of the parameter must not be null!");

		final Page pageByEnglishName = getPageByName(englishName);
		if (pageByEnglishName != null && pageByEnglishName.getId() != pageId) {
			throw new DuplicatePageNameException(format("Pagename '%s' is already in use", pageByEnglishName));
		}
		final Page pageByGermanName = getPageByName(germanName);
		if (pageByGermanName != null && pageByGermanName.getId() != pageId) {
			throw new DuplicatePageNameException(format("Pagename '%s' is already in use", pageByGermanName));
		}
		try {
			super.update(page);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Fügt {@code page} dem Datenbestand hinzu. Falls {@code page} bereits im
	 * Datenbestand vorhanden ist (vgl. {@link JPADAO#create(JPAEntity)}, wird
	 * eine {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param page
	 *            Das zu speichernde {@link Page}-Objekt.
	 * @throws DuplicatePageNameException
	 *             Falls der Seitenname bereits vergeben ist.
	 * @throws UnexpectedUniqueViolationException
	 *             Falls der Aufruf der Oberklassenmethode unerwarteterweise
	 *             eine {@link DuplicateUniqueFieldException} ausgelöst hat.
	 * @throws IllegalArgumentException
	 *             Falls {@code page == null}, {@code page.getId() != null},
	 *             {@code page.getGermanName() == null},
	 *             {@code page.getEnglishName() == null} oder {@code page} kein
	 *             durch JPA verwaltetes Objekt ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vgl.
	 *             {@link javax.persistence.EntityManager#persist(Object)}).
	 */
	@Override
	public synchronized void create(final Page page) throws DuplicatePageNameException {
		assertNotNull(page);
		final String englishName = assertNotNull(page.getEnglishName(), "The name of the parameter must not be null!");
		final String germanName = assertNotNull(page.getGermanName(), "The name of the parameter must not be null!");

		final Page pageByEnglishName = getPageByName(englishName);
		if (pageByEnglishName != null && pageByEnglishName.getEnglishName() != null
				&& pageByEnglishName.getEnglishName().equals(page.getEnglishName())) {
			throw new DuplicatePageNameException(format("Pagename '%s' is already in use", pageByEnglishName));
		}
		final Page pageByGermanName = getPageByName(germanName);
		if (pageByGermanName != null && pageByGermanName.getGermanName() != null
				&& pageByGermanName.getGermanName().equals(page.getGermanName())) {
			throw new DuplicatePageNameException(format("Pagename '%s' is already in use", pageByGermanName));
		}
		try {
			super.create(page);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Gibt die Seite mit dem gegebenen Namen zurück. Falls es keine Seite mit
	 * dem gegebenen Namen gibt, wird {@code null} zurückgegeben.
	 * 
	 * @param name
	 *            der Seitenname der gesuchten Seite
	 * @return Die Seiten zum gegebenen Seitennamen oder {@code null} falls es
	 *         keine solche Seite gibt.
	 * @throws IllegalArgumentException
	 *             Falls der gegebene Seitenname leer ist oder den Wert
	 *             {@code null} hat.
	 */
	public Page getPageByName(final String name) {
		assertNotEmpty(name);
		final List<Page> pages = getEntityManager().createNamedQuery("Page.findByName", getClazz())
				.setParameter(1, name).getResultList();
		return pages.isEmpty() ? null : pages.get(0);
	}

	/**
	 * Liefert eine Liste mit allen Seiten, die in der Datenbank existieren.
	 * 
	 * @return Liste aller bekannten Seiten
	 */
	public List<Page> getAllPages() {
		return getEntityManager().createNamedQuery("Page.findAll", getClazz()).getResultList();
	}

	@Override
	Class<Page> getClazz() {
		return Page.class;
	}
}
