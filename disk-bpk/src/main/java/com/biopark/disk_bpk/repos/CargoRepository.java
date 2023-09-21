package com.biopark.disk_bpk.repos;

import com.biopark.disk_bpk.domain.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CargoRepository extends JpaRepository<Cargo, Long> {
}
