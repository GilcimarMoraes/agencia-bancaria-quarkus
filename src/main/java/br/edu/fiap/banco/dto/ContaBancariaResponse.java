package br.edu.fiap.banco.dto;

import br.edu.fiap.banco.entity.ContaBancaria;

import java.math.BigDecimal;

public record ContaBancariaResponse(
        Long id,
        String agencia,
        String numero,
        BigDecimal saldo,
        boolean ativa,
        String titularNome,
        Long TipoContaId,
        String TipoContaNome
) {
    public static ContaBancariaResponse de( ContaBancaria contaBancaria) {
        return new ContaBancariaResponse(
                contaBancaria.id(),
                contaBancaria.agencia(),
                contaBancaria.numero(),
                contaBancaria.saldo(),
                contaBancaria.ativa(),
                contaBancaria.titular().nome(),
                contaBancaria.tipoConta().id(),
                contaBancaria.tipoConta().nome()
        );
    }
}
