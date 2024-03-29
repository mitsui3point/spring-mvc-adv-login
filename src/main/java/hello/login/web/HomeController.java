package hello.login.web;

import hello.login.SessionConst;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.session.SessionManager;
import hello.login.web.annotation.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

//    @GetMapping("/")
    public String loginHome(@CookieValue(value = "memberId", required = false) Long memberId,
                            Model model) {

        if (memberId == null) {
            return "home";
        }

        Member member = memberRepository.findById(memberId);
        if (member == null) {
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome";
    }

//    @GetMapping("/")
    public String loginHomeV2(HttpServletRequest request,
                              Model model) {

        Member sessionMember = (Member) sessionManager.getSession(request);

        if (sessionMember == null) {
            return "home";
        }

        model.addAttribute("member", sessionMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String loginHomeV3(HttpServletRequest request,
                              Model model) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String loginHomeV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                    Model model) {

        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String loginHomeV3ArgumentResolver(@Login Member loginMember,
                                    Model model) {

        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}