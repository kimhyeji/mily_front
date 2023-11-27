package com.mily.image;

import com.mily.standard.util.Ut;
import com.mily.user.MilyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.rmi.NotBoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final ImageRepository imageRepository;

    // 조회
    public Optional<Image> findBy(String relTypeCode, Long relId, String typeCode, String type2Code, long fileNo) {
        return imageRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(relTypeCode, relId, typeCode, type2Code, fileNo);
    }

    @Transactional
    public Image save(String relTypeCode, Long relId, String typeCode, String type2Code, long fileNo, MultipartFile sourceFile) {
        String sourceFilePath = Ut.file.toFile(sourceFile, AppConfig.getTempDirPath());
        return save(relTypeCode, relId, typeCode, type2Code, fileNo, sourceFilePath);
    }

    // 명령
    @Transactional
    public Image save(String relTypeCode, Long relId, String typeCode, String type2Code, long fileNo, String sourceFile) {
        if (!Ut.file.exists(sourceFile)) return null;
        // fileNo 가 0 이면, 이 파일은 로직상 무조건 새 파일이다.
        if (fileNo > 0) remove(relTypeCode, relId, typeCode, type2Code, fileNo);

        String originFileName = Ut.file.getOriginFileName(sourceFile);
        String fileExt = Ut.file.getExt(originFileName);
        String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
        String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);
        long fileSize = new File(sourceFile).length();
        String fileDir = getCurrentDirName(relTypeCode);

        int maxTryCount = 3;

        Image image = null;

        for (int tryCount = 1; tryCount <= maxTryCount; tryCount++) {
            try {
                if (fileNo == 0) fileNo = genNextFileNo(relTypeCode, relId, typeCode, type2Code);

                image = Image.builder()
                        .relTypeCode(relTypeCode)
                        .relId(relId)
                        .typeCode(typeCode)
                        .type2Code(type2Code)
                        .fileExtTypeCode(fileExtTypeCode)
                        .fileExtType2Code(fileExtType2Code)
                        .originFileName(originFileName)
                        .fileSize(fileSize)
                        .fileNo(fileNo)
                        .fileExt(fileExt)
                        .fileDir(fileDir)
                        .build();

                imageRepository.save(image);

                break;
            } catch (Exception ignore) {

            }
        }

        File file = new File(image.getFilePath());

        file.getParentFile().mkdirs();

        Ut.file.moveFile(sourceFile, file);
        Ut.file.remove(sourceFile);

        return image;
    }

    private long genNextFileNo(String relTypeCode, Long relId, String typeCode, String type2Code) {
        return imageRepository
                .findTop1ByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeOrderByFileNoDesc(relTypeCode, relId, typeCode, type2Code)
                .map(Image -> Image.getFileNo() + 1)
                .orElse(1L);
    }

    private String getCurrentDirName(String relTypeCode) {
        return relTypeCode + "/" + Ut.date.getCurrentDateFormatted("yyyy_MM_dd");
    }

    public Map<String, Image> findImagesMapKeyByFileNo(String relTypeCode, long relId, String typeCode, String type2Code) {
        List<Image> Images = imageRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeOrderByFileNoAsc(relTypeCode, relId, typeCode, type2Code);

        return Images
                .stream()
                .collect(Collectors.toMap(
                        Image -> String.valueOf(Image.getFileNo()), // key
                        Image -> Image // value
                ));
    }

    public Optional<Image> findById(long id) {
        return imageRepository.findById(id);
    }

    @Transactional
    public void remove(String relTypeCode, long relId, String typeCode, String type2Code, long fileNo) {
        findBy(relTypeCode, relId, typeCode, type2Code, fileNo).ifPresent(this::remove);
    }

    @Transactional
    public void remove(Image Image) {
        Ut.file.remove(Image.getFilePath());
        imageRepository.delete(Image);
        imageRepository.flush();
    }

    public List<Image> findByRelId(String modelName, Long relId) {
        return imageRepository.findByRelTypeCodeAndRelId(modelName, relId);
    }

    @Transactional
    public Image saveTempFile(MilyUser actor, MultipartFile file) {
        return save("temp_" + actor.getUserLoginId(), actor.getId(), "common", "editorUpload", 0, file);
    }

    @Transactional
    public Image tempToFile(String url, BaseEntity entity, String typeCode, String type2Code, long fileNo) {
        String fileName = Ut.file.getFileNameFromUrl(url);
        String fileExt = Ut.file.getFileExt(fileName);

        long ImageId = Long.parseLong(fileName.replace("." + fileExt, ""));
        Image tempImage = findById(ImageId).get();

        Image newImage = save(entity.getModelName(), entity.getId(), typeCode, type2Code, fileNo, tempImage.getFilePath());

        remove(tempImage);

        return newImage;
    }

    public void removeOldTempFiles() {
        findOldTempFiles().forEach(this::remove);
    }

    private List<Image> findOldTempFiles() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        return imageRepository.findByRelTypeCodeAndCreateDateBefore("temp", oneDayAgo);
    }
}