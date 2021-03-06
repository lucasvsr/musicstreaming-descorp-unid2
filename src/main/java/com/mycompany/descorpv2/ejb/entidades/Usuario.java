/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.descorpv2;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Bernardes
 */
@Entity
@Table(name = "TB_USUARIO")
@NamedQueries(
        {
            @NamedQuery(
                    name = "Usuario.PorNome",
                    query = "SELECT c FROM Usuario c WHERE c.nome LIKE :nome ORDER BY c.id"
            )
        }
)
@Inheritance(strategy = InheritanceType.JOINED) //Estratégia de herança.
@DiscriminatorColumn(name = "DISC_USUARIO", //Nome da coluna que vai discriminar subclasses.
        discriminatorType = DiscriminatorType.STRING, length = 1)
@Access(AccessType.FIELD)
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "TXT_NOME", nullable = false)
    protected String nome;

    @NotNull
    @Email
    @Column(name = "TXT_EMAIL", nullable = false)
    protected String email;

    @NotBlank
    @Size(min = 6, max = 20)
    @Pattern(regexp = "((?=.*\\p{Lower})(?=.*\\p{Upper}).{6,20})",
            message = "{exemplo.jpa.Usuario.senha}")
    @Column(name = "TXT_SENHA", nullable = false)
    protected String senha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
