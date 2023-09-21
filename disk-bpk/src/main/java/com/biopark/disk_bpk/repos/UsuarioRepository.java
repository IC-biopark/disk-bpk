package com.biopark.disk_bpk.repos;

import com.biopark.disk_bpk.domain.Cargo;
import com.biopark.disk_bpk.domain.Resposta;
import com.biopark.disk_bpk.domain.Turma;
import com.biopark.disk_bpk.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByTurmaList(Turma turma);

    Usuario findFirstByTurmaList(Turma turma);

    Usuario findFirstByCargo(Cargo cargo);

    Usuario findFirstByRespostaList(Resposta resposta);

}
