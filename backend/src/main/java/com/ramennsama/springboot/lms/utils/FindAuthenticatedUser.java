package com.ramennsama.springboot.lms.utils;

import com.ramennsama.springboot.lms.entity.User;

public interface FindAuthenticatedUser {
    User getAuthenticatedUser();
}
