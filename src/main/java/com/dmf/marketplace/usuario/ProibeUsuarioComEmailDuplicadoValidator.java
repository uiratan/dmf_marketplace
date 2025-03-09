package com.dmf.marketplace.usuario;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

//4
@Component
public class ProibeUsuarioComEmailDuplicadoValidator implements Validator {

    private final UsuarioRepository repository;

    public ProibeUsuarioComEmailDuplicadoValidator(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        //1
        return NovoUsuarioRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        //1
        if (errors.hasErrors()) {
            return;
        }

        NovoUsuarioRequest request = (NovoUsuarioRequest) target;

        //1
        Optional<Usuario> possivelUsuario = repository.findByLogin(request.getLogin());

        //1
        if (possivelUsuario.isPresent()) {
            errors.rejectValue("login", null, "ja existe este email no sistema");
        }
    }
}
