package de.unibremen.gradelog.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.CustomPage;
import de.unibremen.gradelog.model.Message;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.Task;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.CustomPageDAO;
import de.unibremen.gradelog.persistence.UserDAO;

import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Logger;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.PieChartModel;

/**
 * Ermöglicht das Konfigurieren des Dashboards.
 * 
 * @author Rune Krauss
 */
@Named("dashboardBean")
@ViewScoped
public class DashboardController extends AbstractController {
	/**
	 * Model, welches die Spalten vom Dashboard enthält
	 */
	private DefaultDashboardModel model;

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * User-Objekte übernimmt.
	 */
	private final UserDAO userDAO;

	private final CustomPageDAO customPageDAO;

	/**
	 * Der aktuelle Benutzer
	 */
	private User user;

	/**
	 * Widgets im Dashboard
	 */
	private List<String> widgets;

	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = -6560012086857165997L;

	/**
	 * Erzeugt einen {@link DashboardController} mit definierter
	 * {@link Session} und {@link UserDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden
	 * 		{@link DashboardController}s.
	 * @param pUserDAO
	 * 		Die {@link UserDAO} des zu erzeugenden
	 * 		{@link DashboardController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public DashboardController(final Session pSession,
							   final UserDAO pUserDAO,
							   final CustomPageDAO pCustomPageDAO) {
		super(Assertion.assertNotNull(pSession));
		userDAO = Assertion.assertNotNull(pUserDAO);
		customPageDAO = Assertion.assertNotNull(pCustomPageDAO);
	}

	/**
	 * Initialisiert das Dashboard mit den jeweiligen Widgets und merkt sich den
	 * aktuellen Benutzer.
	 */
	@PostConstruct
	public void init() {
		user = getSession().getUser();
		widgets = user.getWidgets();
		model = new DefaultDashboardModel();
		DashboardColumn column = new DefaultDashboardColumn();
		for (String s : widgets)
			column.addWidget(s);
		model.addColumn(column);
	}

	/**
	 * Wird ausgelöst, sobald das Dashboard geändert, d. h. ein Widget
	 * verschoben wird. Demnach regelt es die jeweilige Anpassung des
	 * Dashboards.
	 * 
	 * @param event
	 *            Geändertes Widget
	 */
	public void handleReorder(final DashboardReorderEvent event)
			throws DuplicateUsernameException, DuplicateEmailException {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to reorder a dashboard!");
			return;
		}
		widgets.remove(event.getWidgetId());
		widgets.add(event.getItemIndex(), event.getWidgetId());
		user.setWidgets(widgets);
		userDAO.update(user);
	}

	/**
	 * Ermittelt die nächsten fünf Tasks, die als nächstes anliegen, wenn in den
	 * Einstellungen des Benutzers die Flag 'showAllTodos' auf 'False' gesetzt
	 * ist.
	 * 
	 * @return Tasks
	 */
	public List<Task> getLastTodos() {
		List<Task> tasks = new ArrayList<>();
		tasks.addAll(user.getCalendar().getTasks());
		Collections.sort(tasks);
		if (!user.isActivatedAllTodos()) {
			List<Task> lastTasks = new ArrayList<>();
			Task task = new Task();
			task.setStartTime(new java.sql.Date(new java.util.Date().getTime()));
			int i = 0;
			for (Task t : tasks) {
				if (i == 5)
					break;
				if (t.compareTo(task) >= 0) {
					lastTasks.add(t);
					i++;
				}
			}
			return lastTasks;
		}
		return tasks;
	}

	/**
	 * Berechnet die letzten fünf empfangenen Nachrichten für einen Benutzer.
	 * 
	 * @return Empfangene Nachrichten
	 */
	public List<Message> getLastMessages() {
		List<Message> messages = new ArrayList<>();
		messages.addAll(user.getMessages());
		Collections.sort(messages);
		Collections.reverse(messages);
		List<Message> lastMessages = new ArrayList<>();
		for (Message m : messages) {
			if (!m.getReadStatus(user.getReceiverName()) && !m.getAuthor().equals(user.getReceiverName()))
				lastMessages.add(m);
		}
		return lastMessages;
	}

	/**
	 * Ermittelt die letzten fünf eigenen erstellten Seiten.
	 * 
	 * @return Eigene Seiten
	 */
	public List<CustomPage> getLastCustomPages() {
		List<CustomPage> pages = new ArrayList<>();
		pages.addAll(customPageDAO.getOwnedByUser(user));
		Collections.reverse(pages);
		List<CustomPage> lastPages = new ArrayList<>();
		int i = 5;
		for (CustomPage cp : pages) {
			if (i > 0)
				lastPages.add(cp);
			else
				break;
			i--;
		}
		return lastPages;
	}

	/**
	 * Ermittelt die letzten fünf geteilten Seiten eines Benutzers.
	 * 
	 * @return Geteilte Seiten
	 */
	public List<CustomPage> getLastSharedPages() {
		List<CustomPage> pages = userDAO.getSharedPages(user);
		Collections.reverse(pages);
		List<CustomPage> lastPages = new ArrayList<>();
		int i = 0;
		for (CustomPage cp : pages) {
			if (i < 5)
				lastPages.add(cp);
			i++;
		}
		return lastPages;
	}

	/**
	 * Ermittelt die Besucherzahlen der eigenen Seiten.
	 * 
	 * @return Eigene Seiten
	 */
	private List<CustomPage> getCountedCustomPages() {
		List<CustomPage> pages = userDAO.getSharedPages(user);
		Collections.sort(pages, (cp, cp2) ->
				cp.getCounter() - cp2.getCounter());
		List<CustomPage> countedPages = new ArrayList<>();
		int i = 0;
		for (CustomPage cp : pages) {
			if (i < 5)
				countedPages.add(cp);
			else
				break;
			i++;
		}
		return countedPages;
	}

	/**
	 * Erstellt anhand der besuchten eigenen Seiten ein Kuchendiagramm mit den
	 * jeweiligen Besucherzahlen der jeweiligen Seite.
	 * 
	 * @return Diagramm über Besucherzahlen der eigenen Seiten.
	 */
	public PieChartModel getPieModel() {
		PieChartModel pieModel = new PieChartModel();
		pieModel.setLegendPosition("w");
		List<CustomPage> countedCustomPages = getCountedCustomPages();
		pieModel.setTitle("empty");
		for (CustomPage page : countedCustomPages) {
			pieModel.set(page.getTitle(), page.getCounter());
			pieModel.setTitle("");
		}
		pieModel.setLegendPlacement(LegendPlacement.OUTSIDE);
		pieModel.setLegendPosition("w");
		return pieModel;
	}

	public void handleClose(CloseEvent event) {
	}

	public void handleToggle(ToggleEvent event) {
	}

	public DashboardModel getModel() {
		return model;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}
}
