package com.example.controlecontas.enums;

public enum TipoPagamentoEnum {
    CARTAO("1", "CARTÃO"),
    PIX("2", "PIX"),
    FATURA("3", "FATURA");

    private final String codigoTipoPagamento;
    private final String descricao;

    TipoPagamentoEnum(String codigoTipoPagamento, String descricao) {
        this.codigoTipoPagamento = codigoTipoPagamento;
        this.descricao = descricao;
    }

    public static String getCodidoPorDescricao(String formaPagamento) {
        for (TipoPagamentoEnum tipoPagamentoEnum : TipoPagamentoEnum.values()) {
            if (formaPagamento.equals(tipoPagamentoEnum.getDescricao())) {
                return tipoPagamentoEnum.getCodigoTipoPagamento();
            }
        }

        if (formaPagamento.equals("CRÉDITO") || formaPagamento.equals("DÉBITO")) {
            return CARTAO.getCodigoTipoPagamento();
        }

        return null;
    }

    public String getCodigoTipoPagamento() {
        return codigoTipoPagamento;
    }

    public String getDescricao() {
        return descricao;
    }
}
