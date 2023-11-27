package com.mily.article.feed.service;

import com.mily.article.feed.entity.Feed;
import com.mily.article.feed.repository.FeedRepository;
import com.mily.base.rsData.RsData;
import com.mily.user.MilyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {
    private  final FeedRepository feedRepository;

    public Optional<Feed> findById(long id) {
        return feedRepository.findById(id);
    }

    public List<Feed> getAllFeeds() {
        return feedRepository.findAll();
    }

    @Transactional
    public RsData<Feed> create(MilyUser isLoginedUser, String subject, String body) {
        LocalDateTime now = LocalDateTime.now();

        Feed feed = Feed.builder()
                .subject(subject)
                .body(body)
                .author(isLoginedUser)
                .createDate(now)
                .build();

        feed = feedRepository.save(feed);

        return new RsData<>("S-1", "피드 작성 완료", feed);
    }

    @Transactional
    public void updateView(long id, Feed feed) {
        Feed f = feedRepository.findById(id).orElseThrow((() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다.")));

        feed.updateView(feed.getView());
    }

    @Transactional
    public RsData<Feed> modify(Long id, String subject, String body) {
        LocalDateTime now = LocalDateTime.now();

        Feed feed = feedRepository.findById(id).orElse(null);

        if (feed == null) {
            return new RsData<>("F-1", "게시물을 찾아 올 수 없습니다.", feed);
        }

        feed.setSubject(subject);
        feed.setBody(body);
        feed.setModifyDate(now);

        feedRepository.save(feed);

        return new RsData<>("S-1", "게시물 수정 완료", feed);
    }

    @Transactional
    public RsData<Feed> delete(Long id) {
        Optional<Feed> checkFeed = feedRepository.findById(id);

        if (checkFeed.isEmpty()) {
            return new RsData<>("F-1", "게시물을 찾아 올 수 없습니다.", null);
        }

        Feed feed = checkFeed.get();
        feedRepository.delete(feed);

        return new RsData<>("S-1", "게시물 삭제 완료", feed);
    }
}