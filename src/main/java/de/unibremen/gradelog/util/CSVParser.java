package de.unibremen.gradelog.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Stellt Funktionen bereit, um exportierte CSV-Dateien aus UNTIS in dieses System
 * zu importieren. Diese Klasse betrifft den Vertretungsplan sowie Nutzer-Importe.
 * 
 * @author Marco Glander
 */
public class CSVParser {
	/**
	 * Zellen
	 */
	private String[] cells;
	/**
	 * Liest die Dateien ein.
	 */
	private final BufferedReader reader;
	/**
	 * Zellenlänge
	 */
	private final int cellLength;

	/**
	 * Aktuelle Zeile
	 */
	private int line;

	/**
	 * Initialisiert die Zellenlänge und den Leser für den Datenstrom.
	 * 
	 * @param input
	 *            Eingabestrom
	 * @param cellLength
	 *            Anzahl der erwarteten Zellen.
	 */
	public CSVParser(InputStream input, int cellLength) {
		this.cellLength = cellLength;
		reader = new BufferedReader(new InputStreamReader(input));
		line = 1;
	}

	/**
	 * Ermittelt, ob es in der jeweiligen Datei noch eine Zeile zum lesen
	 * existiert. Anhand der Zellenlänge wird hierbei die Validierung
	 * vorgenommen.
	 * 
	 * @return Status gibt aus, ob noch eine weitere Zeile vorhanden ist (true=ja, false=nein)
	 */
	public boolean nextLine() {
		String currentLine = null;
		try {
			currentLine = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (currentLine != null) {
			currentLine = currentLine.replace("\"", "");
			currentLine = currentLine.replace(" ", "");
			cells = currentLine.split(",");
			if (cells.length < cellLength)
				throw new IllegalArgumentException("CSV File line does not have the specified cell length! (Cells: "
						+ cells.length + " specified cell length: " + cellLength);
			line++;
			return true;
		}
		return false;
	}

	/**
	 * Liefert den Inhalt der angegebenen Zelle als int zurück.
	 * Ist in dieser Zelle kein int vorhanden, wird eine
	 * IllegalArgumentException geworfen.
	 * @param cellPosition die angegebene Zellenposition
	 * @return Inhalt der Zelle als int
	 */
	public int getInt(int cellPosition) {
		try {
			return Integer.parseInt(cells[cellPosition]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Date on this cell could not be parsed! (Line: " + line + " Cell: " + cellPosition);
		}
	}

	/**
	 * Liefert den Inhalt der angegebenen Zelle als String
	 * Es wird keine Überprüfung des Inhaltes vorgenommen,
	 * da der Inhalt der Zelle für den Datentypen irrelevant ist.
	 * @param cellPosition die angegebene Zellenposition
	 * @return Inhalt der Zelle als String
	 */
	public String getString(int cellPosition) {
		return cells[cellPosition];
	}

	/**
	 * Liefert den Inhalt der angegebenen Zelle als Date im
	 * Format yyyMMdd. Enthält die Zelle kein Datum
	 * (oder ist das Format nicht dasselbe), wird eine IllegalArgumentException
	 * geworfen.
	 * @param cellPosition die angegebene Zellenposition
	 * @return Inhalt der Zelle als Date mit dem Format yyyyMMdd
	 */
	public Date getDate(int cellPosition) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		try {
			Date result = new Date(0);
			result = formatter.parse(getString(cellPosition));
			return result;
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"Date on this cell could not be parsed! (Line: " + line + " Cell: " + cellPosition);
		}
	}
}