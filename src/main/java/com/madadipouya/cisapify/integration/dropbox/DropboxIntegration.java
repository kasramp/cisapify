package com.madadipouya.cisapify.integration.dropbox;

import com.madadipouya.cisapify.integration.base.SongAware;
import com.madadipouya.cisapify.integration.dropbox.exception.DropboxIntegrationException;
import com.madadipouya.cisapify.user.model.User;

import javax.servlet.http.HttpServletRequest;

public interface DropboxIntegration extends SongAware {

    String getAuthorizationUrl(HttpServletRequest request) throws DropboxIntegrationException;

    User finishAuthentication(HttpServletRequest request) throws DropboxIntegrationException;
}