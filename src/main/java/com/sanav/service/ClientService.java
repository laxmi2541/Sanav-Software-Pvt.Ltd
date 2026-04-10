package com.sanav.service;

import com.sanav.entity.Client;
import java.util.List;

public interface ClientService {
    List<Client> getAllClients();
    Client createClient(Client client);
    Client updateClient(Long id, Client client);
    void deleteClient(Long id);
}
