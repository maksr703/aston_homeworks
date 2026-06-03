import dao.UserDao;
import dto.request.UserCreateRequest;
import dto.request.UserUpdateRequest;
import dto.response.Page;
import dto.response.UserResponse;
import model.User;
import service.UserService;
import service.UserServiceImpl;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        UserService userService = new UserServiceImpl(new UserDao(HibernateUtil.getSessionFactory()));

        try (Scanner scanner = new Scanner(System.in)) {
            while(true){
                printMenu();

                System.out.println("Select an option:");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice){
                    case 1:
                        try {
                            System.out.print("Enter email: ");
                            String email = scanner.nextLine().trim();
                            System.out.print("Enter password: ");
                            String password = scanner.nextLine().trim();
                            System.out.print("Enter age: ");
                            int age = Integer.parseInt(scanner.nextLine());
                            UserCreateRequest request = new UserCreateRequest(email, password, age);
                            userService.createUser(request);
                            System.out.println("User added successfully!");
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Age must be a number!");
                        } catch (Exception e) {
                            System.out.println("Error adding user: " + e.getMessage());
                        }
                        break;

                    case 2:
                        try {
                            System.out.print("Enter user ID to update: ");
                            Long id = Long.parseLong(scanner.nextLine());
                            System.out.print("Enter new email (leave empty to skip): ");
                            String email = scanner.nextLine().trim();
                            System.out.print("Enter new password (leave empty to skip): ");
                            String password = scanner.nextLine().trim();
                            System.out.print("Enter new age (leave empty to skip): ");
                            Integer age = Integer.parseInt(scanner.nextLine());
                            UserUpdateRequest request = new UserUpdateRequest(id, email, password, age);
                            userService.updateUser(request);
                            System.out.println("User updated successfully!");
                        } catch (NumberFormatException e) {
                            System.out.println("Error: ID and age must be numbers!");
                        } catch (Exception e) {
                            System.out.println("Error updating user: " + e.getMessage());
                        }
                        break;

                    case 3:
                        try {
                            System.out.print("Enter user ID to delete: ");
                            Long id = Long.parseLong(scanner.nextLine());
                            userService.deleteUser(id);
                            System.out.println("User deleted successfully!");
                        } catch (NumberFormatException e) {
                            System.out.println("Error: ID must be a number!");
                        } catch (Exception e) {
                            System.out.println("Error deleting user: " + e.getMessage());
                        }
                        break;

                    case 4:
                        try {
                            System.out.print("Enter user ID: ");
                            Long id = Long.parseLong(scanner.nextLine());
                            Optional<UserResponse> user = userService.getUserById(id);
                            user.ifPresentOrElse(
                                    userResponse -> {
                                        System.out.println("ID: " + userResponse.id());
                                        System.out.println("Email: " + userResponse.email());
                                        System.out.println("Age: " + userResponse.age());
                                        System.out.println("Created: " + userResponse.created());
                                    },
                                    () -> System.out.println("User with ID " + id + " not found.")
                            );
                        } catch (NumberFormatException e) {
                            System.out.println("Error: ID must be a number!");
                        } catch (Exception e) {
                            System.out.println("Error finding user: " + e.getMessage());
                        }
                        break;

                    case 5:
                        try {
                            System.out.print("Enter user Email: ");
                            String email = scanner.nextLine().trim();
                            Optional<UserResponse> user = userService.getUserByEmail(email);
                            user.ifPresentOrElse(
                                    userResponse -> {
                                        System.out.println("ID: " + userResponse.id());
                                        System.out.println("Email: " + userResponse.email());
                                        System.out.println("Age: " + userResponse.age());
                                        System.out.println("Created: " + userResponse.created());
                                    },
                                    () -> System.out.println("User with Email " + email + " not found.")
                            );
                        } catch (Exception e) {
                            System.out.println("Error finding user: " + e.getMessage());
                        }
                        break;

                    case 6:
                        try {
                            System.out.print("Enter page number (default 0): ");
                            String pageInput = scanner.nextLine().trim();
                            int page = pageInput.isEmpty() ? 0 : Integer.parseInt(pageInput);

                            System.out.print("Enter page size (default 10): ");
                            String sizeInput = scanner.nextLine().trim();
                            int size = sizeInput.isEmpty() ? 10 : Integer.parseInt(sizeInput);

                            Page<UserResponse> userPage = userService.getAllUsers(page, size);

                            System.out.println("\n--- Users Page " + (page + 1) + " of " + userPage.totalPages() + " ---");
                            System.out.println("Total users: " + userPage.totalElements());
                            System.out.println("Current page: " + (page + 1));
                            System.out.println("Page size: " + size);
                            System.out.println("-".repeat(50));

                            List<UserResponse> users = userPage.content();
                            if (users.isEmpty()) {
                                System.out.println("No users found on this page.");
                            } else {
                                for (UserResponse user : users) {
                                    System.out.println("ID: " + user.id());
                                    System.out.println("Email: " + user.email());
                                    System.out.println("Age: " + user.age());
                                    System.out.println("Created: " + user.created());
                                    System.out.println("-".repeat(30));
                                }
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Error: Page and size must be numbers!");
                        } catch (Exception e) {
                            System.out.println("Error loading users: " + e.getMessage());
                        }
                        break;

                    case 7:
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice");
                }
            }
        }
    }

    public static void printMenu(){
        System.out.println("1. Add User\n2. Update User\n3. Delete User\n4. Find User by ID\n5. Find User by E-mail\n6. Find All Users\n7. Exit");
    }
}
