package Requets.Panier;

import BD_Connect.PanierBD;
import BD_Connect.UserDB;
import Objects.User;
import connexion.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class main {




    public static void main(String[] args) throws SQLException {
//        User user = UserDB.findUserById(1);
//        System.out.println(user.getBirthday().toString());
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        ResultSet res = Connect.executeQuery("select date_de_naissance from utilisateur where id_user = 1");
//        if(res.next()){
//            String date_de_naissance = res.getString("date_de_naissance");
//            System.out.println(date_de_naissance);
//            LocalDate localDate = LocalDate.parse(date_de_naissance, formatter);
//            System.out.println(localDate);
//        }
        String query = "SELECT id_user, lastname, name, tel, adress, email, mot_de_passe, gender, date_de_naissance FROM utilisateur WHERE id_user = " + 1;
        ResultSet rs = Connect.executeQuery(query);
//        if (rs.next()) {
//            System.out.println(rs.getString("date_de_naissance"));
//        }

        if(rs.next()) {
            User user = new User(
                    1,
                    rs.getString("lastname"),
                    rs.getString("name"),
                    rs.getString("tel"),
                    rs.getString("adress"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("gender"),
                    rs.getString("date_de_naissance"));
            user.setPanier(PanierBD.loadPanierByUser(user)) ;
            System.out.println(user);
        }
    }

}
