package dao;

import domain.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;



public class UserDao implements Dao<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Override
    public void save(User user) {

        Transaction t = null;

        try (Session session =
                     HibernateUtil
                             .getSessionFactory()
                             .openSession()) {

            logger.info("Начало транзакции");

            t = session.beginTransaction();

            session.persist(user);

            t.commit();

            logger.info("Транзакция успешно завершена");

        } catch (Exception e) {

            if (t != null) {
                t.rollback();
                logger.error("Откат изменений");
            }

            logger.error(e.getMessage());
        }
    }

    @Override
    public Optional<User> get(Long id) {

        try (Session session =
                     HibernateUtil
                             .getSessionFactory()
                             .openSession()) {

            return Optional.ofNullable(
                    session.find(User.class, id));
        }
    }

    @Override
    public List<User> getAll() {

        try (Session session =
                     HibernateUtil
                             .getSessionFactory()
                             .openSession()) {

            return session
                    .createQuery("FROM User", User.class)
                    .list();
        }
    }

    @Override
    public void update(User user) {

        Transaction t = null;

        try (Session session =
                     HibernateUtil
                             .getSessionFactory()
                             .openSession()) {

            logger.info("Начало транзакции");

            t = session.beginTransaction();

            session.merge(user);

            t.commit();

            logger.info("Транзакция успешно завершена");

        } catch (Exception e) {

            if (t != null) {
                t.rollback();
                logger.error("Откат изменений");
            }

            logger.error(e.getMessage());
        }
    }

    @Override
    public void delete(User user) {

        Transaction t = null;

        try (Session session =
                     HibernateUtil
                             .getSessionFactory()
                             .openSession()) {

            logger.info("Начало транзакции");

            t = session.beginTransaction();

            session.remove(
                    session.contains(user)
                            ? user
                            : session.merge(user)
            );

            t.commit();

            logger.info("Транзакция успешно завершена");

        } catch (Exception e) {

            if (t != null) {
                t.rollback();
                logger.error("Откат изменений");
            }

            logger.error(e.getMessage());
        }
    }
}
