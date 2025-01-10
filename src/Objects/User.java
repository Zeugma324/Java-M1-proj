package Objects;

import connexion.Connect;

import java.sql.*;

public class User {
    private int idUser; // Correspond à Id_user
    private String lastname; // Correspond à lastname
    private String name; // Correspond à name
    private String tel; // Correspond à tel
    private String address; // Correspond à adress

    private User(String name, String lastname) throws SQLException {
        this.lastname = lastname;
        this.name = name;

        String query = "INSERT INTO utilisateur (name, lastname) VALUES ( "+ name +"," +lastname+ ");";
        try (Connection con = Connect.getConnexion();
             PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.idUser = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during user creation: " + e.getMessage());
            throw e;
        }
    }


    public static User createUtilisateur(String name, String lastname) {
        try {
            return new User(name, lastname);
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
}
