package com.mily.image;

import com.mily.base.rq.Rq;
import com.mily.base.rsData.RsData;
import com.mily.standard.util.Ut;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/Image")
@Validated
public class ImageController {
    private final Rq rq;
    private final ImageService ImageService;

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable long id, HttpServletRequest request) throws FileNotFoundException {
        Image image = ImageService.findById(id).get();
        String filePath = image.getFilePath();

        Resource resource = new InputStreamResource(new FileInputStream(filePath));

        String contentType = request.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

        if (contentType == null) contentType = "application/octet-stream";

        String fileName = Ut.url.encode(image.getOriginFileName()).replace("%20", " ");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType)).body(resource);
    }

    @PostMapping("/temp")
    @ResponseBody
    public RsData<String> temp(@RequestParam("file") MultipartFile file) {
        Image savedFile = ImageService.saveTempFile(rq.getMilyUser(), file);

        return RsData.of("S-1", "임시 파일이 생성되었습니다.", savedFile.getUrl());
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void removeOldTempFiles() {
        ImageService.removeOldTempFiles();
    }
}