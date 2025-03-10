package com.dmf.marketplace.categoria;

import com.dmf.marketplace.compartilhado.ExistsId;
import com.dmf.marketplace.compartilhado.UniqueValue;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.util.Assert;

//4
public class NovaCategoriaRequest {

    //1
    @NotBlank
    @UniqueValue(domainClass = Categoria.class, fieldName = "nome")
    private final String nome;

    //1
    @Positive
    @ExistsId(domainClass = Categoria.class, fieldName = "id")
    private final Long idCategoriaMae;

    public NovaCategoriaRequest(final String nome, final Long idCategoriaMae) {
        this.nome = nome;
        this.idCategoriaMae = idCategoriaMae;
    }

    //1
    public Categoria toModel(EntityManager manager) {
        Categoria categoria = new Categoria(nome);
        //1
        if (idCategoriaMae != null) {
            Categoria categoriaMae = manager.find(Categoria.class, idCategoriaMae);
            Assert.notNull(categoriaMae, "O id da categoria mae precisa ser válido");

            categoria.setCategoriaMae(categoriaMae);
        }
        return categoria;
    }

    public String getNome() {
        return nome;
    }

    public Long getIdCategoriaMae() {
        return idCategoriaMae;
    }
}
