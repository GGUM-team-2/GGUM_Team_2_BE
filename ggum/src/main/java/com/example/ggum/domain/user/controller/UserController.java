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
@RequestMapping("/api/v1/auth")
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

    //관리자 회원가입
    @PostMapping("/managerSignup")
    public ResponseEntity<?> registerManager(@RequestBody UserDTO userDTO) {
        return registerUser(userDTO, "MANAGER");
    }

    //일반유저 회원가입
    @PostMapping("/userSignup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        return registerUser(userDTO, "USER");
    }

    //로그인
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

    //회원탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdrawUser(@RequestParam Long userId) {
        try {
            userService.withdraw(userId);
            return ResponseEntity.ok().body("Accept withdraw");
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 인증 이메일 전송
    @PostMapping("/mailSend")
    public ResponseEntity<?> mailSend(@RequestParam String mail) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            // 이메일 도메인 검증
            if (!isSchoolEmail(mail)) {
                throw new RuntimeException("학교 웹메일이 아닙니다.");
            }

            // 학교 웹메일일 경우 인증 메일 전송
            mailService.sendMail(mail);
            response.put("success", true);
            response.put("message", "인증 메일이 전송되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 학교 웹메일인지 확인하는 파싱 알고리즘
    private boolean isSchoolEmail(String email) {
        boolean result = false;
        String[] parts = email.split("@");
        if (parts.length != 2) {// 이메일 형식인지 확인
            result = false;
        }
        else {//학교 웹 메일인지 확인
            String domain = parts[1];
            String[] domainParts = domain.split("\\.");
            int length = domainParts.length;
            if (length >= 3 && "ac".equals(domainParts[length - 2]) && "kr".equals(domainParts[length - 1])) {
                result = true;
            }
        }
        return result;
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
