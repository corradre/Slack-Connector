package org.mule.extension.internal;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.MuleErrors;

import java.util.Optional;

public enum SlackErrors implements ErrorTypeDefinition<SlackErrors> {

    INVALID_PARAMETER,
    TIME_OUT,
    NOT_ALLOWED,
    ILLEGAL_ACTION(NOT_ALLOWED),
    BAD_CREDENTIALS(MuleErrors.CONNECTIVITY);

    private ErrorTypeDefinition<? extends Enum<?>> parent;

    SlackErrors(ErrorTypeDefinition<? extends Enum<?>> parent) {
        this.parent = parent;
    }

    SlackErrors() {
    }

    @Override
    public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
        return Optional.ofNullable(parent);
    }
}
