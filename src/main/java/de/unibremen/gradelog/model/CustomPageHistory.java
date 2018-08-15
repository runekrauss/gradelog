package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Klasse, die den alten Zustand einer CustomPage wiederherstellen kann. Alle
 * alten Daten wie Text etc. werden hier zwischengespeichert.
 * 
 * @author Marco Glander
 * @author Christopher Wojtkow
 */
@Entity
public class CustomPageHistory extends JPAEntity {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -8683579981657627518L;

	@Column(length = 8192)
	private String content;

	@Column(length = 64)
	private String title;

	@Column(nullable = false)
	private Date date;

	@ManyToOne
	private CustomPage customPage;

	public CustomPageHistory() {
		title = "";
		content = "";
		date = new Date(0);
		customPage = null;
	}

	public CustomPageHistory(final String title, final String content, final java.util.Date date, final CustomPage cp) {
		this.title = title;
		this.content = content;
		this.date = new java.sql.Date(date.getTime());
		this.customPage = cp;
	}

	public String getSimpleDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		String str = dateFormat.format(date.getTime());
		return str;
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof CustomPageHistory && ((CustomPageHistory) other).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("CustomPageHistory {id: %d, content: %s, title: %s, date: %s}",
				getId(), content, title, date);
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public CustomPage getCustomPage() {
		return customPage;
	}

	public void setCustomPage(final CustomPage customPage) {
		this.customPage = customPage;
	}

	public java.util.Date getDate() {
		return new java.util.Date(date.getTime());
	}

	public void setDate(final java.util.Date date) {
		this.date = new java.sql.Date(date.getTime());
	}
}