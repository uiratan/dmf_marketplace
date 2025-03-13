package com.dmf.marketplace.produto;

import com.dmf.marketplace.compartilhado.aws.S3Service;
import com.dmf.marketplace.compartilhado.seguranca.UsuarioLogado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ImagensProdutoController {

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private S3Service s3Service;

    @PostMapping("/{id}/imagens")
    @Transactional // Garante que a transação seja gerenciada
    public ResponseEntity<String> novaImagemProduto(
            @AuthenticationPrincipal UsuarioLogado usuarioLogado,
            @PathVariable("id") Long idProduto,
            @RequestBody @Valid NovaImagemRequest request) {
        Produto produto = manager.find(Produto.class, idProduto);
        if (produto == null) {
            return ResponseEntity.status(404).body("Produto não encontrado.");
        }

        System.out.println(produto);

        if (produto.pertenceAoUsuario(usuarioLogado.get())) {
            System.out.println(usuarioLogado);
            try {
                List<String> s3Urls = s3Service.uploadImages(request.imagens());

                // Adiciona as URLs ao produto e persiste no banco
                produto.adicionarImagens(s3Urls);
                manager.merge(produto); // Atualiza o produto no banco

                return ResponseEntity.ok("Imagens enviadas com sucesso: " + s3Urls);
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Erro ao enviar imagens: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(403).body("Você não tem permissão para adicionar imagens a este produto."); // Status 403 Forbidden
        }
    }

}

/*
Usuário logado adiciona imagem no seu produto
explicação
Com um produto cadastrado, um utilizador logado pode adicionar imagens ao seu produto.
Não precisa salvar a imagem em algum cloud ou no próprio sistema de arquivos.
Cada arquivo de imagem pode virar um link ficticio que pode ser adicionado ao produto.
 */

/*
necessidades
Adicionar uma ou mais imagens a um determinado produto do próprio usuário
restrições
Tem uma ou mais fotos
Só pode adicionar fotos ao produto que pertence ao próprio usuário
resultado esperado
 */

/*
Imagens adicionadas e 200 como retorno
Caso dê erro de validação retorne 400 e o json dos erros
Caso tente adicionar imagens a um produto que não é seu retorne 403.
 */
