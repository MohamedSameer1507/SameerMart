package com.sameer.sameermart.user;

import java.util.Locale;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegistrationForm form) {
        if (form.getRole() == UserRole.ADMIN) {
            throw new InvalidRegistrationRoleException();
        }
        String normalizedEmail = form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException();
        }

        String passwordHash = passwordEncoder.encode(form.getPassword());
        try {
            userRepository.save(form.getName().trim(), normalizedEmail, passwordHash, form.getRole());
        } catch (DuplicateKeyException exception) {
            // Covers a concurrent registration after the pre-insert availability check.
            throw new DuplicateEmailException();
        }
    }

    public SessionUser login(LoginForm form) {
        String normalizedEmail = form.getEmail().trim().toLowerCase(Locale.ROOT);
        UserRepository.StoredUser user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(form.getPassword(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        return new SessionUser(user.id(), user.name(), user.email(), user.role());
    }
}
