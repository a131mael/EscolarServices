package org.escolar.service;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.escolar.enums.CanalMensagem;
import org.escolar.model.Aluno;
import org.escolar.model.MensagemAluno;
import org.escolar.util.Service;

@Stateless
public class MensagemAlunoService extends Service implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public MensagemAluno findById(Long id) {
		return em.find(MensagemAluno.class, id);
	}

	public void save(MensagemAluno mensagem) {
		Aluno al = em.find(Aluno.class, mensagem.getAluno().getId());
		mensagem.setAluno(al);
		mensagem.setDataEnvio(new Date());
		em.persist(mensagem);
	}

	public List<MensagemAluno> findByParam(Long idALuno, String mes, String ano, CanalMensagem canal) {
		List<MensagemAluno> mensagens = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select id from MensagemAluno mensagem  where 1=1 ");

			if (idALuno != null && idALuno > 0) {
				sql.append(" and aluno_id = ");
				sql.append(idALuno);
			}

			if (mes != null && !mes.equalsIgnoreCase("")) {
				sql.append(" and mes = ");
				sql.append(mes);
			}
			if (ano != null && !ano.equalsIgnoreCase("")) {
				sql.append(" and ano = ");
				sql.append(ano);
			}
			if (canal != null) {
				sql.append(" and canalMensagem = ");
				sql.append(canal.ordinal());
			}

			Query query = em.createNativeQuery(sql.toString());
			List<BigInteger> codigo = (List<BigInteger>) query.getResultList();
			if (codigo == null) {
				return new ArrayList<>();
			}

			for (BigInteger id : codigo) {
				mensagens.add(findById(id.longValue()));
			}

			return mensagens;

		} catch (Exception e) {
			return mensagens;
		}
	}

}
