package dev.tomas.tfg.rest.user.repository;

import dev.tomas.tfg.rest.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByNombre(String nombre);
    Optional<User> findByEmail(String email);
}
