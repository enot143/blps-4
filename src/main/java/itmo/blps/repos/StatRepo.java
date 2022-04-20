package itmo.blps.repos;


import itmo.blps.domain.Stat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatRepo extends JpaRepository<Stat, Long> {

}