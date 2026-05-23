package dao;

import domain.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class UserDao implements Dao<User> {

    @Override
    public Optional<User> get(Long id) {
        if (id == null) {
            log.warn("Попытка получения пользователя с ID: null");
            return Optional.empty();
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.find(User.class, id);

            if (user != null) {
                log.debug("Пользователь найден: ID={}", id);
                return Optional.of(user);
            } else {
                log.debug("Пользователь с ID={} не найден в базе данных", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Критическая ошибка при открытии сессии Hibernate: {}", e.getMessage());
            return Optional.empty();
        }

    }

    @Override
    public List<User> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        } catch (Exception e) {
            log.error("Критическая ошибка при открытии сессии Hibernate: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public void save(User user) {
        if (validateUserNotNull(user, "сохранения")) return;
        executeInTransaction(session -> session.persist(user));
        log.debug("Пользователь добавлен. ID: {}.", user.getId());
    }

    @Override
    public void update(User user) {
        if(validateUserNotNull(user, "обновления")) return;
        executeInTransaction(session -> session.merge(user));
        log.debug("Пользователь с ID {} обновлен.", user.getId());
    }

    @Override
    public void delete(User user) {
        if(validateUserNotNull(user, "удаления")) return;
        executeInTransaction(session -> session.remove(user));
        log.debug("Пользователь с ID {} удален.", user.getId());
    }

    private boolean validateUserNotNull(User user, String operation) {
        if (user == null) {
            log.warn("Попытка {} null-объекта User.", operation);
            return true;
        }
        return false;
    }

    private void executeInTransaction(Consumer<Session> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                action.accept(session);
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                log.error("Ошибка в транзакции: {}", e.toString(), e);
                throw e;
            }
        } catch (Exception e) {
            log.error("Критическая ошибка при открытии сессии Hibernate: {}", e.getMessage());
        }
    }
}
