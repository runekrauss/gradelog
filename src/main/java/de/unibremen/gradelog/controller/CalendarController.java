package de.unibremen.gradelog.controller;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.*;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Logger;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.SelectEvent;

import de.unibremen.gradelog.persistence.CalendarDAO;
import de.unibremen.gradelog.persistence.TaskDAO;

/**
 * Dieser Controller kümmert sich rund um die Logik hinsichtlich des Kalenders
 * für die Taks, d. h. um das Löschen bzw. Hinzufügen von Klausurterminen,
 * Übungsterminen usw.
 * 
 * @author Rune Krauss
 * @author Marco Glander
 */
@Named("calendar")
@ViewScoped
public class CalendarController extends AbstractController {
	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = -4873369747607192122L;
	/**
	 * Kalender
	 */
	private Calendar calendar;
	/**
	 * Task
	 */
	private Task task;
	/**
	 * Aktiver Benutzer
	 */
	private User user;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Kalender-Objekte übernimmt.
	 */
	private final CalendarDAO calendarDAO;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Task-Objekte übernimmt.
	 */
	private final TaskDAO taskDAO;

	/**
	 * Erzeugt einen {@link CalendarController} mit definierter
	 * {@link Session}, {@link CalendarDAO} und {@link TaskDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link CalendarController}s.
	 * @param pCalendarDAO
	 * 		Die {@link CalendarDAO} des zu erzeugenden
	 * 		{@link CalendarController}s.
	 * @param pTaskDAO
	 * 		Die {@link TaskDAO} des zu erzeugenden {@link CalendarController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public CalendarController(final Session pSession,
							  final CalendarDAO pCalendarDAO,
							  final TaskDAO pTaskDAO) {
		super(Assertion.assertNotNull(pSession));
		calendarDAO = Assertion.assertNotNull(pCalendarDAO);
		taskDAO = Assertion.assertNotNull(pTaskDAO);
	}

	/**
	 * Initialisiert das Task-Modul mit dem Kalender des jeweiligen Benutzers.
	 */
	@PostConstruct
	public void init() {
		calendar = getSession().getUser().getCalendar();
		user = getSession().getUser();
		task = new Task();
	}

	/**
	 * Hiermit können Tasks hinzugefügt werden. Sollte der Kalender dieses Task
	 * bereits enthalten, so wird es dementsprechend aktualisiert.
	 * 
	 * @param actionEvent
	 *            Jeweilige Aktion
	 */
	public void addTask(ActionEvent actionEvent) {
		if (actionEvent == null)
			return;
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to save a task!");
			return;
		}
		if (task.getStartTime().getTime() > task.getEndTime().getTime()) {
			addMessage("errorInvalidTime");
			return;
		}
		if (!calendar.getTasks().contains(task)) {
			taskDAO.create(assertNotNull(task));
			calendar.addTask(task);
		}
		taskDAO.update(assertNotNull(task));
		calendarDAO.update(assertNotNull(calendar));
		addMessage("successTodoAdded");
		task = new Task();
	}

	/**
	 * Hiermit können Tasks aus dem Kalender gelöscht werden.
	 * 
	 * @param actionEvent
	 *            Jeweilige Aktion
	 */
	public void removeTask(ActionEvent actionEvent) {
		if (actionEvent == null)
			return;
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to remove a task!");
			return;
		}
		calendar.removeTask(task);
		calendarDAO.update(assertNotNull(calendar));
		taskDAO.delete(assertNotNull(task));
		addMessage("successTodoDeleted");
		task = new Task();
	}

	/**
	 * Wird ausgelöst, sobald man einen Termin in dem Kalender selektiert. Dabei
	 * wird der Zeiger auf den aktuellen Task gelegt.
	 * 
	 * @param selectEvent
	 *            Task
	 */
	public void onEventSelect(SelectEvent selectEvent) {
		if (selectEvent == null)
			return;
		task = ((CalendarEvent) selectEvent.getObject()).getTask();
	}

	/**
	 * Wird ausgelöst, sobald man einen Termin in dem Kalender bewegt.
	 * 
	 * @param moveEvent
	 *            Task
	 */
	public void onEventMove(ScheduleEntryMoveEvent moveEvent) {
		task = ((CalendarEvent) moveEvent.getScheduleEvent()).getTask();
		if (moveEvent == null)
			return;
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to edit a task!");
			return;
		}
		if (task.getStartTime().getTime() > task.getEndTime().getTime()) {
			addMessage("errorInvalidTime");
			return;
		}
		taskDAO.update(assertNotNull(task));
		calendarDAO.update(assertNotNull(calendar));
		addMessage("successTodoMoved");
	}

	/**
	 * Wird ausgelöst, sobald man ein Datum selektiert. Dabei werden Start- und
	 * Enddatum zu dem jeweiligen Task gespeichert.
	 * 
	 * @param selectEvent
	 *            Datum
	 */
	public void onDateSelect(SelectEvent selectEvent) {
		if (selectEvent == null)
			return;
		task = new Task();
		task.setStartTime((Date) selectEvent.getObject());
		task.setEndTime((Date) selectEvent.getObject());
	}

	/**
	 * Wenn der Benutzer den Erinnerungsdienst eingeschaltet hat, so wird eine
	 * Meldung in jedem Bereich des Systems dargestellt, dass bald ein Termin
	 * anliegt.
	 * 
	 * @return Statusnachricht des Tasks
	 */
	public boolean reminder() {
		if (user.isActivatedReminder()) {
			Set<Task> tasks = calendar.getTasks();
			for (Task t : tasks) {
				long start = t.getStartTime().getTime();
				long current = new java.util.Date().getTime();
				long dif = start - current;
				long res = TimeUnit.MILLISECONDS.toMinutes(dif);
				boolean isDaylight = java.util.TimeZone.getTimeZone("Europe/Berlin").inDaylightTime( new Date() );
				res = isDaylight ? res - 60 : res;
				if (res < 60 && res > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(final Calendar calendar) {
		this.calendar = calendar;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(final Task task) {
		this.task = task;
	}
	
	public boolean renderDeleteButton()
	{
		return task != null && task.getId() > 0;
	}
}