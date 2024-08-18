package graduationWork.server.security;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class PasswordEncoder {

    public String encode(String loginId, String password) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), getSalt(loginId), 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean matches(String loginId, String rawPassword, String encodedPassword) {
        String encodedRawPassword = encode(loginId, rawPassword);
        return encodedRawPassword.equals(encodedPassword);
    }

    //비밀번호로 salt 값 생성하면 같은 비밀번호를 사용하는 유저끼리 같은 값을 가지게 됨. loginId는 모두 다르니까 loginId로 salt값 생성
    private byte[] getSalt(String loginId) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] keyBytes = loginId.getBytes("UTF-8");

        return digest.digest(keyBytes);
    }
}
