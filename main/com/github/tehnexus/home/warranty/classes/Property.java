package com.github.tehnexus.home.warranty.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.warranty.tree.XTreeNode;

public class Property extends XTreeNode {

	private final int							id;
	private String								name		= "";
	private boolean								isDummy		= false;

	private HashMap<Identifier, List<Integer>>	idTypes		= new HashMap<>(0);
	private HashMap<Identifier, List<Property>>	types		= new HashMap<>(0);

	// used for one-to-many relationship in database:
	private int									idForeign	= -1;

	public Property(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Property(int id, String name, boolean isDummy) {
		this.id = id;
		this.name = name;
		setDummy(isDummy);
	}

	public int getId() {
		return id;
	}

	public int getIdForeign() {
		return idForeign;
	}

	public List<Integer> getIdType(Identifier identifier) {
		return idTypes.get(identifier);
	}

	public String getName() {
		return name;
	}

	public List<Property> getType(Identifier identifier) {
		return types.get(identifier);
	}

	public boolean isDummy() {
		return isDummy;
	}

	public void removeType(Identifier identifier, Property type) {
		idTypes.get(identifier).remove(new Integer(type.getId()));
		types.get(identifier).remove(type);
	}

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}

	protected void setIdForeign(int idForeign) {
		this.idForeign = idForeign;
	}

	protected void setIdType(Identifier identifier, int idType, int index) {
		if (idTypes.containsKey(identifier)) {
			if (index > -1)
				idTypes.get(identifier).set(index, idType);
			else
				idTypes.get(identifier).add(idType);
		}
		else {
			List<Integer> list = new ArrayList<>(0);
			list.add(idType);
			idTypes.put(identifier, list);
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(Identifier identifier, Property type, int index) {
		if (types.containsKey(identifier)) {
			if (index > -1)
				types.get(identifier).set(index, type);
			else
				types.get(identifier).add(type);
		}
		else {
			List<Property> list = new ArrayList<>(0);
			list.add(type);
			types.put(identifier, list);
		}
		setIdType(identifier, type.getId(), index);
	}

	@Override
	public String toString() {
		return name;
	}
}