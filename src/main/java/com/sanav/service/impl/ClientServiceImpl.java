package com.sanav.service.impl;

import com.sanav.entity.Client;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.ClientRepository;
import com.sanav.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client updateClient(Long id, Client client) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        existing.setName(client.getName());
        existing.setLogoUrl(client.getLogoUrl());
        existing.setReview(client.getReview());
        return clientRepository.save(existing);
    }

    @Override
    public void deleteClient(Long id) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        clientRepository.delete(existing);
    }
}
