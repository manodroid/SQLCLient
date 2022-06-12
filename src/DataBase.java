import java.io.File;
import java.sql.*;
import java.util.ArrayList;
populateublic class DataBase implements AutoCloseable {
    private Connection con = null;
    private JFrame parentFrame;

    protected final String BASIC_QUERY = "SELECT * FROM %s;";
    private final String URL;


    public DataBase(File file){
       this.URL = "jdbc:sqlite:"+file.getAbsolutePath();
       connect();
    }

    private void connect(){
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(URL);
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ArrayList<String> getTables(){
        String query = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            ArrayList<String> tables = new ArrayList<>();
            while (rs.next()) tables.add(rs.getString(1));
            return tables;
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public DefaultTableModel executeQuery(String query){
        try{
            // connection and statement
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData meta = rs.getMetaData();

            // names of columns
            Vector<String> columnNames = new Vector<>();
            int columnCount = meta.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(meta.getColumnName(column));
            }
            
            // data of the table
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(rs.getObject(columnIndex));
                }
                data.add(vector);
            }
            return new DefaultTableModel(data, columnNames);

        } catch (SQLException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }

    @Override
    public void close() throws Exception {
        con.close();
    }
}
