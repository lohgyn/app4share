package com.sunlifemalaysia.app4share.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

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

import com.dd.plist.NSArray;
import com.dd.plist.NSData;
import com.dd.plist.NSDate;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNull;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.sunlifemalaysia.app4share.exception.InvalidIPAFileException;
import com.sunlifemalaysia.app4share.exception.NotFoundException;
import com.sunlifemalaysia.app4share.model.IpaAppInfo;
import com.sunlifemalaysia.app4share.model.IpaFile;
import com.sunlifemalaysia.app4share.model.IpaProvisionInfo;
import com.sunlifemalaysia.app4share.model.PlistInfo;
import com.sunlifemalaysia.app4share.repository.IpaFileRepository;
import com.sunlifemalaysia.app4share.service.FileService;
import com.sunlifemalaysia.app4share.service.IpaService;
import com.sunlifemalaysia.app4share.util.ipng.IPngConverter;

@Service
public class IpaServiceImpl implements IpaService, FileService {

    private static final Logger logger = LoggerFactory.getLogger(IpaServiceImpl.class);
    private static final String TEMP_IPA_FILE = "binary.ipa";
    private static final String INFO_PLIST_FILE = "info.plist";

    private static final String ERROR_FORMAT = "{} {} {}";

    private final URI appUri;
    private final String uploadPath;
    private final IpaFileRepository ipaFileRepository;

    public IpaServiceImpl(@Value("${app.uri}") final URI appUri,
            @Value("${app.ipa.upload-path}") final String uploadPath,
            IpaFileRepository ipaFileRepository) {
        this.appUri = appUri;
        this.uploadPath = uploadPath;
        this.ipaFileRepository = ipaFileRepository;

    }

    @Override
    public IpaFile findByFileUuid(final String fileUuid) {
        final IpaFile ipaFile = ipaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new NotFoundException("IPA File not found"));
        ipaFile.setDownloadUri(getItmsServiceUri(ipaFile));

