package com.dmf.marketplace.produto;

import com.dmf.marketplace.compartilhado.ImageUploadService;
import com.dmf.marketplace.compartilhado.seguranca.UsuarioLogado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ImagensProdutoController {

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private Environment environment;

    @PostMapping("/{id}/imagens-url")
    @Transactional // Garante que a transação seja gerenciada
    public ResponseEntity<String> adicionaImagensURL(
            @AuthenticationPrincipal UsuarioLogado usuarioLogado,
            @PathVariable("id") Long idProduto,
            @RequestBody @Valid NovaImagemURLRequest request) {
        Produto produto = manager.find(Produto.class, idProduto);
        if (produto == null) {
            return ResponseEntity.status(404).body("Produto não encontrado.");
        }

        if (produto.pertenceAoUsuario(usuarioLogado.get())) {
            List<String> imageUrls = imageUploadService.uploadImagesFromUrl(request.imagens());
            // Persiste no banco apenas em produção
            if (isProductionProfileActive()) {
                produto.adicionarImagens(imageUrls);
                manager.merge(produto); // Atualiza o produto no banco
                return ResponseEntity.ok("Imagens enviadas e salvas com sucesso: " + imageUrls);
            } else {
                return ResponseEntity.ok("Imagens processadas (fictícias) com sucesso: " + imageUrls);
            }
        } else {
            return ResponseEntity.status(403).body("Você não tem permissão para adicionar imagens a este produto."); // Status 403 Forbidden
        }
    }

    @PostMapping("/{id}/imagens-arquivo")
    @Transactional // Garante que a transação seja gerenciada
    public ResponseEntity<String> adicionaImagensArquivos(
            @AuthenticationPrincipal UsuarioLogado usuarioLogado,
            @PathVariable("id") Long idProduto,
            @Valid NovaImagemArquivoRequest request) {
        Produto produto = manager.find(Produto.class, idProduto);
        if (produto == null) {
            return ResponseEntity.status(404).body("Produto não encontrado.");
        }

        if (produto.pertenceAoUsuario(usuarioLogado.get())) {
            List<String> imageUrls = imageUploadService.uploadImagesFromFiles(request.imagens());
            // Persiste no banco apenas em produção
            if (isProductionProfileActive()) {
                produto.adicionarImagens(imageUrls);
                manager.merge(produto); // Atualiza o produto no banco
                return ResponseEntity.ok("Imagens enviadas e salvas com sucesso: " + imageUrls);
            } else {
                return ResponseEntity.ok("Imagens processadas (fictícias) com sucesso: " + imageUrls);
            }
        } else {
            return ResponseEntity.status(403).body("Você não tem permissão para adicionar imagens a este produto."); // Status 403 Forbidden
        }
    }

    private boolean isProductionProfileActive() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("prod".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}