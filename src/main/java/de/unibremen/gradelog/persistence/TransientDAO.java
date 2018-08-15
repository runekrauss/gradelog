package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;

/**
 * Eine DAO, welche Listen verwendet, um einen für die Laufzeit der Applikation
 * gültigen Datenbestand zu realisieren. Alle Operationen, die auf den internen
 * Datenbestand zugreifen, sind durch geeignete Maßnahmen synchronisiert, sodass
 * Instanzen dieser Klasse parallel verwendet werden können.
 *
 * @param <T>
 *            Das durch die Liste zu verwaltene Datum.
 * 
 * @author Rune Krauss
 */
public class TransientDAO<T> implements GenericDAO<T> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = -5626655545273831970L;

	/**
	 * Der Lock-Mechanismus um kritische Operationen vor konkurierende Zugriffe
	 * zu schützen.
	 */
	private final ReentrantReadWriteLock lock;

	/**
	 * Der Datenbestand.
	 */
	private final ArrayList<T> data;

	/**
	 * Erzeugt ein Listen-DAO mit einem neuen Lock und einem leeren
	 * Datenbestand.
	 */
	public TransientDAO() {
		lock = new ReentrantReadWriteLock();
		data = new ArrayList<>();
	}

	@Override
	public void create(final T t) {
		assertNotNull(t);
		try {
			lock.writeLock().lock();
			data.add(t);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void delete(final T t) {
		assertNotNull(t);
		try {
			lock.writeLock().lock();
			data.remove(t);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Gibt eine Kopie des internen Datenbestandes zurück.
	 * 
	 * @return Eine Kopie des internen Datenbestandes.
	 */
	public List<T> getAll() {
		try {
			lock.readLock().lock();
			return new ArrayList<>(data);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void update(T object) throws DuplicateUniqueFieldException {
	}

}
