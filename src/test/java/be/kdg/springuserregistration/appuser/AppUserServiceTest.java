package be.kdg.springuserregistration.appuser;

import be.kdg.springuserregistration.email.EmailSender;
import be.kdg.springuserregistration.registration.EmailValidator;
import be.kdg.springuserregistration.registration.RegistrationRequest;
import be.kdg.springuserregistration.registration.token.ConfirmationToken;
import be.kdg.springuserregistration.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

class AppUserServiceTest {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private AppUserService appUserService;
    @Mock
    private EmailValidator emailValidator;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        appUserService = new AppUserService(appUserRepository, bCryptPasswordEncoder, confirmationTokenService);
    }

    @Test
    void itShouldThrowWhenEmailIsAlreadyTaken() {
        String firstName = "John";
        String lastName = "Doe";
        String email = "notreally@gmail.com";
        String password = "test";
        AppUser appUser = new AppUser(firstName,
                lastName,
                email,
                password,
                AppUserRole.USER);

        // Reguest
        RegistrationRequest request = new RegistrationRequest(appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getEmail(),
                appUser.getPassword());
        given(appUserRepository.findByEmail(request.getEmail())).willReturn(Optional.of(appUser));

        // valid email
        given(emailValidator.test(request.getEmail())).willReturn(true);

        // Then
        assertThatThrownBy(() -> appUserService.signUpUser(appUser))
                .hasMessage("email already taken");
        // Finally
        then(appUserRepository.findByEmail(request.getEmail())).isNotNull();

    }

    @Test
    void itShouldReturnAppUserWhenSignUpIsSuccessful() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String email = "notreally@gmail.com";
        String password = "test";
        AppUser appUser = new AppUser(firstName, lastName, email, password, AppUserRole.USER);

        // Reguest
        RegistrationRequest request = new RegistrationRequest(appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getEmail(),
                appUser.getPassword());

        // valid email
        given(emailValidator.test(request.getEmail())).willReturn(true);

        // When
        String token = appUserService.signUpUser(appUser);
        assertThat(token).isNotNull().hasLineCount(1);
    }

}