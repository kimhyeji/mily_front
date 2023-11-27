package com.mily.article.milyx;

import com.mily.article.milyx.category.CategoryService;
import com.mily.article.milyx.category.entity.FirstCategory;
import com.mily.article.milyx.category.entity.SecondCategory;
import com.mily.article.milyx.comment.MilyXComment;
import com.mily.article.milyx.comment.MilyXCommentService;
import com.mily.base.rq.Rq;
import com.mily.base.rsData.RsData;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/milyx")
public class MilyXController {
    private final CategoryService categoryService;
    private final MilyXService milyXService;
    private final MilyXCommentService milyXCommentService;
    private final MilyUserService milyUserService;
    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("")
    public String showMilyX(Model model) {
        List<MilyX> milyx = milyXService.getAllPosts();
        Collections.reverse(milyx);
        model.addAttribute("milyx", milyx);

        List<MilyXComment> comments = milyXCommentService.findAll();
        Collections.reverse(comments);

        System.out.println(comments);
        int count = comments.size();
        model.addAttribute("count", count);
        model.addAttribute("comments", comments);


        try {
            MilyUser isLoginedUser = milyUserService.getCurrentUser();
            if (isLoginedUser != null) {
                model.addAttribute("user", isLoginedUser);
            }
            return "mily/milyx/milyx_index";
        } catch (NullPointerException e) {
            return "mily/milyx/milyx_index";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(Model model) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        if (isLoginedUser == null) {
            return "redirect:/milyx";
        }

        // 권한 확인 후 권한 없을 시 돌려 보냄
        if (!isLoginedUser.getRole().equals("member")) {
            return "redirect:/milyx";
        }

        int userPoint = isLoginedUser.getMilyPoint();
        model.addAttribute("myPoint", userPoint);

        if (userPoint < 50) {
            return "redirect:/payment";
        }

        // 최대값 = 해당 유저가 가진 포인트 or 100 중 작은 값으로 결정
        int maxPoint = Math.min(userPoint, 100);

        // 100까지 5 단위의 숫자 리스트 생성
        List<Integer> pointOptions = IntStream.rangeClosed(50, maxPoint)
                .filter(i -> i % 5 == 0)
                .boxed()
                .collect(Collectors.toList());
        model.addAttribute("pointOptions", pointOptions);

        List<FirstCategory> firstCategories = categoryService.getFirstCategories();
        model.addAttribute("firstCategories", firstCategories);
        return "mily/milyx/milyx_create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@RequestParam int firstCategory, @RequestParam String secondCategory, @RequestParam String subject, @RequestParam String body, @RequestParam int point) {
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        FirstCategory fc = categoryService.findByFId(firstCategory);
        SecondCategory sc = categoryService.findBySId(Integer.parseInt(secondCategory));

        RsData<MilyX> rsData = milyXService.create(isLoginedUser, fc, sc, subject, body, point);
        return "redirect:/milyx";
    }

    @GetMapping("/secondCategories")
    public ResponseEntity<List<SecondCategory>> validateId(@RequestParam(value = "firstCategoryId") int firstCategoryId) {
        List<SecondCategory> sc = categoryService.findByFirstCategoryId(firstCategoryId);
        return ResponseEntity.ok().body(sc);
    }

    @AllArgsConstructor
    @Getter
    public static class CreateForm {
        private FirstCategory firstCategory;
        private SecondCategory secondCategory;
        private String subject;
        private String body;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{id}")
    public String showDetail(Model model, @PathVariable long id) {
        try {
            MilyUser isLoginedUser = milyUserService.getCurrentUser();
            if (isLoginedUser != null) {
                String confirmRole = isLoginedUser.getRole();
                model.addAttribute("role", confirmRole);
                model.addAttribute("user", isLoginedUser);
            }
            MilyX milyX = milyXService.findById(id).get();
            int view = milyX.getView() + 1;

            MilyX mx = MilyX.builder()
                    .view(view)
                    .build();

            milyX.updateView(view);
            milyXService.updateView(milyX.getId(), mx);

            model.addAttribute("milyx", milyX);
            model.addAttribute("isAuthor", milyX.getAuthor().getId() == (isLoginedUser.getId()));

            return "mily/milyx/milyx_detail";
        } catch (NullPointerException e) {
            return "mily/milyx/milyx_detail";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String getModify(Model model, @PathVariable long id) {
        // 현재 로그인 한 유저의 정보
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 수정하려는 게시물의 정보 찾아오기
        MilyX milyX = milyXService.findById(id).get();

        // 수정하려는 게시물의 작성자가 맞는 지 체크
        if (milyX.getAuthor().getId() != isLoginedUser.getId()) {
            return "redirect:/milyx/detail/" + id;
        }

        // 수정하려는 게시물의 댓글 유무 확인
        if (!milyX.getComments().isEmpty()) {
            /*
            수정하려는 게시물의 댓글이 존재한다면 수정 불가
            -> 수정하려는 게시물의 detail 로 리다이렉트
             */
            return "redirect:/milyx/detail/" + id;
        }

        model.addAttribute("milyx", milyX);

        return "mily/milyx/milyx_modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String doModify(@PathVariable Long id, @RequestParam String subject, @RequestParam String body) {
        milyXService.modify(id, subject, body);
        return "redirect:/milyx/detail/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String doDelete(@PathVariable Long id, HttpServletRequest hsr) {
        MilyX mx = milyXService.findById(id).orElse(null);

        // 경로 이동 요청 전, 머물던 URL 을 받아 온다.
        String referer = hsr.getHeader("Referer");

        if (mx == null) {
            return "redirect:/milyx/detail" + id;
        }

        // 현재 로그인 한 유저의 정보
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 삭제하려는 게시물의 정보 찾아오기
        MilyX milyX = milyXService.findById(id).get();

        // 관리자인 지 아닌 지 체크
        if (!isLoginedUser.getUserLoginId().equals("admin999")) {
            // 삭제하려는 게시물의 작성자가 맞는 지 체크
            if (milyX.getAuthor().getId() != isLoginedUser.getId()) {
                return "redirect:/milyx/detail" + id;
            }

            // 삭제하려는 게시물의 댓글 유무 확인
            if (!milyX.getComments().isEmpty()) {
                return "redirect:/milyx/detail" + id;
            }
        }

        milyXService.delete(id);

        return "redirect:" + referer;
    }
}