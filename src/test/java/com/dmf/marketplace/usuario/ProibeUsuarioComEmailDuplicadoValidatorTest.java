package com.dmf.marketplace.usuario;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Optional;
import java.util.stream.Stream;

public class ProibeUsuarioComEmailDuplicadoValidatorTest {

    @Test
    @DisplayName("deveria parar caso já tenha erro")
    void testaValidateComErrors() {
        //Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        ProibeUsuarioComEmailDuplicadoValidator validator = new ProibeUsuarioComEmailDuplicadoValidator(usuarioRepository);

        NovoUsuarioRequest target = new NovoUsuarioRequest("email@email.com", "senhaa");

        Errors errors = new BeanPropertyBindingResult(target, "login");
        errors.reject("login");

        //Act
        validator.validate(target, errors);

        Assertions.assertTrue(errors.hasErrors());
        Assertions.assertEquals("login", errors.getGlobalErrors().get(0).getCode());
    }

    @DisplayName("deveria lidar com login duplicado")
    @ParameterizedTest
    @MethodSource("geradorTeste1")
    void teste1(Optional<Usuario> possivelUsuario, boolean esperado) {
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        ProibeUsuarioComEmailDuplicadoValidator validator = new ProibeUsuarioComEmailDuplicadoValidator(usuarioRepository);

        NovoUsuarioRequest target = new NovoUsuarioRequest("email@email.com", "senhaa");
        Errors errors = new BeanPropertyBindingResult(target, "login");

        Mockito.when(usuarioRepository.findByLogin("email@email.com")).thenReturn(possivelUsuario);

        validator.validate(target, errors);

        Assertions.assertEquals(esperado, errors.hasFieldErrors("login"));

    }

    public static Stream<Arguments> geradorTeste1() {
        Optional<Usuario> usuario = Optional.of(new Usuario("email@email.com", new SenhaLimpa("senhaa")));
        return Stream.of(Arguments.of(usuario, true), Arguments.of(Optional.empty(), false));
    }

}