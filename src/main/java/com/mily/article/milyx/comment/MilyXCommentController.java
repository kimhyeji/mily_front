package com.mily.article.milyx.comment;

import com.mily.article.milyx.MilyX;
import com.mily.article.milyx.MilyXService;
import com.mily.article.milyx.repository.MilyXRepository;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserRepository;
import com.mily.user.MilyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/milyxcomment")
public class MilyXCommentController {
    private final MilyXService milyXService;
    private final MilyXRepository milyXRepository;
    private final MilyXCommentService milyXCommentService;
    private final MilyXCommentRepository milyXCommentRepository;
    private final MilyUserService milyUserService;
    private final MilyUserRepository milyUserRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(@PathVariable("id") long id, @RequestParam String body) {
        MilyX milyX = milyXService.findById(id).get();
        milyXCommentService.createComment(milyX, body);
        return String.format("redirect:/milyx/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public RedirectView doDelete(@PathVariable long id, RedirectAttributes redirectAttributes) {
        MilyXComment mxc = milyXCommentService.findById(id).orElse(null);
        long milyXId = milyXCommentService.getMilyXIdByCommentId(id);

        if (mxc == null) {
            redirectAttributes.addFlashAttribute("message", "댓글을 찾을 수 없습니다.");
            return new RedirectView("/milyx/detail/" + milyXId, true);
        }

        // 현재 로그인 한 유저의 정보
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        // 삭제하려는 댓글의 정보 찾아오기
        MilyXComment milyXComment = milyXCommentService.findById(id).get();

        // 삭제하려는 댓글의 채택 여부 확인
        if (milyXComment.isAdopt()) {
            redirectAttributes.addFlashAttribute("message", "댓글 삭제 권한이 없습니다.");
            return new RedirectView("/milyx/detail/" + milyXId, true);
        }

        // 삭제하려는 댓글의 작성자가 맞는 지 체크
        if (mxc.getAuthor().getId() != isLoginedUser.getId()) {
            redirectAttributes.addFlashAttribute("message", "댓글 삭제 권한이 없습니다.");
            return new RedirectView("/milyx/detail/" + milyXId, true);
        }

        milyXCommentService.delete(id);

        redirectAttributes.addFlashAttribute("message", "댓글 삭제 완료");
        return new RedirectView("/milyx/detail/" + milyXId, true);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/adopt/{id}")
    public RedirectView doAdopt(@PathVariable long id, RedirectAttributes redirectAttributes) {
        MilyXComment mxc = milyXCommentService.findById(id).orElse(null);
        long milyXId = milyXCommentService.getMilyXIdByCommentId(id);
        Optional<MilyX> milyX = milyXService.findById(milyXId);

        // 이미 채택된 답변이 있는 경우
        if (milyX.get().isHasAdoptedAnswer()) {
            redirectAttributes.addFlashAttribute("message", "이미 채택된 답변이 있습니다.");
            return new RedirectView("/milyx/detail/" + milyXId, true);
        }

        int point = milyX.get().getMilyPoint();

        if (mxc == null) {
            redirectAttributes.addFlashAttribute("message", "댓글을 찾을 수 없습니다.");
            return new RedirectView("/milyx/detail/" + milyXId, true);
        }

        // 포인트 지급
        MilyUser milyUser = mxc.getAuthor();
        int milyPoint = point + milyUser.getMilyPoint();
        milyUser.setMilyPoint(milyPoint);
        milyUserRepository.save(milyUser);

        // 답변을 채택된 것으로 표시
        mxc.setAdopt(true);
        milyXCommentRepository.save(mxc);

        milyX.get().setHasAdoptedAnswer(true);
        milyXRepository.save(milyX.get());

        return new RedirectView("/milyx/detail/" + milyXId, true);
    }
}