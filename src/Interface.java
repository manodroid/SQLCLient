import javax.swing.*;

public class Interface extends JFrame {
    final String PROGRAM_TITLE = "SQLite Viewer";
    final String BTN_ENTER = "Open";
    final String BTN_EXEC = "Execute";
    DataBase db;

    public Interface() {
        setTitle(PROGRAM_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 800);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        JTextField fileNameTF = new JTextField();
        fileNameTF.setName("FileNameTextField");
        fileNameTF.setBounds(20, 20, getWidth() - 130, 30);
        add(fileNameTF);

        JButton openFileBtn = new JButton(BTN_ENTER);
        openFileBtn.setName("OpenFileButton");
        openFileBtn.setBounds(getWidth() - 100, 20, 70, 30);
        add(openFileBtn);

        JComboBox<String> tablesComboBox = new JComboBox<>();
        tablesComboBox.setName("TablesComboBox");
        tablesComboBox.setBounds(20, 70, getWidth() - 50, 30);
        add(tablesComboBox);

        JTextArea queryTxtArea = new JTextArea();
        queryTxtArea.setName("QueryTextArea");
        queryTxtArea.setBounds(20, 120, getWidth() - 170, 60);
        add(queryTxtArea);

        JButton executeQueryBtn = new JButton(BTN_EXEC);
        executeQueryBtn.setName("ExecuteQueryButton");
        executeQueryBtn.setBounds(getWidth() - 140, 120, 110, 30);
        add(executeQueryBtn);

        openFileBtn.addActionListener(e -> {
            String fileName = fileNameTF.getText().trim()+".db";
            if (!fileName.equals("")) {
                // add joptionpanes and improve the overall gui
                db = new DataBase(fileName);
                tablesComboBox.removeAllItems();
                db.getTableNames().forEach(tablesComboBox::addItem);
                queryTxtArea.removeAll();
            }
        });

        tablesComboBox.addActionListener(e -> {
            String table = (String) tablesComboBox.getSelectedItem();
            queryTxtArea.removeAll();
            queryTxtArea.setText("SELECT * FROM "+table+";");
        });

        setVisible(true);
        //improve layout, foucus/enable, prompts and more hints
        //improve variable names and check for errors when wrong input is given
    }
}
