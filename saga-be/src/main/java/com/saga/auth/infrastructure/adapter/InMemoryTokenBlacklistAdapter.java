package com.saga.auth.infrastructure.adapter;

import com.saga.auth.application.port.TokenBlacklistPort;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryTokenBlacklistAdapter implements TokenBlacklistPort {
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    @Override
    public void blacklistToken(String token) {
        blacklist.add(token);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}