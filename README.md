# SIS - Sistema de Inscrição e Seleção Académica
**Centro de Formação Ocean S.A. • Processo Seletivo 2026**

O **SIS (Sistema de Inscrição e Seleção)** é uma plataforma desktop corporativa moderna desenvolvida em **Java** e **JavaFX**, destinada à gestão integral do processo de admissão académica do Centro de Formação Ocean S.A. O sistema atende tanto os candidatos (auto-registo, acompanhamento do estado de admissão, submissão documental e emissão de comprovativos em PDF) quanto a equipa de secretaria/administração (análise de concorrência por província, gestão de vagas, aprovação/rejeição de candidaturas e relatórios consolidados).

---

## 🏛 1. Arquitetura do Sistema

O projeto segue rigorosamente o padrão arquitetural **MVC (Model-View-Controller)** complementado pelo padrão **DAO (Data Access Object)** e **Service Layer**:

```
src/main/java/
├── controller/                 # Controladores de mediação e regras de fluxo
│   ├── CandidatoController.java
│   ├── CursoController.java
│   ├── InscricaoController.java
│   ├── LoginController.java
│   ├── PainelController.java
│   └── UsuarioSecretariaController.java
├── dao/                        # Acesso a dados e persistência MySQL
│   ├── ConexaoBD.java
│   ├── CandidatoDAO.java
│   ├── CursoDAO.java
│   ├── DatabaseInitializer.java
│   ├── DocumentoDAO.java
│   ├── InscricaoDAO.java
│   ├── QuestionarioCarenciaDAO.java
│   └── UsuarioSecretariaDAO.java
├── model/                      # Modelos de domínio e regras de negócio
│   ├── Candidato.java
│   ├── Curso.java
│   ├── Documento.java
│   ├── Inscricao.java
│   ├── QuestionarioCarencia.java
│   ├── StatusInscricao.java
│   └── UsuarioSecretaria.java
├── service/                    # Serviços especializados de infraestrutura
│   ├── AutenticacaoService.java
│   └── RelatorioPdfService.java
└── View/fx/                    # Camada de Apresentação JavaFX moderna
    ├── ComponentesVisuais.java  # Fábrica de componentes, animações, logos e popups
    ├── Navegador.java           # Gestor de cenas, rotas e atalhos globais
    ├── TelaAutenticacaoFX.java  # Ecrã unificado de Login e Criação de Conta
    ├── TelaDashboardCandidatoFX.java # Painel do Aluno/Candidato
    ├── TelaDashboardSecretariaFX.java# Painel Executivo da Secretaria
    ├── TelaLoginFX.java         # Ecrã alternativo de autenticação direta
    └── TelaNovaInscricaoFX.java # Formulário guiado de nova candidatura
```

---

## ✨ 2. Principais Funcionalidades

### 🔐 2.1 Autenticação Unificada & Roteamento Inteligente
- **Ecrã Único (`TelaAutenticacaoFX`)**: Um único ponto de entrada para alunos e funcionários. O sistema valida as credenciais e roteia automaticamente o utilizador para o **Painel da Secretaria** ou para o **Painel do Candidato**.
- **Registo Imediato de Candidatos**: Aba dedicada para criação de conta de estudante com validação em tempo real de formato de BI (13 dígitos com letra final), contacto telefónico (9 dígitos), email institucional/pessoal e integridade de senhas.

### 📊 2.2 Painel Administrativo da Secretaria (`TelaDashboardSecretariaFX`)
- **Menu Lateral 100% em Português**:
  - `PAINEL ACADÉMICO` (Identificação institucional)
  - `GESTÃO` (Visão Geral, Candidatos, Cursos & Vagas)
  - `ADMINISTRAÇÃO` (Utilizadores, Relatórios PDF)
  - `PREFERÊNCIAS` (Alternar Tema Claro/Escuro, Terminar Sessão)
