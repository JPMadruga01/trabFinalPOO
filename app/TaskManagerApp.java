package app;

import repository.JsonTaskRepository;
import repository.TaskRepository;
import service.TaskService;
import view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class TaskManagerApp {
    public static void main(String[] args) {
        // Garante que a UI do Swing seja criada na thread correta
        SwingUtilities.invokeLater(() -> {
            try {
                // Visual 
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
            } catch (Exception ignored) {}
            
            // Repositório (persistente em JSON)
            TaskRepository repo = new JsonTaskRepository("tasks.json");

            // Serviço
            TaskService service = new TaskService(repo);

            // Tela Principal
            MainFrame mainFrame = new MainFrame(service);
            mainFrame.setVisible(true);
        });
    }
}
