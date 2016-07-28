package ca.sfu.teambeta.accounts;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * The instantiated class handles the generation of unique, random and secure tokens.
 *
 */

public class TokenGenerator {
    private List<String> existingTokens;


    // MARK: Constructors
    public TokenGenerator() {
        existingTokens = new ArrayList<>();
    }

    public TokenGenerator(List<String> existingTokenList) {
        existingTokens = existingTokenList;
    }


    // MARK: The Core Token Interaction Methods
    public String generateUniqueRandomToken() {
        String token = generateRandomToken();

        while (existingTokens.contains(token)) {
            token = generateRandomToken();
        }

        existingTokens.add(token);

        return token;
    }

    public boolean removeTokenFromList(String token) {
        return existingTokens.remove(token);
    }


    // MARK: Token Generation Method
    private String generateRandomToken() {
        // See citations.txt for more information

        // DO NOT CHANGE THESE VALUES
        final int MAX_BIT_LENGTH = 130;
        final int ENCODING_BASE = 32;

        SecureRandom random = new SecureRandom();
        String token = new BigInteger(MAX_BIT_LENGTH, random).toString(ENCODING_BASE);

        return token;
    }
}
