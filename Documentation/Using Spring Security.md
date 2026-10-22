# Using Spring Security

## Authorization

Spring Security handles all authentication and authorization actions that happen in each endpoint. This file intends to explain how to make best use of Spring Security annotations

### Setting Endpoint Authentication Requirements Explicitly In Security Configuration:

Spring Security handles Authentication and restricting access to endpoints automatically. This means if your endpoint needs to be accessed only by those with authentication, our Spring Security setup does that automatically.

A problem arises if you introduce an endpoint which should not be secured by default. This requires modification of the SecurityConfiguration class. The process of explicitly allowing an unauthenticated user to access an endpoint is as follows:
> Note: These instructions are adding to a Builder class. This means you do not put a semicolon ";" at the end of the line. Simply place another period "." and call the next method in the chain. Placing a semicolon will throw a compiler error.
1. Open the SecurityConfiguration.java class and find the first code line the Spring Security in line documentation
2. Create a new line at the end of the indented segment, before the ".anyRequest().authenticated();" segment, which is two indentation blocks back
3. Add a ".requestMatchers()" method call. This is where you place your endpoints that do not need authentication to access. This can include as many endpoints as required
4. Place the String representation of your endpoints into the parameters field, starting at the root and ending at the endpoint
   >The root of the application is where all of our endpoints start. When you access the site locally, you use "localhost:8080/...". The root of our application starts at the "/". So, if we are securing the endpoint "localhost:8080/test.html", we enter "/test.html" as our variable
5. Directly after this line is where we define if we want to require authentication or not. We can use the following two methods to alert Spring Security of this:
   1. The ".authenticated()" method places an authentication requirements on the endpoints listed as parameters from the step above. This method takes no parameters.
   2. The ".permitAll()" method removes authentication requirements for the endpoints listed as parameters from the step above. This method takes no parameters.
   3. There is two others we can use for specifying Authorizations, which will be discussed in the Setting Endpoint Authorization Requirements Explicitly In Security Configuration section
6. That's it. The endpoint will now follow the authentication rules you set.

## Authorization

While Spring Security handles authentication for us, it does not handle Authorization for us, as that is application logic we implement. Spring thankfully gives us two options when declaring Authorization requirements for endpoints, both of which will be described here.

### Setting Endpoint Authorization Requirements With Annotations

This is the simplest and recommended way of setting Authorization requirements, as it gives us method by method control of Authorization.

1. To use these annotations, it requires the @EnableMethodSecurity annotation to be applied above the class statement (Where we place the @RestController annotation)
2. After this, we can use the following annotations for each endpoint we want to restrict based off our permissions
> Permission keys are noted in a separate document, refer to that for explicit names, values, and intended usage
   1. The @PreAuthorize() annotation declares that a user is authorized before the request is processed. This is the most often used annotation for checking for permissions. To populate the annotation, you have two options for values to use as a String:
      1. "hasAuthority()" allows you to check for a specific permission as denoted in PermissionNames. You populate the parameters as a String literal (A string with single quotes ''). If the one listed is present, the user can access the endpoint
      2. "hasAuthorities()" allows you to check for any set of permissions as denoted in PermissionNames. You populate the parameters as String literals (Strings with single quotes ''). If one of them are present, the user can access the endpoint
   2. The @PostAuthorize() annotation declares that a user is authorized after accessing the endpoint. This annotation may have uses elsewhere. To populate the annotation, you have two options for values to use as a String:
      1. "hasAuthority()" allows you to check for a specific permission as denoted in PermissionNames. You populate the parameters as a String. If the one listed is present, the user can access the endpoint
      2. "hasAuthorities()" allows you to check for any set of permissions as denoted in PermissionNames. You populate the parameters as Strings. If one of them are present, the user can access the endpoint
   > Caution: Permissions are allowed if they are conditionally allowed or universally allowed. For these, check the documentation to see if you need a further check to verify the condition and if its set


### Setting Endpoint Authorization Requirements Explicitly In Security Configuration:

We have the ability to map specific endpoints for requiring permissions similarly to how we map endpoints to use authentication or not:
1. Follow steps 1-4 as noted in the "Setting Endpoint Authentication Requirements Explicitly In Security Configuration" section
2. Step 5 is where we can choose the last two options. These options enable authentication as a requirement automatically.
   1. The hasAuthority() method checks if the user has the permission listed, which are described in PermissionNames. You populate the parameters as a String. If the one listed is present, the user can access the endpoint
   2. The hasAuthorities() method checks for if the user has any permissions listed, which are described in PermissionNames. You populate the parameters as Strings. If one of them are present, the user can access the endpoint
3. That's it. The endpoint will now follow the authentication rules you set.

Because this method requires the permission to not have a conditional allow, this approach should only be used for permissions that will either be allowed or denied.

## How Our Application Has Spring Security Setup (As of 10/22/2026):

Our application currently has the following endpoints mapped:

Does not require authentication:

- /api/v1/login
- /api/v1/logout
- /api/v1/signup
- /login.html
- /signup.html

Does require authentication:

- All other endpoints require authentication