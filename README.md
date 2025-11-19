# Gerenciador de Tarefas – Trabalho Final de POO

Programação Orientada a Objetos – Java Swing
Autor: João Pedro da Silva da Madruga
Semestre: 2025/2

# Descrição do Projeto

Este projeto é o Trabalho Final da disciplina Programação Orientada a Objetos, cujo objetivo é desenvolver uma aplicação gráfica completa utilizando Java Swing e aplicando conceitos de orientação a objetos, arquitetura em camadas e persistência de dados.

A aplicação desenvolvida é um Gerenciador de Tarefas, que permite criar, editar, excluir, marcar como concluídas e filtrar tarefas armazenadas localmente com persistência em arquivo.

A atividade segue a Opção A da proposta do professor:

A) Desenvolver do zero uma interface gráfica de usuário para tarefas do usuário de uma aplicação real (ao menos 5 tarefas).

# Principais Funcionalidades

A aplicação implementa todas as funcionalidades obrigatórias, incluindo:

Criar tarefa

Cadastro de nova tarefa informando:

título

descrição

prazo

prioridade (BAIXA, MÉDIA, ALTA)

status

projeto (categoria)

Editar tarefa

Alteração dos dados de uma tarefa selecionada.

Excluir uma ou várias tarefas

Remoção simples ou múltipla via seleção na JTable.

Marcar como concluída

Atualiza o status da tarefa rapidamente.

Filtrar tarefas

Filtros disponíveis:

por texto (busca em título e descrição)

por status

por projeto

somente atrasadas

Visualização em JTable (Componente complexo)

ordenação

seleção múltipla

colunas dinâmicas

modelo customizado TaskTableModel

Persistência de dados

Persistência padrão em JSON legível (tasks.json)

Alternativa opcional usando serialização binária (tasks.dat)

Carregamento automático ao iniciar

Salvamento após operações CRUD

Menu completo 

Arquivo

Novo

Salvar

Sair

Ajuda → Sobre

# Arquitetura do Projeto

O projeto segue arquitetura em camadas:

app/
    TaskManagerApp.java
model/
    Task.java
    TaskStatus.java
    TaskPriority.java
repository/
    TaskRepository.java
    InMemoryTaskRepository.java
    FileTaskRepository.java
    JsonTaskRepository.java
service/
    TaskService.java
view/
    MainFrame.java
    TaskDialog.java
    TaskTableModel.java

# Testes Realizados

A aplicação foi testada com foco nas principais operações:

 Testes de CRUD

criar, editar, excluir e concluir tarefas

validação de campos obrigatórios

múltipla seleção

 Testes de filtros

texto

status

projeto

atrasadas

combinação de filtros

Persistência

arquivo criado automaticamente

dados carregados ao iniciar

alterações persistidas após fechar/reabrir

Interface

JTable ordenável

menus funcionando

diálogos abrindo normalmente

layout responsivo ao redimensionar

# Como executar
1. Compilar via terminal

Abra o terminal na pasta raiz do projeto:

javac -d . model/*.java repository/*.java service/*.java view/*.java app/*.java

2. Executar
java app.TaskManagerApp

Requisitos

Java JDK 17 ou superior

(Opcional) IDE como IntelliJ, NetBeans ou Eclipse

Persistência

A aplicação utiliza dois modos de persistência:

Persistência padrão (JSON)

Arquivo: tasks.json

legível

fácil de depurar

seguro para mudanças de classe

Persistência de dados

Arquivo: tasks.dat

mantida por compatibilidade

usa ObjectOutputStream

# Ferramentas Utilizadas

Java 21

Java Swing (interface gráfica)

ChatGPT para apoio conceitual e revisão do código

Terminal/PowerShell para compilação

GitHub para versionamento

# Entrega

Este repositório deve ser compactado em .zip e enviado no Moodle conforme orientações do professor.

A apresentação em PowerPoint explicando:

as tarefas da aplicação

como executá-las na interface

e os componentes e classes envolvidos

também será disponibilizada junto com o ZIP.

# Fontes consultadas

Documentação oficial Oracle Java Swing

Exemplos do StackOverflow (componentes JTable e JPanel)

ChatGPT para otimização do design e código

GitHub – referências de arquitetura em MVC e Swing

# Licença

Este projeto é acadêmico e não possui fins comerciais.