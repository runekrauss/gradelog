package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.util.Date;

import org.primefaces.model.ScheduleEvent;

/**
 * Steht für ein Stundenplan-Event, d. h. einem Event aus dem Schedule-Objekt
 * von PrimeFaces, welches diesen implementiert.
 * 
 * @author Rune Krauss
 * @author Marco Glander
 * @author Mirco Bockholt
 */
public class TimetableEvent implements ScheduleEvent, Serializable {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 2692029215212604299L;

	/**
	 * ID des Stundenplan-Events
	 */
	private String id;

	/**
	 * Titel des Stundenplan-Events
	 */
	private String title;

	/**
	 * Startdatum inklusive der Zeit
	 */
	private Date startDate;

	/**
	 * Enddatum inklusive der Zeit
	 */
	private Date endDate;

	/**
	 * Ist das Event den ganzen Tag?
	 */
	boolean allDay;

	/**
	 * Status der Editierbarkeit
	 */
	boolean editable = true;

	/**
	 * Jeweilige Style-Klasse
	 */
	private String styleClass;

	/**
	 * Beschreibungstext
	 */
	private String description;

	/**
	 * Ort des Events
	 */
	private String place;

	/**
	 * Dazugehöriger TimetableEntry
	 */
	private TimetableEntry timetableEntry;

	public TimetableEntry getTimetableEntry() {
		return timetableEntry;
	}

	public void setTimetableEntry(TimetableEntry timetableEntry) {
		this.timetableEntry = timetableEntry;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;

	}

	@Override
	public Object getData() {
		return this;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	@Override
	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
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

	public String getUrl() {
		return null;
	}

}
