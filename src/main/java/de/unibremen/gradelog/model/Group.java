package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Repräsentation einer Gruppe. Eine Gruppe enthält Namen, Mitglieder und ein
 * Set aus CustomPages, die dieser Gruppe zur Verfügung stehen.
 * 
 * @author Marco Glander
 * @author Steffen Gerken
 * @author Christopher Wojtkow
 */
@Entity
@Table(name = "Groups")
@NamedQueries({ @NamedQuery(name = "Group.findAll", query = "SELECT g FROM Group g"),
		@NamedQuery(name = "Group.findByName", query = "SELECT g FROM Group g WHERE g.name = ?1"), })
public class Group extends JPAEntity implements Shareable, MessageReceiver {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -6915779321605152546L;

	/**
	 * Name
	 */
	@Column(length = 64, nullable = false, unique = true)
	private String name;
	/**
	 * Liste der Benutzer in dieser Gruppe
	 */
	@ManyToMany(targetEntity = User.class, cascade = CascadeType.PERSIST)
	private Set<User> users;
	/**
	 * Set der CustomPages, welche mit dieser Gruppe geteilt wurden.
	 */
	@ManyToMany(targetEntity = CustomPage.class, mappedBy = "groups")
	private Set<CustomPage> sharedPages;

	public Group() {
		name = "";
		users = new HashSet<>();
		sharedPages = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	/**
	 * Entfernt den angegebenen User aus der Gruppe.
	 * 
	 * @param u
	 *            User, der aus der Gruppe entfernt werden soll
	 */
	public void removeUser(User u) {
		getUsers().remove(u);
	}

	/**
	 * Fügt einen User der Gruppe hinzu.
	 * 
	 * @param u
	 *            User, der der Gruppe hinzugefügt werden soll
	 * @return Link zur nächsten Seite
	 */
	public void addUser(User u) {
		getUsers().add(u);
	}

	/**
	 * Überschreibt die Methode und liefert true, falls die ID gleich ist.
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof Group && ((Group) other).getId() == getId();
	}

	/**
	 * Überschreibt die Methode und lässt nicht über den HashCode Gleichheit
	 * überprüfen, sonder über die ID.
	 */
	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("Group {id: %d, name: %s}", getId(), name);
	}

	public String getDisplayName() {
		return "(G) " + getName();
	}

	public String getReceiverName() {
		return getName();
	}

	public Set<User> getUserSet() {
		return users;
	}

	public boolean removeSharedPage(final CustomPage cp) {
		return sharedPages.remove(cp);
	}

	public boolean addSharedPage(final CustomPage cp) {
		return sharedPages.add(cp);
	}

	public Set<CustomPage> getSharedPages() {
		return sharedPages;
	}

	// nur name nichts anderes (fuer customPages)
	public String getPureName() {
		return getName();
	}

	public String getType() {
		return "(G)";
	}

	public boolean hasProfile() {
		return false;
	}
}
