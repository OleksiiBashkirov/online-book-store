//package mate.academy.config;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class JwtConfig {
//
//    @Value("${jwt.secret}")
//    private String secret;
//
//    @Bean
//    public SecretKey secretKey() {
//        byte[] keyBytes = secret.getBytes();
//        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
//    }
//}
