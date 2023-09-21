package com.biopark.disk_bpk.service;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Cargo;
import com.biopark.disk_bpk.domain.Resposta;
import com.biopark.disk_bpk.domain.Turma;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.model.UsuarioDTO;
import com.biopark.disk_bpk.repos.AvaliacaoRepository;
import com.biopark.disk_bpk.repos.CargoRepository;
import com.biopark.disk_bpk.repos.RespostaRepository;
import com.biopark.disk_bpk.repos.TurmaRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.util.NotFoundException;
import com.biopark.disk_bpk.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TurmaRepository turmaRepository;
    private final CargoRepository cargoRepository;
    private final RespostaRepository respostaRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public UsuarioService(final UsuarioRepository usuarioRepository,
            final TurmaRepository turmaRepository, final CargoRepository cargoRepository,
            final RespostaRepository respostaRepository,
            final AvaliacaoRepository avaliacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.turmaRepository = turmaRepository;
        this.cargoRepository = cargoRepository;
        this.respostaRepository = respostaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public List<UsuarioDTO> findAll() {
        final List<Usuario> usuarios = usuarioRepository.findAll(Sort.by("id"));
        return usuarios.stream()
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .toList();
    }

    public UsuarioDTO get(final Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UsuarioDTO usuarioDTO) {
        final Usuario usuario = new Usuario();
        mapToEntity(usuarioDTO, usuario);
        return usuarioRepository.save(usuario).getId();
    }

    public void update(final Long id, final UsuarioDTO usuarioDTO) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(usuarioDTO, usuario);
        usuarioRepository.save(usuario);
    }

    public void delete(final Long id) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        avaliacaoRepository.findAllByUsuariosQueFinalizaram(usuario)
                .forEach(avaliacao -> avaliacao.getUsuariosQueFinalizaram().remove(usuario));
        usuarioRepository.delete(usuario);
    }

    private UsuarioDTO mapToDTO(final Usuario usuario, final UsuarioDTO usuarioDTO) {
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setNome(usuario.getNome());
        usuarioDTO.setSegundoNome(usuario.getSegundoNome());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setSenha(usuario.getSenha());
        usuarioDTO.setTurmaList(usuario.getTurmaList().stream()
                .map(turma -> turma.getId())
                .toList());
        usuarioDTO.setCargo(usuario.getCargo() == null ? null : usuario.getCargo().getId());
        usuarioDTO.setRespostaList(usuario.getRespostaList() == null ? null : usuario.getRespostaList().getId());
        return usuarioDTO;
    }

    private Usuario mapToEntity(final UsuarioDTO usuarioDTO, final Usuario usuario) {
        usuario.setNome(usuarioDTO.getNome());
        usuario.setSegundoNome(usuarioDTO.getSegundoNome());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(usuarioDTO.getSenha());
        final List<Turma> turmaList = turmaRepository.findAllById(
                usuarioDTO.getTurmaList() == null ? Collections.emptyList() : usuarioDTO.getTurmaList());
        if (turmaList.size() != (usuarioDTO.getTurmaList() == null ? 0 : usuarioDTO.getTurmaList().size())) {
            throw new NotFoundException("one of turmaList not found");
        }
        usuario.setTurmaList(turmaList.stream().collect(Collectors.toSet()));
        final Cargo cargo = usuarioDTO.getCargo() == null ? null : cargoRepository.findById(usuarioDTO.getCargo())
                .orElseThrow(() -> new NotFoundException("cargo not found"));
        usuario.setCargo(cargo);
        final Resposta respostaList = usuarioDTO.getRespostaList() == null ? null : respostaRepository.findById(usuarioDTO.getRespostaList())
                .orElseThrow(() -> new NotFoundException("respostaList not found"));
        usuario.setRespostaList(respostaList);
        return usuario;
    }

    public String getReferencedWarning(final Long id) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Avaliacao usuariosQueFinalizaramAvaliacao = avaliacaoRepository.findFirstByUsuariosQueFinalizaram(usuario);
        if (usuariosQueFinalizaramAvaliacao != null) {
            return WebUtils.getMessage("usuario.avaliacao.usuariosQueFinalizaram.referenced", usuariosQueFinalizaramAvaliacao.getId());
        }
        return null;
    }

}
