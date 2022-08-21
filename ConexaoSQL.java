package TrabalhoAeroporto;

import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;


public class ConexaoSQL {

    public ArrayList<Aeroporto> getConexaoMySQL() {
        ArrayList<Aeroporto> v = new ArrayList();

        String connectionUrl = "jdbc:mysql://localhost:3306/aeroporto_java?serverTimezone=UTC";
        String sqlSelectAllPersons = "SELECT * FROM aeroporto_data";
        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "password");
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
