package com.biopark.disk_bpk.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Usuario implements UserDetails {

        @Id
        @Column(nullable = false, updatable = false)
        @SequenceGenerator(name = "primary_sequence", sequenceName = "primary_sequence", allocationSize = 1, initialValue = 10000)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_sequence")
        private Long id;

        @Column(nullable = false, length = 50)
        private String nome;

        @Column(nullable = false, length = 70)
        private String segundoNome;

        @Column(nullable = false, length = 50)
        private String email;

        @Column(nullable = false, length = 255)
        private String senha;

        @ManyToMany
        @JoinTable(name = "usuario_turma", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "turma_id"))
        private Set<Turma> turmaList;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cargo_id", nullable = false)
        private Cargo cargo;

        @ManyToMany(mappedBy = "usuariosQueFinalizaram")
        private List<Avaliacao> avaliacoesFinalizadas;

        @OneToMany(mappedBy = "usuario")
        private List<Resposta> respostaList;

        @CreatedDate
        @Column(nullable = false, updatable = false)
        private OffsetDateTime dateCreated;

        @LastModifiedDate
        @Column(nullable = false)
        private OffsetDateTime lastUpdated;

        /**
         * @return - Lista de regras de usuario
         */
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
                // TODO Auto-generated method stub
                return senha;
        }

        @Override
        public String getUsername() {
                // TODO Auto-generated method stub
                return email;
        }

        @Override
        public boolean isAccountNonExpired() {
                // TODO Auto-generated method stub
                return true;
        }

        /**
         * A method to check if the account is locked.
         *
         * @return a boolean value indicating if the account is locked or not
         */
        @Override
        public boolean isAccountNonLocked() {
                // TODO Auto-generated method stub
                return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
                // TODO Auto-generated method stub
                return true;
        }

        @Override
        public boolean isEnabled() {
                // TODO Auto-generated method stub
                return true;
        }

}
