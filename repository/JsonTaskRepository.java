package repository;

import model.Task;
import model.TaskPriority;
import model.TaskStatus;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTaskRepository implements TaskRepository {

    private final Map<UUID, Task> data = new LinkedHashMap<>();
    private final File file;

    public JsonTaskRepository(String filename) {
        this.file = new File(filename);
        if (file.exists()) {
            loadFromFile();
        } else {
            // inicializa com 5 tarefas de exemplo
            save(new Task("Comprar mantimentos", "Comprar frutas, vegetais e leite", java.time.LocalDate.now().plusDays(2), TaskPriority.ALTA, TaskStatus.PENDENTE, "Casa"));
            save(new Task("Reunião com equipe", "Discutir o progresso do projeto", java.time.LocalDate.now().plusDays(1), TaskPriority.MEDIA, TaskStatus.PENDENTE, "Trabalho"));
            save(new Task("Exercício físico", "Caminhada de 30 minutos", java.time.LocalDate.now().plusDays(3), TaskPriority.BAIXA, TaskStatus.PENDENTE, "Saúde"));
            save(new Task("Enviar relatório", "Enviar relatório semanal por email", java.time.LocalDate.now().plusDays(4), TaskPriority.ALTA, TaskStatus.PENDENTE, "Trabalho"));
            save(new Task("Backup", "Fazer backup dos arquivos importantes", java.time.LocalDate.now().plusDays(5), TaskPriority.MEDIA, TaskStatus.PENDENTE, "Casa"));
            persist();
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private void loadFromFile() {
        try {
            String content;
            try (FileInputStream fis = new FileInputStream(file); InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); BufferedReader br = new BufferedReader(isr)) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                content = sb.toString();
            }

            // Extrai objetos JSON simples delimitados por { }
            int idx = 0;
            while (idx < content.length()) {
                int start = content.indexOf('{', idx);
                if (start < 0) break;
                int brace = 0;
                int end = start;
                for (; end < content.length(); end++) {
                    char ch = content.charAt(end);
                    if (ch == '{') brace++;
                    else if (ch == '}') {
                        brace--;
                        if (brace == 0) break;
                    }
                }
                if (end >= content.length()) break;
                String obj = content.substring(start, end + 1);
                Map<String, String> fields = parseObject(obj);

                UUID id = null;
                try { if (fields.containsKey("id") && !fields.get("id").isEmpty()) id = UUID.fromString(fields.get("id")); } catch (Exception ignored) {}
                String titulo = fields.getOrDefault("titulo", "").trim();
                if (titulo.isEmpty()) {
                    System.err.println("Ignorando entrada inválida em tasks.json: título ausente");
                    idx = end + 1;
                    continue;
                }
                String descricao = fields.getOrDefault("descricao", "");
                LocalDate prazo = null;
                String prazoS = fields.getOrDefault("prazo", "");
                if (!prazoS.isEmpty()) {
                    try { prazo = LocalDate.parse(prazoS); } catch (Exception ignored) {}
                }
                TaskPriority prioridade = TaskPriority.MEDIA;
                try { if (fields.containsKey("prioridade") && !fields.get("prioridade").isEmpty()) prioridade = TaskPriority.valueOf(fields.get("prioridade")); } catch (Exception ignored) {}
                TaskStatus status = TaskStatus.PENDENTE;
                try { if (fields.containsKey("status") && !fields.get("status").isEmpty()) status = TaskStatus.valueOf(fields.get("status")); } catch (Exception ignored) {}
                String projeto = fields.getOrDefault("projeto", "");

                Task t = new Task(id, titulo, descricao, prazo, prioridade, status, projeto);
                data.put(t.getId(), t);

                idx = end + 1;
            }

        } catch (Exception e) {
            System.err.println("Falha ao carregar tasks.json: " + e.getMessage());
        }
    }

    private Map<String,String> parseObject(String obj) {
        Map<String,String> map = new HashMap<>();
        // procura pares "key": "value" (valor pode ser vazio)
        Pattern p = Pattern.compile("\"(.*?)\"\s*:\s*\"(.*?)\"", Pattern.DOTALL);
        Matcher m = p.matcher(obj);
        while (m.find()) {
            String k = m.group(1);
            String v = m.group(2).replaceAll("\\\\n", "\n").replaceAll("\\\\\"", "\"").replaceAll("\\\\\\\\", "\\");
            map.put(k, v);
        }
        return map;
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public void save(Task task) {
        data.put(task.getId(), task);
        persist();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
        persist();
    }

    @Override
    public void deleteAllById(Collection<UUID> ids) {
        for (UUID id : ids) data.remove(id);
        persist();
    }

    @Override
    public void persist() {
        // escreve como JSON legível
        try (FileOutputStream fos = new FileOutputStream(file); OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write("[");
            boolean first = true;
            for (Task t : data.values()) {
                if (!first) bw.write(",\n");
                first = false;
                bw.write("  {");
                bw.write("\n    \"id\": \"" + escape(t.getId().toString()) + "\",");
                bw.write("\n    \"titulo\": \"" + escape(t.getTitulo()) + "\",");
                bw.write("\n    \"descricao\": \"" + escape(t.getDescricao()) + "\",");
                bw.write("\n    \"prazo\": \"" + (t.getPrazo() != null ? t.getPrazo().toString() : "") + "\",");
                bw.write("\n    \"prioridade\": \"" + (t.getPrioridade() != null ? t.getPrioridade().name() : "") + "\",");
                bw.write("\n    \"status\": \"" + (t.getStatus() != null ? t.getStatus().name() : "") + "\",");
                bw.write("\n    \"projeto\": \"" + escape(t.getProjeto()) + "\"\n  }");
            }
            bw.write("\n]\n");
        } catch (IOException e) {
            System.err.println("Falha ao salvar tasks.json: " + e.getMessage());
        }
    }
}
