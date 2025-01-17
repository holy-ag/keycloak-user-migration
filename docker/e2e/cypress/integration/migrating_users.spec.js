// migrating_users.spec.js created with Cypress
//
// Start writing your Cypress tests below!
// If you're unfamiliar with how Cypress works,
// check out the link below and learn how to write your first test:
// https://on.cypress.io/writing-first-test

const quotedPrintable = require('quoted-printable');

const LEGACY_SYSTEM_URL = "http://legacy-system-example:8080/user-migration-support";

const SMTP_HOST = "mailhog";
const SMTP_PORT = "1025";
const SMTP_FROM = "admin@example.com";

const ADMIN_USERNAME = "admin";
const ADMIN_PASSWORD = "admin";
const ADMIN_EMAIL = 'admin@example.com';

const LEGACY_USER_USERNAME = "lucy";
const LEGACY_USER_PASSWORD = "password";
const LEGACY_USER_EMAIL = 'lucy@example.com';
const LEGACY_USER_FIRST_NAME = 'Lucy';
const LEGACY_USER_LAST_NAME = 'Brennan';

describe('user migration plugin', () => {

    before(() => {
        signInAsAdmin();
        configureLoginSettings();
        configureMigrationPlugin();
        configureEmails();
        signOutViaUI();
    })

    function signInAsAdmin() {
        cy.visit('/admin');
        submitCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    function submitCredentials(user, password) {
        cy.get('#username').type(user);
        cy.get('#password').type(password);
        cy.get('#kc-login').click();
    }

    function signOutViaUI() {
        cy.get('a').contains('Sign Out').click({force: true});
    }

    function configureLoginSettings() {
        cy.visit('/admin/master/console/#/realms/master/login-settings');

        cy.contains("Forgot password").parent().find('[type="checkbox"]')
            .uncheck({force: true})
            .check({force: true});

        cy.get('button').contains('Save').click();
    }

    function configureMigrationPlugin() {
        visitUserFederationPage();
        cy.wait(1000);
        let providerDropdownSelector = '.blank-slate-pf-main-action select[ng-model="selectedProvider"]';
        cy.get(providerDropdownSelector)
            .then($providerDropdown => {
                goToPluginSettings($providerDropdown, providerDropdownSelector);
                cy.get('.form-group.ng-scope').contains('Rest client URI (required)').parent().within(() => {
                    cy.get('input').clear().type(LEGACY_SYSTEM_URL);
                });
                cy.get('button').filter(':visible').contains('Save').click();
            });
    }

    function visitUserFederationPage() {
        cy.intercept('GET',
            '/admin/realms/master/components?parent=*&type=org.keycloak.storage.UserStorageProvider')
            .as('storageProviders');
        cy.visit('/admin/master/console/#/realms/master/user-federation/');
        cy.wait('@storageProviders');
    }

    function goToPluginSettings($providerDropdown, providerDropdownSelector) {
        if ($providerDropdown.is(':visible')) {
            cy.get(providerDropdownSelector)
                .select('User migration using a REST client');
        } else {
            cy.contains('Edit').click();
        }
    }

    function configureEmails() {
        configureAdminPersonalInfo();
        configureSmtpSettings();
    }

    function configureAdminPersonalInfo() {
        cy.intercept('GET', '/realms/master/account/')
            .as("accountDetails");
        cy.visit('/realms/master/account/#/personal-info');
        cy.wait('@accountDetails');

        // Wait a while, otherwise Keycloak overrides the inputs randomly
        cy.wait(5000);

        cy.get('#email-address').clear().type(ADMIN_EMAIL);
        cy.get('#first-name').clear().type(ADMIN_USERNAME);
        cy.get('#last-name').clear().type(ADMIN_USERNAME);

        cy.get('button').contains('Save').click();

        cy.get('.pf-c-alert').should('contain', "Your account has been updated");
    }

    function configureSmtpSettings() {
        cy.visit('/admin/master/console/#/realms/master/smtp-settings');

        cy.get('#smtpHost').clear().type(SMTP_HOST);
        cy.get('#smtpPort').clear().type(SMTP_PORT);
        cy.get('#smtpFrom').clear().type(SMTP_FROM);

        cy.get('a').contains('Test connection').click();
        cy.get('.alert').should('contain', "SMTP connection successful. E-mail was sent!");

        cy.get('button').contains('Save').click();
    }

    beforeEach(() => {
        deleteEmails();
        deleteTestUserIfExists();
    });

    function deleteEmails() {
        cy.mhDeleteAll();
        cy.mhGetAllMails()
            .should('have.length', 0);
    }

    function deleteTestUserIfExists() {
        signInAsAdmin();
        cy.visit('/admin/master/console/#/realms/master/users');
        cy.intercept('GET', '/admin/realms/master/users*').as("userGet");
        cy.get('#viewAllUsers').click();
        cy.wait('@userGet');

        cy.get('body').then($body => {
            if ($body.find('td:contains("' + LEGACY_USER_EMAIL + '")').length > 0) {
                cy.contains(LEGACY_USER_EMAIL).parent().contains('Delete').click();
                cy.get('.modal-dialog button').contains('Delete').click();
                cy.get('.alert').should('contain', "Success");
            }
            signOutViaUI();
        });
    }

    it('should migrate users', () => {
        signInAsLegacyUser();
        updateAccountInformation();
        assertIsLoggedInAsLegacyUser();
    });

    function signInAsLegacyUser() {
        cy.visit('/realms/master/account');
        cy.get('#landingSignInButton').click();
        submitCredentials(LEGACY_USER_USERNAME, LEGACY_USER_PASSWORD);
    }

    function updateAccountInformation() {
        cy.get('#email').should('have.value', LEGACY_USER_EMAIL);
        cy.get('#firstName').should('have.value', LEGACY_USER_FIRST_NAME);
        cy.get('#lastName').should('have.value', LEGACY_USER_LAST_NAME);
        cy.get("input").contains("Submit").click();
    }

    function assertIsLoggedInAsLegacyUser() {
        cy.get('#landingLoggedInUser').should('contain', LEGACY_USER_FIRST_NAME + ' ' + LEGACY_USER_LAST_NAME);
    }

    it('should reset password after inputting wrong credentials', () => {
        attemptLoginWithWrongPassword();
        triggerPasswordReset();
        resetPasswordViaEmail();
    });

    function resetPasswordViaEmail() {
        cy.mhGetMailsBySubject('Reset password').mhFirst().mhGetBody()
            .then(bodyQuotedPrintable => {
                clickPasswordResetLink(bodyQuotedPrintable);
                updateAccountInformation();
                inputNewPassword();
                assertIsLoggedInAsLegacyUser();
            });
    }

    function attemptLoginWithWrongPassword() {
        cy.visit('/realms/master/account');
        cy.get('#landingSignInButton').click();
        submitCredentials(LEGACY_USER_USERNAME, "wrongPassword");
    }

    function triggerPasswordReset() {
        cy.intercept('GET', '/realms/master/login-actions/reset-credentials*')
            .as('resetCredentials');
        cy.get("a").contains("Forgot Password?").click();
        cy.wait('@resetCredentials');
        cy.get('#username').clear().type(LEGACY_USER_EMAIL);
        cy.get('input[type=submit]').click();
        cy.get('body').should('contain.text',
            'You should receive an email shortly with further instructions.');
        cy.mhGetMailsBySubject('Reset password')
            .should('have.length', 1);
    }

    function clickPasswordResetLink(bodyQuotedPrintable) {
        const body = quotedPrintable.decode(bodyQuotedPrintable);
        const resetPassUrl = getUrlFromLink(body, 'Link to reset credentials');

        cy.visit(resetPassUrl);
    }

    function getUrlFromLink(body, linkText) {
        const linkPattern = new RegExp('<a href="([^"]*).+' + linkText + '.*?<\\/a>');
        return linkPattern.exec(body)[1]
            .toString()
            .replace(/(\r\n|\n|\r)/gm, "");
    }

    function inputNewPassword() {
        cy.get('#password-new').type(LEGACY_USER_PASSWORD);
        cy.get('#password-confirm').type(LEGACY_USER_PASSWORD);
        cy.get('input[type=submit]').click();
    }

    it('should reset password before user is migrated', () => {
        cy.visit('/realms/master/account');
        cy.get('#landingSignInButton').click();
        triggerPasswordReset();
        resetPasswordViaEmail()
    });
});