package de.unibremen.gradelog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Speichert Inhalte wie Farben und Hintergründe zum Template ab. Diese beziehen
 * sich auf das Front- und Backend. Wenn die Inhalte null sind, so gelten die
 * Werkseinstellungen.
 * 
 * @author Rune Krauss
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Template.findByName", query = "SELECT t FROM Template t WHERE t.name = ?1"),
		@NamedQuery(name = "Template.findAll", query = "SELECT t FROM Template t") })
public class Template extends JPAEntity {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 5555916775111626071L;

	/**
	 * Name des Templates
	 */
	@Column(length = 8, nullable = false)
	private String name;

	/**
	 * Titel
	 */
	@Column(length = 16)
	private String title;

	/**
	 * Farbe
	 */
	@Column(length = 6)
	private String color;

	/**
	 * Hintergrundfarbe
	 */
	@Column(length = 6)
	private String background;

	/**
	 * Farbe der Navigation
	 */
	@Column(length = 6)
	private String navbar;

	/**
	 * Farbe des Seitenmenüs
	 */
	@Column(length = 6)
	private String sidebar;

	/**
	 * Inhalt der Wartungsseite
	 */
	@Column(length = 128)
	private String maintenanceContent;

	/**
	 * Wartungsmodus
	 */
	private boolean maintenance;

	/**
	 * Mehrsprachigkeit
	 */
	private boolean language = true;

	/**
	 * Anzeige der angemeldeten Benutzer
	 */
	private boolean userStatus = false;

	/**
	 * Login-Anzeige
	 */
	private boolean login = true;

	/**
	 * Intro des Templates
	 */
	@Column(length = 16)
	private String intro;

	/**
	 * Slogan des Templates
	 */
	@Column(length = 32)
	private String slogan;

	/**
	 * Copyright des Templates
	 */
	@Column(length = 32)
	private String copyright;

	public Template() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getNavbar() {
		return navbar;
	}

	public void setNavbar(String navbar) {
		this.navbar = navbar;
	}

	public String getSidebar() {
		return sidebar;
	}

	public void setSidebar(String sidebar) {
		this.sidebar = sidebar;
	}

	public boolean isMaintenance() {
		return maintenance;
	}

	public void setMaintenance(boolean maintenance) {
		this.maintenance = maintenance;
	}

	public boolean isLanguage() {
		return language;
	}

	public void setLanguage(boolean language) {
		this.language = language;
	}

	public boolean isUserStatus() {
		return userStatus;
	}

	public void setUserStatus(boolean userStatus) {
		this.userStatus = userStatus;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public String getMaintenanceContent() {
		return maintenanceContent;
	}

	public void setMaintenanceContent(String maintenanceContent) {
		this.maintenanceContent = maintenanceContent;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	@Override
	public String toString() {
		return String.format("Template {id: %d, title: %s}", getId(), getTitle());
	}
}
