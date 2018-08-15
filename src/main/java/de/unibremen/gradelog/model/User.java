package de.unibremen.gradelog.model;

import static de.unibremen.gradelog.util.Crypt.hash;
import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Date;

import org.primefaces.model.UploadedFile;

/**
 * Diese Klasse repräsentiert einen Nutzer dieser Applikation. Neben Daten zu
 * der Person hinter dem Nutzer werden Daten zu dem Nutzer wie z.B. freigebene
 * Seiten und Nachrichten gespeichert. Diese Klasse bildet den Kern der
 * Applikation, da sie mit allen anderen Klassen verbunden ist.
 * 
 * Die Email eines Benutzers wurde als Primärschlüssel gewählt, um Nutzer an
 * anderen Stelle der Applikation damit identifizieren zu können.
 * 
 * Ein Objekt dieser Klasse gilt als äquivalent, wenn ihre IDs oder Emails
 * gleich sind.
 * 
 * @author Marco Glander
 * @author Rune Krauss
 * @author Christopher Wojtkow
 * @author Mirco Bockholt
 * @author Steffen Gerken
 */
@Entity
@Table(name = "Users")
@NamedQueries({ @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
		@NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.login = ?1"),
		@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = ?1"),
		@NamedQuery(name = "User.findAllTeachers", query = "SELECT u FROM User u WHERE u.role = ?1 AND u.profile.privated = ?2") })
public class User extends JPAEntity implements Shareable, MessageReceiver {

	/**
	 * Standard-Sprache für Benutzer
	 */
	private static final Locale DEFAULT_LANGUAGE = Locale.GERMAN;

	/**
	 * Die eindeutige SerialisierungsID
	 */
	private static final long serialVersionUID = -2841896419854631425L;

	/**
	 * Mögliche Rollen eines Benutzers
	 */
	public static enum Roles {
		STUDENT, TEACHER, ADMIN
	};

	/**
	 * Email des Benutzers
	 */
	@Column(length = 128, nullable = false)
	private String email;

	/**
	 * Benutzername
	 */
	@Column(length = 64, nullable = false, unique = true)
	private String login;

	/**
	 * Passwort des Benutzers
	 */
	@Column(length = 128, nullable = false)
	private String password;

	/**
	 * Benutzerrollen
	 */
	private Roles role;

	/**
	 * Blockier-Status
	 */
	private boolean blocked;

	/**
	 * Online-Status
	 */
	private boolean online;

	/**
	 * Erstellungsdatum
	 */
	private Date creationDate;

	/**
	 * Zeigt Erinnerungen zu den Tasks an
	 */
	private boolean activatedReminder;

	/**
	 * Alle Tasks im Dashboard anzeigen?
	 */
	private boolean activatedAllTodos;

	/**
	 * Standardsprache
	 */
	private Locale language = DEFAULT_LANGUAGE;

	/**
	 * Profil des jeweiligen Benutzers
	 */
	@OneToOne(targetEntity = Profile.class, cascade = CascadeType.ALL)
	private Profile profile;

	/**
	 * Menge aller Einladungen
	 */
	@OneToMany(targetEntity = GroupInvite.class, mappedBy = "user")
	private Set<GroupInvite> invites;

	/**
	 * Menge der zugehörigen Gruppen
	 */
	@ManyToMany(targetEntity = Group.class, mappedBy = "users", cascade = CascadeType.PERSIST)
	private Set<Group> groups;

	/**
	 * Menge der Stundenpläne
	 */
	@OneToOne(targetEntity = Timetable.class, cascade = CascadeType.ALL)
	private Timetable timetable;

	/**
	 * Telefonliste
	 */
	@OneToMany(targetEntity = Telephone.class, cascade = CascadeType.ALL)
	private List<Telephone> telephones;

	/**
	 * Menge der Kalender
	 */
	@OneToOne(targetEntity = Calendar.class, cascade = CascadeType.ALL)
	private Calendar calendar;

	/**
	 * Menge der freigegebenen Seiten
	 */
	@ManyToMany(targetEntity = CustomPage.class, mappedBy = "subs")
	private Set<CustomPage> sharedPages;

