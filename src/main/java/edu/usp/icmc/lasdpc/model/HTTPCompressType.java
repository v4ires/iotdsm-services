package edu.usp.icmc.lasdpc.model;

public enum HTTPCompressType {

    gzip("gzip"),
    compress("compress"),
    deflate("deflate"),
    indentity("identity"),
    br("br");

    private String contentEncoding;

    HTTPCompressType(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }
}
