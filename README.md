0. TEA(TABELA DE ENTIDADE E ASSOCIACAO)
  <img width="3080" height="1189" alt="Sistema de inscricao e selecao (1)" src="https://github.com/user-attachments/assets/00cb7fb0-fdca-4f5f-9e8a-9d74bc793d65" />





1. Sobre o sistema

A instituição oferece aulas gratuitas de preparação para o exame nacional de admissão à universidade. Hoje, quem quer inscrever-se precisa de se deslocar até Maputo, mesmo vindo de outras províncias, o que é caro e demorado para muitas famílias.

O SIS permite que qualquer pessoa se inscreva sem sair de casa e ajuda a instituição a escolher, de forma organizada e justa, quem ocupa as vagas disponíveis.

 2. Objetivos

Objetivo geral
Digitalizar e tornar transparente o processo de inscrição e seleção de candidatos ao curso preparatório.

### Objetivos específicos
- Eliminar a necessidade de deslocação até Maputo para se inscrever.
- Tornar a seleção mais rápida, organizada e transparente.
- Garantir que as vagas não vão só para quem já está em vantagem, mas também para quem mais precisa.
- Retirar da instituição a gestão manual das inscrições.
- Permitir que a instituição ajuste a divisão das vagas sem alterar o código.
- Entregar numa primeira etapa uma versão desktop (sem internet) e evoluir numa segunda etapa para uma versão web.



 3. Funcionamento

3.1 Fluxo do candidato
1. Cria uma conta com os seus dados pessoais (nome, BI, data de nascimento, província, distrito, contacto).
2. Escolhe o curso preparatório pretendido.
3. Responde ao questionário de carência.
4. Submete a inscrição (estado inicial: `PENDENTE`).
5. Mais tarde, entra na conta para consultar o resultado.

 3.2 Fluxo do administrador
1. Cria e configura os cursos (vagas, prazos, divisão mérito/necessidade).
2. Valida os documentos dos candidatos.
3. Regista a nota do exame de cada candidato.
4. Depois do prazo das inscrições, executa a seleção.


3.3 Divisão das vagas
O curso é gratuito e as vagas são limitadas, por isso cada curso divide as suas vagas em duas partes:

| Parte | Exemplo | Critério |
|---|---|---|
| Vagas por mérito | 60% | Melhor nota no exame |
| Vagas por necessidade | 40% | Maior pontuação no questionário de carência |

Os percentuais são guardados em cada `Curso` (`percentualMerito` e `percentualNecessidade`). A instituição pode alterá-los a qualquer momento, sem mexer no código.

3.4 Regra de seleção
Depois do prazo das inscrições, o sistema:
1. Calcula as vagas de cada parte (`getVagasMerito()` e `getVagasNecessidade()`).
2. Ordena os candidatos por nota e aprova os melhores até preencher as vagas de mérito.
3. Entre os restantes, ordena por pontuação de carência (`compararCarencia`) e aprova até preencher as vagas de necessidade. Só entram candidatos elegíveis (`elegivelParaVagaReservada()`).
4. Os candidatos que sobram, mas ainda são elegíveis, ficam em `LISTA_ESPERA`.
5. Os restantes ficam `NAO_APROVADO`.

3.5 Questionário de carência
Poucas perguntas sobre a situação da família do candidato. As respostas geram uma pontuação (0 a 100), usada **apenas** na parte das vagas reservada a quem mais precisa.

| Critério | Pontos |
|---|---|
| Rendimento familiar (5 faixas) | 0 a 40 |
| Situação laboral | 3 a 20 |
| Distância até à instituição | 0 a 15 |
| Órfão ou chefe de família | 15 |
| Não recebe apoio financeiro | 10 |

Níveis de carência: **Alta** (70 ou mais), **Média** (40 a 69) e **Baixa** (abaixo de 40). Têm direito à vaga reservada os candidatos com nível médio ou alto.

Os pesos e as faixas estão em `QuestionarioCarencia` e podem ser ajustados à qualquer momento ao decorrer do trabalho 







```
┌──────────────┐   eventos    ┌──────────────┐   usa    ┌──────────┐   SQL    ┌───────┐
│     View     │ ───────────► │  Controller  │ ───────► │   DAO    │ ───────► │ MySQL │
│ (Swing +     │ ◄─────────── │              │ ◄─────── │          │ ◄─────── │       │
│  FlatLaf)    │   atualiza   └──────┬───────┘  objetos └──────────┘  linhas  └───────┘
└──────────────┘                     │ manipula
                                     ▼
                               ┌──────────┐
                               │  Model   │
                               └──────────┘
```

| Camada | Pacote | Responsabilidade |
|---|---|---|
| **Model** | `model` | Entidades e regras de negócio (pontuação de carência, vagas por curso, estados da inscrição). Não conhece a base de dados nem a interface. |
| **View** | `view` | Ecrãs em Java Swing (`JFrame`, `JPanel`) com o tema FlatLaf: inscrição, login, resultados, administração. Só apresenta dados e capta eventos. |
| **Controller** | `controller` | Liga a View ao Model e aos DAOs: valida o formulário, cria os objetos e chama a persistência. |
| **DAO** | `dao` | Acesso à base de dados via JDBC. Isola o SQL do resto do sistema. |


```
src/main/java
├── dao
│   ├── Tabela.java                     (interface genérica)
│   ├── ConexaoBD.java
│   ├── AdministradorDao.java
│   ├── CandidatoDao.java
│   ├── CursoDao.java
│   └── QuestionarioCarenciaDao.java
├── model
│   ├── Utilizador.java                 (classe abstrata)
│   ├── Candidato.java
│   ├── Administrador.java
│   ├── Curso.java
│   ├── Inscricao.java
│   ├── QuestionarioCarencia.java       (com 3 enums internos)
│   ├── StatusInscricao.java            (enum)
│   └── Perfil.java                     (enum)
├── view                                (planeado)
│   ├── LoginView.java
│   ├── InscricaoView.java              (formulário "Nova inscrição")
│   ├── ResultadoView.java
│   └── AdminView.java
├── controller                          (planeado)
│   └── InscricaoController.java
└── org.example
    └── Main.java
```

Tecnologias

- Java (Maven)
- Java Swing para a interface desktop 
- FlatLaf (`com.formdev:flatlaf`) para o aspeto moderno dos componentes Swing
- MySQL com JDBC (`mysql-connector-j`)
- IntelliJ IDEA / NetBeans




