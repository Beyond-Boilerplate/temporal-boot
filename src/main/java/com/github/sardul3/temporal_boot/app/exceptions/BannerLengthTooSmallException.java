package com.github.sardul3.temporal_boot.app.exceptions;

public class BannerLengthTooSmallException extends RuntimeException {

    public BannerLengthTooSmallException() {
        super("Banner needs to be of appropriate length");
    }
    
}
