package br.edu.fiap.banco.entity;

import java.math.BigDecimal;

public record ContaBancaria(
        Long id,
        String agencia,
        String numero,
        BigDecimal saldo,
        boolean ativa,
        Pessoa titular,
        TipoConta tipoConta
) {
    public ContaBancaria{
        if( agencia == null || !agencia.matches( "\\d{4}") ) {
            throw new IllegalArgumentException( "Agência deve ter 4 números." );
        }
          if( numero == null || !numero.matches( "\\d{6}-\\d") ) {
            throw new IllegalArgumentException( "Número conta deve seguir padrao 000000-0." );
        }
          if( saldo == null || saldo.signum() < 0 ) {
            throw new IllegalArgumentException( "Saldo não pode ser nulo ou negativo" );
        }
          if( titular == null || tipoConta == null ) {
            throw new IllegalArgumentException( "Titular e tipo de conta são obrigatórios." );
        }
    }

    //POST
    public static ContaBancaria nova( String agencia, String numero, Pessoa titular, TipoConta tipoConta ) {
        return new ContaBancaria(
                null,
                agencia,
                numero,
                BigDecimal.ZERO,
                true,
                titular,
                tipoConta
        );
    }

    //GETBYID
    public ContaBancaria comId( Long novoId ) {
        return new ContaBancaria( novoId, agencia, numero, saldo, ativa, titular, tipoConta );
    }

    //PUT
    public ContaBancaria atualizar( String novaAgencia, String novoNumero, TipoConta novoTipoConta ) {
        return new ContaBancaria( id, novaAgencia, novoNumero, saldo, ativa, titular, novoTipoConta );
    }

    //PUT (soft delete)
    public ContaBancaria encerrar() {
        return new ContaBancaria( id, agencia, numero, saldo, false, titular, tipoConta );
    }
}
