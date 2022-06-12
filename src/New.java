import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class Interface extends JFrame {
    DataBase db;
public Interface() {

        //main frame
        setTitle("SQLite Client");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // upper container => buttons / tables / queryArea
        JPanel upperC = new JPanel(); //upper container
        JTextArea queryTA = new JTextArea("Write your queries here");
        upperC.add(queryTA);
        JComboBox<String> tables = new JComboBox<>();

        JButton exe = new JButton("Execute");
        JButton open = new JButton("Open");

        upperC.add(tables);
        upperC.add(open);
        upperC.add(exe);
        tables.setVisible(false);
        // lower container => SQL table
        JTable sqlTable = new JTable();
        JScrollPane scrollC = new JScrollPane(sqlTable);
        JSplitPane content = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperC, scrollC);
        setContentPane(content);


        // Action Listeners
        open.addActionListener(e ->{
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
            db.getTables().forEach(tables::addItem);
            queryTA.removeAll();
        });

        tables.addItemListener(e -> {
            String selectedTable = e.getItem().toString();
            queryTA.setText(String.format(db.BASIC_QUERY, selectedTable));
            sqlTable.setModel( db.executeQuery("SELECT * FROM "+selectedTable+";"));
        });


        setVisible(true);
   
}
