package com.mycompany.descorpv2;

/**
 *
 * @author Bernardes
 */
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "TB_MUSICA")
@NamedQueries(
        {
            @NamedQuery(
                    name = "Musica.PorTitulo",
                    query = "SELECT u FROM Musica u WHERE u.titulo LIKE :titulo ORDER BY u.id"
            )
        }
)
public class Musica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MUSICA")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "TXT_TITULO")
    private String titulo;
    
    @NotNull
    @Column(name = "TXT_DURACAO")
    private float duracao;

    @Size(max = 100)
    @Column(name = "TXT_LINK")
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "TXT_REPUTACAO")
    private Reputacao reputacao;

    @ManyToMany(mappedBy = "musicas")
    private List<Artista> artistas;
    
    @ManyToMany(mappedBy = "musicas")
    private List<Playlist> playlists;

    @ManyToMany(mappedBy = "musicas")
    private List<Album> albuns;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getTitulo() {
	return titulo;
    }

    public void setTitulo(String titulo) {
	this.titulo = titulo;
    }

    public float getDuracao() {
	return duracao;
    }

    public void setDuracao(float duracao) {
	this.duracao = duracao;
    }

    public String getLink() {
	return link;
    }

    public void setLink(String link) {
	this.link = link;
    }

    public List<Artista> getArtistas() {
	return artistas;
    }

    public List setArtistas(List<Artista> artistas) {
	return this.artistas = artistas;
    }
    
    public boolean adicionaArtista(Artista artista) {
        artista.setMusicas(this.setArtistas(artistas));
        return artistas.add(artista);
    }

    public List<Album> getAlbuns() {
        return albuns;
    }

    public List setAlbuns(List<Album> albuns) {
        return this.albuns = albuns;
    }
    
    public boolean adicionaAlbum(Album album) {
        album.setMusicas(this.setAlbuns(albuns));
        return albuns.add(album);
    }

    public Reputacao getReputacao() {
	return reputacao;
    }

    public void setReputacao(Reputacao reputacao) {
	this.reputacao = reputacao;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

}