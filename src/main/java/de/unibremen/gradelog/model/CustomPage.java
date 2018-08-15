package de.unibremen.gradelog.model;

//import java.sql.Timestamp;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

//Date zu String
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Steht für das Modul CustomPage mit Daten wie den Text und dem Benutzer, dem
 * die Seite gehört. Außerdem werden alle Nutzer und Gruppen gespeichert, die
 * Zugriff auf diese Seite haben
 * 
 * @author Marco Glander
 * @author Christopher Wojtkow
 */
@Entity
@Table(name = "Pages")
@NamedQueries({ @NamedQuery(name = "CustomPage.findAll", query = "SELECT c FROM CustomPage c"),
				@NamedQuery(name = "CustomPage.findById", query = "SELECT c FROM CustomPage c WHERE c.id = ?1"),
				@NamedQuery(name = "CustomPage.findOwnedByUser", query = "SELECT c FROM CustomPage c WHERE c.user.id = ?1")})

public class CustomPage extends JPAEntity implements Comparable<CustomPage> {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 7307194544248643534L;

	/**
	 * Der Inhalt einer CustomPage
	 */
	@Column(length = 8192)
	private String content;

	/**
	 * Der Title einer CustomPage
	 */
	@Column(length = 64)
	private String title;

	/**
	 * Das Erschaffungsdatum einer Custompage
	 */
	private Date date;

	/**
	 * Menge an Versionen der CustomPage
	 */
	@OneToMany(targetEntity = CustomPageHistory.class, cascade = CascadeType.REMOVE)
	private Set<CustomPageHistory> history;

	/**
	 * Besitzer der CustomPage
	 */
	@OneToOne
	private User user;

	/**
	 * Menge an User-Objekten den die CustomPage explizit freigegeben wurde
	 */
	@ManyToMany
	private Set<User> subs;

	/**
	 * Menge an Gruppen denen die CustomPage freigegeben wurde.
	 */
	@ManyToMany
	private Set<Group> groups;

	/**
	 * Besucherzähler
	 */
	private int counter;

	public CustomPage() {
		title = "";
		content = "";
		date = new Date(0);
		subs = new HashSet<>();
		groups = new HashSet<>();
	}

	/**
	 * Formartiert das Datum der CustomPage zu einem passenden String.
	 * 
	 * @return Der formatierte Datum-String
	 */
	public String getSimpleDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		String str = dateFormat.format(date.getTime());
		return str;
	}

	public Set<CustomPageHistory> getHistories() {
		return history;
	}

	public void setHistory(final Set<CustomPageHistory> history) {
		this.history = history;
	}

	/**
	 * Gibt das Objekt einer CustomPageHistory mit der angegebenen ID zurück.
	 * 
	 * @param id
	 *            ID der CustomPageHistory, die zurückgegeben werden soll.
	 * @return das gewünschte Objekt einer CustomPageHistory
	 */
	public Set<CustomPageHistory> getHistory(final int id) {
		return history;
	}

	/**
	 * Speichert den Inhalt dieser Seite in die angegebene CustomPageHistory.
	 * 
	 * @param h
	 *            Objekt einer CustomPageHistory, in welcher diese Seite
	 *            gespeichert werden soll.
	 * @return Gibt den Link der nächsten Seite als String zurück
	 */
	public void saveToHistory(final CustomPageHistory h) {
		h.setTitle(title);
		h.setContent(content);
		h.setCustomPage(this);
		h.setDate(date);
		getHistories().add(h);
	}

	/**
	 * Benutzt das angegebene Objekt einer CustomPageHistory, um diese Seite in
	 * den Status der angegebenen History zu versetzen. Dabei wird aus dem
	 * aktuellen Status der Seite eine neue History erstellt.
	 * 
	 * @param h
	 *            Objekt einer CustomPageHistory, dessen Status angenommen
	 *            werden soll
	 */
	public void applyHistory(final CustomPageHistory h) {
		CustomPageHistory newH = new CustomPageHistory();
		saveToHistory(newH);
		this.title = h.getTitle();
		this.content = h.getContent();
		this.date = new java.sql.Date(h.getDate().getTime());
		removeHistory(h);
	}

	/**
	 * Überträgt Title, Inhalt der übergebenen CustomPage sowie ein date in die
	 * attribute der aufrufenden CustomPage
	 * 
	 * @param cp
	 * @param date
	 */
	public void apply(final CustomPage cp, final java.util.Date date) {
		this.title = cp.getTitle();
		this.content = cp.getContent();
		this.date = new java.sql.Date(date.getTime());
	}

	/**
	 * Entfernt das angegebene Objekt einer CustomPageHistory aus der Liste
	 * dieser Seite.
	 * 
	 * @param h
	 *            Das zu löschende Objekt
	 * @return Gibt den Link der nächsten Seite als String zurück
	 */
	public void removeHistory(final CustomPageHistory h) {
		getHistories().remove(h);
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof CustomPage && ((CustomPage) other).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("\nCustomPage {\n" + "\tid: %d,\n" + "\tcontent: %s,\n" + "\ttitle: %s,\n"
				+ "\tDate: %s,\n" + "\tCounter: %d,}", getId(), content, title, getSimpleDate(), counter);
	}

	@Override
	public int compareTo(final CustomPage compared) {
		return this.getId() - compared.getId();
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public java.util.Date getDate() {
		return new java.util.Date(date.getTime());
	}

	public void setDate(final java.util.Date creationDate) {
		this.date = new java.sql.Date(creationDate.getTime());
	}

	public java.sql.Date getDateSql() {
		return date;
	}

	public boolean addSub(final User user) {
		return subs.add(user);
	}

	public boolean removeSub(final User user) {
		return subs.remove(user);
	}

	public List<User> getSubs() {
		ArrayList<User> subList = new ArrayList<>();
		subList.addAll(subs);
		return subList;
	}

	public boolean addGroup(final Group group) {
		return groups.add(group);
	}

	public boolean removeGroup(final Group group) {
		return groups.remove(group);
	}

	public List<Group> getGroups() {
		ArrayList<Group> groupList = new ArrayList<>();
		groupList.addAll(groups);
		return groupList;
	}

	public boolean removeShareable(final Shareable shared) {
		if (shared.getType() == "(G)") {
			return removeGroup((Group) shared);
		} else {
			return removeSub((User) shared);
		}
	}

	public void increment() {
		counter++;
	}

	public int getCounter() {
		return counter;
	}
}