package hello.login.web.login.form;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.hibernate.validator.internal.engine.path.PathImpl.createPathFromString;

public class LoginFormTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validationTest() {
        Set<ConstraintViolation<LoginForm>> validates = validator.validate(new LoginForm(null, null));

        Assertions.assertThat(validates).extracting("propertyPath")
                .containsOnly(
                        createPathFromString("password"),
                        createPathFromString("loginId"));
    }
}
