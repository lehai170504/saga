const fs = require('fs');
const path = require('path');

const mappings = {
    // Auth
    'com.saga.application.dto.AuthResponse': 'com.saga.auth.application.dto.AuthResponse',
    'com.saga.application.dto.GoogleLoginRequest': 'com.saga.auth.application.dto.GoogleLoginRequest',
    'com.saga.application.dto.UserProfileDTO': 'com.saga.auth.application.dto.UserProfileDTO',
    'com.saga.application.port.GoogleAuthPort': 'com.saga.auth.application.port.GoogleAuthPort',
    'com.saga.application.port.JwtProviderPort': 'com.saga.auth.application.port.JwtProviderPort',
    'com.saga.application.port.TokenBlacklistPort': 'com.saga.auth.application.port.TokenBlacklistPort',
    'com.saga.application.service.AuthService': 'com.saga.auth.application.service.AuthService',
    'com.saga.application.usecase.LoginUseCase': 'com.saga.auth.application.usecase.LoginUseCase',
    'com.saga.infrastructure.adapter.GoogleAuthAdapter': 'com.saga.auth.infrastructure.adapter.GoogleAuthAdapter',
    'com.saga.infrastructure.adapter.JwtProviderAdapter': 'com.saga.auth.infrastructure.adapter.JwtProviderAdapter',
    'com.saga.infrastructure.adapter.InMemoryTokenBlacklistAdapter': 'com.saga.auth.infrastructure.adapter.InMemoryTokenBlacklistAdapter',
    'com.saga.infrastructure.controller.AuthController': 'com.saga.auth.infrastructure.controller.AuthController',
    'com.saga.application.service.AuthServiceTest': 'com.saga.auth.application.service.AuthServiceTest',

    // Identity
    'com.saga.domain.IdentityMap': 'com.saga.identity.domain.IdentityMap',
    'com.saga.domain.ExternalProvider': 'com.saga.identity.domain.ExternalProvider',
    'com.saga.application.port.ExternalIdentityPort': 'com.saga.identity.application.port.ExternalIdentityPort',
    'com.saga.application.port.ExternalUserProfile': 'com.saga.identity.application.port.ExternalUserProfile',
    'com.saga.application.port.IdentityMapRepositoryPort': 'com.saga.identity.application.port.IdentityMapRepositoryPort',
    'com.saga.application.service.IdentityService': 'com.saga.identity.application.service.IdentityService',
    'com.saga.infrastructure.adapter.ExternalIdentityAdapter': 'com.saga.identity.infrastructure.adapter.ExternalIdentityAdapter',
    'com.saga.infrastructure.adapter.IdentityMapRepositoryAdapter': 'com.saga.identity.infrastructure.adapter.IdentityMapRepositoryAdapter',
    'com.saga.infrastructure.controller.IdentityController': 'com.saga.identity.infrastructure.controller.IdentityController',
    'com.saga.infrastructure.persistence.entity.IdentityMapEntity': 'com.saga.identity.infrastructure.persistence.entity.IdentityMapEntity',
    'com.saga.infrastructure.persistence.repository.SpringDataIdentityMapRepository': 'com.saga.identity.infrastructure.persistence.repository.SpringDataIdentityMapRepository',
    'com.saga.application.service.IdentityServiceTest': 'com.saga.identity.application.service.IdentityServiceTest',
    'com.saga.infrastructure.controller.IdentityControllerTest': 'com.saga.identity.infrastructure.controller.IdentityControllerTest',

    // Project
    'com.saga.domain.GitRepo': 'com.saga.project.domain.GitRepo',
    'com.saga.domain.JiraBoard': 'com.saga.project.domain.JiraBoard',
    'com.saga.domain.IntegrationStatus': 'com.saga.project.domain.IntegrationStatus',
    'com.saga.application.service.ProjectIntegrationService': 'com.saga.project.application.service.ProjectIntegrationService',
    'com.saga.infrastructure.controller.ProjectIntegrationController': 'com.saga.project.infrastructure.controller.ProjectIntegrationController',
    'com.saga.infrastructure.persistence.entity.GitRepoEntity': 'com.saga.project.infrastructure.persistence.entity.GitRepoEntity',
    'com.saga.infrastructure.persistence.entity.JiraBoardEntity': 'com.saga.project.infrastructure.persistence.entity.JiraBoardEntity',
    'com.saga.infrastructure.persistence.repository.JpaGitRepoRepository': 'com.saga.project.infrastructure.persistence.repository.JpaGitRepoRepository',
    'com.saga.infrastructure.persistence.repository.JpaJiraBoardRepository': 'com.saga.project.infrastructure.persistence.repository.JpaJiraBoardRepository',

    // User
    'com.saga.domain.User': 'com.saga.user.domain.User',
    'com.saga.domain.Student': 'com.saga.user.domain.Student',
    'com.saga.domain.Lecturer': 'com.saga.user.domain.Lecturer',
    'com.saga.domain.Role': 'com.saga.user.domain.Role',
    'com.saga.domain.UserStatus': 'com.saga.user.domain.UserStatus',
    'com.saga.application.port.UserRepositoryPort': 'com.saga.user.application.port.UserRepositoryPort',
    'com.saga.application.port.StudentRepositoryPort': 'com.saga.user.application.port.StudentRepositoryPort',
    'com.saga.application.port.LecturerRepositoryPort': 'com.saga.user.application.port.LecturerRepositoryPort',
    'com.saga.infrastructure.adapter.UserRepositoryAdapter': 'com.saga.user.infrastructure.adapter.UserRepositoryAdapter',
    'com.saga.infrastructure.adapter.StudentRepositoryAdapter': 'com.saga.user.infrastructure.adapter.StudentRepositoryAdapter',
    'com.saga.infrastructure.adapter.LecturerRepositoryAdapter': 'com.saga.user.infrastructure.adapter.LecturerRepositoryAdapter',
    'com.saga.infrastructure.persistence.entity.UserEntity': 'com.saga.user.infrastructure.persistence.entity.UserEntity',
    'com.saga.infrastructure.persistence.entity.StudentEntity': 'com.saga.user.infrastructure.persistence.entity.StudentEntity',
    'com.saga.infrastructure.persistence.entity.LecturerEntity': 'com.saga.user.infrastructure.persistence.entity.LecturerEntity',
    'com.saga.infrastructure.persistence.repository.JpaUserRepository': 'com.saga.user.infrastructure.persistence.repository.JpaUserRepository',
    'com.saga.infrastructure.persistence.repository.JpaStudentRepository': 'com.saga.user.infrastructure.persistence.repository.JpaStudentRepository',
    'com.saga.infrastructure.persistence.repository.JpaLecturerRepository': 'com.saga.user.infrastructure.persistence.repository.JpaLecturerRepository',

    // Shared Security Filter
    'com.saga.infrastructure.security.JwtAuthenticationFilter': 'com.saga.shared.security.JwtAuthenticationFilter'
};

