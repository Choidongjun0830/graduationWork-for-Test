package graduationWork.server.repository;

import graduationWork.server.domain.Transactions;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionsRepository {

    private final EntityManager em;

    public Long save(Transactions transactions) {
        em.persist(transactions);
        return transactions.getId();
    }

    public void delete(Transactions transactions) {
        em.remove(transactions);
    }

    public List<Transactions> findAll() {
        return em.createQuery("select t from Transactions t", Transactions.class).getResultList();
    }

    public Transactions findById(Long id) {
        return em.find(Transactions.class, id);
    }

    public List<Transactions> findByUserId(Long userId) {
        return em.createQuery("select t from Transactions t where t.user.id = :userId", Transactions.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Transactions> findByFrom(String fromAddress) {
        return em.createQuery("select t from Transactions t where t.fromAddress = :fromAddress", Transactions.class)
                .setParameter("fromAddress", fromAddress)
                .getResultList();
    }

    public List<Transactions> findByFromAndValue(String fromAddress, String value) {
        return em.createQuery("select t from Transactions t where t.fromAddress = :fromAddress and t.value = :value", Transactions.class)
                .setParameter("fromAddress", fromAddress)
                .setParameter("value", value)
                .getResultList();
    }

    public List<Transactions> findByFromToValue(String fromAddress, String toAddress, String value) {
        return em.createQuery("select t from Transactions t where t.fromAddress = :fromAddress and t.toAddress = :toAddress and t.value = :value", Transactions.class)
                .setParameter("fromAddress", fromAddress)
                .setParameter("toAddress", toAddress)
                .setParameter("value", value)
                .getResultList();
    }
}
