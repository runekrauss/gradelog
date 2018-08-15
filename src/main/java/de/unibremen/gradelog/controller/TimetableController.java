package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;
import static java.lang.String.format;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.*;
import de.unibremen.gradelog.persistence.TimetableDAO;
import de.unibremen.gradelog.persistence.TimetableEntryDAO;
import de.unibremen.gradelog.util.Assertion;

import de.unibremen.gradelog.util.CSVParser;
import de.unibremen.gradelog.util.LoginGenerator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.SelectEvent;

/**
 * Diese Bean verwaltet den Stundenplan und seine Einträge
 * 
 * @author Mirco Bockholt
 * @author Marco Glander
 */
@Named("timetable")
@ViewScoped
public class TimetableController extends AbstractController {
	/**
	 * Serialisierungs-ID
	 */
	private static final long serialVersionUID = 4722790806367246650L;

	/** Ausgewählter Eintrag */
	private TimetableEntry timetableEntry;
	/** Der Stundenplan */
	private Timetable timetable;
	/** Der zugehörige Benutzer */
	private User user;
	/** DAO für den Stundenplan */
	private final TimetableDAO timetableDao;
	/** DAO für den Stundenplaneintrag */
	private final TimetableEntryDAO timetableEntryDao;

	/** Startdatum */
	private Date startDate;

	/** Startzeit */
	private Date startTime;
	/** Endzeit */
	private Date endTime;

	/**
	 * Erzeugt einen {@link TimetableController} mit definierter
	 * {@link Session}, {@link TimetableDAO} und {@link TimetableEntryDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden
	 * 		{@link TimetableController}s.
	 * @param pTimetableDao
	 * 		Die {@link TimetableDAO} des zu erzeugenden
	 * 		{@link TimetableController}s.
	 * @param pTimetableEntryDao
	 * 		Die {@link TimetableEntryDAO} des zu erzeugenden
	 * 		{@link TimetableController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public TimetableController(final Session pSession,
							   final TimetableDAO pTimetableDao,
							   final TimetableEntryDAO pTimetableEntryDao) {
		super(Assertion.assertNotNull(pSession));
		timetableDao = Assertion.assertNotNull(pTimetableDao);
		timetableEntryDao = Assertion.assertNotNull(pTimetableEntryDao);
	}

	/**
	 * Wird nach dem Konstruktor ausgeführt. Erstellt einen neuen Stundenplan,
	 * sowie ein Objekt des Eintrages, welches den ausgewählten Eintrag
	 * repräsentiert
	 */
	@PostConstruct
	public void init() {
		user = getSession().getUser();
		timetable = user.getTimetable();
		timetableEntry = new TimetableEntry();
		startTime = null;
		endTime = null;
	}

