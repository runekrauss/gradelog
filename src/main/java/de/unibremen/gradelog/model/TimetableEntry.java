package de.unibremen.gradelog.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Stellt den Eintrag in einem Stundenplan dar. Gespeichert werden Titel, Ort,
 * Beschreibung, Start-und Endzeit. Außerdem wird ein Rückverweis auf den
 * jeweiligen Stundenplan mit persistiert.
 * 
 * @author Marco Glander
 * @author Mirco Bockholt
 */
@Entity
public class TimetableEntry extends JPAEntity {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -2180296302733488042L;

	/** Titel */
	@Column(length = 64, nullable = false)
	private String title;

	/** Beschreibung */
	@Column(length = 1024)
	private String description;

	/** Ort */
	@Column(length = 64)
	private String place;

	/** Startzeit */
	private Date startTime;

	/** Endzeit */
	private Date endTime;

	/** Der zugehörige Stundenplan */
	@OneToOne
	private Timetable timetable;

	/**
	 * Dieses Model repräsentiert einen Stundenplaneintrag
	 */
	public TimetableEntry() {
		description = "";
		place = "";
		timetable = null;
		startTime = new Date(0);
		endTime = new Date(0);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Timetable getTimetable() {
		return timetable;
	}

	public void setTimetable(Timetable timetable) {
		this.timetable = timetable;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return new Date(startTime.getTime());
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return new Date(endTime.getTime());
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * Setzt die jeweiligen Werte wie Typ, Titel usw. in einem Stundenplan-Event
	 * (TimetableEntry).
	 * 
	 * @return Stundenplan-Event
	 */
	public TimetableEvent getTimetableEvent() {
		TimetableEvent ce = new TimetableEvent();
		ce.setId("" + getId());
		ce.setDescription(description);
		ce.setStartDate(startTime);
		ce.setEndDate(endTime);
		ce.setTitle(title);
		ce.setAllDay(false);
		ce.setPlace(place);
		ce.setTimetableEntry(this);
		return ce;
	}

	/**
	 * Holt sich die Werte aus dem Stundenplan-Event und speichert sie in diesem
	 * Model ab, damit der Controller darauf einen kompatiblen Zugriff hat.
	 * 
	 * @param ce
	 *            Stundenplan-Event
	 */
	public void setTimetableEvent(TimetableEvent ce) {
		description = ce.getDescription();
		setStartTime(new Date(ce.getStartDate().getTime()));
		setEndTime(new Date(ce.getEndDate().getTime()));
		title = ce.getTitle();
		place = ce.getPlace();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof TimetableEntry && ((TimetableEntry) other).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("TimetableEntry {id: %d, title: %s, description: %s, place: %s}", getId(), title,
				description, place);
	}
}
