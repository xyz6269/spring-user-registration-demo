package be.kdg.springuserregistration.registration;

import be.kdg.springuserregistration.appuser.AppUser;
import be.kdg.springuserregistration.appuser.AppUserRole;
import be.kdg.springuserregistration.appuser.AppUserService;
import be.kdg.springuserregistration.registration.token.ConfirmationToken;
import be.kdg.springuserregistration.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    public String register(RegistrationRequest request) {
        //TODO implement
        boolean isValid = emailValidator.test(request.getEmail());
        if (!isValid) {
          throw new IllegalStateException("Email is not valid");
        }
        String token = appUserService.signUpUser(new AppUser(request.getFirstName(),
                request.getLastName(), request.getEmail(), request.getPassword(), AppUserRole.USER));

        String link = "http://localhost:8080/registration/confirm?token=" + token;

        return token;
    }
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }
}
