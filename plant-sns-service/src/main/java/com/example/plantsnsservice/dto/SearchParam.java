package com.example.plantsnsservice.dto;

import lombok.Getter;

@Getter
public enum SearchParam {
    HASHTAGNAME("hashTagName"),
    SNSPOSTTITLE("snsPostTitle"),

    SNSPOSTCONTENT("snsPostContent"),
    NICKNAME("nickname");



    private final String paramKey;

    SearchParam(String paramKey) {
        this.paramKey = paramKey;
    }

}
