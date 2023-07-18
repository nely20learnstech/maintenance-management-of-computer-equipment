import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;


public class MaintenanceScheduleSection extends JPanel{
    JTable maintenanceTable;

    private JTextField maintainerNumberTextField;
    private JTextField equipmentNumberTextField;
    private JTextField interventionDateTextField;
    private JTextField startingTimeTextField;
    private JTextField endingTimeTextField;
    private JTextField durationTextField;
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

    // JDateChooser
    JDateChooser dateChooser;

    MaintenanceScheduleSection()
    {

        this.setSize(1200, 800);
        this.setLayout(null);

        this.setVisible(true);

        JLabel maintainerSectionLabel = new JLabel();
        maintainerSectionLabel.setText("Entretiens");

        // Set label icon

        // Set bounds
        maintainerSectionLabel.setBounds(10,0,900,100);
        maintainerSectionLabel.setForeground(Color.decode("#002456"));
        maintainerSectionLabel.setFont(new Font("Times New Roman", Font.PLAIN,70));


        // Table Section
        JPanel tablePanel = new JPanel();
        tablePanel.setBounds(610, 100, 600, 500);
        tablePanel.setLayout(new BorderLayout(0, 40));
        JLabel tableTitle = new JLabel("Liste des entretiens");
        tableTitle.setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 20));
        maintenanceTable = new JTable();

        // Custom header
        maintenanceTable.getTableHeader().setBackground(Color.decode("#c9e4fe"));
        maintenanceTable.getTableHeader().setForeground(Color.decode("#002456"));
        maintenanceTable.getTableHeader().setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 16));
        maintenanceTable.setFont(new Font("Times New Roman", Font.PLAIN, 15));


        maintenanceTable.setSize(500, 500);
        maintenanceTable.setDefaultEditor(Object.class, null);
        // Center Text Renderer
        maintenanceTable.setDefaultRenderer(Object.class, new EquipmentSection.CenterTextRenderer());
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);


        maintenanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Get the selected row index
                int selectedRow = maintenanceTable.getSelectedRow();

                // Check if a row is selected
                if (selectedRow != -1) {
                    // Retrieve the data from the selected row for each column
                    Object data1 = maintenanceTable.getValueAt(selectedRow, 0);
                    Object data2 = maintenanceTable.getValueAt(selectedRow, 1);
                    Object data3 = maintenanceTable.getValueAt(selectedRow, 2);
                    Object data4 = maintenanceTable.getValueAt(selectedRow, 3);


                    // Populate the JTextField with the retrieved data
                    maintainerNumberTextField.setText(data1.toString());
                    equipmentNumberTextField.setText(data2.toString());

                    if (data3 instanceof String) {
                        String dateString = (String) data3;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Replace with your desired date format
                        try {
                            java.util.Date dateFromDatabase = dateFormat.parse(dateString);
                            dateChooser.setDate(dateFromDatabase);
                        } catch (ParseException exception) {
                            // Handle the case where the date string cannot be parsed
                            exception.printStackTrace();
                        }
                    }
                    durationTextField.setText(data4.toString());

                } else {
                    // No row is selected, clear the JTextField
                    maintainerNumberTextField.setText("");
                    equipmentNumberTextField.setText("");
                    dateChooser.setDate(Calendar.getInstance().getTime());
                    durationTextField.setText("");

                }
            }
        });

        showTableData();


        // Form Section
        JPanel crudSection = new JPanel();
        crudSection.setBounds(10, 150, 500, 500);
        crudSection.setLayout(null);

        JLabel maintainerNumberLabel = new JLabel();
        maintainerNumberLabel.setBounds(5, 5, 200, 50);
        maintainerNumberLabel.setText("Numéro de l'intervenant : ");
        maintainerNumberLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(maintainerNumberLabel);

        maintainerNumberTextField = new JTextField();
        maintainerNumberTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        maintainerNumberTextField.setBounds(185, 15, 200, 30);
        crudSection.add(maintainerNumberTextField);


        JLabel equipmentNumberLabel = new JLabel();
        equipmentNumberLabel.setBounds(5, 55, 200, 50);
        equipmentNumberLabel.setText("Numéro du matériel : ");
        equipmentNumberLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(equipmentNumberLabel);

        equipmentNumberTextField = new JTextField();
        equipmentNumberTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        equipmentNumberTextField.setBounds(185, 70, 200, 30);
        crudSection.add(equipmentNumberTextField);

        JLabel interventionDateLabel = new JLabel();
        interventionDateLabel.setBounds(5, 110, 200, 50);
        interventionDateLabel.setText(" Date d'intervention : ");
        interventionDateLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(interventionDateLabel);

        dateChooser = new JDateChooser();
        dateChooser.setLocale(Locale.FRENCH);
        dateChooser.setPreferredSize(new Dimension(120, 25));
        dateChooser.setFont(new Font("Times New Roman", Font.PLAIN,16));
        dateChooser.setBounds(185, 125, 200, 30);
        dateChooser.setDate(Calendar.getInstance().getTime());
        crudSection.add(dateChooser);


        JLabel durationLabel = new JLabel();
        durationLabel.setBounds(5, 175, 200, 50);
        durationLabel.setText("Durée : ");
        durationLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        crudSection.add(durationLabel);

        durationTextField = new JTextField();
        durationTextField.setFont(new Font("Times New Roman", Font.PLAIN,16));
        durationTextField.setBounds(185, 180, 200, 30);
        crudSection.add(durationTextField);

        // Buttons

        insertButton = new JButton("Ajouter");
        insertButton.setBounds(5, 280, 100, 30);
        insertButton.setBackground(Color.decode("#2ecc71"));
        insertButton.setForeground(Color.WHITE);
        crudSection.add(insertButton);

        updateButton = new JButton("Modifier");
        updateButton.setBounds(115, 280, 100, 30);
        updateButton.setForeground(Color.WHITE);
        updateButton.setBackground(Color.decode("#64b9ee"));
        crudSection.add(updateButton);

        deleteButton = new JButton("Supprimer");
        deleteButton.setBounds(225, 280, 100, 30);
        deleteButton.setBackground(Color.decode("#ff4c58"));
        deleteButton.setForeground(Color.WHITE);
        crudSection.add(deleteButton);

        cancelButton = new JButton("Effacer");
        cancelButton.setBounds(335, 280, 100, 30);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(Color.GRAY);
        crudSection.add(cancelButton);

        this.add(maintainerSectionLabel);
        this.add(tablePanel);
        this.add(crudSection);
        this.setSize(1200, 800);
        this.setLayout(null);
        this.setMinimumSize(new Dimension(1100, 800));
        this.setVisible(true);


        // buttons perform actions
        insertButton.addActionListener(e -> {

            if(equipmentNumberTextField.getText().trim().isEmpty() || maintainerNumberTextField.getText().trim().isEmpty()|| dateChooser==null || durationTextField.getText().trim().isEmpty()){

                if(maintainerNumberTextField.getText().trim().isEmpty()){
                    String errorMessage = "Veuillez saisir le numéro de votre intervenant.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                }

                if (equipmentNumberTextField.getText().trim().isEmpty()) {
                    String errorMessage = "Veuillez saisir un numéro pour votre matériel.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);

                }

                if(dateChooser==null || dateChooser.toString().isEmpty()){
                    String errorMessage = "Veuillez saisir une date.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                }

                if (durationTextField.getText().trim().isEmpty()) {
                    String errorMessage = "Veuillez saisir la durée l'entretien.";
                    JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);

                }

            }
            else {
                try{
                    String sql = "INSERT INTO maintenance3 (maintainer_number, equipment_number, intervention_date, duration) VALUES (?,?,?,?)";
                    connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1,maintainerNumberTextField.getText().trim());
                    preparedStatement.setString(2,equipmentNumberTextField.getText().trim());

                    java.util.Date selectedDate = dateChooser.getDate();
                    // Convert java.util.Date to java.sql.Date
                    java.sql.Date interventionDate = new java.sql.Date(selectedDate.getTime());
                    preparedStatement.setDate(3, interventionDate);

                    try {
                        int value = Integer.parseInt(durationTextField.getText().trim());
                        preparedStatement.setString(4,durationTextField.getText().trim());

                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(null, "Veuillez saisir une durée valide!", "Durée non valide", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    preparedStatement.executeUpdate();

                }
                catch (SQLException exception){

                    if (exception.getErrorCode() == 1452){
                        String errorMessage = "Ce numéro n'appartient pas à aucun intervenant ou matériel. \n Veuillez saisir un numéro qui existe.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Insertion de numéro qui n'existe pas", JOptionPane.ERROR_MESSAGE);
                        // Display the error message to the user (e.g., show a dialog box or set the text of a label)
                        System.out.println(errorMessage);
                    }
                    else
                        JOptionPane.showMessageDialog(null, exception);
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
                }
                else {
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

                if(equipmentNumberTextField.getText().trim().isEmpty() || maintainerNumberTextField.getText().trim().isEmpty()|| dateChooser==null || durationTextField.getText().trim().isEmpty()){

                    if(dateChooser==null){
                        String errorMessage = "Veuillez saisir une date.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                    }

                    if(maintainerNumberTextField.getText().trim().isEmpty()){
                        String errorMessage = "Veuillez saisir le numéro de votre intervenant.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                    }

                    if (equipmentNumberTextField.getText().trim().isEmpty()) {
                        String errorMessage = "Veuillez saisir un numéro pour votre matériel.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                    }


                    if (durationTextField.getText().trim().isEmpty()) {
                        String errorMessage = "Veuillez saisir la durée l'entretien.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Champ vide", JOptionPane.ERROR_MESSAGE);
                    }

                }

                else {
                    int choice = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment modifier?",
                            "Confirmation de modification", JOptionPane.YES_NO_OPTION);

                    // Handle the user's response
                    if (choice == JOptionPane.YES_OPTION) {
                        // User clicked "Yes" - proceed with edition
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


    public void deleteRecord(){
        try{
            String sql = "DELETE FROM maintenance3 WHERE maintainer_number = ? AND equipment_number = ?";
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,maintainerNumberTextField.getText().trim());
            preparedStatement.setString(2,equipmentNumberTextField.getText().trim());
            preparedStatement.executeUpdate();
            System.out.println("Deleted Successfully");
        }
        catch (SQLException | HeadlessException exception){
            JOptionPane.showMessageDialog(null, exception, "Try again", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void updateRecord(){
        try{
            String sql = "UPDATE maintenance3 SET intervention_date=? , duration=?, maintainer_number=?, equipment_number=? WHERE maintainer_number=? AND equipment_number=?";
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            preparedStatement = connection.prepareStatement(sql);
            java.util.Date selectedDate = dateChooser.getDate();
            // Convert java.util.Date to java.sql.Date
            java.sql.Date interventionDate = new java.sql.Date(selectedDate.getTime());
            preparedStatement.setDate(1, interventionDate);

            try {
                int value = Integer.parseInt(durationTextField.getText().trim());
                preparedStatement.setString(2,durationTextField.getText());

            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, "Veuillez saisir une durée valide!", "Durée non valide", JOptionPane.ERROR_MESSAGE);
                return;
            }

            preparedStatement.setString(3, maintainerNumberTextField.getText().trim());
            preparedStatement.setString(4,equipmentNumberTextField.getText().trim());
            preparedStatement.setString(5, maintainerNumberTextField.getText().trim());
            preparedStatement.setString(6,equipmentNumberTextField.getText().trim());
            preparedStatement.executeUpdate();
        }
        catch (SQLException | HeadlessException exception){
            JOptionPane.showMessageDialog(null, exception);
            System.out.println(exception);
        }
    }

    public void showTableData(){
        maintenanceTable.setRowHeight(40);
        try{
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT maintainer_number as 'N° Intervenant', equipment_number AS 'N° Matériel', DATE_FORMAT(intervention_date, '%d/%m/%Y') AS 'Date d\\'intervention', duration AS 'Nombre d\\'heures' FROM maintenance3 ORDER BY intervention_date DESC";
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
                maintenanceTable.setModel(tableModel);
            }
        }
        catch (Exception exception){
            JOptionPane.showMessageDialog(null, exception);
        }
    }

    public void clearTextField(){
        maintainerNumberTextField.setText("");
        equipmentNumberTextField.setText("");
        // Set the current date as the default date
        dateChooser.setDate(Calendar.getInstance().getTime());
        durationTextField.setText("");
    }

    static class CenterTextRenderer extends DefaultTableCellRenderer{
        public CenterTextRenderer(){
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
}



