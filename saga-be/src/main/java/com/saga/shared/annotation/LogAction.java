package com.saga.shared.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark methods that should be intercepted by the
 * AuditLogAspect
 * to automatically record an audit log in the system.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {

    /**
     * The type of action being performed (e.g., "USER_LOGIN", "IMPORT_ROSTER",
     * "CREATE_TEAM").
     */
    String actionType();
}
