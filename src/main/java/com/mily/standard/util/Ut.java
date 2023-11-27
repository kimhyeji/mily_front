package com.mily.standard.util;

import com.mily.image.AppConfig;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ut {
    public static class date {
        public static String getCurrentDateFormatted(String pattern) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }
    }

    public static class url {
        public static String encode(String message) {
            try {
                return URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }

        public static String modifyQueryParam(String url, String paramName, String paramValue) {
            url = deleteQueryParam(url, paramName);
            url = addQueryParam(url, paramName, paramValue);

            return url;
        }

        public static String addQueryParam(String url, String paramName, String paramValue) {
            if (!url.contains("?")) {
                url += "?";
            }

            if (!url.endsWith("?") && !url.endsWith("&")) {
                url += "&";
            }

            url += paramName + "=" + paramValue;

            return url;
        }

        private static String deleteQueryParam(String url, String paramName) {
            int startPoint = url.indexOf(paramName + "=");
            if (startPoint == -1) return url;

            int endPoint = url.substring(startPoint).indexOf("&");

            if (endPoint == -1) {
                return url.substring(0, startPoint - 1);
            }

            String urlAfter = url.substring(startPoint + endPoint + 1);

            return url.substring(0, startPoint) + urlAfter;
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
    public static class DataNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public DataNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "unauthorized")
    public static class UnauthorizedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "invalidData")
    public static class InvalidDataException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public InvalidDataException(String message) {
            super(message);
        }
    }

    public static class file {
        private static final String ORIGIN_FILE_NAME_SEPARATOR;

        static {
            ORIGIN_FILE_NAME_SEPARATOR = "--originFileName_";
        }

        public static String getOriginFileName(String file) {
            if (file.contains(ORIGIN_FILE_NAME_SEPARATOR)) {
                String[] fileInfos = file.split(ORIGIN_FILE_NAME_SEPARATOR);
                return fileInfos[fileInfos.length - 1];
            }

            return Paths.get(file).getFileName().toString();
        }

        public static String toFile(MultipartFile multipartFile, String tempDirPath) {
            if (multipartFile == null) return "";
            if (multipartFile.isEmpty()) return "";

            String filePath = tempDirPath + "/" + UUID.randomUUID() + ORIGIN_FILE_NAME_SEPARATOR + multipartFile.getOriginalFilename();

            try {
                multipartFile.transferTo(new File(filePath));
            } catch (IOException e) {
                return "";
            }

            return filePath;
        }

        public static void moveFile(String filePath, File file) {
            moveFile(filePath, file.getAbsolutePath());
        }

        public static boolean exists(String file) {
            return new File(file).exists();
        }

        public static String tempCopy(String file) {
            String tempPath = AppConfig.getTempDirPath() + "/" + getFileName(file);
            copy(file, tempPath);

            return tempPath;
        }

        private static String getFileName(String file) {
            return Paths.get(file).getFileName().toString();
        }

        private static void copy(String file, String tempDirPath) {
            try {
                Files.copy(Paths.get(file), Paths.get(tempDirPath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static class DownloadFileFailException extends RuntimeException {

        }

        private static String getFileExt(File file) {
            Tika tika = new Tika();
            String mimeType = "";

            try {
                mimeType = tika.detect(file);
            } catch (IOException e) {
                return null;
            }

            String ext = mimeType.replace("image/", "");
            ext = ext.replace("jpeg", "jpg");

            return ext.toLowerCase();
        }

        public static String getFileExt(String fileName) {
            int pos = fileName.lastIndexOf(".");

            if (pos == -1) {
                return "";
            }

            return fileName.substring(pos + 1).trim();
        }

        public static String getFileNameFromUrl(String fileUrl) {
            try {
                return Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
            } catch (URISyntaxException e) {
                return "";
            }
        }

        public static String downloadFileByHttp(String fileUrl, String outputDir) {
            String originFileName = getFileNameFromUrl(fileUrl);
            String fileExt = getFileExt(originFileName);

            if (fileExt.isEmpty()) {
                fileExt = "tmp";
            }

            String tempFileName = UUID.randomUUID() + ORIGIN_FILE_NAME_SEPARATOR + originFileName + "." + fileExt;
            String filePath = outputDir + "/" + tempFileName;

            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URI(fileUrl).toURL().openStream());
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            } catch (Exception e) {
                throw new DownloadFileFailException();
            }

            File file = new File(filePath);

            if (file.length() == 0) {
                throw new DownloadFileFailException();
            }

            if (fileExt.equals("tmp")) {
                String ext = getFileExt(file);

                if (ext == null || ext.isEmpty()) {
                    throw new DownloadFileFailException();
                }

                String newFilePath = filePath.replace(".tmp", "." + ext);
                moveFile(filePath, newFilePath);
                filePath = newFilePath;
            }

            return filePath;
        }

        public static void moveFile(String filePath, String destFilePath) {
            Path file = Paths.get(filePath);
            Path destFile = Paths.get(destFilePath);

            try {
                Files.move(file, destFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {

            }
        }

        public static String getExt(String filename) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase())
                    .orElse("");
        }

        public static String getFileExtTypeCodeFromFileExt(String ext) {
            return switch (ext) {
                case "jpeg", "jpg", "gif", "png" -> "img";
                case "mp4", "avi", "mov" -> "video";
                case "mp3" -> "audio";
                default -> "etc";
            };

        }

        public static String getFileExtType2CodeFromFileExt(String ext) {

            return switch (ext) {
                case "jpeg", "jpg" -> "jpg";
                case "gif", "png", "mp4", "mov", "avi", "mp3" -> ext;
                default -> "etc";
            };

        }

        public static void remove(String filePath) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static class str {
        public static boolean hasLength(String string) {
            return string != null && !string.trim().isEmpty();
        }

        public static boolean isBlank(String string) {
            return !hasLength(string);
        }

        public static String tempPassword(int len) {
            String passwordSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
            StringBuilder password = new StringBuilder();

            for (int x = 0; x < len; x++) {
                int random = (int) (Math.random() * passwordSet.length());
                password.append(passwordSet.charAt(random));
            }

            return password.toString();
        }

        public static String randomNumStr(int i) {
            String passwordSet = "0123456789";
            StringBuilder password = new StringBuilder();

            for (int x = 0; x < i; x++) {
                int random = (int) (Math.random() * passwordSet.length());
                password.append(passwordSet.charAt(random));
            }

            return password.toString();
        }

        public static String replace(String input, String regex, Function<String, String> replacer) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);

            StringBuilder result = new StringBuilder();

            while (matcher.find()) {
                String replacement = replacer.apply(matcher.group(1));
                matcher.appendReplacement(result, replacement);
            }
            matcher.appendTail(result);

            return result.toString();
        }

        public static String ucfirst(String str) {
            if (str == null || str.isEmpty()) return str;

            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
    }
}