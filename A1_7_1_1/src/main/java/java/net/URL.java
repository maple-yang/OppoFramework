package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Proxy.Type;
import java.util.Hashtable;
import java.util.StringTokenizer;
import sun.net.ApplicationProxy;
import sun.security.util.SecurityConstants;

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
public final class URL implements Serializable {
    static URLStreamHandlerFactory factory = null;
    static Hashtable handlers = null;
    private static final String protocolPathProp = "java.protocol.handler.pkgs";
    static final long serialVersionUID = -7627629688361524110L;
    private static Object streamHandlerLock;
    private String authority;
    private String file;
    transient URLStreamHandler handler;
    private transient int hashCode;
    private String host;
    transient InetAddress hostAddress;
    private transient String path;
    private int port;
    private String protocol;
    private transient String query;
    private String ref;
    private transient String userInfo;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: bogus opcode: 0073 in method: java.net.URL.<clinit>():void, dex: 
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
        // Can't load method instructions: Load method exception: bogus opcode: 0073 in method: java.net.URL.<clinit>():void, dex: 
        */
        throw new UnsupportedOperationException("Method not decompiled: java.net.URL.<clinit>():void");
    }

    public URL(String protocol, String host, int port, String file) throws MalformedURLException {
        this(protocol, host, port, file, null);
    }

    public URL(String protocol, String host, String file) throws MalformedURLException {
        this(protocol, host, -1, file);
    }

    public URL(String protocol, String host, int port, String file, URLStreamHandler handler) throws MalformedURLException {
        this.port = -1;
        this.hashCode = -1;
        if (handler != null) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                checkSpecifyHandler(sm);
            }
        }
        protocol = protocol.toLowerCase();
        this.protocol = protocol;
        if (host != null) {
            if (host.indexOf(58) >= 0 && !host.startsWith("[")) {
                host = "[" + host + "]";
            }
            this.host = host;
            if (port < -1) {
                throw new MalformedURLException("Invalid port number :" + port);
            }
            this.port = port;
            this.authority = port == -1 ? host : host + ":" + port;
        }
        Parts parts = new Parts(file, host);
        this.path = parts.getPath();
        this.query = parts.getQuery();
        if (this.query != null) {
            this.file = this.path + "?" + this.query;
        } else {
            this.file = this.path;
        }
        this.ref = parts.getRef();
        if (handler == null) {
            handler = getURLStreamHandler(protocol);
            if (handler == null) {
                throw new MalformedURLException("unknown protocol: " + protocol);
            }
        }
        this.handler = handler;
    }

    public URL(String spec) throws MalformedURLException {
        this(null, spec);
    }

    public URL(URL context, String spec) throws MalformedURLException {
        this(context, spec, null);
    }

    /* JADX WARNING: Missing block: B:39:0x00ac, code:
            if (r18.equalsIgnoreCase(r23.protocol) != false) goto L_0x00ae;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public URL(URL context, String spec, URLStreamHandler handler) throws MalformedURLException {
        this.port = -1;
        this.hashCode = -1;
        String original = spec;
        int start = 0;
        String newProtocol = null;
        boolean aRef = false;
        boolean isRelative = false;
        if (handler != null) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                checkSpecifyHandler(sm);
            }
        }
        try {
            int limit = spec.length();
            while (limit > 0) {
                if (spec.charAt(limit - 1) > ' ') {
                    break;
                }
                limit--;
            }
            while (start < limit && spec.charAt(start) <= ' ') {
                start++;
            }
            if (spec.regionMatches(true, start, "url:", 0, 4)) {
                start += 4;
            }
            if (start < spec.length() && spec.charAt(start) == '#') {
                aRef = true;
            }
            int i = start;
            while (!aRef && i < limit) {
                int c = spec.charAt(i);
                if (c == 47) {
                    break;
                } else if (c == 58) {
                    String s = spec.substring(start, i).toLowerCase();
                    if (isValidProtocol(s)) {
                        newProtocol = s;
                        start = i + 1;
                    }
                } else {
                    i++;
                }
            }
            this.protocol = newProtocol;
            if (context != null) {
                if (newProtocol != null) {
                }
                if (handler == null) {
                    handler = context.handler;
                }
                if (context.path != null && context.path.startsWith("/")) {
                    newProtocol = null;
                }
                if (newProtocol == null) {
                    this.protocol = context.protocol;
                    this.authority = context.authority;
                    this.userInfo = context.userInfo;
                    this.host = context.host;
                    this.port = context.port;
                    this.file = context.file;
                    this.path = context.path;
                    isRelative = true;
                }
            }
            if (this.protocol == null) {
                throw new MalformedURLException("no protocol: " + spec);
            }
            if (handler == null) {
                handler = getURLStreamHandler(this.protocol);
                if (handler == null) {
                    throw new MalformedURLException("unknown protocol: " + this.protocol);
                }
            }
            this.handler = handler;
            i = spec.indexOf(35, start);
            if (i >= 0) {
                this.ref = spec.substring(i + 1, limit);
                limit = i;
            }
            if (isRelative && start == limit) {
                this.query = context.query;
                if (this.ref == null) {
                    this.ref = context.ref;
                }
            }
            handler.parseURL(this, spec, start, limit);
        } catch (MalformedURLException e) {
            throw e;
        } catch (Exception e2) {
            MalformedURLException exception = new MalformedURLException(e2.getMessage());
            exception.initCause(e2);
            throw exception;
        }
    }

    private boolean isValidProtocol(String protocol) {
        int len = protocol.length();
        if (len < 1 || !Character.isLetter(protocol.charAt(0))) {
            return false;
        }
        for (int i = 1; i < len; i++) {
            char c = protocol.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '.' && c != '+' && c != '-') {
                return false;
            }
        }
        return true;
    }

    private void checkSpecifyHandler(SecurityManager sm) {
        sm.checkPermission(SecurityConstants.SPECIFY_HANDLER_PERMISSION);
    }

    protected void set(String protocol, String host, int port, String file, String ref) {
        synchronized (this) {
            this.protocol = protocol;
            this.host = host;
            if (port != -1) {
                host = host + ":" + port;
            }
            this.authority = host;
            this.port = port;
            this.file = file;
            this.ref = ref;
            this.hashCode = -1;
            this.hostAddress = null;
            int q = file.lastIndexOf(63);
            if (q != -1) {
                this.query = file.substring(q + 1);
                this.path = file.substring(0, q);
            } else {
                this.path = file;
            }
        }
    }

    protected void set(String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref) {
        synchronized (this) {
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            String str = (query == null || query.isEmpty()) ? path : path + "?" + query;
            this.file = str;
            this.userInfo = userInfo;
            this.path = path;
            this.ref = ref;
            this.hashCode = -1;
            this.hostAddress = null;
            this.query = query;
            this.authority = authority;
        }
    }

    public String getQuery() {
        return this.query;
    }

    public String getPath() {
        return this.path;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    public String getAuthority() {
        return this.authority;
    }

    public int getPort() {
        return this.port;
    }

    public int getDefaultPort() {
        return this.handler.getDefaultPort();
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getHost() {
        return this.host;
    }

    public String getFile() {
        return this.file;
    }

    public String getRef() {
        return this.ref;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof URL)) {
            return false;
        }
        return this.handler.equals(this, (URL) obj);
    }

    public synchronized int hashCode() {
        if (this.hashCode != -1) {
            return this.hashCode;
        }
        this.hashCode = this.handler.hashCode(this);
        return this.hashCode;
    }

    public boolean sameFile(URL other) {
        return this.handler.sameFile(this, other);
    }

    public String toString() {
        return toExternalForm();
    }

    public String toExternalForm() {
        return this.handler.toExternalForm(this);
    }

    public URI toURI() throws URISyntaxException {
        return new URI(toString());
    }

    public URLConnection openConnection() throws IOException {
        return this.handler.openConnection(this);
    }

    public URLConnection openConnection(Proxy proxy) throws IOException {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy can not be null");
        }
        Proxy p = proxy == Proxy.NO_PROXY ? Proxy.NO_PROXY : ApplicationProxy.create(proxy);
        SecurityManager sm = System.getSecurityManager();
        if (!(p.type() == Type.DIRECT || sm == null)) {
            InetSocketAddress epoint = (InetSocketAddress) p.address();
            if (epoint.isUnresolved()) {
                sm.checkConnect(epoint.getHostName(), epoint.getPort());
            } else {
                sm.checkConnect(epoint.getAddress().getHostAddress(), epoint.getPort());
            }
        }
        return this.handler.openConnection(this, p);
    }

    public final InputStream openStream() throws IOException {
        return openConnection().getInputStream();
    }

    public final Object getContent() throws IOException {
        return openConnection().getContent();
    }

    public final Object getContent(Class[] classes) throws IOException {
        return openConnection().getContent(classes);
    }

    public static void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
        synchronized (streamHandlerLock) {
            if (factory != null) {
                throw new Error("factory already defined");
            }
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkSetFactory();
            }
            handlers.clear();
            factory = fac;
        }
    }

    static URLStreamHandler getURLStreamHandler(String protocol) {
        URLStreamHandler handler = (URLStreamHandler) handlers.get(protocol);
        if (handler == null) {
            boolean checkedWithFactory = false;
            if (factory != null) {
                handler = factory.createURLStreamHandler(protocol);
                checkedWithFactory = true;
            }
            if (handler == null) {
                StringTokenizer packagePrefixIter = new StringTokenizer(System.getProperty(protocolPathProp, ""), "|");
                while (handler == null && packagePrefixIter.hasMoreTokens()) {
                    try {
                        String clsName = packagePrefixIter.nextToken().trim() + "." + protocol + ".Handler";
                        Class cls = null;
                        try {
                            cls = Class.forName(clsName, true, ClassLoader.getSystemClassLoader());
                        } catch (ClassNotFoundException e) {
                            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
                            if (contextLoader != null) {
                                cls = Class.forName(clsName, true, contextLoader);
                            }
                        }
                        if (cls != null) {
                            handler = (URLStreamHandler) cls.newInstance();
                        }
                    } catch (ReflectiveOperationException e2) {
                    }
                }
            }
            if (handler == null) {
                try {
                    if (protocol.equals("file")) {
                        handler = (URLStreamHandler) Class.forName("sun.net.www.protocol.file.Handler").newInstance();
                    } else {
                        if (protocol.equals("ftp")) {
                            handler = (URLStreamHandler) Class.forName("sun.net.www.protocol.ftp.Handler").newInstance();
                        } else {
                            if (protocol.equals("jar")) {
                                handler = (URLStreamHandler) Class.forName("sun.net.www.protocol.jar.Handler").newInstance();
                            } else {
                                if (protocol.equals("http")) {
                                    handler = (URLStreamHandler) Class.forName("com.android.okhttp.HttpHandler").newInstance();
                                } else {
                                    if (protocol.equals("https")) {
                                        handler = (URLStreamHandler) Class.forName("com.android.okhttp.HttpsHandler").newInstance();
                                    }
                                }
                            }
                        }
                    }
                } catch (Object e3) {
                    throw new AssertionError(e3);
                }
            }
            synchronized (streamHandlerLock) {
                URLStreamHandler handler2 = (URLStreamHandler) handlers.get(protocol);
                if (handler2 != null) {
                    return handler2;
                }
                if (!checkedWithFactory) {
                    if (factory != null) {
                        handler2 = factory.createURLStreamHandler(protocol);
                    }
                }
                if (handler2 != null) {
                    handler = handler2;
                }
                if (handler != null) {
                    handlers.put(protocol, handler);
                }
            }
        }
        return handler;
    }

    private synchronized void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0076  */
    /* JADX WARNING: Missing block: B:36:0x009a, code:
            if (r6.port == -1) goto L_0x009c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        URLStreamHandler uRLStreamHandler = getURLStreamHandler(this.protocol);
        this.handler = uRLStreamHandler;
        if (uRLStreamHandler == null) {
            throw new IOException("unknown protocol: " + this.protocol);
        }
        if (this.authority == null) {
            String str;
            if (this.host == null || this.host.length() <= 0) {
            }
            if (this.host == null) {
                this.host = "";
            }
            if (this.port == -1) {
                str = this.host;
            } else {
                str = this.host + ":" + this.port;
            }
            this.authority = str;
            int at = this.host.lastIndexOf(64);
            if (at != -1) {
                this.userInfo = this.host.substring(0, at);
                this.host = this.host.substring(at + 1);
            }
            this.path = null;
            this.query = null;
            if (this.file != null) {
                int q = this.file.lastIndexOf(63);
                if (q != -1) {
                    this.query = this.file.substring(q + 1);
                    this.path = this.file.substring(0, q);
                } else {
                    this.path = this.file;
                }
            }
            this.hashCode = -1;
        }
        if (this.authority != null) {
            int ind = this.authority.indexOf(64);
            if (ind != -1) {
                this.userInfo = this.authority.substring(0, ind);
            }
        }
        this.path = null;
        this.query = null;
        if (this.file != null) {
        }
        this.hashCode = -1;
    }
}