function getAllJavaFiles(dir, fileList = []) {
    if (!fs.existsSync(dir)) return fileList;
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const filePath = path.join(dir, file);
        if (fs.statSync(filePath).isDirectory()) {
            getAllJavaFiles(filePath, fileList);
        } else if (filePath.endsWith('.java')) {
            fileList.push(filePath);
        }
    }
    return fileList;
}

const basePath = 'src';
const javaFiles = getAllJavaFiles(basePath);

console.log(`Found ${javaFiles.length} Java files.`);

const importReplacements = Object.entries(mappings).map(([oldFull, newFull]) => {
    return { oldImport: `import ${oldFull};`, newImport: `import ${newFull};` };
});

for (const file of javaFiles) {
    let content = fs.readFileSync(file, 'utf8');
    
    // Find if this file needs to be moved based on its old package and class name
    // Parse package name and class name
    const packageMatch = content.match(/package\s+(com\.saga[\w\.]+)\s*;/);
    const classMatch = file.match(/([^\\]+)\.java$/);
    
    let oldFullName = null;
    let newFullName = null;
    let originalContent = content;

    if (packageMatch && classMatch) {
        oldFullName = `${packageMatch[1]}.${classMatch[1]}`;
        if (mappings[oldFullName]) {
            newFullName = mappings[oldFullName];
        }
    }

    // Replace all old imports with new imports
    let modified = false;
    for (const { oldImport, newImport } of importReplacements) {
        if (content.includes(oldImport)) {
            content = content.replace(new RegExp(oldImport.replace(/\./g, '\\.'), 'g'), newImport);
            modified = true;
        }
    }

    // Replace usages inside same package that didn't have imports but now need them?
    // This is tricky. Java allows using classes in the same package without import.
    // E.g., `application.dto.AuthResponse` and `application.dto.GoogleLoginRequest`
    // Since we move them both to `auth.application.dto`, they are still in the same package! So no new import needed.
    // If they were in `application.service` and `application.usecase`, they already had imports!
    // So modifying explicit imports is generally enough.

    if (newFullName) {
        // Update package declaration
        const newPackage = newFullName.substring(0, newFullName.lastIndexOf('.'));
        content = content.replace(packageMatch[0], `package ${newPackage};`);
        
        // Write to new location
        const newPath = file.replace(packageMatch[1].replace(/\./g, '\\'), newPackage.replace(/\./g, '\\'));
        const newDir = path.dirname(newPath);
        fs.mkdirSync(newDir, { recursive: true });
        fs.writeFileSync(newPath, content, 'utf8');
        
        if (file !== newPath) {
            fs.unlinkSync(file);
        }
        console.log(`Moved: ${oldFullName} -> ${newFullName}`);
    } else {
        // File doesn't move, just update imports if modified
        if (modified || content !== originalContent) {
            fs.writeFileSync(file, content, 'utf8');
            console.log(`Updated imports in: ${file}`);
        }
    }
}

// Clean up empty directories
function cleanEmptyDirs(dir) {
    if (!fs.existsSync(dir)) return;
    const files = fs.readdirSync(dir);
    if (files.length === 0) {
        fs.rmdirSync(dir);
    } else {
        for (const file of files) {
            const filePath = path.join(dir, file);
            if (fs.statSync(filePath).isDirectory()) {
                cleanEmptyDirs(filePath);
            }
        }
        // Try again after children might be removed
        if (fs.readdirSync(dir).length === 0) {
            fs.rmdirSync(dir);
        }
    }
}

cleanEmptyDirs('src/main/java/com/saga/application');
cleanEmptyDirs('src/main/java/com/saga/domain');
cleanEmptyDirs('src/main/java/com/saga/infrastructure');
cleanEmptyDirs('src/test/java/com/saga/application');
cleanEmptyDirs('src/test/java/com/saga/infrastructure');
console.log('Refactoring complete.');
