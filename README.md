# 1. Cadastro de novo usuário
## Necessidades
* precisamos saber o instante do cadastro, login e senha.

## Restrições
* O instante não pode ser nulo e não pode ser no futuro
* O login não pode ser em branco ou nula
* O login precisa ter o formato do email
* A senha não pode ser branca ou nula
* A senha precisa ter no mínimo 6 caracteres
* A senha deve ser guardada usando algum algoritmo de hash da sua escolha.

## Resultado esperado
* O usuário precisa estar criado no sistema
* O cliente que fez a requisição precisa saber que o usuário foi criado. Apenas um retorno com status 200 está suficente.

# 2. Não podemos ter dois usuários com o mesmo email.
## Necessidades
* Só pode existir um usuário com o mesmo email.

## Resultado esperado
* Status 400 informando que não foi possível realizar um cadastro com este email.

# 3. Cadastro de categorias
## Necessidades
* No mercado livre você pode criar hierarquias de categorias livres. Ex: Tecnologia -> Celulares -> Smartphones -> Android,Ios etc. Perceba que o sistema precisa ser flexível o suficiente para que essas sequências sejam criadas.
* Toda categoria tem um nome
* A categoria pode ter uma categoria mãe

## Restrições
* O nome da categoria é obrigatório
* O nome da categoria precisa ser único

## Resultado esperado
* Categoria criada e status 200 retornado pelo endpoint.
* Caso exista erros de validação, o endpoint deve retornar 400 e o json dos erros.

# 4. Segurança

## Usuário logado

Você precisa configurar um mecanismo de autenticação via token, provavelmente com o Spring Security, para permitir o login. 

Em todo trecho de código que precisar do usuário logado, na primeira linha do método do controller que precisa de um usuário logado, 
eu buscaria pelo usuário com um email específico e usaria ele como "referência logada" na aplicação.

Depois, se você quiser, é só habilitar o projeto de segurança, receber o usuário como argumento do método e apagar essa linha... 
Tudo deveria funcionar normalmente.

---

### **Funcionamento Geral**
1. O cliente envia uma requisição POST para `/api/auth` com email e senha.
2. O `UserAuthenticationController` autentica as credenciais usando o `AuthenticationManager`.
3. Se válido, gera um token JWT e retorna ao cliente.
4. O cliente inclui o token no header `Authorization` em requisições futuras.
5. O `JwtAuthenticationFilter` valida o token e autentica o usuário no `SecurityContextHolder`.
6. O Spring Security autoriza ou nega o acesso com base nas regras definidas.

---

## Implementação
#### **1. `pom.xml`**
Este é o arquivo de configuração do Maven, que define as dependências e plugins do projeto.

- **Spring Boot Starter Parent**: Herda configurações do Spring Boot 3.4.3, garantindo compatibilidade entre dependências.
- **Dependências principais**:
    - `spring-boot-starter-web`: Para construir uma API REST.
    - `spring-boot-starter-security`: Para autenticação e autorização.
    - `spring-boot-starter-oauth2-resource-server`: Suporte a OAuth2 e JWT.
    - `spring-security-crypto`: Para criptografia de senhas (BCrypt).
    - `spring-boot-starter-data-jpa`: Integração com banco de dados via JPA.
    - `h2`: Banco em memória para testes.
    - `nimbus-jose-jwt`: Biblioteca para manipulação de JWT.
    - `spring-boot-starter-test`: Para testes unitários/integração.
    - `jacoco-maven-plugin`: Para geração de relatórios de cobertura de código.
- **Java Version**: Configurado para Java 23.
- **Plugins**: Inclui o `spring-boot-maven-plugin` para empacotar a aplicação e `jacoco-maven-plugin` para cobertura de testes.

#### **2. `UserAuthenticationController`**
Este é o controlador REST responsável por autenticar usuários.

- **Anotações**:
    - `@RestController`: Define um controlador REST.
    - `@RequestMapping("/api/auth")`: Mapeia requisições para `/api/auth`.
    - `@PostMapping`: Aceita POST com JSON (`consumes` e `produces`).
- **Injeção de Dependências**:
    - `AuthenticationManager`: Gerencia a autenticação.
    - `TokenManager`: Gera e valida tokens JWT.
- **Método `authenticate`**:
    1. Recebe um `LoginInputDto` com email e senha via `@RequestBody`.
    2. Cria um `UsernamePasswordAuthenticationToken` com as credenciais.
    3. Usa o `authManager` para autenticar as credenciais.
    4. Se bem-sucedido, gera um token JWT com `tokenManager.generateToken`.
    5. Retorna o token em um `ResponseEntity.ok`.
    6. Em caso de erro (`AuthenticationException` ou `JOSEException`), loga o erro e retorna `400 Bad Request`.

