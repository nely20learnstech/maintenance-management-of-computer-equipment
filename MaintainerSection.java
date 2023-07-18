import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MaintainerSection extends JPanel{
    JTable maintainerTable;

    private JTextField maintainerNumberTextField;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField phoneNumberTextField;
    private JTextField emailTextField;
    private JTextField salaryTextField;

    private JButton insertButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton cancelButton;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    final String DB_URL = "jdbc:mysql://localhost/maintenance_management?serverTimezone=UTC";
    final String USERNAME = "your_username";
    final String PASSWORD = "your_password";

    // Search TextField
    private JTextField searchTextField;
    // Table row sorter
    private TableRowSorter<DefaultTableModel> sorter;

    // Create a custom cell renderer for the table columns
    DefaultTableCellRenderer renderer;


    MaintainerSection()
    {
        this.setSize(1200, 800);
        this.setVisible(true);

        JLabel maintainerSectionLabel = new JLabel();
        maintainerSectionLabel.setText("Intervenants");

        // Set label icon

        // Set bounds
        maintainerSectionLabel.setBounds(10,0,900,100);
        maintainerSectionLabel.setForeground(Color.decode("#002456"));
        maintainerSectionLabel.setFont(new Font("Times New Roman", Font.PLAIN,70));

        // Search Field
        searchTextField = new JTextField();

        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(searchTextField.getText().trim());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(searchTextField.getText().trim());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(searchTextField.getText().trim());
            }

            public void search(String query) {
                if (query.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter( "(?i)" + query, 0, 1, 2, 3, 4, 5));
                }
            }
        });

        // Table Section
        JPanel tablePanel = new JPanel();
        tablePanel.setBounds(560, 150, 700, 530);


        tablePanel.setLayout(new BorderLayout(0, 20));
        JLabel searchLabel = new JLabel("Rechercher ici : ");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchTextField.setPreferredSize(new Dimension(230, 30));
        searchPanel.add(searchLabel);
        searchPanel.add(searchTextField);

        maintainerTable = new JTable();

        // Center Text Renderer
        maintainerTable.setDefaultRenderer(Object.class, new EquipmentSection.CenterTextRenderer());

        maintainerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Get the selected row index
                int selectedRow = maintainerTable.getSelectedRow();

                // Check if a row is selected
                if (selectedRow != -1) {
                    // Retrieve the data from the selected row for each column
                    Object data1 = maintainerTable.getValueAt(selectedRow, 0);
                    Object data2 = maintainerTable.getValueAt(selectedRow, 1);
                    Object data3 = maintainerTable.getValueAt(selectedRow, 2);
                    Object data4 = maintainerTable.getValueAt(selectedRow, 3);
                    Object data5 = maintainerTable.getValueAt(selectedRow, 4);
                    Object data6 = maintainerTable.getValueAt(selectedRow, 5);

                    // Populate the JTextField with the retrieved data
                    maintainerNumberTextField.setText(data1.toString());
                    firstNameTextField.setText(data2.toString());
                    lastNameTextField.setText(data3.toString());
                    phoneNumberTextField.setText(data4.toString());
                    emailTextField.setText(data5.toString());
                    salaryTextField.setText(data6.toString());
                } else {
                    // No row is selected, clear the JTextField
                    maintainerNumberTextField.setText("");
                    firstNameTextField.setText("");
                    lastNameTextField.setText("");
                    phoneNumberTextField.setText("");
                    emailTextField.setText("");
                    salaryTextField.setText("");

                }
            }
        });


        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(maintainerTable);
        scrollPane.setSize(700, 500);
        JLabel tableTitle = new JLabel("Liste des intervenants");
        tableTitle.setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 20));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(searchPanel,BorderLayout.BEFORE_LINE_BEGINS);
        tablePanel.add(scrollPane, BorderLayout.SOUTH);

        showTableData();

        // Form Section
        JPanel crudSection = new JPanel();
        crudSection.setBounds(10, 150, 450, 500);
        crudSection.setLayout(null);

        JLabel maintainerNumberLabel = new JLabel();
        maintainerNumberLabel.setBounds(5, 5, 200, 30);
        maintainerNumberLabel.setText("Numéro de l'intervenant : ");
        maintainerNumberLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(maintainerNumberLabel);

        maintainerNumberTextField = new JTextField();
        maintainerNumberTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        maintainerNumberTextField.setBounds(200, 5, 200, 30);
        crudSection.add(maintainerNumberTextField);


        JLabel firstNameLabel = new JLabel();
        firstNameLabel.setBounds(5, 115, 150, 30);
        firstNameLabel.setText("Prénom(s) : ");
        firstNameLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(firstNameLabel);

        firstNameTextField = new JTextField();
        firstNameTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        firstNameTextField.setBounds(200, 115, 200, 30);
        crudSection.add(firstNameTextField);

        JLabel lastNameLabel = new JLabel();
        lastNameLabel.setBounds(5, 55, 150, 30);
        lastNameLabel.setText("Nom(s) : ");
        lastNameLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(lastNameLabel);

        lastNameTextField = new JTextField();
        lastNameTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        lastNameTextField.setBounds(200, 55, 200, 30);
        crudSection.add(lastNameTextField);

        JLabel phoneNumberLabel = new JLabel();
        phoneNumberLabel.setBounds(5, 165, 150, 30);
        phoneNumberLabel.setText("Numéro de téléphone : ");
        phoneNumberLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(phoneNumberLabel);

        phoneNumberTextField = new JTextField();
        phoneNumberTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        phoneNumberTextField.setBounds(200, 165, 200, 30);
        crudSection.add(phoneNumberTextField);

        JLabel emailLabel = new JLabel();
        emailLabel.setBounds(5, 215, 150, 30);
        emailLabel.setText("Adresse email : ");
        emailLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(emailLabel);

        emailTextField = new JTextField();
        emailTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        emailTextField.setBounds(200, 215, 200, 30);
        crudSection.add(emailTextField);

        JLabel salaryLabel = new JLabel();
        salaryLabel.setBounds(5, 265, 150, 30);
        salaryLabel.setText("Salaire : ");
        salaryLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(salaryLabel);

        salaryTextField = new JTextField();
        salaryTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        salaryTextField.setBounds(200, 265, 200, 30);
        crudSection.add(salaryTextField);

        // Buttons

        insertButton = new JButton("Ajouter");
        insertButton.setBounds(5, 350, 100, 30);
        insertButton.setBackground(Color.decode("#2ecc71"));

        insertButton.setForeground(Color.WHITE);
        crudSection.add(insertButton);

        updateButton = new JButton("Modifier");
        updateButton.setBounds(115, 350, 100, 30);
        insertButton.setForeground(Color.WHITE);
        updateButton.setBackground(Color.decode("#64b9ee"));
        crudSection.add(updateButton);

        deleteButton = new JButton("Supprimer");
        deleteButton.setBackground(Color.decode("#ff4c58"));
        deleteButton.setBounds(225, 350, 100, 30);
        deleteButton.setForeground(Color.WHITE);
        crudSection.add(deleteButton);

        cancelButton = new JButton("Effacer");
        cancelButton.setBounds(335, 350, 100, 30);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(Color.GRAY);
        crudSection.add(cancelButton);

        this.add(maintainerSectionLabel);
        this.add(tablePanel);
        this.add(crudSection);
        this.setSize(1200, 800);
        // this.setLayout(new FlowLayout());
        this.setLayout(null);
        this.setMinimumSize(new Dimension(1100, 800));
        this.setVisible(true);

