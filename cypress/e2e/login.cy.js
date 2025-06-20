describe("Login flow", () => {
  beforeEach(() => {
    cy.visit("http://localhost:5173/login");
  });

  it("logs in successfully with valid credentials", () => {
    cy.get("#email").type("vlad@gmail.com");
    cy.get("#password").type("1234");
    cy.get("form").submit();

    cy.url().should("include", "/profile/me");
    cy.contains("profile");
  });

  it("shows error on invalid credentials", () => {
    cy.get("#email").type("invalid@example.com");
    cy.get("#password").type("wrongpass");
    cy.get("form").submit();

    cy.get(".login-error").should("contain", "Invalid email or password");
  });
});
