describe("User Settings Flow", () => {
  const timestamp = Date.now();
  const originalEmail = `user_${timestamp}@test.com`;
  const originalPassword = "originalPass123";
  const updatedName = "Updated User";
  const updatedEmail = `updated_${timestamp}@test.com`;
  const updatedPassword = "updatedPass456";

  it("registers, updates settings, and deletes account", () => {
    // Register
    cy.visit("http://localhost:5173/register");
    cy.get('input[name="name"]').type("Initial User");
    cy.get('input[name="email"]').type(originalEmail);
    cy.get('input[name="password"]').type(originalPassword);
    cy.get('input[name="confirmPassword"]').type(originalPassword);
    cy.get("form").submit();

    // Wait for redirect to login
    cy.url({ timeout: 5000 }).should("include", "/login");

    // Log in
    cy.get("#email").type(originalEmail);
    cy.get("#password").type(originalPassword);
    cy.get("form").submit();
    cy.url().should("include", "/profile");

    // Go to Settings via sidebar button (assumes it exists and has label "Settings")
    cy.contains("Settings").click();
    cy.url().should("include", "/settings");

    // Update name, email, password
    cy.get('input[placeholder="Name"]').clear().type(updatedName);
    cy.get('input[placeholder="Email"]').clear().type(updatedEmail);
    cy.get('input[placeholder="New Password"]').type(updatedPassword);
    cy.get('input[placeholder="Confirm Password"]').type(updatedPassword);
    cy.get("form").submit();

    // Should be logged out
    cy.url().should("include", "/login");

    // Log in again with updated credentials
    cy.get("#email").type(updatedEmail);
    cy.get("#password").type(updatedPassword);
    cy.get("form").submit();
    cy.url().should("include", "/profile");

    // Go to Settings again
    cy.contains("Settings").click();
    cy.url().should("include", "/settings");

    // Validate updated data
    cy.get('input[placeholder="Name"]').should("have.value", updatedName);
    cy.get('input[placeholder="Email"]').should("have.value", updatedEmail);

    // Start deletion process
    cy.contains("Delete My Account").click();
    cy.contains("Yes").click(); // first confirmation
    cy.contains("Delete Permanently").click(); // final confirmation

    // Should be redirected to login
    cy.url().should("include", "/login");
    cy.contains("Login");

    // Attempt login should now fail
    cy.get("#email").type(updatedEmail);
    cy.get("#password").type(updatedPassword);
    cy.get("form").submit();
    cy.get(".login-error").should("contain", "Invalid email or password");
  });
});
