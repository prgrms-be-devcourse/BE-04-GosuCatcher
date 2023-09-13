// package com.foo.gosucatcher.domain.member.application;
//
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
// import com.foo.gosucatcher.domain.member.domain.MemberRepository;
//
// @ExtendWith(MockitoExtension.class)
// class MemberServiceTest {
//
// 	@InjectMocks
// 	private MemberService memberService;
// 	@Mock
// 	private MemberRepository memberRepository;
// 	@Mock
// 	private MemberProfileRepository memberProfileRepository;
//
// 	@Test
// 	void mockTest() {
// 	}
//
// 	// @Test
// 	// @DisplayName("중복이 아닌 이메일인 경우 회원가입에 성공한다.")
// 	// void signUpSuccessWhenNoDuplicateEmail() {
// 	// 	//given
// 	// 	var request = new MemberSignupRequest("test", "test@gmail.com", "12345");
// 	// 	Member member = MemberSignupRequest.toMember(request);
// 	//
// 	// 	doReturn(Optional.empty())
// 	// 		.when(memberRepository)
// 	// 		.findByEmail(request.email());
// 	// 	doReturn(member)
// 	// 		.when(memberRepository)
// 	// 		.save(any(Member.class));
// 	//
// 	// 	//when, then
// 	// 	assertThatCode(() -> memberService.signup(request))
// 	// 		.doesNotThrowAnyException();
// 	// 	verify(memberRepository, times(1))
// 	// 		.findByEmail(request.email());
// 	// 	verify(memberRepository, times(1))
// 	// 		.save(any(Member.class));
// 	// }
// 	//
// 	// @Test
// 	// @DisplayName("중복 이메일인 경우 회원가입에 실패한다.")
// 	// void signUpFailWhenDuplicateEmail() {
// 	// 	//given
// 	// 	var request = new MemberSignupRequest("test", "test@gmail.com", "12345");
// 	// 	memberService.signup(request);
// 	//
// 	// 	doThrow(InvalidValueException.class)
// 	// 		.when(memberRepository)
// 	// 		.findByEmail(request.email());
// 	//
// 	// 	//when, then
// 	// 	assertThatThrownBy(() -> memberService.signup(request))
// 	// 		.isInstanceOf(InvalidValueException.class);
// 	// 	verify(memberRepository, times(2))
// 	// 		.findByEmail(request.email());
// 	// 	verify(memberRepository, times(1))
// 	// 		.save(any(Member.class));
// 	// }
// 	//
// 	// @Test
// 	// @DisplayName("중복이 아닌 이메일인 경우 예외가 발생하지 않는다.")
// 	// void doNotThrowExceptionWhenNoDuplicateEmail() {
// 	// 	//given
// 	// 	doReturn(Optional.empty())
// 	// 		.when(memberRepository)
// 	// 		.findByEmail("test@gmail.com");
// 	//
// 	// 	//when, then
// 	// 	assertThatCode(() -> memberService.checkDuplicatedEmail("test@gmail.com"))
// 	// 		.doesNotThrowAnyException();
// 	// 	verify(memberRepository, times(1))
// 	// 		.findByEmail("test@gmail.com");
// 	// }
// 	//
// 	// @Test
// 	// @DisplayName("중복 이메일인 경우 예외가 발생한다.")
// 	// void throwExceptionWhenDuplicateEmail() {
// 	// 	//given
// 	// 	var request = new MemberSignupRequest("test", "test@gmail.com", "12345");
// 	// 	memberService.signup(request);
// 	// 	doThrow(InvalidValueException.class)
// 	// 		.when(memberRepository)
// 	// 		.findByEmail("test@gmail.com");
// 	//
// 	// 	//when, then
// 	// 	assertThatThrownBy(() -> memberService.checkDuplicatedEmail("test@gmail.com"))
// 	// 		.isInstanceOf(InvalidValueException.class);
// 	// }
// 	//
// 	// @Test
// 	// @DisplayName("로그인 정보가 회원 정보와 일치하면 로그인에 성공한다.")
// 	// void logInSuccessIfMemberInfoSameAsLogInRequestInfo() {
// 	// 	//given
// 	// 	var signUpRequest = new MemberSignupRequest("test", "test@gmail.com", "12345");
// 	// 	Member member = MemberSignupRequest.toMember(signUpRequest);
// 	// 	memberService.signup(signUpRequest);
// 	//
// 	// 	//when
// 	// 	var logInRequest = new MemberLoginRequest("test@gmail.com", "12345");
// 	// 	doReturn(Optional.of(member))
// 	// 		.when(memberRepository)
// 	// 		.findByEmail("test@gmail.com");
// 	//
// 	// 	//then
// 	// 	assertThat(memberService.login(logInRequest))
// 	// 		.isInstanceOf(MemberCertifiedResponse.class);
// 	// }
// 	//
// 	// @Test
// 	// @DisplayName("이메일 정보가 일치하면 비밀번호 찾기 성공")
// 	// void findSuccessIfMemberEmailIsValid() {
// 	// 	//given
// 	// 	var signUpRequest = new MemberSignupRequest("test", "test@gmail.com", "12345");
// 	// 	memberService.signup(signUpRequest);
// 	// 	Member member = MemberSignupRequest.toMember(signUpRequest);
// 	// 	String email = signUpRequest.email();
// 	//
// 	// 	doReturn(Optional.of(member))
// 	// 		.when(memberRepository)
// 	// 		.findByEmail(email);
// 	//
// 	// 	//when, then
// 	// 	assertThatCode(() -> memberService.findPassword(email))
// 	// 		.doesNotThrowAnyException();
// 	// 	verify(memberRepository, times(2))
// 	// 		.findByEmail(email);
// 	// }
// }
