package com.sunlifemalaysia.app4share.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

import com.sunlifemalaysia.app4share.exception.InvalidApkFileException;
import com.sunlifemalaysia.app4share.exception.NotFoundException;
import com.sunlifemalaysia.app4share.handler.xml.AndroidManifestXMLHandler;
import com.sunlifemalaysia.app4share.model.ApkFile;
import com.sunlifemalaysia.app4share.model.xml.androidmanifest.AndroidManifest;
import com.sunlifemalaysia.app4share.model.xml.androidmanifest.UsesPermission;
import com.sunlifemalaysia.app4share.repository.ApkFileRepository;
import com.sunlifemalaysia.app4share.service.ApkService;
import com.sunlifemalaysia.app4share.service.FileService;

import brut.androlib.Config;
import brut.androlib.exceptions.AndrolibException;
import brut.androlib.res.ResourcesDecoder;
import brut.androlib.res.data.ResPackage;
import brut.androlib.res.data.ResResource;
import brut.androlib.res.data.ResTable;
import brut.androlib.res.data.ResValuesFile;
import brut.androlib.res.data.value.ResStringValue;
import brut.androlib.res.decoder.AXmlResourceParser;
import brut.androlib.res.decoder.Res9patchStreamDecoder;
import brut.androlib.res.decoder.ResAttrDecoder;
import brut.androlib.res.decoder.ResFileDecoder;
import brut.androlib.res.decoder.ResRawStreamDecoder;
import brut.androlib.res.decoder.ResStreamDecoderContainer;
import brut.androlib.res.decoder.XmlPullStreamDecoder;
import brut.androlib.res.util.ExtMXSerializer;
import brut.androlib.res.util.ExtXmlSerializer;
import brut.directory.Directory;
import brut.directory.DirectoryException;
import brut.directory.ExtFile;
import brut.directory.FileDirectory;
import brut.util.Duo;

@Service
public class ApkServiceImpl implements ApkService, FileService {

    private static final Logger logger = LoggerFactory.getLogger(ApkServiceImpl.class);

    private static final String TEMP_APK_FILE = "binary.apk";
    private static final String ANDROID_MANIFEST_FILE = "AndroidManifest.xml";
    private static final String ERROR_FORMAT = "{} {} {}";

    private final URI appUri;
    private final String uploadPath;
    private final ApkFileRepository apkFileRepository;

    public ApkServiceImpl(@Value("${app.uri}") final URI appUri,
            @Value("${app.apk.upload-path}") final String uploadPath,
            ApkFileRepository apkFileRepository) {
        this.appUri = appUri;
        this.uploadPath = uploadPath;
        this.apkFileRepository = apkFileRepository;

    }

    @Override
    public ApkFile findByFileUuid(final String fileUuid) {
        final ApkFile apkFile = apkFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new NotFoundException("APK File not found"));

