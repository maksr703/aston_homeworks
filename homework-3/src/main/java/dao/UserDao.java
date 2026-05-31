package dao;

import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.specification.SelectionSpecification;

import java.util.List;
import java.util.Optional;

public class UserDao extends AbstractDao<User, Long> {

    public UserDao(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }

    public Optional<User> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            SelectionSpecification<User> specification = SelectionSpecification.create(
                    User.class,
                    "FROM User WHERE email = :email"
            );
            SelectionQuery<User> query = specification.createQuery(session).setParameter("email", email);
            User user = query.uniqueResult();
            return Optional.ofNullable(user);
        }
    }

    public long countAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createNativeQuery(
                    "SELECT COUNT(*) FROM users",
                    Long.class
            );
            return query.uniqueResult();
        }
    }

    public List<User> findUsersWithPagination(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM User u ORDER BY u.id";
            Query<User> query = session.createQuery(hql, User.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }
}
