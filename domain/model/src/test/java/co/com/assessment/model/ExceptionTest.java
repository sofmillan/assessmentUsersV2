package co.com.assessment.model;

import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.exception.SecurityErrorMessage;
import co.com.assessment.model.exception.SecurityException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class ExceptionTest {
    @Test
    void businessExceptionShouldStoreMessage() {
        BusinessException ex = new BusinessException(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED);

        assertEquals(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED.getMessage(), ex.getMessage());
        assertEquals(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED, ex.getBusinessErrorMessage());
    }

    @Test
    void securityExceptionShouldStoreMessage() {
        SecurityException ex = new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS);

        assertEquals(SecurityErrorMessage.INVALID_CREDENTIALS.getMessage(), ex.getMessage());
        assertEquals(SecurityErrorMessage.INVALID_CREDENTIALS, ex.getSecurityErrorMessage());
    }
}
