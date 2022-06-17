import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class Interface extends JFrame {
    
    DataBase db;

    
    public Interface() {

        // look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        //main frame
        setTitle("SQLite Client");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // menus
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
        upperC.setVisible(true);
        upperC.setResizeWeight(0.2);

        // SQL table
        JTable sqlTable = new JTable();
        sqlTable.setFillsViewportHeight(true);
        JScrollPane scrollC = new JScrollPane(sqlTable);

        // lower container
        JSplitPane content = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperC, scrollC);
        content.setVisible(true);
        content.setResizeWeight(0.7);
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


        // syntax highlighter
        HashSet<String> keyWords = new HashSet<>(List.of("ABORT", "ACTION", "ADD", "AFTER", "ALL", "ALTER", "ALWAYS", "ANALYZE", "AND", "AS", "ASC",
                "ATTACH", "AUTOINCREMENT", "BEFORE", "BEGIN", "BETWEEN", "BY", "CASCADE", "CASE", "CAST", "CHECK", "COLLATE",
                "COLUMN", "COMMIT", "CONFLICT", "CONSTRAINT", "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME",
                "CURRENT_TIMESTAMP", "DATABASE", "DEFAULT", "DELETE", "DESC", "DETACH", "DISTINCT",
                "DO", "DROP", "EACH", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUDE", "EXCLUSIVE", "EXISTS", "EXPLAIN", "FAIL"
                , "FILTER", "FIRST", "FOLLOWING", "FOR", "FOREIGN", "FROM", "FULL", "GENERATED", "GLOB", "GROUP", "GROUPS",
                "HAVING", "IF", "IGNORE", "IMMEDIATE", "IN", "INDEX", "INDEXED", "INITIALLY", "INNER", "INSERT", "INSTEAD",
                "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "KEY", "LAST", "LEFT", "LIKE", "LIMIT", "MATCH", "MATERIALIZED",
                "NATURAL", "NO", "NOT", "NOTHING", "NOTNULL", "NULL", "NULLS", "OF", "OFFSET", "ON", "OR", "ORDER", "OTHERS",
                "OUTER", "OVER", "PARTITION", "PLAN", "PRAGMA", "PRECEDING", "PRIMARY", "QUERY", "RAISE", "RANGE", "RECURSIVE",
                "REFERENCES", "REGEXP", "REINDEX", "RELEASE", "RENAME", "REPLACE", "RESTRICT", "RETURNING", "RIGHT", "ROLLBACK",
                "ROW", "ROWS", "SAVEPOINT", "SELECT", "SET", "TABLE", "TEMP", "TEMPORARY", "THEN", "TIES", "TO", "TRANSACTION",
                "TRIGGER", "UNBOUNDED", "UNION", "UNIQUE", "UPDATE", "USING", "VACUUM", "VALUES", "VIEW", "VIRTUAL", "WHEN",
                "WHERE", "WINDOW", "WITH", "WITHOUT"));
        queryTA.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = queryTA.getText().trim();
                for (String subString : text.split("\s")){
                    if (keyWords.contains(subString)){
                        int index = text.indexOf(subString);
                        if (index >= 0) {
                            Highlighter lighter = queryTA.getHighlighter();
                            try {
                                lighter.addHighlight(index, index + subString.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
                            } catch (BadLocationException ex) {
                                throw new RuntimeException(ex);
                            }
                            index = text.indexOf(subString, index+1);
                        }
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        setVisible(true);
        }
}
