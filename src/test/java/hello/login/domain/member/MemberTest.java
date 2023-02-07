package hello.login.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MemberTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("검증 체크")
    void validationTest() {
        //given
        Member member = new Member("", "", null);
        //when
        List<String> actual = validator.validate(member)
                .stream()
                .map(o -> o.getPropertyPath().toString())
                .sorted(String::compareTo)
                .collect(Collectors.toList());
        //then
        Assertions.assertThat(actual).containsExactly("loginId", "name", "password");
    }
}