#### **3. `TokenManager`**
Classe responsável por gerar e validar tokens JWT.

- **Construtor**:
    - Recebe `secret` (chave secreta) e `expirationInMillis` (tempo de expiração) via `@Value` (provavelmente de um `application.properties`).
    - Cria um `JWK` (JSON Web Key) para assinar o token com algoritmo HMAC-SHA256.
- **Método `generateToken`**:
    1. Extrai o `UserDetails` do `Authentication`.
    2. Cria um `JWTClaimsSet` com emissor, sujeito (username), data de emissão e expiração.
    3. Assina o token com `MACSigner` usando o `jwk`.
    4. Retorna o token serializado.
- **Método `isValid`**:
    - Faz o parsing do token e verifica a assinatura com `MACVerifier`.
    - Confirma se o token não expirou.
- **Método `getUserName`**:
    - Extrai o `subject` (username) do token.

#### **4. `LoginInputDto`**
Classe simples que encapsula os dados de entrada (email e senha) e converte para um `UsernamePasswordAuthenticationToken`.

#### **5. `SecurityConfiguration`**
Configura o Spring Security.

- **Injeção**:
    - `UsersService`: Serviço de busca de usuários.
    - `TokenManager`: Para manipulação de JWT.
- **Método `securityFilterChain`**:
    1. Desativa CSRF (não necessário em APIs stateless).
    2. Configura CORS (padrão).
    3. Define sessão como `STATELESS` (sem cookies, apenas JWT).
    4. Regras de autorização:
        - Permite `GET /produtos/{id}`, `POST /usuarios` e `/api/auth/**` sem autenticação.
        - Exige autenticação para todas as outras requisições.
    5. Adiciona o filtro `JwtAuthenticationFilter` antes do filtro padrão.
    6. Define um `AuthenticationEntryPoint` personalizado para erros 401.
- **Beans**:
    - `AuthenticationManager`: Gerenciador de autenticação.
    - `PasswordEncoder`: Usa `BCryptPasswordEncoder` para criptografia de senhas.

#### **6. `JwtAuthenticationFilter`**
Filtro que valida o token JWT em cada requisição.

- **Método `doFilterInternal`**:
    1. Extrai o token do header `Authorization` (formato "Bearer <token>").
    2. Se o token estiver presente e for válido (`tokenManager.isValid`), busca o usuário com `usersService.loadUserByUsername`.
    3. Cria um `UsernamePasswordAuthenticationToken` e define no `SecurityContextHolder`.
    4. Continua o processamento da requisição (`chain.doFilter`).

#### **7. `UsersService`**
Implementa `UserDetailsService` para carregar usuários do banco.

- **Método `loadUserByUsername`**:
    1. Executa uma query JPA (definida em `security.username-query`) para buscar o usuário por username.
    2. Garante que haja no máximo 1 resultado.
    3. Mapeia o resultado para `UserDetails` usando `UserDetailsMapper`.
    4. Lança `UsernameNotFoundException` se o usuário não for encontrado.

#### **8. `Usuario` e `UsuarioLogado`**
- **`Usuario`**: Entidade JPA que representa um usuário no banco, com login, senha (hash) e data de criação.
    - Usa `BCryptPasswordEncoder` para verificar senhas.
- **`UsuarioLogado`**: Implementa `UserDetails`, encapsulando um `Usuario` e fornecendo informações ao Spring Security.

---

# 5. Usuário logado cadastra novo produto
Aqui a gente vai permitir o cadastro de um produto por usuário logado.

## Necessidades
* Tem um nome
* Tem um valor
* Tem quantidade disponível
* Características(cada produto pode ter um conjunto de características diferente)
* Da uma olhada nos detalhes de produtos diferentes do mercado livre.
* Tem uma descrição que vai ser feita usando Markdown
* Pertence a uma categoria
* Instante de cadastro

## Restrições
* Nome é obrigatório
* Valor é obrigatório e maior que zero
* Quantidade é obrigatório e >= 0
* O produto possui pelo menos três características
* Descrição é obrigatória e tem máximo de 1000 caracteres.
* A categoria é obrigatória

## Resultado esperado
* Um novo produto criado e status 200 retornado
* Caso dê erro de validação retorne 400 e o json dos erros

# 6. Usuário logado adiciona imagem no seu produto
* Com um produto cadastrado, um usuário logado pode adicionar imagens ao seu produto. 
* Não precisa salvar a imagem em algum cloud ou no próprio sistema de arquivos. 
* Cada arquivo de imagem pode virar um link ficticio que pode ser adicionado ao produto.

## Necessidades
* Adicionar uma ou mais imagens a um determinado produto do próprio usuário

## Restrições
* Tem uma ou mais fotos
* Só pode adicionar fotos ao produto que pertence ao próprio usuário

