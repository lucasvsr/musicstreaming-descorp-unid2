
package com.mycompany.descorpv2;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author leand
 */
@Entity
@Table(name = "TB_PLAYLIST")
@NamedNativeQueries(
        {
            @NamedNativeQuery(
                    name = "Playlist.PorTituloSQL",
                    query = "SELECT ID_PLAYLIST, TXT_TITULO FROM TB_PLAYLIST WHERE TXT_TITULO LIKE ? ORDER BY ID_PLAYLIST",
                    resultClass = Playlist.class
            ),
            @NamedNativeQuery(
                    name = "Playlist.QuantidadeMusicasSQL",
                    query = "SELECT c.ID_PLAYLIST, c.TXT_TITULO, c.ID_FREE, c.ID_PREMIUM, count(ic.ID_MUSICA) as TOTAL_MUSICAS from TB_PLAYLIST c, TB_PLAYLIST_MUSICA ic where c.TXT_TITULO LIKE ? and c.ID_PLAYLIST = ic.ID_PLAYLIST GROUP BY c.ID_PLAYLIST",
                    resultSetMapping = "Playlist.QuantidadeMusicas"
            )
        }
)
@NamedQueries(
        {
            @NamedQuery(
                    name = "Playlist.PorTitulo",
                    query = "SELECT u FROM Playlist u WHERE u.titulo LIKE :titulo ORDER BY u.id"
            )
        }
)
@SqlResultSetMapping(
        name = "Playlist.QuantidadeMusicas",
        entities = {
            @EntityResult(entityClass = Playlist.class)},
        columns = {
            @ColumnResult(name = "TOTAL_MUSICAS", type = Long.class)}
)

public class Playlist implements Serializable
{
    
    @Id
    @Column(name = "ID_PLAYLIST")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "TXT_TITULO", nullable = false)
    protected String titulo;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TB_PLAYLIST_MUSICA", joinColumns = {
        @JoinColumn(name = "ID_PLAYLIST")},
            inverseJoinColumns = {
                @JoinColumn(name = "ID_MUSICA")})
    private List <Musica> musicas;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PREMIUM", referencedColumnName = 
"ID_PREMIUM")
    private Premium premium;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_FREE", referencedColumnName = "ID_FREE")
    private Free free;

    public Free getFree() {
        return free;
    }

    public void setFree(Free free) {
        this.free = free;
    }
    
    public List<Musica> getMusicas() {
	return musicas;
    }

    public List setMusicas(List<Musica> musicas) {
	return this.musicas = musicas;
    }
    
    public boolean adicionaMusica(Musica musica) {
        musica.setPlaylists(this.setMusicas(musicas));
        return musicas.add(musica);
    }

    public String getTitulo() {
        return titulo;
    }

    public Long getId() {
        return id;
    }

    public Premium getPremium() {
        return premium;
    }

    public void setPremium(Premium premium) {
        this.premium = premium;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}