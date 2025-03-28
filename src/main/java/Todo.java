import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;

public class Todo {
    private JFrame frame;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskInput;
    private ArrayList<String> tasks = new ArrayList<>();
    private final Color PRIMARY_COLOR = new Color(0, 0, 0);
    private final Color SECONDARY_COLOR = new Color(240, 240, 245);
    private final Color ACCENT_COLOR = new Color(255, 87, 34);

    public Todo() {
        initializeUI();
        setupComponents();
        setupEventListeners();
        loadTasks(); // Auto-load tasks on startup
    }

    private void initializeUI() {
        frame = new JFrame("To-Do List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(SECONDARY_COLOR);
        frame.setVisible(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupComponents() {
        // Main panel with card-like appearance
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        mainPanel.setBackground(Color.WHITE);

        // Task input panel
        JPanel inputPanel = createInputPanel();

        // Task list with modern styling
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setForeground(new Color(60, 60, 60));
        taskList.setBackground(Color.WHITE);
        taskList.setFixedCellHeight(40);
        taskList.setCellRenderer(new TaskListRenderer());

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button panel with modern buttons
        JPanel buttonPanel = createButtonPanel();

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(Color.WHITE);

        taskInput = new JTextField();
        taskInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        taskInput.setBackground(Color.WHITE);
        taskInput.setForeground(new Color(60, 60, 60));

        JButton addButton = createStyledButton("Add Task", PRIMARY_COLOR);
        addButton.setPreferredSize(new Dimension(100, 40));

        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        return inputPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton removeButton = createStyledButton("Remove", new Color(244, 67, 54));
        JButton saveButton = createStyledButton("Save", new Color(76, 175, 80));
        JButton clearButton = createStyledButton("Clear All", ACCENT_COLOR);

        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(adjustBrightness(color, -20));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private Color adjustBrightness(Color color, int amount) {
        int r = Math.max(0, Math.min(255, color.getRed() + amount));
        int g = Math.max(0, Math.min(255, color.getGreen() + amount));
        int b = Math.max(0, Math.min(255, color.getBlue() + amount));
        return new Color(r, g, b);
    }

    private void setupEventListeners() {
        // Get the add button from the input panel
        JButton addButton = (JButton) ((JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(0)).getComponent(1);

        addButton.addActionListener(e -> addTask());
        taskInput.addActionListener(e -> addTask());

        // Get buttons from the button panel
        JPanel buttonPanel = (JPanel) ((JPanel) frame.getContentPane().getComponent(0)).getComponent(2);
        JButton removeButton = (JButton) buttonPanel.getComponent(0);
        JButton saveButton = (JButton) buttonPanel.getComponent(1);
        JButton clearButton = (JButton) buttonPanel.getComponent(2);

        removeButton.addActionListener(e -> removeSelectedTask());
        saveButton.addActionListener(e -> saveTasks());
        clearButton.addActionListener(e -> clearAllTasks());
    }

    private void addTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            taskListModel.addElement(task);
            tasks.add(task);
            taskInput.setText("");
        } else {
            showMessage("Task cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        taskInput.requestFocus();
    }

    private void removeSelectedTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            taskListModel.remove(selectedIndex);
            tasks.remove(selectedIndex);
        } else {
            showMessage("Please select a task to remove", "No Task Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAllTasks() {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to clear all tasks?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            taskListModel.clear();
            tasks.clear();
        }
    }

    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("tasks.txt"))) {
            for (String task : tasks) {
                writer.println(task);
            }
            showMessage("Tasks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showMessage("Error saving tasks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTasks() {
        File file = new File("tasks.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            taskListModel.clear();
            tasks.clear();
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    taskListModel.addElement(line);
                    tasks.add(line);
                }
            }
        } catch (IOException ex) {
            showMessage("Error loading tasks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(frame, message, title, messageType);
    }

    // Custom list cell renderer for better task display
    private static class TaskListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setIcon(new ImageIcon("task_icon.png"));
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Todo());
    }
}