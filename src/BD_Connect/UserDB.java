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

	public static User findUserById(int id) throws SQLException {
		String query = "SELECT * FROM utilisateur WHERE id_user = " + id;
		ResultSet rs = Connect.executeQuery(query);
		if(rs.next()) {
			User user = new User(
				id,
				rs.getString("lastname"),
				rs.getString("name"),
				rs.getString("tel"),
				rs.getString("adress"),
				rs.getString("email"),
				rs.getString("mot_de_passe")
			);
			user.setPanier(PanierBD.loadPanierByUser(user)) ;
			return user;
		}
		return null;
	}
	
	public User findUserBylogin(String email, String mdp) throws SQLException, NoSuchAlgorithmException {
		String query = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
		PreparedStatement ps = Connect.executeQueryPrepared(query);
		ps.setString(1, email);
		ps.setString(2, hash(mdp));
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			return new User(rs.getInt("Id_user"),rs.getString("lastname"),rs.getString("name"),null, null, null, email, mdp);
		}
		return null;
	}
	
	public int createUser (String lastname, String name, String email, String mdp) throws SQLException, NoSuchAlgorithmException {
		String qr = String.format("INSERT INTO utilisateur (email, mot_de_passe, lastname, name) VALUES ('%s', '%s', '%s', '%s')",email, hash(mdp),lastname, name);
		return Connect.creationWithAutoIncrement(qr);
		
	}

}
