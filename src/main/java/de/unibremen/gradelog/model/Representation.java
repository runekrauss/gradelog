package de.unibremen.gradelog.model;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Diese Klasse stellt den Eintrag in dem Vertretungsplan dar. Gespeichert
 * werden Datum, Stunde, Lehrer, vertretender Lehrer, Fach, Raum,
 * Vertretungsraum und die betroffenen Klassen.
 * 
 * @author Marco Glander
 *
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Representation.findAll", query = "SELECT r FROM Representation r"),
		@NamedQuery(name = "Representation.clear", query = "DELETE FROM Representation r") })
public class Representation extends JPAEntity implements Comparable<Representation> {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 2438846358729130215L;

	/**
	 * Das Datum, an dem die Vertretung in Kraft tritt
	 */
	private Date date;

	/**
	 * Die Stunde, die betroffen ist von dieser Vertretung
	 */
	private int lesson;

	/**
	 * Der ursprüngliche Lehrer der betroffenen Stunde
	 */
	private String teacher;

	/**
	 * Der (optionale) Vertretungslehrer. Ist dieser leer, übernimmt der normale
	 * Lehrer diese Stunde
	 */
	private String repreTeacher;

	/**
	 * Das Fach dieser Stunde
	 */
	private String subject;

	/**
	 * Der ursprüngliche Raum dieser Stunde.
	 */
	private String room;

	/**
	 * Der (optionale) Vertretungsraum. Ist dieser leer, findet der Unterricht
	 * im ursprügnlichen Raum statt.
	 */
	private String repreRoom;

	/**
	 * Die betroffenen Klassen.
	 */
	private String classes;

	/**
	 * Erstellt einen Eintrag für den Vertretungsplan. Alle Attribute werden
	 * initialisiert.
	 */
	public Representation() {
		date = new Date(0);
		lesson = 1;
		teacher = "";
		repreTeacher = "";
		subject = "";
		room = "";
		repreRoom = "";
		classes = "";
	}

	public java.util.Date getDate() {
		return new java.util.Date(date.getTime());
	}

	public void setDate(java.util.Date date) {
		this.date = new Date(date.getTime());
	}

	public int getHour() {
		return lesson;
	}

	public void setHour(int hour) {
		this.lesson = hour;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getRepreTeacher() {
		return repreTeacher;
	}

	public void setRepreTeacher(String repreTeacher) {
		this.repreTeacher = repreTeacher;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getRepreRoom() {
		return repreRoom;
	}

	public void setRepreRoom(String repreRoom) {
		this.repreRoom = repreRoom;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	/**
	 * Formartiert das Datum der Vertretung zu einem passenden String.
	 * 
	 * @return Der formatierte Datum-String
	 */
	public String getSimpleDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String str = dateFormat.format(date.getTime());
		return str;
	}

	@Override
	public String toString() {
		return String.format("Representation {id: %d}", getId());
	}

	/**
	 * Dient zur Sortierung des Vertretungsplans. Eine Vertretung ist vor dem zu
	 * vergleichenden Eintrag zu setzen, wenn das Datum vor dem des zu
	 * vergleichenden Eintrages ist. Sind diese gleich, wird der Eintrag nach
	 * vorne sortiert, der an dem betroffenen Tag vorher passiert.
	 */
	@Override
	public int compareTo(Representation other) {
		int compared = this.getDate().compareTo(other.getDate());
		if (compared == 0) {
			compared = this.getHour() - other.getHour();
		}
		return compared;
	}
}
