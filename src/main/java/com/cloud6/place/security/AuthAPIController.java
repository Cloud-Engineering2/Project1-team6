package com.cloud6.place.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud6.place.entity.User;
import com.cloud6.place.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthAPIController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/signup")
	public ResponseEntity<String> registerUser(@RequestBody SignUpRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 아이디입니다.");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 이메일입니다.");
		}


		String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

		User user = new User();
		user.setUsername(signUpRequest.getUsername());
		user.setEmail(signUpRequest.getEmail());
		user.setPassword(encodedPassword);
		

		userRepository.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다.");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
	    String username = loginRequest.getUsername();
	    String password = loginRequest.getPassword();

	    // 아이디 체크
	    User user = userRepository.findByUsername(username).orElse(null);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .contentType(MediaType.APPLICATION_JSON)
	                .body("아이디가 존재하지 않습니다.");
	    }

	    // 비밀번호 체크
	    if (!passwordEncoder.matches(password, user.getPassword())) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .contentType(MediaType.APPLICATION_JSON)
	                .body("비밀번호가 일치하지 않습니다.");
	    }

	    // 토큰 생성
	    String token = jwtTokenProvider.createToken(username);

	    // HttpOnly 쿠키에 저장
	    ResponseCookie cookie = ResponseCookie.from("token", token)
	            .httpOnly(true)  // JavaScript에서 접근 불가
	            .secure(false)   // HTTPS에서만 사용 (개발 중이라면 false로 설정)
	            .path("/")       // 쿠키 유효 경로
	            .maxAge(86400)   // 1일 (86400초)
	            .sameSite("Lax")  // CSRF 공격 방지
	            .build();

	    // 로그인 성공 응답
	    return ResponseEntity.ok()
	            .header(HttpHeaders.SET_COOKIE, cookie.toString())
	            .body("로그인 성공");
	}
	@GetMapping("/check-login")
	public ResponseEntity<?> checkLogin(HttpServletRequest request) {
	    // 쿠키에서 토큰 확인
	    Cookie[] cookies = request.getCookies();
	    String token = null;
	    
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (cookie.getName().equals("token")) {
	                token = cookie.getValue();
	                break;
	            }
	        }
	    }
	    if (token != null && jwtTokenProvider.validateToken(token)) {
	        // 토큰이 유효하면 로그인 상태
	        return ResponseEntity.ok("로그인됨");
	    } else {
	        // 토큰이 없거나 유효하지 않으면 로그인되지 않음
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인되지 않음");
	    }
	}
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
	    // 쿠키에서 'token'을 찾고 삭제하기 위해 설정
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (cookie.getName().equals("token")) {
	                // 쿠키에서 토큰을 삭제하고, 만료된 쿠키를 다시 설정
	                cookie.setValue(null);
	                cookie.setMaxAge(0);  // 만료 시간 0으로 설정
	                cookie.setPath("/");  // 쿠키 경로 설정
	                response.addCookie(cookie);  // 삭제된 쿠키를 응답에 추가
	                break;
	            }
	        }
	    }

	    // 로그아웃 성공 응답
	    return ResponseEntity.ok("로그아웃 되었습니다.");
	}

}
