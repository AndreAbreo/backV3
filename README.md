# ServidorApp

This project uses environment variables to configure LDAP authentication and JWT token generation.

## LDAP Configuration

Create a `.env` file in the project root with the following variables:

```
LDAP_URL=ldap://your-ldap-host:389
LDAP_DOMAIN=YOUR_DOMAIN
# Optional separated host and port
LDAP_HOST=your-ldap-host
LDAP_PORT=389
```

- **LDAP_URL** – Full LDAP connection URL.
- **LDAP_DOMAIN** – Domain name used to build the `SECURITY_PRINCIPAL` when the username does not include `@`.
- **LDAP_HOST** and **LDAP_PORT** – Alternative way to specify the server. These variables are currently not used directly in the code but are provided for convenience.

Ensure the `.env` file is not committed to version control. The `.gitignore` already excludes it.

## JWT Secret

The variable `JWT_SECRET` must also be defined in `.env`. It is used by `JwtService` and `JwtTokenFilter` to sign and validate tokens.

```
JWT_SECRET=your-secret-key
```

## Login Flow

The login endpoint (`/usuarios/login`) first verifies LDAP credentials before issuing a JWT:

1. The user submits their username and password.
2. If the username contains `@` or `\`, LDAP authentication is attempted using `LDAP_URL` and `LDAP_DOMAIN`.
3. After successful LDAP authentication, the user is looked up in the database.
4. If the account is active, a JWT is generated with `JwtService.generateToken` and returned in the response.

Relevant code snippet from `UsuarioResource`:

```java
autenticarLDAP(loginRequest.getUsuario(), loginRequest.getPassword());
UsuarioDto userDto = usuarioMapper.toDto(usuarioEntity, new CycleAvoidingMappingContext());
String token = jwtService.generateToken(
        userDto.getEmail(), userDto.getId(), userDto.getIdPerfil().getNombrePerfil());
```

This ensures only users authenticated via LDAP receive a valid JWT.

