package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotEmpty;
import static de.unibremen.gradelog.util.Assertion.assertNotNull;
import static java.lang.String.format;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TransactionRequiredException;

import de.unibremen.gradelog.exception.DuplicateTemplateNameException;
import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.JPAEntity;
import de.unibremen.gradelog.model.Template;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Template}.
 * 
 * @author Rune Krauss
 */
@Stateless
public class TemplateDAO extends JPADAO<Template> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 1543383068333720238L;

	@Override
	Class<Template> getClazz() {
		return Template.class;
	}

	/**
	 * Aktualisiert den Eintrag von {@code template} im Datenbestand. Falls
	 * {@code theUser} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 *
	 * @param template
	 *            Das zu aktualisierende {@link Template}-Objekt.
	 * @throws DuplicateTemplateNameException
	 *             Falls der zu aktualisierende Templatename bereits an ein
	 *             anderes Objekt vergeben ist.
	 * @throws UnexpectedUniqueViolationException
	 *             Falls der Aufruf der Oberklassenmethode unerwarteterweise
	 *             eine {@link DuplicateUniqueFieldException} ausgelöst hat.
	 * @throws IllegalArgumentException
	 *             Falls {@code template == null},
	 *             {@code template.getId() == null}, es noch keinen Eintrag für
	 *             {@code template} im Datenbestand gibt,
	 *             {@code template.getName() == null} oder {@code template} kein
	 *             durch JPA verwaltetes Objekt ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vlg.
	 *             {@link javax.persistence.EntityManager#merge(Object)}).
	 */
	@Override
	public synchronized void update(final Template template) throws DuplicateTemplateNameException {
		final int templateId = assertNotNull(template.getId(), "The id of the parameter must not be null!");
		assertNotNull(getById(templateId), "The parameter is not yet registered!");

		final String name = assertNotNull(template.getName(), "The name of the parameter must not be null!");

		final Template templateByName = getTemplateByName(name);
		if (templateByName != null && templateByName.getId() != templateId) {
			throw new DuplicateTemplateNameException(format("Pagename '%s' is already in use", templateByName));
		}
		try {
			super.update(template);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Fügt {@code template} dem Datenbestand hinzu. Falls {@code template}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param template
	 *            Das zu speichernde {@link Template}-Objekt.
	 * @throws DuplicateTemplateNameException
	 *             Falls der Templatename bereits vergeben ist.
	 * @throws UnexpectedUniqueViolationException
	 *             Falls der Aufruf der Oberklassenmethode unerwarteterweise
	 *             eine {@link DuplicateUniqueFieldException} ausgelöst hat.
	 * @throws IllegalArgumentException
	 *             Falls {@code template == null},
	 *             {@code template.getId() != null},
	 *             {@code template.getName() == null} oder {@code template} kein
	 *             durch JPA verwaltetes Objekt ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vgl.
	 *             {@link javax.persistence.EntityManager#persist(Object)}).
	 */
	@Override
	public synchronized void create(final Template template) throws DuplicateTemplateNameException {
		assertNotNull(template);
		final String name = assertNotNull(template.getName(), "The name of the parameter must not be null!");

		final Template templateByName = getTemplateByName(name);
		if (templateByName != null && templateByName.getName() != null
				&& templateByName.getName().equals(template.getName())) {
			throw new DuplicateTemplateNameException(format("Pagename '%s' is already in use", templateByName));
		}

		try {
			super.create(template);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	public Template getTemplateByName(final String name) {
		assertNotEmpty(name);
		final List<Template> templates = getEntityManager().createNamedQuery("Template.findByName", getClazz())
				.setParameter(1, name).getResultList();
		return templates.isEmpty() ? null : templates.get(0);
	}

	/**
	 * Gibt eine Liste mit allen innerhalb der Applikation bekannten Benutzern
	 * zurück.
	 *
	 * @return Liste mit allen innerhalb der Applikation bekannten Benutzern.
	 */
	public List<Template> getAllTemplates() {
		return getEntityManager().createNamedQuery("Template.findAll", getClazz()).getResultList();
	}
}
