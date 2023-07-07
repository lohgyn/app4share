package com.sunlifemalaysia.app4share.controller.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sunlifemalaysia.app4share.service.QrCodeService;

@Controller
@RequestMapping("/qr-code")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;

    }

    @GetMapping()
    public ResponseEntity<byte[]> generateQRCodeImage(@RequestParam String text,
            @RequestParam(required = false) int width, @RequestParam(required = false) int height) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(httpHeaders)
                .body(qrCodeService.generateQRCodeImage(text, width, height));

    }
}