//        this.pack();


        // buttons perform actions
        insertButton.addActionListener(e -> {

            if(maintainerNumberTextField.getText().trim().isEmpty() || lastNameTextField.getText().trim().isEmpty() || salaryTextField.getText().trim().isEmpty() || phoneNumberTextField.getText().trim().isEmpty()){

                if(maintainerNumberTextField.getText().trim().isEmpty()){
                    String errorMessage = "Veuillez saisir le numéro de votre intervenant.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);

//                    equipmentErrorLabel.setForeground(Color.red);
//                    equipmentErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    equipmentErrorLabel.setText(errorMessage);
//                    crudSection.add(equipmentErrorLabel);

                }

                if (lastNameTextField.getText().trim().isEmpty()) {
                    String errorMessage = "Veuillez saisir le nom de votre intervenant.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
//                    descriptionErrorLabel.setForeground(Color.red);
//                    descriptionErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    descriptionErrorLabel.setText(errorMessage);
//                    crudSection.add(descriptionErrorLabel);


                }


                if (salaryTextField.getText().trim().isEmpty()) {
                    String errorMessage = "*Veuillez saisir le salaire de votre intervenant.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
//                    characteristicsErrorLabel.setForeground(Color.red);
//                    characteristicsErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    characteristicsErrorLabel.setText(errorMessage);
//                    crudSection.add(characteristicsErrorLabel);

                }

                if (phoneNumberLabel.getText().trim().isEmpty()) {
                    String errorMessage = "*Veuillez saisir le numéro de téléphone de votre intervenant.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
//                    characteristicsErrorLabel.setForeground(Color.red);
//                    characteristicsErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    characteristicsErrorLabel.setText(errorMessage);
//                    crudSection.add(characteristicsErrorLabel);

                }

            }
            else {


                try {
                    String sql = "INSERT INTO maintainer (maintainer_number, maintainer_firstname, maintainer_lastname, phone_number, email_address, salary) VALUES (?,?,?,?,?,?)";
                    connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                    //                    Statement stat = connection.createStatement();
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, maintainerNumberTextField.getText().trim());
                    preparedStatement.setString(2, firstNameTextField.getText().trim());
                    preparedStatement.setString(3, lastNameTextField.getText().trim());
                    preparedStatement.setString(4, phoneNumberTextField.getText().trim());
                    preparedStatement.setString(5, emailTextField.getText().trim());
                    preparedStatement.setString(6, salaryTextField.getText().trim());
                    preparedStatement.executeUpdate();
                    //                JOptionPane.showMessageDialog(null, "Inserted successfully");
                } catch (SQLException exception) {
                    if (exception.getErrorCode() == 1062) {
                        // Extract the necessary information to create a readable error message
                        String constraintName = exception.getMessage(); // Constraint name, e.g., "PRIMARY"
                        String columnName = ""; // Column name, e.g., "id"
                        // You can extract the column name from the SQLException message or retrieve it from the table metadata

                        // Create a readable error message
                        //                    String errorMessage = "Primary key constraint violation: Duplicate value for column " + columnName;

                        String errorMessage = "Ce numéro appartient déjà à un intervenant. \n Veuillez saisir un autre numéro.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Duplication de numéro", JOptionPane.WARNING_MESSAGE);
                        // Display the error message to the user (e.g., show a dialog box or set the text of a label)
                        System.out.println(errorMessage);
                    } else {
                        JOptionPane.showMessageDialog(null, exception);
                    }
                }
                showTableData();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(maintainerNumberTextField.getText().trim().isEmpty()){
                    String errorMessage = "Veuillez saisir le numéro de votre intervenant.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);

//                    equipmentErrorLabel.setForeground(Color.red);
//                    equipmentErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    equipmentErrorLabel.setText(errorMessage);
//                    crudSection.add(equipmentErrorLabel);

                }
                else{
                    int choice = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer?",
                            "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                    // Handle the user's response
                    if(choice == JOptionPane.YES_OPTION){
                        // User clicked "Yes" - proceed with deletion
                        deleteRecord();
                    }
                    showTableData();
                }


            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(maintainerNumberTextField.getText().trim().isEmpty() || lastNameTextField.getText().trim().isEmpty() || salaryTextField.getText().trim().isEmpty() || phoneNumberTextField.getText().trim().isEmpty()){

                    if(maintainerNumberTextField.getText().trim().isEmpty()){
                        String errorMessage = "Veuillez saisir le numéro de votre intervenant.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);

//                    equipmentErrorLabel.setForeground(Color.red);
//                    equipmentErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    equipmentErrorLabel.setText(errorMessage);
//                    crudSection.add(equipmentErrorLabel);

                    }

                    if (lastNameTextField.getText().trim().isEmpty()) {
                        String errorMessage = "Veuillez saisir le nom de votre intervenant.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
//                    descriptionErrorLabel.setForeground(Color.red);
//                    descriptionErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    descriptionErrorLabel.setText(errorMessage);
//                    crudSection.add(descriptionErrorLabel);


                    }


                    if (salaryTextField.getText().trim().isEmpty()) {
                        String errorMessage = "*Veuillez saisir le salaire de votre intervenant.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
//                    characteristicsErrorLabel.setForeground(Color.red);
//                    characteristicsErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    characteristicsErrorLabel.setText(errorMessage);
//                    crudSection.add(characteristicsErrorLabel);

                    }

                    if (phoneNumberLabel.getText().trim().isEmpty()) {
                        String errorMessage = "*Veuillez saisir le numéro de téléphone de votre intervenant.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
//                    characteristicsErrorLabel.setForeground(Color.red);
//                    characteristicsErrorLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//                    characteristicsErrorLabel.setText(errorMessage);
//                    crudSection.add(characteristicsErrorLabel);

                    }

                }
                else {

                    int choice = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment modifier?",
                            "Confirmation de modification", JOptionPane.YES_NO_OPTION);

                    // Handle the user's response
                    if (choice == JOptionPane.YES_OPTION) {
                        // User clicked "Yes" - proceed with deletion
                        updateRecord();
                    }
                    showTableData();
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTextField();
            }
        });

    }

    public void clearTextField(){
        maintainerNumberTextField.setText("");
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        phoneNumberTextField.setText("");
        emailTextField.setText("");
        salaryTextField.setText("");
    }

    public void deleteRecord()
    {
        try{
            String sql = "DELETE FROM maintainer WHERE maintainer_number = ?";
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,maintainerNumberTextField.getText());
            preparedStatement.executeUpdate();
//            JOptionPane.showMessageDialog(null, "Deleted successfully");
        }
        catch (SQLException | HeadlessException exception){
            JOptionPane.showMessageDialog(null, exception, "Try again",JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateRecord(){
        try{
            String sql = "UPDATE maintainer SET maintainer_firstname=?, maintainer_lastname=? , phone_number=?, email_address=?, salary=? WHERE maintainer_number=? ";
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
//                    Statement stat = connection.createStatement();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(6, maintainerNumberTextField.getText().trim());
            preparedStatement.setString(1,firstNameTextField.getText().trim());
            preparedStatement.setString(2,lastNameTextField.getText().trim());
            preparedStatement.setString(3,phoneNumberTextField.getText().trim());
            preparedStatement.setString(4,emailTextField.getText().trim());
            preparedStatement.setString(5,salaryTextField.getText().trim());
            preparedStatement.executeUpdate();
//            JOptionPane.showMessageDialog(null, "Updated successfully");
        }
        catch (SQLException exception){
            JOptionPane.showMessageDialog(null, exception);
        }
    }

    public void showTableData(){

        // Create a custom header renderer
        maintainerTable.getTableHeader().setBackground(Color.decode("#c9e4fe"));
        maintainerTable.getTableHeader().setForeground(Color.decode("#002456"));
        maintainerTable.getTableHeader().setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 16));
        maintainerTable.setFont(new Font("Times New Roman", Font.PLAIN, 15));

        // Default table cell renderer
        renderer =  new DefaultTableCellRenderer();
        renderer.setFont(new Font("Times New Roman", Font.BOLD, 16)); // Set the desired font size

        maintainerTable.setRowHeight(40);
        // Disable cell editing
        maintainerTable.setDefaultEditor(Object.class, null);

        try{
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT maintainer_number as 'N° Intervenant' , maintainer_lastname as 'Nom(s)' , maintainer_firstname as 'Prénom(s)',  phone_number as 'Numéro de téléphone', email_address as 'Adresse email', salary as 'Salaire' FROM maintainer ORDER BY maintainer_number DESC";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            // Get the ResultSet metadata
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();


            // Create the table model with column names
            DefaultTableModel tableModel = new DefaultTableModel();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                tableModel.addColumn(metaData.getColumnLabel(columnIndex));
            }

            // Populate the table model with data from the ResultSet
            while(resultSet.next()){
                Object[] rowData = new Object[columnCount];
                for(int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                    rowData[columnIndex - 1] = resultSet.getObject(columnIndex);
                }
                tableModel.addRow(rowData);

                // Set the table model for the JTable
                maintainerTable.setModel(tableModel);
                sorter = new TableRowSorter<>(tableModel);
                maintainerTable.setRowSorter(sorter);
            }
            // table1.setModel(DbUtils.resulSetToTableModel(resultSet));
        }
        catch (Exception exception){
            JOptionPane.showMessageDialog(null, exception);
        }
    }


    static class CenterTextRenderer extends DefaultTableCellRenderer{
        public CenterTextRenderer(){
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
}





