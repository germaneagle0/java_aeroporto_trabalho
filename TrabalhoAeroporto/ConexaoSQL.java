/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TrabalhoAeroporto;

import java.sql.*;
import java.util.ArrayList;

public class ConexaoSQL {

    public ArrayList<Aeroporto> getConexaoMySQL() {
        ArrayList<Aeroporto> v = new ArrayList();

        String connectionUrl = "jdbc:mysql://localhost:3306/aeroporto_java?serverTimezone=UTC";
        String sqlSelectAllPersons = "SELECT * FROM aeroporto_data";
        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "Germaneagle_12");
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                String name = rs.getString("name");
                String iata = rs.getString("iata");
                String cidade = rs.getString("county");
                String estado = rs.getString("state");

                Aeroporto arr = new Aeroporto(latitude,longitude,name,iata,cidade,estado);
                v.add(arr);
                System.out.println("Loaded: " + name + " (" + iata + ") Latitude: " + latitude + " Longitude: " + longitude);
            }
            return v;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
