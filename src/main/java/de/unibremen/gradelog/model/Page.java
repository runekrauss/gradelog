package de.unibremen.gradelog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Beinhaltet eine Seite wie 'FAQ' mit sprachabhängigen Inhalten.
 * 
 * @author Rune Krauss
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Page.findAll", query = "SELECT p FROM Page p"),
		@NamedQuery(name = "Page.findByName", query = "SELECT p FROM Page p WHERE p.englishName = ?1"), })
public class Page extends JPAEntity {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 5319096729830236396L;

	/**
	 * Deutscher Titel
	 */
	@Column(length = 16, nullable = false, unique = true)
	private String germanName;

	/**
	 * Englischer Titel
	 */
	@Column(length = 16, nullable = false, unique = true)
	private String englishName;

	/**
	 * Deutscher Inhalt
	 */
	@Column(length = 16392)
	private String germanContent;

	/**
	 * Englischer Inhalt
	 */
	@Column(length = 16392)
	private String englishContent;

	/**
	 * Status der Seite
	 */
	private boolean activated;

	/**
	 * Initialisiert ein Objekt dieser Klasse mit einem leeren Inhalt.
	 */
	public Page() {
		germanContent = "";
		englishContent = "";
	}

	public String getGermanName() {
		return germanName;
	}

	public void setGermanName(final String germanName) {
		this.germanName = germanName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(final String englishName) {
		this.englishName = englishName;
	}

	public String getGermanContent() {
		return germanContent;
	}

	public void setGermanContent(final String germanContent) {
		this.germanContent = germanContent;
	}

	public String getEnglishContent() {
		return englishContent;
	}

	public void setEnglishContent(final String englishContent) {
		this.englishContent = englishContent;
	}

	public void setActivated(final boolean activated) {
		this.activated = activated;
	}

	public boolean getActivated() {
		return activated;
	}
}
