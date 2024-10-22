package com.example.ggum.domain.user.controller;

import com.example.ggum.domain.user.dto.ResponseDTO;
import com.example.ggum.domain.user.dto.UserDTO;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.security.TokenProvider;
import com.example.ggum.domain.user.service.MailService;
import com.example.ggum.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MailService mailService; // MailService 주입

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ResponseEntity<?> registerUser(UserDTO userDTO, String role) {
        try {
            User user = com.example.ggum.domain.user.entity.User.builder()
                    .email(userDTO.getEmail())
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .role(role)
                    .likeCount(0)
                    .dislikeCount(0)
                    .build();
            com.example.ggum.domain.user.entity.User registeredUser = userService.create(user);
            UserDTO responseUserDTO = UserDTO.builder()
                    .email(registeredUser.getEmail())
                    .id(registeredUser.getId())
                    .username(registeredUser.getUsername())
                    .role(registeredUser.getRole())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/managerSignup")
    public ResponseEntity<?> registerManager(@RequestBody UserDTO userDTO) {
        return registerUser(userDTO, "MANAGER");
    }

    @PostMapping("/userSignup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        return registerUser(userDTO, "USER");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
        User user = userService.getByCredentials(userDTO.getEmail(), userDTO.getPassword(), passwordEncoder);

        if (user != null) {
            final String token = tokenProvider.create(user);
            final UserDTO responseUserDTO = UserDTO.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login failed")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 인증 이메일 전송
    @PostMapping("/mailSend")
    public ResponseEntity<?> mailSend(@RequestParam String mail) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            mailService.sendMail(mail); // 비동기로 메일 전송
            response.put("success", true);
            response.put("message", "인증 메일이 전송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 인증번호 일치 여부 확인
    @GetMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestParam String mail, @RequestParam int userNumber) {
        boolean isMatch = mailService.checkVerificationNumber(mail, userNumber); // 사용자별 인증번호 확인

        if (isMatch) {
            return ResponseEntity.ok().body("인증번호가 일치합니다.");
        } else {
            return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
        }
    }
}
