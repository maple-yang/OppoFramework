package com.android.internal.telephony;

import android.app.AppGlobals;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.telephony.PhoneNumberUtils;
import android.telephony.Rlog;
import android.util.AtomicFile;
import android.util.Xml;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/*  JADX ERROR: NullPointerException in pass: ReSugarCode
    java.lang.NullPointerException
    	at jadx.core.dex.visitors.ReSugarCode.initClsEnumMap(ReSugarCode.java:159)
    	at jadx.core.dex.visitors.ReSugarCode.visit(ReSugarCode.java:44)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:12)
    	at jadx.core.ProcessClass.process(ProcessClass.java:32)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
    	at java.lang.Iterable.forEach(Iterable.java:75)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
    	at jadx.core.ProcessClass.process(ProcessClass.java:37)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
    */
/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    	at jadx.core.utils.BlockUtils.isAllBlocksEmpty(BlockUtils.java:546)
    	at jadx.core.dex.visitors.ExtractFieldInit.getConstructorsList(ExtractFieldInit.java:221)
    	at jadx.core.dex.visitors.ExtractFieldInit.moveCommonFieldsInit(ExtractFieldInit.java:121)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:46)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:42)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:12)
    	at jadx.core.ProcessClass.process(ProcessClass.java:32)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
    	at java.lang.Iterable.forEach(Iterable.java:75)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
    	at jadx.core.ProcessClass.process(ProcessClass.java:37)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
    */
/*  JADX ERROR: NullPointerException in pass: ClassModifier
    java.lang.NullPointerException
    	at jadx.core.dex.visitors.ClassModifier.removeFieldUsageFromConstructor(ClassModifier.java:100)
    	at jadx.core.dex.visitors.ClassModifier.removeSyntheticFields(ClassModifier.java:75)
    	at jadx.core.dex.visitors.ClassModifier.visit(ClassModifier.java:48)
    	at jadx.core.dex.visitors.ClassModifier.visit(ClassModifier.java:40)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:12)
    	at jadx.core.ProcessClass.process(ProcessClass.java:32)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
    	at java.lang.Iterable.forEach(Iterable.java:75)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
    	at jadx.core.ProcessClass.process(ProcessClass.java:37)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
    */
public class SmsUsageMonitor {
    private static final String ATTR_COUNTRY = "country";
    private static final String ATTR_FREE = "free";
    private static final String ATTR_PACKAGE_NAME = "name";
    private static final String ATTR_PACKAGE_SMS_POLICY = "sms-policy";
    private static final String ATTR_PATTERN = "pattern";
    private static final String ATTR_PREMIUM = "premium";
    private static final String ATTR_STANDARD = "standard";
    static final int CATEGORY_FREE_SHORT_CODE = 1;
    static final int CATEGORY_NOT_SHORT_CODE = 0;
    static final int CATEGORY_POSSIBLE_PREMIUM_SHORT_CODE = 3;
    static final int CATEGORY_PREMIUM_SHORT_CODE = 4;
    static final int CATEGORY_STANDARD_SHORT_CODE = 2;
    private static final boolean DBG = true;
    private static final int DEFAULT_SMS_CHECK_PERIOD = 60000;
    private static final int DEFAULT_SMS_MAX_COUNT = 30;
    public static final int PREMIUM_SMS_PERMISSION_ALWAYS_ALLOW = 3;
    public static final int PREMIUM_SMS_PERMISSION_ASK_USER = 1;
    public static final int PREMIUM_SMS_PERMISSION_NEVER_ALLOW = 2;
    public static final int PREMIUM_SMS_PERMISSION_UNKNOWN = 0;
    private static final String SHORT_CODE_PATH = "/data/misc/sms/codes";
    private static final String[] SKIP_SEND_LIMIT_PACKAGES = null;
    private static final String SMS_POLICY_FILE_DIRECTORY = "/data/misc/sms";
    private static final String SMS_POLICY_FILE_NAME = "premium_sms_policy.xml";
    private static final String TAG = "SmsUsageMonitor";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_SHORTCODE = "shortcode";
    private static final String TAG_SHORTCODES = "shortcodes";
    private static final String TAG_SMS_POLICY_BODY = "premium-sms-policy";
    private static final boolean VDBG = false;
    private final AtomicBoolean mCheckEnabled;
    private final int mCheckPeriod;
    private final Context mContext;
    private String mCurrentCountry;
    private ShortCodePatternMatcher mCurrentPatternMatcher;
    private final int mMaxAllowed;
    private final File mPatternFile;
    private long mPatternFileLastModified;
    private AtomicFile mPolicyFile;
    private final HashMap<String, Integer> mPremiumSmsPolicy;
    private final SettingsObserverHandler mSettingsObserverHandler;
    private final HashMap<String, ArrayList<Long>> mSmsStamp;