	/**
	 * Menge der Dateien
	 */
	@OneToMany(targetEntity = File.class, cascade = CascadeType.ALL)
	private Set<File> files;

	/**
	 * Menge der Evaluationen
	 */
	@OneToMany(mappedBy = "user")
	private Set<Evaluation> evaluations;

	/**
	 * Menge der Fehlzeiten
	 */
	@OneToMany(mappedBy = "user")
	private Set<Missing> missings;

	/**
	 * Menge der Nachrichten
	 */
	@ManyToMany(targetEntity = Message.class)
	private Set<Message> messages;

	/**
	 * Das hochgeladene File
	 */
	@Transient
	private UploadedFile file;

	/**
	 * File der hochgeladenen Benutzer aus UNTIS
	 */
	@Transient
	private UploadedFile dif;

	/**
	 * Widgets im Dashboard
	 */
	private List<String> widgets;

	/**
	 * Initialisiert einen Benutzer mit den Standardwerten, so gilt bspw. die
	 * Standardrolle 'Student'.
	 */
	public User() {
		login = "";
		password = "";
		email = "";
		setCreationDate(new java.util.Date());
		activatedReminder = true;
		role = Roles.STUDENT;
		profile = new Profile();

		invites = new HashSet<>();
		groups = new HashSet<>();
		timetable = new Timetable();
		timetable.setUser(this);
		telephones = new ArrayList<>();
		calendar = new Calendar();
		calendar.setUser(this);
		sharedPages = new HashSet<>();
		files = new HashSet<>();
		evaluations = new HashSet<>();
		missings = new HashSet<>();
		messages = new HashSet<>();
		widgets = new ArrayList<>();
		widgets.add("messages");
		widgets.add("todos");
		widgets.add("customPages");
		widgets.add("sharedPages");
		widgets.add("sharedPagesCounter");
	}

	/**
	 * Lehnt den angegebenen GroupInvite dieses User ab. Dadurch wird der
	 * Beitritt zur Gruppe verhindert und der GroupInvite aus der Liste
	 * entfernt.
	 * 
	 * @param i
	 *            GroupInvite, der abgelehnt werden soll
	 * @return Link zur nächsten Seite
	 */
	public void refuseInvite(GroupInvite i) {
		getInvites().remove(i);
	}

	/**
	 * Gibt die Benutzerrolle zurück. Standardmäßig gilt die Studenten-Rolle.
	 * 
	 * @return Benutzerrolle
	 */
	public String getUserRole() {
		if (getRole() == Roles.ADMIN)
			return "admin";
		else if (getRole() == Roles.TEACHER)
			return "teacher";
		else
			return "student";
	}

