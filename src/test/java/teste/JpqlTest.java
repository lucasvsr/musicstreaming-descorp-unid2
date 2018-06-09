package teste;

import com.mycompany.descorpv2.Album;
import com.mycompany.descorpv2.Artista;
import com.mycompany.descorpv2.CartaoCredito;
import com.mycompany.descorpv2.Musica;
import com.mycompany.descorpv2.Playlist;
import com.mycompany.descorpv2.Premium;
import com.mycompany.descorpv2.Reputacao;
import com.mycompany.descorpv2.Usuario;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 *
 * @author bernardes
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("JPQLValidation")
public class JpqlTest {

    private static EntityManagerFactory emf;
    private static Logger logger;
    private EntityManager em;
    private EntityTransaction et;

    public JpqlTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        logger = Logger.getGlobal();
        //logger.setLevel(Level.INFO);
        logger.setLevel(Level.SEVERE);
        emf = Persistence.createEntityManagerFactory("descorpv2");
        DbUnitUtil.inserirDados();
    }

    @AfterClass
    public static void tearDownClass() {
        emf.close();
    }

    @Before
    public void setUp() {
        em = emf.createEntityManager();
        beginTransaction();
    }

    @After
    public void tearDown() {
        commitTransaction();
        em.close();
    }

    private void beginTransaction() {
        et = em.getTransaction();
        et.begin();
    }

    private void commitTransaction() {
        try {
            et.commit();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            et.rollback();
            fail(ex.getMessage());
        }
    }

    @Test
    public void t01_usuarioPorNomeExpecifico() {
        logger.info("Executando t01: SELECT u FROM Usuario u WHERE u.nome LIKE :nome ORDER BY c.id");
        TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.nome LIKE :nome ORDER BY u.id",
                Usuario.class);
        query.setParameter("nome", "Leo");
        List<Usuario> usuarios = query.getResultList();

        for (Usuario usuario : usuarios) {
            assertTrue(usuario.getNome().startsWith("Leo"));
        }

        assertEquals(1, usuarios.size());
    }

    @Test
    public void t02_usuariosPorID() {
        logger.info("Executando t19: SELECT * FROM TB_MUSICA ORDER BY ID_MUSICA");
        TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u ORDER BY u.id",
                Usuario.class);
        List<Usuario> usuarios = query.getResultList();

        assertEquals(5, usuarios.size());
    }

    @Test
    public void t03_AlbumPorNomeExpecifico() {
        logger.info("Executando t02: SELECT a FROM Album a WHERE a.titulo LIKE :titulo ORDER BY a.titulo");
        TypedQuery<Album> query = em.createQuery(
                "SELECT a FROM Album a WHERE a.titulo LIKE :titulo ORDER BY a.titulo",
                Album.class);
        query.setParameter("titulo", "black");
        List<Album> albuns = query.getResultList();
        for (Album album : albuns) {
            assertTrue(album.getTitulo().startsWith("black"));
        }
        assertEquals(3, albuns.size());
    }

    @Test
    public void t04_atualizarUsuarioMerge() {
        logger.info("Executando t04: atualizar Usuario com Merge");
        TypedQuery<Usuario> query = em.createNamedQuery("Usuario.PorNome", Usuario.class);
        query.setParameter("nome", "Leo");
        Usuario usuario = query.getSingleResult();
        assertNotNull(usuario);

        usuario.setNome("Leo do paranaue");

        em.merge(usuario);
        em.flush();
        em.clear();

        query.setParameter("nome", usuario.getNome());

        usuario = query.getSingleResult();
        assertEquals(1, query.getResultList().size());
        assertEquals("Leo", usuario.getNome());
    }

    @Test
    public void t05_atualizarUsuario() {
        logger.info("Executando t06: atualizar Usuario");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        TypedQuery<Usuario> query = em.createNamedQuery("Usuario.PorNome", Usuario.class);
        query.setParameter("nome", "Marco");

        Usuario usuario = query.getSingleResult();
        assertNotNull(usuario);
        em.clear();

        usuario.setNome("Mark Zuke");
        em.merge(usuario);
        //em.persist(usuario);
        assertNotNull(usuario.getId());

        Set<ConstraintViolation<Usuario>> constraintViolations = validator.validate(usuario);

        if (logger.isLoggable(Level.INFO)) {
            for (ConstraintViolation violation : constraintViolations) {
                Logger.getGlobal().log(Level.INFO, "{0}.{1}: {2}", new Object[]{violation.getRootBeanClass(), violation.getPropertyPath(), violation.getMessage()});
            }
        }
        assertEquals(0, constraintViolations.size());

    }

    @Test
    public void t06_persistirUsuario() {
        logger.info("Executando t06? persistir usuario");

        Premium premium = new Premium();
        CartaoCredito cartaoCredito = new CartaoCredito();

        premium.setNome("Pablodo06");
        premium.setEmail("pablo@gmail.com");
        premium.setSenha("aAlguma12");
        premium.setCartaoCredito(cartaoCredito);

        cartaoCredito.setNumero("5391876104271209");
        cartaoCredito.setBandeira("MASTERCARD");
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, Calendar.DECEMBER, 1);
        cartaoCredito.setDataExpiracao(calendar.getTime());
        premium.setCartaoCredito(cartaoCredito);

        em.persist(premium);
        em.persist(cartaoCredito);
        em.flush();

        assertNotNull(premium.getId());
        assertNotNull(cartaoCredito.getId());
        logger.log(Level.INFO, "Premium {0} incluído com sucesso.", premium);
    }

    @Test
    public void t07_buscarUsuarioInserido07() {

        logger.info("Executando t08: SELECT u FROM Usuario u WHERE u.nome LIKE :nome ORDER BY c.id");
        TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.nome LIKE :nome ORDER BY u.id",
                Usuario.class);
        query.setParameter("nome", "Pablodo06");
        List<Usuario> usuarios = query.getResultList();

        for (Usuario usuario : usuarios) {
            assertTrue(usuario.getNome().startsWith("Pablodo06"));
        }

    }

    @Test
    public void t08_usuarioPorNomeEspecifico() {
        logger.info("Executando t01: Usuario.PorNome");
        TypedQuery<Usuario> query = em.createNamedQuery("Usuario.PorNome", Usuario.class);
        query.setParameter("nome", "Pablodo06");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        List<Usuario> usuarios = query.getResultList();

        for (Usuario usuario : usuarios) {
            assertTrue(usuario.getNome().startsWith("Pablo"));
        }

        assertEquals(1, usuarios.size());
    }

    @Test
    public void t09_delete1musica() {
        logger.info("Executando t15: DELETE Musica AS o WHERE o.id = ?1");
        Long id = (long) 4;
        Query query = em.createQuery("DELETE from Musica AS m WHERE m.id = ?1");
        query.setParameter(1, id);
        query.executeUpdate();
        Musica musica = em.find(Musica.class, id);
        assertNull(musica);
        logger.log(Level.INFO, "Musica {0} removida com sucesso.", id);
    }

    @Test
    public void t10_persistirNovoUsuarioERRADO() {

        logger.info("Executando t10: persistir Usuario errado");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Premium premium = new Premium();
        CartaoCredito cartaoCredito = new CartaoCredito();

        premium.setNome("Erroo");
        premium.setEmail("errado@gmail.com");
        premium.setSenha("aguma12");//errado
        premium.setCartaoCredito(cartaoCredito);

        cartaoCredito.setNumero("4929293458709012");
        cartaoCredito.setBandeira("VISA");
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, Calendar.DECEMBER, 1);
        cartaoCredito.setDataExpiracao(calendar.getTime());
        premium.setCartaoCredito(cartaoCredito);

        Set<ConstraintViolation<Premium>> constraintViolations = validator.validate(premium);
        if (logger.isLoggable(Level.INFO)) {
            for (ConstraintViolation violation : constraintViolations) {
                Logger.getGlobal().log(Level.INFO, "{0}.{1}: {2}", new Object[]{violation.getRootBeanClass(), violation.getPropertyPath(), violation.getMessage()});
            }
        }

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void t11_atualizarUsuario2() {
        logger.info("Executando t06: atualizar Usuario");
        TypedQuery<Usuario> query = em.createNamedQuery("Usuario.PorNome", Usuario.class);
        query.setParameter("nome", "Zuzu");
        Usuario usuario = query.getSingleResult();
        assertNotNull(usuario);
        usuario.setNome("Zuzu alterado");
        em.merge(usuario);
        em.persist(usuario);
        //assertNotNull(usuario.getId());

        assertEquals("Zuzu alterado", usuario.getNome());
    }

    @Test
    public void t12_atualizarUsuarioMerge() {
        logger.info("Executando t17: atualizar Usuario com Merge");
        TypedQuery<Usuario> query = em.createNamedQuery("Usuario.PorNome", Usuario.class);
        query.setParameter("nome", "Pablodo06");
        Usuario usuario = query.getSingleResult();
        assertNotNull(usuario);
        usuario.setNome("PabloModificadono12");
        em.merge(usuario);
        em.flush();
    }

    @Test
    public void t13_removerUsuario() {
        logger.info("Executando t07: remover Usuario");
        TypedQuery<Usuario> query = em.createNamedQuery("Usuario.PorNome", Usuario.class);
        query.setParameter("nome", "PabloModificadono12");
        Usuario usuario = query.getSingleResult();
        assertNotNull(usuario);
        em.remove(usuario);
        em.flush();
    }

    @Test
    public void t14_usuariosVisa() {
        logger.info("Executando t07: SELECT u FROM Usuario u WHERE u.cartaoCredito.bandeira like ?1 ORDER BY u.nome DESC");
        TypedQuery<Premium> query;
        query = em.createQuery(
                "SELECT p FROM Premium p WHERE p.cartaoCredito.bandeira like ?1 ORDER BY p.nome DESC",
                Premium.class);
        query.setParameter(1, "VISA"); //Setando parâmetro posicional.
        query.setMaxResults(20); //Determinando quantidade máxima de resultados.
        List<Premium> premiums = query.getResultList();

        for (Premium premium : premiums) {
            assertEquals("VISA", premium.getCartaoCredito().getBandeira());
        }

        assertEquals(2, premiums.size());
    }

    @Test
    public void t15_usuariosVisaMastercard() {
        logger.info("Executando t08: SELECT u FROM Usuario u WHERE u.cartaoCredito.bandeira LIKE ?1 OR u.cartaoCredito.bandeira LIKE ?2 ORDER BY u.nome DESC");
        TypedQuery<Premium> query;
        query = em.createQuery(
                "SELECT p FROM Premium p WHERE p.cartaoCredito.bandeira IN ('VISA', 'MASTERCARD') ORDER BY p.nome DESC",
                Premium.class);
        List<Premium> premiums = query.getResultList();

        for (Premium premium : premiums) {
            switch (premium.getCartaoCredito().getBandeira()) {
                case "VISA":
                    assertTrue(true);
                    break;
                case "MASTERCARD":
                    assertTrue(true);
                    break;
                default:
                    assertTrue(false);
                    break;
            }
        }

        assertEquals(3, premiums.size());
    }

    @Test
    public void t16_compradoresMastercardMaestro() {
        logger.info("Executando t09:SELECT p FROM Premium p WHERE p.cartaoCredito.bandeira IN ('MAESTRO', 'MASTERCARD') ORDER BY c.nome DESC");
        TypedQuery<Premium> query;
        query = em.createQuery(
                "SELECT p FROM Premium p WHERE p.cartaoCredito.bandeira IN ('MAESTRO', 'MASTER') ORDER BY p.nome DESC",
                Premium.class);
        List<Premium> premiums = query.getResultList();

        for (Premium premium : premiums) {
            switch (premium.getCartaoCredito().getBandeira()) {
                case "MAESTRO":
                    assertTrue(true);
                    break;
                case "MASTER":
                    assertTrue(true);
                    break;
                default:
                    assertTrue(false);
                    break;
            }
        }

        assertEquals(1, premiums.size());
    }

    @Test
    public void t17_artistasPorNome() {

        logger.info("Executando t17: SELECT * FROM Artista");
        TypedQuery<Artista> query = em.createQuery(
                "SELECT a FROM Artista a", Artista.class);
        List<Artista> artistas = query.getResultList();

        assertEquals(8, artistas.size());
        

    }

    @Test
    public void t18_artistasPorPais() {

        logger.info("Executando t18: SELECT * FROM Artista");
        TypedQuery<Artista> query = em.createQuery(
                "SELECT a FROM Artista a ORDER BY a.pais", Artista.class);
        List<Artista> artistas = query.getResultList();

        assertEquals(8, artistas.size());
    }

    @Test
    public void t19_musicasPorID() {

        logger.info("Executando t19: SELECT * FROM TB_MUSICA ORDER BY ID_MUSICA");
        TypedQuery<Musica> query = em.createQuery(
                "SELECT m FROM Musica m ORDER BY m.id",
                Musica.class);
        List<Musica> musicas = query.getResultList();

        assertEquals(17, musicas.size());
    }

    @Test
    public void t20_musicasPorPais() {

        logger.info("Executando t20: SELECT * FROM TB_MUSICA ORDER BY TXT_DURACAO");
        TypedQuery<Musica> query = em.createQuery(
                "SELECT m FROM Musica m ORDER BY m.duracao",
                Musica.class);
        List<Musica> musicas = query.getResultList();

        assertEquals(17, musicas.size());

    }

    @Test
    public void t21_musicasPorReputacao() {

        logger.info("Executando t20: SELECT * FROM TB_MUSICA ORDER BY TXT_REPUTACAO");
        TypedQuery<Musica> query;
        query = em.createQuery(
                "SELECT m FROM Musica m ORDER BY m.reputacao",
                Musica.class);
        List<Musica> musicas = query.getResultList();

        assertEquals(17, musicas.size());

    }

    @Test
    public void t22_playlistPorNome() {
        logger.info("Executando t22:playlist nome(5 - 1)");
        TypedQuery<Playlist> query = em.createQuery(
                "SELECT p FROM Playlist p ORDER BY p.titulo", Playlist.class);
        List<Playlist> playlists = query.getResultList();

        assertEquals(5, playlists.size());

    }

    @Test
    public void t23_atualizarPlaylist() {
        logger.info("Executando t23: atualizar Playlist");
        TypedQuery<Playlist> query = em.createNamedQuery("Playlist.PorTitulo", Playlist.class);
        query.setParameter("titulo", "This is Metallica");
        Playlist playlist = query.getSingleResult();
        assertNotNull(playlist);
        playlist.setTitulo("Heavy Metal");
        em.flush();
    }

    @Test
    public void t24_playlistPorUsuario() {
        logger.info("Executando t24:SELECT u FROM Usuario u WHERE u.playlists.id IN('1','2') ORDER BY u.nome DESC");
        TypedQuery<Playlist> query;
        query = em.createQuery("SELECT p FROM Playlist p WHERE p.titulo IN('so heavy','Heavy Metal') ORDER BY p.titulo DESC",
                Playlist.class);
        List<Playlist> playlists = query.getResultList();
        for (Playlist playlist : playlists) {
            switch (playlist.getTitulo()) {
                case "so heavy":
                    assertTrue(true);
                    break;
                case "Heavy Metal":
                    assertTrue(true);
                    break;
                default:
                    assertTrue(false);
                    break;
            }
        }
    }

    @Test
    public void t25_cartaoPorExpiracao() {
        logger.info("existe 5 mas exclui um usuário no teste ai 4");

        TypedQuery<String> query = (TypedQuery< String>) em.createNativeQuery(
                "SELECT TXT_NUMERO, TXT_BANDEIRA FROM TB_CARTAO_CREDITO ORDER BY DT_EXPIRACAO");
        List<String> ListaCartao = query.getResultList();
        assertEquals(5, ListaCartao.size());

    }

    @Test
    public void t26_cartoesExpirados() {
        logger.info("Executando t26: SELECT c FROM CartaoCredito c WHERE c.dataExpiracao < CURRENT_DATE");
        TypedQuery<String> query = (TypedQuery<String>) em.createNativeQuery("SELECT TXT_NUMERO, TXT_BANDEIRA FROM TB_CARTAO_CREDITO WHERE DT_EXPIRACAO < NOW()");
        List<String> cartoesExpirados = query.getResultList();
        assertNotNull(cartoesExpirados);
    }

    @Test
    public void t27_bandeirasDistintas() {
        logger.info("Executando t14: SELECT DISTINCT(c.bandeira) FROM CartaoCredito c ORDER BY c.bandeira");
        TypedQuery<String> query
                = em.createQuery("SELECT DISTINCT(c.bandeira) FROM CartaoCredito c ORDER BY c.bandeira", String.class);
        List<String> bandeiras = query.getResultList();
        assertEquals(3, bandeiras.size());
    }

    @Test
    public void t28_ordenacaoCartao() {
        logger.info("Executando t15: SELECT c FROM CartaoCredito c ORDER BY c.bandeira DESC, c.dono.nome ASC");
        TypedQuery<CartaoCredito> query;
        query = em.createQuery(
                "SELECT c FROM CartaoCredito c ORDER BY c.bandeira DESC, c.dono.nome ASC",
                CartaoCredito.class);
        List<CartaoCredito> cartoes = query.getResultList();

        if (logger.isLoggable(Level.INFO)) {
            for (CartaoCredito cartao : cartoes) {
                logger.log(Level.INFO, "{0}: {1}", new Object[]{cartao.getBandeira(), cartao.getDono().getNome()});
            }
        }

        assertEquals(4, cartoes.size());
    }

    @Test
    public void t29_ordenacaoCartao() {
        logger.info("Executando t16: SELECT c.bandeira, c.dono.nome FROM CartaoCredito c ORDER BY c.bandeira DESC, c.dono.nome ASC");
        TypedQuery<Object[]> query;
        query = em.createQuery(
                "SELECT c.bandeira, c.dono.nome FROM CartaoCredito c ORDER BY c.bandeira DESC, c.dono.nome ASC",
                Object[].class);
        List<Object[]> cartoes = query.getResultList();

        if (logger.isLoggable(Level.INFO)) {
            for (Object[] cartao : cartoes) {
                logger.log(Level.INFO, "{0}: {1}", new Object[]{cartao[0], cartao[1]});
            }
        }

        assertEquals(4, cartoes.size());
    }

    @Test
    public void t30_premiunComCartao() {
        logger.info("Executando t21: SELECT c FROM Premium c JOIN c.cartaoCredito cc ORDER BY c.dataCriacao DESC");
        TypedQuery<Premium> query;
        query = em.createQuery(
                "SELECT p FROM Premium p JOIN p.cartaoCredito pp ORDER BY p.id DESC",
                Premium.class);
        List<Premium> premiums = query.getResultList();
        assertEquals(4, premiums.size());

        if (logger.isLoggable(Level.INFO)) {
            for (Premium premium : premiums) {
                logger.log(Level.INFO, "{0}: {1}", new Object[]{premium.getId(), premium.getNome()});
            }
        }
    }

    @Test
    public void t31_persistirMusica() {
        logger.info("Executando t32: persistir Musica");
        Musica musica = new Musica();
        musica.setTitulo("Setanejo");
        musica.setReputacao(Reputacao.MEDIO);
        musica.setDuracao((float) 12.3);
        musica.setLink("link");
        em.persist(musica);
        em.flush();
        assertNotNull(musica.getId());
        logger.log(Level.INFO, "Musica {0} incluída com sucesso.", musica);
    }

    @Test
    public void t32_playlistQuantidadeMusicas() {
        logger.info("Executando t27: Playlist.QuantidadeMusciasSQL");
        Query query;
        query = em.createNamedQuery("Playlist.QuantidadeMusicasSQL");
        query.setParameter(1, "so heavy");
        List<Object[]> resultados = query.getResultList();
        assertEquals(1, resultados.size());

        if (logger.isLoggable(Level.INFO)) {
            for (Object[] resultado : resultados) {
                logger.log(Level.INFO, "{0}: {1}", resultado);
            }
        }
    }

    @Test
    public void t33_Musicas() {
        logger.info("Executando t29: SELECT c, COUNT(i) FROM Playlist c, Musica i WHERE c MEMBER OF i.playlists GROUP BY c HAVING COUNT(i) >= ?1");
        Query query = em.createQuery("SELECT c, COUNT(i) FROM Playlist c, Musica i WHERE c MEMBER OF i.playlists GROUP BY c HAVING COUNT(i) >= ?1");
        query.setParameter(1, (long) 1);
        List<Object[]> resultados = query.getResultList();
        assertEquals(5, resultados.size());

        if (logger.isLoggable(Level.INFO)) {
            for (Object[] resultado : resultados) {
                logger.log(Level.INFO, "{0}: {1}", resultado);
            }
        }
    }

    @Test
    public void t34_categoriaQuantidadeItens() {
        logger.info("Executando t29: SELECT c, COUNT(i) FROM Categoria c, Item i WHERE c MEMBER OF i.categorias GROUP BY c HAVING COUNT(i) >= ?1");
        Query query = em.createQuery("SELECT c, COUNT(i) FROM Playlist c, Musica i WHERE c MEMBER OF i.playlists GROUP BY c HAVING COUNT(i) >= ?1");
        query.setParameter(1, (long) 3);
        List<Object[]> resultados = query.getResultList();
        assertEquals(4, resultados.size());

        if (logger.isLoggable(Level.INFO)) {
            for (Object[] resultado : resultados) {
                logger.log(Level.INFO, "{0}: {1}", resultado);
            }
        }
    }

    @Test
    public void t35_persistirMusica() {
        logger.info("Executando t32: persistir Categoria");
        Musica musica = new Musica();
        musica.setTitulo("Fade To Black");
        musica.setReputacao(Reputacao.MEDIO);
        musica.setDuracao((float) 5.1);
        musica.setLink("exemplo");
        em.persist(musica);
        em.flush();
        assertNotNull(musica.getId());
        logger.log(Level.INFO, "Musica {0} incluída com sucesso.", musica);
    }
    
    @Test
    public void t44_persistirArtistaErradoPais()
    {
    logger.info("Teste pais de artista errado(validacao)");
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Artista artista = new Artista();
    artista.setNome("System of a Down");
    artista.setPais("AAA");//errado
    
     Set<ConstraintViolation<Artista>> constraintViolations = validator.validate(artista);
        if (logger.isLoggable(Level.INFO)) {
            for (ConstraintViolation violation : constraintViolations) {
                Logger.getGlobal().log(Level.INFO, "{0}.{1}: {2}", new Object[]{violation.getRootBeanClass(), violation.getPropertyPath(), violation.getMessage()});
            }
        }

        assertEquals(1, constraintViolations.size());
    
    }

    @Test
    public void t36_atualizarMusica() {
        logger.info("Executando t34: atualizar Musica");
        TypedQuery<Musica> query = em.createNamedQuery("Musica.PorTitulo", Musica.class);
        query.setParameter("titulo", "zuzun");
        Musica musica = query.getSingleResult();
        assertNotNull(musica);
        musica.setTitulo("Musica de Verdade");
        em.flush();
        assertEquals(0, query.getResultList().size());
    }

    @Test
    public void t37_update() {
        logger.info("Executando t30: UPDATE Vendedor AS v SET v.dataNascimento = ?1 WHERE v.id = ?2");
        Long id = (long) 4;
        String email = "teste@gmail.com";        
        Query query = em.createQuery("UPDATE Premium AS v SET v.email = ?1 WHERE v.id = ?2");
        query.setParameter(1, email);
        query.setParameter(2, id);
        query.executeUpdate();
        Premium premium = em.find(Premium.class, id);
        assertNotNull(premium.getEmail());
        logger.info(premium.getEmail());
    }

    @Test
    public void t40_atualizarMusicaMerge() {
        logger.info("Executando t33: atualizar Musica com Merge");
        TypedQuery<Musica> query = em.createNamedQuery("Musica.PorTitulo", Musica.class);
        query.setParameter("titulo", "iron man");
        Musica musica = query.getSingleResult();
        assertNotNull(musica);
        em.clear();
        musica.setTitulo("Iron Man");
        em.merge(musica);
        em.flush();
        assertEquals(1, query.getResultList().size());
    }

    @Test
    public void t41_removerMusica() {
        logger.info("Executando t35: remover Musica");
        TypedQuery<Musica> query = em.createNamedQuery("Musica.PorTitulo", Musica.class);
        query.setParameter("titulo", "one");
        Musica musica = query.getSingleResult();
        assertNotNull(musica);
        em.remove(musica);
        em.flush();
        assertEquals(0, query.getResultList().size());
    }

    @Test
    public void t42_Musicas() {
        logger.info("Executando t29: SELECT c, COUNT(i) FROM Playlist c, Musica i WHERE c MEMBER OF i.playlists GROUP BY c HAVING COUNT(i) >= ?1");
        Query query = em.createQuery("SELECT c, COUNT(i) FROM Playlist c, Musica i WHERE c MEMBER OF i.playlists GROUP BY c HAVING COUNT(i) >= ?1");
        query.setParameter(1, (long) 1);
        List<Object[]> resultados = query.getResultList();
        assertEquals(5, resultados.size());

        if (logger.isLoggable(Level.INFO)) {
            for (Object[] resultado : resultados) {
                logger.log(Level.INFO, "{0}: {1}", resultado);
            }
        }
    }

    @Test
    public void t43_PlaylistSQLNomeada() {
        logger.info("Executando t26: Playlist.PorTituloSQL");
        Query query;
        query = em.createNamedQuery("Playlist.PorTituloSQL");
        query.setParameter(1, "MPB");
        List<Playlist> playlists = query.getResultList();
        assertEquals(1, playlists.size());

        if (logger.isLoggable(Level.INFO)) {
            for (Playlist playlist : playlists) {
                logger.log(Level.INFO, playlist.getTitulo());
            }
        }
    }

}
