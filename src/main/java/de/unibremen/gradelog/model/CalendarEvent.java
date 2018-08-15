package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.util.Date;

import org.primefaces.model.ScheduleEvent;

import de.unibremen.gradelog.model.Task.TaskType;

/**
 * Steht für ein Kalender-Event, d. h. einem Event aus dem Schedule-Objekt von
 * PrimeFaces, welches diesen implementiert.
 * 
 * @author Rune Krauss
 */
public class CalendarEvent implements ScheduleEvent, Serializable {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 4779929390175444865L;

	/**
	 * ID des Kalender-Events
	 */
	private String id;

	/**
	 * Titel des Kalender-Events
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
	 * Typ des Events wie z. B. Sport
	 */
	private Task.TaskType type;

	/**
	 * Dazugehöriges Task
	 */
	private Task task;

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
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

	public TaskType getType() {
		return type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public String getUrl() {
		return null;
	}

}
