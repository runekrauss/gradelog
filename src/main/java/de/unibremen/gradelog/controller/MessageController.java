package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.UserDAO;

import de.unibremen.gradelog.model.Message;
import de.unibremen.gradelog.model.MessageReceiver;
import de.unibremen.gradelog.persistence.GroupDAO;
import de.unibremen.gradelog.persistence.MessageDAO;

/**
 * Diese Bean kümmert sich um die Message-Komponente dieser Applikation. Sie ist
 * in der Lage, neue Nachrichten zu erstellen und auf bestehende Nachrichten zu
 * antworten. Sie kümmert sich weiterhin um die Persistenz der Nachrichten
 * mittels MessageDAO.
 * 
 * Sie bietet weiterhin Hilfsmethoden für die Widgets von PrimeFaces, darunter
 * z.B. auch Methoden für das autocomplete Feld, welche Vorschläge für Empfänger
 * anbietet.
 * 
 * @author Marco Glander
 * @autho Christos Dhimitris
 */

@Named("messageBean")
@ViewScoped
public class MessageController extends AbstractController {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -1837688541417891257L;

	/**
	 * Stellt alle Empfänger dar, die der Nutzer auf der Seite als Empfänger für
	 * seine neue Nachricht ausgewählt hat.
	 */
	private List<String> selectedReceivers;
	/**
	 * Alle Nutzer der Applikation
	 */
	private List<User> allUsers;
	/**
	 * Alle Nachrichten des Nutzers, der diese Seite verwendet.
	 */
	private List<Message> allMessages;
	/**
	 * User, der die aktuelle Seite verwendet.
	 */
	private User user;
	/**
	 * Neue Nachricht, die manipuliert wird, wenn der Nutzer eine neue Nachricht
	 * versendet.
	 */
	private Message message;
	/**
	 * DAO für die Persistenz von Nachrichten
	 */
	private final MessageDAO messageDao;
	/**
	 * DAO für die Peristenz von Usern
	 */
	private final UserDAO userDao;
	/**
	 * DAO für die Persistenz von Gruppen
	 */
	private final GroupDAO groupDao;

	/**
	 * Erzeugt einen {@link MessageController} mit definierter {@link Session},
	 * {@link MessageDAO}, {@link UserDAO} und {@link GroupDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link MessageController}s.
	 * @param pMessageDao
	 *		Die {@link MessageDAO} des zu erzeugenden
	 *		{@link MessageController}s.
	 * @param pUserDao
	 * 		Die {@link UserDAO} des zu erzeugenden {@link MessageController}s.
	 * @param pGroupDao
	 * 		Die {@link GroupDAO} des zu erzeugenden {@link MessageController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public MessageController(final Session pSession,
							 final MessageDAO pMessageDao,
							 final UserDAO pUserDao,
							 final GroupDAO pGroupDao) {
		super(Assertion.assertNotNull(pSession));
		messageDao = Assertion.assertNotNull(pMessageDao);
		userDao = Assertion.assertNotNull(pUserDao);
		groupDao = Assertion.assertNotNull(pGroupDao);
	}

	/**
	 * Diese Methode wird von JSF automatisch aufgerufen, sobald diese Bean als
	 * eingebunden deklariert wird. Sie bereitet die Benutzung der Seite durch
	 * den User vor, indem sie eine neue Message erstellt, die vom User
	 * manipuliert werden kann, sowie nützliche Collections, die Daten
	 * enthalten.
	 */
	@PostConstruct
	public void init() {
		message = new Message();
		user = getSession().getUser();
		allMessages = new ArrayList<>();
		allMessages.addAll(getSession().getUser().getMessages());
		for (Message m : allMessages) {
			messageDao.update(m);
		}
		Collections.sort(allMessages);
		allUsers = userDao.getAllUsers();
		selectedReceivers = new ArrayList<>();
	}

