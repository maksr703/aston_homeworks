package dao;

import model.User;
import org.hibernate.Session;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.HibernateUtilTest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private UserDao userDao;

    @BeforeAll
    static void setUpClass() {
        HibernateUtilTest.initialize(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @AfterAll
    static void tearDownClass() {
        HibernateUtilTest.shutdown();
    }

    @BeforeEach
    void setUp() {
        clearDatabase();
        userDao = new UserDao(HibernateUtilTest.getSessionFactory());
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    // ---------------- Helper methods ----------------
    private void clearDatabase() {
        try (Session session = HibernateUtilTest.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private User createUser(String email, String password, int age) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setAge(age);
        user.setCreated(Instant.now());
        return user;
    }

    private User saveUser(String email, String password, int age) {
        User user = createUser(email, password, age);
        userDao.save(user);
        return user;
    }

    // ---------------- Tests ----------------

    @Test
    void testFindById_ExistingUser() {
        User user = saveUser("test@test.com", "password", 25);

        Optional<User> found = userDao.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("test@test.com", found.get().getEmail());
    }

    @Test
    void testFindById_NonExistingUser() {
        Optional<User> found = userDao.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByEmail_ExistingEmail() {
        User user = saveUser("email@test.com", "pass", 30);

        Optional<User> found = userDao.findByEmail("email@test.com");

        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
    }

    @Test
    void testFindByEmail_NonExistingEmail() {
        Optional<User> found = userDao.findByEmail("notfound@test.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void testSave_NewUser() {
        User newUser = createUser("new@test.com", "newpass", 28);

        userDao.save(newUser);

        Optional<User> saved = userDao.findByEmail("new@test.com");
        assertTrue(saved.isPresent());
        assertNotNull(saved.get().getId());
    }

    @Test
    void testUpdate_ExistingUser() {
        User user = saveUser("old@test.com", "oldpass", 25);

        user.setEmail("updated@test.com");
        user.setAge(30);

        userDao.update(user);

        User updated = userDao.findById(user.getId()).orElseThrow();
        assertEquals("updated@test.com", updated.getEmail());
        assertEquals(30, updated.getAge());
    }

    @Test
    void testDelete_User() {
        User user = saveUser("tobedeleted@test.com", "pass", 40);

        userDao.delete(user);

        Optional<User> found = userDao.findById(user.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void testCountAllUsers() {
        saveUser("u1@test.com", "p", 20);
        saveUser("u2@test.com", "p", 30);
        saveUser("u3@test.com", "p", 25);

        long count = userDao.countAllUsers();

        assertEquals(3, count);
    }

    @Test
    void testPagination_FirstPage() {
        // Сохраняем 3 пользователя
        saveUser("user1@test.com", "p", 25);
        saveUser("user2@test.com", "p", 30);
        saveUser("user3@test.com", "p", 35);

        // Получаем первую страницу (2 пользователя)
        List<User> page1 = userDao.findUsersWithPagination(0, 2);

        // Проверяем размер страницы
        assertEquals(2, page1.size());
    }

    @Test
    void testPagination_SecondPage() {
        // Сохраняем 3 пользователя
        saveUser("user1@test.com", "p", 25);
        saveUser("user2@test.com", "p", 30);
        saveUser("user3@test.com", "p", 35);

        // Получаем вторую страницу (1 пользователь)
        List<User> page2 = userDao.findUsersWithPagination(1, 2);

        // Проверяем размер страницы
        assertEquals(1, page2.size());
    }
}

