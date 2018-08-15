package de.unibremen.gradelog.model;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import de.unibremen.gradelog.persistence.SessionDAO;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.MissingDAO;

/**
 * Diese Klasse repräsentiert die Session eines Benutzers bzw. einer Sitzung vor
 * dem Login. Sie hat daher {@link SessionScoped}. Da Objekte dieser Klasse vom
 * Container durch Serialisierung passiviert und bei Bedarf wieder aktiviert
 * werden können, sind sie serialisierbar.
 *
 * Objekte dieser Klasse werden nicht über die Laufzeit der Applikation hinaus
 * persistiert!
 *
 * In der init-Methode, die nach der Erzeugung eines Objektes dieser Klasse
 * aufgerufen wird, trägt sich jedes solche Objekt in der Liste der Sessions der
 * {@link SessionDAO} ein. Bevor es zerstört wird, wird es durch die
 * destroy-Methode wieder aus dieser Liste entfernt.
 * 
 * @author Rune Krauss
 * @author Christopher Wojtkow
 * @author Steffen Gerken
 */
@SessionScoped
public class Session implements Serializable {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 3828357750697477354L;

	/**
	 * Verwaltet die bekannten Benutzer.
	 */
	@Inject
	private UserDAO userDAO;

	/**
	 * Verwaltet die Sessions.
	 */
	@Inject
	private SessionDAO sessionDAO;
	/**
	 * Speichert den Zeitpunkt, an dem sich ein Benutzer in diese Session
	 * eingeloggt hat.
	 */
	private LocalDateTime loginTime;
	/**
	 * Enthält alle importierten Benutzer.
	 */
	private Map<String, String> importedUsers;

	/**
	 * Enthält alle importierten Vertretungen.
	 */
	private List<Representation> importedRepresentations;

	/**
	 * Die Id des innerhalb dieser Session eingeloggten {@link User}s. Falls in
	 * dieser Session noch niemand eingeloggt ist, ist der Wert {@code null}.
	 *
	 * Hinweis: Man könnte hier auch das User-Objekt direkt speichern und bei
	 * jedem Aufruf von {@link #getUser()} aktualisieren. Da die Aktualisierung
	 * jedoch auch nur mit der Id des Objektes arbeiten würde, macht es keinen
	 * Unterschied. Ferner muss bei dieser Lösung die {@link User}-Klasse nicht
	 * serialisierbar sein.
	 */
	private int userId = -1;

	/**
	 * Nach Erzeugung eines Objektes dieser Klasse trägt sich dieses Objekt in
	 * die Liste aller Sessions der {@link SessionDAO} ein.
	 */
	@PostConstruct
	public void init() {
		sessionDAO.create(this);
		importedUsers = new HashMap<>();
		importedRepresentations = new ArrayList<>();
	}

	/**
	 * Kurz vor der Zerstörung dieses Objektes trägt es sich noch aus der Liste
	 * aller Sessions der {@link SessionDAO} aus.
	 */
	@PreDestroy
	public void destroy() {
		sessionDAO.delete(this);
	}

	/**
	 * Setzt den Zeitpunkt, an dem sich der aktuelle Benutzer in diese Session
	 * eingeloggt hat, auf den gegebenen Zeitpunkt. Der Wert wird (außer einer
	 * Prüfung auf {@code null}) nicht auf Plausibilität geprüft, kann also in
	 * der Zukunft oder auch sehr weit in der Vergangenheit liegen.
	 *
	 * @param theLoginTime
	 *            Der neue Login-Zeitpunkt des aktuellen Benutzers.
	 * @throws IllegalArgumentException
	 *             Falls der gegebene Zeitpunkt den Wert {@code null} hat.
	 */
	public void setLoginTime(final LocalDateTime theLoginTime) {
		loginTime = assertNotNull(theLoginTime);
	}

	/**
	 * Gibt den Login-Zeitpunkt dieser Session zurück.
	 *
	 * @return Den Login-Zeitpunkt dieser Session.
	 */
	public LocalDateTime getLoginTime() {
		return loginTime;
	}

	/**
	 * Gibt den innerhalb dieser Session eingeloggten {@link User} zurück. Ist
	 * niemand eingeloggt, so wird {@code null} zurückgegeben.
	 *
	 * @return Der innerhalb dieser Session eingeloggte {@link User} oder
	 *         {@code null}.
	 */
	public User getUser() {
		return userId == -1 ? null : userDAO.getById(userId);
	}

	/**
	 * Setzt den innerhalb dieser Session eingeloggten {@link User} auf
	 * {@code theUser}. Kann {@code null} sein, um einen {@link User} aus einer
	 * Session zu entfernen.
	 *
	 * @param theUser
	 *            Der innerhalb dieser Session eingeloggte {@link User}. Kann
	 *            {@code null} sein.
	 */
	public void setUser(final User theUser) {
		userId = theUser == null ? -1 : theUser.getId();
	}

	/**
	 * Gibt an, ob in der zugehörigen Session aktuell jemand eingeloggt ist oder
	 * nicht.
	 *
	 * @return {@code true} Falls in der zugehörigen Session jemand eingeloggt
	 *         ist, sonst {@code false}.
	 */
	public boolean isLoggedIn() {
		return userId != -1;
	}

	/**
	 * Bereitet die Map als Arrayliste auf, sodass sie in einer Datentabelle
	 * angezeigt werden kann.
	 * 
	 * @return Importierte Benutzer
	 */
	public List<Map.Entry<String, String>> getImportedUsers() {
		Set<Map.Entry<String, String>> userSet = importedUsers.entrySet();
		return new ArrayList<Map.Entry<String, String>>(userSet);
	}

	public Map<String, String> getImportedUserMap() {
		return importedUsers;
	}

	public List<Representation> getImportedRepresentations() {
		return importedRepresentations;
	}

	public void setImportedRepresentations(List<Representation> importedRepresentations) {
		this.importedRepresentations = importedRepresentations;
	}
}