	/**
	 * Setzt die Benutzerrolle. Standardmäßig gilt die Studenten-Rolle.
	 * 
	 * @param role
	 *            Rolle
	 */
	public void setUserRole(final String role) {
		if (role.equals("admin"))
			this.role = Roles.ADMIN;
		else if (role.equals("teacher"))
			this.role = Roles.TEACHER;
		else
			this.role = Roles.STUDENT;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(final String login) {
		this.login = login.toLowerCase();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		if ( password == null || password.equals("") )
			return;
		this.password = hash(assertNotNull(password));
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(final UploadedFile file) {
		this.file = file;
	}

	public UploadedFile getDIF() {
		return dif;
	}

	public void setDIF(final UploadedFile dif) {
		this.dif = dif;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(final boolean blocked) {
		this.blocked = blocked;
	}

	public Locale getLanguage() {
		return language;
	}

	public String getUserLanguage() {
		if (getLanguage().equals(Locale.ENGLISH))
			return "english";
		else
			return "german";
	}

	public void setUserLanguage(final String language) {
		if (language.equals("english")) {
			this.language = Locale.ENGLISH;
		} else {
			this.language = Locale.GERMAN;
		}
	}

	public void setLanguage(final Locale language) {
		this.language = language;
	}

	public java.util.Date getCreationDate() {
		return new java.util.Date(creationDate.getTime());
	}

	public void setCreationDate(final java.util.Date creationDate) {
		this.creationDate = new java.sql.Date(creationDate.getTime());
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(final Roles role) {
		this.role = role;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(final Profile profile) {
		this.profile = profile;
	}

	public Set<GroupInvite> getInvites() {
		return invites;
	}

	public void setInvites(final Set<GroupInvite> invites) {
		this.invites = invites;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(final Set<Group> groups) {
		this.groups = groups;
	}

	public Timetable getTimetable() {
		return timetable;
	}

	public void setTimetable(final Timetable timetable) {
		this.timetable = timetable;
	}

	public List<Telephone> getTelephones() {
		return telephones;
	}

	public void setTelephones(final List<Telephone> telephones) {
		this.telephones = telephones;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(final Calendar calendar) {
		this.calendar = calendar;
		calendar.setUser(this);
	}

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(final Set<File> files) {
		this.files = files;
	}

	public Set<Evaluation> getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(final Set<Evaluation> evaluations) {
		this.evaluations = evaluations;
	}

	public void setMissings(final Set<Missing> missings) {
		this.missings = missings;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(final Set<Message> messages) {
		this.messages = messages;
	}

	public void setOnline(final boolean online) {
		this.online = online;
	}

	public boolean isOnline() {
		return online;
	}

	public boolean removeSharedPage(final CustomPage cp) {
		return sharedPages.remove(cp);// remove gibt Boolean zurück
	}

	public boolean addSharedPage(final CustomPage cp) {
		return sharedPages.add(cp);
	}

	public Set<CustomPage> getSharedPages() {
		return sharedPages;
	}

	public String removeFile(final File f) {
		getFiles().remove(f);
		return null;
	}

	public String addFile(final File f) {
		getFiles().add(f);
		return null;
	}

	public String removeEvaluation(final Evaluation ev) {
		getEvaluations().remove(ev);
		return null;
	}

	public String addEvaluation(final Evaluation ev) {
		getEvaluations().add(ev);
		return null;
	}

	public String removeMissing(final Missing mis) {
		getMissings().remove(mis);
		return null;
	}

	public String addMissing(final Missing mis) {
		getMissings().add(mis);
		return null;
	}

	public Set<Missing> getMissings() {
		return missings;
	}

	public String removeMessage(final Message mes) {
		getMessages().remove(mes);
		return null;
	}

	public String addMessage(final Message mes) {
		getMessages().add(mes);
		return null;
	}

	public void removeGroup(final Group group) {
		getGroups().remove(group);
	}

	public void addInvite(final GroupInvite i) {
		if (getInvites().contains(i)) {
			return;
		}
		getInvites().add(i);
	}

	public String addTelephone(final Telephone telephone) {
		getTelephones().add(telephone);
		return null;
	}

	public String removeTelephone(final Telephone telephone) {
		getTelephones().remove(telephone);
		return null;
	}

	@Override
	public boolean equals(final Object theObject) {
		if (!(theObject instanceof User)) {
			return false;
		}
		final User otherUser = (User) theObject;
		return getId() == otherUser.getId();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("User {id: %d, login: %s, email: %s}", getId(), login, email);
	}

	public static Locale getDefaultLanguage() {
		return DEFAULT_LANGUAGE;
	}

	public String getDisplayName() {
		return profile.getFirstName() + " " + profile.getLastName() + " (" + email + ")";
	}

	public String getReceiverName() {
		return email;
	}

	public Set<User> getUserSet() {
		Set<User> result = new HashSet<>();
		result.add(this);
		return result;
	}

	public String getType() {
		return " ";
	}
	
	// nur name nichts anderes (fuer customPages)
	public String getPureName() {
		return this.getProfile().getFirstName() + " " + this.getProfile().getLastName();
	}


	public boolean hasProfile() {
		return true;
	}

	public boolean isActivatedReminder() {
		return activatedReminder;
	}

	public void setActivatedReminder(boolean activatedReminder) {
		this.activatedReminder = activatedReminder;
	}

	public List<String> getWidgets() {
		return widgets;
	}

	public void setWidgets(final List<String> widgets) {
		this.widgets = widgets;
	}

	public boolean isActivatedAllTodos() {
		return activatedAllTodos;
	}

	public void setActivatedAllTodos(boolean activatedAllTodos) {
		this.activatedAllTodos = activatedAllTodos;
	}
}
