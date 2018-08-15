package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link GroupInviteDAO}.
 *
 * @author Marco Glander
 */
@Stateless
public class GroupInviteDAO extends JPADAO<GroupInvite> {

    /**
     * Die eindeutige ID für Serialisierung.
     */
    private static final long serialVersionUID = -1367533537250474217L;

    /**
     * Fügt {@code groupInvite} dem Datenbestand hinzu. Falls
     * {@code groupInvite} bereits im Datenbestand vorhanden ist (vgl.
     * {@link JPADAO#create(JPAEntity)}, wird eine
     * {@link IllegalArgumentException} ausgelöst.
     *
     * @param groupInvite Das zu speichernde {@link GroupInvite}-Objekt.
     * @throws IllegalArgumentException Falls {@code groupInvite == null} oder {@code groupInvite}
     *                                  kein durch JPA verwaltetes Objekt ist.
     */
    public synchronized void create(GroupInvite groupInvite) {
        assertNotNull(groupInvite);
        try {
            super.create(groupInvite);
        } catch (final DuplicateUniqueFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aktualisiert den Eintrag von {@code groupInvite} im Datenbestand. Falls
     * {@code groupInvite} noch nicht im Datenbestand vorhanden ist, wird eine
     * {@link IllegalArgumentException} ausgelöst.
     *
     * @param groupInvite Das zu speichernde {@link GroupInvite}-Objekt.
     * @throws IllegalArgumentException Falls {@code groupInvite == null} oder {@code groupInvite}
     *                                  kein durch JPA verwaltetes Objekt ist.
     */
    public synchronized void update(GroupInvite groupInvite) {
        assertNotNull(groupInvite);
        try {
            super.update(groupInvite);
        } catch (final DuplicateUniqueFieldException e) {
            throw new UnexpectedUniqueViolationException(e);
        }
    }

    /**
     * Löscht ein GroupInvite-Objekt aus dem Datenbestand.
     */
    public synchronized void delete(GroupInvite groupInvite) {
        super.delete(groupInvite);
    }

    public Class<GroupInvite> getClazz() {
        return GroupInvite.class;
    }

    public GroupInvite getById(int id) {
        EntityManager em = getEntityManager();
        return em.find(GroupInvite.class, id);
    }

    /**
     * Liefert eine Liste aller GruppenEinladungen einer Gruppe zurück
     *
     * @param group die Gruppe, dessen Einladungen geladen werden sollen
     * @return Liste aller gefundenen Einladungen
     */
    public List<GroupInvite> getGroupInvites(Group group) {
        List<GroupInvite> result = getEntityManager().createNamedQuery("GroupInvite.findAll", getClazz())
                .getResultList();
        for (GroupInvite invite : result) {
            if (!invite.getGroup().equals(group))
                result.remove(invite);
        }
        return result;
    }

    public List<GroupInvite> getByUser(User u) {
        return getEntityManager().createNamedQuery("select g from GroupInvite g where g.user = :user", getClazz())
                .setParameter("email", u.getEmail()).getResultList();
    }
}
