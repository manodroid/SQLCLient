import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import javax.swing.tree.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Interface extends JFrame {
    
    DataBase db;

    
    public Interface() {

        //main frame
        setTitle("SQLite Client");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // menu Database
        JMenuBar options = new JMenuBar();
        JMenuItem openDB = new JMenuItem("Open DB");
        JMenuItem exportExcel = new JMenuItem("Export to Excel");
        JMenuItem execute = new JMenuItem("Execute Query");
        options.add(openDB);
        options.add(execute);
        options.add(exportExcel);
        setJMenuBar(options);
        // text area for writing queries
        JTextArea queryTA = new JTextArea("Write your queries here");
        // tables tree
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        JTree tables = new JTree(treeModel);
        tables.setEditable(true);
        tables.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // upper container
        JSplitPane upperC = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tables, queryTA);
        upperC.setResizeWeight(0.5);
        upperC.setDividerLocation(0.80);
        // SQL table
        JTable sqlTable = new JTable();
        sqlTable.setFillsViewportHeight(true);
        JScrollPane scrollC = new JScrollPane(sqlTable);
        // lower container
        JSplitPane content = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperC, scrollC);
        content.setResizeWeight(0.5);
        content.setDividerLocation(0.70);
        setContentPane(content);


        // Action Listeners
        openDB.addActionListener(e -> {
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
            db = new DataBase(fileChooser.getSelectedFile(), content);
            String[] tableNames = db.getTables().toArray(new String[0]);
            for (String table : tableNames){
                rootNode.add(new DefaultMutableTreeNode(table));
                treeModel.reload(rootNode);
            }
            queryTA.removeAll();
        });

        // execute queries from the text area
        execute.addActionListener(e ->
                sqlTable.setModel(db.executeQuery(queryTA.getText().trim()))
        );


        // export the table as an excel file
        exportExcel.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog(null, "Enter the name of the file: ",
                    "Save Table As...", JOptionPane.PLAIN_MESSAGE);
            try{
                TableModel model = sqlTable.getModel();
                FileWriter excel = new FileWriter(System.getProperty("user.home") +
                        System.getProperty("file.separator") + filename + ".tsv");
                for(int i = 0; i < model.getColumnCount(); i++){
                    excel.write(model.getColumnName(i) + "\t");
                }

                excel.write("\n");

                for(int i=0; i< model.getRowCount(); i++) {
                    for(int j=0; j < model.getColumnCount(); j++) {
                        excel.write(model.getValueAt(i,j).toString()+"\t");
                    }
                    excel.write("\n");
                }

                excel.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        tables.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tables.getLastSelectedPathComponent();
            String selectedTable = selectedNode.toString();
            queryTA.setText(String.format(db.BASIC_QUERY, selectedTable));
            sqlTable.setModel(db.executeQuery("SELECT * FROM " + selectedTable + ";"));
        });

        setVisible(true);
    }

}
