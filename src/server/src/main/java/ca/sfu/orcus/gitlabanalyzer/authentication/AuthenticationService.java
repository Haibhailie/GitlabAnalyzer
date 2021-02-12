package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationRepository repository;

    @Autowired
    public AuthenticationService(AuthenticationRepository repository) {
        this.repository = repository;
    }

    public void addNewUser(User newUser) {
        repository.addNewUser(newUser);
    }
}
