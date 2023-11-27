package com.mily.article.milyx.comment;

import com.mily.article.milyx.MilyX;
import com.mily.base.rsData.RsData;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MilyXCommentService {
    private final MilyXCommentRepository milyXCommentRepository;
    private final MilyUserService milyUserService;

    public RsData<MilyXComment> createComment (MilyX milyX, String body) {
        LocalDateTime now = LocalDateTime.now();
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        MilyXComment milyXComment = MilyXComment.builder()
                .milyX(milyX)
                .author(isLoginedUser)
                .body(body)
                .adopt(false)
                .createDate(now)
                .build();

        milyXCommentRepository.save(milyXComment);

        return new RsData<>("S-1", "답변 등록 완료", milyXComment);
    }

    public List<MilyXComment> findAll () {
        return milyXCommentRepository.findAll();
    }

    public List<MilyXComment> findByMilyX (MilyX milyX) {
        return milyXCommentRepository.findByMilyX(milyX);
    }

    public Optional<MilyXComment> findById(long id) {
        return milyXCommentRepository.findById(id);
    }

    public long getMilyXIdByCommentId (long commentId) {
        MilyXComment milyXComment = milyXCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + commentId));
        return milyXComment.getMilyX().getId();
    }

    @Transactional
    public RsData<MilyXComment> delete (long id) {
        Optional<MilyXComment> mxcOptional = milyXCommentRepository.findById(id);

        if (mxcOptional.isEmpty()) {
            return new RsData<>("F-1", "댓글을 찾아 올 수 없습니다.", null);
        }

        MilyXComment mxc = mxcOptional.get();
        milyXCommentRepository.delete(mxc);

        return new RsData<>("S-1", "댓글 삭제 완료", mxc);
    }

    public List<MilyXComment> findAuthorId(long id) {
        return milyXCommentRepository.findByAuthorId(id);
    }
}