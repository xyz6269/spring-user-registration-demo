package be.kdg.springuserregistration.appuser;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.repository.config.BootstrapMode;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.data.repository.config.BootstrapMode.*;

@DataJpaTest(  properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
}, bootstrapMode = DEFAULT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppUserRepositoryTest {
    @Mock
    private AppUserRepository appUserRepository;

    // test if the appUserRepository is not null
    @Test
    void testAppUserRepositoryNotNull() {
        assertThat(appUserRepository).isNotNull();
    }


    @Test
    void findByEmail() {
        // given
        String email = "test@gmail.com";
        AppUser appUser = new AppUser("John", "Doe", email, "test", AppUserRole.USER);
        given(appUserRepository.findByEmail(email)).willReturn(Optional.of(appUser));
        // when
        Optional<AppUser> foundAppUser = appUserRepository.findByEmail(email);
        // then
        assertThat(foundAppUser).isPresent();

        assertThat(foundAppUser.get().getEmail()).isEqualTo(email);

    }

    @Test
    void enableAppUser() {
        // given
        String email = "test@gmail.com";
        AppUser appUser = new AppUser("John", "Doe", email, "test", AppUserRole.USER);
        given(appUserRepository.save(appUser)).willReturn(appUser);
        // when
        AppUser savedAppUser = appUserRepository.save(appUser);
        // then
        assertThat(savedAppUser.getIsEnabled()).isTrue();



    }
}