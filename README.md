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


# 2. Cadastro de categorias
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

# 3. Segurança

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

### **TODO: Melhorias**

1. **Segurança do Token JWT**:
    - **Chave Secreta**: O `secret` deveria ser mais longo e complexo (mínimo 256 bits para HMAC-SHA256). Considere usar um gerador seguro como `SecureRandom`.
    - **Armazenamento Seguro**: Armazene o `secret` em um cofre (ex.: Vault) ou como variável de ambiente, não diretamente no `application.properties`.

2. **Expiração e Refresh Token**:
    - Atualmente, só há um token de acesso. Adicione suporte a **refresh tokens** para evitar que o usuário precise relogar após a expiração.
    - Exemplo: Gere um refresh token com expiração maior e um endpoint `/api/auth/refresh`.

3. **Validação de Entrada**:
    - No `LoginInputDto`, adicione anotações de validação como `@NotBlank` e `@Email` para garantir que os dados sejam válidos antes do processamento.

4. **Logging**:
    - Melhore os logs no `UserAuthenticationController` e `JwtAuthenticationEntryPoint` para incluir mais contexto (ex.: IP do cliente, endpoint solicitado).

5. **CORS Configuração**:
    - A configuração atual usa o padrão do Spring. Especifique origens permitidas explicitamente (ex.: `http.cors().configurationSource(...)`) para maior segurança.

6. **Testes**:
    - Adicione mais testes unitários e de integração para cobrir cenários como token inválido, usuário inexistente, etc.
    - Use mocks para `AuthenticationManager` e `TokenManager`.

7. **Gerenciamento de Exceções**:
    - Crie uma exceção personalizada para autenticação e um `ControllerAdvice` para tratar erros de forma consistente, retornando mensagens JSON detalhadas.

8. **Performance**:
    - No `UsersService`, considere adicionar um cache (ex.: Caffeine ou Spring Cache) para evitar consultas repetitivas ao banco.

9. **Documentação**:
    - Integre o Springdoc OpenAPI para gerar documentação automática da API (ex.: Swagger UI).

10. **Versão do Java**:
    - Java 23 é recente (lançado em setembro de 2024). Considere usar uma versão LTS (ex.: Java 21) para maior suporte e estabilidade em produção.

---

### **Conclusão**
O código implementa uma autenticação baseada em JWT de forma sólida, mas há espaço para melhorias em segurança, usabilidade (refresh tokens) e manutenção (validação, testes, logs). Se precisar de exemplos de código para alguma sugestão, posso fornecer!