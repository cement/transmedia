package cn.hnen.transmedia.entry;


import org.springframework.lang.Nullable;

/**
 * @author YSH
 * @created 20190105
 * @desc 定义http 状态码
 */
public enum HttpStatusEnum {
    CONTINUE(100, "Continue","继续"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols","切换协议"),
    PROCESSING(102, "Processing","处理"),
    CHECKPOINT(103, "Checkpoint","检查点"),
    OK(200, "OK","请求成功"),
    CREATED(201, "Created","已创建"),
    ACCEPTED(202, "Accepted","已接受"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information","非授权信息"),
    NO_CONTENT(204, "No Content","无内容"),
    RESET_CONTENT(205, "Reset Content","重置内容"),
    PARTIAL_CONTENT(206, "Partial Content","部分内容"),
    MULTI_STATUS(207, "Multi-Status",""),
    ALREADY_REPORTED(208, "Already Reported","已报告"),
    IM_USED(226, "IM Used","IM Used"),
    MULTIPLE_CHOICES(300, "Multiple Choices","多种选择"),
    MOVED_PERMANENTLY(301, "Moved Permanently","永久移动"),
    FOUND(302, "Found","临时移动"),


    SEE_OTHER(303, "See Other","查看其它地址"),
    NOT_MODIFIED(304, "Not Modified","未修改"),
    /** @deprecated */
    @Deprecated
    USE_PROXY(305, "Use Proxy","使用代理"),
    UNUSED(306,"Unused","废弃"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect","临时重定向"),
    PERMANENT_REDIRECT(308, "Permanent Redirect","永久重定向"),
    BAD_REQUEST(400, "Bad Request","客户端请求服务器无法理解"),
    UNAUTHORIZED(401, "Unauthorized","未授权"),
    PAYMENT_REQUIRED(402, "Payment Required","保留"),
    FORBIDDEN(403, "Forbidden","拒绝执行"),
    NOT_FOUND(404, "Not Found","资源无法找到"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed","请求中方法禁止"),
    NOT_ACCEPTABLE(406, "Not Acceptable","无法完成请求"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required","需要代理身份认证"),
    REQUEST_TIMEOUT(408, "Request Timeout","请求时间超时"),
    CONFLICT(409, "Conflict","处理请求发生冲突"),
    GONE(410, "Gone","资源永久删除"),
    LENGTH_REQUIRED(411, "Length Required","无法处理没有Content-Length的请求"),
    PRECONDITION_FAILED(412, "Precondition Failed","先决条件错误"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large","请求的载荷过大"),
    /** @deprecated */
    @Deprecated
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large","请求的实体过大"),
    URI_TOO_LONG(414, "URI Too Long","URI过长"),
    /** @deprecated */
    @Deprecated
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long","URI过长"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type","无法处理的媒体格式"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable","请求范围无效"),
    EXPECTATION_FAILED(417, "Expectation Failed","无法满足Expect的请求头信息"),
    I_AM_A_TEAPOT(418, "I'm a teapot","不能冲泡咖啡,因为这是茶壶"),
    /** @deprecated */
    @Deprecated
    INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space On Resource","资源空间不足"),
    /** @deprecated */
    @Deprecated
    METHOD_FAILURE(420, "Method Failure","Method Failure"),
    /** @deprecated */
    @Deprecated
    DESTINATION_LOCKED(421, "Destination Locked","目标已锁定"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity","无法处理的实体"),
    LOCKED(423, "Locked","已锁定"),
    FAILED_DEPENDENCY(424, "Failed Dependency","失败的依赖项"),
    UPGRADE_REQUIRED(426, "Upgrade Required","需要升级"),
    PRECONDITION_REQUIRED(428, "Precondition Required","要求先决条件"),
    TOO_MANY_REQUESTS(429, "Too Many Requests","请求太多"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large","请求头字段太大"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons","因法律原因不可用"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error","服务器内部错误"),
    NOT_IMPLEMENTED(501, "Not Implemented","不支持的请求"),
    BAD_GATEWAY(502, "Bad Gateway","网关或者代理收到了一个无效的响应"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable","暂时的无法处理请求"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout","网关或代理未及时收到信息"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported","不支持的HTTP协议版本"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates","变体也可以协商"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage","存储不足"),
    LOOP_DETECTED(508, "Loop Detected","回路检测"),
    BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded","超出带宽限制"),
    NOT_EXTENDED(510, "Not Extended","未扩展"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required","需要网络身份验证");

    private final int value;
    private final String reasonPhrase;



    private final String chinesePhrase;

    private HttpStatusEnum(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
        this.chinesePhrase=reasonPhrase;
    }
    private HttpStatusEnum(int value, String reasonPhrase,String chinesePhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
        this.chinesePhrase=chinesePhrase;
    }
    public int getValue() {
        return value;
    }

    public String getChinesePhrase() {
        return chinesePhrase;
    }
    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public HttpStatusEnum.Series series() {
        return HttpStatusEnum.Series.valueOf(this);
    }

    public boolean is1xxInformational() {
        return this.series() == HttpStatusEnum.Series.INFORMATIONAL;
    }

    public boolean is2xxSuccessful() {
        return this.series() == HttpStatusEnum.Series.SUCCESSFUL;
    }

    public boolean is3xxRedirection() {
        return this.series() ==HttpStatusEnum.Series.REDIRECTION;
    }

    public boolean is4xxClientError() {
        return this.series() == HttpStatusEnum.Series.CLIENT_ERROR;
    }

    public boolean is5xxServerError() {
        return this.series() == HttpStatusEnum.Series.SERVER_ERROR;
    }

    public boolean isError() {
        return this.is4xxClientError() || this.is5xxServerError();
    }

    public String toString() {
        return this.value + " " + this.name();
    }

    public static HttpStatusEnum valueOf(int statusCode) {
        HttpStatusEnum status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        } else {
            return status;
        }
    }

    @Nullable
    public static HttpStatusEnum resolve(int statusCode) {
        HttpStatusEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            HttpStatusEnum status = var1[var3];
            if (status.value == statusCode) {
                return status;
            }
        }

        return null;
    }

    public static enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        private Series(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static HttpStatusEnum.Series valueOf(HttpStatusEnum status) {
            return valueOf(status.value);
        }

        public static HttpStatusEnum.Series valueOf(int statusCode) {
            HttpStatusEnum.Series series = resolve(statusCode);
            if (series == null) {
                throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
            } else {
                return series;
            }
        }

        @Nullable
        public static HttpStatusEnum.Series resolve(int statusCode) {
            int seriesCode = statusCode / 100;
            HttpStatusEnum.Series[] var2 = values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                HttpStatusEnum.Series series = var2[var4];
                if (series.value == seriesCode) {
                    return series;
                }
            }

            return null;
        }
    }
}

