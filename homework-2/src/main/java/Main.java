import dao.Dao;
import dao.UserDao;
import domain.User;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class Main {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Dao<User> userDao = new UserDao();

        while (true) {
            printMenu();
            int choice = getIntInput(sc, "Выберите действие: ");


            switch (choice) {
                case 1 -> createUser(sc, userDao);
                case 2 -> deleteUser(sc, userDao);
                case 3 -> updateUser(sc, userDao);
                case 4 -> showAllUsers(userDao);
                case 5 -> showUserById(sc, userDao);
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }

            if (choice == 0) {
                break;
            }
        }

        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n=== Управление пользователями ===");
        System.out.println("1. Добавить пользователя");
        System.out.println("2. Удалить пользователя");
        System.out.println("3. Изменить пользователя");
        System.out.println("4. Посмотреть всех пользователей");
        System.out.println("5. Посмотреть пользователя по ID");
        System.out.println("0. Выйти");
        System.out.print("Ваш выбор: ");
    }

    private static int getIntInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return sc.nextInt();
            } catch (Exception e) {
                System.out.println("Пожалуйста, введите целое число.");
                sc.next();
            }
        }
    }

    private static long getLongInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return sc.nextLong();
            } catch (Exception e) {
                System.out.println("Пожалуйста, введите целое число (ID).");
                sc.next();
            }
        }
    }

    private static void createUser(Scanner sc, Dao<User> userDao) {
        User user = new User();

        System.out.print("Имя: ");
        user.setName(sc.next());

        System.out.print("Email: ");
        user.setEmail(sc.next());

        user.setAge(getIntInput(sc, "Возраст: "));
        user.setCreatedAt(Instant.now());

        userDao.save(user);
    }

    private static Optional<User> getUserById(Scanner sc, Dao<User> userDao) {
        long id = getLongInput(sc, "Введите ID пользователя: ");
        Optional<User> userOpt = userDao.get(id);

        if (userOpt.isEmpty()) {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }

        return userOpt;
    }

    private static void deleteUser(Scanner sc, Dao<User> userDao) {
        Optional<User> userOpt = getUserById(sc, userDao);
        userOpt.ifPresent(userDao::delete);
    }

    private static void updateUser(Scanner sc, Dao<User> userDao) {
        Optional<User> userOpt = getUserById(sc, userDao);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            System.out.print("Новое имя: ");
            user.setName(sc.next());

            System.out.print("Новый email: ");
            user.setEmail(sc.next());

            user.setAge(getIntInput(sc, "Новый возраст: "));

            userDao.update(user);
        }
    }

    private static void showAllUsers(Dao<User> userDao) {
        List<User> users = userDao.getAll();
        if (users.isEmpty()) {
            System.out.println("В базе нет пользователей.");
        } else {
            System.out.println("\nСписок всех пользователей:");
            users.forEach(user -> System.out.println(formatUser(user)));
        }
    }

    private static void showUserById(Scanner sc, Dao<User> userDao) {
        Optional<User> userOpt = getUserById(sc, userDao);
        if (userOpt.isPresent()) {
            System.out.println("\nИнформация о пользователе:");
            System.out.println(formatUser(userOpt.get()));
        }
    }

    private static String formatUser(User user) {
        return String.format(
                "ID: %d%nName: %s%nEmail: %s%nAge: %d%nCreated at: %s%n",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                DATE_FORMATTER.format(user.getCreatedAt())
        );
    }
}
