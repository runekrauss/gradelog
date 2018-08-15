package de.unibremen.gradelog.persistence;

import de.unibremen.gradelog.exception.*;
import de.unibremen.gradelog.model.JPAEntity;
import de.unibremen.gradelog.model.Preference;

import javax.ejb.Stateless;
import javax.persistence.TransactionRequiredException;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Preference}.
 *
 * @author Rune Krauss
 */
@Stateless
public class PreferenceDAO extends JPADAO<Preference> {

    /**
     * Die eindeutige ID für Serialisierung.
     */
    private static final long serialVersionUID = 1543383068333720238L;

    /**
     * Aktualisiert den Eintrag von {@code preference} im Datenbestand. Falls
     * {@code preference} noch nicht im Datenbestand vorhanden ist, wird eine
     * {@link IllegalArgumentException} ausgelöst.
     *
     * @param preference
     *            Das zu aktualisierende {@link Preference}-Objekt.
     * @throws UnexpectedUniqueViolationException
     *             Falls der Aufruf der Oberklassenmethode unerwarteterweise
     *             eine {@link DuplicateUniqueFieldException} ausgelöst hat.
     * @throws IllegalArgumentException
     *             Falls {@code preference == null},
     *             {@code preference.getId() == null}, es noch keinen Eintrag für
     *             {@code preference} im Datenbestand gibt oder {@code preference} kein
     *             durch JPA verwaltetes Objekt ist.
     * @throws TransactionRequiredException
     *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
     *             vorliegt (vlg.
     *             {@link javax.persistence.EntityManager#merge(Object)}).
     */
    @Override
    public synchronized void update(final Preference preference) {
        final int preferenceId = assertNotNull(preference.getId(), "The id of the parameter must not be null!");
        assertNotNull(getById(preferenceId), "The parameter is not yet registered!");
        try {
            super.update(preference);
        } catch (final DuplicateUniqueFieldException e) {
            throw new UnexpectedUniqueViolationException(e);
        }
    }

    /**
     * Fügt {@code preference} dem Datenbestand hinzu. Falls {@code preference} bereits im
     * Datenbestand vorhanden ist (vgl. {@link JPADAO#create(JPAEntity)}, wird
     * eine {@link IllegalArgumentException} ausgelöst.
     *
     * @param preference
     *            Das zu speichernde {@link Preference}-Objekt.
     * @throws UnexpectedUniqueViolationException
     *             Falls der Aufruf der Oberklassenmethode unerwarteterweise
     *             eine {@link DuplicateUniqueFieldException} ausgelöst hat.
     * @throws IllegalArgumentException
     *             Falls {@code preference == null}, {@code preference.getId() != null} oder {@code preference} kein
     *             durch JPA verwaltetes Objekt ist.
     * @throws TransactionRequiredException
     *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
     *             vorliegt (vgl.
     *             {@link javax.persistence.EntityManager#persist(Object)}).
     */
    @Override
    public synchronized void create(final Preference preference) {
        assertNotNull(preference);
        try {
            super.create(preference);
        } catch (final DuplicateUniqueFieldException e) {
            throw new UnexpectedUniqueViolationException(e);
        }
    }

    public Preference getPreference() {
        return getEntityManager().createNamedQuery("Preference.find", getClazz()).getSingleResult();
    }

    @Override
    Class<Preference> getClazz() {
        return Preference.class;
    }
}
