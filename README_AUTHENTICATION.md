# Authentication System Implementation

## Overview

This document describes the authentication system implemented for the Car Showroom application as part of Jira story **SCMM-3**.

## Features Implemented

### Backend (Spring Boot)

1. **BCrypt Password Hashing**
   - All passwords are hashed using BCrypt before storage
   - Spring Security dependency added for BCryptPasswordEncoder
   - Security filter chain disabled (demo project)

2. **Authentication Endpoint**
   - `POST /api/auth/login` - Authenticates users with userId, password, and role
   - Returns 200 with user details on success
   - Returns 401 on invalid credentials or role mismatch

3. **User Management Endpoints**
   - `POST /api/users` - Create new user with hashed password
   - `PUT /api/users/:id/password` - Update password with BCrypt hashing
   - Role defaults to CUSTOMER if not provided

4. **Role-Based Authentication**
   - Validates that user's role matches the requested role during login
   - Supports three roles: CUSTOMER, STAFF, MANAGER

### Frontend (React)

1. **Signup Page** (`/signup`)
   - User registration with role selection
   - Routes to correct home based on actual role (fixes Swing app bug)
   - Supports role-specific fields (department for MANAGER, designation for STAFF)

2. **Login Page** (`/login`)
   - Authentication with userId, password, and role
   - Shows inline error on failure (fixes Swing app bug - no redirect to signup)
   - Routes to role-appropriate home on success

3. **Protected Routes**
   - `ProtectedRoute` component enforces role-based access
   - Redirects unauthenticated users to `/login`
   - Redirects wrong-role users to their correct home

4. **Authentication Context**
   - Manages user state (userId, name, role)
   - Provides `setUser()` and `clearUser()` methods
   - Used throughout the application for auth state

5. **Logout Functionality**
   - Clears authentication context
   - Redirects to landing page

## Key Files

### Backend

```
src/main/java/com/slingshot/carshowroom/
├── config/
│   └── SecurityConfig.java              # BCrypt encoder configuration
├── controller/
│   ├── AuthController.java              # Login endpoint
│   └── UserController.java              # User management (updated)
├── service/
│   ├── AuthService.java                 # Authentication logic
│   └── UserService.java                 # User CRUD with BCrypt (updated)
├── dto/
│   ├── LoginRequest.java                # Login request DTO
│   └── LoginResponse.java               # Login response DTO
└── model/
    ├── User.java                        # User entity (existing)
    └── Role.java                        # Role enum (existing)
```

### Frontend

```
src/
├── services/
│   └── authService.ts                   # API service for auth
├── pages/
│   ├── SignUp.tsx                       # Signup page (updated)
│   └── Login.tsx                        # Login page (updated)
├── components/
│   ├── ProtectedRoute.tsx               # Route protection (updated)
│   └── Navbar.tsx                       # Logout button (existing)
└── context/
    └── AppContext.tsx                   # Auth state management (existing)
```

## API Endpoints

### POST /api/users

Create a new user with BCrypt-hashed password.

**Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "contactInfo": "123-456-7890",
  "role": "CUSTOMER",
  "department": null,
  "designation": null
}
```

**Response (201):**
```json
{
  "userId": 1,
  "name": "John Doe",
  "role": "CUSTOMER"
}
```

### POST /api/auth/login

Authenticate a user with role validation.

**Request:**
```json
{
  "userId": 1,
  "password": "password123",
  "role": "CUSTOMER"
}
```

**Response (200):**
```json
{
  "userId": 1,
  "name": "John Doe",
  "role": "CUSTOMER"
}
```

**Response (401):**
- Invalid password
- Role mismatch
- User not found

### PUT /api/users/:id/password

Update user password with BCrypt hashing.

**Request:**
```json
{
  "password": "newPassword123"
}
```

**Response (204):** No content

## Security Considerations

### Implemented

✅ BCrypt password hashing (industry standard)  
✅ Role-based authentication  
✅ No plaintext passwords in database  
✅ Password validation during login  
✅ Role validation during login  

### Not Implemented (Demo Project)

❌ JWT/session tokens (context state only)  
❌ Password reset flow  
❌ Email verification  
❌ Persistent sessions (lost on page refresh)  
❌ Rate limiting  
❌ Account lockout  
❌ HTTPS enforcement  

## Testing

### Backend Tests

```bash
cd car-showroom
mvn test
```

**Test Classes:**
- `AuthServiceTest` - Authentication logic tests
- `UserServiceTest` - User creation and password hashing tests
- `AuthControllerTest` - Login endpoint integration tests

### Frontend Tests

Tests can be added for:
- `authService.ts` - API service unit tests
- `Login.tsx` - Login flow integration tests
- `SignUp.tsx` - Signup flow integration tests
- `ProtectedRoute.tsx` - Route protection tests

## Running the Application

### Backend

```bash
cd car-showroom
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`

### Frontend

```bash
cd car-showroom-client
npm install
npm run dev
```

Frontend runs on `http://localhost:3000`

## Migration from Swing App

### Bugs Fixed

1. **Login Error Handling**
   - **Old:** Failed login redirected to signup page
   - **New:** Shows inline error message

2. **Signup Routing**
   - **Old:** Always routed to Staff Home regardless of role
   - **New:** Routes to correct home based on actual role

3. **Password Security**
   - **Old:** Plaintext password comparison
   - **New:** BCrypt hashed passwords

## Usage Examples

### Signup Flow

1. User visits `/signup`
2. Fills out form with name, email, password, role
3. Submits form → `POST /api/users`
4. Backend hashes password and creates user
5. Frontend receives response with userId, name, role
6. Context is updated with user data
7. User is redirected to `/{role}/home`

### Login Flow

1. User visits `/login`
2. Enters userId, password, and selects role
3. Submits form → `POST /api/auth/login`
4. Backend validates credentials and role
5. On success: Frontend updates context and redirects to `/{role}/home`
6. On failure: Shows inline error message

### Protected Route Access

1. User tries to access `/customer/home`
2. `ProtectedRoute` checks if user is authenticated
3. If not authenticated → redirect to `/login`
4. If wrong role (e.g., STAFF) → redirect to `/staff/home`
5. If correct role → render page

## Troubleshooting

### "401 Unauthorized" on Login

- Verify userId exists in database
- Check password is correct
- Ensure role matches user's assigned role

### "Email already registered" on Signup

- Email must be unique
- Check if user already exists with that email

### Redirected to Login After Refresh

- This is expected behavior (no persistent sessions)
- User must log in again after page refresh

### Password Not Updating

- Verify userId is correct
- Check that new password meets requirements
- Ensure endpoint is `PUT /api/users/:id/password`

## Future Enhancements

- [ ] JWT token-based authentication
- [ ] Refresh token mechanism
- [ ] Password strength validation
- [ ] Password reset via email
- [ ] Remember me functionality
- [ ] Session timeout handling
- [ ] Rate limiting on login attempts
- [ ] Account lockout after failed attempts
- [ ] Two-factor authentication
- [ ] OAuth2 integration

## References

- **Jira Story:** SCMM-3
- **Specification:** `spec.md`
- **Spring Security Docs:** https://spring.io/projects/spring-security
- **BCrypt:** https://en.wikipedia.org/wiki/Bcrypt