## Resultado esperado
* Imagens adicionadas e 200 como retorno
* Caso dê erro de validação retorne 400 e o json dos erros
* Caso tente adicionar imagens a um produto que não é seu retorne 403.

## Desafio extra
Como você faria para que em dev a imagem virasse um link fictício e em produção executasse um código que enviasse a imagem para algum cloud da vida?

--- 

# 7. Adicione uma opinião sobre um produto
Um usuário logado pode opinar sobre um produto. Claro que o melhor era que isso só pudesse ser feito depois da compra, mas podemos lidar com isso depois.

## Necessidades
* Tem uma nota que vai de 1 a 5
* Tem um título. Ex: espetacular, horrível...
* Tem uma descrição
* O usuário logado que fez a pergunta
* O produto que para o qual a pergunta foi direcionada

## Restrições
* A restrição óbvia é que a nota é no mínimo 1 e no máximo 5
* Título é obrigatório
* Descrição é obrigatório e tem no máximo 500 caracteres
* usuário é obrigatório
* o produto relacionado é obrigatório

## Resultado esperado
* Uma nova opinião é criada e status 200 é retornado
* Em caso de erro de validação, retorne 400 e o json com erros.

# 8. Faça uma pergunta ao vendedor(a)
Um usuário logado pode fazer uma pergunta sobre o produto

## Necessidades
* A pergunta tem um título
* Tem instante de criação
* O usuário que fez a pergunta
* O produto relacionado a pergunta
* O vendedor recebe um email com a pergunta nova e o link para a página de visualização do produto(ainda vai existir)
* O email não precisa ser de verdade. Pode ser apenas um print no console do servidor com o corpo.

## Restrições
* O título é obrigatório
* O produto é obrigatório
* O usuário é obrigatório

## Resultado esperado
* Uma nova pergunta é criada e a lista de perguntas, com a nova pergunta adicionada, é retornada. Status 200
* Em caso de erro de validação, retorne 400 e o json com erros.

# 9. Escreva o código necessário para montar a página de detalhe
O front precisa montar essa página => https://produto.mercadolivre.com.br/MLB-1279370191-bebedouro-bomba-eletrica-p-garrafo-galo-agua-recarregavel-_JM?quantity=1&variation=49037204722&onAttributesExp=true

Não temos todas as informações, mas já temos bastante coisa. Faça, do jeito que achar melhor o código necessário para que o endpoint retorne as informações para que o front monte a página.

## Informações que já temos como retornar
- Links para imagens
- Nome do produto
- Preço do produto
- Características do produto
- Des​crição do produto
- Média de notas do produto
- Número total de notas do produto
- Opiniões sobre o produto
- Perguntas para aquele produto

# 10. Finaliza compra - parte 1
Aqui a gente vai simular uma integração com um gateway como paypal, pagseguro etc. O fluxo geralmente é o seguinte:
- O sistema registra uma nova compra e gera um identificador de compra que pode ser passado como argumento para o gateway.
- O cliente efetua o pagamento no gateway
- O gateway invoca uma url do sistema passando o identificador de compra do próprio sistema e as informações relativas a transação em si.
- Então essa é a parte 1 do processo de finalização de compra. Onde apenas geramos a compra no sistema. Não precisamos da noção de um carrinho compra. Apenas temos o usuário logado comprando um produto.

## Necessidades
- A pessoa pode escolher a quantidade de itens daquele produto que ela quer comprar
- O estoque do produto é abatido
- Um email é enviado para a pessoa que é dona(o) do produto informando que um usuário realmente disse que queria comprar seu produto.
- Uma compra é gerada informando o status INICIADA e com as seguintes informações:
  - gateway escolhido para pagamento
  - produto escolhido
  - quantidade
  - comprador(a)
- Suponha que o cliente pode escolher entre pagar com o Paypal ou Pagseguro.

## Restrições
- A quantidade é obrigatória
- A quantidade é positiva
- Precisa ter estoque para realizar a compra​

## Resultado esperado
- Caso a pessoa escolha o paypal seu endpoint deve gerar o seguinte redirect(302):
  - Retorne o endereço da seguinte maneira: paypal.com/{idGeradoDaCompra}?redirectUrl={urlRetornoAppPosPagamento}
- Caso a pessoa escolha o pagseguro o seu endpoint deve gerar o seguinte redirect(302):
    - Retorne o endereço da seguinte maneira: pagseguro.com?returnId={idGeradoDaCompra}&redirectUrl={urlRetornoAppPosPagamento}
- Caso aconteça alguma restrição retorne um status 400 informando os problemas.

# 12. Finaliza compra - parte 2
Aqui estamos lidando com o retorno do gateway de pagamento

