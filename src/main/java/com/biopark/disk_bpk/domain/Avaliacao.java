package com.biopark.disk_bpk.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Avaliacao {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column(nullable = false, length = 50)
    private String titulo;

    @Column(nullable = false, length = 100)
    private String descricao;

    @ManyToMany
    @NotNull
    @JoinTable(
            name = "avaliacao_pergunta",
            joinColumns = @JoinColumn(name = "avaliacao_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "pergunta_id", nullable = false)
    )
    private Set<Pergunta> perguntaList;

    @ManyToMany
    @JoinTable(
            name = "avaliacoes_finalizadas_usuario",
            joinColumns = @JoinColumn(name = "avaliacao_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> usuariosQueFinalizaram;

    @ManyToMany
    @NotNull
    @JoinTable(
            name = "avaliacao_turma",
            joinColumns = @JoinColumn(name = "avaliacao_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "turma_id", nullable = false)
    )
    private List<Turma> turmaList;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
