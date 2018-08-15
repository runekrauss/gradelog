package de.unibremen.gradelog.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.CustomPage;
import de.unibremen.gradelog.model.Group;
import de.unibremen.gradelog.model.GroupInvite;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.GroupDAO;
import de.unibremen.gradelog.persistence.GroupInviteDAO;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.CustomPageDAO;
import de.unibremen.gradelog.util.Assertion;

/**
 * Dieser Controller kümmert sich um die Logik hinsichtlich der Gruppen sowie
 * der Einladungen. Er wird außerdem zum löschen oder bearbeiten von Datensätzen
 * aus der Datenbank für Gruppen Models genutzt.
 * 
 * @author Steffen Gerken
 * @author Marco Glander
 *
 */

@Named("groupBean")
@ViewScoped
public class GroupController extends AbstractController {
	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 987039415784518655L;
	/**
	 * Gruppe
	 */
	private Group group;

	private Group selectedGroup;

	private GroupInvite selectedGroupInvite;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Gruppen-Objekte übernimmt.
	 */
	private final GroupDAO groupDao;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Benutzer-Objekte übernimmt.
	 */
	private final UserDAO userDao;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Gruppeneinladung-Objekte übernimmt.
	 */
	private final GroupInviteDAO groupInviteDao;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * CustomPage-Objekte übernimmt.
	 */
	private final CustomPageDAO customPageDao;
	/**
	 * Durch den AutoCompleteButton gefundene User
	 */
	private List<User> selectedUsers;
	/**
	 * Liste von Strings, welche der AutoCompleteButton zurück gibt.
	 */
	private List<String> returnedUsers;

	/**
	 * Erzeugt einen {@link GroupController} mit definierter {@link Session},
	 * {@link GroupDAO}, {@link UserDAO}, {@link GroupInviteDAO} und
	 * {@link CustomPageDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link GroupController}s.
	 * @param pGroupDao
	 * 		Die {@link GroupDAO} des zu erzeugenden {@link GroupController}s.
	 * @param pUserDao
	 * 		Die {@link UserDAO} des zu erzeugenden {@link GroupController}s.
	 * @param pGroupInviteDao
	 * 		Die {@link GroupInviteDAO} des zu erzeugenden
	 * 		{@link GroupController}s.
	 * @param pCustomPageDao
	 * 		Die {@link CustomPageDAO} des zu erzeugenden
	 * 		{@link GroupController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public GroupController(final Session pSession,
						   final GroupDAO pGroupDao,
						   final UserDAO pUserDao,
						   final GroupInviteDAO pGroupInviteDao,
						   final CustomPageDAO pCustomPageDao) {
		super(Assertion.assertNotNull(pSession));
		groupDao = Assertion.assertNotNull(pGroupDao);
		userDao = Assertion.assertNotNull(pUserDao);
		groupInviteDao = Assertion.assertNotNull(pGroupInviteDao);
		customPageDao = Assertion.assertNotNull(pCustomPageDao);
	}

	/**
	 * Initialisiert eine Gruppe und die Liste der zurück gegebenen Benutzer.
	 */
	@PostConstruct
	public void init() {
		group = new Group();
		returnedUsers = new ArrayList<>();
		selectedGroup = new Group();
		selectedGroupInvite = new GroupInvite();
	}

