package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.CustomPage;
import de.unibremen.gradelog.model.CustomPageHistory;
import de.unibremen.gradelog.model.Group;
import de.unibremen.gradelog.model.MessageReceiver;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.Shareable;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.persistence.CustomPageDAO;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.GroupDAO;

/**
 * Der CustomPageConstroller stellt Funktionen zu Interaktion mit CustomPage
 * Models zur Verfügung. Vorallem das Herausgeben von Daten für die Facelets
 * sowie Methoden zum speichern/updaten und löschen in der Datenbank.
 * 
 * @author Christopher Wojtkow
 *
 */
@Named("pageBean")
@ViewScoped
public class CustomPageController extends AbstractController {
	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 2837617459897310046L;
	/**
	 * Die aktuell zubearbeitende CustomPage, dessen Attribute durch die
	 * UIKomponenten des Facelets geschrieben und gelesen werden.
	 */
	private CustomPage customPage;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * CustomPage-Objekte übernimmt.
	 */
	private final CustomPageDAO customPageDao;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Benutzer-Objekte übernimmt.
	 */
	private final UserDAO userDao;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Gruppen-Objekte übernimmt.
	 */
	private final GroupDAO groupDao;
	/**
	 * Hält nach einem redirect die ID der zu bearbeitenden CustomPage.
	 */
	private String data;
	/**
	 * Typ von Seite für die der CustompageController gerade zuständig ist. Kann
	 * "Show" oder Leer sein
	 */
	private String type = " ";
	/**
	 * Hält den User dieser Session
	 */
	private User user;
	/**
	 * Autocomplete
	 */
	private List<String> selectedReceivers;
	/**
	 * Liste zum Sortieren
	 */
	private List<CustomPage> customPages;
	/**
	 * Liste zum Sortieren
	 */
	private List<CustomPage> sharedPages;
	/**
	 * Liste zum Sortieren
	 */
	private List<CustomPageHistory> histories;
	private CustomPage lastPage;

	/**
	 * Erzeugt einen {@link CustomPageController} mit definierter
	 * {@link Session}, {@link CustomPageDAO}, {@link UserDAO} und
	 * {@link GroupDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erstellenden
	 * 		{@link CustomPageController}s.
	 * @param pCustomPageDao
	 * 		Die {@link CustomPageDAO} des zu erstellenden
	 * 		{@link CustomPageController}s.
	 * @param pUserDao
	 * 		Die {@link UserDAO} des zu erstellenden
	 * 		{@link CustomPageController}s.
	 * @param pGroupDao
	 * 		Die {@link GroupDAO} des zu erstellenden
	 * 		{@link CustomPageController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public CustomPageController(final Session pSession,
								final CustomPageDAO pCustomPageDao,
								final UserDAO pUserDao,
								final GroupDAO pGroupDao) {
		super(Assertion.assertNotNull(pSession));
		customPageDao = Assertion.assertNotNull(pCustomPageDao);
		userDao = Assertion.assertNotNull(pUserDao);
		groupDao = Assertion.assertNotNull(pGroupDao);
	}

	/**
	 * Stellt sofort nach dem Aufruf des Konstruktors eine neue leere CustomPage
	 * bereit und instanziert Attribute, die - auf null gesetzt - zu
	 * unerwartetem Verhalten führen können.
	 */
	@PostConstruct
	public void init() {
		customPage = new CustomPage();
		selectedReceivers = new ArrayList<>();
		user = getSession().getUser();
	}

