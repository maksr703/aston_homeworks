import dao.Dao;
import dao.UserDao;
import domain.User;
import lombok.extern.slf4j.Slf4j;
import util.HibernateUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Dao<User> userDao = new UserDao();

        while (true) {
            System.out.println("1. Добавить пользователя");
            System.out.println("2. Удалить пользователя");
            System.out.println("3. Изменить пользователя");
            System.out.println("4. Посмотреть всех пользователя");
            System.out.println("5. Посмотреть пользователя по ID");
            System.out.println("0. Выйти");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> {
                    User user = new User();

                    System.out.print("Name: ");
                    user.setName(sc.next());

                    System.out.print("Email: ");
                    user.setEmail(sc.next());

                    System.out.print("Age: ");
                    user.setAge(sc.nextInt());

                    user.setCreatedAt(Instant.now());

                    userDao.save(user);
                }
                case 2 -> {
                    System.out.println("Id: ");
                    long id = sc.nextLong();

                    Optional<User> userOpt = userDao.get(id);

                    if  (userOpt.isEmpty()) {
                        log.info("Пользователь не найден :(");
                        continue;
                    }

                    User user = userOpt.get();

                    userDao.delete(user);
                }
                case 3 -> {
                    System.out.println("Id: ");
                    long id = sc.nextLong();

                    Optional<User> userOpt = userDao.get(id);

                    if  (userOpt.isEmpty()) {
                        log.info("Пользователь не найден :(");
                        continue;
                    }

                    User user = userOpt.get();

                    System.out.println("Name: ");
                    user.setName(sc.next());

                    System.out.println("Email: ");
                    user.setEmail(sc.next());

                    System.out.println("Age: ");
                    user.setAge(sc.nextInt());

                    userDao.update(user);
                }
                case 4 -> {
                    List<User> users = userDao.getAll();
                    users.forEach(System.out::println);
                }
                case 5 -> {
                    System.out.println("Id: ");
                    long id = sc.nextLong();
                    Optional<User> userOpt = userDao.get(id);

                    if  (userOpt.isEmpty()) {
                        log.info("Пользователь не найден :(");
                        continue;
                    }

                    User user = userOpt.get();

                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                    .withZone(ZoneId.systemDefault());

                    System.out.printf(
                            "Name: %s%nEmail: %s%nAge: %d%nCreated at: %s%n",
                            user.getName(),
                            user.getEmail(),
                            user.getAge(),
                            formatter.format(user.getCreatedAt())
                    );
                }
                case 0 -> {
                    HibernateUtil.getSessionFactory().close();
                    System.exit(0);
                }
            }
        }
    }


}