        return ipaFile;
    }

    @Override
    public List<IpaFile> findAll() {

        List<IpaFile> ipaFiles = ipaFileRepository.findAll();

        ipaFiles.stream().forEach(ipaFile -> ipaFile.setDownloadUri(getItmsServiceUri(ipaFile)));

        return ipaFiles;
    }

    private String getItmsServiceUri(final IpaFile ipaFile) {

        return ITMS_SERVICE_URI_TEMPLATE.replace("${url}", buildPlistUriString(ipaFile));

    }

    @Override
    public void deleteIpaFile(final String fileUuid) {

        IpaFile ipaFile = this.findByFileUuid(fileUuid);

        File baseDir = getIpaFilePath(ipaFile).toFile().getParentFile();

        deleteAllFiles(baseDir.toPath());

        ipaFileRepository.deleteById(ipaFile.getId());

    }

    @Override
    public void uploadIpaFile(final MultipartFile multipartFile) throws InvalidIPAFileException {

        File tempFile = transferMultipartFileTo(multipartFile,
                getFilePath(UUID.randomUUID().toString(), TEMP_IPA_FILE));

        if (tempFile == null) {
            throw new InvalidIPAFileException(String.format("Unable to upload %s.", multipartFile.getName()));
        }

        IpaFile ipaFile = getIpaInfo(tempFile);
        ipaFileRepository.save(ipaFile);

        moveIpaFile(tempFile, ipaFile);
        createPlistFile(ipaFile);
        deleteFile(tempFile.toPath());

    }

    private void moveIpaFile(final File tempFile, final IpaFile ipaFile) {
        final File binaryFile = moveFileTo(tempFile.toPath(), getIpaFilePath(ipaFile));

        if (binaryFile == null) {
            throw new InvalidIPAFileException("Unable to move .ipa file: " + tempFile.getName());
        }
    }

    private void createPlistFile(final IpaFile ipaFile) {

        final File plistFile = writeToFile(generatePlistContent(ipaFile), getPlistFilePath(ipaFile));

        if (plistFile == null) {
            throw new InvalidIPAFileException("Unable to create .plist file for " + ipaFile.getBundleExecutable());
        }

    }

    @Override
    public void uploadIpaReleaseNotes(final MultipartFile multipartFile, final String fileUuid) {

        IpaFile ipaFile = this.findByFileUuid(fileUuid);

        try {
            String base64ReleaseNotes = Base64.getEncoder().encodeToString(multipartFile.getBytes());
            ipaFile.setBase64ReleaseNotes(base64ReleaseNotes);
            ipaFileRepository.save(ipaFile);

        } catch (IOException ex) {
            logError(ex);
        }

    }

    @Override
    public byte[] getIpaIcon(final String fileUuid) {

        IpaFile ipaFile = this.findByFileUuid(fileUuid);

        if (ipaFile.getBase64Icon() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return Base64.getDecoder().decode(ipaFile.getBase64Icon());

    }

    @Override
    public Path getFilePath(final String fileUuid, final String fileName) {
        return Paths.get(uploadPath, fileUuid, fileName);
    }

    private Path getIpaFilePath(final IpaFile ipaFile) {
        return getFilePath(ipaFile.getFileUuid(), ipaFile.getAppName() + ".ipa");
    }

    private Path getPlistFilePath(final IpaFile ipaFile) {
        return getFilePath(ipaFile.getFileUuid(), INFO_PLIST_FILE);
    }

    private IpaFile getIpaInfo(final File tempFile) {

        final IpaFile ipaFile = new IpaFile();

        ipaFile.setFileUuid(tempFile.getParentFile().getName());
        ipaFile.setFileSize(tempFile.length());

        int readCount = 0;

        try (ZipFile zipFile = new ZipFile(tempFile)) {

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements() && readCount < 2) {
                ZipEntry entry = entries.nextElement();

                if (StringUtils.endsWith(entry.getName(), ".app/Info.plist")) {

                    NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(zipFile.getInputStream(entry));

                    ipaFile.setAppName(rootDict.get("CFBundleName").toString());
                    ipaFile.setAppDisplayName(rootDict.get("CFBundleDisplayName").toString());
                    ipaFile.setAppVersion(rootDict.get("CFBundleShortVersionString").toString());
                    ipaFile.setAppBuild(rootDict.get("CFBundleVersion").toString());
                    ipaFile.setBundleId(rootDict.get("CFBundleIdentifier").toString());
                    ipaFile.setBundleExecutable(rootDict.get("CFBundleExecutable").toString());
                    ipaFile.setMinimumVersionRequired(rootDict.get("MinimumOSVersion").toString());
                    ipaFile.setDeviceFamily(getDeviceFamily((NSArray) rootDict.get("UIDeviceFamily")));
                    ipaFile.setSupportedPlatforms(
                            nsArrayToString((NSArray) rootDict.get("CFBundleSupportedPlatforms")));
                    ipaFile.setCapabilities(nsArrayToString((NSArray) rootDict.get("UIRequiredDeviceCapabilities")));
                    ipaFile.setIconName(getLastIpaBundleIconFileName(rootDict));

                    setPlistInfoList(ipaFile, rootDict, IpaAppInfo.class);

                    readCount++;
                }

                if (StringUtils.endsWith(entry.getName(), ".app/embedded.mobileprovision")) {

                    final String plistContent = getPlistFromMobileProvisionFile(zipFile.getInputStream(entry));

                    if (plistContent.isEmpty()) {
                        logger.error("unable to read embedded.mobileprovision file.");
                        break;
                    }

                    NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(plistContent.getBytes());

                    ipaFile.setProvisionAppIdName(rootDict.get("AppIDName").toString());
                    ipaFile.setProvisionCreationDate(((NSDate) rootDict.get("CreationDate")).getDate());
                    ipaFile.setProvisionPlatform(((NSArray) rootDict.get("Platform")).toASCIIPropertyList());
                    ipaFile.setProvisionExpirationDate(((NSDate) rootDict.get("ExpirationDate")).getDate());
                    ipaFile.setProvisionName(rootDict.get("Name").toString());
                    ipaFile.setProvisionTeamName(rootDict.get("TeamName").toString());
                    ipaFile.setProvisionUuid(rootDict.get("UUID").toString());
                    setPlistInfoList(ipaFile, rootDict, IpaProvisionInfo.class);

                    readCount++;

                }
            }

        } catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException
                | SAXException ex) {

            logError(ex);

        }

        if (readCount >= 2) {
            setIpaIcon(ipaFile, tempFile);
            return ipaFile;
        }

        throw new InvalidIPAFileException("Invalid .ipa file.");
    }

    private String getLastIpaBundleIconFileName(final NSDictionary rootDict) {

        NSDictionary iconDict = (NSDictionary) rootDict.get("CFBundleIcons");

        if (iconDict == null) {
            return null;
        }

        NSDictionary primaryIconDict = (NSDictionary) iconDict.get("CFBundlePrimaryIcon");

        if (primaryIconDict == null) {
            return null;
        }

        NSArray iconFiles = (NSArray) primaryIconDict.get("CFBundleIconFiles");

        if (iconFiles.getArray().length < 1) {
            return null;
        }

        return iconFiles.getArray()[iconFiles.getArray().length - 1].toString();

    }

    private void setIpaIcon(final IpaFile ipaFile, final File file) {
        if (ipaFile.getIconName() == null) {
            logger.error("unable to find icon.");
            return;
        }

        try (ZipFile zipFile = new ZipFile(file)) {

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (!entry.isDirectory() && entry.getName().contains(ipaFile.getIconName())) {

                    final byte[] pngBytes = new IPngConverter(zipFile.getInputStream(entry).readAllBytes())
                            .convertPngFile();

                    ipaFile.setBase64Icon(Base64.getEncoder().encodeToString(pngBytes));
                    return;
                }
            }

        } catch (IOException ex) {
            logError(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends PlistInfo> void setPlistInfoList(final IpaFile ipaFile, final NSDictionary dictionary,
            final Class<T> clz) {

        List<T> list = new ArrayList<>(dictionary.count());

        dictionary.forEach((key, nsObject) -> readNSObjectIntoList(list, key, nsObject, clz));

        list.forEach(info -> info.setIpaFile(ipaFile));

        if (clz.equals(IpaAppInfo.class)) {
            ipaFile.setIpaAppInfos((List<IpaAppInfo>) list);
        } else if (clz.equals(IpaProvisionInfo.class)) {
            ipaFile.setIpaProvisionInfos((List<IpaProvisionInfo>) list);
        }

    }

    private <T extends PlistInfo> List<T> readNSDictionaryIntoList(final String rootKey, final NSDictionary dictionary,
            final Class<T> clz) {

        List<T> list = new ArrayList<>(dictionary.count());

        dictionary.forEach((key, nsObject) -> readNSObjectIntoList(list, rootKey + "_" + key, nsObject, clz));

        return list;
    }

    private <T extends PlistInfo> List<T> readNSArrayIntoList(final String key, final NSArray nsArray,
            final Class<T> clz) {

        final NSObject[] nsObjects = nsArray.getArray();

        List<T> list = new ArrayList<>(nsObjects.length);

        for (int i = 0; i < nsObjects.length; i++) {
            readNSObjectIntoList(list, key + "_" + i, nsObjects[i], clz);
        }

        return list;

    }

    private <T extends PlistInfo> void readNSObjectIntoList(final List<T> list, final String key,
            final NSObject nsObject, final Class<T> clz) {

        final String value;
        final String valueType;
        if (nsObject instanceof NSString nsString) {
            value = nsString.getContent();
            valueType = "String";

        } else if (nsObject instanceof NSArray nsArray) {
            list.addAll(readNSArrayIntoList(key, nsArray, clz));
            return;
        } else if (nsObject instanceof NSDictionary nsDictionary) {
            list.addAll(readNSDictionaryIntoList(key, nsDictionary, clz));
            return;
        } else if (nsObject instanceof NSData nsData) {
            value = nsData.getBase64EncodedData();
            valueType = "Data";
        } else if (nsObject instanceof NSDate nsDate) {
            value = nsDate.toString();
            valueType = "Date";
        } else if (nsObject instanceof NSNumber nsNumber) {
            value = nsNumber.stringValue();

            if (nsNumber.isBoolean()) {
                valueType = "Boolean";
            } else if (nsNumber.isInteger()) {
                valueType = "Integer";
            } else {
                valueType = "Number";
            }

        } else if (nsObject instanceof NSNull) {
            value = "N/A";
            valueType = "Null";

        } else {
            return;
        }

        try {
            T info = clz.getDeclaredConstructor().newInstance();
            info.setKey(key);
            info.setValue(value);
            info.setValueType(valueType);
            list.add(info);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException ex) {

            logError(ex);
        }

    }

    private String getPlistFromMobileProvisionFile(final InputStream stream) throws IOException {
        String s = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

        int i = s.indexOf("<plist version=\"1.0\">");

        if (i >= 0) {
            s = s.substring(i);

            i = s.indexOf("</plist>");

            if (i >= 0) {
                return s.substring(0, i + "</plist>".length());
            }
        }

        return "";
    }

    private String getDeviceFamily(final NSArray uiDeviceFamily) {

        return nsArrayToList(uiDeviceFamily)
                .stream()
                .map(obj -> {

                    switch (obj.toString()) {
                        case "1":
                            return "iPhone";
                        case "2":
                            return "iPad";
                        default:
                            return obj.toString();
                    }

                })
                .reduce((family1, family2) -> family1 + ", " + family2)
                .orElse("");

    }

    private String nsArrayToString(final NSArray nsArray) {

        return nsArrayToList(nsArray)
                .stream()
                .map(Object::toString)
                .reduce((str1, str2) -> str1 + ", " + str2)
                .orElse("");

    }

    private List<NSObject> nsArrayToList(final NSArray nsArray) {

        if (nsArray == null || nsArray.count() == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(nsArray.getArray());

    }

    private byte[] generatePlistContent(final IpaFile ipaFile) {

        return PLIST_TEMPLATE
                .replace("${ipa-url}", buildIpaUriString(ipaFile))
                .replace("${bundle-identifier}", ipaFile.getBundleId())
                .replace("${bundle-version}", ipaFile.getAppVersion())
                .replace("${bundle-name}", ipaFile.getAppName()).getBytes();
    }

    private String buildIpaUriString(final IpaFile ipaFile) {
        return UriComponentsBuilder.fromUri(appUri)
                .pathSegment("ipa", ipaFile.getFileUuid(), ipaFile.getBundleExecutable() + ".ipa").toUriString();
    }

    private String buildPlistUriString(final IpaFile ipaFile) {
        return UriComponentsBuilder.fromUri(appUri)
                .pathSegment("ipa", ipaFile.getFileUuid(), INFO_PLIST_FILE).toUriString();
    }

    private void logError(final Exception ex) {
        logger.error(ERROR_FORMAT, ex.getStackTrace()[0], ex.getClass(), ex.getMessage());
    }

    private static final String PLIST_TEMPLATE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
            <plist version="1.0">
            <dict>
                <key>items</key>
                <array>
                    <dict>
                        <key>assets</key>
                        <array>
                            <dict>
                                <key>kind</key>
                                <string>software-package</string>
                                <key>url</key>
                                <string>${ipa-url}</string>
                            </dict>
                        </array>
                        <key>metadata</key>
                        <dict>
                            <key>bundle-identifier</key>
                            <string>${bundle-identifier}</string>
                            <key>bundle-version</key>
                            <string>${bundle-version}</string>
                            <key>kind</key>
                            <string>software</string>
                            <key>title</key>
                            <string>${bundle-name}</string>
                        </dict>
                    </dict>
                </array>
            </dict>
            </plist>
            """;

    private static final String ITMS_SERVICE_URI_TEMPLATE = """
            itms-services://?action=download-manifest&url=${url}
            """;
}
