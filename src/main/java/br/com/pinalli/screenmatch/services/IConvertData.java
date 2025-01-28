package br.com.pinalli.screenmatch.services;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IConvertData {
    <T> T getData(String json, Class<T> c) throws JsonProcessingException;
}
