import java.sql.*;
import java.util.ArrayList;

public class DataBase {
    private final String URL;
    Connection conn = null;
    String workingTable;
    public DataBase(String fileName){
        this.URL = "jdbc:sqlite:"+fileName;
        this.workingTable = fileName;
    }

    private Connection connect(){
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

    public ResultSet executeQuery(String query){
        conn = connect();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            // insert doesnt work
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }
}
