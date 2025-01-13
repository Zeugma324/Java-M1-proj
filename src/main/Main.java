package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Requets.Produit;
import Requets.User;
import Requets.Panier;
import connexion.Connect;

public class Main {
	
	public static void main(String[] args) throws SQLException {
		String query = "SELECT * FROM produit ORDER BY RAND() LIMIT 1";
		ResultSet res = Connect.executeQuery(query);
		
		
		if(res.next()) {
			Produit p1 = new Produit(res.getInt(7), res.getString(1),res.getDouble(2),res.getInt(3),res.getInt(4),res.getInt(5),res.getInt(6),1);
			System.out.println(p1);
			List<Produit> lp = new ArrayList<>();
	        lp.add(p1); 
	        User u1 = new User(
	                1,
	                "Doe",
	                "John",
	                "123-456-7890",
	                "123 Main Street, Quebec"
	            );
			Panier pa1 = new Panier(1, u1);
			pa1.addProduit(p1);
		}
		
		
	}
}
