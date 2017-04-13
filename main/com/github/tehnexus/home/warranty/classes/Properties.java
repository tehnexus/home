package com.github.tehnexus.home.warranty.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.util.Util;

public class Properties extends HashMap<Integer, Property> {

	private final Identifier				identifier;
	private HashMap<Identifier, Properties>	allTypes	= new HashMap<>(0);

	public Properties(Identifier identifier, ResultSet rs) throws SQLException {
		this.identifier = identifier;

		if (Util.isAnyOf(identifier, Identifier.SHOPTYPE, Identifier.ATTACHMENTTYPE, Identifier.PAYMENT)) {
			doTypes(rs);
			return;
		}

		switch (identifier) {
		case ATTACHMENT:
			doAttachments(rs);
			break;
		case MANUFACTURER:
			doManufacturers(rs);
			break;
		case PRODUCT:
			doProducts(rs);
			break;
		case SHOP:
			doShops(rs);
			break;
		default:
			break;
		}

	}

	public int getNewId() {
		int newid = 0;
		while (true) {
			newid++;
			boolean idexists = false;
			for (int id : keySet()) { // loop product keys (ids)
				if (newid == id) {
					idexists = true;
					break;
				}
			}
			if (!idexists)
				return newid;
		}
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public Properties getTypes(Identifier identifier) {
		return allTypes.get(identifier);
	}

	public void setTypeValues(Identifier identifier, Properties allTypes, boolean isForeign) {
		this.allTypes.put(identifier, allTypes);
		if (isForeign)
			setForeignTypes(allTypes, identifier);
		else
			setTypes(allTypes, identifier);
	}

	private void doAttachments(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int id = rs.getInt(1);
			int idForeign = rs.getInt(2);
			int idType = rs.getInt(3);
			String comment = rs.getString(5);

			Attachment attach = new Attachment.Builder(id).comment(comment).idForeign(idForeign).typeId(idType).build();

			put(attach.getId(), attach);
		}
	}

	private void doManufacturers(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int id = rs.getInt(1);
			String name = rs.getString(2);
			String fullname = rs.getString(3);
			String address = rs.getString(4);
			String url = rs.getString(5);
			String email = rs.getString(6);
			String comment = rs.getString(7);

			Manufacturer manu = new Manufacturer.Builder(id).name(name).fullname(fullname).address(address).url(url)
					.email(email).comment(comment).build();

			put(manu.getId(), manu);
		}
	}

	private void doProducts(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int id = rs.getInt(1);
			String name = rs.getString(2);
			String fullname = rs.getString(3);
			String serial = rs.getString(4);
			long date = rs.getInt(5);
			double warranty = rs.getDouble(6);
			double price = rs.getDouble(7);
			String order = rs.getString(8);
			String invoice = rs.getString(9);
			String customer = rs.getString(10);
			String comment = rs.getString(11);

			int idShop = rs.getInt(14);
			int idManu = rs.getInt(13);
			int idPay = rs.getInt(15);

			Product product = new Product.Builder(id).name(name).fullname(fullname).serial(serial).date(date)
					.warranty(warranty).price(price).order(order).invoice(invoice).customer(customer).comment(comment)
					.idShop(idShop).idManu(idManu).idPay(idPay).build();

			put(id, product);
		}
	}

	private void doShops(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int id = rs.getInt(1);
			String name = rs.getString(2);
			String fullname = rs.getString(3);
			String address = rs.getString(4);
			String email = rs.getString(5);
			String phone = rs.getString(6);
			String fax = rs.getString(7);
			String comment = rs.getString(8);
			int idType = rs.getInt(9);

			Shop shop = new Shop.Builder(id).name(name).fullname(fullname).address(address).email(email).phone(phone)
					.fax(fax).comment(comment).idType(idType).build();

			put(id, shop);
		}
	}

	private void doTypes(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int id = rs.getInt(1);
			String name = rs.getString(2);

			Property p = new Property(id, name);
			put(p.getId(), p);
		}
	}

	private void setForeignTypes(Properties allTypes, Identifier identifier) {
		for (Entry<Integer, Property> eParent : entrySet()) {

			Property parent = eParent.getValue(); // product
			for (Entry<Integer, Property> eChild : allTypes.entrySet()) {

				Property child = eChild.getValue();
				if (child.getIdForeign() == parent.getId()) { // assign attachments
					parent.setType(identifier, child, -1);
				}
			}
		}
		return;
	}

	private void setTypes(Properties allTypes, Identifier identifier) {
		for (Entry<Integer, Property> eParent : entrySet()) {

			Property parent = eParent.getValue();
			for (Entry<Integer, Property> eChild : allTypes.entrySet()) {

				Property child = eChild.getValue();
				if (child.getId() == parent.getIdType(identifier)) {
					parent.setType(identifier, child, -1);
					break;
				}
			}
		}
		return;
	}
}
