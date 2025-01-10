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

    private Utilisateurs(String name, String lastname) throws SQLException {
        Id_user = findId();
        this.lastname = lastname;
        this.name = name;
        try (Connection con = Connect.getConnection();
             Statement stm = con.createStatement()) {
            String query = "INSERT INTO utilisateur (id_user, name, lastname)" +
                    " VALUES (" + Id_user + ",'" + name + "','" + lastname + "')";
            stm.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("SQL Error during user creation");
            throw e; // 抛出异常，终止对象初始化
        }
    }


    // set an id for the utilisateur
    private int findId() {
        String query = "SELECT MAX(id_user) as lastId FROM utilisateur";
        try (Connection con = Connect.getConnection();
             Statement stm = con.createStatement();
             ResultSet res = stm.executeQuery(query)) {
            return res.next() ? res.getInt("lastId") + 1 : 1; // 如果为空表，从1开始
        } catch (SQLException e) {
            System.out.println("SQL Error");
            e.printStackTrace();
            return 1; // 默认从1开始
        }
    }


    public static Utilisateurs createUtilisateur(String name, String lastname) {
        try {
            return new Utilisateurs(name, lastname);
        } catch (SQLException e) {
            System.out.println("Failed to create user: " + e.getMessage());
            return null; // 返回 null 表示创建失败
        }
    }


    public void update( String key, String value){
        String query = "UPDATE utilisateur SET "+key+" = '"+value+"' WHERE id_user = "+Id_user;
        try(Connection con = Connect.getConnection();
            Statement stm = con.createStatement()){
            stm.executeUpdate(query);
        }catch (SQLException e){
            System.out.println("SQL Error");
            e.printStackTrace();
        }
    }
    public void setAdress(String adress) {
        this.adress = adress;
        update("adress",adress);
    }

    public void setTel(String tel) {
        this.tel = tel;
        update("tel",tel);
    }
}
