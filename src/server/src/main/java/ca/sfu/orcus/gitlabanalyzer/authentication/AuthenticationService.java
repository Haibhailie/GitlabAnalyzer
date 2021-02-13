package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationRepository repository;
    private final JwtTokenCreator tokenCreator;

    @Autowired
    public AuthenticationService(AuthenticationRepository repository, JwtTokenCreator tokenCreator) {
        this.repository = repository;
        this.tokenCreator = tokenCreator;
    }

    public String addNewUser(User newUser) {
        String jwt = tokenCreator.createJwt(newUser);
        newUser.setJwt(jwt);
        repository.addNewUser(newUser);
        return jwt;
    }
}
