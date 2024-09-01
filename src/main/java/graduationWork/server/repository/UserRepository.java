package graduationWork.server.repository;

import graduationWork.server.domain.User;
import graduationWork.server.security.PasswordEncoder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;

    public Long save(User user) {
        em.persist(user);
        return user.getId();
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public List<User> findAll() {
        return em.createQuery("select u from User u", User.class).getResultList();
    }

    public List<User> findByUsername(String username) {
        return em.createQuery("select u from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();
    }

    public User findByLoginId(String loginId) {
        List<User> resultList = em.createQuery("select u from User u where u.loginId = :loginId", User.class)
                .setParameter("loginId", loginId)
                .getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    public boolean existsByUsername(String username) {
        List<User> findByNameUsers = this.findByUsername(username);
        if (!findByNameUsers.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean existsByLoginId(String loginId) {
        User findUser = findByLoginId(loginId);
        return findUser != null;
    }

    public User findAdmin() {
        return em.createQuery("select u from User u where u.role = :role", User.class)
                .setParameter("role", "ROLE_ADMIN")
                .getSingleResult();
    }
}
