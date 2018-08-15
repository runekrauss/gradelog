package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Schnittstelle zum Nachrichtenempfang.
 * 
 * @author Marco Glander
 *
 */
public interface MessageReceiver extends Serializable {

	String getReceiverName();

	String getDisplayName();

	Set<User> getUserSet();
}
