# Project Overview

## Introduction

This project is a dashboard application built with Kotlin, featuring a server component, shared modules, and mobile
applications. It provides integration with GitHub and includes various features for repository management and
monitoring. The application supports multiple platforms including iOS and web through Kotlin Multiplatform.

## Project Structure

- `server/`: Main server component
    - Contains REST API endpoints
    - Handles GitHub integration
    - Includes database operations
    - Houses integration tests
- `shared/`: Common code shared across components
    - Contains data models and DAOs
    - Includes common utilities
    - Platform-specific implementations
- `composeApp/`: Compose Multiplatform application
    - UI components
    - Platform-specific implementations
- `iosApp/`: iOS application module
    - iOS-specific implementations
    - Platform integration
- `docs/`: Project documentation
- `buildSrc/`: Build configuration and dependencies
- `gradle/`: Gradle configuration files

## Technology Stack

- **Backend**: Kotlin
- **Database**: PostgresSQL + Redis
- **Caching**: Redis
- **Integration**: GitHub API
- **Testing**: Kotlin Test Framework
- **UI Framework**: Compose Multiplatform
- **Code Quality**: Detekt
- **CI/CD**: Firebase Distribution

## Main Components

1. **GitHub Integration**
    - Repository management
    - Data synchronization
    - Custom repository utilities
    - User authentication and authorization

2. **Data Management**
    - Repository data storage
    - Entity management
    - Data access objects (DAOs)
    - Cache management

3. **API Routes**
    - GitHub-related endpoints
    - Repository operations
    - Data synchronization endpoints
    - User management endpoints

4. **Mobile Applications**
    - Cross-platform UI components
    - Platform-specific implementations
    - Shared business logic

## Setup Instructions

1. Environment Setup:
    - Install JDK 11 or later
    - Install Kotlin development environment
    - Install Redis server
    - Install H2 Database
    - (Optional) Install Xcode for iOS development

2. Configuration:
    - Configure property files:
        - Copy `.example.properties` to create necessary property files
        - Set up `test-server.properties` for testing environment
        - Configure `gradle.properties` for build settings
        - Update `local.properties` with your local development settings
    - Set up required environment variables
    - Configure GitHub API credentials
    - Set up Firebase configuration (if needed)

3. Build and Run:
   ```bash
   # Build the entire project
   ./gradlew build

   # Run server tests
   ./gradlew :server:test

   # Run shared module tests
   ./gradlew :shared:test

   # Run compose app
   ./gradlew :composeApp:run

   # Build iOS app (requires Xcode)
   ./gradlew :iosApp:build
   ```

## Development Guidelines

### Code Style

- Follow Kotlin coding conventions
- Use ktlint for code formatting
- Follow the official Kotlin style guide
- Maintain consistent naming conventions
- Document public APIs and complex logic

### Testing Requirements

- Write unit tests for new features
- Maintain minimum 80% code coverage
- Include integration tests for API endpoints
- Use test utilities from `server/src/test/kotlin/com/takaotech/dashboard/utils`
- Mock external dependencies in tests

### Project Organization

- Keep shared code in the shared module
- Platform-specific code goes in respective modules
- Use proper package structure
- Follow SOLID principles
- Implement proper error handling

### Documentation

- Document all API endpoints
- Update README for major changes
- Include KDoc for public functions
- Document configuration changes
- Keep architecture diagrams updated

## Testing

- Integration tests: `server/src/integrationTest`
- Unit tests: `server/src/test`
- UI tests: `composeApp/src/test`
- Test utilities: `server/src/test/kotlin/com/takaotech/dashboard/utils`
- Run tests before submitting PRs

### Mobile App Distribution

1. Configure Firebase Distribution
2. Build release version
3. Upload to Firebase Distribution

## Troubleshooting

### Common Issues

1. Database Connection Issues
    - Verify H2 Database configuration
    - Check connection string
    - Ensure proper permissions

2. Redis Connection
    - Verify Redis server is running
    - Check Redis connection settings
    - Clear Redis cache if needed

3. GitHub API Issues
    - Verify API credentials
    - Check rate limits
    - Validate request format

### Debug Tools

- Use logging framework for debugging
- Check application logs
- Monitor Redis cache
- Use GitHub API debug tools

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes following guidelines
4. Add tests
5. Submit pull request
6. Ensure CI passes

## Support

For issues and support:

- Create GitHub issues for bugs
- Use pull requests for contributions
- Check documentation for common solutions
- Contact team for urgent issues