	/**
	 * Speichert die gerade zu bearbeitende Gruppe in die Datenbank.
	 * Aktualisiert außerdem den Benutzer und versendet Einladungen.
	 */
	public void save() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save calendar values!");
			return;
		}
		if (groupDao.getGroupByName(group.getName()) != null) {
			addMessage("groupNameIsDoubled");
			return;
		}
		User user = getSession().getUser();
		groupDao.create(group);
		user.getGroups().add(group);
		group.addUser(user);
		try {

			userDao.update(user);
			groupDao.update(group);
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
					"errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("CustomPageController: Email ", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
					user.getEmail());
		}
		setSelectedGroup(group);
		addInvites();
		addMessage("sucessfullGroupCreate");

		init();
	}

	/**
	 * Der angemeldete Benutzer wird aus der Gruppe entfernt. Außerdem erfolgt
	 * eine Prüfung, ob die Gruppe noch Benutzer enthält, da sie sonst gelöscht
	 * wird. Falls die geschieht, werden alle mit der Gruppe verknüpften
	 * Einladungen auch gelöscht. Aktualisiert dabei die Benutzer in der
	 * Datenbank.
	 */
	public void updateDelete() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save group values!");
			return;
		}
		User user = getSession().getUser();
		Group group = getSelectedGroup();
		user.removeGroup(getSelectedGroup());
		group.removeUser(user);
		try {
			userDao.update(user);
			groupDao.update(getSelectedGroup());

			if (getSelectedGroup().getUsers().isEmpty()) {

				List<GroupInvite> allInvites = groupInviteDao.getGroupInvites(group);
				for (GroupInvite invite : allInvites) {
					invite.getUser().getInvites().remove(invite);
					groupInviteDao.delete(invite);
					userDao.update(invite.getUser());
				}
				remove(group);
			}
			addMessage("successfullGroupLeave");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
					"errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("CustomPageController: Email ", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
					user.getEmail());
		}
	}

	/**
	 * Überprüft ob der Benutzer neue Benutzer zu einer Gruppe eingeladen hat
	 * und parsed die List<String> in eine Liste von Benutzern. Diesen werden
	 * dann eingeladen und es werden neue Gruppeneinladungen in der Datenbank
	 * erstellt. Überprüft außerdem ob die angegebenen Benutzer schon eine
	 * Einladung dieser Gruppe haben oder Mitglieder sind.
	 */
	public void addInvites() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save group values!");
			return;
		}
		HashSet<User> users = new HashSet<>();
		if(returnedUsers == null){
			return;
		}
		for (String parse : returnedUsers) {
			User found = userDao.getUserByEmail(parse);
			if (found != null)
				users.add(found);// Mengen ignorieren duplikate
		}
		for (User u : users) {
			Session session = getSession();
			Group group = getSelectedGroup();
			GroupInvite invite = new GroupInvite();
			invite.setGroup(group);
			invite.setUser(u);
			if (session.getUser().equals(u) || u.getInvites().contains(invite)) {
				continue;
			}
			groupInviteDao.create(invite);
			u.addInvite(invite);
			try {
				userDao.update(u);
			} catch (final DuplicateUsernameException e) {
				addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
						"errorUsernameAlreadyInUse", u.getLogin());
			} catch (final DuplicateEmailException e) {
				addMessageWithLogging("CustomPageController: Email ", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
						u.getEmail());
			}
		}
		addMessage("successfullUserInvite");
	}

	/**
	 * Diese Methode wird aufgerufen, wenn ein User einen Invite abgelehnt hat.
	 * Der Invite wird aus dem Set im User Model gelöscht, um ihn dann aus der
	 * Datenbank zu entfernen.
	 *
	 */
	public void removeReject() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete group invite values!");
			return;
		}
		User user = getSession().getUser();
		GroupInvite invite = getSelectedGroupInvite();
		user.getInvites().remove(invite);
		try {
			userDao.update(user);
		} catch (DuplicateUsernameException e) {
			e.printStackTrace();
		} catch (DuplicateEmailException e) {
			e.printStackTrace();
		}
		if (invite != null)
			groupInviteDao.delete(invite);
		addMessage("succesfullInviteReject");
	}

	/**
	 * Diese Methode wird aufgerufen, wenn der User einen Invite annimmt. Der
	 * Invite wird aus dem Set im User gelöscht. Weiterhin wird die Gruppe des
	 * Invites dem Set<Group> hinzugefügt und es wird der GroupInvite aus der
	 * Datenbank entfernt.
	 */
	public void removeAccept() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete group invite values!");
			return;
		}
		User user = getSession().getUser();
		GroupInvite invite = getSelectedGroupInvite();
		Group group = invite.getGroup();
		user.getGroups().add(group);
		group.getUsers().add(user);
		user.getInvites().remove(invite);

		groupDao.update(group);
		try {
			// groupDao.update(invite.getGroup());
			userDao.update(user);
		} catch (DuplicateUsernameException e) {
			e.printStackTrace();
		} catch (DuplicateEmailException e) {
			e.printStackTrace();
		}
		if (invite != null)
			groupInviteDao.delete(invite);
		addMessage("succesfullInviteAccept");
	}

	/**
	 * Entfernt das aktuelle Gruppen Model aus der Datenbank.
	 */
	public void remove() {
		remove(group);
	}

	/**
	 * Löscht die Gruppe aus der Datenbank.
	 * 
	 * @param g
	 *            die zu löschende Gruppe
	 */
	public void remove(Group g) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete group values!");
			return;
		}
		if (g != null) {
			for(CustomPage c : g.getSharedPages()){
				c.removeGroup(g);
				customPageDao.update(c);
			}
			groupDao.delete(g);
			addMessage("successfullGroupDelete");
		}
	}

	public Set<Group> getGroups() {
		return getSession().getUser().getGroups();
	}

	public Group getGroup() {
		return group;
	}

	public Group getSelectedGroup() {
		return selectedGroup;

	}

	public void setSelectedGroup(Group selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	public List<User> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<User> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	/**
	 * Wird vom AutoCompleteFeld genutzt, damit dieses Vorschläge machen kann.
	 * Zeigt keine Vorschläge zu Benutzern die schon eine Einladung haben oder
	 * schon Mitglied der Gruppe sind.
	 * 
	 * @param query
	 * @return
	 */
	public List<User> completeUsers(String query) {
		List<User> allUsers = userDao.getAllUsers();
		List<User> filteredUsers = new ArrayList<>();

		for (final User user : allUsers) {
			if (user.equals(getSession().getUser())) {
				continue;
			}

			if (user.getDisplayName().toLowerCase().contains(query.toLowerCase())) {
				if (!(user.getGroups().contains(getSelectedGroup())
						|| user.getInvites().contains(getSelectedGroupInvite()))) {
					filteredUsers.add(user);
				}
			}
		}

		return filteredUsers;
	}

	public void setReturnedUsers(List<String> returnedUsers) {
		this.returnedUsers = returnedUsers;
	}

	public List<String> getReturnedUsers() {
		return returnedUsers;
	}

	public GroupInvite getSelectedGroupInvite() {
		return selectedGroupInvite;
	}

	public void setSelectedGroupInvite(GroupInvite selectedGroupInvite) {
		this.selectedGroupInvite = selectedGroupInvite;
	}

	public Set<GroupInvite> getGroupInvites() {
		User user = getSession().getUser();
		return user.getInvites();
	}
}
