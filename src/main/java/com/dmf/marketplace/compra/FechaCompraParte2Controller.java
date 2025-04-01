package com.dmf.marketplace.compra;

public class FechaCompraParte2Controller {
}

/*
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
 */