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

import com.sunlifemalaysia.app4share.exception.InvalidIPAFileException;
import com.sunlifemalaysia.app4share.exception.NotFoundException;
import com.sunlifemalaysia.app4share.model.IpaFile;
import com.sunlifemalaysia.app4share.service.IpaService;
import com.sunlifemalaysia.app4share.service.QrCodeService;
import com.sunlifemalaysia.app4share.service.TikaService;

@Controller
@RequestMapping("/ipa")
public class IpaController {

    private final IpaService ipaService;
    private final QrCodeService qrCodeService;
    private final TikaService tikaService;

    public IpaController(IpaService ipaService, QrCodeService qrCodeService, TikaService tikaService) {
        this.ipaService = ipaService;
        this.qrCodeService = qrCodeService;
        this.tikaService = tikaService;
    }

    @GetMapping
    public String viewIpaFiles(Model model) {

        List<IpaFile> ipaFiles = ipaService.findAll();

        model.addAttribute("ipaFiles", ipaFiles);

        return "ipa-files";
    }

    @GetMapping("{fileUuid}")
    public String viewIpaFile(Model model, @PathVariable String fileUuid) {

        model.addAttribute("ipaFile",
                ipaService.findByFileUuid(fileUuid));

        return "view-ipa-file";
    }

    @GetMapping("{fileUuid}/icon")
    public ResponseEntity<byte[]> getIpaIcon(@PathVariable String fileUuid) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(httpHeaders).body(ipaService.getIpaIcon(fileUuid));

    }

    @GetMapping("{fileUuid}/{fileName:.+}")
    public ResponseEntity<FileSystemResource> downloadIpaFile(@PathVariable String fileUuid,
            @PathVariable String fileName)
            throws IOException {

        Path filePath = ipaService.getFilePath(fileUuid, fileName);

        if (!Files.exists(filePath)) {
            throw new NotFoundException("File not found.");
        }

        FileSystemResource resource = new FileSystemResource(filePath.toAbsolutePath());

        String tikaMediaTypeStr = tikaService.detectMediaType(new BufferedInputStream(resource.getInputStream()))
                .toString();

        MediaType mediaType;

        if (tikaMediaTypeStr.endsWith("plist")) {
            mediaType = MediaType.APPLICATION_XML;
        } else {
            mediaType = MediaType.parseMediaType(tikaMediaTypeStr);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);
        httpHeaders.setContentDisposition(ContentDisposition.inline().filename(fileName).build());

        return ResponseEntity.ok().headers(httpHeaders).body(resource);

    }

    private static final String REDIRECT_TO_IPA_PAGE = "redirect:/ipa";

    @GetMapping("{fileUuid}/qr-code")
    public ResponseEntity<byte[]> generateQRCodeImage(@PathVariable String fileUuid) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);

        final IpaFile ipaFile = ipaService.findByFileUuid(fileUuid);

        return ResponseEntity.ok().headers(httpHeaders)
                .body(qrCodeService.generateQRCodeImage(ipaFile.getDownloadUri()));

    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String uploadIpaFile(@RequestParam("file") MultipartFile multipartFile) {

        ipaService.uploadIpaFile(multipartFile);

        return REDIRECT_TO_IPA_PAGE;
    }

    @PostMapping("/release-notes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String uploadIpaReleaseNotes(@RequestParam("file") MultipartFile multipartFile,
            @RequestParam("fileUuid") String fileUuid) {

        ipaService.uploadIpaReleaseNotes(multipartFile, fileUuid);

        return REDIRECT_TO_IPA_PAGE;
    }

    @ResponseBody
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> deleteIpaFile(String fileUuid) {

        ipaService.deleteIpaFile(fileUuid);

        return ResponseEntity.ok(true);
    }

    @ExceptionHandler({ InvalidIPAFileException.class, NotFoundException.class })
    public String handleIOException(Exception ex, RedirectAttributes redirectAttributes) {

        String message = ex instanceof InvalidIPAFileException invalidIpa ? invalidIpa.getReason() : ex.getMessage();

        redirectAttributes.addFlashAttribute("error", message);

        return REDIRECT_TO_IPA_PAGE;
    }

}
