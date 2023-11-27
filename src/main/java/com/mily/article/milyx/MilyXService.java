package com.mily.article.milyx;

import com.mily.article.milyx.category.entity.FirstCategory;
import com.mily.article.milyx.category.entity.SecondCategory;
import com.mily.article.milyx.repository.MilyXRepository;
import com.mily.base.rsData.RsData;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MilyXService {
    private final MilyXRepository mxr;
    private final MilyUserRepository mur;

    public List<MilyX> getAllPosts() {
        return mxr.findAll();
    }

    public List<MilyX> findByAuthor(MilyUser author) {
        return mxr.findByAuthor(author);
    }

    @Transactional
    public RsData<MilyX> create(MilyUser author, FirstCategory firstCategory, SecondCategory secondCategory, String subject, String body, int milyPoint) {
        LocalDateTime now = LocalDateTime.now();
        int point = author.getMilyPoint();

        MilyX mx = MilyX.builder()
                .firstCategory(firstCategory)
                .secondCategory(secondCategory)
                .subject(subject)
                .body(body)
                .author(author)
                .milyPoint(milyPoint)
                .createDate(now)
                .build();

        mx = mxr.save(mx);

        // 글을 작성한 유저의 포인트에서 글 작성에 소모한 포인트만큼 차감
        point -= milyPoint;

        // 차감한 값으로 세팅
        author.setMilyPoint(point);
        mur.save(author);

        return new RsData<>("S-1", "게시물 생성 완료", mx);
    }

    @Transactional
    public RsData<MilyX> modify(Long id, String subject, String body) {
        LocalDateTime now = LocalDateTime.now();

        MilyX mx = mxr.findById(id).orElse(null);

        if (mx == null) {
            return new RsData<>("F-1", "게시물을 찾아 올 수 없습니다.", mx);
        }

        mx.setSubject(subject);
        mx.setBody(body);
        mx.setModifyDate(now);

        mxr.save(mx);

        return new RsData<>("S-1", "게시물 수정 완료", mx);
    }

    @Transactional
    public RsData<MilyX> delete(Long id) {
        Optional<MilyX> mxOptional = mxr.findById(id);
        int point;

        if (mxOptional.isEmpty()) {
            return new RsData<>("F-1", "게시물을 찾아 올 수 없습니다.", null);
        }

        MilyX mx = mxOptional.get();

        // 글의 point 값을 가져 온다.
        point = mx.getMilyPoint();

        // 글 작성자의 정보를 가져 온다.
        MilyUser author = mx.getAuthor();

        // 글 작성자의 milyPoint 값을 가져 온다.
        int milyPoint = author.getMilyPoint();

        // 글 작성 시 소모한 포인트만큼 반환 한다.
        milyPoint += point;

        // repository 에 저장 한다.
        author.setMilyPoint(milyPoint);
        mur.save(author);

        mxr.delete(mx);

        return new RsData<>("S-1", "게시물 삭제 완료", mx);
    }

    public Optional<MilyX> findById(long id) {
        return mxr.findById(id);
    }

    @Transactional
    public void updateView(long id, MilyX milyX) {
        MilyX milyx = mxr.findById(id).orElseThrow((() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다.")));

        milyX.updateView(milyX.getView());
    }

    /* 더미 데이터용입니다 */
    @Transactional
    public RsData<MilyX> dummyCreate(MilyUser author, FirstCategory firstCategory, SecondCategory secondCategory, String subject, String body, int milyPoint) {
        LocalDateTime now = LocalDateTime.now();

        MilyX mx = MilyX.builder()
                .firstCategory(firstCategory)
                .secondCategory(secondCategory)
                .subject(subject)
                .body(body)
                .author(author)
                .milyPoint(milyPoint)
                .createDate(now)
                .build();

        mx = mxr.save(mx);

        return new RsData<>("S-1", "게시물 생성 완료", mx);
    }

    public List<MilyX> findAll() {
        return mxr.findAll();
    }
}