package org.escolar.service;

import com.itextpdf.html2pdf.HtmlConverter;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.escolar.model.Abastecimento;

@Stateless
public class AbastecimentoService {

    @PersistenceContext
    private EntityManager em;

    public List<Abastecimento> findAll() {
        return em.createQuery("SELECT a FROM Abastecimento a ORDER BY a.data DESC", Abastecimento.class)
                .setMaxResults(100)
                .getResultList();
    }

    public long contarAbastecimentosHoje() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(java.util.Calendar.HOUR_OF_DAY, 0);
        c.set(java.util.Calendar.MINUTE, 0);
        c.set(java.util.Calendar.SECOND, 0);
        c.set(java.util.Calendar.MILLISECOND, 0);
        try {
            return em.createQuery(
                "SELECT COUNT(a) FROM Abastecimento a WHERE a.data >= :inicio AND a.statusPix = 'PAGO'",
                Long.class)
                .setParameter("inicio", c.getTime())
                .getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public BigDecimal getUltimoValorPorLitro() {
        try {
            return em.createQuery(
                "SELECT a.valorPorLitro FROM Abastecimento a ORDER BY a.data DESC", BigDecimal.class)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Abastecimento save(Abastecimento a) {
        if (a.getId() == null) {
            em.persist(a);
            em.flush();
            return a;
        }
        Abastecimento managed = em.find(Abastecimento.class, a.getId());
        if (managed == null) {
            em.persist(a);
            em.flush();
            return a;
        }
        managed.setData(a.getData());
        managed.setLitros(a.getLitros());
        managed.setValorPorLitro(a.getValorPorLitro());
        managed.setValorTotal(a.getValorTotal());
        managed.setStatusPix(a.getStatusPix());
        managed.setEndToEndId(a.getEndToEndId());
        managed.setNomeDestinatario(a.getNomeDestinatario());
        managed.setErroPix(a.getErroPix());
        managed.setWhatsappEnviado(a.isWhatsappEnviado());
        if (a.getComprovantePdf() != null) managed.setComprovantePdf(a.getComprovantePdf());
        em.flush();
        return managed;
    }

    public byte[] gerarComprovantePdf(Abastecimento a) throws Exception {
        Locale br = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getInstance(br);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        NumberFormat nfLitros = NumberFormat.getInstance(br);
        nfLitros.setMinimumFractionDigits(0);
        nfLitros.setMaximumFractionDigits(2);

        String dataHora = new SimpleDateFormat("EEEE, dd/MM/yyyy 'às' HH:mm", br).format(a.getData());
        String destinatario = a.getNomeDestinatario() != null && !a.getNomeDestinatario().isEmpty()
                ? a.getNomeDestinatario() : "Rudipel Rudnick Petróleo Ltda";

        String html = buildHtml(
                dataHora,
                destinatario,
                nfLitros.format(a.getLitros()),
                nf.format(a.getValorPorLitro()),
                nf.format(a.getValorTotal()),
                a.getEndToEndId() != null ? a.getEndToEndId() : "—"
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, baos);
        return baos.toByteArray();
    }

    private static final String SICOOB_LOGO_B64 = "iVBORw0KGgoAAAANSUhEUgAAArwAAACfCAYAAAAbHQryAAAACXBIWXMAAC4jAAAuIwF4pT92AAAAIGNIUk0AAHolAACAgwAA+f8AAIDoAABSCAABFVgAADqXAAAXb9daH5AAAEshSURBVHja7J13mCRF3cc/VTOb9/JxR5YskpOKgqCYMAFGJGdECa9iAlExg4qYABMIgggoIEFEkBzvyJxHPC5yXOTS3m3e6Xr/qJrdnp6e3Qk9naY+z9N3OzM9Xd3VPdXf+vUvCKUUFovFYrFYLBZLWpG2CywWi8VisVgsVvBaLBaLxWKxWCxW8FosFovFYrFYLPEjW8nK3/73QaN9fCDwDuCtKC2ki7yDlc+fLh/i4T+V+/OCVVBKjXye/2z4tUKZ94bfV6AcVfhaKZSDXtyvzbqO92+n8G/HUTi5kfea+3tY0zxF3b3j4SAygly/vbLCYRHwBHA3MBRKi+2tsHIt/PUu6O2HliZ7FiwWi8ViiQA18776CN4SfBr4hhG7oyNGlO7In6JYGgsQLtErxIjoFQgUqvBbwy/0H8IlqkX+bUZE8nADEnAA5WpQCIQwAjnfmnC9L/TmRP49FANNbWzUs4QtVr/M65vsCz29IKzxPEReBX4NXGa7wmKxWCwWi5daVdlfgRuLxK4o+A+/z/xeC+HzkfCsJwr/Fp62hHC9P7yIgvWHFzyvhxeh/5dm8X4uxcj7EpASJ9vM7ktmKNm7WtHUgY9921I/dgAuBR4GJtnusFgsFovFEpTgvQs4aqyVRhO9wkfpCuH/RVHwt1arwiOMhShc0y2GhRAjAlq4tuESubhEsr/41X/jErtCCqRQ9DR3MrVvhdhp6QwjeIW9usJnf+AZYFzdWlAKMhKkLPS1sVgsFovFkjrB+1PgQ5Ur3dFWE74flrTyur/msf7mxatb4HqFbLEV2N/K67Yi57c9LKBd25BC0d06kZ1XPCPa1i1QtEywgigatgLurNvWsxkYGIS+fshkbG9bLBaLxZJSwbsN2me3bDVbq2uDn/gtsPL6uTa4tzXswiA82xMetwZRIILdLgxSjFh1pcfiK6RAShhsaqXF6Rd7LnnUqOCsvcKiYT/g6MC3qhS0tcDrK2DdBmiygtdisVgsljQK3nHA14LeibKsvN41XALW69owomI9BmCvlVficuYd+d6I5db1txSFPrz5bQ37+QqkcNjQPpnt1rwspr75gqJ1ok7rYImCbwe+xaYsdPfC7Hna0muxWCwWiyWVgvcDwKHVqNkwAtiG2xKFArfQbUG4/HjLC2DzukJ4fXyHLb4CVEYylGli76WPKQY2KLKt9iqLhrcCOwW2NUfB5PHw4gKY8zqM77Q9bLFYLBZLSgXvvsCm9diRsgPYRBkBbBQGsHkSNQy7MrgD2AqFrzDWX68wdgWwCe3GIKRLAEuBRNHTOoFN178ut1n+DDRbX94IeXsgW1FAWzP09MEjs7TvrrRBiRaLxWKxpFXwblGLmh3LyluRa0Mpa28ZAWxeKy6+luBSAWwjonfYbUKIopRlPa3j2G35k2Q3LFU0d2LTlEXCxGA2Y6y7Dz2vrbuTxtlJjMVisVgsKRa8PbU2KCpcqVQAW8FL4fLlLZWbF5e49arwguwLIwFuvm4MsrAdiSdlmQCJor+5g0kDq8Uuy2ZCtg2bpiwSBmveguPAlAkwZzHc9xRMsK4MFovFYrGkXfDOBgYCV7q1WnkZI4ANTwCbOzWZWwi7/HW9r4usuCUC2ORwbl6H9W2Tedubs8T4NXMULROtVTB85tYmdhWM74D+QfjHfTAwpEsL2/NosVgsFkuqBe/dwIyqW4tBAJvbYis98lqIwvVFUd7ewoptjBHgNpRtpolB9ljyqM7WYNOUhUk38FDV386nIGtrgRvugUXLtVuDY7NuWCwWi8WSdsH7InBZEA2XZ/ANoAIbIwFsRZvwyb2LuwIbIyJWFhWuKPbrLXB7kAKJw4a2SWy1bq7YZOUsm6YsXG4AeqsWu81NMLETbnkYnnwZNppoLbsWi8VisTSI4M0LiUV1VrouK+7YFdj8xLDws/j6pBgbzVKLy1UBjwuD9Cs7XFCMAqQUKCkZamphr6WPQX+XKTtsqTOKavPwKnSO3cnj4a6Z8N8nYOoE7atisVgsFoulYQQvwFE1tSqqW3fUALZh8Vto4i0VwFYgaIsC2CgdwAYeS+7Ia+lj/ZVC0dMyjmm9y8QOy56EbIe28ipll7otnI9iKcoI2EoWAUydCA88C7c8BJPG64IT1rprsVgsFkvDCd5HgPtqbTzQNGUFf/u4NrhcFoRPBTZRbgCbLPTVdVdjK8zTawLYhEAKRXfrRPZY/iSt6xcpmsdbAVU/VoL6IdWoXceB6ZPgmVfgpvt1wFprkz1XFovFYrE0qOAFOCEItVuW6HUXkSg3gM29QlHlNVEUwObevvBxexAlcvMKT6niUuJ3oKmVjly32GXZkyCbjWnYUgdOq+pbjgPTJsK8JXDdPdqHt6NNZ2qwWCwWi8XSsIJ3EXBJaHvq59pQRQU27+aGXR6M/23h9lwi2P2HJzdvyfRlBRXYHLraJrPjqlli0uoXFS2TrOUweJ4Dbq5K7E4aB6u64C93wsCgzrdrMzJYLBaLxdLwghfga8CGWkVsXSuwURjAVmTlxVN22C8VWYGlt9iFQXqC1YQ0FSnygWxG9DqZDFIK9lw2E5wBRabJXoHBckTF31BKW3KFgL/9F1au1YUmrNi1WCwWi8UKXkM/8OUgdqRuFdhEsTAeLTevW4UXuTWUEsUUryt9XBukUKxvm8iWXfPZcsUz0DIRW3I4MK4HXq74W9kMjGuHWx+BVxfBtElW7FosFovFYgVvEVcAr9RF6QZdga0oyq14q+4AtoICFQWuDKKgJDEulwev9ReXH6+2AgsGmtvFnsufQPSuUmTb85kF7FL9MgR8oaLrTqH7fepEeGQWPPistuwKWwLaYrFYLBYreP05rqZvV1yBzSeATZRXga3Ayiu8GRcYNV+vkKMEsMnC9b0BbMMWX6nobelkcv8qsfPSGZBpM0Zeq1prWL6DUl0VpS5zcjq/7quLdPqxznabfsxisVgsFit4R2UmcEe9d1b4iOBSBrmSAWyyVCU2jy+vx7xcUDaY4jLC+e/7+fiKIh9ghw2tE9hl1XOirWuhomWCFVrV8wZwYUXfcBRMGAfruuGGeyHnQGebPQcWi8VisaSUbIDbOgFYUZOaVcP/+X5W+KfPmgKEApXfljAaxvv9/Ou8plX6i8K8pyg2LCtAKIESSm/XiGIllHFbEFpIuf+WeeGsRv5XIIVgKNtM+8AG9lw2g8e2+xTIjC07XB2nVrS2UtDWDFkJf78Plq2GjSeH7bc7DdgI6ATagSbPpdYH9ACrgeXmb0tjkjHXyhRzvbQBzZ51etDBwyuAJbbL6AC2ML+zCcA4oNX0mwRywAA6BmUD0AWsBBYD62z3hc5mZplozleruc6zZjwcMmNinzlXa4Gl1FLx1WIFb42sBC4Gzq636C0SwaLQOFckapVLnJr3hNE++e8KUbB6USPCpYKVKNTKbncK5f3bk75MSL09qRQbWiey7bpXxPyVs1g6dTcY6LJXZGU8Bvy7ArWr/VImdsJtj8Ks12DjKfXKtbsJsAOwHbCL+XtjI142MoN6OawGlqEt2S8BTwMvALOAQXsJpIJxwNbANuZ62ckItunAVCN2m8vYzqARAkuAucATwDPAs0B3SicDO5lld2A3YEsjniZXsb0u03evm9/Ys+Y39zKw3l6mgYyJ+fO1j7nWNwU2r0KLKDNBeQOYb67zF8y5mt9Ak+FxxmjSbiZ6LWZi3GwmDa3GoNJs/m8yk77mEgosPxkcMhPC/ESj20wO15tJ4TpgTdI6TKgKHuN++98HlbPaOmB81XukCv7z/Wz4z5F/Cp9Gq4KPRj5TkD9epUbWG/7bBI8pjLHVvOd2/cQBx6zn5NdxFE6+WrCj13cc87ejt+Xk1zHvOTn9JH1S90qen7LXtU9v9bGD6V87xY6JFfFW4NWy13YUbDwJnn4VrrkLJnToAhPBuDJMBg4ADgL2A3al0HIbNEuA/6DzDt8RUf//1giMQTNQ9gO9ZskPjMuMJWaJEWPd9rJlI2BfYH9z49/LWLfqxZvmGrneXDNJZnPgw8D7gPcacVtv1gKPAg+bCfb/7CVcFk1mLPwIcKC5zsPIxTkbeAC4B7jfTGSSRIcZI6YBW5nJ71Rzj5loJsIbmdcdjFjDw2QDsMpMLhYBC8xk4zkz2R4Ia0fUzPKL/tZD8B4HXFXbEYwtelWx8i1L9LpFLcr1vlP4uaNGPh/+jtLrDYtgZ+TzvLDNf14gbhWonBa9yhkRyE5OMa53zaLnp+zzlmc3++CPGFx3nh0jy+Ya4Niy1845sNFEWLoKfneLPlHj2mu17k4GPgl8Dni3mVlHwVzgj+gnLEMhtlvp5Ha1sZ69bATE48BTDXK97m1u/B8B9jQ3qSh4AfgFcGWC+m4T4LPmd/Yugos9qaUPbwX+Djxvh+IiPgx8HvgQ2oIbJWuBB81k7+YwhViZjEPnj98D/RRwc5eYTSrLzRj/oJl43J9mwZufYe1cT8FbKHpH3iyw5rrFb8H7LtHrjKynRa8RpbisvLhErbHyFohetHh1W4Ddlt38ennB6zigclr0jutbc9Bzk/e5/9mN3z+BwXUrKO/RZaPjgJhMuf52jkvc/uFWWPqmTkdWvd/uAcBJwGHU8jQjeBYA3wKuC6m959CPkmsVD3eZSXLaLGc7AkcBB6MtuXHiYeD0mPf5h9CxIZ9EP6qNI/eZyfdVNDYbo+MpPlfTvb++vGEmKX+kmpzt9WFXtHtamllgRO+NVOSCGLzgrddM+fiavh1SBbai12Lki8MV2HwKTiApzMvLSMoynY7MnYNXjGxWuEoV6+XBTEbcLyWAWgf8n9WyZfF13V9lpCxTDrRkdaDaLQ/BouU6HVl1YvcQ4G4zcz02ZmIX9OOvvwG/T9C53Bnt9z8LuBrt15d0Pg/8C+3/+e0Yil2A95g+PyKG+3aEEeR3mb5sifG5PghtLZ+DLsLUQmOxA/Ar9KPt78dY7IJ2f/mK+V1eDbw9Bvs01ADXyFZm4noH8CJwDtpdI3TqJXifAm4KYkM15ebFPzdvYbGIMiuwiUJ5XZh+zPUan/Rkrpy80lWEQkqOymTMprU++z2KRTat7qjLchQXl7euiTScNB7ueRqeeAmmTazGjWFfI3RvBT6YgAHmC2Zfk8Yx5mZ0UgL3vRVtMX0FbWH/WEL2+2+m3+PAR4EZZp/2T9j53w74pRG+JzeAiJlujvcVY6hpTdj+H4MO6rwS2DbC/Wi0XJhvAy4A5gE/IWSXl3r6Qp0WvNKtcDVR+mVBBTbpzfErigSzt3xwgTXYpZjdnwt3dgbhrrYGCH4rMuINkTWvnWGVdqRVtaMux5S9ruPARhPg+blw10yYNE7POCoTMZegfU0/mLCB5RD0o9akkQUuB36UoH0+A+1HfYmxeCWNq9G+sVFaCW8zFqB3JvyGvgXwJ+BJdMBWGjkLeA1t0U46x5tj+Q6WMOkAzjV9/600CN430Y84aha9Zbk2lFmBTfhVYPN+7lNooug9v0psPhXW/NcV66XkbCkhk9FV2lw67VEU/7W61nd5rOy+yTnasrt8Ddz8oK6i1tZcSUaG9xvrxekJHlSORvuQJpHzqDTHcvh8zlwjvyX64JxauYPwI70xN7uXgU+k7Ia+D/AI8OsUHdNu6FSQvya6AN168QO0P/u7rRYNlTbgx+hYjrpPuusd7fo9ak0JMproHU0Ei9E/1y9LuDZQWhy7K7D5ilpEkSXYbdk1Lg5fkRkxJDMCmTEWXlWg2E606tZ3Ob5sy25Hq879duP9sL4bJnRW4spwNjqlzZYpGFB+F5GQCYI/oCP048Z2wD+BG0imRdePScD5Ibb3FvMb+zFlP89LJGehgzt3TcFxPE+0TwLqzS7o7DHftDo0dHYyk6mzkix4qauVJsAAtiJfX28AGxRZdwv8IDyBbnisvLq3BQJeEpIrhDS+vflgN6Xcmm1x+X6qDbNcgWJOWX672YwWvLc/DnPeqDQjw+XotE1pYVy9B5E686uY7c8ZaKvuYSm86XyVcLLEHIoOXnl/g9zMd0cHCB6dwH1vQsfj/JrG4UJ0DITNmBQ+vwYuS7LgvYFacxWW6dpQJGIrCGDDJWihOHCtKIDNz7VBMuz6UBTQNhysxglSaldSKXVFYR8LL6C+iVLrCqpeNO7SD+r/ysvKoGDKBHhsNjz2P52Rofy4gDtIZsDUWCT5mD6H9ouMmqnAnWj3BUk6aQM+Xec2zgduQVeGajSuQQfsJIXtzMTkUw14rg5BuzhsZTVo6HwRHbiaSMELOiVF3RnLylvqZSnXhqKtui237o+FKwiuVACbfuufUoqZUgryS8bt0lC4DIE6w5p2FaC+hlLdYwrjnAOTx8MrC+GOGTCuQ1t7y9O7/0FHiaeRndAJzZPKJyNu/4Pmxn9wA9xs6il4r0S7uTUy5wB/ScB+vhdtld6ugc/VDsZY93YsYXMEOl9yIgXvs2hLb81qtqY0ZcLfylvKSlwUwOZe1WPlpbwAtlNEBoqWYpeG/PJXFP9rcL27AMUlY67nKO3GsL4Hbn1U++92tJYbpHYtujpQmjkwwfseZSDJmeiUdBs1yI1mrzpt91Zqzc+eHo5F+4DHlUPRhQLa7KliPDpVng1mC59TzPibOMEL8KWgNiTK+ayMALZCv97KAtiEq5DE8PaEKEhf5glg+76QYpUWvq5FmpxoDqUsl8c0uDvDcWWtJwW0tcDdT8KyVdrSW57f7neAIxtg8EhyAF5UFpaLgN802E1mOtrvO0juQj8itoxwWM1GoPrwKbTLiaVQJz2KLg9uCZffEGBgcJiCdzW15lsT5X1WVQCb9/PRAthc1uYCH16KU5cZ5btaSL5XEKhmFlkQtOa7PI9S/2xQsXsfSj005nqOo7MwzJoLz8zRPrzlZWQ4AJ2OphGYkuB935zwa8tfhQ7iajTaA75W7kCXCLYU8zl0zt648GECKhiVUmYQbZGKRiWwIkphB19cAKwKQvTWrQKbW/iWCmBzC2GXQ69fgJtZ5dSCQLWSQWslOa0hBW85Uc1KQWuzTj123zPQlNF+u+Vd+41kyehI8L43hyzYrweOa+AbTFDR6VeQXr/4oDiZeBQ92B0dx2ApTRZt6W23XREqO6LdgBIneAFODKMRXy+GEj67JSuwFbk2iOIyxIy4LvgGsAmeFlLcJKSg5ALaSll6WYFSP2gwwXsZjrN0jH4Z8d2d+RIsWw3jO8r1270EnXu0URhI+P6H5U/4Z+DwBr/BDAawjW+GNdangB9EPDGYCDxoT0NZTEe76FjC5cKkCt7b0Im4a1azlVZg865XMoDNpwJbUUoy13tFwWqF1uET/FwZCvPwKr+0ZN7lfFBdDRKp1g3qrLJSkI1vg9dXwJOvaLFbXkqGLdGpTxqJ1Qnf/0wIbfyCkDLKxJheYE2N2/hwUDeoBuIOdOq7KLgXmGBPQdnsT7pytSeBTYIwRESVT/KIoDZUawU24SN+ywpgK6qmVlyBDcFfpOR/UuiYKr9FSNNAeVrwhAax7p6NUrkyXB60+8LTr0B3r3ZtKC8F2e8acMB4M+H7r+q8/dPQFfYanTeBtTV8fxLwL9uNVfHfCNq8jPpl5kgzZwMH2W4IlZqfGEVVcvRl4K/UUnlGjHILdH028qfPFzxv+W1SACr/QV7TmjeE604shpWw0ttQOEKIs8ZS5FKaFRyFTtUwKjejreN7pPiifoVy8++Nb4e5S2D2ApjYWa4rw1toTL/CBXa8LMm+DToJ8mNhjd+/m+SWso6aPYDz0OWWw+ATNN6TriC5DZ22zEnwMXQBfWiXtyHzXos5rrjFfXwAHbxc9dPKKAem09Em6qZaRa+v9vWKXpdyFaJQGxWJWqUttAo1/J7I/ynyb4m8tC1GAZKzpaJrrEOQUlt6cZxyK8ofBeqFFA8i5T9SlgJmzYP+QRjXXq7g/UoDDswO8IS9P/nSifXJc/NYDd89G9gnpsc1CPSYEb49xqL8R8B1wLw6t9MK/D3G1+EGIGfEV2tM97EDXUwlKQGuz6ADSV8HlgEr0EkEBszvQ7l0YQewKbA1sD2wJ9pVaeMI918a0Vv1dRvlj74LOBed6zIc3CI4L3rdatklbPU6AqXUsCAWqkATF+T8VWoksA2hlgj4dTkCVmSMQlaOXsbmReBqAopajBm3A4+XJ1PaYNFyeO0NmFC22G0mPoE0G4Cl6MfH3WZ2nTGDezs6kGRqQLPsV4GVVsv5crOxZiSFAWPh6Ab6zWSm2Szj0C4Ftbiq3VPl9zYnXn6Nj6Mj6h8G5pg+22CG7XHANOBtwHuA/dBZCuLC39BPHerdRlyE5DLgAeAR4GnXuDhk9nESurz4u9AFIA4iPkUxjkWXG38qAWPH3WgXlrEYAtaZ5SXPJOmzwFkRTmzfl1TBixkgv0wtZU8rsfJ636SU0B3RT8OiN2/ZVcYa7NpW4XsAHFX+/uezNCiQZbspnon2g24iXZxS/pWbgRcWQncfTJ1QruD9AMEn1a+E+9H+jU8Csxk7OGgiupb7W8xMew9gV3Qi7s4K2n3W6tqS19sHY7x/y9AuTI+j3cDmAYuMeBvyWb/VTJI2M2PqFsDO5prZlrGDotaZa7Qa/hqTPvsj8Psxrvn8ZPN5dAo60Dm5T0fnxo2ad6JLPNcrJ+5+RF+uG9P/v0JbtPtLrNONtkK+5ro2pxuh+X/mWo+aq9Hl2+NOrakd+4BrzHIu8JMIjqGmIhRxeKxzMrXm/ytT9BaJ4DJcG4ocdT0CesTFYVgQPyiEeKB8vevy4XXKFrxd6LQ/F6dGegguBJaXtW5LE6xYAwuWQWfZ5YPzgjeqm/BFxtJUCWuN4HnORwjvYma7+6HLBo9mrbnDatsiJhlhFDdeQ1udb0FbuypJJ9cHLDbLTM9nbcB2aCvZfuba2cKzzn9KCOmx+BjRl66+HV0oZE6V33/ILBcBvzb9FCWX1lHwXhfxsfUYsXp5ld9fDvzcnKfzgO9GfDxvAz7vmjw1AhegnxzeGHK7NblUyBh03F2U+xi7Zk1V/Fept4py8wqfjA7uwhYjL0/Alb1hrEVKo6Ydp9JMBr9EqcUpycrQjaPOHRb9oy05R2djmLcUVq+HlpZKLoGwBW+/EQNfqOFGXEoIPwL8EDgYbQE+ArjWWK7cDFnB68tVMRn/3OPgp9H+ct80Y2KQuZN7gf+Zyddx6CcH+5tJ81yzTrVW2ksi7ruvoEsXB/EbexL92PxnER/TdGMMCpoTfSY6YfIo2hp6eQDbGgDON9fxoojP188bcAy9yfR/mEysZdyOy4B/VM1bqDI3r/Dk3C1MT+arfos+z6cxQ3CJkMwfM+9uwWKKVuR9eCtbTqjyezFb1BfLFsdCQG8/LFoBTVkqyFY1Ef14N0z2B/4dQjsrjHXhaCNkDgfuNJ89TW1pptLI3kYgxYGH0NbWg9GW3bBwjPj4Ktry+1G0j1+lnGCuuShQaJeUX9Vh298k+pzMPw14ezJiYXadGRMXBrzdR9FuO1EG5m5uJqyNxg+AN0Jsr5Ua/LfjInjnoyschccouXn9lLOvlbfwu91C8DXhKkAx1iIFZKRizVAzDDZp8VeOlXNkuQdHPVzhd+K2vIDjXDN2RTWztDfDG2/CktW6wlr57BXy9X4B0QQyDKCd+j+K9kv8stW3RVwdg33oBo5HuwI8EIP9uZPqLMoXRLjP76T6ILtyuAodpBMVk4HPBLi9L5ltRvWbO7KO2+8y10OUwWM/atDx9JchK7fEW3hB5wMcqLkrKNPKW2pN4THoCvf7PmWHRwpPnCWE6BdCMPait9tHhvWqmXeOWwmyB9aP1yJQVeTecFTCrbvlF9NwHN1xC1dA3wBkRCVXx9tCvJaHQh4ESvEwMMPq2wI+SPQBJg8AbwX+kvC+/Cz60XsUfAztflBvbjRCMQ0i6ryIjuE+wkvd9W5gSUTHuSPw3gYcU+8Nsa1BdKxC4gXvAPCNQPR/GaK3SASXqMBW9D3hK6DnIvhzuVZdR0i6ydIhc2yRyXH09Hlcvucd7DV1gRa9fS2gcpRp9XwdR12WUOvuzTjOk2Vbd6WE9T2wbDW0NlVae2ujEK/l+dg0YHEl6kDPP6BdGN5IQV9GZdH6FuG4CuX5HdraGwVvRbvg1MrniSaH6irgQyELoihF55cbcExdRnjFN7rMOU684AUddbksjIb8rLy+PrsU5tstEcB2ghgjOC3/eS8ZhhBsk+nhHa3r2Kypj8X9HezUuZrL9vwvZ+/yEG2ZPlg3HnJCX0djWz/PQqkNCQxWO60i625rFlashdUboKW50lM+McTruMfqylhyADq7RVRciC5hnAb2psYUQVXyGNG4UZyATtgfBUFUQ/tGRPv+CXQBiTCZg04xFwWHUnv6r6QxWIsIrZDltXxZxrDzan/0UWUAm3e9sQLYjIi9Q8DDY1l1h4SgV2SYIAbZI7OOHZs2IAV0OxkEikW941jZ386JW8/m2nf9m4O2fBW622BDm7H2jmrxzeE4XynbUhqP5bs4zsqy18+nHlu6GgYGTXm6iggzUfkWWOLINyJs+5fo3JVpIaqKhVHmyT08onaPRFccq5Z8paywuYKQMjD5cBk6YDcKTqCxaCa8mgA1ZWKJo+C9Gx11GR6VBrCJAkffU5D4W3XNd3tlBoRgG7WB3cU6JolBulWWQSWGN5MRin4nw0vrJzOtpY9f7v0QF77zPqa1r4PVE2BQjuXmcDmO83JCxO5aHOeHFX1HAOt7YdkaaK7qt5UJ8YqabCwblvgwFe33GQW3osvupoXxEYm/nxGtK8jj1JozvjragMNq+H4UPsh96AJJUXJ8g02MomLjELXk/bV8Wca0A08KSsTWJYBthAuFYKmfF4MEBjOSfplhSm6AXYbW8RbVQw5BTwntJYzwXd7XzqLucRyy+QKuPfBuPrnjbOhtgq72kWAvf5/YkxLiu1v5fub9dzf0mXRkFTMQ8jV8MZY4cUxE7a6sUazEkY8QftGiIcLP+enHaRH2ebUcFcH+/gSd+zlKZhNNDvJ9iMZfOirCrFZZ04QzroL3FeBPYYnekdfCV9j6BbAJITYIIc71y8CgpKAvmyWrFFv3b2CHgS7a1BA9IksOwVgP46VQ5JTgpa6JtGYUF75jBpcc+ABbT3oTVo2D3gyoIT9L6GM4zu0xt+7OwnFurvh7GQFrNkDvAGSqumw3hHwNb4d+WiGwxIFTI2r3oynsyygsWL+ihujsAFlIdfmKa+WQKu/X+xBuwC5of864TPjPiajdw2gczgqpnfvQOedTJ3hB+4iFYpUTpUSwz3uul6f4uTEMZTMMZTJM7evjrd3rmDbQx4CU9IvKuzorFKv6W3h13UTeu+kyrvvQ/Zy6z7Pa1rG6w/i25v17h5eTPa9jtjhHVv3dVetH3BsqZ1VEM9/ZBBNlbameXdEpg8LmKqLNC1oPWiIS8RfGqA+isDRPAvat4ntR5BH+PTrPdByYDTwTQbsfb5Cx9TvAZiG1dVmtG4iz4O0mCL+3Kl0bxqjANhvB9W5fXSUFA00ZmgdzbLm2iy02rCfjOPRmMqgyrLolT5DQwVpz14+jfyjLN/aezfUfu593vmUxvNkBG4oKVqzAUT+OqSvDdTjOCxV/TwE9A9DVA9mqXXEXRHQd72REzyU01mOuOHFwBG32AWeksC8/Qm0BVNXw34gmrKWYQbClwsulGh/0KCoKXhqza/YXEbR5QMz1VRAciq60FgYL0aWMUyt48z+cwGpkiwpXGiWA7Ri3YXeoOYOSginrethy1TrG9/XTn80wJGVgz7MzQrF+KMvLayaw65QuLv/wDM476Ck6mvtgRad+iDQS1PZtHGddzFwZcjjOl6r6bkbAhl7ortp/F8JJUj8apwOvoR/1bW81aKhEEUD4qxhZuYLkvRG0+ccY9sMNEbT5jgrX34bwn2y8iHZJjBM3En4MxziiL3BTT74I3BJie4EEXiZhBnJ0XZVu5QFs1yN4TgAqKxhqydDSO8jGS7vYaI2+v/Vn65MQQKAtvvPXd/Bmbyun7jGXmz75KB/ddR50NcGagoIVp8TMleE8k52hcsErgJ5+GBiqJh1ZnnnA0oiv5Q60q86rwF8JNyF7ozIe2D/kNhU6o0Aa2Tfk9jYA/4phP1wXQZu7xfxcAVwbw3M1QLjVwPIcmMLf/9uBfxKAe0EF/IeACs0kQfA+DDwYlOitMYAtJxSnCwG51gwomLCsm+mLu2jtG2SgOYMjRd2jlDJC0ZeTvLBqPNPaBrj0I09z8SeeYtNJXbCsHfoEqNw/yDnPkHOIwbKEXO6n5HJUtaicFrxDTi2CF+CuGF3XR5n9eRZdnWdzq03rwvsJP3Dwb8CaFPblFCq3MtbKvcQjWM3Li2YSHSbT0P7o5fKOCPrl1pheu7dEJA7jRLVZMzZBV+q7A3iCcAPyeoFPB7WxLMngKGBxGA0JiivWCjFc9+A81SJXOxlB86oBxq3ooWVgCKdVopokssJat7XupxSK5T0trO5v4rC3LmW/LVdx6YytuXrmVtDVCpN6TgCej8H5O7nqbyogp6BvkAC6999El5uxFHuY5efmZnFNjG8aSeTdEbT5l5T25R4RTB5mxLg/7iT8il57AP8rc92wn2wsBV6I6bmKwsIbN5eGyeiMHaKEFuwwk6rNgU2BbdFPCXYjvMISXt5HgJVLkyJ430Cb0Gvz4zBq1k/Uut8UgBr5R78pWe60Zn4qenK0L+ilfU0fskkw1JohI0GoaDpGCoWjBK+u6mRK+wA/+eBLvGfrlVz84La8NHfyLDoHrqNj8AicyLJjPWFuDNUzlNPuDMHcoPoJP+im3N/ip80yF/1o8DaiqxaUFsL2YVwHPJTSvozCYnh/jPvjuQjaLNf/vzmCaz/OY9Vcs2wbYpu7AZ2EnxKzFEcAnykheJuI3xP/I4CZQd9kk8JX0GWHO4LY2Fiid/i1A6pFQEZ8JLukj5bXe2kayuF0SGgSSBWPzslIxZreZtb2Z3n/1mt415ZP84cZW3LJw1seqZa2vp/OgWlawIeIngUcVfOJGgT6c0HUStuADoA5M+bX+rbAd83yENpieDOw1urXqm46YXK3mVSlkT1Cbq+HaFJKlcuzMT4HewR1r6yAuE/0ng1Z8LYAW1O+Rb7uMoFwK47WwuepQ2BokgTvAPBVdI6/gJVuidUUqBaJ6HOekHO65zatHthCtEqc9gyZ/HZiVFZACIUA5qxqZ3zrEOe+bz4Hbb9q7UX3bHX5i8vbz6ElR8Fuu/c94GMRIIcc/uIo8VqNB4XI9tPtDJJTgUxAL0iA4HVzgFkuBq4G/kB8HxvGceKwZQSCN62E3ZfPmuluXHkO7as9KYbnYOcI+mNWzK/fKFLJbRojwZsEVgGfRMduBU42YZ3xB3Ru3h2CEL1jWnmzAoRQ8sXu7TOrBt5kUraJDAjzZYEJanPl4xXCLB796C5ukV/HpemMAFXDr4fXpXBbo2tS/f1MRtE9kGH28k52nNqrLj38pdXL17YiMmpkO6JIV46mOavoY8WQI850anSlaMoq2lrhhO+N55X5LdCaq/UaWgpcQRDlq8NlghHqZ6IDMC6u16CQMsEbNjNT3J+bhdzeczHvjxw61WCYwUkblWm22SKC/ngt5ucrCkPBJnYYLpt/ASeiy7HXhWwCO+VE4JG6t6JAtEnEgj7E6sFJTMwipPaZHRa1UicNEMLzvvlMSJDSWF7FyLoFItn1v3CJS+96XuHpXs/7ed56mxGKN7paREuTM2Vy5+DIeooCbx3va6/+r9Ly+1UJ62u1GjdnHdpatctGgH7IZwLHEp0jfq0cZpZb0UnVrfD15y0ht9cLzE9pX06O4Oa9MAH9sjBkwTsFmMjYWUDCnpwsQfvIWsFbPEGxjM5y4NvA5fVuKImC91F0XrbaqieNZeVtkrAhp+SCXkSrQGZASjUsYvOLfq2GBe7w4hW/bmEr8lkW3MLWI5i91l1XtTfhU+5YCFfEnWt9IRVDOcEGJ+MrYkcTtDWI3WUEVEu9KSsZEiCECDIysNdMnK5J+EBxqFn+CvwQnd/XMsLbQm5vEfEJUKnH5CHs+8WSBPTLyyG312ImH2MJ3mkRXPtxZwXgEG5w1jQspViPdlH9EdAVRoNJLX13fJAb89V0LQKxqA/RnROyTSKNQJVy5H+3mBWuzwqF64iQLf6sUATjse66LbzSLWR9rLtekVpKrJYrYmsQu6Ctp4HgOIK2FsVGkxwYCvQa+ivw95QMHEebG++37BhaQNi5jeeluC+3jKDNlQnol8URtDmujHWmhrxPbybgXK0i/MDf6XYYLhK5DwLnANsB3whL7CZZ8C4nCAuin6BTQJuEVYNKLO5DdEgjcNWIuC34XxUK4AJhq0a19I64MhSuN7xvXl9g4TlhooR1t4Rw9YrYUUVt9WL3YeC/QZ3onANtLYopExwYCjxC8HCiCWSoBwL4MdrvcW87rgL60W+YzE1xX06KoM21CeiXKPaxrYx1wrYsJqHQSj86bWCYTLbDcAELga8BP0Vb3ENFJrjjvk4Qjw+9FdgkIFBiYR8ip4RsFgViVbrcFmRe3Mpiy22RBVe6gtoK/le+ll23yPX6+4J7HVUkYGu0ztbqdht4MJgUMHWiU69kx+8lXem+dgeeAs6wY2vognd1ivuyM4I21yegX6JwYRlL8DZHILS6EnIdh32+2u0wXMAu6GIyTxkNt40VvOXhAGcFukUFtGdgyYASyweE6MwYkTviuzvsw5u34MrCILWRpThQTUiKrbslLLtef1/3OgVWW5+ANq8A9v7t9zogxfs76mAxHczBNpsO6WHeCfw6WgK8i+rLLsaV3wJXNfjgGrZI605xX4ad01WRDH/oKMaN1jE+H0d5bg9B0pOQ6zjs85XF4iWDfgr5M3RmjyvRhhoreMfgSnRN89rIi8IsMOAgFvYKmQGZLRasI64LqtCyyxgWXtOG9Pr3+n0PTxoyUXzW3GnMvIFqZVl5RdBal150nuTgzTzdgt22G0JulIO+uiQ+fhnYi/RZ6I4DHo9ArMSF1pDb609xXzZH0JdJ6M/BGJ6LFsLPQDOYkOs47P3MYBlLfR2PdsW7EtjYCt7ROTG422MG8XqfI9YOCdGhA9WEVCNZGbwpx1wiWMrSgWqylLXWo2yFW6jmA9VGseR6RXG5gWqiDPFfBV+ux+x5KAdTJjgsWJ6B1RKa6lba7mUzy0xbUYd90dWqxjfYQCojuOnnUtyfYVuqBhIkosJmqIxzJWO2T3ESWGHi2Mu1bI5HZxr6khW8pZkJ/LOmLSigRachY2GfkK3SuC+oYSEr3W4LeUErXaJ0FB9evFkVfALV3ELXm3xBymJhW49AtRpGgnnokr2BooDxHYq16yU/ubITp1/U2860GG3pvSFlA8kO6Dr3LQ00eGYjsK6k2ZojsMSFPnuuarqt2ElwfBkHXArcSB38n2VKOum0mofyJqFY0Ivsc4RsFSMBap5AtWGrrScrgzszAz6FKQrFrSoKPisVqOYt1+Z1ZQg0UK367x9Vl6lxDrbZLMelt7Tx+mMtsHEujOFqAF3H+xspG0i2I/617oMWaGHf+FtT3J9hW6paCN+NohqiKF7TX8a5ClvYJcVXNRuzc2Xx59Po0uKBBrWlRfCuQCfer26+1yZh5YASi/uRnkA14c3KID2+uz6Ban6pyIoyNrgD1SS+gWruE1SQoWGUaXwEgWp3o6Mug52GK5g+xeGFeVn+dH0nTA79ydDP0Y71T6ZoEHkHjRPIlotApKXZV3ogAsGbhAlEFPvYV8a5Ctuy2JyQ67gt5Pa6sFTLDsDzBFhASKaoc75LNVG9GUChmN+HEAjRVGiVLXBpKJFXd7QlryXd/r0wSkU1PBXVigRr6UC1MYVr8K4MAMfU64ROn+Twk2s76J+bhSlO+DYL7fv6DrS1dyglv5Pj0BbstDNE+D6gU1Pcn1FkI+hMQL9EsY89ZXwedtaEpEz2wt7PVVa31vz7ehLYwgreYk6paO18GrI3+hErB6TskMPleKUrz670Kzghvb66atQMDaJUaWCP+4Lw/k0Z1l1R/F5IgWq/oA7Jo3MObLVJjnueaubOW9th01wUYtfNz4G3An9Jye/kKhojiC3sx4nbpbgvo0i5loRrdFwEbY6Vn3gD4RdYSMK5ykZwvlZiCWKS8iABuA+lTfBeD8wue+0mAb2OEgv7kE0CmdEuC8P5djM+eXVdfrlu3105mpV3OP+uKm0NxsfKyxiBasI/fVlIgWprqZOfa2uz7ufvX9kJ6yV0qKgFL+jAvOPRWQ9uT/jvpAWdMznthJ3HdZsU9+W6CNpMQpWq6RFc02OlT1QRCK0knKvJhF8x8E0sQbA1OpDNCl4Px5e1lgJaJSzsU6JLpyFzB6j5CVi/8sG4A9XAPzcvI2K3qKqaS3n65e0dUbTBB6rVqHjPoA4+kkM52HazIa69u5UX72+DzYfilthlJnAIuljFTQn+nRwJbJvyQTJskbYFMCGlfbkigjanJaBftg+5vdVlTuTCFlobJUTwhh1kaAVvcBxStr4rQRqrgDxtZgKfGVPsrh1S4vU+IdqlsdaqwhRkwxZdVZhnt1Sgmhyl8IRLpAq//Lt++XV90pQFGagmqhe7zwPXBn3ilIJJ4xXLVkt+fnUntCh9hcYzk+EMc43tjHalOYHkuQlcBHwyxQPkmpDbazaTiGdS2JfzI2hzkwT0y9YxFVArI+iHJuKdOzmKJzDLY3T8fwe+RWlDZxM6CHMc2hK+PbAbuhzwHjE5hj8A11Glu1pay959cVTBK4EMinm9Sgw4Uk7MFAnZYd9d6Skf7OeKIMZaVIHYFW5rsEeYBh6oVoIaDcPH1+OkOQ5svUmOr1/WyapnW2C7wSSk7X4BXXTjh8BngWPR1t8kcJixeqxO6TjwRgRtvjulgncJ2o1pYsoFSqVsFXJ7r5e53rKQ92uKEUgvxvhc7RpBm0tj9hueW+V3dwDeg04X9pEIj6EZHVNzVjVfTqNLQ34W/B3fTxTQloEVg4hlA0J2ZhCM+O7KUtZar+g1QrRkpgZ8AtXcAtVj4fX+HVigWvBpyP6OLgMYrNhVsNk0h5kvNHHFdZ2wUeKSIqwCfm8Ez7uBXwKvJGC/jye9vBRBmx9OaV8OmBtmmLw95n2yJfCWkNucVeZ6iyPoj7gHbYbtftIPLIrR8deSoeJV4Argo+Z3GWVxpjOp8mlqWgUvwI/wSwmSBYYcJeb1IiVCZj2BatJdaEINW3qLAtXy71EohIetr6MFqlFCFHuEagwD1aDSTBjlXogCJo93uODaDpzFGZgUi0C1ankcOBvYEfgQOrvDipju6zEpHgOieAz/ftKbj/f1kNvbiWiyIJTL3hG0Wa6Fbl4E+xb3CcqOIbc3m7EzaiSRp9CpLT9D+IHBeb5oBW8xJxS8ylt3F/cjVg2KfBqyovLBJVwTfAPVpE+gmnSJ1RL5eAvE61iBasQmUO071CGRds6BbTbNcdvDLTxwRxtskUtTQcb/oq2oW5n/H4jZ/u0BbJ7S3/9rEbTZZkRvGpkbcntZ4u0etHMEbZb71CIKt5r9Y3yu2oE9Q27z5ZTrq5vM/SMKl7gvWcFbzO3oACtNs4ANOSUW9iHahLHoquLcuvlAtZKuDWr0HLvuYDOvi4JPIBoUiuKiz0UJzRqudfdNtNU8+JGoRZFz4IJrOqFPQKtK47XYi7b0vg/tC3VljPbt7Sn9/b9G+FZJgBNT2p9PRNDme2PcH2ELvHXocqvlsJjwfdj3IPxy3uXybiN6w2QW6WeuuZ+FzZZU8YRFNsAJORLQ1t0WqVjYp8SGnJBtcsQ1QY64LQy7L3gtt0WC2Cd1GYWBasLjk1uQb9eTxcGtSL2uDKLE/yVV7Ghit/rh6NR6nJycA1tvluOKf7Uz5+FW2DyXhEC1WnnEiKLtgctjsD+7pbivn4+gzUNJRoaBSnkqgjYPiGlftAEHhdzmbCrLghD2+ZoYkfgphyh86x+hMXgRnf0h9pNh2RAnQ3EdbRJWDSLe6Bdy3EhFtSJRKzypxjxWXlz5dEu5PHhTj0lRbOH1C1rzE6RiDCFbptYt48NRmQn8sy4jZKdi3hsZLrqmA9odXeq5cXgN7RO9B3B/hPuxd6p//9Hw7RT25UuEn2ZpP8Iv7lAOHyf8nK6zK1z/oQj65bMxvXbDTr84RGNYePNcQPhpIA+gwrzn8RC8Cu1u0J7R+XGDXjoyX6RJDjG3V8ghR4gmUWzV9auoJguLTRSJ2zID1QqKTbj1rvBoUBnbQLUT6nHaBbDRRIfLbuqg55UmmOY0gnXXj+eNtei8iNrfKsV9+1BE7Z6GTtWUJpyI+vPoGPbF4RG0eW+F6z8bwT4eEcNztT3hF9l5nugCuqIi7MwNe1JhfuB45OHNCFgzBH2O/jtomsQ61g2dJ9cM/VR2ZozIVQWV1USpKmqyhJuCV8RKT85dtxD1BqS5fXndPr7D6r9QwEYcqHY5dUrvJAQMObBwaQYyic7KEBQ/QefPvCLkdieaKySNZ+Be9GPgsK1x0vx20lbY4ynCt+J9AfhFjPqgFZ2eKe6CdybQQ7i+q1PQQZv3xuh8fSGCNu9uwPvXnWaiHxabUGFawOgFrwCaBWJxP+L1PmiTwbu9K0DxM9EqvyYybDQsXIuEbmGgWj7tmPRLLyY9PrieTAzDQldSFKiWF7DSR9iKUYpJRGDdzaGLKtQNpaCzXWlXBit4Af6MdjH4UohtjjM38d4U9mcfcA/RJEs/DG25vy9F/fkv4Kcht7k9sA/R+BD7cRrahzdMHqHyaPgec+19POR9PSdGgldSp/iTMbi9Ae9dYecczlKhu1N8XBoygky71EtbHZYOicxysnRZdf1z7xaXD8YnW4McLVANnyA1PIFqbiUqiiuqjRmoVqaqrdE6/H9At9WgoXM6fjmk60cr4UcwN8rN5zbTv2nhRaJJ93ZRjPrgmxG0+Z8qv/fPCPb1A4Sf87YUxxJ+Lufl6FzsjUZXBG1OTJ7gNZpXSMg2QSYDmWyAS5P5X3KbEGqGO8uCdKUaK1kaWBYHqo32N97AtdEC1cqsqDaaiK1ToNpS4FKrPSMVSmFaQbIp7ss7Imy7g/Q93oyiytKBRFMa1ssRwMYRtHtLyN9LywTlxxG02YjuDLWpjeqp6ElLvLI0KJAZRaZJkcnUYckqZIYTi9OOqcJANDlKqeASr0EVWXHzgrZUoFphIFrsAtWOxhIlc0JsawhdBjOtLAIejbD99xCP9HNB8c+I2v1zDI79NxG0+YJZqmE18FgE+/wxYN+Iz9UZwKYRtHt1g96zJsZdZMcuLZkQkM0YK299lpeE5E9SFvriSlnouyt9AtVGq8JWJHJLBKoVVVrLpzrzCNhaA9Vq/P79pMvvMIkMhthWD+l3XYn6acVJwCUp6cunCb/qGmg/3lMiPO4fA1MjaPePNX7/soj6628RnqsJRBPouBQdM9CIbBFBm32JFrxKuVwbsnVaJF8WgqG8K4P08831EbuypDuDTxoyj2vDaJbZelRUq5ETsUTNtBDb6gpZYEfBjZUOjnXgdOC6lPTnryNq949EUwp7D6JJrh+EcLzeTGrDZmvgtxH12T+A5gja/TONSxQl1SvyG45f4Qmd1YtMBmS9liw9QnKm9OTTdQerMZpFl0KxCz6f4x94JioIVGMsQVufQLXfAAsa4Md5uLl5xrUi1oEhtrWqAc73IPCnGOzH54Fn0JkHkkyUN/awnz61AHdFdKw3o8u610IOuDai/T+D8HMWnwt8MKLj/UMDC95PR9DmymQLXkx2KgHZjCIj67b8Xgi1qCA7g/QEqlHsslBkBaZEzl1v2WC3T26ZgWpiFBFbp0C1DcDXG+TH+XH049G5aP/Kt8do394JvCPE9l5qkHMel0CaPU2ffzXBfdlNdL6K2xPuY+MHCPeJi5vvBbSdCyK8Vq6nijKwVfI5dD7zKPgn8HqDit3PEr7xaCXwRuIF7/DOZeq7CMkRjOKTW2ThxZuJoTizg1vclrLyjlh7YxeodiYw0CA/0J3M/21o/8onjOXoS0QT6JAnC9wUcptPN8g5X0S0foVuMkaAPwV8Jib7VOnwcV6E+/p+wkk3dy/RBV89BfwvoG3NB/4d4fm6Hzg4BLF7Q4THeD6NSxSW7Zcr/X3EV/Aa14Zstq4BbI8JyX9kqUA1Cn13fYPUqghU873LRF9R7VXgqgb5cba7BK+b96GDm+ab2fpxwHYh7tdWwJPAZiH3x+wGGpjj9gRjb7S/4Ux0ztBJIbe/KXAy8AMqL/2ymOjSXoF+SvME9SmN/RYzETwowuM7I+DtnRvxtX6nMarU63cdpdh9KMDJSdL4VwTjFsDDZgxKgeA1o6+UxiIr67QITsQbqCaLq6kVuyco3wwNRdbdksJWjSmAvX/7vQ5G6wJwZAP9QPdn9GIAzegqWVeh04M9jq4u9WHqk4NznBmwX6bC2uABMGREdqOwhHj62b0D+Avaf/4vaKvv9Dq0swXwIXTGgXuBhWjf5u9QXZDPVyLut7eb381JAW7zaHSBjb0iPK4ZZhIUJLOMOImS35hJUlCBh5ujLf0/i/i4vtygYvd6dAq6qH4jFZGIZPOZ+padXQr8XAi+jjfozC/vrqtIBfh8DqOnITNSvuJKamNQ4/fvpHEea0Pljyj3Ncs30MFPT5vZ/KvAPLRFeC7lR4xKYAcjcvYHPoWuQR8F9wDrG2yQ/j90JpKmGO7beLSl91i0e9EMI1TmGHH6hhmz1qBzJ+fMLz9jBOsEdOqsjdF+p9PRTwy2AnY2112pkWIrc01XwgLgSuCECPusBe2Hfxw6K8A/qtzOR4CzqP+j93I4rU7bPYvwSw17OdT08cVmsjW/SqF7qjEURF3J8Fbg2QYbQ/dGp1mMyt1nAO1bny7Bq1xCs46i91wEJwupJnmtu+50ZRRlaHAJWT+RW6qimk9Qmp9wrSgNWW3m3WMb7Me6Xw3fbXIJYDddwAojRLqAdeb/nPm83YjayWjn/s1i0hfXNKBVoh/4AvFPIdQMHGAWNzlzbfW6rq8mc+PvrGFc370KwQvaunV0DCYQ7zHLa+g0dA+igwMXlbh7TAfeCrwb7f+5Z4x+k8/XadvzzaTgzIiPsQXtYnGOEYy3o7OXzCthOGhBu5ftAXzCGAniMmE9NSHj3lAA29gdHex9esTHcnM1hprElBOVUuforRM54IsCrvcGrIE3964qtgCDryuDv0tDiUA1UYa7Qn1cGS6k9rQ3SaLJ3OCCZrxZkkTOCINGJG+VfE8C9z2D9pkL2m9uL6qzjnah/U3j4iqynRFS55jXi9HR8+vRuZjb0BbCbYkmV+to9FI/626e/BOOjhgcr0C7jx3mupbmm3tSr/l8KrANsFEMf4vfMIaOJFCNgupAPxV6H/rJwPticixV5QFPjOAVQiEzpjBFfa6EGwScK2D3UtkZhFC+7gpjuTJ4yw+7BWwpIVuRi0L1HdJD9IEMYbMj2gpm0aJvoIGP/1PAMiMgLVoAVssf0ZaffWJ4XJsTTbGKajia+heJUGiL9h0xPP7xaCtiEngZ+HmCft+HA7ug4xjWodOQDnqui2Z0ieBNzbIl0QSkjcZrVOG/myjBCyNWXlG/UeAoIZhdYHUVaI9LH99erylXiBL75nFliFGg2ik0HvtiyfPdBj/+N9HJ0m+xlwJQe0aSQ8zN1FId/0A/qg2Df6MDjj5vu71qDk7Y/k6h2D0qiVTtjiOTcoR5W3w9K7BlMryA4HrpE4g2HKhWquiERyQLr8hljIpqVVDj92cTn5ykYXIgFtBFA5babuBWoo/wjgs7oB/3V8tSGi8eICiWo62uYXIUFSbutwxzCjqI1BIuzwD/Sb3gHd5hWd8lk+FkBE6pvLveqmq+mRg8llq3MB6rsESIgWrHNegP5p12zCCH9uOzaL5JtEn540IHtQduXUPj5PMOkih8Ix10qkVLZVyJzgpiCZ+a0qfKJB5xkY9tsEu3gG8VZmNQJQtP+Prk5l/L0oFqUJvYrdGV4R9mptRobEe4hSTiyhnAWtsNBXyM+kXGJ+03UisnNOj4Ui2fJbry3i8AR9hTUDZPoAP+LOFzPvBKQwnevA9vnS29P0WwpCBQzaM2hdfP17c4BVQSqFaZ6q9Z8DQie9kxgweA39tu8GU/dCBKI7NrQNs5COvPWw7nEH2mlOuB79tTMSYLiE+WgkbjMXQ1yJqQST16KY2lt37LyZQqQuEtKezRoAVW2/gFqn2H5KRRCZrdG3zQWI8OLLL40412eXmhgfsgqCwL68wEotteViW5EF3BMQ58j3hWIIwLK83Y0GO7IpK+/1AgujHJvVBn0XunQD3g6/ZAcWU134pqonSgWk05d6tXvG8CP2rgH857G3zgOIDGq6pWKV3oKkKPNOjxbxvgthZYkVCSXxK/lJCnof1TLYUsRfu2r7BdETqD6MxKgUycEy14hVDIOi5CcmJBJYm82PSpMOFbUQ2f/6PLuQtwfAP/cNqAtzfw8X8MeM6On2XRjy5IcV0DHvvm6JLEQfGC+d2tsZfVMN8Fzo7pvp0IXGpP0TCvoKu72WwW4dOHfuI0L6gNyqT3iBR1XeYDv/Wz8LqFarkV1fwEboiuDE8Tz0TjYbE/8SlFGTaHYbMQVMORNF5hFoFOTxYkLwK7UV3Z4rRxEvDDmO/jGdgc3QB3G7FrLbvhsxQdczMrUL2Y+NFZmqOo0yIEZwN9XpcF6ROoVk1FtYpvRdXT6JG4+zXgMfcC70fnmrVUx4XAB9CP5xuFejwJWYz2of9Xg15Hy9AuVX9OyP7+EF31rVH5JTplW58dAkPnYTPRCDxziUxD79TVyisZEoIv4UpDJkQJDRrfQLUrgTkN/iPapcGO93kjMO6z42fN3AvsnCCxUis71mm7fcAnCCDaOmHcbq6fBxO239eifVdfbKBz1YV+snM2lqgmGgdQJ6t6KgSvEHVPUzYiGEepqFZLoBpjid3qFe8gjZuGzM27GuhY/2RmyHPsaQ+MHvTj6IOpMRdkAtipzts/H/3kYVHK+zGHLoN6CLA6ocfwnBHrjZDK8HYz2WtE3/2oeQ2diaGuEw2Zlt4S3mCygBchOL6olHCpSmn4W35HE7Gj6tnazLvfxEZJvw/YtAGOcx7waeBUO37WjbvMTfHr6KwnaWQPoLXObdxn+vE3Ke3DG83xXZKS4/miEe5pzFO9GF0s5RBsufWwWQecZ34r/613YzJNPVfH6msIwWMIbvMNVBulolrJDA3huDIsQD8isD8qmJni49uATje3A3CzPd2hcBE6hdcPgOUpO7ZOgk1PVopedInr/YF7UtJ3jxnh9Fm01SpN3A68zVzzaciv3IPOg7wDthx2FJOMH5tx5ifopyF1J1WCt84ZG5BCW868rgya8gPVKhKxtSnek+3vCtBlTvcFPm4G7VxKjmuNEV7bowuK5OypDpUu9OP5bdCP4l5K0bGF6QL0KPBBdDaRpE5MnwQORwfH3p7y6/58M+b8lmQGda0DLkaX0T7HTLws4fAw2jVsG+DbwKpQNWLaerPOrg3LESMlGN3CNoaBavehg20sI9xhrC9box9JP57Q45gFfMV1HMvsqY2UHvSTlJ3Qkd3XkGwL2P3ooMewudVMTA8mOdkcbjJi/R3A3xvoml8KnGWEyw9IhivAK8DXgK2Ar2LdF8LiITOx2AEdkPZndGxR6GRT17Wi9uxfY/A9ITgdmIrLulvQtte6G36gGsBx9ndWktfRltGL0L5D70EXZngXwSbdD3qwvh24BW0RiwMT7VhVxN1mORudhupQtA/5ZjHf76fM9XU78GzE+3KXWXYGPgN8jvoH0lXC40bc3kaASfETLHzPBy5AP0E7BvhojH6vy8zk6Qbi6TbTkrLrYchMlmeaifOTwMK47FyqBK+iOG1YnThJCG71Wnd9U5SNImLrGKj2W7SPjGVsXjbLn9DV2N6NTsWzF7os6jYR7ddytD/gDPN/HEvdLgEmh9TWQMKuqzfRgUs3mnH2ALQVcB+0FTNKAdyDroD2BNrd5zHiGYz0glm+DxxoJg4fMP2XCXE/VgIPoB/H3g/MtsNmEX2u630LdMT9e4GDCDdgWKGLLN1vxsy7ibfbxSJG4i92QVc6HJ+A8+2Ye1T+/jnH/P+8uS/EEqGUKnvlb//7oDrsAdAs4YVumlf20zRB0NQMTU2QbVJkmyCbhUxW6f8zI4vMKP2/SR8m8v+LUETvU0KwN0KVTD9WJHArcWWofv+7gXEMm53jixSw2bQcZ1w0gTv+0Q6bxtIFdSszEO2OdiHYwgxKmwCTAhK2i80yH+0H+oT5vz/mp3B8iMKji/T4KGfQPpB7oq2Y+etqY3NddQbQxnpzba0wN6CFwFx0mqk5JDvDxGZmMrovOohqG3TwSxDWstfRwb6vGrH9GNqFyPp5VkeTuc73B3Y1wm4bc63Xyjpzruahn4A9ZcbO1xPcXxOB6eYa38qMB5OBKcAEM+Z2Au3m/1bzfwu1VRLNmYlBl9EQ683fXeiUeqvMWLIYHYz5Btp6PhR1h6mZ5aeaz6b1V1Zvwavg88CcGAaqfTkJYjdBLDCL16ew0wjfaUb4jjdLfiDKus7DgBlAes0Asg4dcLbcDM4DCe2bLnt5VH1zyVtGvExGW8Smmhtcp+uG1mZuasJYWHrNtdPjura6zM1pqbnG0sgb6Gwk7owk043onW76broRCZNMv0kz0Rg0E8k1rt/gm+Zm/oaZFNjxMzgGjQh9wiOCtzOTvPx5mm7E3gRznrLmGh8y4+U6c65WmWWJEV5pG4PWmqXcXN9Zc99pNdd5i1kyrn4UrmtauMagQdO/Pa4xZC0pDn7OpvmXJuo4bgl4DcHVwLExClR7BbjcjrGhsGEU0WKxVMtqklukIEqWk770cGkWwS+RrqwmUTFkx4vykak+ujoXowBOR9BfdiW1Mna3RsV7gr2kLRaLxWKxWBpJ8NZf9G4QupKZS7FWb92tUezeSnLTbFksFovFYrFYwRtPvQvArxHMi7iiGsAX7OVssVgsFovF0oCCF+pechgBJ0UcqPZDrP+axWKxWCwWS+MK3rqbeQUPAPdHFKi2DviuvZQtFovFYrFYGlnwhsNJ1WrxGhXvSbbrLRaLxWKxWBpd8KqRimh1XOYj+I2fiK1joNosdC13i8VisVgsFktDC97wOAfoKRKx9QtUO952ucVisVgsFosVvEUis45Lr4DTyxa1tSne64Bn7SVssVgsFovFYgVvqIoXwVXokof1DFTL4RHWFovFYrFYLBYreMPk82UJ7+o5F10H3mKxWCwWi8ViBW9pvVnH5WkBt5a07tYmdpcCP7eXrsVisVgsFosVvJEqXgQnAcqv2Ro53l62FovFYrFYLFbwxoFVwPcIVvE+Btxtu9ZisVgsFovFCt648ANgRTBaF4CjbJdaLBaLxWKxWMEbN75AMIr3CmCB7U6LxWKxWCwWK3jjxi3AzBqtu/3AWbYrLRaLxWKxWKzgrYr6x69xTI3W3bOAHnumLBaLxWKxWKzgjavqnQN8rco9uw34oz1BFovFYrFYLFbwxp1fAN+p8DvXAYfarrNYLBaLxWKxgjcp/Aj4KPD0GOstBE4FjrRdZrFYLBaLxVIbWdsFoXOnWT4KHARsD3QCfegsDI8CNwA521UWi8VisVgstSOUUrYXLBaLxWKxWCypxbo0WCwWi8VisVis4LVYLBaLxWKxWJLK/w8AUxe8BwKxi0YAAAAASUVORK5CYII=";

    private String buildHtml(String dataHora, String destinatario,
            String litros, String valorPorLitro, String valorTotal, String endToEndId) {

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'/><style>"
                + "body{font-family:Arial,Helvetica,sans-serif;max-width:580px;margin:0 auto;padding:24px;color:#333;font-size:13px}"
                + ".header{text-align:center;padding-bottom:18px;margin-bottom:18px;border-bottom:3px solid #00843D}"
                + ".title{color:#00843D;font-size:16px;font-weight:bold;margin:10px 0 3px}"
                + ".subtitle{color:#666;font-size:12px}"
                + ".badge{background:#00843D;color:white;padding:4px 16px;border-radius:20px;font-size:12px;display:inline-block;margin-top:8px;font-weight:bold}"
                + ".amount{font-size:32px;font-weight:bold;color:#00843D;text-align:center;margin:18px 0 4px}"
                + ".amount-label{text-align:center;color:#888;font-size:11px;margin-bottom:18px}"
                + ".section{background:#f5faf5;border:1px solid #d4ebd4;border-radius:6px;padding:12px 16px;margin-bottom:10px}"
                + ".section-title{color:#00843D;font-weight:bold;font-size:10px;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;padding-bottom:5px;border-bottom:1px solid #cce4cc}"
                + "table.info{width:100%;border-collapse:collapse}"
                + "table.info td{padding:4px 0;font-size:12px;vertical-align:top}"
                + "td.lbl{color:#888;width:38%;white-space:nowrap;padding-right:10px}"
                + "td.val{color:#333;font-weight:600;text-align:right}"
                + ".e2e{font-family:monospace;font-size:9px;word-break:break-all;color:#999;background:#fff;padding:5px 7px;border-radius:3px;border:1px solid #e8e8e8;margin-top:4px}"
                + ".divider{height:1px;background:#ddd;margin:6px 0}"
                + ".footer{text-align:center;color:#bbb;font-size:10px;margin-top:18px;padding-top:12px;border-top:1px solid #eee;line-height:1.5}"
                + "</style></head><body>"
                + "<div class='header'>"
                + "<img src='data:image/png;base64," + SICOOB_LOGO_B64 + "' style='max-height:50px;max-width:200px'/>"
                + "<div class='title'>Comprovante de Pagamento PIX</div>"
                + "<div class='subtitle'>" + dataHora + "</div>"
                + "<div class='badge'>&#10003; Pago com sucesso</div>"
                + "</div>"
                + "<div class='amount'>R$&nbsp;" + valorTotal + "</div>"
                + "<div class='amount-label'>Valor pago</div>"
                + "<div class='section'>"
                + "<div class='section-title'>Origem &#8212; Pagador</div>"
                + "<table class='info'>"
                + "<tr><td class='lbl'>Nome</td><td class='val'>Favo de Mel Transp. Escolar e Turismo Ltda</td></tr>"
                + "<tr><td class='lbl'>CNPJ</td><td class='val'>03.660.921/0001-79</td></tr>"
                + "<tr><td class='lbl'>Institui&#231;&#227;o</td><td class='val'>Sicoob</td></tr>"
                + "</table>"
                + "</div>"
                + "<div class='section'>"
                + "<div class='section-title'>Destino &#8212; Benefici&#225;rio</div>"
                + "<table class='info'>"
                + "<tr><td class='lbl'>Nome</td><td class='val'>" + destinatario + "</td></tr>"
                + "<tr><td class='lbl'>Tipo de chave</td><td class='val'>CNPJ</td></tr>"
                + "<tr><td class='lbl'>Chave PIX</td><td class='val'>75.415.075/0001-32</td></tr>"
                + "</table>"
                + "</div>"
                + "<div class='section'>"
                + "<div class='section-title'>Detalhes do Abastecimento</div>"
                + "<table class='info'>"
                + "<tr><td class='lbl'>Quantidade</td><td class='val'>" + litros + " litros</td></tr>"
                + "<tr><td class='lbl'>Pre&#231;o por litro</td><td class='val'>R$&nbsp;" + valorPorLitro + "</td></tr>"
                + "<tr><td colspan='2'><div class='divider'></div></td></tr>"
                + "<tr><td class='lbl'><b>Valor total</b></td><td class='val'><b>R$&nbsp;" + valorTotal + "</b></td></tr>"
                + "</table>"
                + "</div>"
                + "<div class='section'>"
                + "<div class='section-title'>Identifica&#231;&#227;o da Transa&#231;&#227;o</div>"
                + "<table class='info'>"
                + "<tr><td class='lbl'>Tipo</td><td class='val'>PIX Enviado</td></tr>"
                + "<tr><td class='lbl'>Data/Hora</td><td class='val'>" + dataHora + "</td></tr>"
                + "</table>"
                + "<div class='e2e'>" + endToEndId + "</div>"
                + "</div>"
                + "<div class='footer'>"
                + "Este documento &#233; um comprovante v&#225;lido de pagamento realizado via Pix.<br/>"
                + "Emitido pelo sistema Tefamel &#8212; Favo de Mel Transporte Escolar e Turismo Ltda"
                + "</div></body></html>";
    }

}
