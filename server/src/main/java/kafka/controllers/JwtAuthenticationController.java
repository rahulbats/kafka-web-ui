package kafka.controllers;

import kafka.model.JwtRequest;
import kafka.model.JwtResponse;
import kafka.service.JwtUserDetailsService;
import kafka.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        Authentication authentication = authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        //Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
        //List<String> groupNames =  authorities.stream().map(authority->authority.getAuthority().substring("ROLE_".length())).collect(Collectors.toList());
        //final UserDetails userDetails = userDetailsService
          //      .loadUserByUsername(authenticationRequest.getUsername());

        UserDetails userDetails = new User(authenticationRequest.getUsername(), authenticationRequest.getPassword(),
                authentication.getAuthorities());
        //BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //final UserDetails userDetails = new User(authenticationRequest.getUsername(), encoder.encode( authenticationRequest.getPassword()),
          //      new ArrayList<>());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token, authenticationRequest.getUsername()).getString());
    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            //kafkaAuthenticationService.validate(username, password);

        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        } /*catch (KafkaException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }*/
    }
}