    /* renamed from: com.android.internal.telephony.SmsUsageMonitor$1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ SmsUsageMonitor this$0;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: bogus opcode: 00e8 in method: com.android.internal.telephony.SmsUsageMonitor.1.<init>(com.android.internal.telephony.SmsUsageMonitor):void, dex: 
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:254)
            	at jadx.core.ProcessClass.process(ProcessClass.java:29)
            	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
            	at java.lang.Iterable.forEach(Iterable.java:75)
            	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
            	at jadx.core.ProcessClass.process(ProcessClass.java:37)
            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
            Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
            	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1227)
            	at com.android.dx.io.OpcodeInfo.getName(OpcodeInfo.java:1234)
            	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:581)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:74)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
            	... 10 more
            */
        AnonymousClass1(com.android.internal.telephony.SmsUsageMonitor r1) {
            /*
            // Can't load method instructions: Load method exception: bogus opcode: 00e8 in method: com.android.internal.telephony.SmsUsageMonitor.1.<init>(com.android.internal.telephony.SmsUsageMonitor):void, dex: 
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.SmsUsageMonitor.1.<init>(com.android.internal.telephony.SmsUsageMonitor):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: bogus opcode: 00e5 in method: com.android.internal.telephony.SmsUsageMonitor.1.run():void, dex: 
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:254)
            	at jadx.core.ProcessClass.process(ProcessClass.java:29)
            	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
            	at java.lang.Iterable.forEach(Iterable.java:75)
            	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
            	at jadx.core.ProcessClass.process(ProcessClass.java:37)
            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
            Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
            	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1227)
            	at com.android.dx.io.OpcodeInfo.getName(OpcodeInfo.java:1234)
            	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:581)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:74)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
            	... 10 more
            */
        public void run() {
            /*
            // Can't load method instructions: Load method exception: bogus opcode: 00e5 in method: com.android.internal.telephony.SmsUsageMonitor.1.run():void, dex: 
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.SmsUsageMonitor.1.run():void");
        }
    }

    private static class SettingsObserver extends ContentObserver {
        private final Context mContext;
        private final AtomicBoolean mEnabled;

        SettingsObserver(Handler handler, Context context, AtomicBoolean enabled) {
            super(handler);
            this.mContext = context;
            this.mEnabled = enabled;
            onChange(false);
        }

        public void onChange(boolean selfChange) {
            boolean z = true;
            AtomicBoolean atomicBoolean = this.mEnabled;
            if (Global.getInt(this.mContext.getContentResolver(), "sms_short_code_confirmation", 1) == 0) {
                z = false;
            }
            atomicBoolean.set(z);
        }
    }

    private static class SettingsObserverHandler extends Handler {
        SettingsObserverHandler(Context context, AtomicBoolean enabled) {
            context.getContentResolver().registerContentObserver(Global.getUriFor("sms_short_code_confirmation"), false, new SettingsObserver(this, context, enabled));
        }
    }

    private static final class ShortCodePatternMatcher {
        private final Pattern mFreeShortCodePattern;
        private final Pattern mPremiumShortCodePattern;
        private final Pattern mShortCodePattern;
        private final Pattern mStandardShortCodePattern;

        ShortCodePatternMatcher(String shortCodeRegex, String premiumShortCodeRegex, String freeShortCodeRegex, String standardShortCodeRegex) {
            Pattern compile;
            Pattern pattern = null;
            if (shortCodeRegex != null) {
                compile = Pattern.compile(shortCodeRegex);
            } else {
                compile = null;
            }
            this.mShortCodePattern = compile;
            if (premiumShortCodeRegex != null) {
                compile = Pattern.compile(premiumShortCodeRegex);
            } else {
                compile = null;
            }
            this.mPremiumShortCodePattern = compile;
            if (freeShortCodeRegex != null) {
                compile = Pattern.compile(freeShortCodeRegex);
            } else {
                compile = null;
            }
            this.mFreeShortCodePattern = compile;
            if (standardShortCodeRegex != null) {
                pattern = Pattern.compile(standardShortCodeRegex);
            }
            this.mStandardShortCodePattern = pattern;
        }

        int getNumberCategory(String phoneNumber) {
            if (this.mFreeShortCodePattern != null && this.mFreeShortCodePattern.matcher(phoneNumber).matches()) {
                return 1;
            }
            if (this.mStandardShortCodePattern != null && this.mStandardShortCodePattern.matcher(phoneNumber).matches()) {
                return 2;
            }
            if (this.mPremiumShortCodePattern != null && this.mPremiumShortCodePattern.matcher(phoneNumber).matches()) {
                return 4;
            }
            if (this.mShortCodePattern == null || !this.mShortCodePattern.matcher(phoneNumber).matches()) {
                return 0;
            }
            return 3;
        }
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: bogus opcode: 0073 in method: com.android.internal.telephony.SmsUsageMonitor.<clinit>():void, dex: 
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
        	at jadx.core.ProcessClass.process(ProcessClass.java:29)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
        	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1227)
        	at com.android.dx.io.OpcodeInfo.getName(OpcodeInfo.java:1234)
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:581)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:74)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 9 more
        */
    static {
        /*
        // Can't load method instructions: Load method exception: bogus opcode: 0073 in method: com.android.internal.telephony.SmsUsageMonitor.<clinit>():void, dex: 
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.SmsUsageMonitor.<clinit>():void");
    }

    public static int mergeShortCodeCategories(int type1, int type2) {
        if (type1 > type2) {
            return type1;
        }
        return type2;
    }

    public SmsUsageMonitor(Context context) {
        this.mSmsStamp = new HashMap();
        this.mCheckEnabled = new AtomicBoolean(true);
        this.mPatternFile = new File(SHORT_CODE_PATH);
        this.mPatternFileLastModified = 0;
        this.mPremiumSmsPolicy = new HashMap();
        this.mContext = context;
        ContentResolver resolver = context.getContentResolver();
        this.mMaxAllowed = Global.getInt(resolver, "sms_outgoing_check_max_count", 30);
        this.mCheckPeriod = Global.getInt(resolver, "sms_outgoing_check_interval_ms", 60000);
        this.mSettingsObserverHandler = new SettingsObserverHandler(this.mContext, this.mCheckEnabled);
        loadPremiumSmsPolicyDb();
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0064 A:{SYNTHETIC, Splitter: B:31:0x0064} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0053 A:{SYNTHETIC, Splitter: B:25:0x0053} */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0039 A:{SYNTHETIC, Splitter: B:16:0x0039} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ShortCodePatternMatcher getPatternMatcherFromFile(String country) {
        XmlPullParserException e;
        Throwable th;
        FileReader patternReader = null;
        try {
            FileReader patternReader2 = new FileReader(this.mPatternFile);
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(patternReader2);
                ShortCodePatternMatcher patternMatcherFromXmlParser = getPatternMatcherFromXmlParser(parser, country);
                this.mPatternFileLastModified = this.mPatternFile.lastModified();
                if (patternReader2 != null) {
                    try {
                        patternReader2.close();
                    } catch (IOException e2) {
                    }
                }
                return patternMatcherFromXmlParser;
            } catch (FileNotFoundException e3) {
                patternReader = patternReader2;
                Rlog.e(TAG, "Short Code Pattern File not found");
                this.mPatternFileLastModified = this.mPatternFile.lastModified();
                if (patternReader != null) {
                    try {
                        patternReader.close();
                    } catch (IOException e4) {
                    }
                }
                return null;
            } catch (XmlPullParserException e5) {
                e = e5;
                patternReader = patternReader2;
                try {
                    Rlog.e(TAG, "XML parser exception reading short code pattern file", e);
                    this.mPatternFileLastModified = this.mPatternFile.lastModified();
                    if (patternReader != null) {
                        try {
                            patternReader.close();
                        } catch (IOException e6) {
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    this.mPatternFileLastModified = this.mPatternFile.lastModified();
                    if (patternReader != null) {
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                patternReader = patternReader2;
                this.mPatternFileLastModified = this.mPatternFile.lastModified();
                if (patternReader != null) {
                    try {
                        patternReader.close();
                    } catch (IOException e7) {
                    }
                }
                throw th;
            }
        } catch (FileNotFoundException e8) {
            Rlog.e(TAG, "Short Code Pattern File not found");
            this.mPatternFileLastModified = this.mPatternFile.lastModified();
            if (patternReader != null) {
            }
            return null;
        } catch (XmlPullParserException e9) {
            e = e9;
            Rlog.e(TAG, "XML parser exception reading short code pattern file", e);
            this.mPatternFileLastModified = this.mPatternFile.lastModified();
            if (patternReader != null) {
            }
            return null;
        }
    }

    private ShortCodePatternMatcher getPatternMatcherFromResource(String country) {
        XmlResourceParser xmlResourceParser = null;
        try {
            xmlResourceParser = this.mContext.getResources().getXml(17891346);
            ShortCodePatternMatcher patternMatcherFromXmlParser = getPatternMatcherFromXmlParser(xmlResourceParser, country);
            return patternMatcherFromXmlParser;
        } finally {
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
        }
    }

    private ShortCodePatternMatcher getPatternMatcherFromXmlParser(XmlPullParser parser, String country) {
        try {
            XmlUtils.beginDocument(parser, TAG_SHORTCODES);
            while (true) {
                XmlUtils.nextElement(parser);
                String element = parser.getName();
                if (element == null) {
                    Rlog.e(TAG, "Parsing pattern data found null");
                    break;
                } else if (!element.equals(TAG_SHORTCODE)) {
                    Rlog.e(TAG, "Error: skipping unknown XML tag " + element);
                } else if (country.equals(parser.getAttributeValue(null, "country"))) {
                    return new ShortCodePatternMatcher(parser.getAttributeValue(null, ATTR_PATTERN), parser.getAttributeValue(null, ATTR_PREMIUM), parser.getAttributeValue(null, ATTR_FREE), parser.getAttributeValue(null, ATTR_STANDARD));
                }
            }
        } catch (XmlPullParserException e) {
            Rlog.e(TAG, "XML parser exception reading short code patterns", e);
        } catch (IOException e2) {
            Rlog.e(TAG, "I/O exception reading short code patterns", e2);
        }
        Rlog.d(TAG, "Country (" + country + ") not found");
        return null;
    }

    void dispose() {
        this.mSmsStamp.clear();
    }

    public boolean check(String appName, int smsWaiting) {
        ArrayList<Long> sentList;
        for (String name : SKIP_SEND_LIMIT_PACKAGES) {
            if (appName.equals(name)) {
                return true;
            }
        }
        synchronized (this.mSmsStamp) {
            removeExpiredTimestamps();
            sentList = (ArrayList) this.mSmsStamp.get(appName);
            if (sentList == null) {
                sentList = new ArrayList();
                this.mSmsStamp.put(appName, sentList);
            }
        }
        return isUnderLimit(sentList, smsWaiting);
    }

    public int checkDestination(String destAddress, String countryIso) {
        synchronized (this.mSettingsObserverHandler) {
            if (PhoneNumberUtils.isEmergencyNumber(destAddress, countryIso)) {
                Rlog.d(TAG, "isEmergencyNumber");
                return 0;
            } else if (this.mCheckEnabled.get()) {
                if (countryIso != null) {
                    if (!(this.mCurrentCountry != null && countryIso.equals(this.mCurrentCountry) && this.mPatternFile.lastModified() == this.mPatternFileLastModified)) {
                        if (this.mPatternFile.exists()) {
                            Rlog.d(TAG, "Loading SMS Short Code patterns from file");
                            this.mCurrentPatternMatcher = getPatternMatcherFromFile(countryIso);
                        } else {
                            Rlog.d(TAG, "Loading SMS Short Code patterns from resource");
                            this.mCurrentPatternMatcher = getPatternMatcherFromResource(countryIso);
                        }
                        this.mCurrentCountry = countryIso;
                    }
                }
                if (this.mCurrentPatternMatcher != null) {
                    int numberCategory = this.mCurrentPatternMatcher.getNumberCategory(destAddress);
                    return numberCategory;
                }
                Rlog.e(TAG, "No patterns for \"" + countryIso + "\": using generic short code rule");
                if (destAddress.length() <= 5) {
                    return 3;
                }
                return 0;
            } else {
                Rlog.e(TAG, "check disabled");
                return 0;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0072 A:{PHI: r7 , ExcHandler: java.io.FileNotFoundException (e java.io.FileNotFoundException), Splitter: B:7:0x0024} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0087 A:{PHI: r7 , ExcHandler: java.io.IOException (r2_0 'e' java.io.IOException), Splitter: B:7:0x0024} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00f4 A:{PHI: r7 , ExcHandler: org.xmlpull.v1.XmlPullParserException (r4_0 'e' org.xmlpull.v1.XmlPullParserException), Splitter: B:7:0x0024} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:24:0x0073, code:
            if (r7 != null) goto L_0x0075;
     */
    /* JADX WARNING: Missing block: B:26:?, code:
            r7.close();
     */
    /* JADX WARNING: Missing block: B:32:0x0087, code:
            r2 = move-exception;
     */
    /* JADX WARNING: Missing block: B:34:?, code:
            android.telephony.Rlog.e(TAG, "Unable to read premium SMS policy database", r2);
     */
    /* JADX WARNING: Missing block: B:35:0x0091, code:
            if (r7 != null) goto L_0x0093;
     */
    /* JADX WARNING: Missing block: B:37:?, code:
            r7.close();
     */
    /* JADX WARNING: Missing block: B:44:?, code:
            android.telephony.Rlog.e(TAG, "Error: non-numeric policy type " + r10);
     */
    /* JADX WARNING: Missing block: B:56:0x00f4, code:
            r4 = move-exception;
     */
    /* JADX WARNING: Missing block: B:58:?, code:
            android.telephony.Rlog.e(TAG, "Unable to parse premium SMS policy database", r4);
     */
    /* JADX WARNING: Missing block: B:59:0x00fe, code:
            if (r7 != null) goto L_0x0100;
     */
    /* JADX WARNING: Missing block: B:61:?, code:
            r7.close();
     */
    /* JADX WARNING: Missing block: B:65:0x010c, code:
            if (r7 != null) goto L_0x010e;
     */
    /* JADX WARNING: Missing block: B:67:?, code:
            r7.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadPremiumSmsPolicyDb() {
        synchronized (this.mPremiumSmsPolicy) {
            if (this.mPolicyFile == null) {
                this.mPolicyFile = new AtomicFile(new File(new File(SMS_POLICY_FILE_DIRECTORY), SMS_POLICY_FILE_NAME));
                this.mPremiumSmsPolicy.clear();
                FileInputStream infile = null;
                try {
                    infile = this.mPolicyFile.openRead();
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(infile, StandardCharsets.UTF_8.name());
                    XmlUtils.beginDocument(parser, TAG_SMS_POLICY_BODY);
                    while (true) {
                        XmlUtils.nextElement(parser);
                        String element = parser.getName();
                        if (element == null) {
                            if (infile != null) {
                                try {
                                    infile.close();
                                } catch (IOException e) {
                                }
                            }
                        } else if (element.equals("package")) {
                            String packageName = parser.getAttributeValue(null, "name");
                            String policy = parser.getAttributeValue(null, ATTR_PACKAGE_SMS_POLICY);
                            if (packageName == null) {
                                Rlog.e(TAG, "Error: missing package name attribute");
                            } else if (policy == null) {
                                Rlog.e(TAG, "Error: missing package policy attribute");
                            } else {
                                this.mPremiumSmsPolicy.put(packageName, Integer.valueOf(Integer.parseInt(policy)));
                            }
                        } else {
                            Rlog.e(TAG, "Error: skipping unknown XML tag " + element);
                        }
                    }
                } catch (FileNotFoundException e2) {
                } catch (IOException e3) {
                } catch (NumberFormatException e4) {
                    Rlog.e(TAG, "Unable to parse premium SMS policy database", e4);
                    if (infile != null) {
                        try {
                            infile.close();
                        } catch (IOException e5) {
                        }
                    }
                } catch (XmlPullParserException e6) {
                }
            }
        }
        return;
    }

    private void writePremiumSmsPolicyDb() {
        synchronized (this.mPremiumSmsPolicy) {
            FileOutputStream outfile = null;
            try {
                outfile = this.mPolicyFile.startWrite();
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(outfile, StandardCharsets.UTF_8.name());
                out.startDocument(null, Boolean.valueOf(true));
                out.startTag(null, TAG_SMS_POLICY_BODY);
                for (Entry<String, Integer> policy : this.mPremiumSmsPolicy.entrySet()) {
                    out.startTag(null, "package");
                    out.attribute(null, "name", (String) policy.getKey());
                    out.attribute(null, ATTR_PACKAGE_SMS_POLICY, ((Integer) policy.getValue()).toString());
                    out.endTag(null, "package");
                }
                out.endTag(null, TAG_SMS_POLICY_BODY);
                out.endDocument();
                this.mPolicyFile.finishWrite(outfile);
            } catch (IOException e) {
                Rlog.e(TAG, "Unable to write premium SMS policy database", e);
                if (outfile != null) {
                    this.mPolicyFile.failWrite(outfile);
                }
            }
        }
    }

    public int getPremiumSmsPermission(String packageName) {
        checkCallerIsSystemOrPhoneOrSameApp(packageName);
        synchronized (this.mPremiumSmsPolicy) {
            Integer policy = (Integer) this.mPremiumSmsPolicy.get(packageName);
            if (policy == null) {
                return 0;
            }
            int intValue = policy.intValue();
            return intValue;
        }
    }

    public void setPremiumSmsPermission(String packageName, int permission) {
        checkCallerIsSystemOrPhoneApp();
        if (permission < 1 || permission > 3) {
            throw new IllegalArgumentException("invalid SMS permission type " + permission);
        }
        synchronized (this.mPremiumSmsPolicy) {
            this.mPremiumSmsPolicy.put(packageName, Integer.valueOf(permission));
        }
        new Thread(new AnonymousClass1(this)).start();
    }

    private static void checkCallerIsSystemOrPhoneOrSameApp(String pkg) {
        int uid = Binder.getCallingUid();
        int appId = UserHandle.getAppId(uid);
        if (appId != 1000 && appId != 1001 && uid != 0) {
            try {
                ApplicationInfo ai = AppGlobals.getPackageManager().getApplicationInfo(pkg, 0, UserHandle.getCallingUserId());
                if (!UserHandle.isSameApp(ai.uid, uid)) {
                    throw new SecurityException("Calling uid " + uid + " gave package" + pkg + " which is owned by uid " + ai.uid);
                }
            } catch (RemoteException re) {
                throw new SecurityException("Unknown package " + pkg + "\n" + re);
            }
        }
    }

    private static void checkCallerIsSystemOrPhoneApp() {
        int uid = Binder.getCallingUid();
        int appId = UserHandle.getAppId(uid);
        if (appId != 1000 && appId != 1001 && uid != 0) {
            throw new SecurityException("Disallowed call for uid " + uid);
        }
    }

    private void removeExpiredTimestamps() {
        long beginCheckPeriod = System.currentTimeMillis() - ((long) this.mCheckPeriod);
        synchronized (this.mSmsStamp) {
            Iterator<Entry<String, ArrayList<Long>>> iter = this.mSmsStamp.entrySet().iterator();
            while (iter.hasNext()) {
                ArrayList<Long> oldList = (ArrayList) ((Entry) iter.next()).getValue();
                if (oldList.isEmpty() || ((Long) oldList.get(oldList.size() - 1)).longValue() < beginCheckPeriod) {
                    iter.remove();
                }
            }
        }
    }

    private boolean isUnderLimit(ArrayList<Long> sent, int smsWaiting) {
        Long ct = Long.valueOf(System.currentTimeMillis());
        long beginCheckPeriod = ct.longValue() - ((long) this.mCheckPeriod);
        while (!sent.isEmpty() && ((Long) sent.get(0)).longValue() < beginCheckPeriod) {
            sent.remove(0);
        }
        if (sent.size() + smsWaiting > this.mMaxAllowed) {
            return false;
        }
        for (int i = 0; i < smsWaiting; i++) {
            sent.add(ct);
        }
        return true;
    }

    private static void log(String msg) {
        Rlog.d(TAG, msg);
    }
}
