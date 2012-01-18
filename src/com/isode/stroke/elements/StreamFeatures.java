/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tron?on.
 * All rights reserved.
 */
package com.isode.stroke.elements;

//FIXME: parser/serialiser
import java.util.ArrayList;
import java.util.Collection;

public class StreamFeatures implements Element {

    public StreamFeatures() {
        hasStartTLS_ = false;
        hasResourceBind_ = false;
        hasSession_ = false;
        hasStreamManagement_ = false;
    }

    public void setHasStartTLS() {
        hasStartTLS_ = true;
    }

    public boolean hasStartTLS() {
        return hasStartTLS_;
    }

    public void setHasSession() {
        hasSession_ = true;
    }

    public boolean hasSession() {
        return hasSession_;
    }

    public void setHasResourceBind() {
        hasResourceBind_ = true;
    }

    public boolean hasResourceBind() {
        return hasResourceBind_;
    }

    public Collection<String> getCompressionMethods() {
        return compressionMethods_;
    }

    public void addCompressionMethod(String mechanism) {
        compressionMethods_.add(mechanism);
    }

    public boolean hasCompressionMethod(String mechanism) {
        return compressionMethods_.contains(mechanism);
    }

    public Collection<String> getAuthenticationMechanisms() {
        return authenticationMechanisms_;
    }

    public void addAuthenticationMechanism(String mechanism) {
        authenticationMechanisms_.add(mechanism);
    }

    public boolean hasAuthenticationMechanism(String mechanism) {
        return authenticationMechanisms_.contains(mechanism);
    }

    public boolean hasAuthenticationMechanisms() {
        return !authenticationMechanisms_.isEmpty();
    }

    public boolean hasStreamManagement() {
        return hasStreamManagement_;
    }

    public void setHasStreamManagement() {
        hasStreamManagement_ = true;
    }

    public boolean hasRosterVersioning() {
        return hasRosterVersioning_;
    }

    public void setHasRosterVersioning() {
        hasRosterVersioning_ = true;
    }
    
    @Override
    public String toString() {
        return "StreamFeatures: hasStartTLS=" + hasStartTLS_ +
        "; hasResourceBind_=" + hasResourceBind_ +
        "; hasSession_=" + hasSession_ +
        "; hasStreamManagement_ =" + hasStreamManagement_ +
        "; compression methods:" + compressionMethods_.size() +
        "; authentication mechs:" + authenticationMechanisms_.size();
    }

    private boolean hasStartTLS_;
    private ArrayList<String> compressionMethods_ = new ArrayList<String>();
    private ArrayList<String> authenticationMechanisms_ = new ArrayList<String>();
    private boolean hasResourceBind_;
    private boolean hasSession_;
    private boolean hasStreamManagement_;
    private boolean hasRosterVersioning_;
}
