package com.mily;

import com.mily.article.milyx.MilyX;
import com.mily.article.milyx.MilyXService;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MilyHomeController {
    private final MilyUserService milyUserService;
    private final MilyXService milyXService;

    @GetMapping("/")
    public String showMilyMain(Model model) {
        List<MilyX> milyXList = milyXService.findAll();
        model.addAttribute("milyx", milyXList);

        try {
            MilyUser isLoginedUser = milyUserService.getCurrentUser();
            if (isLoginedUser != null) {
                model.addAttribute("user", isLoginedUser);
            }
            return "/mily/mily_main";
        } catch (NullPointerException e) {
            return "/mily/mily_main";
        }
    }

    @GetMapping("/none")
    public String showNone(Model model) {
        try {
            MilyUser isLoginedUser = milyUserService.getCurrentUser();
            if (isLoginedUser != null) {
                model.addAttribute("user", isLoginedUser);
            }
            return "/none";
        } catch (NullPointerException e) {
            return "/none";
        }
    }
}