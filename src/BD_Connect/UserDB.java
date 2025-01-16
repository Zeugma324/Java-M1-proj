package BD_Connect;

import connexion.Connect;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;

import Objects.User;


public class UserDB {

	private static String hash(String a) throws NoSuchAlgorithmException {
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(a.getBytes());
		return HexFormat.of().formatHex(hash);
	}
	
	public static User findUserBylogin(String email, String mdp) throws SQLException, NoSuchAlgorithmException {
		String query = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
		PreparedStatement ps = Connect.executeQueryPrepared(query);
		ps.setString(1, email);
		ps.setString(2, hash(mdp));
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			System.out.println("utilisateur récuperé avec succès");
			return new User(rs.getInt("Id_user"),rs.getString("lastname"),rs.getString("name"),null, null, null, email, mdp, null, null);
		}
		return null;
	}
	
	public static User createUser(String lastname, String name, String email, String mdp, String tel, String address, String gender, String birthday) 
	        throws SQLException, NoSuchAlgorithmException {
	    String qr = String.format("INSERT INTO utilisateur (email, mot_de_passe, lastname, name, tel, adress, gender, date_de_naissance) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", email, hash(mdp), lastname, name, tel, address, gender.equals("M") ? "homme" : "femme", birthday);
	    Connect.creationWithAutoIncrement(qr);
	    Connect.closeConnexion();
	    System.out.println("utilisateur créé avec succés");
	    return findUserBylogin(email, mdp);
	}


}
