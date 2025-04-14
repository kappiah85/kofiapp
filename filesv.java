import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class StudentFileApp extends JFrame {
    private static final String FILE_NAME = "students.txt";

    private JTextField idField, nameField;
    private JTextArea outputArea;

    public StudentFileApp() {
        setTitle("Student File Manager");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> insertStudent());
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(e -> deleteStudent());
        inputPanel.add(deleteButton);

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void insertStudent() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                outputArea.append("✗ Name field cannot be empty\n");
                return;
            }

            // Check if ID already exists
            if (studentExists(id)) {
                outputArea.append("✗ Student ID already exists\n");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(id + "," + name);
                writer.newLine();
                outputArea.append("✓ Student " + name + " (ID: " + id + ") added successfully\n");
            }

        } catch (NumberFormatException e) {
            outputArea.append("✗ Invalid ID format. Please enter a number.\n");
        } catch (IOException e) {
            outputArea.append("✗ File error: " + e.getMessage() + "\n");
        }
    }

    private void deleteStudent() {
        try {
            int id = Integer.parseInt(idField.getText());

            File inputFile = new File(FILE_NAME);
            File tempFile = new File("temp_students.txt");

            boolean found = false;

            try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", 2);
                    int currentId = Integer.parseInt(parts[0]);

                    if (currentId != id) {
                        writer.write(line);
                        writer.newLine();
                    } else {
                        found = true;
                    }
                }
            }

            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
            }

            if (found) {
                outputArea.append("✓ Student with ID " + id + " deleted successfully\n");
            } else {
                outputArea.append("✗ No student found with ID " + id + "\n");
            }

        } catch (NumberFormatException e) {
            outputArea.append("✗ Invalid ID format. Please enter a number.\n");
        } catch (IOException e) {
            outputArea.append("✗ File error: " + e.getMessage() + "\n");
        }
    }

    private boolean studentExists(int id) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                int existingId = Integer.parseInt(parts[0]);
                if (existingId == id) return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentFileApp();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
