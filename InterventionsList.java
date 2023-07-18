import com.toedter.calendar.JDateChooser;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class InterventionsList extends JPanel {
    private final JTextField searchTextField;

    //    private final JTextField searchDate;
    private final DefaultTableModel tableModel;
    private final JLabel totalValueLabel;

    private final String DB_URL = "jdbc:mysql://localhost/maintenance_management?serverTimezone=UTC";
    private final String USERNAME = "root";
    private final String PASSWORD = "/*roasted*/9-K";

    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private JComboBox<String> comboBox;
    private JTextField monthTextField;
    private JTextField yearTextField;
    private JDateChooser firstDate;
    private JDateChooser secondDate;

    JLabel maintainerNumberLabel;
    JLabel lastNameLabel;
    JLabel firstNameLabel;
    JLabel salaryLabel;
    JLabel dateLabel;

    String selectedOption;

    // Table panel and table
    JPanel tablePanel;
    JTable prestationTable;


    public InterventionsList() {
        setSize(1200, 800);
        setLayout(null);

        // Header label
        String prestationTitle = "Listage des interventions effectuées par un intervenant";
        JLabel prestationLabel = new JLabel(prestationTitle);
        prestationLabel.setBounds(10, 0, 1200, 70);
        prestationLabel.setForeground(Color.decode("#002456"));
        prestationLabel.setFont(new Font("Times New Roman", Font.PLAIN, 50));
        add(prestationLabel);

        // Search components
        JLabel NumberLabel = new JLabel();
        NumberLabel.setBounds(25, 90, 150, 50);
        NumberLabel.setText("N° Intervenant :");
//        NumberLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        add(NumberLabel);

        searchTextField = new JTextField();
        searchTextField.setBounds(130, 100, 150, 30);
//        searchTextField.setFont(new Font("Times New Roman", Font.PLAIN, 16));

//        JLabel DateLabel = new JLabel();
//        DateLabel.setBounds(300, 90, 150, 50);
//
//        DateLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
//        add(DateLabel);

//        searchDate = new JTextField();
//        searchDate.setBounds(350, 100, 150, 30);
//        searchDate.setFont(new Font("Times New Roman", Font.PLAIN, 16));

        JButton searchButton = new JButton("Rechercher");
        searchButton.setBackground(Color.decode("#64b9ee"));
        searchButton.setForeground(Color.white);
        searchButton.setBounds(780, 100, 120, 30);

        // Create the JComboBox with options
        comboBox = new JComboBox<>();
//        comboBox.addItem("Veuillez choisir une option");
        comboBox.addItem("Mois");
        comboBox.addItem("Année");
        comboBox.addItem("Entre deux dates");

        // Create the TextFields
        firstDate = new JDateChooser();
        secondDate = new JDateChooser();
        monthTextField = new JTextField(15);
        yearTextField = new JTextField(15);

        firstDate.setLocale(Locale.FRENCH);
        secondDate.setLocale(Locale.FRENCH);

        comboBox.setBounds(300, 100, 180, 30);
        monthTextField.setBounds(500, 100, 100, 30);
        yearTextField.setBounds(500, 100, 100, 30);
        firstDate.setBounds(500, 100, 125, 30);
        secondDate.setBounds(640, 100, 125, 30);

        monthTextField.setVisible(true);
        yearTextField.setVisible(false);
        firstDate.setVisible(false);
        secondDate.setVisible(false);


        // Add ActionListener to the JComboBox
        comboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectedOption = (String) comboBox.getSelectedItem();
                // Modify the visibility or add/remove components based on the selection
                if (selectedOption.equals("Mois")) {
//                    DateLabel.setText("Mois :");
                    monthTextField.setVisible(true);
                    yearTextField.setVisible(false);
                    firstDate.setVisible(false);
                    secondDate.setVisible(false);
                } else if (selectedOption.equals("Année")) {
//                    DateLabel.setText("Année :");
                    monthTextField.setVisible(false);
                    yearTextField.setVisible(true);
                    firstDate.setVisible(false);
                    secondDate.setVisible(false);
                } else if (selectedOption.equals("Entre deux dates")) {
//                    DateLabel.setText("Entre deux dates :");
                    monthTextField.setVisible(false);
                    yearTextField.setVisible(false);
                    firstDate.setVisible(true);
                    secondDate.setVisible(true);
                } else if (selectedOption == null) {
//                    DateLabel.setText("Entre deux dates :");
                    monthTextField.setVisible(true);
                    yearTextField.setVisible(false);
                    firstDate.setVisible(false);
                    secondDate.setVisible(false);
                } else if (selectedOption.equals("Veuillez choisir une option")) {
//                    DateLabel.setText("Entre deux dates :");
                    monthTextField.setVisible(false);
                    yearTextField.setVisible(false);
                    firstDate.setVisible(false);
                    secondDate.setVisible(false);
                }

                // Refresh the layout to reflect the changes
                revalidate();
                repaint();
            }
        });

        // Dynamic component


        add(comboBox);
        add(firstDate);
        add(secondDate);
        add(monthTextField);
        add(yearTextField);


        add(searchTextField);
