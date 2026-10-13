package br.edu.fiap.banco.controller.mapper;

import jakarta.ws.rs.core.UriInfo;
import org.h2.server.web.JakartaWebServlet;

import java.net.URI;

public interface GenericController {

    default URI gerarHeaderLocation(Long id, UriInfo uriInfo ) {
        return uriInfo.getBaseUriBuilder()
                .path( "/{id}" )
                .build();
    }
}