- **Métricas em Tempo Real**:
  - Total de Candidaturas Submetidas.
  - Total de Cursos Oferecidos e Vagas Totais Disponíveis.
  - Rácio de Concorrência (candidatos por vaga).
- **Gráficos Analíticos Dinâmicos**:
  - `BarChart`: Distribuição geográfica dos candidatos pelas 11 províncias de Moçambique.
  - `PieChart`: Proporção de ocupação e oferta de vagas por curso.
- **Lista Completa de Candidaturas Registadas**:
  - Visualização de **todas as inscrições** realizadas no sistema.
  - Contentor de visualização ajustado com barra de rolagem dedicada e contadores dinâmicos.
  - Etiquetas reais de estado: `Aceite` (Verde), `Recusado` (Vermelho) e `Pendente / Lista Espera` (Amarelo).
- **Gestão de Candidatos com Busca Rápida**:
  - Tabela filtrável por Nome ou Número de BI com atalho dedicado (`Ctrl + P`).
  - Aprovação ou recusa com persistência direta na base de dados.
- **Emissão de Relatórios em PDF (iText 8)**:
  - Balanço geral de inscrições e comprovativos individuais com layout corporativo oficial.

### 🎓 2.3 Portal do Candidato (`TelaDashboardCandidatoFX`)
- **Acompanhamento de Estado**: Identificação visual clara do estado da inscrição (`Pendente`, `Aceite`, `Recusado`).
- **Submissão e Gestão Documental**:
  - Cópia e armazenamento seguro de documentos oficiais (BI autenticado, Certificado de Habilitações, Atestado de Residência).
  - Persistência dos ficheiros na pasta do sistema `~/SIS_Documentos/`.
- **Download de Comprovativo Oficial**: Emissão a qualquer momento do comprovativo de candidatura assinado digitalmente em PDF.

### 📋 2.4 Questionário de Carência Socioeconómica
Algoritmo de cálculo social que atribui até 100 pontos para ordenação em vagas reservadas e bolsas sociais:
1. **Rendimento Familiar Mensal**: Até 40 pontos (quanto menor a renda, maior a pontuação).
2. **Situação Laboral do Agregado**: Até 20 pontos (prioridade para famílias em situação de desemprego).
3. **Distância até à Instituição**: Até 15 pontos adicionais para candidatos residentes a mais de 100 km.
4. **Condição de Órfão ou Chefe de Família**: +15 pontos de bonificação.
5. **Ausência de Apoio Governamental/Familiar**: +10 pontos.
- **Popups Informativos com Ativação Única**: Ao passar o rato sobre cada critério, o sistema apresenta um popup toast temporário (3 segundos de duração) explicando o impacto daquele item no processo seletivo. O popup é apresentado apenas uma vez por campo para evitar poluição visual.

---

## ⌨ 3. Guia de Atalhos de Teclado Globais

| Atalho | Ação Executada | Ecrãs Aplicáveis |
| :--- | :--- | :--- |
| **`Ctrl + P`** | **Focar campo de pesquisa de candidatos** / início de formulário | Dashboard Secretaria, Autenticação, Nova Inscrição |
| **`Ctrl + O`** | **Tornar campos de senha visíveis / ocultos** (Alternador) | Ecrã de Login, Registo de Candidato, Nova Inscrição |
| **`Ctrl + T`** ou **`F2`** | **Alternar Tema (Modo Claro ☀ / Modo Escuro 🌙)** | Global (disponível em todo o sistema) |
| **`Ctrl + Enter`** ou **`Ctrl + S`** | **Submeter formulário de inscrição / login** | Autenticação, Nova Inscrição |
| **`Ctrl + Tab`** | **Alternar entre abas Login e Criar Conta** | Ecrã de Autenticação |
| **`F1`** | **Exibir popup de ajuda com os atalhos disponíveis** | Global (disponível em todo o sistema) |
| **`Esc`** | **Cancelar operação ou voltar ao ecrã inicial** | Global |
| **`Alt + N`** | Focar diretamente no campo **Nome Completo** | Formulário de Inscrição |
| **`Alt + B`** | Focar diretamente no campo **Número de BI** | Formulário de Inscrição |
| **`Alt + C`** | Focar diretamente no campo **Celular / Telefone** | Formulário de Inscrição |
| **`Alt + E`** | Focar diretamente no campo **Email de Registo** | Formulário de Inscrição |

