package org.openmrs.module.sync2.client.rest;

import java.util.Arrays;
import java.util.Collections;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestClient implements Client {

    private RestTemplate restTemplate = new RestTemplate();

    public RestClient() {
        restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]
                { new StringHttpMessageConverter(), new SimpleObjectMessageConverter() }));
    }

    @Override
    public Object retrieveObject(String category, String url, String username, String password)
            throws RestClientException {
        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthInterceptor(username, password)));
        SimpleObject restResource = restTemplate.getForObject(url, SimpleObject.class);
    
        return restResource;
    }
    
    @Override
    public ResponseEntity<String> createObject(String url, String username, String password, Object object)
            throws RestClientException {
        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthInterceptor(username, password)));
        return restTemplate.postForEntity(url, object, String.class);
    }

    @Override
    public ResponseEntity<String> deleteObject(String url, String username, String password, String uuid) {
        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthInterceptor(username, password)));
        url += "/" + uuid;
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<Object>(uuid), String.class);
    }

    @Override
    public ResponseEntity<String> updateObject(String url, String username, String password, Object object) {
        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthInterceptor(username, password)));

        url += "/" + ((SimpleObject) object).get("uuid");
        return restTemplate.postForEntity(url, object, String.class);
    }
}
