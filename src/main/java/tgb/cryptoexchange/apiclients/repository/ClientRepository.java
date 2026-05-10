package tgb.cryptoexchange.apiclients.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tgb.cryptoexchange.apiclients.entity.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {

    boolean existsByUsername(String username);

    Optional<Client> findByApiKey(String apiKey);
}
