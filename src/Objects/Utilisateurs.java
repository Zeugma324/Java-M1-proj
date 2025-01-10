package Objects;

import connexion.Connect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Utilisateurs {
    int Id_user;
    String lastname;
    String name;
    String tel;
    String adress;

    public Utilisateurs(String name, String lastname) {
        Id_user = findId();
        this.lastname = lastname;
        this.name = name;
        try(Connection con = Connect.getConnection();
            Statement stm = con.createStatement()){
            String query = "INSERT INTO utilisateur (id_user, name, lastname)" +
                    " VALUES ("+Id_user+",'"+name+"','"+lastname+"')";
            stm.executeUpdate(query);
        }catch (SQLException e){
            System.out.println("SQL Error");
            e.printStackTrace();
        }
    }

    // set an id for the utilisateur
    private int findId(){
        String query = "SELECT MAX(id_user) as lastId FROM utilisateur";
        int id = 0;
        try (Connection con = Connect.getConnection();
             Statement stm = con.createStatement()) {
            ResultSet res = stm.executeQuery(query);
            id = res.getInt("lastId")+1;
        } catch (SQLException e) {
            System.out.println("SQL Error");
            e.printStackTrace();
        }
        return id;
    }

    public void setAdress(String adress) {
        this.adress = adress;
        try(Connection con = Connect.getConnection();
            Statement stm = con.createStatement()){
            String query = "UPDATE utilisateur SET adress = '"+adress+"' WHERE id_user = "+Id_user;
            stm.executeUpdate(query);
        }catch (SQLException e){
            System.out.println("SQL Error");
            e.printStackTrace();
        }
    }

    public void setTel(String tel) {
        this.tel = tel;
        try(Connection con = Connect.getConnection();
            Statement stm = con.createStatement()){
            String query = "UPDATE utilisateur SET tel = '"+tel+"' WHERE id_user = "+Id_user;
            stm.executeUpdate(query);
        }catch (SQLException e){
            System.out.println("SQL Error");
        }
    }
}
