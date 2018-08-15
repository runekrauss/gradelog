package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.util.*;

import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.TreeNodeChildren;

/**
 * Dieses Model repräsentiert einen Knoten in der Telefonkette. Es erbt von dem
 * Interface Treenode.
 * 
 * @author Mirco Bockholt
 */

public class TelephoneNode extends DefaultOrganigramNode {
	
	/** Name */
	private String name;

	/** Telefonnummer */
	private String number;

	/** Das zugehörige Telephone Objekt */
	private Telephone telephone;

	/**
	 * Konstruktor
	 */
	public TelephoneNode() {
	}
	
	public TelephoneNode(String name, String number, TelephoneNode parent)
	{
		super("telephone", name, parent);
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Telephone getTelephone() {
		return telephone;
	}

	public void setTelephone(Telephone telephone) {
		this.telephone = telephone;
	}
	
	@Override
	public Object getData()
	{
		return name + "          " + number;
	}
	
}