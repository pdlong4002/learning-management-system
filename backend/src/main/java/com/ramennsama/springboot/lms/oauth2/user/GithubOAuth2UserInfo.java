package com.ramennsama.springboot.lms.oauth2.user;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String AVATAR_URL = "avatar_url";
    private static final String LOGIN = "login";

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return getStringAttribute(ID);
    }

    @Override
    public String getName() {
        String name = getStringAttribute(NAME);
        return name != null ? name : getStringAttribute(LOGIN);
    }

    @Override
    public String getEmail() {
        return getStringAttribute(EMAIL);
    }

    @Override
    public String getImageUrl() {
        return getStringAttribute(AVATAR_URL);
    }
}
