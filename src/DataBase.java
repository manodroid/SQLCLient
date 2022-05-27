import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;

public class DataBase {
    private final String URL;

    public DataBase(String fileName){
        this.URL = "jdbc:sqlite:"+fileName;
    }

    private Connection connect(){
        Connection conn = null;
        try{ //add hints and option panes
            conn = DriverManager.getConnection(URL);
        } catch (SQLException se){
            System.out.println(se.getMessage());
        }
        return conn;
    }

    public ArrayList<String> getTableNames() {
        String sqlQuery = "SELECT name FROM sqlite_master WHERE type= 'table' " +
                            "AND name NOT LIKE 'sqlite_%'";
        ArrayList<String> tables = new ArrayList<>();
        try (Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) tables.add(rs.getString("name"));
        } catch (SQLException se){
            System.out.println(se.getMessage());
        }
        return tables;
    }
}
