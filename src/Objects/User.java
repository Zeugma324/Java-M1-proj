package Objects;

import connexion.Connect;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class User {
    private final int idUser;
    private String lastname;
    private String name;
    private String tel;
    private String address;
    private Panier_old panier;

    private User(int idUser) throws SQLException {
        this.idUser = idUser;
        String query = "SELECT * FROM utilisateur WHERE id_user = " + idUser + ";";
        try (Connection con = Connect.getConnexion();
            Statement stm = con.createStatement();
            ResultSet result = Connect.executeQuery(query);) {
            if (result.next()) {
                name = result.getString("name");
                lastname = result.getString("lastname");
                address = result.getString("adress");
                tel = result.getString("tel");
            }
        }
    }

    public Panier_old getPanier() {
        return panier;
    }

    private boolean haveValidPanier() throws SQLException {
        return Connect.recordExists("SELECT * FROM panier WHERE id_user = " + idUser + " AND Date_fin IS NULL");
    }

    // UNFINI
    public void connectPanier() throws SQLException {
        panier = new Panier_old(this,haveValidPanier());
    }

    public static User findUtilisateur(int idUser) throws SQLException {
        return new User(idUser);
    }

    private static int addUserDB(String name, String lastname) throws SQLException {
        String query = "INSERT INTO utilisateur (name, lastname) VALUES ('" + name + "', '" + lastname + "');";
        int idUser = Connect.creationWithAutoIncrement(query);
        return idUser;
    }


    public static User createUtilisateur(String name, String lastname) throws SQLException {
        int idUser = addUserDB(name,lastname);
        System.out.println("Votre id est = " + idUser);
        return new User(idUser);
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
    public static void main(String[] args) throws SQLException {
        User user1 = User.createUtilisateur("Kaiyang","ZHANG");

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

    public ArrayList<Panier_old> HistoryPanier() throws SQLException {
        ArrayList<Panier_old> panierList = new ArrayList<>();
        String query = "SELECT * FROM panier WHERE Id_user = " + idUser + " AND Date_fin IS NOT NULL";
        try (Connection con = Connect.getConnexion();
             Statement stm = con.createStatement();
             ResultSet result = stm.executeQuery(query);) {
            while (result.next()) {
                panierList.add(new Panier_old(result.getInt("Id_panier")));
            }
        }
        return panierList;
    }

}
