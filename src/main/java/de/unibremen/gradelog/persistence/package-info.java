/**
 * Diese Klassen ermöglichen eine nachhaltige Speicherung der jeweiligen Daten. Sie erben
 * dabei von der Klasse JPADAO. Diese verwendet wiederum den Standard JPA, um einen
 * über die Laufzeit der Applikation hinaus gültigen Datenbestand zu realisieren und implementiert
 * auch den EntityManager, der von der EntityManagerFactory erstellt wird.
 * Die Konfiguration z. B. von Treibern davon erfolgt von der persistence.xml. Zudem implementiert
 * die JPADAO die Schnittstelle GenericDAO, welche einen Zwang realisiert,
 * die Methoden save und remove zu implementieren. Die jeweiligen DAOs nehmen jeweils
 * die modifizierten Model-Objekte entgegen und sorgen dann dementsprechend für
 * die Persistenz. Wir verwenden hier explizit das DAO-Pattern, wodurch die eigentliche
 * Programmlogik von technischen Details der Datenspeicherung befreit wird und somit flexibler
 * einsetzbar ist. Wir benötigen dieses Paket, da die zu verwendenden Technologien
 * vorgegeben werden, wie es auch die Issue Card 10 beschreibt.
 */
package de.unibremen.gradelog.persistence;
