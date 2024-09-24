package graduationWork.server.init;

import graduationWork.server.domain.User;
import graduationWork.server.security.PasswordEncoder;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InitUsers {

    private final InitUserService initUserService;

//    @PostConstruct
//    public void init() {
//        initUserService.dbInit1();
//        initUserService.dbInit2();
//        initUserService.dbInit3();
//    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitUserService {

        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        public void dbInit1() {
            User user = new User();
            user.setUsername("관리자");
            user.setLoginId("adong08");
            user.setPassword(passwordEncoder.encode("adong08","1111"));
            user.setRole("ROLE_ADMIN");
            user.setEmail("dsada1234@naver.com");
            user.setJoinDate(LocalDate.now());
            user.setWalletAddress("0x2F420eE5487923Da799D057Ee3BbFc631ba86cd7");
            em.persist(user);
        }

        public void dbInit2() {
            User user = new User();
            user.setUsername("최동준");
            user.setLoginId("adong0808");
            user.setPassword(passwordEncoder.encode("adong0808","1111"));
            user.setRole("ROLE_USER");
            user.setEmail("adong0808@naver.com");
            user.setJoinDate(LocalDate.now());
            user.setWalletAddress("0xED0313FdC8A0cd7c36f5beE689455c6b95742Ed8");
            em.persist(user);
        }

        public void dbInit3() {
            User user = new User();
            user.setUsername("이재우");
            user.setLoginId("jwlee2357");
            user.setPassword(passwordEncoder.encode("jwlee2357","1111"));
            user.setRole("ROLE_USER");
            user.setEmail("rink2357@soongsil.ac.kr");
            user.setJoinDate(LocalDate.now());
            user.setWalletAddress("0x2F420eE5487923Da799D057Ee3BbFc631ba86cd7");
            em.persist(user);
        }
    }
}
