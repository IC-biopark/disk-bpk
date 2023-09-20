package com.biopark.disk_bpk.repos;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findAllByUsuariosQueFinalizaram(Usuario usuario);

    Avaliacao findFirstByUsuariosQueFinalizaram(Usuario usuario);

    List<Avaliacao> findAllByPerguntaList(Pergunta pergunta);

    Avaliacao findFirstByPerguntaList(Pergunta pergunta);

}
