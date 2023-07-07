package com.sunlifemalaysia.app4share.service;

public interface QrCodeService {

    public byte[] generateQRCodeImage(String barcodeText, int width, int height);

    public byte[] generateQRCodeImage(String barcodeText);
}
