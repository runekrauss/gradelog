/**
 * Die Klassen dieses Pakets repräsentieren die Datensicht (siehe Kapitel 6 auf Seite 62),
 * d. h. sie halten die jeweiligen Attribute und Objekte dieser Klassen können modifiziert,
 * erstellt oder gelöscht werden. Dabei werden die Models in Bezug auf deren Konstrukte
 * wie z. B. Attribute mit sog. Annotationen versehen, d. h. es wird unter anderem
 * verdeutlicht, welche Attribute zur eigentlichen Entität gehören bzw. Beziehungen zu
 * anderen Entitäten verdeutlicht. Letztendlich wird dann dieses modifizierte Objekt
 * an die DAO übergeben, welche sich dann explizit um die Persistenz kümmert (siehe
 * Kapitel 5.1.3 auf Seite 46).
 * Anhand dieses Pakets lässt sich bereits ablesen, dass wir das Entwurfsmuster Fassade
 * verwenden. Hierbei dient die Klasse User als Fassade, welche mittels Sets und Listen
 * Zugang zu den Subsystemen bietet. Auf die Subsysteme wird hierbei selten bis gar nicht
 * von außen zugegriffen. Wir haben uns für dieses Entwurfsmuster entschieden, da wir so
 * eine einfache Modularisierung unserer Software gewährleisten können (siehe Strategie
 * 1 der Issue Card 8). Zudem verwenden wir diese Strategie, da die einzelnen Module
 * sich alle nach den Nutzern richten müssen, um eine möglichst große Individualisierung
 * zu gewährleisten. Als Beispiel sei hier der Stundenplan erwähnt, welcher an jeden User
 * angepasst werden muss. Durch diese Modularisierung ist es uns auch möglich, einzelne
 * Module zu verwerfen oder neue Module hinzuzufügen. Somit können wir die Strategie
 * 1 unserer Issue Cards 9 und 11 erfüllen.
 */
package de.unibremen.gradelog.model;