        apkFile.setDownloadUri(getApkUriString(apkFile));
        return apkFile;
    }

    @Override
    public List<ApkFile> findAll() {
        List<ApkFile> apkFiles = apkFileRepository.findAll();

        apkFiles.stream().forEach(apkFile -> apkFile.setDownloadUri(getApkUriString(apkFile)));

        return apkFiles;
    }

    private String getApkUriString(final ApkFile apkFile) {
        return UriComponentsBuilder.fromUri(appUri)
                .pathSegment("apk", apkFile.getFileUuid(), apkFile.getAppName() + ".apk").toUriString();
    }

    @Override
    public void deleteApkFile(final String fileUuid) {

        ApkFile apkFile = this.findByFileUuid(fileUuid);

        File baseDir = getApkFilePath(apkFile).toFile().getParentFile();

        deleteAllFiles(baseDir.toPath());

        apkFileRepository.deleteById(apkFile.getId());

    }

    @Override
    public ApkFile uploadApkFile(final MultipartFile multipartFile) throws InvalidApkFileException {

        String apkFolder = UUID.randomUUID().toString();

        File tempApkFile = transferMultipartFileTo(multipartFile, getFilePath(apkFolder, TEMP_APK_FILE));

        if (tempApkFile == null) {
            throw new InvalidApkFileException(String.format("Unable to upload %s.", multipartFile.getName()));
        }

        ApkFile apkFile = decodeApkFile(tempApkFile);

        apkFile = apkFileRepository.save(apkFile);

        moveApkFile(tempApkFile, apkFile);

        return apkFile;

    }

    private ApkFile decodeApkFile(final File tempApkFile) {

        final ExtFile extFile = new ExtFile(tempApkFile);

        try {

            Config config = Config.getDefaultConfig();
            config.setForceDecodeManifest(Config.FORCE_DECODE_MANIFEST_FULL);
            config.analysisMode = true;

            final ResourcesDecoder androlibResources = new ResourcesDecoder(config, extFile);
            final File outDir = new File(extFile.getParentFile().getPath());
            final ResTable resTable = androlibResources.getResTable();

            androlibResources.decodeManifest(outDir);

            final File manifestXml = new File(outDir, ANDROID_MANIFEST_FILE);

            final ApkFile apkFile = getApkFileInfoByAndroidManifestFileAndResourceTable(manifestXml, resTable);
            apkFile.setFileUuid(tempApkFile.getParentFile().getName());
            apkFile.setFileSize(tempApkFile.length());
            apkFile.setBase64Icon(
                    getBase64ApkIcon(androlibResources, resTable, extFile, outDir, apkFile.getIconName()));

            return apkFile;

        } catch (AndrolibException ex) {
            logError(ex);

        } finally {

            try {
                extFile.close();
            } catch (IOException ex) {
                logError(ex);
            }
        }

        throw new InvalidApkFileException("Unable to decode .apk file.");
    }

    private ApkFile getApkFileInfoByAndroidManifestFileAndResourceTable(final File androidManifestFile,
            final ResTable resTable) throws AndrolibException {
        final ApkFile apkFile = new ApkFile();

        try {
            SAXParserFactory factory = SAXParserFactory.newDefaultInstance();
            SAXParser saxParser = factory.newSAXParser();
            AndroidManifestXMLHandler androidManifestXMLHandler = new AndroidManifestXMLHandler();
            saxParser.parse(androidManifestFile, androidManifestXMLHandler);

            AndroidManifest androidManifest = androidManifestXMLHandler.getAndroidManifest();

            apkFile.setAppPackage(androidManifest.getManifest().getAttrPackage());
            apkFile.setCompileSdkVersion(androidManifest.getManifest().getAttrAndroidCompileSdkVersion());
            apkFile.setCompileSdkVersionCodename(
                    androidManifest.getManifest().getAttrAndroidCompileSdkVersionCodeName());
            apkFile.setPlatformBuildVersionCode(androidManifest.getManifest().getAttrPlatformBuildVersionCode());
            apkFile.setPlatformBuildVersionName(androidManifest.getManifest().getAttrPlatformBuildVersionName());
            apkFile.setVersionCode(androidManifest.getManifest().getAttrAndroidVersionCode());
            apkFile.setVersionName(androidManifest.getManifest().getAttrAndroidVersionName());

            apkFile.setPermissions(androidManifest.getManifest().getUsesPermissions().stream()
                    .map(UsesPermission::getAttrAndroidName).collect(Collectors.joining(", ")));

            apkFile.setIconName(
                    StringUtils.substringAfterLast(androidManifest.getManifest().getApplication().getAttrAndroidIcon(),
                            "@"));

            apkFile.setAppName(getApkAppNameByPackageAndAndroidLabelAndResourceTable(
                    androidManifest.getManifest().getApplication().getAttrAndroidLabel(), resTable));

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logError(ex);
            throw new InvalidApkFileException("Unable to decode .apk file.");
        }

        return apkFile;

    }

    private String getApkAppNameByPackageAndAndroidLabelAndResourceTable(final String androidLabel,
            final ResTable resTable)
            throws AndrolibException {

        logger.info("Android Label: {}", androidLabel);

        String valueName = StringUtils.substringAfter(androidLabel, "@string/");

        final Iterator<ResValuesFile> valuesFileIterator = resTable.getCurrentResPackage().listValuesFiles().iterator();

        while (valuesFileIterator.hasNext()) {
            ResValuesFile valuesFile = valuesFileIterator.next();

            if (valuesFile.getPath().equalsIgnoreCase("values/strings.xml")) {
                final Iterator<ResResource> resourceIterator = valuesFile.listResources().iterator();

                while (resourceIterator.hasNext()) {
                    ResResource resource = resourceIterator.next();
                    if (resource.getResSpec().getName().contains(valueName)
                            && resource.getResSpec().getType().isString()) {

                        return ((ResStringValue) resource.getValue()).encodeAsResXmlValue();
                    }
                }
                break;
            }
        }

        return "";
    }

    private String getBase64ApkIcon(final ResourcesDecoder androlibResources, final ResTable resTable,
            final ExtFile apkFile, final File outDir,
            final String iconName) {

        try {
            final String iconsDirName = "icons";

            final String[] iconTokens = iconName.split("/");

            if (iconTokens.length < 2) {
                logger.error("Unable to tokenize icon name.");
                return null;
            }

            Duo<ResFileDecoder, AXmlResourceParser> duo = getResFileDecoder();

            ResFileDecoder fileDecoder = duo.m1;

            Directory in = apkFile.getDirectory();
            Directory out = new FileDirectory(outDir);
            out = out.createDir(iconsDirName);
            Iterator<ResPackage> iteratorPackage = resTable.listMainPackages().iterator();

            while (iteratorPackage.hasNext()) {
                ResPackage resPackage = iteratorPackage.next();

                Iterator<ResResource> iteratorResource = resPackage.listFiles().iterator();

                while (iteratorResource.hasNext()) {
                    ResResource resource = iteratorResource.next();
                    String resourceFilePath = resource.getFilePath();

                    if (!resourceFilePath.contains("anydpi") && resourceFilePath.startsWith(iconTokens[0])
                            && resourceFilePath.endsWith(iconTokens[1])) {
                        logger.info("Extracted icon: {}", resource.getFilePath());
                        fileDecoder.decode(resource, in, out, androlibResources.getResFileMapping());
                    }
                }
            }

            // get largest dim png
            File iconsDir = new File(outDir, iconsDirName);
            File[] icons = iconsDir.listFiles();
            return Base64.getEncoder()
                    .encodeToString(Files.readAllBytes(icons[icons.length - 1].listFiles()[0].toPath()));

        } catch (AndrolibException | DirectoryException | IOException ex) {
            logError(ex);
        }

        return null;
    }

    private Duo<ResFileDecoder, AXmlResourceParser> getResFileDecoder() {
        ResStreamDecoderContainer decoders = new ResStreamDecoderContainer();
        decoders.setDecoder("raw", new ResRawStreamDecoder());
        decoders.setDecoder("9patch", new Res9patchStreamDecoder());

        AXmlResourceParser axmlParser = new AXmlResourceParser();
        axmlParser.setAttrDecoder(new ResAttrDecoder());
        decoders.setDecoder("xml", new XmlPullStreamDecoder(axmlParser, getResXmlSerializer()));

        return new Duo<>(new ResFileDecoder(decoders), axmlParser);
    }

    private ExtMXSerializer getResXmlSerializer() {
        ExtMXSerializer serial = new ExtMXSerializer();
        serial.setProperty(ExtXmlSerializer.PROPERTY_SERIALIZER_INDENTATION, "    ");
        serial.setProperty(ExtXmlSerializer.PROPERTY_SERIALIZER_LINE_SEPARATOR, System.getProperty("line.separator"));
        serial.setProperty(ExtXmlSerializer.PROPERTY_DEFAULT_ENCODING, "utf-8");
        serial.setDisabledAttrEscape(true);
        return serial;
    }

    private void moveApkFile(final File tempFile, final ApkFile apkFile) {

        final File movedFile = moveFileTo(tempFile.toPath(), getApkFilePath(apkFile));

        if (movedFile == null) {
            throw new InvalidApkFileException("Unable to move .apk file: " + tempFile.getName());
        }

    }

    @Override
    public void housekeepOldApkFile(final String appPackage) {
        final List<ApkFile> apkFiles = apkFileRepository.findAllByAppPackageOrderByIdDesc(appPackage);

        if (apkFiles.size() > 5) {
            apkFiles.subList(5, apkFiles.size())
                    .forEach(apkFile -> deleteApkFile(apkFile.getFileUuid()));
        }
    }

    @Override
    public void uploadApkReleaseNotes(final MultipartFile multipartFile, final String fileUuid) {

        ApkFile apkFile = this.findByFileUuid(fileUuid);

        try {
            String base64ReleaseNotes = Base64.getEncoder().encodeToString(multipartFile.getBytes());

            apkFile.setBase64ReleaseNotes(base64ReleaseNotes);

            apkFileRepository.save(apkFile);

        } catch (IOException ex) {
            logError(ex);
        }

    }

    @Override
    public byte[] getApkIcon(final String fileUuid) {
        ApkFile apkFile = this.findByFileUuid(fileUuid);

        if (apkFile.getBase64Icon() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return Base64.getDecoder().decode(apkFile.getBase64Icon());
    }

    @Override
    public Path getFilePath(String uuid, String fileName) {
        return Paths.get(uploadPath, uuid, fileName);
    }

    private Path getApkFilePath(ApkFile apkFile) {
        return getFilePath(apkFile.getFileUuid(), apkFile.getAppName() + ".apk");
    }

    private void logError(Exception ex) {
        logger.error(ERROR_FORMAT, ex.getStackTrace()[0], ex.getClass(), ex.getMessage());
    }

}
