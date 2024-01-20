package br.com.alura.screenmatch.service;

public interface DataConversionInterface {
    <T> T getData(String json, Class<T> tClass);
}
