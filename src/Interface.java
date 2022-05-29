import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Interface extends JFrame {
    DataBase db;

    public Interface() {
        setTitle("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 1200);
        GridBagLayout gbLay = new GridBagLayout();
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);


        JTextField fileNameTF = new JTextField("Enter the name of the database");
        fileNameTF.setName("FileNameTextField");
        fileNameTF.setBounds(20, 20, getWidth() - 200, 30);
        add(fileNameTF);


        JTable Table = new JTable();
        // put it inside a scrollbar container
        //add(Table); //make visible or not etc
        //Table.setBounds(20, 200, getWidth()-50, 50);


        JButton openFileBtn = new JButton("Open");
        openFileBtn.setName("OpenFileButton");
        openFileBtn.setBounds(getWidth() - 140, 20, 100, 30);
        add(openFileBtn);

        JComboBox<String> tablesComboBox = new JComboBox<>();
        tablesComboBox.setName("TablesComboBox");
        tablesComboBox.setBounds(20, 70, getWidth() - 200, 30);
        add(tablesComboBox);
        tablesComboBox.setVisible(false);

        JTextArea queryTxtArea = new JTextArea();
        queryTxtArea.setName("QueryTextArea");
        //queryTxtArea.setBounds(20, 120, getWidth() - 170, 60);
       // add(queryTxtArea);

        JButton executeQueryBtn = new JButton("Execute");
        executeQueryBtn.setName("ExecuteQueryButton");
        executeQueryBtn.setBounds(getWidth() - 140, 70, 100, 30);
        add(executeQueryBtn);
        executeQueryBtn.setVisible(false);

        openFileBtn.addActionListener(e -> {
            String fileName = fileNameTF.getText().trim()+".db";
            if (!fileName.isBlank()) {
                // add joptionpanes and improve the overall gui
                db = new DataBase(fileName);
                tablesComboBox.removeAllItems();
                db.getTableNames().forEach(tablesComboBox::addItem);
                queryTxtArea.removeAll();
            }
            executeQueryBtn.setVisible(true);
            tablesComboBox.setVisible(true);
            // execute SELECT * FROM table directly
        });

        tablesComboBox.addActionListener(e -> {
            String table = (String) tablesComboBox.getSelectedItem();

            queryTxtArea.removeAll();
            queryTxtArea.setText("SELECT * FROM "+table+";");
        });

        //button to execute all queries or add keyboard listener with enter
        executeQueryBtn.addActionListener(e -> {
            if (db.conn != null){
                String query = queryTxtArea.getText().trim();
                DefaultTableModel tableModel = new DefaultTableModel();
                try {
                    ResultSet result = db.executeQuery(query);
                    ResultSetMetaData md = result.getMetaData();
                    int columns = md.getColumnCount();
                    for (int i=1; i<columns; i++){
                        tableModel.addColumn(md.getColumnLabel(i));
                    }
                    Object[] row = new Object[columns];
                    while (result.next()) {
                        for (int i=0; i<columns; i++){
                            row[i] = result.getObject(i+1);
                        }
                        tableModel.addRow(row);
                    }
                    Table.setModel(tableModel);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryTxtArea, Table);
        add(mainPane);
        mainPane.setBounds(20, 120, getWidth()-50, getHeight()-160);
        mainPane.setResizeWeight(0.5);
        mainPane.setDividerLocation(mainPane.getWidth()/2);
        setVisible(true);
        setResizable(true);
        //improve layout, foucus/enable, prompts and more hints
        //improve variable names and check for errors when wrong input is given
    }
}
