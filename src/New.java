import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class New extends JFrame {
    DataBase db;

    public New() {
        //main frame
        setTitle("SQLite Client");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setLayout(null); //add a gridbag layout or smth

        // upper container => buttons / tables / queryArea
        JPanel upperC = new JPanel(); //upper container
        JComboBox<String> tables = new JComboBox<>();
        JButton exe = new JButton("Execute");
        JButton open = new JButton("Open");
        JTextArea queryTA = new JTextArea("Write your queries here");

        upperC.add(tables);
        upperC.add(open);
        upperC.add(exe);
        upperC.add(queryTA); //add a scrollpane here
        tables.setVisible(false);
        // lower container => SQL table
        JTable sqlTable = new JTable();
        JScrollPane scrollC = new JScrollPane(sqlTable);


        JSplitPane content = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperC, sqlTable);
        setContentPane(content);

        // action listeners
        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select database to open");
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".db");
                }

                @Override
                public String getDescription() {
                    return null;
                }
            });
            fileChooser.showOpenDialog(null);
            tables.setVisible(true);
            db = new DataBase(fileChooser.getSelectedFile());
            tables.removeAllItems();
            db.getTableNames().forEach(tables::addItem);
            queryTA.removeAll();

            //initialize table with select * from table by default
        });

        exe.addActionListener(e -> {
            db.executeQuery(queryTA.getText().trim());
            // implement a proper execute query method
            // populate and update the table
        });

        setVisible(true);
    }
}
