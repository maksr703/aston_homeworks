package dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.specification.SelectionSpecification;
import util.HibernateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AbstractDao<T, ID> implements GenericDao<T, ID> {

    private final Class<T> entityClass;
    protected final SessionFactory sessionFactory;

    protected AbstractDao(Class<T> entityClass, SessionFactory sessionFactory) {
        this.entityClass = entityClass;
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void save(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            T entity = session.find(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            SelectionSpecification<T> specification = SelectionSpecification.create(entityClass, "FROM" + entityClass.getSimpleName());
            SelectionQuery<T> query = specification.createQuery(session);
            return query.getResultList();
        }
    }

    @Override
    public void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(entity);
            session.getTransaction().commit();
        }

    }

    @Override
    public void update(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(entity);
            session.getTransaction().commit();
        }
    }
}
