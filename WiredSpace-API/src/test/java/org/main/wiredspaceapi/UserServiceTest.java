package org.main.wiredspaceapi;

import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    @Test
    void testGreetUser() {
        // Создаем экземпляры необходимых объектов
        UserService userService = new UserService();
        User user = new User("Andrii", "123", UserRole.PREMIUM_USER);

        // Перенаправляем System.out для перехвата вывода
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Вызываем тестируемый метод
        userService.GreetUser(user);

        // Восстанавливаем исходный System.out
        System.setOut(originalOut);

        // Получаем вывод в виде строки и проверяем наличие ожидаемых подстрок
        String output = outContent.toString();
        assertTrue(output.contains("Greeting"), "Output should contain 'Greeting'");
        assertTrue(output.contains("Andrii"), "Output should contain the user name 'Andrii'");
    }
}
