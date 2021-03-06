package java.lang;

import sun.misc.DoubleConsts;
import sun.misc.FpUtils;
import sun.util.locale.LanguageTag;

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
    	at jadx.core.dex.visitors.ExtractFieldInit.checkStaticFieldsInit(ExtractFieldInit.java:58)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:44)
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
public final class Double extends Number implements Comparable<Double> {
    public static final int BYTES = 8;
    public static final int MAX_EXPONENT = 1023;
    public static final double MAX_VALUE = Double.MAX_VALUE;
    public static final int MIN_EXPONENT = -1022;
    public static final double MIN_NORMAL = Double.MIN_NORMAL;
    public static final double MIN_VALUE = Double.MIN_VALUE;
    public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
    public static final double NaN = Double.NaN;
    public static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
    public static final int SIZE = 64;
    public static final Class<Double> TYPE = null;
    private static final long serialVersionUID = -9172774392245257468L;
    private final double value;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: bogus opcode: 00e9 in method: java.lang.Double.<clinit>():void, dex: 
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
        Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
        	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1227)
        	at com.android.dx.io.OpcodeInfo.getName(OpcodeInfo.java:1234)
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:581)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:74)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 9 more
        */
    static {
        /*
        // Can't load method instructions: Load method exception: bogus opcode: 00e9 in method: java.lang.Double.<clinit>():void, dex: 
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Double.<clinit>():void");
    }

    public static native long doubleToRawLongBits(double d);

    public static native double longBitsToDouble(long j);

    public static String toString(double d) {
        return FloatingDecimal.getThreadLocalInstance().loadDouble(d).toJavaFormatString();
    }

    public static String toHexString(double d) {
        if (!FpUtils.isFinite(d)) {
            return toString(d);
        }
        StringBuffer answer = new StringBuffer(24);
        if (FpUtils.rawCopySign(1.0d, d) == -1.0d) {
            answer.append(LanguageTag.SEP);
        }
        answer.append("0x");
        d = Math.abs(d);
        if (d == 0.0d) {
            answer.append("0.0p0");
        } else {
            String str;
            int i;
            boolean subnormal = d < Double.MIN_NORMAL;
            long signifBits = (doubleToLongBits(d) & DoubleConsts.SIGNIF_BIT_MASK) | 1152921504606846976L;
            answer.append(subnormal ? "0." : "1.");
            String signif = Long.toHexString(signifBits).substring(3, 16);
            if (signif.equals("0000000000000")) {
                str = "0";
            } else {
                str = signif.replaceFirst("0{1,12}$", "");
            }
            answer.append(str);
            StringBuilder append = new StringBuilder().append("p");
            if (subnormal) {
                i = -1022;
            } else {
                i = FpUtils.getExponent(d);
            }
            answer.append(append.append(i).toString());
        }
        return answer.toString();
    }

    public static Double valueOf(String s) throws NumberFormatException {
        return new Double(FloatingDecimal.getThreadLocalInstance().readJavaFormatString(s).doubleValue());
    }

    public static Double valueOf(double d) {
        return new Double(d);
    }

    public static double parseDouble(String s) throws NumberFormatException {
        return FloatingDecimal.getThreadLocalInstance().readJavaFormatString(s).doubleValue();
    }

    public static boolean isNaN(double v) {
        return v != v;
    }

    public static boolean isInfinite(double v) {
        return v == Double.POSITIVE_INFINITY || v == Double.NEGATIVE_INFINITY;
    }

    public static boolean isFinite(double d) {
        return Math.abs(d) <= Double.MAX_VALUE;
    }

    public Double(double value) {
        this.value = value;
    }

    public Double(String s) throws NumberFormatException {
        this(valueOf(s).doubleValue());
    }

    public boolean isNaN() {
        return isNaN(this.value);
    }

    public boolean isInfinite() {
        return isInfinite(this.value);
    }

    public String toString() {
        return toString(this.value);
    }

    public byte byteValue() {
        return (byte) ((int) this.value);
    }

    public short shortValue() {
        return (short) ((int) this.value);
    }

    public int intValue() {
        return (int) this.value;
    }

    public long longValue() {
        return (long) this.value;
    }

    public float floatValue() {
        return (float) this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public int hashCode() {
        return hashCode(this.value);
    }

    public static int hashCode(double value) {
        long bits = doubleToLongBits(value);
        return (int) ((bits >>> 32) ^ bits);
    }

    public boolean equals(Object obj) {
        if ((obj instanceof Double) && doubleToLongBits(((Double) obj).value) == doubleToLongBits(this.value)) {
            return true;
        }
        return false;
    }

    public static long doubleToLongBits(double value) {
        long result = doubleToRawLongBits(value);
        if ((result & DoubleConsts.EXP_BIT_MASK) != DoubleConsts.EXP_BIT_MASK || (DoubleConsts.SIGNIF_BIT_MASK & result) == 0) {
            return result;
        }
        return 9221120237041090560L;
    }

    public int compareTo(Double anotherDouble) {
        return compare(this.value, anotherDouble.value);
    }

    public static int compare(double d1, double d2) {
        int i = -1;
        if (d1 < d2) {
            return -1;
        }
        if (d1 > d2) {
            return 1;
        }
        long thisBits = doubleToLongBits(d1);
        long anotherBits = doubleToLongBits(d2);
        if (thisBits == anotherBits) {
            i = 0;
        } else if (thisBits >= anotherBits) {
            i = 1;
        }
        return i;
    }

    public static double sum(double a, double b) {
        return a + b;
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static double min(double a, double b) {
        return Math.min(a, b);
    }
}
