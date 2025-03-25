package com.dmf.marketplace.compartilhado.seguranca.google;

public class GoogleCodeDto {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "GoogleCodeDto{" +
                "code='" + code + '\'' +
                '}';
    }
}