import com.zk.configuration.auth.util.AlgorithmKeyUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * “{@link Jwts}”的单元测试。
 *
 * @since 1.0
 */
class JwtsTests {

    @Test
    void testKeyToJson() {
        List<KeyPair> keyPairs = new ArrayList<>(9);
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.RS256));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.RS384));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.RS512));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.PS256));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.PS384));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.PS512));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.ES256));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.ES384));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.ES512));

        keyPairs.forEach(keyPair -> {
            byte[] privateKeyByte = keyPair.getPrivate().getEncoded();
            byte[] publicKeyByte = keyPair.getPublic().getEncoded();

            System.out.println("privateKeyByte: " + Arrays.toString(privateKeyByte));
            System.out.println("publicKeyByte: " + Arrays.toString(publicKeyByte));

            Map<String, Object> map = new HashMap<>(4);
            map.put("privateKeyByte", privateKeyByte);
            map.put("publicKeyByte", publicKeyByte);
            map.put("privateKeyBase64", Base64.getEncoder().encodeToString(privateKeyByte));
            map.put("publicKeyBase64", Base64.getEncoder().encodeToString(publicKeyByte));

            String privateKeyBase64 = (String) map.get("privateKeyBase64");
            String publicKeyBase64 = (String) map.get("publicKeyBase64");
            byte[] privateKeyByteConvert = Base64.getDecoder().decode(privateKeyBase64);
            byte[] publicKeyByteConvert = Base64.getDecoder().decode(publicKeyBase64);
            System.out.println("privateKeyByte: " + Arrays.toString(privateKeyByteConvert));
            System.out.println("publicKeyByte: " + Arrays.toString(publicKeyByteConvert));


//            try {
//                System.out.println(JsonUtils.getPrettyJson(map));
//            } catch (JsonProcessingException e) {
//                Assertions.fail();
//            }
        });
    }

    @Test
    void testWithGivenKey() {
        // given Base64 encoded string of RSA512 key
        String privateKeyBase64 = "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";
        String publicKeyBase64 = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAmfwja9Hj5+KoQuZD5u0HDd7HeqB3QgILh49P0ZZ3XwBx8Gjwhs29BMJA9f28WXGMNPBKLfDEJHR4a9El3C4tLhCB1yORskMYtB+gbUaRQ4bPa6WwaIU4XG7ce3n7K9nKnLD6ufQzUJZcVRX0ovMhXa/xDbjMe8m7ZsG2tMu30PThkHvCTQBzzNyLjHs/ycnNdtRwOfMWYsdxTnQFYecxze43RuEZQstlVpIEsRdCLmzkyBgTLGUMlxUUk8LqBNfWbV8JRX6bXzC3RP7LfEltdnW3ePR9MBUNFGonKGC6BJp3njRRnQWViC40k0DFnumJjN42q7w7GAxQjirexPVFttHBNSl9H4erEE+2uZwJFCW7EBOkS+9RciKd/8Nrbjzw1hVysjb45fDqxhMqjymCu4+cWwinhpRnM2G5D3/aSoVQ6rVS0GpwNHIOJP55WS3pE9jixosMxND+0DzUS/CXJM2o9KC7h11Tbx+Mppch+FzTsXZYpqrje+MEPjkCcxN2mOw93qvvNuM4a8RVkM6vzdOf2BM/8XAbgd+nI28jJQibeDea5+ekHqVNEUHfderXh6ux+WcBbRAttFG+8sQa6SPOcCcCMW2MUAjISuuxxtoco8EyclLvSMTjwhvTxRNCznK606Y834bb6mja2svc16h/uVUiz2nu1gNLPS2Ffz8CAwEAAQ==";

        PrivateKey privateKey = null;
        try {
            privateKey = AlgorithmKeyUtils.generateRsaPrivateKey(Base64.getDecoder().decode(privateKeyBase64));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Assertions.fail("创建私钥出错", e);
        }

        String jws = Jwts.builder().setSubject("Bob-RSA-512").signWith(privateKey).compact();

        PublicKey publicKey = null;
        try {
            publicKey = AlgorithmKeyUtils.generateRsaPublicKey(Base64.getDecoder().decode(publicKeyBase64));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Assertions.fail("创建公钥出错", e);
        }
        Jws<Claims> parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-RSA-512", parseClaimsJws.getBody().getSubject());
    }

    @Test
    void testHMACWithSHA() {
        // HMAC SHA-256
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject("Bob-SHA-256").signWith(key).compact();
        Jws<Claims> parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-SHA-256", parseClaimsJws.getBody().getSubject());

        // HMAC SHA-384
        key = Keys.secretKeyFor(SignatureAlgorithm.HS384);
        jws = Jwts.builder().setSubject("Bob-SHA-384").signWith(key).compact();
        parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-SHA-384", parseClaimsJws.getBody().getSubject());

        // HMAC SHA-512
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        jws = Jwts.builder().setSubject("Bob-SHA-512").signWith(key).compact();
        parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-SHA-512", parseClaimsJws.getBody().getSubject());
    }

    @Test
    void testRSA_PKCS() {
        // RSA-256
        KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        String jws = Jwts.builder().setSubject("Bob-RSA-256").signWith(keyPair.getPrivate()).compact();
        Jws<Claims> parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-RSA-256", parseClaimsJws.getBody().getSubject());

        // RSA-384
        keyPair = Keys.keyPairFor(SignatureAlgorithm.RS384);
        jws = Jwts.builder().setSubject("Bob-RSA-384").signWith(keyPair.getPrivate()).compact();
        parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-RSA-384", parseClaimsJws.getBody().getSubject());

        // RSA-512
        keyPair = Keys.keyPairFor(SignatureAlgorithm.RS512);
        jws = Jwts.builder().setSubject("Bob-RSA-512").signWith(keyPair.getPrivate()).compact();
        parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-RSA-512", parseClaimsJws.getBody().getSubject());
    }

    @Test
    void testECDSA() {
        // Elliptic Curve - ES256
        KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.ES256);
        String jws = Jwts.builder().setSubject("Bob-ES-256").signWith(keyPair.getPrivate()).compact();
        Jws<Claims> parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);
        Assertions.assertEquals("Bob-ES-256", parseClaimsJws.getBody().getSubject());

        // Elliptic Curve - ES384
        keyPair = Keys.keyPairFor(SignatureAlgorithm.ES384);
        jws = Jwts.builder().setSubject("Bob-ES-384").signWith(keyPair.getPrivate()).compact();
        parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-ES-384", parseClaimsJws.getBody().getSubject());

        // Elliptic Curve - ES512
        keyPair = Keys.keyPairFor(SignatureAlgorithm.ES512);
        jws = Jwts.builder().setSubject("Bob-ES-512").signWith(keyPair.getPrivate()).compact();
        parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-ES-512", parseClaimsJws.getBody().getSubject());
    }

    @Test
    void testRSA_MGF1() {
        // RSA-256
        KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.PS256);
        String jws = Jwts.builder().setSubject("Bob-RSA-MGF1-256").signWith(keyPair.getPrivate()).compact();
        Jws<Claims> parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-RSA-MGF1-256", parseClaimsJws.getBody().getSubject());

        // RSA-384
        keyPair = Keys.keyPairFor(SignatureAlgorithm.PS384);
        jws = Jwts.builder().setSubject("Bob-RSA-MGF1-384").signWith(keyPair.getPrivate()).compact();
        parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-RSA-MGF1-384", parseClaimsJws.getBody().getSubject());

        // RSA-512
        keyPair = Keys.keyPairFor(SignatureAlgorithm.PS512);
        jws = Jwts.builder().setSubject("Bob-RSA-MGF1-512").signWith(keyPair.getPrivate()).compact();
        parseClaimsJws = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jws);

        Assertions.assertEquals("Bob-RSA-MGF1-512", parseClaimsJws.getBody().getSubject());
    }

    @Test
    void testKeyLength() {
        List<KeyPair> keyPairs = new ArrayList<>(6);
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.RS256));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.RS384));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.RS512));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.PS256));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.PS384));
        keyPairs.add(Keys.keyPairFor(SignatureAlgorithm.PS512));

        final int max = 1000;
        final int maxLength = 4000;
        for (int i = 0; i < max; i++) {
            keyPairs.forEach(keyPair -> {
                String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
                String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
                System.out.println("privateKeyBase64 (" + privateKeyBase64.length() + "): " + privateKeyBase64);
                System.out.println("publicKeyBase64 (" + publicKeyBase64.length() + "): " + publicKeyBase64);
                System.out.println();

                Assertions.assertTrue(privateKeyBase64.length() <= maxLength);
                Assertions.assertTrue(publicKeyBase64.length() <= maxLength);
            });
        }
    }

    @Test
    void testAnything() {
        // 对“超时设置”的分解测试
        String expiration = "12d";
        Assertions.assertEquals(StringUtils.substring(expiration, expiration.length() - 1), "d");
        Assertions.assertEquals(NumberUtils.toLong(StringUtils.substring(expiration, 0, expiration.length() - 1)), 12);

        // 对“签发者”的测试
        Set<String> issuer = new LinkedHashSet<>();
        issuer.add("cloud.k-group.com.cn");
        issuer.add("yibin-credit.scrcu.com");
        issuer.add("192.168.200.33");

        String[] issArray = issuer.toArray(new String[0]);
        String iss = StringUtils.join(issArray, "|");
        Assertions.assertEquals(iss, "cloud.k-group.com.cn|yibin-credit.scrcu.com|192.168.200.33");
    }
}