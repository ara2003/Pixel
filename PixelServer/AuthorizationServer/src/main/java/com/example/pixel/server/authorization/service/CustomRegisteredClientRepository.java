package com.example.pixel.server.authorization.service;

import com.example.pixel.server.authorization.entity.AuthClient;
import com.example.pixel.server.authorization.exception.ClientNotFoundException;
import com.example.pixel.server.authorization.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientRepository clientRepository;

    private final TokenSettings tokenSettings;

    private final ClientSettings clientSettings;

    @Override
    public void save(RegisteredClient registeredClient) {
        throw new IllegalArgumentException("save(" + registeredClient + ")");
    }

    @Override
    public RegisteredClient findById(String id) {
        throw new IllegalArgumentException("findById(" + id + ")");
    }

    @Transactional
    @Override
    public RegisteredClient findByClientId(String clientId) {
        var client = clientRepository.findByClientId(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));
        return toRegisteredClient(client);
    }

    private RegisteredClient toRegisteredClient(AuthClient client) {
        var builder = RegisteredClient.withId("" + client.getId())
                .clientName(client.getClientName())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .clientSettings(clientSettings)
                .tokenSettings(tokenSettings);
        for (var scope : client.getScopes())
            builder.scope(scope.getName());
        for (var scope : client.getRedirectUris())
            builder.redirectUri(scope.getName());
        return builder.build();
    }

}
