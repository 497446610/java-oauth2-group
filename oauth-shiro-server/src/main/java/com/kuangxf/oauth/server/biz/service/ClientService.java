package com.kuangxf.oauth.server.biz.service;

import java.util.List;

import com.kuangxf.oauth.server.biz.domain.Client;


public interface ClientService {

    public Client createClient(Client client);
    public Client updateClient(Client client);
    public void deleteClient(Long clientId);

    Client findOne(Long clientId);

    List<Client> findAll();

    Client findByClientId(String clientId);
    Client findByClientSecret(String clientSecret);

}
