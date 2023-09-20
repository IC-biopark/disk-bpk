package com.biopark.disk_bpk.service;

import com.biopark.disk_bpk.domain.Turma;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.model.TurmaDTO;
import com.biopark.disk_bpk.repos.TurmaRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.util.NotFoundException;
import com.biopark.disk_bpk.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;

    public TurmaService(final TurmaRepository turmaRepository,
            final UsuarioRepository usuarioRepository) {
        this.turmaRepository = turmaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<TurmaDTO> findAll() {
        final List<Turma> turmas = turmaRepository.findAll(Sort.by("id"));
        return turmas.stream()
                .map(turma -> mapToDTO(turma, new TurmaDTO()))
                .toList();
    }

    public TurmaDTO get(final Long id) {
        return turmaRepository.findById(id)
                .map(turma -> mapToDTO(turma, new TurmaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TurmaDTO turmaDTO) {
        final Turma turma = new Turma();
        mapToEntity(turmaDTO, turma);
        return turmaRepository.save(turma).getId();
    }

    public void update(final Long id, final TurmaDTO turmaDTO) {
        final Turma turma = turmaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(turmaDTO, turma);
        turmaRepository.save(turma);
    }

    public void delete(final Long id) {
        final Turma turma = turmaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        usuarioRepository.findAllByTurmaList(turma)
                .forEach(usuario -> usuario.getTurmaList().remove(turma));
        turmaRepository.delete(turma);
    }

    private TurmaDTO mapToDTO(final Turma turma, final TurmaDTO turmaDTO) {
        turmaDTO.setId(turma.getId());
        turmaDTO.setNome(turma.getNome());
        turmaDTO.setPeriodo(turma.getPeriodo());
        return turmaDTO;
    }

    private Turma mapToEntity(final TurmaDTO turmaDTO, final Turma turma) {
        turma.setNome(turmaDTO.getNome());
        turma.setPeriodo(turmaDTO.getPeriodo());
        return turma;
    }

    public String getReferencedWarning(final Long id) {
        final Turma turma = turmaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Usuario turmaListUsuario = usuarioRepository.findFirstByTurmaList(turma);
        if (turmaListUsuario != null) {
            return WebUtils.getMessage("turma.usuario.turmaList.referenced", turmaListUsuario.getId());
        }
        return null;
    }

}
