describe("Registration flow", () => {
  beforeEach(() => {
    cy.visit("http://localhost:5173/register");
  });

  it("registers a new user", () => {
    const uniqueEmail = `user_${Date.now()}@test.com`;

    cy.get('input[name="name"]').type("Test User");
    cy.get('input[name="email"]').type(uniqueEmail);
    cy.get('input[name="password"]').type("testpass");
    cy.get('input[name="confirmPassword"]').type("testpass");

    cy.get("form").submit();

    cy.get(".register-success").should("contain", "✅ Success! User");
    cy.url().should("include", "/register");

    cy.url({ timeout: 5000 }).should("include", "/login");
    cy.contains("✅ Registration successful");
  });

  it("shows error when password is weak", () => {
    const uniqueEmail = `user_${Date.now()}@test.com`;

    cy.get('input[name="name"]').type("Test User");
    cy.get('input[name="email"]').type(uniqueEmail);
    cy.get('input[name="password"]').type("123");
    cy.get('input[name="confirmPassword"]').type("123");

    cy.get("form").submit();

    cy.get(".register-error").should("contain", "at least 6 characters");
    cy.url().should("include", "/register");
  });

  it("shows error when email already exists", () => {
    cy.get('input[name="name"]').type("Vlad");
    cy.get('input[name="email"]').type("vlad@gmail.com");
    cy.get('input[name="password"]').type("123456");
    cy.get('input[name="confirmPassword"]').type("123456");

    cy.get("form").submit();

    cy.get(".register-error").should("contain", "is already in use");
    cy.url().should("include", "/register");
  });
});
