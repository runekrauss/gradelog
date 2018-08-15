package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Stellt funktionen für CustomPage Interaktionen bereit für Klassen denen
 * Custompages freigebbar sein sollen
 * 
 * @author Christopher Wojtkow
 *
 */
public interface Shareable extends Serializable {

	/**
	 * Fügt eine freigegebene Seite hinzu
	 * 
	 * @param cp
	 *            Die dem Shareable freizugebene Seite
	 * @return Ob der Vorgang erfolgreich war
	 */
	boolean addSharedPage(final CustomPage cp);

	/**
	 * Entfernt eine freigegebene Seite
	 * 
	 * @param cp
	 *            Die dem Shareable zu entfernende Seite
	 * @return Ob der Vorgang erfolgreich war
	 */
	boolean removeSharedPage(final CustomPage cp);

	/**
	 * Gibt eine Identifikator für die Shareable Instanz zurück
	 * 
	 * @return Der Identifikator
	 */
	String getType();

	/**
	 * Gibt den formartierten Namen des Shareable's zurück
	 * @return			Der formatierte Name
	 */
	public String getPureName();//nur name nichts anderes
	
	/**
	 * Gibt true zurück wenn die implementierende Klasse eine Art von
	 * Profilseite bereitstellen kann.
	 * 
	 * @return true wenn ein Profil darstellbar ist
	 */
	boolean hasProfile();

	Set<CustomPage> getSharedPages();
}