---

## 🥚 4. Easter Egg

- **Ativação**: No topo da barra lateral ou cabeçalho do sistema, mantenha o ponteiro do rato pressionado sobre o logotipo da instituição por **mais de 20 segundos** contínuos.
- **Resultado**: Uma animação com efeito popup exibe a mensagem:
  > *"Eu Sou inevitável"*

---

## 🎨 5. Design System e Interface Visual

- **Paleta de Cores Moderna**:
  - Tema Claro: Tons de ardósia limpos (`#F8FAFC`, `#FFFFFF`), primária em azul oceano (`#2563EB`) e bordas suaves (`#E2E8F0`).
  - Tema Escuro: Fundo profundo em ardósia escura (`#0F172A`, `#1E293B`), texto com alto contraste (`#F1F5F9`) e acentos ciano/teal (`#2DD4BF`).
- **Campos de Senha com Visibilidade Integrada**: Botão de olho embutido no próprio contorno do input, eliminando campos soltos ou desalinhados.
- **Popups Toast Flutuantes**: Sistema de notificações não-bloqueantes com cantos arredondados, ícones de alerta e desaparecimento automático.

---

## 🗄 6. Base de Dados e Configuração

O sistema utiliza **MySQL** como motor de base de dados relacional. Ao arrancar, a classe `DatabaseInitializer` verifica e cria automaticamente as tabelas e dados de seed necessários.

### Esquema das Tabelas:
1. `candidatos`: ID, nome_completo, numero_bi, data_nascimento, telefone, provincia, distrito, morada, email, senha, data_inscricao.
2. `usuarios_secretaria`: ID, nome, email, senha, perfil.
3. `cursos`: ID, codigo, nome, descricao, numero_vagas.
4. `inscricoes`: ID, id_candidato, id_curso, data_inscricao, status, pontuacao_carencia.
5. `questionarios_carencia`: ID, id_candidato, faixa_rendimento, situacao_laboral, distancia_km, is_orfao, recebe_apoio, pontuacao_final.
6. `documentos_candidato`: ID, id_candidato, tipo_documento, caminho_arquivo, data_envio.

### Credenciais Administrativas Padrão:
- **Email**: `admin@ocean.co.mz`
- **Senha**: `admin123`

---

## 🚀 7. Como Compilar e Executar

### Pré-requisitos:
- **Java JDK 21** ou superior instalado e configurado nas variáveis de ambiente (`JAVA_HOME`).
- **Apache Maven 3.8+**.
- **Servidor MySQL** em execução na porta `3306` com uma base de dados chamada `sistema_inscricao` (ou credenciais configuradas em `ConexaoBD.java`).

### Passos de Execução:

1. **Clonar o Repositório**:
   ```bash
   git clone <url-do-repositorio>
   cd SISTEMA-DE-INSCRICAO-e-SELECAO-feature-model-dao-continuacao
   ```

2. **Compilar o Projeto**:
   ```bash
   mvn clean compile
   ```

3. **Executar a Aplicação**:
   ```bash
   mvn javafx:run
   ```
   *Ou execute a classe principal:* `Main.java` *diretamente através do IntelliJ IDEA ou Eclipse.*

---

## 👥 Equipa de Desenvolvimento
- **SIS - Ocean S.A.** • Desenvolvido sob princípios modernos de Engenharia de Software, MVC estrito e JavaFX Design System.
