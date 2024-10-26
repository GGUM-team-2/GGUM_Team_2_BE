package com.example.ggum.domain.user.controller;

import com.example.ggum.domain.user.dto.ResponseDTO;
import com.example.ggum.domain.user.dto.UserDTO;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import com.example.ggum.security.TokenProvider;
import com.example.ggum.domain.user.service.MailService;
import com.example.ggum.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<?> withdrawUser(@RequestParam("userId") Long userId) {
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
    public ResponseEntity<?> mailSend(@RequestParam("mail") String mail) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            if (!userService.isSchoolEmail(mail)) {
                throw new RuntimeException("학교 웹메일이 아닙니다.");
            }

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

    //회원가입을 위한 메일 전송
    @PostMapping("/mailSendForSignup")
    public ResponseEntity<?> mailSendForSignup(@RequestParam("mail") String mail) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            if (userService.existsByEmail(mail)) {
                throw new RuntimeException("이미 등록된 이메일입니다.");
            }

            if (!userService.isSchoolEmail(mail)) {
                throw new RuntimeException("학교 웹메일이 아닙니다.");
            }

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

    //비밀번호 재설정을 위한 메일 전송
    @PostMapping("/mailSendForCheckEmail")
    public ResponseEntity<?> mailSendForCheckEmail(@RequestParam("mail") String mail) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            if (!userService.existsByEmail(mail)) {
                throw new RuntimeException("해당 이메일을 가진 사용자가 존재하지 않습니다.");
            }

            mailService.sendMail(mail);
            response.put("success", true);
            response.put("message", "비밀번호 찾기 인증 메일이 전송되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    //토큰 재발행
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims;
            try {
                claims = tokenProvider.validateAndGetClaims(token);
            } catch (ExpiredJwtException e) {
                claims = e.getClaims();
            }

            if (claims != null) {
                User user = userRepository.findById(Long.parseLong(claims.getSubject()));
                if (user != null) {
                    String newToken = tokenProvider.create(user);
                    return ResponseEntity.ok().body("새로운 토큰: " + newToken);
                }
            }

            return ResponseEntity.badRequest().body("유효하지 않은 사용자 정보입니다.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }
    }

    //닉네임 변경
    @PostMapping("/changeUsername")
    public ResponseEntity<?> changeUsername(@RequestParam("userId") Long userId, @RequestParam("newUsername") String newUsername) {
        try {
            if (userService.existsByUsername(newUsername)) {
                throw new RuntimeException("이미 존재하는 닉네임입니다.");
            }

            User user = userRepository.findById(userId);
            if (user == null) {
                throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
            }

            user.setUsername(newUsername);
            userRepository.save(user);


            UserDTO responseDTO = UserDTO.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //닉네임 중복 확인
    @GetMapping("/checkSamename")
    public ResponseEntity<?> checkSamename(@RequestParam("username") String username) {
        HashMap<String, Object> response = new HashMap<>();

        if (userService.existsByUsername(username)) {
            response.put("success", false);
            response.put("message", "이미 존재하는 닉네임입니다.");
            return ResponseEntity.badRequest().body(response);
        } else {
            response.put("success", true);
            response.put("message", "사용가능한 닉네임입니다!");
            return ResponseEntity.ok().body(response);
        }
    }

    //토큰 검증용 API
    @GetMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String userId = tokenProvider.validateAndGetUserId(token);

            return ResponseEntity.ok().body("사용자 ID: " + userId);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }
    }

    // 인증번호 일치 여부 확인
    @GetMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestParam("mail") String mail, @RequestParam("userNumber") int userNumber) {
        boolean isMatch = mailService.checkVerificationNumber(mail, userNumber); // 사용자별 인증번호 확인

        if (isMatch) {
            return ResponseEntity.ok().body("인증번호가 일치합니다.");
        } else {
            return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
        }
    }
}
