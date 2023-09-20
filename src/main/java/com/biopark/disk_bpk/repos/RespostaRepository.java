package com.biopark.disk_bpk.repos;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Pergunta;
import com.biopark.disk_bpk.domain.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    Resposta findFirstByAvaliacao(Avaliacao avaliacao);

    Resposta findFirstByPergunta(Pergunta pergunta);

}
