# API Test Cases - Shopping Cart Item Removal Feature

**JIRA Issue:** SCRUM-11694  
**Feature:** Remove items from shopping cart  
**API Version:** 1.0.0  
**Last Updated:** 2024-01-15  
**Test Environment:** Development/Staging  

---

## Table of Contents
1. [Overview](#overview)
2. [Test Environment Setup](#test-environment-setup)
3. [API Endpoints Under Test](#api-endpoints-under-test)
4. [Positive Test Cases](#positive-test-cases)
5. [Negative Test Cases](#negative-test-cases)
6. [Performance Test Cases](#performance-test-cases)
7. [Edge Case Test Cases](#edge-case-test-cases)
8. [Test Data Requirements](#test-data-requirements)
9. [Acceptance Criteria Mapping](#acceptance-criteria-mapping)

---

## Overview

This document contains comprehensive test cases for the shopping cart item removal feature. The feature allows authenticated users to remove items from their shopping cart, with real-time updates to the cart state and total price calculation.

### Business Requirements
- Users can remove items from their cart by clicking 'Remove'
- Cart displays updated total price after removal
- Cart state persists across navigation and refresh
- Empty cart displays appropriate message

### Non-Functional Requirements
- Support up to 10,000 concurrent users
- Item removal must complete in <500ms
- 99.9% uptime
- Secure session tokens for authorization

---

## Test Environment Setup

### Prerequisites
- SpringBoot application running on `http://localhost:8080`
- PostgreSQL/H2 database configured
- Valid JWT authentication tokens
- Postman or Newman for test execution

### Configuration
```properties
Base URL: http://localhost:8080/api
Authentication: Bearer Token (JWT)
Content-Type: application/json
Accept: application/json
```

---

## API Endpoints Under Test

### 1. DELETE /api/cart/items/{itemId}
**Purpose:** Remove an item from the shopping cart

**Request:**
- Method: DELETE
- Path Parameter: `itemId` (UUID)
- Headers: 
  - `Authorization: Bearer {jwt_token}`
  - `Accept: application/json`

**Response (200 OK):**
```json
{
  "id": "uuid",
  "userId": "uuid",
  "items": [
    {
      "id": "uuid",
      "productId": "uuid",
      "quantity": 2,
      "price": 29.99
    }
  ],
  "totalPrice": 59.98,
  "updatedAt": "2024-01-15T10:30:00.000Z",
  "isEmpty": false,
  "emptyMessage": null
}
```

### 2. GET /api/cart
**Purpose:** Retrieve current cart state

**Request:**
- Method: GET
- Headers:
  - `Authorization: Bearer {jwt_token}`
  - `Accept: application/json`

**Response (200 OK):**
```json
{
  "id": "uuid",
  "userId": "uuid",
  "items": [...],
  "totalPrice": 99.99,
  "updatedAt": "2024-01-15T10:30:00.000Z",
  "isEmpty": false,
  "emptyMessage": null
}
```

---

## Positive Test Cases

### TC001: Get Current Cart (Setup)

**Test Case ID:** TC001  
**Priority:** High  
**Test Type:** Functional - Positive  

**Objective:**  
Retrieve the current cart to setup test data for subsequent tests.

**Preconditions:**
- User is authenticated with valid JWT token
- User has an active cart with at least one item

**Test Steps:**
1. Send GET request to `/api/cart`
2. Include valid JWT token in Authorization header
3. Verify response status code
4. Verify response body structure
5. Extract and store `cart_id` and first `item_id` for subsequent tests

**Expected Results:**
- Status Code: 200 OK
- Response contains:
  - `id` (UUID)
  - `userId` (UUID)
  - `items` (array with at least one item)
  - `totalPrice` (number >= 0)
  - `updatedAt` (ISO 8601 timestamp)
- Response time < 500ms

**Acceptance Criteria Mapping:**
- Validates cart state retrieval
- Confirms cart data structure

---

### TC002: Remove Item from Cart Successfully

**Test Case ID:** TC002  
**Priority:** Critical  
**Test Type:** Functional - Positive  

**Objective:**  
Verify that an authenticated user can successfully remove an item from their cart.

**Preconditions:**
- User is authenticated with valid JWT token
- Cart contains at least one item
- Valid `item_id` is available from TC001

**Test Steps:**
1. Send DELETE request to `/api/cart/items/{item_id}`
2. Include valid JWT token in Authorization header
3. Verify response status code is 200
4. Verify response contains updated cart
5. Verify removed item is no longer in items array
6. Verify total price is recalculated
7. Verify `updatedAt` timestamp is updated

**Expected Results:**
- Status Code: 200 OK
- Response contains updated CartResponse DTO:
  - `id` matches cart ID
  - `items` array does not contain removed item
  - `totalPrice` is recalculated correctly
  - `updatedAt` timestamp is current
- Response time < 500ms

**Acceptance Criteria Mapping:**
- ✅ AC1: Item is removed from cart when 'Remove' is clicked
- ✅ AC2: Cart displays updated total price after removal

**Test Data:**
```json
{
  "item_id": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### TC003: Verify Cart State Persists After Removal

**Test Case ID:** TC003  
**Priority:** High  
**Test Type:** Functional - Positive  

**Objective:**  
Verify that cart state persists after item removal (simulating navigation/refresh).

**Preconditions:**
- TC002 has been executed successfully
- Item has been removed from cart

**Test Steps:**
1. Send GET request to `/api/cart`
2. Include valid JWT token in Authorization header
3. Verify response status code is 200
4. Verify removed item is still not present in cart
5. Verify total price remains correctly calculated

**Expected Results:**
- Status Code: 200 OK
- Cart state reflects previous removal:
  - Removed item is not in items array
  - Total price matches expected value
  - Cart data is consistent
- Response time < 500ms

**Acceptance Criteria Mapping:**
- ✅ AC3: Cart state persists after navigation or refresh

---

### TC004: Remove Last Item Shows Empty Cart Message

**Test Case ID:** TC004  
**Priority:** High  
**Test Type:** Functional - Positive  

**Objective:**  
Verify that removing the last item from cart displays "Your cart is empty" message.

**Preconditions:**
- User is authenticated
- Cart contains exactly one item
- Valid `last_item_id` is available

**Test Steps:**
1. Send DELETE request to `/api/cart/items/{last_item_id}`
2. Include valid JWT token in Authorization header
3. Verify response status code is 200
4. Verify `isEmpty` field is true
5. Verify `emptyMessage` is "Your cart is empty"
6. Verify `items` array is empty
7. Verify `totalPrice` is 0

**Expected Results:**
- Status Code: 200 OK
- Response contains:
  - `isEmpty`: true
  - `emptyMessage`: "Your cart is empty"
  - `items`: [] (empty array)
  - `totalPrice`: 0
- Response time < 500ms

**Acceptance Criteria Mapping:**
- ✅ AC4: If cart is empty, show "Your cart is empty" message

---

## Negative Test Cases

### TC005: Remove Non-Existent Item (404)

**Test Case ID:** TC005  
**Priority:** High  
**Test Type:** Functional - Negative  

**Objective:**  
Verify that attempting to remove a non-existent item returns 404 error.

**Preconditions:**
- User is authenticated with valid JWT token
- Item ID does not exist in cart or database

**Test Steps:**
1. Send DELETE request to `/api/cart/items/00000000-0000-0000-0000-000000000000`
2. Include valid JWT token in Authorization header
3. Verify response status code is 404
4. Verify error response structure
5. Verify error message indicates item not found

**Expected Results:**
- Status Code: 404 Not Found
- Response contains ErrorResponse DTO:
  ```json
  {
    "timestamp": "2024-01-15T10:30:00.000Z",
    "traceId": "uuid",
    "errorCode": "CART_ITEM_NOT_FOUND",
    "message": "Item not found in cart",
    "details": [
      {
        "field": "itemId",
        "issue": "Item with ID 00000000-0000-0000-0000-000000000000 does not exist in cart"
      }
    ]
  }
  ```
- Response time < 500ms

**Validation Rules:**
- Item must exist in cart before removal

---

### TC006: Remove Item with Invalid UUID Format (400)

**Test Case ID:** TC006  
**Priority:** Medium  
**Test Type:** Functional - Negative  

**Objective:**  
Verify that invalid UUID format returns 400 Bad Request.

**Preconditions:**
- User is authenticated with valid JWT token

**Test Steps:**
1. Send DELETE request to `/api/cart/items/invalid-uuid-format`
2. Include valid JWT token in Authorization header
3. Verify response status code is 400
4. Verify error response structure
5. Verify error message indicates validation failure

**Expected Results:**
- Status Code: 400 Bad Request
- Response contains ErrorResponse DTO:
  ```json
  {
    "timestamp": "2024-01-15T10:30:00.000Z",
    "traceId": "uuid",
    "errorCode": "VALIDATION_ERROR",
    "message": "Invalid UUID format",
    "details": [
      {
        "field": "itemId",
        "issue": "Must be a valid UUID format"
      }
    ]
  }
  ```
- Response time < 500ms

**Validation Rules:**
- Item ID must be valid UUID format

---

### TC007: Remove Item Without Authentication (401)

**Test Case ID:** TC007  
**Priority:** Critical  
**Test Type:** Security - Negative  

**Objective:**  
Verify that unauthenticated requests are rejected with 401 Unauthorized.

**Preconditions:**
- Valid item ID exists
- No authentication token provided

**Test Steps:**
1. Send DELETE request to `/api/cart/items/{item_id}`
2. Do NOT include Authorization header
3. Verify response status code is 401
4. Verify error response indicates unauthorized

**Expected Results:**
- Status Code: 401 Unauthorized
- Response contains ErrorResponse DTO:
  ```json
  {
    "timestamp": "2024-01-15T10:30:00.000Z",
    "traceId": "uuid",
    "errorCode": "UNAUTHORIZED",
    "message": "Unauthorized - Authentication required",
    "details": []
  }
  ```
- Response time < 500ms

**Security Requirements:**
- All cart operations require authentication
- JWT token must be valid and not expired

---

### TC008: Remove Item from Another User's Cart (403)

**Test Case ID:** TC008  
**Priority:** Critical  
**Test Type:** Security - Negative  

**Objective:**  
Verify that users cannot remove items from other users' carts.

**Preconditions:**
- Two valid user accounts exist
- User A has items in their cart
- User B is authenticated with valid JWT token

**Test Steps:**
1. Authenticate as User B
2. Attempt to remove item from User A's cart
3. Send DELETE request to `/api/cart/items/{user_a_item_id}`
4. Include User B's JWT token in Authorization header
5. Verify response status code is 403
6. Verify error response indicates forbidden

**Expected Results:**
- Status Code: 403 Forbidden
- Response contains ErrorResponse DTO:
  ```json
  {
    "timestamp": "2024-01-15T10:30:00.000Z",
    "traceId": "uuid",
    "errorCode": "FORBIDDEN",
    "message": "Forbidden - You can only modify your own cart",
    "details": []
  }
  ```
- Response time < 500ms

**Security Requirements:**
- Users can only modify their own cart
- Authorization checks must validate cart ownership

---

## Performance Test Cases

### TC009: Remove Item Performance Test

**Test Case ID:** TC009  
**Priority:** High  
**Test Type:** Non-Functional - Performance  

**Objective:**  
Verify that item removal operation completes within performance requirements.

**Preconditions:**
- User is authenticated
- Cart contains at least one item
- System is under normal load

**Test Steps:**
1. Send DELETE request to `/api/cart/items/{item_id}`
2. Include valid JWT token
3. Measure response time
4. Verify operation completes within 500ms (NFR requirement)
5. Verify operation completes within 300ms (optimal)

**Expected Results:**
- Status Code: 200 OK
- Response time < 500ms (mandatory)
- Response time < 300ms (optimal)
- Operation completes successfully

**Performance Requirements:**
- NFR: Operation must complete in <500ms
- Target: Operation should complete in <300ms
- System must support 10,000 concurrent users

**Load Testing Scenarios:**
1. Single user: < 300ms
2. 100 concurrent users: < 500ms
3. 1,000 concurrent users: < 500ms
4. 10,000 concurrent users: < 500ms

---

## Edge Case Test Cases

### TC010: Remove Item Twice (Idempotency Test)

**Test Case ID:** TC010  
**Priority:** Medium  
**Test Type:** Functional - Edge Case  

**Objective:**  
Verify system behavior when attempting to remove the same item twice.

**Preconditions:**
- User is authenticated
- Item has already been removed from cart

**Test Steps:**
1. Remove item from cart (first time)
2. Verify item is removed successfully
3. Attempt to remove the same item again (second time)
4. Verify response status code is 404
5. Verify error message indicates item not found

**Expected Results:**
- First removal: 200 OK
- Second removal: 404 Not Found
- Error message: "Item not found in cart"
- Response time < 500ms

**Idempotency Behavior:**
- DELETE operations should be idempotent
- Second attempt returns 404 (item already removed)
- No side effects on cart state

---

### TC011: Remove Item with Null UUID

**Test Case ID:** TC011  
**Priority:** Low  
**Test Type:** Functional - Edge Case  

**Objective:**  
Verify system behavior when null UUID is provided.

**Preconditions:**
- User is authenticated

**Test Steps:**
1. Send DELETE request to `/api/cart/items/null`
2. Include valid JWT token
3. Verify response status code is 400 or 404
4. Verify error response is returned

**Expected Results:**
- Status Code: 400 Bad Request or 404 Not Found
- Response contains error message
- Response time < 500ms

---

## Test Data Requirements

### User Accounts
```json
{
  "user_a": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "email": "user_a@example.com",
    "jwt_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "user_b": {
    "userId": "550e8400-e29b-41d4-a716-446655440002",
    "email": "user_b@example.com",
    "jwt_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### Cart Data
```json
{
  "cart_id": "650e8400-e29b-41d4-a716-446655440010",
  "user_id": "550e8400-e29b-41d4-a716-446655440001",
  "items": [
    {
      "id": "750e8400-e29b-41d4-a716-446655440020",
      "productId": "850e8400-e29b-41d4-a716-446655440030",
      "quantity": 2,
      "price": 29.99
    },
    {
      "id": "750e8400-e29b-41d4-a716-446655440021",
      "productId": "850e8400-e29b-41d4-a716-446655440031",
      "quantity": 1,
      "price": 49.99
    }
  ],
  "totalPrice": 109.97
}
```

### Invalid Test Data
```json
{
  "invalid_uuid": "invalid-uuid-format",
  "non_existent_uuid": "00000000-0000-0000-0000-000000000000",
  "null_uuid": "null"
}
```

---

## Acceptance Criteria Mapping

| AC # | Acceptance Criteria | Test Cases | Status |
|------|---------------------|------------|--------|
| AC1 | Item is removed from cart when 'Remove' is clicked | TC002, TC004 | ✅ Covered |
| AC2 | Cart displays updated total price after removal | TC002, TC003, TC004 | ✅ Covered |
| AC3 | Cart state persists after navigation or refresh | TC003 | ✅ Covered |
| AC4 | If cart is empty, show "Your cart is empty" message | TC004 | ✅ Covered |

### Functional Requirements Coverage

| Requirement | Test Cases | Status |
|-------------|------------|--------|
| Remove item from cart on user action | TC002, TC004 | ✅ Covered |
| Recalculate and display total price | TC002, TC003, TC004 | ✅ Covered |
| Persist cart state (session or database) | TC003 | ✅ Covered |
| Real-time UI update | TC002, TC004 | ✅ Covered |
| Show empty cart message if needed | TC004 | ✅ Covered |

### Validation Coverage

| Validation | Test Cases | Status |
|------------|------------|--------|
| Item exists in cart before removal | TC005 | ✅ Covered |
| Cart updates correctly after removal | TC002, TC003 | ✅ Covered |
| Total price recalculated correctly | TC002, TC003, TC004 | ✅ Covered |
| Valid UUID format | TC006 | ✅ Covered |

### Non-Functional Requirements Coverage

| NFR | Test Cases | Status |
|-----|------------|--------|
| Support 10,000 concurrent users | TC009 (load testing) | ✅ Covered |
| Operation completes in <500ms | TC001-TC011 (all tests) | ✅ Covered |
| 99.9% uptime | Manual monitoring | ⚠️ Requires monitoring |
| Secure session tokens | TC007, TC008 | ✅ Covered |

---

## Test Execution Summary

### Test Coverage Statistics
- **Total Test Cases:** 11
- **Positive Tests:** 4 (36%)
- **Negative Tests:** 4 (36%)
- **Performance Tests:** 1 (9%)
- **Edge Cases:** 2 (18%)

### Priority Distribution
- **Critical:** 3 test cases
- **High:** 5 test cases
- **Medium:** 2 test cases
- **Low:** 1 test case

### Expected Test Results
- **Pass Rate Target:** ≥ 95%
- **Performance Compliance:** 100% (all tests < 500ms)
- **Security Compliance:** 100% (authentication/authorization enforced)

---

## Notes and Assumptions

1. **Authentication:** All tests assume JWT-based authentication is implemented
2. **Database:** Tests can run against H2 in-memory database or PostgreSQL
3. **Test Data:** Test data should be reset between test runs for consistency
4. **Concurrency:** Performance tests should be executed separately with load testing tools
5. **Environment:** Tests are designed for development/staging environments
6. **Monitoring:** Production monitoring required for 99.9% uptime validation

---

## Test Execution Instructions

### Using Postman
1. Import `test/postman/collection.json`
2. Import `test/postman/environment.json`
3. Set environment variables (base_url, jwt_token)
4. Run collection using Collection Runner
5. Review test results

### Using Newman (CLI)
```bash
newman run test/postman/collection.json \
  -e test/postman/environment.json \
  --reporters cli,json,html \
  --reporter-html-export test/reports/newman-report.html
```

### Using Maven
```bash
mvn clean test -Dtest=CartApiIntegrationTest
```

---

**Document Version:** 1.0  
**Last Reviewed:** 2024-01-15  
**Reviewed By:** QA Team  
**Approved By:** Project Manager  
