describe("User Settings Flow", () => {
  const timestamp = Date.now();
  const originalEmail = `user_${timestamp}@test.com`;
  const originalPassword = "originalPass123";
  const updatedName = "Updated User";
  const updatedEmail = `updated_${timestamp}@test.com`;
  const updatedPassword = "updatedPass456";

  it("registers, updates settings, and deletes account", () => {
    cy.log("Step 1: Register new user");
    cy.visit("http://localhost:5173/register");
    cy.get('input[name="name"]').type("Initial User");
    cy.get('input[name="email"]').type(originalEmail);
    cy.get('input[name="password"]').type(originalPassword);
    cy.get('input[name="confirmPassword"]').type(originalPassword);
    cy.get("form").submit();

    cy.log("Step 2: Wait for redirect to login");
    cy.url({ timeout: 5000 }).should("include", "/login");

    cy.log("Step 3: Log in with new user");
    cy.get("#email").type(originalEmail);
    cy.get("#password").type(originalPassword);
    cy.get("form").submit();
    cy.url().should("include", "/profile");

    cy.log("Step 4: Open Settings page");
    cy.contains("Settings").click();
    cy.url().should("include", "/settings");

    cy.log("Step 5: Update user info (name, email, password)");
    cy.get('input[placeholder="Name"]').clear().type(updatedName);
    cy.get('input[placeholder="Email"]').clear().type(updatedEmail);
    cy.get('input[placeholder="New Password"]').type(updatedPassword);
    cy.get('input[placeholder="Confirm Password"]').type(updatedPassword);
    cy.get("form").submit();

    cy.log("Step 6: Should be logged out after update");
    cy.url().should("include", "/login");

    cy.log("Step 7: Log in again with updated credentials");
    cy.get("#email").type(updatedEmail);
    cy.get("#password").type(updatedPassword);
    cy.get("form").submit();
    cy.url().should("include", "/profile");

    cy.log("⚙Step 8: Open Settings again to validate updated data");
    cy.contains("Settings").click();
    cy.url().should("include", "/settings");

    cy.log("Step 9: Validate updated name and email");
    cy.get('input[placeholder="Name"]').should("have.value", updatedName);
    cy.get('input[placeholder="Email"]').should("have.value", updatedEmail);

    cy.log("Step 10: Start account deletion flow");
    cy.contains("Delete My Account").click();
    cy.contains("Yes").click();
    cy.contains("Delete Permanently").click();

    cy.log("Step 11: Should be redirected to login after deletion");
    cy.url().should("include", "/login");
    cy.contains("Login");

    cy.log("Step 12: Try logging in with deleted account (should fail)");
    cy.get("#email").type(updatedEmail);
    cy.get("#password").type(updatedPassword);
    cy.get("form").submit();
    cy.get(".login-error").should("contain", "Invalid email or password");
  });
});
