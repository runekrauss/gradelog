package de.unibremen.gradelog.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Hält eine Nachricht. Enthält die Gruppe und den eingeladenen Nutzer.
 * 
 * @author Marco Glander
 * @author Christos Dhimitris
 */
@Entity
public class Message extends JPAEntity implements Comparable<Message> {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 5427444384187613760L;

	/**
	 * Thema der Nachricht
	 */
	@Column(length = 64, nullable = false)
	private String subject;

	/**
	 * Inhalt der Nachricht
	 */
	@Column(length = 1024, nullable = false)
	private String content;

	/**
	 * Datum der Nachricht
	 */
	@Column(nullable = false)
	private Timestamp date;

	/**
	 * Autor einer Nachricht
	 */
	private String author;

	/**
	 * Alle Empfänger einer Nachricht
	 */
	private Set<String> receivers;

	/**
	 * Alle Nutzer, die diese Nachricht gelesen haben
	 */
	private Map<String, Boolean> confirmMap;

	public Message() {
		subject = "";
		content = "";
		date = new Timestamp(new java.util.Date().getTime());
		confirmMap = new HashMap<>();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getDateString() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return df.format(new Date(date.getTime()));
	}

	public boolean getReadStatus(String s) {
		return confirmMap.containsKey(s) ? confirmMap.get(s) : false;
	}

	public void confirmRead(String s) {
		confirmMap.put(s, true);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Set<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(Set<String> receivers) {
		this.receivers = receivers;
	}

	public int getAnswerWindow() {
		return getId() * -1;
	}

	public String getViewWindow() {
		return "view" + getId();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Message && ((Message) other).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("Message {id: %d,  subject: %s, content: %s}", getId(), getSubject(), getContent());
	}

	@Override
	public int compareTo(Message mes) {
		return mes.getId() - getId();
	}
}
