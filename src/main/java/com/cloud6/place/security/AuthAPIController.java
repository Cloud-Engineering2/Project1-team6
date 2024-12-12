package com.cloud6.place.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.cloud6.place.entity.User;
import com.cloud6.place.repository.UserRepository;

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
		System.out.println(signUpRequest);
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 아이디입니다.");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 이메일입니다.");
		}

//		System.out.println("회원가입 input 비밀번호: " + signUpRequest.getPassword()); // 입력된 비밀번호 로그

		// 비밀번호 암호화 (단 한번만)
//		String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
//		System.out.println("암호화된 비밀번호: " + encodedPassword); // 암호화된 비밀번호 로그

		User user = new User();
		user.setUsername(signUpRequest.getUsername());
		user.setEmail(signUpRequest.getEmail());
		user.setPassword(signUpRequest.getPassword());
		

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
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
					.body("아이디가 존재하지 않습니다.");
		}

		if (!passwordEncoder.matches(password, user.getPassword())) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		            .contentType(MediaType.APPLICATION_JSON)
		            .body("비밀번호가 일치하지 않습니다.");
		}

		// 토큰 생성
		String token = jwtTokenProvider.createToken(username);

		// 로그인 성공 시 토큰 반환
		return ResponseEntity.ok(Map.of("token", "Bearer " + token));
	}
}
