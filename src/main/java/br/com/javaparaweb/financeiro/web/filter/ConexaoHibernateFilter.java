package br.com.javaparaweb.financeiro.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import br.com.javaparaweb.financeiro.util.HibernateUtil;


@WebFilter(urlPatterns = { "*.jsf" })
public class ConexaoHibernateFilter implements Filter {
	private SessionFactory sf;

	public void init(FilterConfig config) throws ServletException {
		this.sf = HibernateUtil.getSessionFactory();
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws ServletException {

		//Pega sessao corrente do app web
		Session currentSession = this.sf.getCurrentSession();

		//transacao do hibernate
		Transaction transaction = null;

		try {
			//inicia transacao hibernate
			transaction = currentSession.beginTransaction();
			//vai para pagina solicitada
			chain.doFilter(servletRequest, servletResponse);
			//Volta da pagina chamada e salva a transacao
			transaction.commit();
			//Fecha transacao caso aberta
			if (currentSession.isOpen()) {
				currentSession.close();
			}
		} catch (Throwable ex) {
			try {
				//em caso de excessao, desfaz a transacao
				if (transaction.isActive()) {
					transaction.rollback();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			throw new ServletException(ex);
		}
	}

	public void destroy() {
		
	}
}
