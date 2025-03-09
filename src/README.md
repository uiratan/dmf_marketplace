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


