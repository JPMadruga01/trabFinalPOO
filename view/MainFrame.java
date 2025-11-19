package view;

import service.TaskService;
import model.Task;
import model.TaskPriority;
import model.TaskStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainFrame extends JFrame{

    private final TaskService service;
    private final TaskTableModel tableModel;
    private final JTable table;

    private final JTextField txtBusca = new JTextField();
    private final JComboBox<TaskStatus> cbStatus = 
            new JComboBox<>(new TaskStatus[]{null, TaskStatus.PENDENTE, TaskStatus.EM_ANDAMENTO, TaskStatus.CONCLUIDA});
    private final JCheckBox checkAtrasadas = new JCheckBox("Somente atrasadas");
    private final JTextField txtProjeto = new JTextField();

    public MainFrame(TaskService service) {
        super("Gerenciador de Tarefas");
        this.service = service;
        this.tableModel = new TaskTableModel();
        this.table = new JTable(tableModel);
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900,600);
        setLocationRelativeTo(null);
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());

        // Painel de Filtros
        JPanel filters = new JPanel(new GridBagLayout());
        filters.setBorder(new EmptyBorder(8,8,8,8));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int col = 0;
        c.gridx = col++; c.gridy = 0; filters.add(new JLabel("Buscar:"), c);
        c.gridx = col++; c.weightx = 1; filters.add(txtBusca, c); c.weightx = 0;
        c.gridx = col++; filters.add(new JLabel("Status:"), c);
        c.gridx = col++; filters.add(cbStatus, c);
        c.gridx = col++; filters.add(new JLabel("Projeto:"), c);
        c.gridx = col++;
        // torna o campo 'Projeto' maior: mais colunas e permite ocupar espaço extra
        txtProjeto.setColumns(14);
        c.weightx = 0.35; // permite que o campo cresça horizontalmente
        filters.add(txtProjeto, c);
        c.weightx = 0;
        c.gridx = col++; filters.add(checkAtrasadas, c);
        c.gridx = col++; JButton btnAplicar = new JButton("Aplicar"); filters.add(btnAplicar, c); 

        add(filters, BorderLayout.NORTH);

        // Tabela
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Botões inferiores
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnConcluir = new JButton("Marcar concluída");
        JButton btnRefresh = new JButton("Atualizar");

        bottom.add(btnNovo);
        bottom.add(btnEditar);
        bottom.add(btnExcluir);
        bottom.add(btnConcluir);
        bottom.add(btnRefresh);

        add(bottom, BorderLayout.SOUTH);

        // Ações
        btnAplicar.addActionListener(e -> refresh());
        btnRefresh.addActionListener(e -> refresh());

        btnNovo.addActionListener(e -> {
            Task t = showTaskDialog(null);
            if (t != null) {
                try {
                    service.criar(t);
                    refresh();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao criar tarefa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEditar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Task t = tableModel.get(row);
                Task edited = showTaskDialog(t);
                if (edited != null) {
                    try {
                        service.atualizar(edited);
                        refresh();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar tarefa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma tarefa para editar.");
            }
        });

        btnExcluir.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(this, "Selecione ao menos uma tarefa para excluir.");
                return;
            }
            List<UUID> ids = new ArrayList<>();
            for (int r : rows) ids.add(tableModel.get(r).getId());
            int ok = JOptionPane.showConfirmDialog(this, "Confirma exclusão das tarefas selecionadas?", "Excluir", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                service.deletar(ids);
                refresh();
            }
        });

        btnConcluir.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(this, "Selecione ao menos uma tarefa para marcar como concluída.");
                return;
            }
            List<UUID> ids = new ArrayList<>();
            for (int r : rows) ids.add(tableModel.get(r).getId());
            service.marcarConcluida(ids);
            refresh();
        });

        // Duplo clique para editar
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        Task t = tableModel.get(row);
                        Task edited = showTaskDialog(t);
                        if (edited != null) {
                                    try {
                                        service.atualizar(edited);
                                        refresh();
                                    } catch (IllegalArgumentException ex) {
                                        JOptionPane.showMessageDialog(MainFrame.this, "Erro ao atualizar tarefa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                                    }
                        }
                    }
                }
            }
        });
    }

    // Cria a barra de menus da aplicação.
    // 'Arquivo' contém ações principais: Novo (abrir diálogo), Salvar (força persistência)
    // e Sair. 'Ajuda->Sobre' exibe informações da aplicação.
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem newItem = new JMenuItem("Novo");
        newItem.addActionListener(e -> {
            Task t = showTaskDialog(null);
            if (t != null) {
                service.criar(t);
                refresh();
            }
        });

        JMenuItem saveItem = new JMenuItem("Salvar");
        // Ao clicar em Salvar, chamamos persist() do repositório (quando suportado).
        saveItem.addActionListener(e -> {
            try {
                service.getRepository().persist();
                JOptionPane.showMessageDialog(this, "Salvo com sucesso.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
            }
        });

        JMenuItem exitItem = new JMenuItem("Sair");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Ajuda");
        JMenuItem aboutItem = new JMenuItem("Sobre");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Gerenciador de Tarefas\nImplementado com Java Swing.\nMenu: Novo, Salvar, Sair."));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void refresh() {
        String texto = txtBusca.getText();
        TaskStatus status = (TaskStatus) cbStatus.getSelectedItem();
        boolean apenasAtrasadas = checkAtrasadas.isSelected();
        String projeto = txtProjeto.getText();
        List<Task> lista = service.listar(texto, status, apenasAtrasadas, projeto);
        tableModel.setData(lista);
    }

    private Task showTaskDialog(Task t) {
        JTextField txtTitulo = new JTextField();
        JTextArea txtDescricao = new JTextArea(4, 20);
        JTextField txtPrazo = new JTextField(); // yyyy-MM-dd
        JComboBox<TaskPriority> cbPrioridade = new JComboBox<>(TaskPriority.values());
        JComboBox<TaskStatus> cbStatusLocal = new JComboBox<>(TaskStatus.values());
        JTextField txtProjetoLocal = new JTextField();

        if (t != null) {
            txtTitulo.setText(t.getTitulo());
            txtDescricao.setText(t.getDescricao());
            txtPrazo.setText(t.getPrazo() != null ? t.getPrazo().toString() : "");
            cbPrioridade.setSelectedItem(t.getPrioridade());
            cbStatusLocal.setSelectedItem(t.getStatus());
            txtProjetoLocal.setText(t.getProjeto());
        } else {
            cbPrioridade.setSelectedItem(TaskPriority.MEDIA);
            cbStatusLocal.setSelectedItem(TaskStatus.PENDENTE);
        }

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Título:"), c);
        c.gridx = 1; c.weightx = 1; p.add(txtTitulo, c); c.weightx = 0;
        c.gridx = 0; c.gridy++; p.add(new JLabel("Descrição:"), c);
        c.gridx = 1; c.fill = GridBagConstraints.BOTH; p.add(new JScrollPane(txtDescricao), c); c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy++; p.add(new JLabel("Prazo (yyyy-MM-dd):"), c);
        c.gridx = 1; p.add(txtPrazo, c);
        c.gridx = 0; c.gridy++; p.add(new JLabel("Prioridade:"), c);
        c.gridx = 1; p.add(cbPrioridade, c);
        c.gridx = 0; c.gridy++; p.add(new JLabel("Status:"), c);
        c.gridx = 1; p.add(cbStatusLocal, c);
        c.gridx = 0; c.gridy++; p.add(new JLabel("Projeto:"), c);
        c.gridx = 1; p.add(txtProjetoLocal, c);

        // Mantém o diálogo aberto até o usuário fornecer um título válido (ou cancelar).
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, p, t == null ? "Nova tarefa" : "Editar tarefa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return null;

            String titulo = txtTitulo.getText() != null ? txtTitulo.getText().trim() : "";
            if (titulo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Título é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                continue; // mantém o diálogo aberto para correção
            }

            String descricao = txtDescricao.getText();
            LocalDate prazo = null;
            try {
                String s = txtPrazo.getText().trim();
                if (!s.isEmpty()) prazo = LocalDate.parse(s);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-MM-dd.", "Validação", JOptionPane.WARNING_MESSAGE);
                continue; // mantém o diálogo aberto para correção
            }

            TaskPriority prioridade = (TaskPriority) cbPrioridade.getSelectedItem();
            TaskStatus statusLocal = (TaskStatus) cbStatusLocal.getSelectedItem();
            String projetoLocal = txtProjetoLocal.getText();

            if (t == null) {
                return new Task(titulo, descricao, prazo, prioridade, statusLocal, projetoLocal);
            } else {
                t.setTitulo(titulo);
                t.setDescricao(descricao);
                t.setPrazo(prazo);
                t.setPrioridade(prioridade);
                t.setStatus(statusLocal);
                t.setProjeto(projetoLocal);
                return t;
            }
        }
    }

    // Modelo de tabela
    private static class TaskTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Título", "Prazo", "Prioridade", "Status", "Projeto"};
        private List<Task> data = new ArrayList<>();

        public void setData(List<Task> list) {
            this.data = new ArrayList<>(list);
            fireTableDataChanged();
        }

        public Task get(int row) { return data.get(row); }

        @Override
        public int getRowCount() { return data.size(); }

        @Override
        public int getColumnCount() { return cols.length; }

        @Override
        public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Task t = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return t.getId().toString();
                case 1: return t.getTitulo();
                case 2: return t.getPrazo() != null ? t.getPrazo().toString() : "";
                case 3: return t.getPrioridade();
                case 4: return t.getStatus();
                case 5: return t.getProjeto();
                default: return "";
            }
        }
    }
}
