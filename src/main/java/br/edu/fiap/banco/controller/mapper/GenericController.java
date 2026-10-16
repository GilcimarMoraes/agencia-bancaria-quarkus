package br.edu.fiap.banco.controller.mapper;

import br.edu.fiap.banco.controller.TipoContaResource;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public interface GenericController {

    default URI gerarHeaderLocation(Long id, UriInfo uriInfo, Class<?> resource ) {
        return uriInfo.getBaseUriBuilder()
                .path( resource )
                .path( "/{id}" )
                .build( id );
    }
}
