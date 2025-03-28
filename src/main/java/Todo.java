import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Todo {
    private JFrame frame;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskInput;
    private ArrayList<String> tasks = new ArrayList<>();

    public Todo() {
        frame = new JFrame("To-Do List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));

        taskInput = new JTextField();
        taskInput.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton addButton = new JButton("Add Task");
        styleButton(addButton, new Color(76, 175, 80));

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 14));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setForeground(Color.BLACK);
        taskList.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JButton removeButton = new JButton("Remove Selected");
        styleButton(removeButton, new Color(244, 67, 54));
        JButton saveButton = new JButton("Save Tasks");
        styleButton(saveButton, new Color(33, 150, 243));
        JButton loadButton = new JButton("Load Tasks");
        styleButton(loadButton, new Color(255, 152, 0));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(new Color(240, 240, 240));
        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> addTask());
        taskInput.addActionListener(e -> addTask());

        removeButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                taskListModel.remove(selectedIndex);
                tasks.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a task to remove", "No Task Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        saveButton.addActionListener(e -> saveTasks());
        loadButton.addActionListener(e -> loadTasks());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void addTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            taskListModel.addElement(task);
            tasks.add(task);
            taskInput.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Task cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        taskInput.requestFocus();
    }

    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("tasks.txt"))) {
            for (String task : tasks) {
                writer.println(task);
            }
            JOptionPane.showMessageDialog(frame, "Tasks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving tasks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            taskListModel.clear();
            tasks.clear();
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    taskListModel.addElement(line);
                    tasks.add(line);
                }
            }
            JOptionPane.showMessageDialog(frame, "Tasks loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(frame, "No saved tasks found", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading tasks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Todo();
        });
    }
}