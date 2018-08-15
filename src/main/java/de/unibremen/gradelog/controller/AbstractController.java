package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;
import static de.unibremen.gradelog.util.Assertion.assertWithoutNull;

import java.io.Serializable;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;

/**
 * Basisklasse der Controller, welcher wichtige Funktionalitäten - wie z. B.
 * einen Benutzer aus der aktuellen Session zu holen - bereitstellt. Zudem kann
 * man hierüber auch Meldungen generieren, welche dann in Form von z. B.
 * Infopanel angezeigt werden können.
 * 
 * @author Rune Krauss
 */
public abstract class AbstractController implements Serializable {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 7307194544248643534L;

	/**
	 * Der Logger für diese Klasse.
	 */
	private static final Logger logger = Logger.getLogger(AbstractController.class);

	/**
	 * Enthält die aktuelle Session.
	 */
	private final Session session;

	/**
	 * Erzeugt einen {@link AbstractController} mit definierter
	 * {@link Session}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link AbstractController}s.
	 * @throws IllegalArgumentException
	 * 		Falls {@code pSession == null}.
     */
	public AbstractController(final Session pSession) {
		session = Assertion.assertNotNull(pSession);
	}

	/**
	 * Gibt an, ob in der zugehörigen Session aktuell jemand eingeloggt ist oder
	 * nicht.
	 *
	 * @return {@code true} Falls in der zugehörigen Session jemand eingeloggt
	 *         ist, sonst {@code false}.
	 */
	public boolean isLoggedIn() {
		return session.isLoggedIn();
	}

