package com.github.tehnexus.sqlite;

public class SQLStrings {

	public static String deleteFromtblManufacturer() {
		return "";
	}

	public static String deleteFromtblProduct() {
		return "DELETE FROM tblProduct WHERE ID=?";
	}

	public static String deleteFromtblProductProperties() {
		return "DELETE FROM tblProductProperties WHERE ProductID=?";
	}

	public static String deleteFromtblShop() {
		return "";
	}

	public static String insertIntotblProduct() {
		return "INSERT INTO tblProduct(ID, Name, Description, Serial, "
				+ "Date, Warranty, Price, [Order], Invoice, Customer) " + "VALUES(?,?,?,?,?,?,?,?,?,?)";
	}

	public static String insertIntotblProductProperties() {
		return "INSERT INTO tblProductProperties(ProductID,ManufacturerID,ShopID,PaymentID)" + " VALUES(?,?,?,?)";
	}

	public static String insertIntotblShop() {
		return "INSERT INTO tblShop(ID,Name,Fullname,Address,EMail,Phone,Fax,Comment) VALUES(?,?,?,?,?,?,?,?)";
	}

	public static String insertIntotblShopProperties() {
		return "INSERT INTO tblShopProperties(ShopID,TypeID) VALUES(?,?)";
	}

	public static String interIntotblManufacturer() {
		return "";
	}

	public static String interIntotblShop() {
		return "";
	}

	public static String intertIntotblProductProperties() {
		return "";
	}

	public static String queryAttachments() {
		return "SELECT * FROM tblAttachment";
	}

	public static String queryAttachmentTypes() {
		return "SELECT * FROM tblAttachmentType";
	}

	public static String queryDefault() {
		return "SELECT * FROM vw_default_all";
	}

	public static String queryManufacturers() {
		return "SELECT * FROM tblManufacturer";
	}

	public static String queryPayments() {
		return "SELECT * FROM tblPayment";
	}

	public static String queryProducts() {
		return "SELECT * FROM tblProduct AS P LEFT JOIN tblProductProperties AS PP ON P.ID = PP.ProductID";
	}

	public static String queryShops() {
		return "SELECT S.ID, S.Name, S.Fullname, S.Address, S.eMail, S.Phone, S.Fax, S.Comment, ST.ID "
				+ "FROM tblShop AS S INNER JOIN tblShopProperties AS SP ON S.ID = SP.ShopID "
				+ "INNER JOIN tblShopType AS ST ON SP.TypeID = ST.ID";
	}

	public static String queryShopTypes() {
		return "SELECT * FROM tblShopType";
	}

	public static String updatetblManufacturer() {
		return "";
	}

	public static String updatetblProduct() {
		return "UPDATE tblProduct SET Name=?,Description=?,Serial=?, "
				+ "Date=?,Warranty=?,Price=?,[Order]=?,Invoice=?,Customer=?,Comment=? WHERE ID=?";
	}

	public static String updatetblProductProperties() {
		return "UPDATE tblProductProperties SET ManufacturerID=?,ShopID=?,PaymentID=? WHERE ProductID=?";
	}

	public static String updatetblShop() {
		return "";
	}
}
