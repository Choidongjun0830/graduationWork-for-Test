package graduationWork.server.service;

import graduationWork.server.domain.User;
import graduationWork.server.repository.UserRepository;
import graduationWork.server.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(String loginId, String password) {
        User findUser = userRepository.findByLoginId(loginId);
        String savedPassword = findUser.getPassword();
        if (findUser != null && passwordEncoder.matches(loginId, password, savedPassword)) {
            return findUser;
        }else{
            return null;
        }
    }


}
