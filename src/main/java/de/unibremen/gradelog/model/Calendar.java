package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import de.unibremen.gradelog.model.Task;

/**
 * Steht für den Kalender mit Daten wie den Tasks und dem Benutzer, welcher das
 * ScheduleModel von PrimeFaces implementiert, d. h. es steht für den Kalender,
 * der jeweilige Events in einem Schedule-Objekt halten kann.
 * 
 * @author Rune Krauss
 * @author Marco Glander
 */
@Entity
public class Calendar extends JPAEntity implements ScheduleModel {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 4999062083314099125L;

	/**
	 * Alle Tasks in dem jeweiligen Kalender
	 */
	@OneToMany(mappedBy = "calendar")
	private Set<Task> tasks;

	/**
	 * Dazugehöriger Benutzer für den Kalender
	 */
	@OneToOne
	private User user;

	/**
	 * Erstellt einen neuen Kalender. Initialisiert wird hier nur tasks als
	 * leeres HashSet, um den Zustand eines leeren Kalenders darzustellen.
	 */
	public Calendar() {
		tasks = new HashSet<>();
	}

	/**
	 * Fügt ein Event dem Task-Model hinzu.
	 * 
	 * @param event
	 *            Das ScheduleEvent-Objekt, womit man Zugriff auf die Daten wie
	 *            z. B. dem Titel aus dem PrimeFaces-Objekt hat.
	 */
	@Override
	public void addEvent(ScheduleEvent event) {
		Task task = new Task();
		task.setCalendarEvent((CalendarEvent) event.getData());
		addTask(task);
	}

	/**
	 * Löscht ein Event aus diesem Kalender.
	 * 
	 * @param event
	 *            Das ScheduleEvent-Objekt, womit man Zugriff auf die Daten wie
	 *            z. B. dem Titel aus dem PrimeFaces-Objekt hat.
	 * 
	 * @return Status
	 */
	@Override
	public boolean deleteEvent(ScheduleEvent event) {
		removeTask(((CalendarEvent) event.getData()).getTask());
		return true;
	}

	/**
	 * Holt sich alle Kalender-Events aus einem Task und fügt sie der Liste von
	 * ScheduleEvents hinzu, welche dann zurückgegeben werden.
	 * 
	 * @return Event-Liste
	 */
	@Override
	public List<ScheduleEvent> getEvents() {
		List<ScheduleEvent> events = new ArrayList<ScheduleEvent>();
		for (Task t : tasks) {
			events.add(t.getCalendarEvent());
		}
		return events;
	}

	/**
	 * Holt sich das jeweilige Event aus den Tasks anhand der ID.
	 * 
	 * @param id
	 *            ID des gesuchten Events.
	 * 
	 * @return Event, ansonsten null
	 */
	@Override
	public ScheduleEvent getEvent(String id) {
		for (Task t : tasks) {
			if (t.getCalendarEvent().getId().equals(id))
				return t.getCalendarEvent();
		}
		return null;
	}

	/**
	 * Aktualisiert ein Event. Zwei Events sind gleich, wenn sie dieselbe ID
	 * besitzen.
	 * 
	 * @param event
	 *            Das ScheduleEvent-Objekt, womit man Zugriff auf die Daten wie
	 *            z. B. dem Titel aus dem PrimeFaces-Objekt hat.
	 */
	@Override
	public void updateEvent(ScheduleEvent event) {
		((CalendarEvent) event.getData()).getTask().setCalendarEvent((CalendarEvent) event.getData());
		;
	}

	/**
	 * Gibt die Anzahl der Tasks dieses Kalenders zurück
	 * 
	 * @return Anzahl der Tasks dieses Kalenders
	 */
	@Override
	public int getEventCount() {

		return tasks.size();
	}

	/**
	 * Leert die Liste der Tasks dieses Kalenders.
	 */
	@Override
	public void clear() {
		tasks.clear();

	}

	/**
	 * Gibt wahr zurück, wenn der Kalender sein Limit an Terminen erreicht hat
	 */
	@Override
	public boolean isEventLimit() {
		return false;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Fügt diesem Kalender ein Task hinzu
	 * 
	 * @param t
	 *            der Tasks, der hinzugefügt werden soll
	 */
	public String addTask(Task t) {
		getTasks().add(t);
		return null;
	}

	/**
	 * Löscht ein Task aus diesem Kalender
	 * 
	 * @param t
	 *            der Task, der gelöscht werden soll
	 */
	public String removeTask(Task t) {
		getTasks().remove(t);
		return null;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Calendar && ((Calendar) other).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("Calendar {id: %d, user: %s, email: %s}", getId(),
				user.getProfile().getFirstName() + " " + user.getProfile().getLastName(), user.getEmail());

	}

}
