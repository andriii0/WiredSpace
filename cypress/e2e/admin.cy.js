describe("Admin deletes newly registered user", () => {
    const timestamp = Date.now();
    const userEmail = `user_${timestamp}@test.com`;
    const userPassword = "testPass123";
    const userName = "Test User";

    it("Registers a user, logs in as admin, and deletes the user", () => {
        // Шаг 1: Регистрация
        cy.visit("http://localhost:5173/register");
        cy.get('input[name="name"]').type(userName);
        cy.get('input[name="email"]').type(userEmail);
        cy.get('input[name="password"]').type(userPassword);
        cy.get('input[name="confirmPassword"]').type(userPassword);
        cy.get("form").submit();
        cy.url({ timeout: 5000 }).should("include", "/login");

        // Шаг 2: Логин как админ
        cy.get("#email").clear().type("admin@wired.space");
        cy.get("#password").clear().type("admin123");
        cy.get("form").submit();

        // Шаг 3: Переход в Users
        cy.contains("Users", { timeout: 10000 }).click();

        // Шаг 4: Поиск по email
        cy.get('input[placeholder*="Search"]', { timeout: 10000 })
            .should("be.visible")
            .clear()
            .type(userEmail);

        // Шаг 5: Убедиться, что точно нашли нужного
        cy.get(".userlist-card")
            .contains("p", userEmail)
            .should("have.text", userEmail)
            .click();

        // Шаг 6: Нажимаем "Delete User"
        cy.contains("🗑️ Delete User").click();

        // Шаг 7: Проверка модального окна и подтверждение удаления
        cy.contains("Yes").click(); // Нажать "Yes" в модальном окне

        // Шаг 8: Поиск по email снова, чтобы убедиться, что пользователь удалён
        cy.get('input[placeholder*="Search"]', { timeout: 10000 })
            .should("be.visible")
            .clear()
            .type(userEmail);

        // Шаг 9: Проверка, что email больше не существует в списке
        cy.contains(userEmail).should('not.exist'); // Проверка, что userEmail исчез
    });
});
