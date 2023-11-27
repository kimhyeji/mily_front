package com.mily.user;

import com.mily.article.milyx.MilyX;
import com.mily.article.milyx.MilyXService;
import com.mily.article.milyx.category.CategoryService;
import com.mily.article.milyx.category.entity.FirstCategory;
import com.mily.article.milyx.category.entity.SecondCategory;
import com.mily.article.milyx.comment.MilyXComment;
import com.mily.article.milyx.comment.MilyXCommentService;
import com.mily.base.rq.Rq;
import com.mily.base.rsData.RsData;
import com.mily.estimate.Estimate;
import com.mily.estimate.EstimateRepository;
import com.mily.payment.Payment;
import com.mily.payment.PaymentService;
import com.mily.reservation.Reservation;
import com.mily.reservation.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequestMapping("/user")
@RequiredArgsConstructor
@Controller
public class MilyUserController {
    private final Rq rq;
    private final MilyUserService milyUserService;
    private final CategoryService categoryService;
    private final MilyXService milyXService;
    private final MilyXCommentService milyXCommentService;
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final EstimateRepository estimateRepository;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showUserLogin() {
        return "mily/milyuser/login_form";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/signup")
    public String showSignup() {
        return "mily/milyuser/signup_form";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/signup")
    public String doSignup(@Valid SignupForm signupForm) {
        RsData<MilyUser> signupRs = milyUserService.userSignup(
                signupForm.getUserLoginId(),
                signupForm.getUserPassword(),
                signupForm.getUserName(),
                signupForm.getUserEmail(),
                signupForm.getUserPhoneNumber(),
                signupForm.getUserDateOfBirth()
        );

        if (signupRs.isFail()) {
            rq.historyBack(signupRs.getMsg());
            return "common/js";
        }

        return rq.redirect("/", signupRs.getMsg());
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/lawyerSignup")
    public String showLawyerSignup(Model model) {
        List<FirstCategory> categories = categoryService.getFirstCategories();
        model.addAttribute("categories", categories);
        return "mily/milyuser/lawyer_signup_form";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/lawyerSignup")
    public String doLawyerSignup(@Valid LawyerSignupForm lawyerSignupForm, @Valid SignupForm signupForm) {
        RsData<MilyUser> signupRs1 = milyUserService.userSignup(
                signupForm.getUserLoginId(),
                signupForm.getUserPassword(),
                signupForm.getUserName(),
                signupForm.getUserEmail(),
                signupForm.getUserPhoneNumber(),
                signupForm.getUserDateOfBirth()
        );

        MilyUser milyUser = signupRs1.getData();

        RsData<LawyerUser> signupRs2 = milyUserService.lawyerSignup(
                lawyerSignupForm.getMajor(),
                lawyerSignupForm.getIntroduce(),
                lawyerSignupForm.getOfficeAddress(),
                lawyerSignupForm.getLicenseNumber(),
                lawyerSignupForm.getArea(),
                milyUser,
                lawyerSignupForm.getProfileImg()
        );

        if (signupRs2.isFail()) {
            rq.historyBack(signupRs2.getMsg());
            return "common/js";
        }

        return rq.redirect("/", signupRs2.getMsg());
    }

    @Getter
    @AllArgsConstructor
    public static class SignupForm {
        @NotBlank
        private String userLoginId;

        @NotBlank
        private String userPassword;

        @NotBlank
        private String userName;

        @NotBlank
        @Email
        private String userEmail;

        @NotBlank
        private String userPhoneNumber;

        @NotBlank
        private String userDateOfBirth;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class LawyerSignupForm {
        public MilyUser milyUser;

        @NotBlank
        private String major;

        @NotBlank
        private String introduce;

        @NotBlank
        private String officeAddress;

        @NotBlank
        private String licenseNumber;

        @NotBlank
        private String area;

        private MultipartFile profileImg;
    }

    @GetMapping("checkUserLoginIdDup")
    @ResponseBody
    public RsData checkUserLoginIdDup(String userLoginId) {
        return milyUserService.checkUserLoginIdDup(userLoginId);
    }

    @GetMapping({"checkUserEmailDup", "/mypage/checkUserEmailDup"})
    @ResponseBody
    public RsData checkUserEmail(String userEmail) {
        return milyUserService.checkUserEmailDup(userEmail);
    }

    @GetMapping({"checkUserPhoneNumberDup", "/mypage/checkUserPhoneNumberDup" })
    @ResponseBody
    public RsData checkUserPhoneNumber(String userPhoneNumber) {
        return milyUserService.checkUserPhoneNumberDup(userPhoneNumber);
    }

    @GetMapping("/estimate")
    public String showForm(EstimateCreateForm estimateCreateForm, Model model) {
        List<FirstCategory> firstCategories = categoryService.getFirstCategories();
        List<SecondCategory> secondCategories = categoryService.getSecondCategories();

        model.addAttribute("firstCategories", firstCategories);
        model.addAttribute("secondCategories", secondCategories);

        return "estimate";
    }

    @PostMapping("/estimate")
    public String getEstimate(@Valid EstimateCreateForm estimateCreateForm) {
        MilyUser milyUser = milyUserService.getCurrentUser();

        if (!milyUser.getRole().equals("member")) {
            return rq.redirect("/", "접근 권한이 없습니다.");
        }

        milyUserService.sendEstimate(estimateCreateForm.getFirstCategory(), estimateCreateForm.getSecondCategory(), estimateCreateForm.getArea(), estimateCreateForm.getBody(), milyUser);
        return rq.redirect("/", "견적서가 전달되었습니다.");
    }

    @Getter
    @AllArgsConstructor
    public class EstimateCreateForm {
        private FirstCategory firstCategory;

        private SecondCategory secondCategory;

        @NotBlank
        private String area;

        @NotBlank
        private String body;
    }

    @GetMapping("/waitLawyerList")
    public String getWaitingLawyerList(Principal principal, Model model) {
        String userLoginId = principal.getName();
        if (milyUserService.isAdmin(userLoginId)) {
            List<MilyUser> waitingLawyers = milyUserService.getWaitingLawyerList();
            model.addAttribute("waitingLawyers", waitingLawyers);
            return "/mily/waiting_lawyer_list";
        } else {
            return rq.redirect("/", "접근 권한이 없습니다.");
        }
    }

    @PostMapping("/approveLawyer/{id}")
    public String approveLawyer(@PathVariable int id, Principal principal, HttpServletRequest hsr) {
        // 경로 이동 요청 전, 머물던 URL 을 받아 온다.
        String referer = hsr.getHeader("Referer");

        String adminLoginId = principal.getName();
        milyUserService.approveLawyer(id, adminLoginId);

        return "redirect:" + referer;
    }

    // 아이디 찾기 페이지를 보여주는 핸들러
    @PreAuthorize("isAnonymous()")
    @GetMapping("/findId")
    public String showFindId() {
        return "mily/milyuser/find_id_form";  // 해당 페이지의 경로와 이름을 알맞게 수정하세요.
    }

    // 비밀번호 찾기 페이지를 보여주는 핸들러
    @PreAuthorize("isAnonymous()")
    @GetMapping("/findPassword")
    public String showFindPassword() {
        return "mily/milyuser/find_password_form";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findPassword")
    public String findPassword(@RequestParam String userLoginId, @RequestParam String userEmail, RedirectAttributes redirectAttributes) {
        // findByUsernameAndEmail을 호출하여 사용자를 찾습니다.
        return milyUserService.findByuserLoginIdAndEmail(userLoginId, userEmail)
                .map(member -> {
                    // 임시 비밀번호 발송 로직을 실행합니다.

                    milyUserService.sendTempPasswordToEmail(member);
                    // 성공 메시지와 함께 로그인 페이지로 리다이렉트합니다.
                    redirectAttributes.addFlashAttribute("message", "해당 회원의 이메일로 임시 비밀번호를 발송하였습니다.");
                    return "redirect:/user/login?lastUsername=" + member.getUserLoginId();
                })
                .orElseGet(() -> {
                    // 사용자를 찾을 수 없을 경우 에러 메시지를 설정하고 이전 페이지로 이동합니다.
                    redirectAttributes.addFlashAttribute("errorMessage", "일치하는 회원이 존재하지 않습니다.");
                    return "redirect:/mily/milyuser/find_password_form";
                });
    }

    @PostMapping("/retrieveId")
    public String retrieveId(@RequestParam String userEmail, Model model, RedirectAttributes redirectAttributes) {
        MilyUser milyUser = milyUserService.findUserLoginIdByEmail(userEmail);
        model.addAttribute("foundId", milyUser.getUserLoginId());
        return "mily/milyuser/retrieve_id_result";
    }

    @PostMapping("/retrievePassword")
    public String retrievePassword(@RequestParam String userEmail, String userloginId, Model model, RedirectAttributes redirectAttributes) {
        MilyUser milyUser = milyUserService.findUserLoginIdByEmail(userEmail);
        model.addAttribute("foundPassword", milyUser.getUserPassword());
        return "mily/milyuser/retrieve_password_result";
    }

    @PostMapping("/findLoginIdPage")
    public ResponseEntity<String> retrieveId(@RequestParam("userEmail") String userEmail) {
        MilyUser milyUser = milyUserService.findUserLoginIdByEmail(userEmail);
        if (milyUser.getUserEmail().equals(userEmail)) {
            return ResponseEntity.ok("");
        } else {
            return ResponseEntity.badRequest().body("아이디를 찾을 수 없습니다.");
        }
    }

    @GetMapping("getEstimate")
    public String getEstimate(Model model) {
        MilyUser user = milyUserService.getCurrentUser();

        if (!user.getRole().equals("approve")) {
            return rq.redirect("/", "접근 권한이 없습니다.");
        }

        String category = milyUserService.getCurrentUser().getLawyerUser().getMajor();
        FirstCategory firstCategory = categoryService.findByFTitle(category);
        String area = milyUserService.getCurrentUser().getLawyerUser().getArea();
        List<Estimate> estimates = milyUserService.getEstimate(LocalDateTime.now(), firstCategory, area);
        model.addAttribute("estimates", estimates);
        return "estimate_list";
    }

    /* 마이 페이지, 관리자 대시 보드, 변호사 대시 보드 */
    @GetMapping("/mypage/info")
    public String showMyPage(HttpServletRequest hsr, Model model) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 경로 이동 요청 전, 머물던 URL 을 받아 온다.
        String referer = hsr.getHeader("Referer");

        // 현재 로그인 상태가 아닌 유저의 요청을 받으면 돌려 보냄.
        if (isLoginedUser == null) {
            return "redirect:/";
        }

        // 현재 로그인 된 사용자의 권한이 "member"일 때
        if (isLoginedUser.getRole().equals("member")) {
            // 사용자의 전화 번호를 가리는 작업
            String phoneNumber = isLoginedUser.getUserPhoneNumber();
            phoneNumber = phoneNumber.substring(0, 3) + "-***" + phoneNumber.substring(6, 7) + "-**" + phoneNumber.substring(9);

            // 사용자의 이메일을 가리는 작업
            String email = milyUserService.maskEmail(isLoginedUser.getUserEmail());

            model.addAttribute("user", isLoginedUser);
            model.addAttribute("userPhone", phoneNumber);
            model.addAttribute("userEmail", email);

            // 사용자가 작성 한 글
            List<MilyX> userPosts = milyXService.findByAuthor(isLoginedUser);
            int posts = userPosts.size();

            model.addAttribute("posts", posts);
            model.addAttribute("userPosts", userPosts);

            // 사용자의 상담 예약 내역
            List<Reservation> userReservation = reservationService.findByMilyUser(isLoginedUser);
            model.addAttribute("reservations", userReservation.size());
            model.addAttribute("reservation", userReservation);

            // 사용자의 포인트 충전 내역
            if (isLoginedUser.getPayments() != null) {
                model.addAttribute("payments", isLoginedUser.getPayments().size());
            }

            /* 내 정보 페이지가 기본값으로 적용 됨 */
            return "/mily/milyuser/information/member/info";
        }

        // 현재 로그인 된 사용자의 권한이 "lawyer"일 때
        if (isLoginedUser.getRole().equals("approve")) {
            long id = isLoginedUser.getId();

            // 사용자가 작성 한 답변
            List<MilyXComment> userComments = milyXCommentService.findAuthorId(id);
            int count = userComments.size();

            model.addAttribute("user", isLoginedUser);
            model.addAttribute("commentsCount", count);
            model.addAttribute("comments", userComments);

            // 모든 견적서 보이게
            List<Estimate> estimateList = estimateRepository.findAll();
            int estimates = estimateList.size();

            model.addAttribute("estimatesCount", estimates);
            model.addAttribute("estimate", estimateList);

            return "/mily/milyuser/information/lawyer/lawyer_dashboard";
        }

        // 현재 로그인 된 사용자의 권한이 "admin"일 때
        if (isLoginedUser.getRole().equals("admin")) {
            List<MilyUser> allUsers = milyUserService.findAll();
            int users = allUsers.size();

            model.addAttribute("user", isLoginedUser);
            model.addAttribute("users", users);
            model.addAttribute("userList", allUsers);

            List<MilyUser> waitingLawyers = milyUserService.getWaitingLawyerList();
            int waiting = waitingLawyers.size();

            model.addAttribute("waiting", waiting);
            model.addAttribute("waitingLawyers", waitingLawyers);

            List<Payment> allPayments = paymentService.findAll();
            int payments = allPayments.size();

            if (!allPayments.isEmpty()) {
                model.addAttribute("payments", payments);
                model.addAttribute("paymentsList", allPayments);
            }

            List<MilyX> allMilyX = milyXService.findAll();
            int milyXs = allMilyX.size();

            if (!allMilyX.isEmpty()) {
                model.addAttribute("milyXs", milyXs);
                model.addAttribute("milyXList", allMilyX);
            }

            return "/mily/milyuser/information/admin/admin_dashboard";
        }

        return "redirect:" + referer;
    }

    /* 내 정보 수정 */
    @GetMapping("/mypage/edit")
    public String getEditInformation(HttpServletRequest hsr, Model model) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 경로 이동 요청 전, 머물던 URL 을 받아 온다.
        String referer = hsr.getHeader("Referer");

        if (isLoginedUser != null) {
            model.addAttribute("user", isLoginedUser);
            return "mily/milyuser/information/member/edit";
        }

        return "redirect:" + referer;
    }

    @PostMapping("/mypage/edit")
    public String doEditInformation(@RequestParam String userEmail, @RequestParam String userPhoneNumber, HttpServletRequest hsr, Model model) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 경로 이동 요청 전, 머물던 URL 을 받아 온다.
        String referer = hsr.getHeader("Referer");

        if ( isLoginedUser != null) {
            milyUserService.editInformation(isLoginedUser, userEmail, userPhoneNumber);
            model.addAttribute("user", isLoginedUser);

            return "redirect:" + referer;
        }

        return "redirect:" + referer;
    }

    @GetMapping("/mypage/edit/password")
    public String getEdiitPassword(HttpServletRequest hsr, Model model) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 경로 이동 요청 전, 머물던 URL 을 받아 온다.
        String referer = hsr.getHeader("Referer");

        if (isLoginedUser != null) {
            return "mily/milyuser/information/member/password";
        }

        return "redirect:" + referer;
    }

    @PostMapping("/mypage/edit/password")
    public String postEditPassword(@RequestParam String userPassword, @RequestParam String userPassword2) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        if (isLoginedUser != null && userPassword.equals(userPassword2)) {
            milyUserService.editPassword(isLoginedUser, userPassword);
            return "redirect:/user/mypage/edit";
        }
        return "redirect:/user/mypage/edit";
    }

    /* 비밀번호 체크 */
    @PostMapping("/checkpassword")
    public ResponseEntity<Boolean> checkPassword(@RequestBody Map<String, String> payload) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();
        String rawPassword = payload.get("password");

        return ResponseEntity.ok(milyUserService.checkPassword(isLoginedUser, rawPassword));
    }

    @GetMapping("/mypage/withdraw")
    public String showWithdraw(HttpServletRequest hsr, Model model) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 경로 이동 요청 전, 머물던 URL 을 받아 온다.
        String referer = hsr.getHeader("Referer");

        if (isLoginedUser != null) {
            model.addAttribute("user", isLoginedUser);
            return "mily/milyuser/information/member/withdraw";
        }

        return "redirect:" + referer;
    }

    @GetMapping("/mypage/withdraw/ok")
    public String doWithdraw(HttpServletRequest hsr) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 경로 이동 요청 전, 머물던 URL 을 받아 온다.
        String referer = hsr.getHeader("Referer");

        if (isLoginedUser != null) {
            milyUserService.withdraw(isLoginedUser);
            return "redirect:/user/logout";
        }

        return "redirect:" + referer;
    }

    @GetMapping("lawyers")
    public String lawyerLists(Model model) {
        List<MilyUser> lawyers = milyUserService.findAllApproveLawyer("approve");
        model.addAttribute("lawyers", lawyers);
        return "lawyers";
    }
}