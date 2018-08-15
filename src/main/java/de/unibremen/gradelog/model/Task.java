package de.unibremen.gradelog.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Dieses Model verwaltet die Daten bezüglich der Tasks in dem Kalender eines
 * jeweiligen Benutzers, d. h. die Beschreibung, den Titel usw.
 * 
 * @author Rune Krauss
 * @author Marco Glander
 */
@Entity
public class Task extends JPAEntity implements Comparable<Task> {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 6556041531956421143L;

	/**
	 * Der dazugehörige Benutzer
	 */
	@ManyToOne
	private User user;

	/**
	 * Der dazugehörige Kalender
	 */
	@ManyToOne
	private Calendar calendar;

	/**
	 * Bezeichnung des Tasks
	 */
	@Column(length = 64, nullable = false)
	private String title;

	/**
	 * Beschreibung des Tasks
	 */
	@Column(length = 2048)
	private String description;

	/**
	 * Startdatum mit Uhrzeit
	 */
	@Column(nullable = false)
	private Date startTime;

	/**
	 * Enddatum mit Uhrzeit
	 */
	@Column(nullable = false)
	private Date endTime;

	/**
	 * Ort des Tasks
	 */
	@Column(length = 64)
	private String place;

	/**
	 * Gilt der Task für den ganzen Tag?
	 */
	private boolean allDay;

	public enum TaskType {
		EXAM, EXCERCISE, SPORT, OTHER, NONE
	};

	/**
	 * Typ des Tasks, wie z. B. Sport
	 */
	private TaskType type;

	public Task() {
		title = "";
		description = "";
		startTime = new Date(new java.util.Date().getTime());
		endTime = new Date(new java.util.Date().getTime());
		place = "";
		user = null;
		calendar = null;
		type = TaskType.NONE;
	}

	/**
	 * Setzt die jeweiligen Werte wie Typ, Titel usw. in einem Kalender-Event
	 * (Task).
	 * 
	 * @return Kalender-Event
	 */
	public CalendarEvent getCalendarEvent() {
		CalendarEvent ce = new CalendarEvent();
		ce.setId("" + getId());
		ce.setDescription(description);
		ce.setStartDate(startTime);
		ce.setEndDate(endTime);
		ce.setTitle(title);
		ce.setAllDay(allDay);
		ce.setType(type);
		ce.setTask(this);
		return ce;
	}

	/**
	 * Holt sich die Werte aus dem Kalender-Event und speichert sie in diesem
	 * Model ab, damit der Controller darauf einen kompatiblen Zugriff hat.
	 * 
	 * @param ce
	 *            Kalender-Event
	 */
	public void setCalendarEvent(CalendarEvent ce) {
		description = ce.getDescription();
		setStartTime(ce.getStartDate());
		setEndTime(ce.getEndDate());
		title = ce.getTitle();
		allDay = ce.isAllDay();
		type = ce.getType();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public java.util.Date getStartTime() {
		return new java.util.Date(startTime.getTime());
	}

	public void setStartTime(java.util.Date startTime) {
		this.startTime = new Date(startTime.getTime());
	}

	public java.util.Date getEndTime() {
		return new java.util.Date(endTime.getTime());
	}

	public void setEndTime(java.util.Date endTime) {
		this.endTime = new Date(endTime.getTime());
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public TaskType getType() {
		return type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public String getTaskType() {
		if (getType() == TaskType.EXAM)
			return "exam";
		else if (getType() == TaskType.EXCERCISE)
			return "excercise";
		else if (getType() == TaskType.SPORT)
			return "sport";
		else if (getType() == TaskType.OTHER)
			return "other";
		else
			return "none";
	}

	public void setTaskType(final String type) {
		if (type.equals("exam"))
			this.type = TaskType.EXAM;
		else if (type.equals("excercise"))
			this.type = TaskType.EXCERCISE;
		else if (type.equals("sport"))
			this.type = TaskType.SPORT;
		else if (type.equals("other"))
			this.type = TaskType.OTHER;
		else
			this.type = TaskType.NONE;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Task && ((Task) other).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("Task {id: %d, title: %s, description: %s, startTime: %s, endTime: %s}", getId(), title,
				description, startTime, endTime);
	}

	public boolean getAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	@Override
	public int compareTo(Task t) {
		return getStartTime().compareTo(t.getStartTime());
	}
}