//        add(searchDate);
        add(searchButton);

        maintainerNumberLabel = new JLabel();
        maintainerNumberLabel.setBounds(25, 140, 300, 50);
        maintainerNumberLabel.setText("N° Intervenant :");
//        maintainerNumberLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        add(maintainerNumberLabel);

        lastNameLabel = new JLabel();
        lastNameLabel.setBounds(25, 180, 300, 50);
        lastNameLabel.setText("Nom :");
//        lastNameLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        add(lastNameLabel);

        firstNameLabel = new JLabel();
        firstNameLabel.setBounds(25, 220, 300, 50);
        firstNameLabel.setText("Prénoms :");
//        firstNameLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        add(firstNameLabel);


        salaryLabel = new JLabel();
        salaryLabel.setBounds(25, 260, 300, 40);
        salaryLabel.setText("Taux horaire :");
//        salaryLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        add(salaryLabel);

        dateLabel = new JLabel();
        dateLabel.setBounds(25, 300, 500, 40);
        dateLabel.setText("Date :");
//        dateLabel.setFont(new Font("Times New Roman", Font.PLAIN,16));
        add(dateLabel);


        // Table and total label
        tableModel = new DefaultTableModel();
        prestationTable = new JTable(tableModel);

        // Center Text Renderer
        prestationTable.setDefaultRenderer(Object.class, new EquipmentSection.CenterTextRenderer());

        // Custom header
        prestationTable.getTableHeader().setBackground(Color.decode("#c9e4fe"));
        prestationTable.getTableHeader().setForeground(Color.decode("#002456"));
        prestationTable.getTableHeader().setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 16));
        prestationTable.setFont(new Font("Times New Roman", Font.PLAIN, 15));

        // Total value label
        totalValueLabel = new JLabel();
        tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBounds(25, 400, 900, 300);
        // Search button ActionListener
        searchButton.addActionListener(e -> {
            String searchNumber = searchTextField.getText().trim();

            if (searchTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Veuillez saisir le numéro de l'intervenant.", "Champ vide", JOptionPane.ERROR_MESSAGE);
            }

            if (selectedOption == null) {
                String month = monthTextField.getText().trim();
                if (!searchNumber.isEmpty() && !month.isEmpty()) {
                    try {
                        int value = Integer.parseInt(monthTextField.getText().trim());
                        fetchMaintainerData(searchNumber);
                        fetchPrestationMonthData(searchNumber, month);
                        calculateTotalMonthPrestation(searchNumber, month);
                        setDate("Mois : " + value);

                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(null, "Veuillez saisir un mois valide!", "Mois non valide", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else if (month.isEmpty()) {
                    clearTableAndTotal();
                    JOptionPane.showMessageDialog(null, "Veuillez saisir le mois de l'intervention.", "Champ vide", JOptionPane.ERROR_MESSAGE);
                }
//                else {
//                    JOptionPane.showMessageDialog(null, "Veuillez saisir le mois de l'intervention.", "Champ vide", JOptionPane.ERROR_MESSAGE);
//                }
            } else if (selectedOption.equals("Entre deux dates")) {
                java.util.Date second = secondDate.getDate();
                java.util.Date first = firstDate.getDate();

                if (first == null || second == null) {
                    JOptionPane.showMessageDialog(null, "Veuillez saisir correctement les dates de l'intervetion.", "Champ vide", JOptionPane.ERROR_MESSAGE);
                } else if (!searchNumber.isEmpty() || first != null || second != null) {

                    if (first.compareTo(second) > 0) {
                        JOptionPane.showMessageDialog(null, "La première date doit être inférieure à la deuxième.", "La première date supérieure à la deuxième", JOptionPane.ERROR_MESSAGE);
                    } else {
                        fetchMaintainerData(searchNumber);
                        fetchPrestation2DatesData(searchNumber, first, second);
                        calculateTotal2DatesPrestation(searchNumber, first, second);

                        // Create a SimpleDateFormat instance with the desired French date format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("fr"));
                        setDate("Date du " + dateFormat.format(first) + " à " + dateFormat.format(second));
                    }

                } else {
                    clearTableAndTotal();

                }
            } else if (selectedOption.equals("Mois")) {
                String month = monthTextField.getText().trim();
                if (!searchNumber.isEmpty() || !month.isEmpty()) {
                    try {
                        int value = Integer.parseInt(monthTextField.getText().trim());
                        fetchMaintainerData(searchNumber);
                        fetchPrestationMonthData(searchNumber, month);
                        calculateTotalMonthPrestation(searchNumber, month);
                        setDate("Mois : " + value);

                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(null, "Veuillez saisir un mois valide!", "Mois non valide", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    clearTableAndTotal();
                    JOptionPane.showMessageDialog(null, "Veuillez saisir le mois de l'intervetion.", "Champ vide", JOptionPane.ERROR_MESSAGE);
                }
            } else if (selectedOption.equals("Année")) {
                String year = yearTextField.getText().trim();

                if (!searchNumber.isEmpty() && !year.isEmpty()) {

                    try {
                        int value = Integer.parseInt(yearTextField.getText().trim());
                        fetchMaintainerData(searchNumber);
                        fetchPrestationYearData(searchNumber, year);
                        calculateTotalYearPrestation(searchNumber, year);
                        setDate("Année : " + year);

                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(null, "Veuillez saisir une année valide!", "Année non valide", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                } else if (year.isEmpty()) {
                    clearTableAndTotal();
                    JOptionPane.showMessageDialog(null, "Veuillez saisir l'année de l'intervetion.", "Champ vide", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        JButton downloadButton = new JButton("Télécharger en PDF");
        downloadButton.setBackground(Color.decode("#002456"));
        downloadButton.setForeground(Color.white);
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("intervention.pdf");
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Le fichier ne peut pas être téléchargé", "Erreur de téléchargement", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        downloadButton.setBounds(25, 760, 200, 30);

        prestationTable.setRowHeight(40);
        prestationTable.setDefaultEditor(Object.class, null);
        tablePanel.add(totalValueLabel, BorderLayout.SOUTH);
        tablePanel.add(new JScrollPane(prestationTable), BorderLayout.CENTER);

        tablePanel.setVisible(false);
        add(tablePanel, BorderLayout.BEFORE_FIRST_LINE);

        setVisible(true);
    }

    private void fetchMaintainerData(String maintainer_number) {
        clearLabels();
        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT maintainer_number, maintainer_lastname, maintainer_firstname, salary\n" +
                    "FROM maintainer WHERE maintainer_number = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, maintainer_number);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String maintainerNumber = resultSet.getString("maintainer_number");
                String lastName = resultSet.getString("maintainer_lastname");
                String firstName = resultSet.getString("maintainer_firstname");
                String salary = resultSet.getString("salary");

                maintainerNumberLabel.setText("N° Intervenant : " + maintainerNumber);
                lastNameLabel.setText("Nom : " + lastName);
                firstNameLabel.setText("Prénom(s) : " + firstName);
                salaryLabel.setText("Taux horaire : " + salary + " Ar");
            } else {
                JOptionPane.showMessageDialog(null, "Ce numéro n'existe pas. Veuillez saisir un numéro valide", "Ce numéro n'existe pas", JOptionPane.ERROR_MESSAGE);
                tablePanel.setVisible(false);
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        } finally {
            closeResultSet();
            closePreparedStatement();
        }

    }


    private void fetchPrestation2DatesData(String maintainer_number, java.util.Date firstDate, java.util.Date secondDate) {
        clearTable();

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT " +
                    "equipment.equipment_number AS 'N° Matériel', designation AS Désignation, duration AS 'Nombre d\\'heures', salary * duration AS Montant, DATE_FORMAT(intervention_date, '%d/%m/%Y') AS 'Date d\\'intervention'" +
                    " FROM maintenance3" +
                    " JOIN maintainer ON maintainer.maintainer_number = maintenance3.maintainer_number" +
                    " Join equipment ON equipment.equipment_number = maintenance3.equipment_number" +
                    " WHERE maintainer.maintainer_number = ? and intervention_date between ? and ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, maintainer_number);
            java.sql.Date firstInterventionDate = new java.sql.Date(firstDate.getTime());
            preparedStatement.setDate(2, firstInterventionDate);
            java.sql.Date secondInterventionDate = new java.sql.Date(secondDate.getTime());
            preparedStatement.setDate(3, secondInterventionDate);
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create an array to hold the column names
            String[] columnNames = new String[columnCount];


            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                tableModel.addColumn(metaData.getColumnLabel(columnIndex));
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    rowData[columnIndex - 1] = resultSet.getObject(columnIndex);
                }
                tableModel.addRow(rowData);

            }

        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        } finally {
            closeResultSet();
            closePreparedStatement();
        }

        tablePanel.setVisible(true);
    }

    private void fetchPrestationMonthData(String maintainer_number, String date) {
        clearTable();

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT " +
                    "equipment.equipment_number AS 'N° Matériel', designation AS Désignation, duration AS 'Nombre d\\'heures', salary * duration AS Montant, DATE_FORMAT(intervention_date, '%d/%m/%Y') AS 'Date d\\'intervention'" +
                    " FROM maintenance3" +
                    " JOIN maintainer ON maintainer.maintainer_number = maintenance3.maintainer_number" +
                    " Join equipment ON equipment.equipment_number = maintenance3.equipment_number" +
                    " WHERE maintainer.maintainer_number = ? and month(intervention_date) = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, maintainer_number);
            preparedStatement.setString(2, date);
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create an array to hold the column names
            String[] columnNames = new String[columnCount];


            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                tableModel.addColumn(metaData.getColumnLabel(columnIndex));
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    rowData[columnIndex - 1] = resultSet.getObject(columnIndex);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        } finally {
            closeResultSet();
            closePreparedStatement();
        }

        tablePanel.setVisible(true);
    }

    private void fetchPrestationYearData(String maintainer_number, String date) {
        clearTable();


        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT " +
                    "equipment.equipment_number AS 'N° Matériel', designation AS Désignation, duration AS 'Nombre d\\'heures', salary * duration AS Montant, DATE_FORMAT(intervention_date, '%d/%m/%Y') AS 'Date d\\'intervention'" +
                    " FROM maintenance3" +
                    " JOIN maintainer ON maintainer.maintainer_number = maintenance3.maintainer_number" +
                    " Join equipment ON equipment.equipment_number = maintenance3.equipment_number" +
                    " WHERE maintainer.maintainer_number = ? and year(intervention_date) = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, maintainer_number);
            preparedStatement.setString(2, date);
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create an array to hold the column names
//            String[] columnNames = new String[columnCount];


            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                tableModel.addColumn(metaData.getColumnLabel(columnIndex));
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    rowData[columnIndex - 1] = resultSet.getObject(columnIndex);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        } finally {
            closeResultSet();
            closePreparedStatement();
        }

        tablePanel.setVisible(true);
    }

    private void calculateTotalMonthPrestation(String maintainer_number, String date) {
        clearTotalValue();

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT sum(salary*duration) as total_amount\n" +
                    "FROM maintenance3 \n" +
                    "JOIN maintainer ON maintainer.maintainer_number = maintenance3.maintainer_number\n" +
                    "WHERE maintainer.maintainer_number = ? and month(intervention_date) = ?;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, maintainer_number);
            preparedStatement.setString(2, date);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int totalPrestation = resultSet.getInt(1);
                totalValueLabel.setText("MONTANT TOTAL : " + totalPrestation + " Ar");
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        } finally {
            closeResultSet();
            closePreparedStatement();
        }
    }


    private void calculateTotalYearPrestation(String maintainer_number, String date) {
        clearTotalValue();

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT sum(salary*duration) as total_amount\n" +
                    "FROM maintenance3 \n" +
                    "JOIN maintainer ON maintainer.maintainer_number = maintenance3.maintainer_number\n" +
                    "WHERE maintainer.maintainer_number = ? and year(intervention_date) = ?;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, maintainer_number);
            preparedStatement.setString(2, date);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int totalPrestation = resultSet.getInt(1);
                totalValueLabel.setText("MONTANT TOTAL : " + totalPrestation + " Ar");
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        } finally {
            closeResultSet();
            closePreparedStatement();
        }
    }

    private void calculateTotal2DatesPrestation(String maintainer_number, java.util.Date firstDate, java.util.Date secondDate) {
        clearTotalValue();

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT sum(salary*duration) as total_amount\n" +
                    "FROM maintenance3 \n" +
                    "JOIN maintainer ON maintainer.maintainer_number = maintenance3.maintainer_number\n" +
                    "WHERE maintainer.maintainer_number = ? and intervention_date between ? and ?;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, maintainer_number);
            java.sql.Date firstInterventionDate = new java.sql.Date(firstDate.getTime());
            preparedStatement.setDate(2, firstInterventionDate);
            java.sql.Date secondInterventionDate = new java.sql.Date(secondDate.getTime());
            preparedStatement.setDate(3, secondInterventionDate);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double totalPrestation = resultSet.getDouble(1);
                totalValueLabel.setText("MONTANT TOTAL : " + totalPrestation + " Ar");
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        } finally {
            closeResultSet();
            closePreparedStatement();
        }
    }


    private void clearTableAndTotal() {
        clearTable();
        clearTotalValue();
    }

    private void clearTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    }

    private void clearTotalValue() {
        totalValueLabel.setText("");
    }

    private void closeResultSet() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    private void closePreparedStatement() {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }


    private void clearLabels() {
        maintainerNumberLabel.setText("N° Intervenant :");
        lastNameLabel.setText("Nom :");
        firstNameLabel.setText("Prénoms :");
        salaryLabel.setText("Taux horaire :");
        dateLabel.setText("Date :");
    }


    // Set dates, month, year.
    private void setDate(String date) {
        dateLabel.setText(date);
    }

}



