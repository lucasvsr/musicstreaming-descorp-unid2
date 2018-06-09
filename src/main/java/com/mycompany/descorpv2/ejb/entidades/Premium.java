package com.mycompany.descorpv2;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author bernardes
 */
@Entity
@Table(name = "TB_PREMIUM")
@DiscriminatorValue(value = "P")
@PrimaryKeyJoinColumn(name = "ID_PREMIUM", referencedColumnName = "ID")
public class Premium extends Usuario implements Serializable {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, 
optional = true)
    @JoinColumn(name = "ID_CARTAO_CREDITO", referencedColumnName = 
"ID")
    private CartaoCredito cartaoCredito;   
    
    @OneToMany(mappedBy = "premium", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists;

    public CartaoCredito getCartaoCredito() {
        return cartaoCredito;
    }

    public void setCartaoCredito(CartaoCredito cartaoCredito) {
        this.cartaoCredito = cartaoCredito;
        this.cartaoCredito.setDono(this);
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }


    public boolean adicionaPlaylist(Playlist playlist) {
        playlist.setPremium(this);
        return playlists.add(playlist);
    }


    @Override
    public String toString() {
        return "exemplo.jpa.Premium[ id=" + id + " ]";
    }
}


