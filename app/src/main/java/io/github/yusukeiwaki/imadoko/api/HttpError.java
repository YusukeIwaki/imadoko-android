package io.github.yusukeiwaki.imadoko.api;

public class HttpError extends Exception {
    public final int code;
    public final String body;

    public HttpError(int code, String body) {
        super();
        this.code = code;
        this.body = body;
    }
}
