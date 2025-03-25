package com.dmf.marketplace.categoria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NovaCategoriaRequestTest {

    @PersistenceContext
    private EntityManager manager;

    @Test
    @DisplayName("deve criar categoria sem categoria mae")
    public void teste1() {
        // Arrange
        NovaCategoriaRequest request = new NovaCategoriaRequest("categoria", null);

        // Act
        Categoria categoria = request.toModel(manager);
        manager.persist(categoria);
        manager.flush();
        // Assert
        assertNotNull(categoria.getId());
        assertEquals("categoria", categoria.getNome());
        assertNull(categoria.getCategoriaMae());
    }

    @Test
    @DisplayName("deve criar categoria com categoria mae válida")
    public void teste2() {
        // Arrange
        Categoria categoriaMae = new Categoria("categoria mae");
        manager.persist(categoriaMae);
        manager.flush();

        NovaCategoriaRequest request = new NovaCategoriaRequest("categoria", categoriaMae.getId());

        // Act
        Categoria categoriaFilha = request.toModel(manager);
        manager.persist(categoriaFilha);
        manager.flush();

        // Assert
        assertNotNull(categoriaFilha.getId());
        assertEquals("categoria", categoriaFilha.getNome());
        assertEquals(categoriaMae.getId(), categoriaFilha.getCategoriaMae().getId());
    }

    @Test
    @DisplayName("deve lançar exceção com categoria mae inválida")
    public void teste3() {
        // Arrange
        NovaCategoriaRequest request = new NovaCategoriaRequest("Smartphones", 999L); // ID inexistente

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> request.toModel(manager));

        assertEquals("O id da categoria mae precisa ser válido", exception.getMessage());
    }

}