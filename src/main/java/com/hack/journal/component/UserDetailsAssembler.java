package com.hack.journal.component;

import com.hack.journal.controller.UserController;
import com.hack.journal.model.user.UserDetailsWithoutSecrets;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsAssembler extends RepresentationModelAssemblerSupport<Object, UserDetailsWithoutSecrets> {
    public UserDetailsAssembler() {
        super(UserController.class, UserDetailsWithoutSecrets.class);
    }

    @Override
    public UserDetailsWithoutSecrets toModel(Object entity) {
        return (UserDetailsWithoutSecrets) entity;
    }
}
