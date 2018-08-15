/**
 * 
 * Die Klassen in diesem Paket kümmern sich um die Logik in dieser Applikation und erben
 * dabei von der Klasse AbstractController. Somit wird es den abgeleiteten Klassen
 * ermöglicht - im Hinblick auf die Validierung - auf Methoden zuzugreifen, die sich um die
 * Verwaltung von Fehlernachrichten kümmern. Zudem wird durch die Vererbung ermöglicht,
 * in jedem Modul zu prüfen, ob ein Benutzer eingeloggt ist oder nicht. Somit kann
 * man also auch Zugriffsberechtigungen steuern. Des Weiteren wird ein globaler Logout
 * potenziell ermöglicht.
 */
package de.unibremen.gradelog.controller;
