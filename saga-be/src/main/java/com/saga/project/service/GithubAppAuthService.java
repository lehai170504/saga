package com.saga.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Date;

@Service
public class GithubAppAuthService {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Value("${app.github.app-id:}")
    private String appId;

    @Value("${app.github.private-key:}")
    private String privateKeyString;

    @Value("${app.github.private-key-path:}")
    private String privateKeyPath;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateJwt() throws Exception {
        if (appId == null || appId.isEmpty()) {
            throw new IllegalStateException("Missing GitHub App ID configuration");
        }

        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 600000; // 10 minutes maximum

        PrivateKey privateKey;
        if (privateKeyString != null && !privateKeyString.isEmpty()) {
            privateKey = getPrivateKeyFromString(privateKeyString);
        } else if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
            privateKey = getPrivateKeyFromFile(privateKeyPath);
        } else {
            throw new IllegalStateException("Missing GitHub Private Key configuration");
        }

        return Jwts.builder()
                .issuer(appId)
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(expMillis))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private PrivateKey getPrivateKeyFromFile(String filename) throws Exception {
        try (FileReader keyReader = new FileReader(filename)) {
            return parsePrivateKey(keyReader);
        }
    }

        private PrivateKey getPrivateKeyFromString(String keyString) throws Exception {
        keyString = keyString.replace("\\n", "\n");
        try (java.io.StringReader keyReader = new java.io.StringReader(keyString)) {
            return parsePrivateKey(keyReader);
        }
    }

    private PrivateKey parsePrivateKey(java.io.Reader reader) throws Exception {
        try (PEMParser pemParser = new PEMParser(reader)) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
            
            if (object instanceof PEMKeyPair) {
                return converter.getPrivateKey(((PEMKeyPair) object).getPrivateKeyInfo());
            } else if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            } else {
                throw new IllegalArgumentException("Unknown key format in file");
            }
        }
    }

    public String getInstallationAccessToken(String installationId) throws Exception {
        String jwt = generateJwt();
        String url = "https://api.github.com/app/installations/" + installationId + "/access_tokens";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        
        JsonNode node = objectMapper.readTree(response.getBody());
        return node.get("token").asText();
    }
}
