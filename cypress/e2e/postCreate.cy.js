describe("Profile page and post creation", () => {
  const timestamp = Date.now();
  const email = `user_${timestamp}@test.com`;
  const password = "initialPass123";
  const name = "Test User";
  const postText = `My first post ${timestamp}`;

  it("Registers, logs in and creates a post", () => {
    // Register
    cy.visit("http://localhost:5173/register");
    cy.get('input[name="name"]').type(name);
    cy.get('input[name="email"]').type(email);
    cy.get('input[name="password"]').type(password);
    cy.get('input[name="confirmPassword"]').type(password);
    cy.get("form").submit();

    // After successful registration, user is redirected to login
    cy.url({ timeout: 5000 }).should("include", "/login");

    // Login
    cy.get("#email").type(email);
    cy.get("#password").type(password);
    cy.get("form").submit();

    // Should land on profile page
    cy.url({ timeout: 5000 }).should("include", "/profile");

    // Go to /profile/me to ensure post form is available
    cy.visit("http://localhost:5173/profile/me");

    // Wait for input field to show
    cy.get("input.post-input").should("be.visible");

    // Create a post
    cy.get("input.post-input").type(postText);
    cy.get("button.create-post-btn").click();

    // Check if the post appears
    cy.contains(postText, { timeout: 5000 }).should("exist");
  });
});