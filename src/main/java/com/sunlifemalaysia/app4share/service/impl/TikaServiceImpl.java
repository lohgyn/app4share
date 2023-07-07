package com.sunlifemalaysia.app4share.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sunlifemalaysia.app4share.exception.BadRequestException;
import com.sunlifemalaysia.app4share.service.TikaService;

@Service
public class TikaServiceImpl implements TikaService {

    private static final Logger logger = LoggerFactory.getLogger(TikaServiceImpl.class);
    private static final String ERROR_FORMAT = "{} {}";

    @Override
    public MediaType detectMediaType(InputStream is) {
        Detector detector = new DefaultDetector();
        Metadata metadata = new Metadata();

        try {
            return detector.detect(is, metadata);
        } catch (IOException ex) {
            logger.error(ERROR_FORMAT, ex.getClass(), ex.getMessage());

            throw new BadRequestException("Unable to detect media type");
        }

    }
}
