package com.dmf.marketplace.pergunta;

import com.dmf.marketplace.compartilhado.email.EmailService;
import com.dmf.marketplace.compartilhado.seguranca.UsuarioLogado;
import com.dmf.marketplace.produto.Produto;
import com.dmf.marketplace.usuario.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produtos/{idProduto}/perguntas")
public class PerguntaController {

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private EmailService emailService;

    @PostMapping
    @Transactional
    public ResponseEntity<List<Pergunta>> criar(
            @PathVariable Long idProduto,
            @RequestBody @Valid NovaPerguntaRequest request,
            @AuthenticationPrincipal UsuarioLogado usuarioLogado
    ) {
        Produto produto = manager.find(Produto.class, idProduto);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }
        //1 //1
        Usuario consumidor = usuarioLogado.get();
        if (consumidor == null) {
            return ResponseEntity.notFound().build();
        }

        Pergunta pergunta = request.toModel(manager, produto, consumidor);
        manager.persist(pergunta);

        EmailNovaPergunta result = getEmailNovaPergunta(pergunta);
        emailService.enviarEmail(result.destinatario(), result.assunto(), result.corpo());

        return this.listar();
    }

    private EmailNovaPergunta getEmailNovaPergunta(Pergunta pergunta) {
        String destinatario = pergunta.getProduto().getUsuario().getLogin();
        String assunto = "Nova pergunta sobre seu produto: " + pergunta.getProduto().getNome();
        String corpo = """
                Olá %s,

                Você recebeu uma nova pergunta sobre seu produto "%s":

                Pergunta: "%s"

                Acesse o link abaixo para visualizar e responder a pergunta:
                http://localhost:8080/produtos/%d/perguntas/%d

                Atenciosamente,
                Equipe Marketplace
                """.formatted(
                destinatario,
                pergunta.getProduto().getNome(),
                pergunta.getPergunta(),
                pergunta.getProduto().getId(),
                pergunta.getId()
        );
        return new EmailNovaPergunta(destinatario, assunto, corpo);
    }

    private record EmailNovaPergunta(String destinatario, String assunto, String corpo) {
    }

    @GetMapping
    public ResponseEntity<List<Pergunta>> listar() {
        List<Pergunta> perguntas = manager.createQuery("SELECT o FROM Pergunta o", Pergunta.class).getResultList();
        return ResponseEntity.ok(perguntas);
    }

    @GetMapping("/{idPergunta}")
    public ResponseEntity<Pergunta> encontrarPorId(
            @PathVariable Long idProduto,
            @PathVariable Long idPergunta) {
        Produto produto = manager.find(Produto.class, idProduto);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<Pergunta> pergunta = manager.createQuery(
                        "SELECT o FROM Pergunta o WHERE o.id = :id AND o.produto.id = :idProduto",
                        Pergunta.class)
                .setParameter("id", idPergunta)
                .setParameter("idProduto", idProduto)
                .getResultStream()
                .findFirst();

        return pergunta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}