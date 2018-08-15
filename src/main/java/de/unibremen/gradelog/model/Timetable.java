package de.unibremen.gradelog.model;

import java.util.*;

import javax.persistence.*;

import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.UploadedFile;

/**
 * Stellt den Stundenplan eines Nutzers dar. In diesem stehen der Besitzer und
 * alle Stundenplaneinträge.
 * 
 * @author Marco Glander
 * @author Mirco Bockholt
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Timetable.findAll", query = "SELECT u FROM User u"), })
public class Timetable extends JPAEntity implements ScheduleModel {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 8873492781754578342L;

	/** Der zugehörige Benutzer */
	@OneToOne
	private User user;

	/** Stundenplaneinträge */
	@OneToMany
	private Set<TimetableEntry> entries;
	
	/** Wochenenden anzeigen? **/
	private boolean showWeekends;

	/**
	 * Startdatum für den Scheduler (wird benötigt, um eine statische Woche zu
	 * simulieren)
	 */
	@Transient
	private Date initialDate = new Date(518400000);

	/**
	 * File der hochgeladenen Stundenpläne aus UNTIS
	 */
	@Transient
	private UploadedFile dif;

	/**
	 * Dieses Model repräsentiert einen Stundenplan
	 */
	public Timetable() {
		user = null;
		showWeekends=false;
		entries = new HashSet<>();
	}

	public Date getInitialDate() {
		return this.initialDate;
	}

	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	public User getUser() {
		return user;
	}

	public void setUserId(User user) {
		this.user = user;
	}

	public UploadedFile getDIF() {
		return dif;
	}

	public void setDIF(final UploadedFile dif) {
		this.dif = dif;
	}

	public Set<TimetableEntry> getEntries() {
		return entries;
	}

	public void setEntries(Set<TimetableEntry> entries) {
		this.entries = entries;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void addEntry(TimetableEntry te) {
		getEntries().add(te);
	}

	public void removeEntry(TimetableEntry te) {
		getEntries().remove(te);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Timetable && ((Timetable) other).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("Timetable {id: %d, user: %s}", getId(),
				/* user.getFirstName() + " " + user.getLastName() */ "Pommes");
	}

	/**
	 * Fügt ein Event dem TimetableEntry-Model hinzu.
	 * 
	 * @param event
	 *            Das ScheduleEvent-Objekt, womit man Zugriff auf die Daten wie
	 *            z. B. dem Titel aus dem PrimeFaces-Objekt hat.
	 */
	@Override
	public void addEvent(ScheduleEvent event) {
		TimetableEntry timetableEntry = new TimetableEntry();
		timetableEntry.setTimetableEvent((TimetableEvent) event.getData());
		addEntry(timetableEntry);
	}

	/**
	 * Löscht ein Event aus diesem Stundenplan.
	 * 
	 * @param event
	 *            Das ScheduleEvent-Objekt, womit man Zugriff auf die Daten wie
	 *            z. B. dem Titel aus dem PrimeFaces-Objekt hat.
	 * 
	 * @return Status
	 */
	@Override
	public boolean deleteEvent(ScheduleEvent event) {
		removeEntry(((TimetableEvent) event.getData()).getTimetableEntry());
		return true;
	}

	/**
	 * Holt sich alle Stundenplan-Events aus einem Entry und fügt sie der Liste
	 * von ScheduleEvents hinzu, welche dann zurückgegeben werden.
	 * 
	 * @return Event-Liste
	 */
	@Override
	public List<ScheduleEvent> getEvents() {
		List<ScheduleEvent> events = new ArrayList<ScheduleEvent>();
		for (TimetableEntry t : entries) {
			events.add(t.getTimetableEvent());
		}
		return events;
	}

	/**
	 * Holt sich das jeweilige Event aus den Entries anhand der ID.
	 * 
	 * @param id
	 *            ID des gesuchten Events.
	 * 
	 * @return Event, ansonsten null
	 */
	@Override
	public ScheduleEvent getEvent(String id) {
		for (TimetableEntry t : entries) {
			if (t.getTimetableEvent().getId().equals(id))
				return t.getTimetableEvent();
		}
		return null;
	}

	public boolean isShowWeekends() {
		return showWeekends;
	}

	public void setShowWeekends(boolean showWeekends) {
		this.showWeekends = showWeekends;
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
		((TimetableEvent) event.getData()).getTimetableEntry().setTimetableEvent((TimetableEvent) event.getData());
		;
	}

	@Override
	public int getEventCount() {

		return entries.size();
	}

	@Override
	public void clear() {
		entries.clear();

	}

	@Override
	public boolean isEventLimit() {
		return false;
	}
}
