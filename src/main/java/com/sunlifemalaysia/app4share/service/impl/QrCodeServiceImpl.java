package com.sunlifemalaysia.app4share.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sunlifemalaysia.app4share.exception.BadRequestException;
import com.sunlifemalaysia.app4share.service.QrCodeService;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeServiceImpl.class);
    private static final String ERROR_FORMAT = "{} {}";

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 200;

    private static final MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(-82416, -16762554);

    @Override
    public byte[] generateQRCodeImage(String barcodeText, int width, int height) {

        if (width < 1) {
            width = DEFAULT_WIDTH;
        }

        if (height < 1) {
            height = DEFAULT_HEIGHT;
        }

        try {

            QRCodeWriter barcodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig), "png", baos);

            return baos.toByteArray();

        } catch (IllegalArgumentException | IOException | WriterException ex) {
            logger.error(ERROR_FORMAT, ex.getClass(), ex.getMessage());

            throw new BadRequestException("Unable to generate QR Code");
        }

    }

    @Override
    public byte[] generateQRCodeImage(String barcodeText) {

        return generateQRCodeImage(barcodeText, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
