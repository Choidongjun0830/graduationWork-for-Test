//package graduationWork.server.config;
//
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//@RequiredArgsConstructor
//@EnableWebSecurity
//@Configuration
//public class WebSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                        .requestMatchers("/", "/join","/login").permitAll() //루트 경로는 인증 없이 사용
//                        .anyRequest().authenticated() //그 외 모든 요청은 인증 필요
//                )
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login") //인증이 필요한 화면에 접근하면 /login으로 리다이렉트
//                        .loginProcessingUrl("/loginProc")
//                        .defaultSuccessUrl("/")
//                        .failureUrl("/login?error")
//                        .permitAll() //로그인 화면은 모두 접근
//                )
//                .rememberMe(rememberMe -> rememberMe
//                        .key("uniqueAndSecret")); //브라우저를 닫았다 열어도 일정 시간동안 기억
//
//        http
//                .csrf((auth)-> auth.disable());
//
//        http
//                .sessionManagement((auth) -> auth
//                        .sessionFixation().changeSessionId()
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(true));
//
//        return http.build();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
