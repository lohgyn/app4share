package com.sunlifemalaysia.app4share.handler.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sunlifemalaysia.app4share.model.xml.androidmanifest.AndroidManifest;
import com.sunlifemalaysia.app4share.model.xml.androidmanifest.Application;
import com.sunlifemalaysia.app4share.model.xml.androidmanifest.Manifest;
import com.sunlifemalaysia.app4share.model.xml.androidmanifest.UsesPermission;

public class AndroidManifestXMLHandler extends DefaultHandler {

    private static final String MANIFEST = "manifest";
    private static final String APPLICATION = "application";
    private static final String USES_PERMISSION = "uses-permission";

    // manifest attributes
    private static final String ATTR_ANDROID_VERSION_CODE = "android:versionCode";
    private static final String ATTR_ANDROID_VERSION_NAME = "android:versionName";
    private static final String ATTR_ANDROID_COMPILE_SDK_VERSION = "android:compileSdkVersion";
    private static final String ATTR_ANDROID_COMPILE_SDK_VERSION_CODENAME = "android:compileSdkVersionCodename";
    private static final String ATTR_PACKAGE = "package";
    private static final String ATTR_PLATFORM_BUILD_VERSION_CODE = "platformBuildVersionCode";
    private static final String ATTR_PLATFORM_BUILD_VERSION_NAME = "platformBuildVersionName";

    // application attributes
    private static final String ATTR_ANDROID_THEME = "android:theme";
    private static final String ATTR_ANDROID_LABEL = "android:label";
    private static final String ATTR_ANDROID_ICON = "android:icon";
    private static final String ATTR_ANDROID_NAME = "android:name";

    private AndroidManifest androidManifest;

    @Override
    public void startDocument() throws SAXException {
        androidManifest = new AndroidManifest();
        Manifest manifest = new Manifest();
        manifest.setApplication(new Application());
        manifest.setUsesPermissions(new ArrayList<>());
        androidManifest.setManifest(manifest);

    }

    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attributes)
            throws SAXException {
        switch (qName) {
            case MANIFEST:
                Manifest manifest = androidManifest.getManifest();
                manifest.setAttrAndroidVersionCode(attributes.getValue(ATTR_ANDROID_VERSION_CODE));
                manifest.setAttrAndroidVersionName(attributes.getValue(ATTR_ANDROID_VERSION_NAME));
                manifest.setAttrAndroidCompileSdkVersion(attributes.getValue(ATTR_ANDROID_COMPILE_SDK_VERSION));
                manifest.setAttrAndroidCompileSdkVersionCodeName(
                        attributes.getValue(ATTR_ANDROID_COMPILE_SDK_VERSION_CODENAME));
                manifest.setAttrPackage(attributes.getValue(ATTR_PACKAGE));
                manifest.setAttrPlatformBuildVersionCode(attributes.getValue(ATTR_PLATFORM_BUILD_VERSION_CODE));
                manifest.setAttrPlatformBuildVersionName(attributes.getValue(ATTR_PLATFORM_BUILD_VERSION_NAME));

                break;
            case APPLICATION:
                Application application = androidManifest.getManifest().getApplication();

                application.setAttrAndroidTheme(attributes.getValue(ATTR_ANDROID_THEME));
                application.setAttrAndroidLabel(attributes.getValue(ATTR_ANDROID_LABEL));
                application.setAttrAndroidIcon(attributes.getValue(ATTR_ANDROID_ICON));
                application.setAttrAndroidName(attributes.getValue(ATTR_ANDROID_NAME));
                break;

            case USES_PERMISSION:
                UsesPermission usesPermission = new UsesPermission();
                usesPermission.setAttrAndroidName(attributes.getValue(ATTR_ANDROID_NAME));
                androidManifest.getManifest().getUsesPermissions().add(usesPermission);
                break;
            default:
                break;
        }

    }

    public AndroidManifest getAndroidManifest() {
        return androidManifest;
    }
}
