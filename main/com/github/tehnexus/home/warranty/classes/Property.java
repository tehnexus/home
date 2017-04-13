package com.github.tehnexus.home.warranty.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.warranty.tree.XTreeNode;

public class Property extends XTreeNode {

	private final int							id;
	private String								name;

	private HashMap<Identifier, Integer>		idTypes		= new HashMap<>(0);
	private HashMap<Identifier, List<Property>>	types		= new HashMap<>(0);

	private boolean								isDummy		= false;

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

	protected void setIdForeign(int idForeign) {
		this.idForeign = idForeign;
	}

	public int getIdForeign() {
		return idForeign;
	}

	public int getId() {
		return id;
	}

	public int getIdType(Identifier identifier) {
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

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
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
		setIdType(identifier, type.getId());
	}

	@Override
	public String toString() {
		return name;
	}

	protected void setIdType(Identifier identifier, int idType) {
		idTypes.put(identifier, idType);
	}
}