	/**
	 * Meldet die Fehlermeldung an das gegebene Ziel im gegebenen Kontext.
	 * 
	 * @param targetClientId
	 *            ClientId der UI-Komponente, die die Nachricht anzeigen soll.
	 * @param formatText
	 *            Die Fehlernachricht als Format-String.
	 * @param formatParameters
	 *            Format-Parameter für die Fehlernachricht.
	 * @throws IllegalArgumentException
	 *             falls {@code formatText == null} oder
	 *             {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	void addMessage(final String targetClientId, final String formatText, final Object... formatParameters) {
		final String message = getTranslation(assertNotNull(formatText), assertWithoutNull(formatParameters));
		final FacesContext context = FacesContext.getCurrentInstance();
		final FacesMessage facesMessage = new FacesMessage(message);
		context.addMessage(targetClientId, facesMessage);
	}

	/**
	 * Meldet die Fehlermeldung an das gegebene Ziel im gegebenen Kontext.
	 * 
	 * @param target
	 *            UI-Komponente, die die Nachricht anzeigen soll.
	 * @param formatText
	 *            Die Fehlernachricht als Format-String.
	 * @param formatParameters
	 *            Format-Parameter für die Fehlernachricht.
	 * @throws IllegalArgumentException
	 *             falls {@code formatText == null} oder
	 *             {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	void addMessage(final UIComponent target, final String formatText, final Object... formatParameters) {
		final String targetClientId = target == null ? null : target.getClientId(FacesContext.getCurrentInstance());
		addMessage(targetClientId, formatText, formatParameters);
	}

	/**
	 * Meldet die Fehlermeldung an den Kontext weiter.
	 * 
	 * @param formatText
	 *            Die Fehlernachricht als Format-String.
	 * @param formatParameters
	 *            Format-Parameter für die Fehlernachricht.
	 * @throws IllegalArgumentException
	 *             falls {@code formatText == null} oder
	 *             {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	protected void addMessage(final String formatText, final Object... formatParameters) {
		addMessage((String) null, formatText, formatParameters);
	}

	/**
	 * Meldet die Fehlermeldung an das gegebene Ziel im gegebenen Kontext weiter
	 * und protokolliert die Meldung inklusive der aufgetretenen Exception mit
	 * gegegeben Logging-Priorität, sofern diese aktiv ist.
	 * 
	 * @param targetClientId
	 *            ClientId der UI-Komponente, die die Nachricht anzeigen soll.
	 * @param e
	 *            Die aufgetretene Exception.
	 * @param logger
	 *            Logger, in welchem die Fehlernachricht geloggt werden soll.
	 * @param level
	 *            Das Logging-Level.
	 * @param formatText
	 *            Die Fehlernachricht als Format-String.
	 * @param formatParameters
	 *            Format-Parameter für die Fehlernachricht.
	 * @throws IllegalArgumentException
	 *             falls {@code priority == null} oder
	 *             {@code formatText == null} oder
	 *             {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	void addMessageWithLogging(final String targetClientId, final Exception e, final Logger logger, final Level level,
			final String formatText, final Object... formatParameters) {
		addMessage(targetClientId, assertNotNull(formatText), assertWithoutNull(formatParameters));
		if (logger.isEnabledFor(assertNotNull(level))) {
			final String logMessage = getLogTranslation(formatText, formatParameters)
					+ String.format(" (key: '%s')", formatText);
			if (e == null) {
				logger.log(level, logMessage);
			} else {
				logger.log(level, logMessage, e);
			}
		}
	}

	/**
	 * Meldet die Fehlermeldung an den Kontext weiter und protokolliert die
	 * Meldung inklusive der aufgetretenen Exception mit gegegeben
	 * Logging-Priorität, sofern diese aktiv ist.
	 * 
	 * @param e
	 *            Die aufgetretene Exception.
	 * @param logger
	 *            Logger, in welchem die Fehlernachricht geloggt werden soll.
	 * @param level
	 *            Das Logging-Level.
	 * @param formatText
	 *            Die Fehlernachricht als Format-String.
	 * @param formatParameters
	 *            Format-Parameter für die Fehlernachricht.
	 * @throws IllegalArgumentException
	 *             falls {@code priority == null} oder
	 *             {@code formatText == null} oder
	 *             {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	void addMessageWithLogging(final Exception e, final Logger logger, final Level level, final String formatText,
			final Object... formatParameters) {
		addMessageWithLogging(null, e, logger, level, formatText, formatParameters);
	}

	/**
	 * Liefert die aktuelle Sprache zurück. Dies ist die Sprache des aktuellen
	 * Benutzers, sofern einer eingeloggt ist. Ansonsten ist es die vom Client
	 * angeforderte Sprache, sofern vorhanden. Sollte auch diese nicht zur
	 * Verfügung stehen, ist es die Standardsprache der Applikation.
	 * 
	 * @return Aktuelle Sprache.
	 */
	public Locale getLanguage() {
		if (session != null) {
			final User user = session.getUser();
			if (user != null) {
				final Locale language = user.getLanguage();
				if (language != null) {
					logger.debug(
							String.format("Current language is acquired from user: %s [USER]", language.toString()));
					return language;
				}
			}
		}
		final FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			final UIViewRoot root = context.getViewRoot();
			final Locale clientLanguage = root == null
					? context.getApplication().getViewHandler().calculateLocale(context) : root.getLocale();
			if (clientLanguage != null) {
				logger.debug(String.format("Current language is acquired from client: %s [FACE]", clientLanguage));
				return clientLanguage;
			}
		}
		final Locale defaultLanguage = User.getDefaultLanguage();
		logger.debug(
				String.format("Current language is acquired from application default: %s [DEFAULT]", defaultLanguage));
		return defaultLanguage;
	}

	/**
	 * Liefert den Ausdruck zum gegebenen Schlüssel in der aktuellen Sprache
	 * zurück. Ist für den gegebenen Schlüssel in der aktuellen Sprache kein
	 * Ausdruck vorhanden, wird eine Standardzeichenkette inklusive des
	 * gegebenen Schlüssels zurückgegeben.
	 * 
	 * @param messageKey
	 *            Der Schlüssel des Ausdrucks, welcher in den
	 *            Internationalisierungs-Dateien verwendet wird.
	 * @param formatParameters
	 *            Format-Parameter zum Einfügen in die übersetzte Nachricht.
	 * @return Der Ausdruck, der dem gegebenen Schlüssel in der aktuellen
	 *         Sprache zugeordnet ist.
	 * @throws IllegalArgumentException
	 *             falls {@code messageKey == null} oder
	 *             {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	public String getTranslation(final String messageKey, final Object... formatParameters) {
		return getTranslation(getLanguage(), assertNotNull(messageKey), assertWithoutNull(formatParameters));
	}

	/**
	 * Liefert den Ausdruck zum gegebenen Schlüssel in der Logging-Sprache
	 * zurück. Ist für den gegebenen Schlüssel in der Logging-Sprache kein
	 * Ausdruck vorhanden, wird eine Standardzeichenkette inklusive des
	 * gegebenen Schlüssels zurückgegeben.
	 * 
	 * @param messageKey
	 *            Der Schlüssel des Ausdrucks, welcher in den
	 *            Internationalisierungs-Dateien verwendet wird.
	 * @param formatParameters
	 *            Format-Parameter zum Einfügen in die übersetzte Nachricht.
	 * @return Der Ausdruck, der dem gegebenen Schlüssel in der Logging-Sprache
	 *         zugeordnet ist.
	 * @throws IllegalArgumentException
	 *             falls {@code messageKey == null} oder
	 *             {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	public String getLogTranslation(final String messageKey, final Object... formatParameters) {
		return getTranslation(Locale.ENGLISH, assertNotNull(messageKey), assertWithoutNull(formatParameters));
	}

	/**
	 * Liefert den Ausdruck zum gegebenen Schlüssel in der gegebenen Sprache
	 * zurück. Ist für den gegebenen Schlüssel in der gegebenen Sprache kein
	 * Ausdruck vorhanden, wird eine Standardzeichenkette inklusive des
	 * gegebenen Schlüssels zurückgegeben.
	 *
	 * @param locale
	 *            Die gewünschte Sprache, in welche übersetzt werden soll.
	 * @param messageKey
	 *            Der Schlüssel des Ausdrucks, welcher in den
	 *            Internationalisierungs-Dateien verwendet wird.
	 * @param formatParameters
	 *            Format-Parameter zum Einfügen in die übersetzte Nachricht.
	 * @return Der Ausdruck, der dem gegebenen Schlüssel in der gegebenen
	 *         Sprache zugeordnet ist.
	 * @throws IllegalArgumentException
	 *             falls {@code locale == null} oder {@code messageKey == null}
	 *             oder {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist oder {@code logger == null}
	 *             und eine Problemmeldung anfällt.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	public static String getTranslation(final Locale locale, final String messageKey,
			final Object... formatParameters) {
		try {
			final ResourceBundle bundle = ResourceBundle.getBundle("internationalization.general",
					assertNotNull(locale));
			return getTranslation(bundle, assertNotNull(messageKey), assertNotNull(formatParameters));
		} catch (final MissingResourceException e) {
			assertNotNull(logger).error(String.format(
					"Severe internationalization error: Internationalization bundle for locale '%s' not found!",
					locale.toString()), e);
		}
		return String.format("No message found for key '%s'!", messageKey);
	}

	/**
	 * Liefert den Ausdruck zum gegebenen Schlüssel in der gegebenen Sprache
	 * zurück. Ist für den gegebenen Schlüssel in der gegebenen Sprache kein
	 * Ausdruck vorhanden, wird eine Standardzeichenkette inklusive des
	 * gegebenen Schlüssels zurückgegeben.
	 *
	 * @param messageKey
	 *            Der Schlüssel des Ausdrucks, welcher in den
	 *            Internationalisierungs-Dateien verwendet wird.
	 * @param formatParameters
	 *            Format-Parameter zum Einfügen in die übersetzte Nachricht.
	 * @return Der Ausdruck, der dem gegebenen Schlüssel in der gegebenen
	 *         Sprache zugeordnet ist.
	 * @throws IllegalArgumentException
	 *             falls {@code locale == null} oder {@code messageKey == null}
	 *             oder {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist oder {@code logger == null}
	 *             und eine Problemmeldung anfällt.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	private static String getTranslation(final ResourceBundle bundle, final String messageKey,
			final Object... formatParameters) {
		try {
			final String message = assertNotNull(bundle).getString(assertNotNull(messageKey));
			return String.format(message, assertWithoutNull(formatParameters));
		} catch (final MissingResourceException e) {
			return resolveTranslationError(bundle, messageKey, e);
		}
	}

	/**
	 * Liefert den Ausdruck zum gegebenen Schlüssel in der gegebenen Sprache
	 * zurück. Ist für den gegebenen Schlüssel in der gegebenen Sprache kein
	 * Ausdruck vorhanden, wird eine Standardzeichenkette inklusive des
	 * gegebenen Schlüssels zurückgegeben.
	 *
	 * @param messageKey
	 *            Der Schlüssel des Ausdrucks, welcher in den
	 *            Internationalisierungs-Dateien verwendet wird.
	 * @return Der Ausdruck, der dem gegebenen Schlüssel in der gegebenen
	 *         Sprache zugeordnet ist.
	 * @throws IllegalArgumentException
	 *             falls {@code locale == null} oder {@code messageKey == null}
	 *             oder {@code formatParameters == null} ist oder einer der
	 *             Format-Parameter {@code null} ist oder {@code logger == null}
	 *             und eine Problemmeldung anfällt.
	 * @throws IllegalFormatException
	 *             Wenn der Format-String ein unzulässiges oder nicht zu den
	 *             Parametern passendes Format aufweist.
	 */
	private static String resolveTranslationError(final ResourceBundle bundle, final String messageKey,
			final Exception cause) {
		final Logger theLogger = assertNotNull(logger);
		theLogger.warn(String.format("Internationalization error: No message found for key '%s' in language %s!",
				assertNotNull(messageKey), assertNotNull(bundle).getLocale().toString()), cause);
		try {
			return getTranslation(bundle, "errorNoTranslationFound", messageKey);
		} catch (final MissingResourceException e) {
			theLogger.error(
					"Severe internationalization error: No message found for the 'errorNoTranslationFound' key!", e);
			return String.format("No message found for key '%s'!", messageKey);
		}
	}

	protected Session getSession() {
		return session;
	}

	public void setUser(final User theUser) {
		session.setUser(assertNotNull(theUser));
	}

	public User getUser() {
		return session.getUser();
	}

	final protected Logger getLogger() {
		return Logger.getLogger(getClass());
	}

	protected void setLocale() {
		FacesContext.getCurrentInstance().getViewRoot().setLocale(getLanguage());
	}

	protected void setInvalidateSession() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	}
}
