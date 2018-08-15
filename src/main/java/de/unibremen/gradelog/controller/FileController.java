package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.File;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.FileDAO;
import de.unibremen.gradelog.persistence.UserDAO;

/**
 * Diese Klasse steuert die Logik zum Fileupload.
 * 
 * @author Marco Glander
 */
@Named("fileBean")
@ViewScoped
public class FileController extends AbstractController {
	/**
	 * Eindeutige ID für die Serialisierung
	 */
	private static final long serialVersionUID = 7399431776339532745L;

	/**
	 * Dateipfad für hochzuladene Dateien
	 */
	private String filePath;

	/**
	 * Datei, die beschrieben wird, sollte der Nutzer eine neue Datei hochladen
	 */
	private File file;

	/**
	 * Datei, die der Nutzer aus der Tabelle auswählen kann
	 */
	private File selectedFile;
	/**
	 * DAO, die für die Persistenz von Dateien verantwortlich ist.
	 */
	private final FileDAO fileDao;
	/**
	 * DAO, die für die Persistenz von Nutzern verantwortlich ist.
	 */
	private final UserDAO userDao;

	/**
	 * Nutzer, der diese Seite derzeit benutzt.
	 */
	private User user;
	/**
	 * Alle Dateien, die der aktuelle Nutzer dieser Seite hochgeladen hat.
	 */
	private List<File> files;

	/**
	 * Erzeugt einen {@link FileController} mit definierter {@link Session},
	 * {@link FileDAO} und {@link UserDAO}.
	 *
	 * @param pSession
	 *            Die {@link Session} des zu erzeugenden
	 *            {@link FileController}s.
	 * @param pFileDao
	 *            Die {@link FileDAO} des zu erzeugenden
	 *            {@link FileController}s.
	 * @param pUserDao
	 *            Die {@link UserDAO} des zu erzeugenden
	 *            {@link FileController}s.
	 * @throws IllegalArgumentException
	 *             Falls einer der übergebenen Parameter {@code null} ist.
	 */
	@Inject
	public FileController(final Session pSession, final FileDAO pFileDao, final UserDAO pUserDao) {
		super(Assertion.assertNotNull(pSession));
		fileDao = Assertion.assertNotNull(pFileDao);
		userDao = Assertion.assertNotNull(pUserDao);
	}

	/**
	 * Diese Methode wird von JSF automatisch aufgerufen, sobald diese Bean als
	 * eingebunden deklariert wird. Sie bereitet die Benutzung der Seite durch
	 * den User vor, indem sie den Dateipfad für die Dateien generiert, sowie
	 * eine neue Datei, die der Nutzer manipuliert, sollte er eine Datei
	 * hochladen. Außerdem wird hier files initialisiert.
	 **/
	@PostConstruct
	public void init() {
		filePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		filePath += "resources/pdf/";
		user = getSession().getUser();
		file = new File();
		files = new ArrayList<>(user.getFiles());
		Collections.sort(files);
	}

	public Set<File> getFiles() {
		return getSession().getUser().getFiles();
	}

	public File getFile() {
		return file;
	}

	public File getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
	}

	/**
	 * Lädt ein neue Datei hoch. Dabei wird kontrolliert, ob der Nutzer dazu
	 * berechtigt ist. Danach wird sich das UploadedFile von Primefaces geholt,
	 * sodass die Daten der Datei auf ein neues Objekt der Klasse {link File}
	 * gesetzt werden können. Danach wird der Inhalt der hochgeladenen Datei in
	 * das Objekt geschrieben.
	 * 
	 * Sobald der Upload durchgeführt wurde, wird init() aufgerufen, um die
	 * Seite in den Ursprungszustand zurückzuversetzen.
	 * 
	 * @param event
	 *            Hochgeladene Datei
	 */
	public void upload(FileUploadEvent event) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to upload a profile picture!");
			return;
		}
		UploadedFile uploadedFile = event.getFile();
		try {
			assertNotNull(uploadedFile);
			assertNotNull(file);
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorCannotUpload");
			return;
		}
		try {
			java.io.File checkFile = null;
			String dataEnd="";
			String generatedId="";
			while (checkFile == null || checkFile.exists()) {
				generatedId = UUID.randomUUID().toString();
				String[] splitter = uploadedFile.getFileName().split("\\.");
				dataEnd = "." + splitter[splitter.length - 1];
				checkFile = new java.io.File(filePath + generatedId + dataEnd);
			}
			FileOutputStream fos = new FileOutputStream(filePath + generatedId + dataEnd);
			fos.write(uploadedFile.getContents());
			fos.close();
			file.setUser(user);
			file.setFile(uploadedFile);
			file.setName(generatedId + dataEnd);
			file.setContent(uploadedFile.getContents());
			file.setPath(filePath);
			file.setDate(Calendar.getInstance(TimeZone.getTimeZone("CEST"), Locale.GERMANY).getTime());
			user.addFile(file);

			int mark = 0;
			ArrayList<File> fileList = new ArrayList<>(user.getFiles());
			for (int i = 0; i < fileList.size(); i++) {
				File f = fileList.get(i);
				if (f.getShowName() != null
						&& f.getShowName().equals(uploadedFile.getFileName() + (mark == 0 ? "" : " (" + mark + ")"))) {
					mark++;
					i = -1;
				}
			}
			file.setShowName(uploadedFile.getFileName() + (mark == 0 ? "" : " (" + mark + ")"));

			fileDao.create(file);
			userDao.update(user);
			addMessage("successUploadComplete");
		} catch (Exception e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorCannotUpload");
			e.printStackTrace();
		}
		init();
	}

	/**
	 * Löscht die vom Nutzer ausgewählte Datei. Dabei wird die Datei aus dem
	 * User entfernt und der entsprechende Datenbankeintrag gelöscht.
	 */
	public void delete() {
		Logger logger = getLogger();
		try {
			assertNotNull(selectedFile);
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorFileDelete");
			return;
		}
		user.removeFile(selectedFile);
		try {
			userDao.update(assertNotNull(user));
			fileDao.delete(selectedFile);
			addMessage("successFileDelete");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorEmailAlreadyInUse", user.getEmail());
		}
		java.io.File deleteFile = new java.io.File(selectedFile.getPath() + selectedFile.getName());
		deleteFile.delete();
		addMessage("successFileDelete");
	}

	/**
	 * Generiert den Link zu der angegebenen Datei.
	 * 
	 * @param file
	 *            die Datei, dessen Dateipfad generiert werden soll.
	 * @return Dateipfad der Datei
	 */
	public String generateLink(File file) {
		HttpServletRequest requestObj = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		String result = getBaseUrl(requestObj);
		try {
			assertNotNull(file);
			assertNotNull(file.fileExists() ? file : null);
			result += "/gradelog/resources/pdf/" + file.getName();
		} catch (IllegalArgumentException e) {
			result += "/gradelog/resources/pdf/ERROR_FILE_NOT_FOUND.pdf";
		}
		return result;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFiles(Set<File> files) {
		this.files = new ArrayList<>(files);
	}
	
	 public String getFileDate(File file)
    {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
    	return dateFormat.format(file.getDate());
    }
	 
	 public int getFileSize()
	 {
		 return files.size();
	 }

	/**
	 * Gibt die Basis-Url zurück (verweist auf das root-Directory). Wird
	 * benutzt, um den Dateipfad zu generieren.
	 * 
	 * @param request
	 *            der HTTP-Request, um den Link zu generieren.
	 * @return Basis-Url der Applikation
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		String scheme = request.getScheme() + "://";
		String serverName = request.getServerName();
		String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
		return scheme + serverName + serverPort;
	}

}
