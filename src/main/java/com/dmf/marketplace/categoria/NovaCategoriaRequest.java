package com.dmf.marketplace.categoria;

import com.dmf.marketplace.compartilhado.validation.ExistsId;
import com.dmf.marketplace.compartilhado.validation.UniqueValue;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.util.Assert;

/**
 * @param nome           1
 * @param idCategoriaMae 1
 */ //4
public record NovaCategoriaRequest(
        @UniqueValue(domainClass = Categoria.class, fieldName = "nome") @NotBlank String nome,
        @ExistsId(domainClass = Categoria.class, fieldName = "id") @Positive Long idCategoriaMae) {

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

}
