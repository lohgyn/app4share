package com.sunlifemalaysia.app4share.controller.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sunlifemalaysia.app4share.exception.InvalidApkFileException;
import com.sunlifemalaysia.app4share.exception.NotFoundException;
import com.sunlifemalaysia.app4share.model.ApkFile;
import com.sunlifemalaysia.app4share.service.ApkService;
import com.sunlifemalaysia.app4share.service.QrCodeService;
import com.sunlifemalaysia.app4share.service.TikaService;

@Controller
@RequestMapping("/apk")
public class ApkController {

    private final ApkService apkService;
    private final QrCodeService qrCodeService;
    private final TikaService tikaService;

    public ApkController(ApkService apkService, QrCodeService qrCodeService, TikaService tikaService) {
        this.apkService = apkService;
        this.qrCodeService = qrCodeService;
        this.tikaService = tikaService;
    }

    @GetMapping
    public String viewApkFiles(Model model) {

        List<ApkFile> apkFiles = apkService.findAll();

        model.addAttribute("apkFiles", apkFiles);

        return "apk-files";
    }

    @GetMapping("{fileUuid}")
    public String viewApkFile(Model model, @PathVariable String fileUuid) {

        model.addAttribute("apkFile",
                apkService.findByFileUuid(fileUuid));

        return "view-apk-file";
    }

    @GetMapping("{fileUuid}/icon")
    public ResponseEntity<byte[]> getIpaIcon(@PathVariable String fileUuid) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(httpHeaders).body(apkService.getApkIcon(fileUuid));

    }

    @GetMapping("{fileUuid}/{fileName:.+}")
    public ResponseEntity<FileSystemResource> downloadApkFile(@PathVariable String fileUuid,
            @PathVariable String fileName)
            throws IOException {

        Path filePath = apkService.getFilePath(fileUuid, fileName);

        if (!Files.exists(filePath)) {
            throw new NotFoundException("File not found.");
        }

        FileSystemResource resource = new FileSystemResource(filePath.toAbsolutePath());

        String tikaMediaTypeStr = tikaService.detectMediaType(new BufferedInputStream(resource.getInputStream()))
                .toString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(tikaMediaTypeStr));
        httpHeaders.setContentDisposition(ContentDisposition.inline().filename(fileName).build());

        return ResponseEntity.ok().headers(httpHeaders).body(resource);

    }

    @GetMapping("{fileUuid}/qr-code")
    public ResponseEntity<byte[]> generateQRCodeImage(@PathVariable String fileUuid) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);

        final ApkFile apkFile = apkService.findByFileUuid(fileUuid);

        return ResponseEntity.ok().headers(httpHeaders)
                .body(qrCodeService.generateQRCodeImage(apkFile.getDownloadUri()));

    }

    private static final String REDIRECT_TO_APK_PAGE = "redirect:/apk";

    @PostMapping("/release-notes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String uploadIpaReleaseNotes(@RequestParam("file") MultipartFile multipartFile,
            @RequestParam("fileUuid") String fileUuid) {

        apkService.uploadApkReleaseNotes(multipartFile, fileUuid);

        return REDIRECT_TO_APK_PAGE;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String uploadApkFile(@RequestParam("file") MultipartFile multipartFile) {

        apkService.uploadApkFile(multipartFile);

        return REDIRECT_TO_APK_PAGE;
    }

    @ResponseBody
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> deleteApkFile(String fileUuid) {

        apkService.deleteApkFile(fileUuid);

        return ResponseEntity.ok(true);
    }

    @ExceptionHandler({ InvalidApkFileException.class, NotFoundException.class })
    public String handleIOException(Exception ex, RedirectAttributes redirectAttributes) {

        String message = ex instanceof InvalidApkFileException invalidApk ? invalidApk.getReason() : ex.getMessage();

        redirectAttributes.addFlashAttribute("error", message);
        return REDIRECT_TO_APK_PAGE;
    }

}
