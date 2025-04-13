<div align="center">
  <h1>Imagens da aplicação</h1>
  <table style="display: inline-table;">
    <tr>
      <td align="center"><img src="img/login.png">Login</td>
      <td align="center"><img src="img/home.png">Home</td>
      <td align="center"><img src="img/cargas.png">Carga(as)</td>
      <td align="center"><img src="img/BarraDeProgresso.png">Barra de Progresso</td>
    </tr>
  </table>
</div>

<br>
<h4>
   Aplicação que gerencia o desempenho de usuários que praticam musculação, para otimizar e monitorar seu desempenho de maneira eficaz. Neste contexto, o projeto é dedicado ao         gerenciamento integral do desempenho de usuários engajados na prática da musculação. <br>
   Oferecendo uma experiência intuitiva e abrangente, fornecendo ferramentas para o acompanhamento de treinos, análise de progresso, definição de metas 
   personalizadas. 
<br>
<div>
<h2 align="center">Tecnologias Usadas</h2>
  <br>
    <h4 align="center"> 
      <p>
       <img align="center" alt="kruger" height="35" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg"/>
       Java
       <img align="center" alt="kruger" height="35" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/css3/css3-original.svg"/>
       CSS3
       <img align="center" alt="kruger" height="35" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg"/>
       MySQL Database
       </p>
   </h4>
</div>
<br>
    <h2 align="center">UML</h2>

```mermaid
    classDiagram
    class User {
      - id: Integer
      - name: String
      - password: String
    }

    class Heavys {
      - id: Integer
      - exercise: String
      - heavy: Double
      - maxRep: Integer
      - minRep: Integer
      - userId: Integer
    }

    User --> "1*" Heavys : -loads
```

<h3> Destrinchando a estrutura: </h3>

🔹 `User`: Entidade que representa um usuário(a) que armazena informações como id, nome, senha e o os pesos ao qual ele pertence.

🔹 `Heavys`: Entidade que representa as cargas do usuário(a) de um determinado exercício, peso, repetições mínimas e máximas e um identificador para cada usuário.

- `1*`: Expressão utilizada para demonstrar uma 'dependência' ou associação entre as entidades, que neste caso trata-se de que um `User` deve ter **1 ou mais** `- loads`. 

> Com essa dependência entre as classes, é garantido que cada `User` tenha seus dados armazenados e presistidos nos `Heavys`.