	/**
	 * onRedirect wird durch das Laden einer CustomPage via show.xhtml
	 * ausgeführt und führt selbst Methoden aus, die nach der Konstruktion der
	 * View, das Model noch aktualisieren. Kann nur korrekt funktionieren nach
	 * Konstruktion der View durch init().
	 */
	public void onRedirect(final String data, final String redirectType) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to execute Methods on redirect.");
			return;
		}
		if (redirectType != null)
			type = redirectType;
		if (type.equals("show")) {
			setCustomPageInBeanById(data, true);
		} else {
			setCustomPageInBeanById(data, false);
		}
	}

	/**
	 * Seiten-Redirect mit übergebener CustomPage-ID. Ist mindestens einer der
	 * Parameter null, so wird ein leerer String zurückgegeben.
	 * 
	 * @return show, die nächste seite
	 */
	public String redirect(final String ident, final String next) {
		if (null == ident || null == next)
			return "";
		return next + "?faces-redirect=true&id=" + ident;
	}

	/**
	 * Speichert das übergebene Model in der Datenbank. Wurde beim Laden 'data'
	 * auf eine id gesetzt, so überschreibt die Methode den Inhalt des Models
	 * mit der ID assozierten CustomPage, schreibt ihren alten Inhalt in eine
	 * CustomPageHistory, assoziert diesen mit dem Model und updated abhängige
	 * Model's über die jeweiligen DAO's.
	 * 
	 * Die Methode liefert nur korrekte Ergebnisse, wenn davor onRedirect()
	 * aufgerufen wurde.
	 * 
	 * @param cp
	 *            Die zu aktualisierende CustomPage
	 */
	public void save(final CustomPage cp) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user saved a CustomPage.");
			return;
		}
		try {
			if (null == data) {
				saveNew(cp);
			} else {
				saveEdit(cp);
			}

		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorPageOrUserNull");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
					"errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("CustomPageController: Email ", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
					user.getEmail());
		}
	}

	/**
	 * Speichert die übergebene Custompage in der Datenbank ab.
	 * 
	 * @param cp
	 *            Dies abzuspeichernde CustomPage
	 * @throws DuplicateUsernameException
	 *             Wenn die UserDAO die Exception wirft.
	 * @throws DuplicateEmailException
	 *             Wenn die UserDAO die Exception wirft.
	 * @throws IllegalArgumentException
	 *             Wenn {@link Assertion#assertNotNull} feststellt dass cp oder
	 *             der Controller-interne User null ist.
	 */
	private void saveNew(final CustomPage cp)
			throws DuplicateUsernameException, DuplicateEmailException, IllegalArgumentException {
		if (cp.getContent().length() > 8192) {
			addMessage("errorMaxLength");
			return;
		}
		customPage.setUser(assertNotNull(user));
		customPage.setDate(new Date());
		customPageDao.create(assertNotNull(cp));
		parseReceivers(cp);
		addMessage("successPageSaved");
	}

	/**
	 * Parst selectedReceivers und aktualisiert die freigegebenen CustomPages
	 * für Nutzer/Gruppen.
	 * 
	 * @param cp
	 * @throws DuplicateUsernameException
	 * @throws DuplicateEmailException
	 */
	private void parseReceivers(final CustomPage cp) throws DuplicateUsernameException, DuplicateEmailException {
		if (null != selectedReceivers) {
			for (String parse : selectedReceivers) {
				Shareable found = userDao.getUserByEmail(parse);
				if (null == found) {
					found = groupDao.getGroupByName(parse);
					if (null != found) {
						cp.addGroup((Group) found);
						found.addSharedPage(cp);
						groupDao.update((Group) found);
						customPageDao.update(cp);
					}
				} else {
					cp.addSub((User) found);
					found.addSharedPage(cp);
					userDao.update((User) found);
					customPageDao.update(cp);
				}
			}
		}
	}

	/**
	 * Speichert den aktuellen Stand der CustomPage in eine CusomPageHistory und
	 * aktualisiert die Custompage mit den Änderungen im Parameter cp, es wird
	 * hier davon ausgegangen, dass onRedirect davor aufgerufen wurde, sodass
	 * sichergestellt ist, dass 'data' validen Inhalt hat.
	 * 
	 * @param cp
	 *            Die CustomPage, dessen Zustand übernommen werden soll.
	 * @throws IllegalArgumentException
	 *             Wenn {@link Assertion#assertNotNull} feststellt, dass cp
	 *             oder der Controller-interne User null ist.
	 */
	private void saveEdit(final CustomPage cp) throws IllegalArgumentException {
		if (cp.getContent().length() > 8192) {
			addMessage("errorMaxLength");
			return;
		}
		CustomPage found = getCustomPageById(data, false);
		assertNotNull(found);
		assertNotNull(cp);
		CustomPageHistory history = new CustomPageHistory(found.getTitle(), found.getContent(), found.getDate(), found);
		found.saveToHistory(history);
		found.apply(cp, new Date());
		customPageDao.update(found);
		addMessage("successPageUpdated");
	}

	/**
	 * Löscht das aktuelle CustomPage-Model des Controllers aus der Datenbank
	 */
	public void remove() {
		remove(customPage);

	}

	/**
	 * Löscht das selektierte customPage-Model des Controllers aus der Datenbank
	 */
	public void removeSelectedPage() {
		remove(getSelectedPage());
	}

	/**
	 * Löscht die übergebene customPage aus der Datenbank und räumt Referenzen
	 * durch das andere Model auf.
	 * 
	 * @param cp
	 *            Die zu löschende CustomPage
	 * @throws IllegalArgumentException
	 *             Wenn {@link Assertion#assertNotNull} feststellt dass cp oder
	 *             der Controller-interne User null ist.
	 */
	public void remove(final CustomPage cp) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to remove a CustomPage.");
			return;
		}
		try {
			assertNotNull(user);
			assertNotNull(cp);
			List<Shareable> share = getSharedList(cp);
			for (Shareable s : share) {
				s.removeSharedPage(cp);
				if ("(G)".equals(s.getType()))
					groupDao.update((Group) s);
				else
					userDao.update((User) s);
			}
			customPageDao.delete(cp);
			addMessage("successPageRemoved");
		} catch (final IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorPageOrUserNull");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
					"errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("CustomPageController: Email ", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
					user.getEmail());
		}
	}

	/**
	 * Wendet die übergebene CustomPageHistory auf das übergebene CustomPage
	 * Objekt an. Dabei wird der alte Status wieder als CustomPageHistory
	 * hinzugefügt und daraufhin die bearbeitete Custompage in der Datenbank
	 * aktualisiert.
	 * 
	 * @param cp
	 *            Die zu bearbeitende CustomPage
	 * @param cph
	 *            Die anzuwendende CustomPageHistory
	 * @throws IllegalArgumentException
	 *             Wenn {@link Assertion#assertNotNull} feststellt dass cp oder
	 *             der Controller-interne User null ist.
	 */
	public void applyHistory(final CustomPage cp, final CustomPageHistory cph) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to update a CustomPage with a CustomPageHistory.");
			return;
		}
		try {
			assertNotNull(user);
			assertNotNull(cph);
			assertNotNull(cp);
			CustomPage current = cp;
			CustomPageHistory history = new CustomPageHistory(current.getTitle(), current.getContent(),
					current.getDate(), current);
			current.saveToHistory(history);
			current.applyHistory(assertNotNull(cph));
			customPageDao.update(current);
			addMessage("successPageHistoryUpdated");
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorPageUserHistoryNull");
		}
	}

	/**
	 * Löscht die angegebene CustomPageHistory aus der übergebenen CustomPage
	 * und aktualisiert daraufhin die Datenbank.
	 * 
	 * @param cp
	 *            Die zu bearbeitende CustomPage
	 * @param cph
	 *            Die zu löschende CustomPageHistory
	 * @throws IllegalArgumentException
	 *             Wenn {@link Assertion#assertNotNull} feststellt dass cp oder
	 *             der Controller-interne User null ist.
	 */
	public void removeHistory(final CustomPage cp, final CustomPageHistory cph) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to remove CustomPageHistory from a CustomPage.");
			return;
		}
		try {
			assertNotNull(user);
			CustomPage current = assertNotNull(cp);
			current.removeHistory(assertNotNull(cph));
			customPageDao.update(current);
			addMessage("successPageHistoryRemoved");
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorPageUserHistoryNull");
		}
	}

	/**
	 * Fügt dem übergebenen Custompage Objekt die {@link #selectedReceivers}
	 * freigegebenen Gruppen und User hinzu. Entsprechende Referenzen werden in
	 * den Group und User Objekten gesetzt und daraufhin in der Datenbank
	 * aktualisiert.
	 * 
	 * @param cp
	 *            Die zu bearbeitende CustomPage
	 * @throws IllegalArgumentException
	 *             Wenn {@link Assertion#assertNotNull} feststellt dass cp null
	 *             ist.
	 */
	public void addShareable(final CustomPage cp) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to update a CustomPage with a Shareable.");
			return;
		}
		try {
			assertNotNull(cp);
			if (!user.equals(cp.getUser()))
				throw new IllegalArgumentException();
			parseReceivers(cp);
			addMessage("successPageSharedUpdated");
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorPageOrSharedNull");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
					"errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("CustomPageController: Email ", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
					user.getEmail());
		}
	}

	/**
	 * Löscht den derzeitigen user aus den freigegebenen Shareables der
	 * CustomPage
	 * 
	 * @param cp
	 *            Die zubearbeitende CustomPage
	 */
	public void removeShareable(final CustomPage cp) {
		removeShareable(cp, user);
	}

	/**
	 * Löscht die übergebene Instanz eines Shareables aus der übergebenen
	 * CustomPage und löscht entsprechende Referenzen in dem Shareable.
	 * Anschließend wird die Datenbank aktualisiert.
	 * 
	 * @param cp
	 *            Die zubearbeitende CustomPage
	 * @param shared
	 *            Die zuentfernende Gruppe
	 */
	public void removeShareable(final CustomPage cp, final Shareable shared) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to remove Shareable from a CustomPage.");
			return;
		}
		try {
			CustomPage current = cp;
			assertNotNull(shared);
			assertNotNull(cp);
			current.removeShareable(shared);
			shared.removeSharedPage(cp);
			customPageDao.update(current);
			if (shared.getType().equals("(G)"))
				groupDao.update((Group) shared);
			else
				userDao.update((User) shared);
			addMessage("successPageSharedRemoved");
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorPageSharedNull");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
					"errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("CustomPageController: Email", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
					user.getEmail());
		}
	}

	/**
	 * Gibt für die Übergebene id (als String) die zugeordnete CustomPage
	 * zurück. Sofern die View durch ein show.xhtml konstruiert wurde, wird auch
	 * der Besucherzähler der Seite hochgezählt.
	 * 
	 * @param identification
	 *            Die page-ID als String
	 * @param onlyShow
	 *            boolean der dem User erlaubt die mit der ID assozierten
	 *            CustomPage einzusehen. True wenn show.xhtml die Methode
	 *            ausführt, false sonst
	 * @return Die gesucht Seite falls Zugriffsrechte vorhanden sind, die Seite
	 *         vorhanden ist und im Rahmen von onlyShow Zugriff gewährt werden
	 *         soll. Sonst null.
	 */
	private CustomPage getCustomPageById(final String identification, final boolean onlyShow) {
		Logger logger = getLogger();
		if (identification != null && !identification.equals("")) {
			try {
				int ident = Integer.parseInt(identification);
				CustomPage found = customPageDao.getById(ident);
				// Page gefunden && user war Besitzer
				if (found != null && user.equals(found.getUser())) {
					if (type.equals("show")) {
						found.increment();
						customPageDao.update(found);
					}
					return found;
				}
				// Page gefunden && es war show.xhtml && User besitzt eine
				// Freigabe zur seite
				if (found != null && onlyShow && contains(found)) {
					found.increment();
					customPageDao.update(found);
					return found;
					// Page nicht gefunden || Aufruf nicht durch show erfolgt ||
					// User hat überhaupt keinen Zugriff
				} else {
					return null;
				}
			} catch (NumberFormatException e) {
				addMessageWithLogging(e, logger, Level.DEBUG, "errorCustomPageNotOwned");
			}
		}
		return null;
	}

	/**
	 * Überprüft, ob der derzeitige User Zugriff auf die übergebene Custompage
	 * hat.
	 * 
	 * @param cp
	 *            Die Custompage deren sub-user und Gruppen nach dem User
	 *            durchsucht werden sollen
	 * @return true wenn der User gefunden wurde, sonst false
	 */
	private boolean contains(final CustomPage cp) {
		ArrayList<User> list = new ArrayList<>();
		list.addAll(cp.getSubs());
		for (Group g : cp.getGroups()) {
			list.addAll(g.getUsers());
		}
		return list.contains(user);
	}

	/**
	 * Setzt eine CustomPage, die mit der ID assoziert ist, als
	 * {@link #customPage} im CustomPage-Controller. Falls der User im Kontext
	 * des Aufrufes keine Zu- griffsrechte besitzt, so wird im Contoller eine
	 * Neue CustomPage gesetzt.
	 * 
	 * @param ident
	 *            Die page-ID als String (zur Nutzung durch jsf)
	 * @param onlyShow
	 *            Ob der Aufruf mit show Berechtigung ausgeführt wurde.
	 * 
	 */
	private void setCustomPageInBeanById(final String ident, final boolean onlyShow) {
		if (null != ident)
			this.customPage = getCustomPageById(ident, onlyShow);
		if (null == customPage)
			this.customPage = new CustomPage();
	}

	/**
	 * Gibt eine Liste an CustomPage-Objekten zurück, die der derzeitige User
	 * erzeugt hat.
	 * 
	 * @return Liste an CustomPage-Objekten
	 */
	public List<CustomPage> getCustomPages() {
		if(null == customPages) {
			customPages = customPageDao.getOwnedByUser(user);
			return customPages;
		} else return customPages;
	}

	/**
	 * Gibt eine Liste an CustomPage-Objekten zurück, auf die der derzeitige
	 * User Zugriff hat. Die Liste entspricht einer Menge von allen zugreifbaren
	 * Seiten.
	 *
	 * @return Liste von Seiten, die dem User via Gruppen oder ihm persönlich
	 *         geteilt wurden.
	 */
	public List<CustomPage> getSharedPages() {
		if(null == sharedPages) {
			sharedPages = userDao.getSharedPages(user);
			return sharedPages;
		} else return sharedPages;
	}

	/**
	 * Gibt für die übergebene CustomPage eine Liste von CustomPageHistory
	 * Objekten zurück. Ist die CustomPage {@code null}, so wird eine leere
	 * Liste zurückgegeben.
	 * 
	 * @param page
	 *            Die CustomPage, dessen Versionen aufgelistet werden sollen.
	 * @return Versionen der CustomPage (ohne den aktuellen Zustand)
	 */
	public List<CustomPageHistory> getHistoryList(final CustomPage page) {
		if (null == page) {
			return new ArrayList<>();
		} else if(null == histories || !page.equals(lastPage)) {
			lastPage = page;
			histories = new ArrayList<>();
			histories.addAll(page.getHistories());
			return histories;
		} else{
			return histories;
		}
	}

	/**
	 * Gibt für eine übergebene CustomPage eine Liste an Shareable-Objekten
	 * zurück. Diese Liste enthält alle Gruppen und User, die dem derzeitigen
	 * User die übergebene Custompage freigegeben haben.
	 * 
	 * @param page
	 *            Die CustomPage, deren "Freigeber" ermittelt werden sollen
	 * @return Die Liste an Sharable's, die dem user die übergebene Seite
	 */
	public List<Shareable> getSharers(final CustomPage page) {
		ArrayList<Shareable> sharerList = new ArrayList<>();
		if (null == page)
			return sharerList;
		else {
			HashSet<Shareable> sharers = new HashSet<>();
			sharers.add(page.getUser());
			for (Group g : page.getGroups()) {
				if (g.getUsers().contains(user))
					sharers.add(g);
			}
			sharerList.addAll(sharers);
			return sharerList;
		}
	}

	/**
	 * Gibt eine Liste von Usern und Gruppe zurück, die Zugriff auf die
	 * übergebene CustomPage haben. Ist die CustomPage {@code null}, so wird
	 * eine leere Liste zurückgegeben.
	 * 
	 * @param page
	 *            Die CustomPage, dessen Zugriffsberechtigte aufgelistet werden
	 *            sollen.
	 * @return
	 */
	public List<Shareable> getSharedList(final CustomPage page) {
		ArrayList<Shareable> shared = new ArrayList<>();
		if (page == null) {
			return shared;
		} else {
			shared.addAll(page.getSubs());
			shared.addAll(page.getGroups());
			return shared;
		}
	}

	public List<String> getSelectedReceivers() {
		return selectedReceivers;
	}

	public void setSelectedReceivers(final List<String> selectedReceivers) {
		this.selectedReceivers = selectedReceivers;
	}

	/**
	 * Vervollständigt für einen eingegebenen String die Eingabe, sofern es User
	 * oder Gruppen gibt, deren Name den String enthält.
	 * 
	 * @param query
	 *            Der zu vervollständigende Name eines Users oder einer Group
	 * @return Die Liste aller User und Gruppen (um ein Objekt erweitert sofern
	 *         die Vervollständigung erfolgreich war)
	 */
	public List<MessageReceiver> completeReceiver(final String query) {
		List<MessageReceiver> receivers = new ArrayList<>();
		List<MessageReceiver> result = new ArrayList<>();
		receivers.addAll(user.getGroups());
		receivers.addAll(userDao.getAllUsers());
		for (MessageReceiver mr : receivers) {
			if (mr.getDisplayName().toLowerCase().contains(query.toLowerCase())) {
				result.add(mr);
			}
		}
		return result;
	}

	public void onFileClick(String file) {
		customPage.setContent(customPage.getContent() + file);
	}

	public CustomPage getCustomPage() {
		return customPage;
	}

	public void setCustomPage(final CustomPage cp) {
		this.customPage = cp;
	}

	public String getData() {
		return data;
	}

	public void setData(final String data) {
		this.data = data;
	}
	
	//+----------------+
	//|Table Selections|
	//+----------------+
	
	CustomPage selectedPage = null;
	CustomPageHistory selectedHistory = null;
	Shareable selectedShared = null;
	
	public CustomPage getSelectedPage() {
		return selectedPage;
	}

	public void setSelectedPage(final CustomPage selectedPage) {
		this.selectedPage = selectedPage;
	}

	public CustomPageHistory getSelectedHistory() {
		return selectedHistory;
	}

	public void setSelectedHistory(final CustomPageHistory selectedHistory) {
		this.selectedHistory = selectedHistory;
	}

	public Shareable getSelectedShared() {
		return selectedShared;
	}

	public void setSelectedShared(final Shareable selectedShared) {
		this.selectedShared = selectedShared;
	}

	/**
	 * Ob das Autocomplete der new.xhtml deaktivert sein soll
	 * 
	 * @return true, wenn data gesetzt ist, sonst false
	 */
	public boolean autoCompleteDisabled() {
		return data != null;
	}
}
