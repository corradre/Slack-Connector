package org.mule.extension.internal;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

public class ExecuteErrorsProvider implements ErrorTypeProvider {

    @Override public Set<ErrorTypeDefinition> getErrorTypes() {
        HashSet<ErrorTypeDefinition> errors = new HashSet<>();
        errors.add(SlackErrors.INVALID_PARAMETER);
        errors.add(SlackErrors.BAD_CREDENTIALS);
        errors.add(SlackErrors.ILLEGAL_ACTION);
        return errors;
    }
}
