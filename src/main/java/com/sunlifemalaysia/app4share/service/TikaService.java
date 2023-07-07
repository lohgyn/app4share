package com.sunlifemalaysia.app4share.service;

import java.io.InputStream;

import org.apache.tika.mime.MediaType;

public interface TikaService {

    public MediaType detectMediaType(InputStream is);
}