## Necessidades
O meio de pagamento(pagseguro ou paypal) redireciona para a aplicação passando no mínimo 3 argumentos:
- id da compra no sistema de origem
- id do pagamento na plataforma escolhida
- status da compra. 
  - Para o status vamos assumir os dois básicos(Sucesso, Falha). 
  - Os gateways de pagamento informam isso de maneira distinta.
    - Paypal retorna o número 1 para sucesso e o número 0 para erro.
    - Pagseguro retorna a string SUCESSO ou ERRO.
    
## Temos alguns passos aqui.
1. Precisamos registrar a tentativa de pagamento com todas as informações envolvidas. 
2. Além das citadas, é necessário registrar o exato instante do processamento do retorno do pagamento.
3. Caso a compra tenha sido concluída com sucesso:

   1. Precisamos nos comunicar com o setor de nota fiscal que é um outro sistema. 
      - Ele precisa receber apenas receber o id da compra e o id do usuário que fez a compra.
      - Neste momento você não precisa criar outro projeto para simular isso. 
      - Crie um controller com um endpoint fake e faça uma chamada local mesmo.
   2. Também precisamos nos comunicar com o sistema de ranking dos vendedores. 
      - Esse sistema recebe o id da compra e o id do vendedor.
      - Neste momento você não precisa criar outro projeto para simular isso. 
      - Faça uma chamada local mesmo.
   3. Para fechar precisamos mandar um email para quem comprou avisando da conclusão com sucesso. 
      - Pode colocar o máximo de informações da compra que puder.

4. Caso a compra não tenha sido concluída com sucesso, precisamos:
   1. enviar um email para o usuário informando que o pagamento falhou com o link para que a pessoa possa tentar de novo.
   
## Restriçoes
- id de compra, id de transação e status são obrigatórios para todas urls de retorno de dentro da aplicação.
- O id de uma transação que vem de alguma plataforma de pagamento só pode ser processado com sucesso uma vez.
- A transação da plataforma(qualquer que seja) de id X para uma compra Y só pode ser processada com sucesso uma vez.
- Uma transação que foi concluída com sucesso não pode ter seu status alterado para qualquer coisa outra coisa.
- Não podemos ter duas transações com mesmo id de plataforma externa associada a uma compra.

## Desafio extra 1
O email não precisa ser real, manda um syso. Mas o sistema precisa estar preparado para enviar email real em produção.

## Desafio extra 2
Temos duas plataformas de pagamento externas neste momento, e se eu te disser que vamos ter mais, como seu código ficaria preparado para esse requisito de negócio?
   ​

## Resultado esperado
- Status 200 dizendo retornando o status do pagamento.
- Em caso de erro de validação, retorne 400 e o json com erros.


# **Melhorias**

1. **Segurança do Token JWT**:
    - **Chave Secreta**: O `secret` deveria ser mais longo e complexo (mínimo 256 bits para HMAC-SHA256). Considere usar um gerador seguro como `SecureRandom`.

3. **Validação de Entrada**:
    - No `LoginInputDto`, adicione anotações de validação como `@NotBlank` e `@Email` para garantir que os dados sejam válidos antes do processamento.

4. **Logging**:
    - Melhore os logs no `UserAuthenticationController` e `JwtAuthenticationEntryPoint` para incluir mais contexto (ex.: IP do cliente, endpoint solicitado).

6. **Testes**:
    - Adicione mais testes unitários e de integração para cobrir cenários como token inválido, usuário inexistente, etc.
    - Use mocks para `AuthenticationManager` e `TokenManager`.

7. **Gerenciamento de Exceções**:
    - Crie uma exceção personalizada para autenticação e um `ControllerAdvice` para tratar erros de forma consistente, retornando mensagens JSON detalhadas.

8. **Performance**:
    - No `UsersService`, considere adicionar um cache (ex.: Caffeine ou Spring Cache) para evitar consultas repetitivas ao banco.

10. **Versão do Java**:
    - Java 23 é recente (lançado em setembro de 2024). Considere usar uma versão LTS (ex.: Java 21) para maior suporte e estabilidade em produção.

5. **CORS Configuração**:
    - A configuração atual usa o padrão do Spring. Especifique origens permitidas explicitamente (ex.: `http.cors().configurationSource(...)`) para maior segurança.

---

### **TODO**

1. **Segurança do Token JWT**:
    - **Armazenamento Seguro**: Armazene o `secret` em um cofre (ex.: Vault) ou como variável de ambiente, não diretamente no `application.properties`.

2. **Expiração e Refresh Token**:
    - Atualmente, só há um token de acesso. Adicione suporte a **refresh tokens** para evitar que o usuário precise relogar após a expiração.
    - Exemplo: Gere um refresh token com expiração maior e um endpoint `/api/auth/refresh`.

9. **Documentação**:
    - Integre o Springdoc OpenAPI para gerar documentação automática da API (ex.: Swagger UI).
