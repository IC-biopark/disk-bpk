package com.biopark.disk_bpk.service;

import com.biopark.disk_bpk.domain.Cargo;
import com.biopark.disk_bpk.domain.Usuario;
import com.biopark.disk_bpk.model.CargoDTO;
import com.biopark.disk_bpk.repos.CargoRepository;
import com.biopark.disk_bpk.repos.UsuarioRepository;
import com.biopark.disk_bpk.util.NotFoundException;
import com.biopark.disk_bpk.util.WebUtils;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CargoService {

    private final CargoRepository cargoRepository;
    private final UsuarioRepository usuarioRepository;

    public CargoService(final CargoRepository cargoRepository,
            final UsuarioRepository usuarioRepository) {
        this.cargoRepository = cargoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<CargoDTO> findAll() {
        final List<Cargo> cargos = cargoRepository.findAll(Sort.by("id"));
        return cargos.stream()
                .map(cargo -> mapToDTO(cargo, new CargoDTO()))
                .toList();
    }

    public CargoDTO get(final Long id) {
        return cargoRepository.findById(id)
                .map(cargo -> mapToDTO(cargo, new CargoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CargoDTO cargoDTO) {
        final Cargo cargo = new Cargo();
        mapToEntity(cargoDTO, cargo);
        return cargoRepository.save(cargo).getId();
    }

    public void update(final Long id, final CargoDTO cargoDTO) {
        final Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(cargoDTO, cargo);
        cargoRepository.save(cargo);
    }

    public void delete(final Long id) {
        cargoRepository.deleteById(id);
    }

    private CargoDTO mapToDTO(final Cargo cargo, final CargoDTO cargoDTO) {
        cargoDTO.setId(cargo.getId());
        cargoDTO.setNome(cargo.getNome());
        return cargoDTO;
    }

    private Cargo mapToEntity(final CargoDTO cargoDTO, final Cargo cargo) {
        cargo.setNome(cargoDTO.getNome());
        return cargo;
    }

    public String getReferencedWarning(final Long id) {
        final Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Usuario cargoUsuario = usuarioRepository.findFirstByCargo(cargo);
        if (cargoUsuario != null) {
            return WebUtils.getMessage("cargo.usuario.cargo.referenced", cargoUsuario.getId());
        }
        return null;
    }

}
