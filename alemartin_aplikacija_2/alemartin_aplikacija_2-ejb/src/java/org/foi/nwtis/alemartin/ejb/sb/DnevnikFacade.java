/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ejb.sb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.alemartin.ejb.eb.Dnevnik;

/**
 *
 * @author TOSHIBA
 */
@Stateless
public class DnevnikFacade extends AbstractFacade<Dnevnik> {

    @PersistenceContext(unitName = "alemartin_aplikacija_2PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikFacade() {
        super(Dnevnik.class);
    }

    public List<Dnevnik> getLogRecords(String username, String ipAdresa, Date datumOd, Date datumDo, 
            String adresaZahtjeva, String trajanje, int start, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Dnevnik> dnevnik = cq.from(Dnevnik.class);
        List<Predicate> conditionsList = new ArrayList<>();
        if (username != null && !username.isEmpty()) {
            conditionsList.add(cb.equal(dnevnik.get("korisnik"), username));
        }
        if (adresaZahtjeva != null && !adresaZahtjeva.isEmpty()) {
            conditionsList.add(cb.equal(dnevnik.get("url"), adresaZahtjeva));
        }
        if (ipAdresa != null && !ipAdresa.isEmpty()) {
            conditionsList.add(cb.equal(dnevnik.get("ipadresa"), ipAdresa));
        }
        if (datumOd != null && datumDo != null) {
            conditionsList.add(cb.greaterThanOrEqualTo(dnevnik.<Date>get("vrijeme"), datumOd));
            conditionsList.add(cb.lessThanOrEqualTo(dnevnik.<Date>get("vrijeme"), datumDo));
        }
        if (trajanje != null && !trajanje.isEmpty()) {
            conditionsList.add(cb.equal(dnevnik.get("trajanje"), trajanje));
        }
  
        cq.select(dnevnik).where(conditionsList.toArray(new Predicate[]{}));
        List<Dnevnik> d = new ArrayList<>();
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(max);
        q.setFirstResult(start);
        return getEntityManager().createQuery(cq).getResultList();
    }
}
