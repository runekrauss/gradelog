package de.unibremen.gradelog.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * Hält die Einladung zu einer Gruppe. Enthält die Gruppe und den eingeladenen
 * Nutzer.s
 *
 * @author Marco Glander
 * @author Steffen Gerken
 */
@Entity
@NamedQueries({@NamedQuery(name = "GroupInvite.findAll", query = "SELECT g FROM GroupInvite g")})
public class GroupInvite extends JPAEntity {

    /**
     * Die eindeutige id für Serialisierung.
     */
    private static final long serialVersionUID = -6275826441068669431L;

    /**
     * Benutzer
     */
    @ManyToOne
    private User user;

    /**
     * Gruppe
     */
    @OneToOne(targetEntity = Group.class)
    private Group group;

    public GroupInvite() {
        user = null;
        group = null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * Überschreibt die Methode equals, sodass true returned wird, sobals die Id
     * gleich ist oder ein GroupInvite mit gleicher Gruppe und gleichem User
     * schon vorhanden ist.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof GroupInvite) {
            GroupInvite invite = (GroupInvite) other;
            return invite.getId() == getId()
                    || (invite.getUser().equals(getUser()) && invite.getGroup().equals(getGroup()));
        }

        return false;
    }

    /**
     * Überschreibt die Methode hashCode, sodass zur überprüfung die Id genutzt
     * wird.
     */
    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return String.format("GroupInvite {id: %d, user: %s, group: %s}", getId(),
                user.getProfile().getFirstName() + " " + user.getProfile().getLastName(), group.getName());
    }
}
