package com.mily.article.feed.controller;

import com.mily.article.feed.entity.Feed;
import com.mily.article.feed.service.FeedService;
import com.mily.base.rsData.RsData;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;
    private final MilyUserService milyUserService;

    @GetMapping("")
    public String showFeeds(Model model) {
        List<Feed> feeds = feedService.getAllFeeds();
        Collections.reverse(feeds);

        model.addAttribute("feed", feeds);

        try {
            MilyUser isLoginedUser = milyUserService.getCurrentUser();
            if (isLoginedUser != null) {
                model.addAttribute("user", isLoginedUser);
            }
            return "mily/feed/feed_index";
        } catch (NullPointerException e) {
            return "mily/feed/feed_index";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(Model model, HttpServletRequest hsr) {
        String referer = hsr.getHeader("Referer");
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        if (isLoginedUser.getRole().equals("approve")) {
            return "mily/feed/feed_create";
        }

        return "redirect:" + referer;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String doCreate(@RequestParam String subject, @RequestParam String body, HttpServletRequest hsr) {
        String referer = hsr.getHeader("Referer");
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        if (!isLoginedUser.getRole().equals("approve")) {
            return "redirect:" + referer;
        }

        RsData<Feed> rsData = feedService.create(isLoginedUser, subject, body);
        return "redirect:/feed";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{id}")
    public String showDetail(Model model, @PathVariable long id, HttpServletRequest hsr) {
        String referer = hsr.getHeader("Referer");

        try {
            MilyUser isLoginedUser = milyUserService.getCurrentUser();
            if (isLoginedUser != null) {
                String confirmRole = isLoginedUser.getRole();
                model.addAttribute("role", confirmRole);
                model.addAttribute("user", isLoginedUser);
            }
            Feed feed = feedService.findById(id).get();
            int view = feed.getView() + 1;

            Feed f = Feed.builder()
                    .view(view)
                    .build();

            feed.updateView(view);
            feedService.updateView(feed.getId(), feed);

            model.addAttribute("feed", feed);
            model.addAttribute("isAuthor", feed.getAuthor().getId() == (isLoginedUser.getId()));

            return "mily/feed/feed_detail";
        } catch (NullPointerException e) {
            return "redirect:" + referer;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String getModify(Model model, @PathVariable long id, HttpServletRequest hsr) {
        String referer = hsr.getHeader("Referer");
        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        Feed feed = feedService.findById(id).get();

        if (feed.getAuthor().getId() != isLoginedUser.getId()) {
            return "redirect:" + referer;
        }

        model.addAttribute("feed", feed);

        return "mily/feed/feed_modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String doModify(@PathVariable Long id, @RequestParam String subject, @RequestParam String body) {
        feedService.modify(id, subject, body);

        return "redirect:/feed/detail/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String doDelete(@PathVariable Long id, HttpServletRequest hsr) {
        String referer = hsr.getHeader("Referer");

        Feed feed = feedService.findById(id).orElse(null);

        if (feed == null) {
            return "redirect:" + referer;
        }

        MilyUser isLoginedUser = milyUserService.getCurrentUser();

        if (!isLoginedUser.getUserLoginId().equals("admin999")) {
            if (isLoginedUser.getId() != feed.getAuthor().getId()) {
                return "redirect:" + referer;
            }
        }

        feedService.delete(id);

        return "redirect:" + referer;
    }
}