package com.example.ecommerce_user_service.services;

import com.example.ecommerce_user_service.dtos.UserDto;
import com.example.ecommerce_user_service.models.Session;
import com.example.ecommerce_user_service.models.SessionStatus;
import com.example.ecommerce_user_service.models.User;
import com.example.ecommerce_user_service.repositories.SessionRepository;
import com.example.ecommerce_user_service.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    // object of above two will be created by spring jpa. who will create the object of bcrypt? we should create.
    // where? SpringSceurity class
    //private BCryptPasswordEncoder bCryptPasswordEncoder;


    public AuthService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        //this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserDto signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        //user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);

    }
    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        //User doesn't exist
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        //validation
        //if (!user.getPassword().equals(password)) { after encrypting the password
        /*if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            return null;
        }*/
        //String token = RandomStringUtils.randomAlphanumeric(30);

       /* String message = "{\n" +
        "   \"email\": \"naman@scaler.com\",\n" +
        "   \"roles\": [\n" +
        "      \"mentor\",\n" +
        "      \"ta\"\n" +
        "   ],\n" +
        "   \"expirationDate\": \"23rdOctober2023\"\n" +
        "}";

        byte[] content = message.getBytes(StandardCharsets.UTF_8);*/

        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", user.getEmail());
        jsonForJwt.put("roles", user.getRoles());
        //if(key is not null then only put)
        jsonForJwt.put("ExpirationDate", new Date());
        jsonForJwt.put("createdAt", new Date());

        //for signature generation
        MacAlgorithm alg = Jwts.SIG.HS256;
        SecretKey key = alg.key().build();

         String token = Jwts.builder().claims(jsonForJwt).signWith(key,alg).compact();


        //have written any conversion logic of user to userdto?

        UserDto userDto = User.from(user);

        // should the token be part of userdto. does client worried about that?no. so keep it in headers
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" +token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);
        return response;
    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if(sessionOptional.isEmpty()){
            return null;
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);

        return ResponseEntity.ok().build();

    }

    public SessionStatus validate(String token, Long userId) {
        Optional <Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if(sessionOptional.isEmpty()){
            return null;
        }
        MacAlgorithm alg = Jwts.SIG.HS256;
        SecretKey key = alg.key().build();

        Claims claims =
                Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        //if(exirytime>currentdate){
        //check login device
        return SessionStatus.ACTIVE;
    }
}
