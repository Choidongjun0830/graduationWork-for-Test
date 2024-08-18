package graduationWork.server.service;

import graduationWork.server.domain.Address;
import graduationWork.server.domain.User;
import graduationWork.server.dto.PasswordUpdateForm;
import graduationWork.server.repository.UserRepository;
import graduationWork.server.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(User user) {

        //중복 회원 검증
        boolean IsAlreadyExists = userRepository.existsByUsername(user.getUsername());
        if (IsAlreadyExists) {
            return 0L; //에러 처리.
        }

        user.setPassword(passwordEncoder.encode(user.getLoginId(), user.getPassword()));
        user.setRole("ROLE_USER"); //ADMIN은 그냥 DB에 넣어두기
        user.setJoinDate(LocalDate.now());

        userRepository.save(user);
        return user.getId();
    }

    public User findOne(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    @Transactional
    public boolean updatePassword(Long userId, PasswordUpdateForm passwordUpdateForm) {

        User user = userRepository.findById(userId);
        String loginId = user.getLoginId();

        if(!passwordEncoder.matches(loginId, passwordUpdateForm.getCurrentPassword(), user.getPassword())) {
            return false;
        }

        if(!passwordUpdateForm.getNewPassword().equals(passwordUpdateForm.getNewPasswordConfirm())) {
            return false;
        }

        String encodedNewPassword = passwordEncoder.encode(loginId, passwordUpdateForm.getNewPassword());
        user.setPassword(encodedNewPassword);

        return true;
    }

    @Transactional
    public void updateAddress(Long userId, Address address) {

        User user = userRepository.findById(userId);
        user.setAddress(address);
    }

    @Transactional
    public void updateWalletAddress(Long userId, String walletAddress) {
        User user = userRepository.findById(userId);
        user.setWalletAddress(walletAddress);
    }

    public Boolean checkLoginIdUnique(String loginId) {
        return userRepository.existsByLoginId(loginId); //존재하면 true
    }
}