	/**
	 * Hiermit können Entries hinzugefügt werden. Sollte der Stundenplan diesen
	 * Entry bereits enthalten, so wird es dementsprechend aktualisiert.
	 * 
	 * @param actionEvent
	 *            Jeweilige Aktion
	 */
	public void addTimetableEntry(final ActionEvent actionEvent) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to save timetable values!");
			return;
		}
		try {
			assertNotNull(startTime);
			assertNotNull(endTime);
			assertNotNull(timetableEntry);
			assertNotNull(timetable);
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, getLogger(), Level.DEBUG, "errorInvalidTimetableTime");
			return;
		}

		if (startTime.getTime() >= endTime.getTime()) {
			addMessage("errorInvalidTimetableTime");
			return;
		}
		if (!timetable.getEntries().contains(timetableEntry)) {
			timetableEntryDao.create(assertNotNull(timetableEntry));
			timetable.addEntry(timetableEntry);
		}
		
		
		
		timetableEntry.setStartTime(new java.sql.Date(startDate.getTime() + startTime.getTime() + 3600000));
		timetableEntry.setEndTime(new java.sql.Date(startDate.getTime() + endTime.getTime() + 3600000));
		timetableEntryDao.update(timetableEntry);
		timetableDao.update(timetable);
		
		addMessage("successEntryAdded");
		init();
	}

	/**
	 * Hiermit können Entries aus dem Stundenplan gelöscht werden.
	 * 
	 * @param actionEvent
	 *            Jeweilige Aktion
	 */
	public void removeTimetableEntry(final ActionEvent actionEvent) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to save timetable values!");
			return;
		}
		assertNotNull(timetableEntry);
		assertNotNull(timetable);
		timetable.removeEntry(timetableEntry);
		timetableDao.update(timetable);
		timetableEntryDao.delete(timetableEntry);
		addMessage("successEntryDeleted");
		timetableEntry = new TimetableEntry();
	}

	/**
	 * Wird ausgelöst, sobald man einen Termin (belegte Zelle) in dem
	 * Stundenplan selektiert. Dabei wird der Zeiger auf das aktuelle Entry
	 * gelegt.
	 * 
	 * @param selectEvent
	 *            TimetableEntry
	 */
	public void onEventSelect(final SelectEvent selectEvent) {
		timetableEntry = ((TimetableEvent) selectEvent.getObject()).getTimetableEntry();
		startTime = timetableEntry.getStartTime();
		startDate = new Date(startTime.getTime() - (startTime.getTime() % 86400000) - 3600000);
		endTime = timetableEntry.getEndTime();
	}

	/**
	 * Wird ausgelöst, sobald man ein Datum (leere Zelle) selektiert. Dabei
	 * werden Start- und Enddatum zu dem jeweiligen Entry gespeichert.
	 * 
	 * @param selectEvent
	 *            Datum
	 */
	public void onDateSelect(final SelectEvent selectEvent) {
		if (selectEvent == null)
			return;
		timetableEntry = new TimetableEntry();
		startTime = null;
		endTime = null;
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		startDate = (Date) selectEvent.getObject();
		try {
			startDate = formatter.parse(formatter.format(startDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wird ausgelöst, sobald man einen Eintrag im Stundenplan bewegt.
	 * 
	 * @param moveEvent
	 *            timetableEntry
	 */
	public void onEventMove(ScheduleEntryMoveEvent moveEvent) {
		timetableEntry = ((TimetableEvent) moveEvent.getScheduleEvent()).getTimetableEntry();
		if (moveEvent == null)
			return;
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to edit a timetableEntry!");
			return;
		}
		if (timetableEntry.getStartTime().getTime() > timetableEntry.getEndTime().getTime()) {
			addMessage("errorInvalidTime");
			return;
		}
		
		timetableEntryDao.update(assertNotNull(timetableEntry));
		timetableDao.update(assertNotNull(timetable));
	
		addMessage("successTodoMoved");
	}

	/**
	 * Importiert den jeweiligen Stundenplan anhand eines DIF-Files.
	 *
	 * @param event
	 *            Das hochzuladene File
	 */
	public void uploadDIF(FileUploadEvent event) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to import a timetable!");
			return;
		}
		try {
			ArrayList<TimetableEntry> list = new ArrayList<>();
			CSVParser csvParser = new CSVParser(event.getFile().getInputstream(), 8);
			while (csvParser.nextLine()) {
				TimetableEntry newEnt = new TimetableEntry();
				newEnt.setTitle(csvParser.getString(3));
				newEnt.setPlace(csvParser.getString(4));
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date(518400000));
				cal.add(Calendar.DAY_OF_YEAR, csvParser.getInt(5)-3);
				cal.add(Calendar.HOUR, csvParser.getInt(6) / 60 - 1);
				cal.add(Calendar.MINUTE, csvParser.getInt(6) % 60);
				
				java.sql.Date date = new java.sql.Date(cal.getTime().getTime());
				newEnt.setStartTime(date);
				
				cal = Calendar.getInstance();
				cal.setTime(new Date(date.getTime()));
				cal.add(Calendar.HOUR, csvParser.getInt(7) / 60);
				cal.add(Calendar.MINUTE, csvParser.getInt(7) % 60);
				
				newEnt.setEndTime(new java.sql.Date(cal.getTime().getTime()));
				
				list.add(newEnt);
			}
			
			for (TimetableEntry r : list) {
				timetableEntryDao.create(r);
				timetable.addEntry(r);
			}
			
			timetableDao.update(timetable);
			
			addMessage("successImportTimetable");
			init();
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorInvalidFormat");
		} catch (IOException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorInvalidBinaryStream");
		}
	}
	
	public Timetable getTimetable() {
		return timetable;
	}

	public void setTimetable(final Timetable timetable) {
		this.timetable = timetable;
	}

	public TimetableEntry getTimetableEntry() {
		return timetableEntry;
	}

	public void setTimetableEntry(final TimetableEntry timetableEntry) {
		this.timetableEntry = timetableEntry;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(final Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(final Date endTime) {
		this.endTime = endTime;
	}
	
	public boolean renderDeleteButton()
	{
		return timetableEntry != null && timetableEntry.getId() > 0;
	}
	
	public void onShowWeekendsClick()
	{
		timetableDao.update(timetable);
	}
}
