package Objects;

import connexion.Connect;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class User {
	private int idUser;
	private String lastname;
	private String name;
	private String tel;
	private String address;
	private Panier panier;
	private String email;
	private String mdp;

	private User(String email, String mdp) throws SQLException, NoSuchAlgorithmException {
		this.email = email;
		this.mdp = hash(mdp);
		String query = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ? ;";

		PreparedStatement ps = Connect.executeQueryPrepared(query);
		ps.setString(1, this.email);
		ps.setString(2, this.mdp);
		ResultSet res = ps.executeQuery();
		if (res.next()) {
			name = res.getString("name");
			lastname = res.getString("lastname");
			address = res.getString("adress");
			tel = res.getString("tel");
		}
	}

	public Panier getPanier() {
		return panier;
	}

	private boolean haveValidPanier() throws SQLException {
		return Connect.recordExists("SELECT * FROM panier WHERE id_user = " + idUser + " AND Date_fin IS NULL");
	}

	// UNFINI
	public void connectPanier() throws SQLException {
		panier = new Panier(this,haveValidPanier());
	}

	public static User findUtilisateur(String email, String mdp) throws SQLException, NoSuchAlgorithmException {
		return new User(email, mdp);
	}

	private static int addUserDB(String email, String mdp, String lastname, String name) throws SQLException, NoSuchAlgorithmException {
		String query = "INSERT INTO utilisateur (email, mot_de_passe, lastname, name) VALUES ('" + email + "', '" + hash(mdp) + "','" + lastname + "','" + name + "');";
		int idUser = Connect.creationWithAutoIncrement(query);
		return idUser;
	}


	public static User createUtilisateur(String email, String mdp,String lastname,String name) throws SQLException, NoSuchAlgorithmException {
		int idUser = addUserDB(email,mdp, lastname, name);
		System.out.println("Votre id est = " + idUser);
		return new User(email, mdp);
	}


	public void update( String key, String value) throws SQLException {
		String query = "UPDATE utilisateur SET "+key+" = '"+value+"' WHERE id_user = "+ idUser;
		Connect.executeUpdate(query);
	}

	public String getAdress() {
		return address;
	}

	public void setAdress(String adress) throws SQLException {
		this.address = adress;
		update("adress",adress);
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) throws SQLException {
		this.tel = tel;
		update("tel",tel);
	}

	public String getName() {
		return name;
	}

	public String getLastname() {
		return lastname;
	}

	public int getId() {
		return idUser;
	}

	@Override
	public String toString() {
		return "User{" +
				"idUser=" + idUser +
				", name='" + name + '\'' +
				", lastname='" + lastname + '\'' +
				'}';
	}
	public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
		User user1 = User.createUtilisateur("Kaiyang@gmail.com","Kaiyang", "Kaiyang" , "test");

		System.out.println(user1);
		user1.setAdress("30 Ave chocolat");
		user1.setTel("0612345678");
		System.out.println(user1);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return idUser == user.idUser;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(idUser);
	}

	public ArrayList<Panier> HistoryPanier() throws SQLException, NoSuchAlgorithmException {
		ArrayList<Panier> panierList = new ArrayList<>();
		String query = "SELECT * FROM panier WHERE Id_user = " + idUser + " AND Date_fin IS NOT NULL";
		try (Connection con = Connect.getConnexion();
				Statement stm = con.createStatement();
				ResultSet result = stm.executeQuery(query);) {
			while (result.next()) {
				panierList.add(new Panier(result.getInt("Id_panier")));
			}
		}
		return panierList;
	}

	private static String hash (String a) throws NoSuchAlgorithmException {
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(a.getBytes());
		return HexFormat.of().formatHex(hash);
	}

}
