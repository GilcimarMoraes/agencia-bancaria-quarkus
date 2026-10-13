package br.edu.fiap.banco.controller;

import br.edu.fiap.banco.controller.mapper.GenericController;
import br.edu.fiap.banco.dto.TipoContaRequest;
import br.edu.fiap.banco.dto.TipoContaResponse;
import br.edu.fiap.banco.service.TipoContaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;

@Path( "/api/tipo-conta" )
@Consumes( MediaType.APPLICATION_JSON )
@Produces( MediaType.APPLICATION_JSON )
@Tag( name = "TiposConta" )
public class TipoContaResource implements GenericController {

    private final TipoContaService service;

    @Inject
    public TipoContaResource(TipoContaService service) {
        this.service = service;
    }

    @POST
    @Operation( summary = "Cadastrar um novo tipo de conta" )
    public Response cadastrar(@NotNull @Valid TipoContaRequest request, @Context UriInfo uriInfo ) {
        TipoContaResponse response = service.cadastrar( request );

        URI location = gerarHeaderLocation( response.id(), uriInfo );

        return Response.created( location )
                .entity( response )
                .build();
    }

    @GET
    @Path( "/{id}" )
    public TipoContaResponse getTipoContaById( @PathParam( "id" ) Long id ) {
        TipoContaResponse response = service.getTipoContaById( id );

        return response;
    }

    @PUT
    @Path( "/{id}" )
    public TipoContaResponse atualizar( @PathParam( "id" ) Long id, @NotNull @Valid TipoContaRequest request ) {
        TipoContaResponse response = service.atualizar( id, request );
        return response;
    }

    @DELETE
    @Path( "/{id}" )
    public Response excluir( @PathParam( "id" ) Long id ) {
        service.excluir( id );
        return Response.noContent().build();
    }
}
