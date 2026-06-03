package util;

import domain.User;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    @Getter
    private static final SessionFactory sessionFactory =
            buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {

            logger.info("Конфигурирование Hibernate...");

            return new Configuration()
                    .addAnnotatedClass(User.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            logger.error("Ошибка инициализации SessionFactory", e);

            throw new RuntimeException(
                    "Не удалось создать SessionFactory", e);
        }
    }
}
