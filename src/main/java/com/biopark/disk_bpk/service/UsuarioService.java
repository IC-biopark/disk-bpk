package com.biopark.disk_bpk.service;

import com.biopark.disk_bpk.domain.Avaliacao;
import com.biopark.disk_bpk.domain.Cargo;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.model.UsuarioDTO;
import com.biopark.disk_bpk.repos.AvaliacaoRepository;
import com.biopark.disk_bpk.repos.CargoRepository;
import com.biopark.disk_bpk.repos.TurmaRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.util.NotFoundException;
import com.biopark.disk_bpk.util.WebUtils;

import jakarta.transaction.Transactional;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CargoRepository cargoRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public UsuarioService(final UsuarioRepository usuarioRepository,
            final TurmaRepository turmaRepository, final CargoRepository cargoRepository, AvaliacaoRepository avaliacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cargoRepository = cargoRepository;
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

    /**
     * Cria um novo usuário e o salva no banco de dados.
     *
     * @param usuarioDTO O objeto UsuarioDTO contendo os detalhes do usuário a ser
     *                   criado.
     * @return Um valor Long representando o ID do usuário criado.
     */
    public Long create(final UsuarioDTO usuarioDTO) {

        // Encontra um usuário existente pelo email
        Usuario usuarioExistente = usuarioRepository.findByEmail(usuarioDTO.getEmail());
        if (usuarioExistente != null)
            throw new RuntimeException("Email já cadastrado"); // Lança uma exceção se o email já estiver cadastrado

        // Cria um novo objeto de usuário
        final Usuario usuario = new Usuario();
        mapToEntity(usuarioDTO, usuario);

        // Criptografa a senha do usuário
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Salva o usuário no repositório e retorna o ID do usuário
        return usuarioRepository.save(usuario).getId();

    }

    public void update(final Long id, final UsuarioDTO usuarioDTO) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(usuarioDTO, usuario);
        usuarioRepository.save(usuario);
    }

    public void delete(final Long id) {
        usuarioRepository.deleteById(id);
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
        return usuarioDTO;
    }

    private Usuario mapToEntity(final UsuarioDTO usuarioDTO, final Usuario usuario) {
        Cargo cargo = cargoRepository.findById(usuarioDTO.getCargo()).orElseThrow();
        usuario.setCargo(cargo);
        BeanUtils.copyProperties(usuarioDTO, usuario);
        return usuario;
    }

    public Usuario buscarUsuario(String login) throws Exception {
        try {
            return usuarioRepository.findByEmail(login);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar usuário");
        }
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
