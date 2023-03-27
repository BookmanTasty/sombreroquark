# 🎩 SombreroQuark 🎩
Welcome to the documentation of SombreroQuark! 🎉

SombreroQuark is a lightweight API built on Quarkus, designed to help you manage users, roles, permissions, and authorization with ease. It offers robust support for JWT tokens, Google OAuth2 authentication, email verification, and magic link login.

## 🔍 Features
Some of the features of SombreroQuark include:

🔒 Robust User Management: Securely register, manage and authenticate users with SombreroQuark's user management system.

🎫 Role-based Access Control: Create roles and assign permissions to users to control access to various resources.

🔑 JWT Token Authentication: Authenticate users and control access to resources through JSON Web Tokens (JWT).

🌐 OAuth 2.0 Support: Authenticate users with Google OAuth 2.0 and integrate SombreroQuark with other OAuth 2.0 providers.

📧 Email Verification: Verify users' email addresses to ensure that only valid users can access your resources.

🔗 Magic Link Authentication: Allow users to log in to their accounts with a single click through secure magic links sent via email.

💻 Lightweight API: Built on the Quarkus framework, SombreroQuark is lightweight, fast and efficient.

If you're looking for a powerful yet lightweight solution for managing users and authentication in your application, SombreroQuark is the perfect choice!

## 📚 Documentation

👋 Hello everyone! Just a quick update on the SombreroQuark documentation. We are currently 30% done with documenting all the features of this lightweight Quarkus API. 📝 Stay tuned for more updates as we continue to work hard on making the documentation as comprehensive as possible!

# Getting Started🚀

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

1. Create a `.env` file in the root directory of your project by copying the `.env.example` file.

```bash
cp .env.example .env
```
2. Fill in the variables in your `.env` file with the appropriate values.
3. Run the application in dev mode.
```shell script
./mvnw compile quarkus:dev
```
## 🐳🏃‍♂️ Running SombreroQuark in Docker 🏃‍♀️🐳

1. Clone the SombreroQuark repository from GitHub:

```bash
git clone https://github.com/BookmanTasty/sombreroquark.git
```
2. Navigate to the SombreroQuark directory:

```bash
cd sombreroquark
```
3. Copy the .env.example file to .env:

```bash
cp .env.example .env
```
4. Configure the .env file with your credentials.


5. Run the following command to start the Docker Compose file:

```bash
docker-compose up
```
Once the containers are up and running, you can access SombreroQuark at http://localhost:8080.

🎉🚀 Congratulations! You have successfully run SombreroQuark in Docker.

## .env.example documentation
### App configuration
- **PUBLIC_URL**: the public URL where your application will be hosted.
- **CORS_ORIGINS**: a comma-separated list of origins that are allowed to make cross-origin requests to your application.

### DB configuration
- **DB_KIND**: the type of database you're using, in this case PostgreSQL.
> **Note**: SombreroQuark currently only supports PostgreSQL.
- **DB_URL**: the JDBC URL to connect to your database.
- **DB_USER**: the username to authenticate with your database.
- **DB_PASSWORD**: the password to authenticate with your database.
- **DB_NAME**: the name of the database you're using.

### OIDC
- **GOOGLE_OAUTH_CLIENT_ID**: the OAuth 2.0 client ID for Google authentication.
- **GOOGLE_OAUTH_CLIENT_SECRET**: the OAuth 2.0 client secret for Google authentication.
- **GOOGLE_OAUTH_URL**: the URL where your Google OAuth 2.0 provider is located.
- **GOOGLE_OAUTH_SCOPE**: a comma-separated list of OAuth 2.0 scopes to request from Google.
- **GOOGLE_OAUTH_REDIRECT_URI**: the redirect URI to use after successful Google OAuth 2.0 authentication.

### JWT JWKS signing
- **JWT_JWKS_KEY**: the key to use for JWT JWKS signing.
- **JWT_ISSUER**: the issuer name for your JWT tokens.
>**Note**: To create a JWKS key with a key size of 2048, key use for signature, and algorithm RS256 for SombreroQuark, you can follow these steps:
> - Go to https://mkjwk.org/
> - Select "RS" as the key type
> - Set the "Key Size" to 2048
> - Set the "Key Use" to "signature"
> - Set the "Algorithm" to "RS256"
> - Click "Generate Key" button
> - Copy the "Private Key" and "Public Key" (in JSON format) and encode them in base 64.

### Mailer configuration
- **STMP_MAILER_FROM**: the email address to use as the sender for emails.
- **STMP_MAILER_HOST**: the hostname of your SMTP mail server.
- **STMP_MAILER_PORT**: the port of your SMTP mail server.
- **STMP_MAILER_USERNAME**: the username to authenticate with your SMTP mail server.
- **STMP_MAILER_PASSWORD**: the password to authenticate with your SMTP mail server.
- **STMP_MAILER_AUTH_METHODS**: a comma-separated list of SMTP authentication methods to use.
- **STMP_MAILER_SSL**: whether or not to use SSL for SMTP communication.

### Email templates
- **REMOTE_TEMPLATE_WELCOME_URL**: the URL where the email template for welcome emails is located.
- **REMOTE_TEMPLATE_VERIFY_EMAIL_URL**: the URL where the email template for email verification emails is located.
- **REMOTE_TEMPLATE_MAGIC_LINK_URL**: the URL where the email template for magic link emails is located.
- **REMOTE_TEMPLATE_RESET_PASSWORD_URL**: the URL where the email template for password reset emails is located.

### Default admin user
- **ADMIN_USER**: the username for the default admin user.
- **ADMIN_PASSWORD**: the password for the default admin user.
- **ADMIN_EMAIL**: the email address for the default admin user.

# SombreroQuark API Documentation

Please refer to the official SombreroQuark API documentation for more information:

- [SombreroQuark API Documentation](https://app.swaggerhub.com/apis-docs/CLEYVALUNA/sombreroquark-api/1.0.0-SNAPSHOT#/)

The documentation includes information on available endpoints, request and response schemas, authentication mechanisms, and more. If you have any questions or issues with the API, please refer to the documentation first or contact the API team for assistance.

## SombreroQuark To-Do List 📋
Here are some of the planned and desired features for SombreroQuark! 🚀

- ✔️ Basic user management and authorization functionality
- ✔️ JWT token-based authentication and authorization
- ✔️Google OAuth2 authentication
- ✔️ Email verification and magic link login
- ✔️ User roles and permissions
- ✔️ Support for PostgreSQL database
- 🏗️ Support for OAuth2 authentication from other providers (Facebook, GitHub, etc.)
- 🏗️ Allow loading email templates locally
- 🏗️ Document email template customization
- 🏗️ Accept requests for new features

Stay tuned for future updates and improvements! 💪