	/**
	 * Schreibt die Attributswerte dieses Models per DAO in die Datenbank.
	 * Außerdem wird die Nachricht an alle ausgewählten Empfänger verschickt.
	 */
	public void save() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save message values!");
			return;
		}
		if (message.getContent().length() > 1024) {
			addMessage("errorMaxLength");
			return;
		}
		try {
			assertNotNull(user);
			assertNotNull(message);
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, getLogger(), Level.DEBUG, "errorMessageIncomplete");
			return;
		}
		message.setAuthor(user.getEmail());
		message.setReceivers(new HashSet<>(selectedReceivers));
		messageDao.create(message);
		user.addMessage(message);
		for (String parse : selectedReceivers) {
			MessageReceiver found = userDao.getUserByEmail(parse);
			if (found == null)
				found = groupDao.getGroupByName(parse);
			for (User u : found.getUserSet()) {
				u.addMessage(message);

				try {
					userDao.update(u);
				} catch (DuplicateUsernameException e) {
					e.printStackTrace();
				} catch (DuplicateEmailException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			userDao.update(user);
		} catch (DuplicateUsernameException e) {
			e.printStackTrace();
		} catch (DuplicateEmailException e) {
			e.printStackTrace();
		}
		addMessage("successMessageSaved");
		init();
	}

	/**
	 * Markiert die übergeben Nachricht als von User gelesen
	 * 
	 * @param m
	 *            die Nachricht, die als gelesen markiert werden soll
	 */
	public void confirmRead(Message m) {
		m.confirmRead(user.getReceiverName());
		messageDao.update(m);
	}

	/**
	 * Gibt die Anzahl der ungelesenen Nachrichten des aktiven Nutzers zurück.
	 * 
	 * @return Anzahl ungelesene Nachrichten
	 */
	public int checkUnread() {
		Set<Message> messages = user.getMessages();
		int count = 0;
		for (Message m : messages) {
			if (!m.getAuthor().equals(user.getReceiverName()) && !m.getReadStatus(user.getReceiverName()))
				count++;
		}
		return count;
	}

	/**
	 * Löscht die übergebene Nachricht aus der Datenbank und aus dem
	 * Datenbestand des Users
	 * 
	 * @param message
	 *            Nachricht, die gelöscht werden soll
	 */
	public void delete(Message message) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save message values!");
			return;
		}
		user.getMessages().remove(message);
		try {
			userDao.update(user);
			addMessage("messageDeleted");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("registerUserForm:username", e, getLogger(), Level.DEBUG, "errorUsernameAlreadyInUse",
					getSession().getUser().getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("registerUserForm:email", e, getLogger(), Level.DEBUG, "errorEmailAlreadyInUse",
					getSession().getUser().getEmail());
		}
	}

	public void save(Message m) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save message values!");
			return;
		}
		try {
			messageDao.update(assertNotNull(m));
		} catch (final IllegalArgumentException e) {

		}
	}

	/**
	 * Antwortet auf die übergebene Nachricht. Der Titel wird mit 'RE:'
	 * erweitert. Die Antwort wird nur zum originalen Autor versendet!
	 * 
	 * @param m
	 *            die Nachricht, auf die geantwortet werden soll
	 */
	public void reply(Message m) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save message values!");
			return;
		}
		try {
			assertNotNull(message);
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, getLogger(), Level.DEBUG, "errorMessageIncomplete");
			return;
		}

		HashSet<String> newReceivers = new HashSet<>();
		newReceivers.addAll(m.getReceivers());
		message.setReceivers(newReceivers);
		message.setSubject("RE: " + m.getSubject());
		selectedReceivers.add(m.getAuthor());
		save();
	}

	/**
	 * Löscht den Datensatz dieses Models aus der Datenbank
	 */
	public void remove() {
		remove(message);
	}

	public void remove(Message m) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete message values!");
			return;
		}
		try {
			messageDao.delete(assertNotNull(m));
		} catch (final IllegalArgumentException e) {
		}
	}

	public List<Message> getMessages() {
		return allMessages;

	}

	/**
	 * Gibt alle gesendeten Nachrichten des Nutzers zurück
	 * 
	 * @return gesendete Nachrichten des Nutzers
	 */
	public List<Message> getSentMessages() {
		List<Message> result = new ArrayList<>();
		for (Message m : allMessages) {
			if (!m.getReceivers().contains(user.getReceiverName()))
				result.add(m);
		}
		return result;

	}

	/**
	 * Gibt alle empfangenen Nachrichten des Nutzers zurück
	 * 
	 * @return Liste aller empfangenen Nachrichten
	 */
	public List<Message> getReceivedMessages() {
		List<Message> result = new ArrayList<>();
		for (Message m : allMessages) {
			if (!m.getAuthor().equals(user.getReceiverName())) {
				result.add(m);
			}

		}
		return result;

	}

	public Message getMessage() {
		return message;
	}

	public List<String> getSelectedReceivers() {
		return selectedReceivers;
	}

	public void setSelectedReceivers(List<String> selectedReceivers) {
		this.selectedReceivers = selectedReceivers;
	}

	/**
	 * Gibt eine anzuzeigende Liste mit allen innerhalb der Applikation
	 * bekannten Benutzern zurück. Diese Liste ist keine Kopie, sondern Teil des
	 * internen Zustands der Bean.
	 *
	 * @return Die anzuzeigende Liste aller innerhalb der Applikation bekannten
	 *         Benutzern.
	 */
	public List<User> getAllUsers() {
		return allUsers;
	}

	/**
	 * Wird vom AutoCompleteFeld genutzt, damit dieses Vorschläge machen kann.
	 * Der Nutzer der Session wird hierbei ignoriert. Es können auch Gruppen
	 * vorgeschlagen werden.
	 * 
	 * @param query
	 * @return result Liste von allen Vorschlägen
	 */
	public List<MessageReceiver> completeReceiver(String query) {
		List<MessageReceiver> receivers = new ArrayList<>();
		List<MessageReceiver> result = new ArrayList<>();
		receivers.addAll(getSession().getUser().getGroups());
		receivers.addAll(allUsers);
		for (MessageReceiver mr : receivers) {
			if (mr.getDisplayName().toLowerCase().contains(query.toLowerCase())
					&& !mr.getDisplayName().equals(user.getDisplayName())) {
				result.add(mr);
			}
		}
		return result;
	}
}
