package com.saga.shared.aspect;

import com.saga.project.service.SystemAuditLogService;
import com.saga.shared.annotation.LogAction;
import com.saga.user.entity.User;
import com.saga.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final SystemAuditLogService auditLogService;
    private final JpaUserRepository userRepository;

    @Around("@annotation(logAction)")
    public Object logAuditAction(ProceedingJoinPoint joinPoint, LogAction logAction) throws Throwable {
        Object result = null;
        boolean success = true;
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            success = false;
            errorMessage = ex.getMessage();
            throw ex;
        } finally {
            try {
                recordLog(joinPoint, logAction, success, errorMessage);
            } catch (Exception e) {
                log.error("Failed to record audit log via AOP", e);
            }
        }
    }

    private void recordLog(ProceedingJoinPoint joinPoint, LogAction logAction, boolean success, String errorMessage) {
        UUID actorId = getCurrentUserId();
        if (actorId == null) {
            log.warn("Cannot log action {}: No authenticated user found", logAction.actionType());
            return;
        }

        Map<String, Object> details = new HashMap<>();
        details.put("success", success);

        if (!success && errorMessage != null) {
            details.put("error", errorMessage);
        }

        // Try to serialize arguments
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            Map<String, Object> arguments = new HashMap<>();
            if (parameterNames != null && args != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    // Avoid serializing huge or non-serializable objects (like MultipartFile,
                    // ServletRequest, etc.)
                    // We only serialize basic types or DTOs
                    Object arg = args[i];
                    if (arg != null && isSerializable(arg)) {
                        arguments.put(parameterNames[i], arg);
                    } else if (arg != null) {
                        arguments.put(parameterNames[i], arg.getClass().getSimpleName());
                    }
                }
            }
            details.put("arguments", arguments);
        } catch (Exception e) {
            log.warn("Could not serialize arguments for audit log", e);
        }

        auditLogService.logActionAsync(actorId, logAction.actionType(), details);
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String email) {
            return userRepository.findByEmail(email).map(User::getId).orElse(null);
        }
        return null;
    }

    private boolean isSerializable(Object obj) {
        String name = obj.getClass().getName();
        return !name.startsWith("org.springframework.web.") &&
                !name.startsWith("jakarta.servlet.") &&
                !name.startsWith("java.io.") &&
                !name.startsWith("org.apache.poi.");
    }
}
