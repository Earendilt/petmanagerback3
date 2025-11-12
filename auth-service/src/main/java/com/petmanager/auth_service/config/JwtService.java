package com.petmanager.auth_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.private-key:}")
    private String privateKeyString;

    @Value("${jwt.public-key:}")
    private String publicKeyString;

    @Value("${jwt.expiration:60000}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token.expiration:604800000}")
    private long refreshExpiration;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        // Check if keys are provided and not empty
        if (privateKeyString == null || privateKeyString.trim().isEmpty() ||
            publicKeyString == null || publicKeyString.trim().isEmpty()) {
            System.out.println("WARNING: JWT keys not provided. Generating temporary keys...");
            System.out.println("For production, set JWT_PRIVATE_KEY and JWT_PUBLIC_KEY environment variables!");
            generateKeyPair();
        } else {
            loadKeys();
        }
    }

    private void generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();

        System.out.println("Generated RSA key pair for JWT signing");
        System.out.println("Public Key (base64): " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }

    private void loadKeys() throws Exception {
        System.out.println("Loading static keys from configuration...");

        try {
            // Sanitize the private key
            // Handle both escaped (\n) and actual newlines, remove PEM headers
            String privateKeyPEM = sanitizePemKey(privateKeyString);
            String publicKeyPEM = sanitizePemKey(publicKeyString);

            // Decode from Base64
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);

            // Generate keys
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            System.out.println("Successfully loaded static RSA keys");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load JWT keys: " + e.getMessage());
            System.err.println("Private key length: " + privateKeyString.length());
            System.err.println("Public key length: " + publicKeyString.length());
            throw new RuntimeException("Failed to load JWT keys. Ensure they are in proper PEM format.", e);
        }
    }

    private String sanitizePemKey(String pemKey) {
        return pemKey
                // Remove PEM headers and footers
                .replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                // Remove escaped newlines (from properties files)
                .replace("\\n", "")
                // Remove actual newlines (from multiline env vars)
                .replace("\n", "")
                .replace("\r", "")
                // Remove escaped equals signs
                .replace("\\=", "=")
                // Remove all whitespace
                .replaceAll("\\s+", "")
                .trim();
    }

    public String getPublicKeyAsString() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
