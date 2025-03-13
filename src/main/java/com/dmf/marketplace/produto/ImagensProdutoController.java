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

import java.io.IOException;
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

        if (produto.pertenceAoUsuario(usuarioLogado.get())) {
            try {
                List<String> imageUrls = imageUploadService.uploadImages(request.imagens());
                // Persiste no banco apenas em produção
                if (isProductionProfileActive()) {
                    produto.adicionarImagens(imageUrls);
                    manager.merge(produto); // Atualiza o produto no banco
                    return ResponseEntity.ok("Imagens enviadas e salvas com sucesso: " + imageUrls);
                } else {
                    return ResponseEntity.ok("Imagens processadas (fictícias) com sucesso: " + imageUrls);
                }
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Erro ao enviar imagens: " + e.getMessage());
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