package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.Telephone;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.TelephoneDAO;

import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.model.TelephoneNode;

import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.OrganigramNode;

/**
 * Dieser Controller verwaltet die Telefonkette. Er generiert die Kette beim
 * Seitenaufruf und stellt Methoden zum Selektieren und Speichern von Änderungen
 * innerhalb der Kette bereit.
 * 
 * @author Mirco Bockholt
 * @author Marco Glander
 */

@Named("telephoneBean")
@ViewScoped
public class TelephoneController extends AbstractController {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 4722790806367250294L;

	/** DAO für den User */
	private final UserDAO userDao;
	/** DAO für Telephone */
	private final TelephoneDAO telephoneDao;

	/** Selektierter Knoten */
	private TelephoneNode selectedNode;

	/** Selektiertes Telephone Objekt */
	private Telephone selectedTelephone;

	/** Der zugehörige Benutzer */
	private User user;

	/** Das Diagramm-Modell */
	private OrganigramNode root = null;

	/**
	 * Erzeugt einen {@link TelephoneController} mit definierter
	 * {@link Session}, {@link UserDAO} und {@link TelephoneDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden
	 * 		{@link TelephoneController}s.
	 * @param pUserDao
	 * 		Die {@link UserDAO} des zu erzeugenden
	 * 		{@link TelephoneController}s.
	 * @param pTelephoneDao
	 * 		Die {@link TelephoneDAO} des zu erzeugenden
	 * 		{@link TelephoneController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public TelephoneController(final Session pSession,
							   final UserDAO pUserDao,
							   final TelephoneDAO pTelephoneDao) {
		super(Assertion.assertNotNull(pSession));
		userDao = Assertion.assertNotNull(pUserDao);
		telephoneDao = Assertion.assertNotNull(pTelephoneDao);
	}

	/**
	 * Wird nach dem Konstrukor ausgeführt (beim Aufruf der Seite). Besitzt der
	 * Benutzer noch keine Telefonkette, so wird eine neue für diesen generiert.
	 * Zudem wird die Struktur der Telefonkette erstellt und gefüllt.
	 */
	@PostConstruct
	public void init() {
		user = getSession().getUser();
		selectedNode = new TelephoneNode();
		selectedTelephone = null;

		if (user.getTelephones().size() == 0) {
			for (int i = 0; i < 31; i++) {
				Telephone tf = new Telephone(user, "Person " + i, "");
				telephoneDao.create(tf);
				user.addTelephone(tf);
			}
			try {
				userDao.update(assertNotNull(user));
			} catch (final IllegalArgumentException e) {
				addMessage("errorIllegalArgument");
			} catch (final DuplicateUsernameException e) {
				addMessageWithLogging("registerUserForm:username", e, getLogger(), Level.DEBUG,
						"errorUsernameAlreadyInUse", getSession().getUser().getLogin());
			} catch (final DuplicateEmailException e) {
				addMessageWithLogging("registerUserForm:email", e, getLogger(), Level.DEBUG, "errorEmailAlreadyInUse",
						getSession().getUser().getEmail());
			}
		}
		Collections.sort(user.getTelephones());

		List<TelephoneNode> nodeList = new ArrayList<>();
		for (int i = 0, parent = 0; nodeList.size() < 23; i++) {
			TelephoneNode newNode = new TelephoneNode(user.getTelephones().get(i).getName(),
					user.getTelephones().get(i).getNumber(), i > 0 ? nodeList.get(parent) : null);
			newNode.setSelectable(true);
			newNode.setTelephone(user.getTelephones().get(i));
			nodeList.add(newNode);
			if (i < 15 && i % 2 == 0 && i > 0)
				parent++;
			else if (i >= 15)
				parent++;
		}
		root = nodeList.get(0);
	}

	public void update() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save telephone values!");
			return;
		}
		try {
			telephoneDao.update(assertNotNull(selectedTelephone));
			addMessage("successfulEditTelephone");
		} catch (final IllegalArgumentException e) {
			addMessage("errorIllegalArgument");
		}
		init();
	}

	public TelephoneNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TelephoneNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public Telephone getSelectedTelephone() {
		return selectedTelephone;
	}

	public void setSelectedTelephone(Telephone selectedTelephone) {
		this.selectedTelephone = selectedTelephone;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public void setUser(User user) {
		this.user = user;
	}

	public OrganigramNode getRoot() {
		return root;
	}

	public void setRoot(OrganigramNode root) {
		this.root = root;
	}
	
	public void nodeSelectListener(OrganigramNodeSelectEvent event) {
        selectedNode = (TelephoneNode) event.getOrganigramNode();
        selectedTelephone = selectedNode.getTelephone();
    }

}
