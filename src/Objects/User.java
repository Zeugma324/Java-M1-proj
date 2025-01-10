package Objects;

import connexion.Connect;

import java.sql.*;

public class User {
    private int idUser;
    private String lastname;
    private String name;
    private String tel;
    private String address;

    private User(int idUser) throws SQLException {
        this.idUser = idUser;
        String query = "SELECT * FROM utilisateur WHERE id_user = " + idUser + ";";
        ResultSet result = Connect.executeQuery(query);
            if (result.next()) {
                name = result.getString("name");
                lastname = result.getString("lastname");
                address = result.getString("adress");
                tel = result.getString("tel");
            }
    }

    public static User findUtilisateur(int idUser) throws SQLException {
        return new User(idUser);
    }

    private static int addUserDB(String name, String lastname) throws SQLException {
        int idUser = 0;
        String query = "INSERT INTO utilisateur (name, lastname) VALUES ('" + name + "', '" + lastname + "');";
        try (Connection con = Connect.getConnexion();
             PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idUser = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        return idUser;
    }


    public static User createUtilisateur(String name, String lastname) {
        try {
            int idUser = addUserDB(name,lastname);
            return new User(idUser);
        } catch (SQLException e) {
            System.out.println("Failed to create user: " + e.getMessage());
            return null;
        }
    }



    public void update( String key, String value){
        String query = "UPDATE utilisateur SET "+key+" = '"+value+"' WHERE id_user = "+ idUser;
        try(Connection con = Connect.getConnexion();
            Statement stm = con.createStatement()){
            stm.executeUpdate(query);
        }catch (SQLException e){
            System.out.println("SQL Error");
            e.printStackTrace();
        }
    }

    public String getAdress() {
        return address;
    }

    public void setAdress(String adress) {
        this.address = adress;
        update("adress",adress);
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
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
//        User user = User.findUtilisateur(1);
//        System.out.println(user);

        User user1 = User.createUtilisateur("Kaiyang","ZHANG");

        System.out.println(user1);
        user1.setAdress("30 Ave chocolat");
        user1.setTel("0612345678");
        System.out.println(user1);
    }
}
