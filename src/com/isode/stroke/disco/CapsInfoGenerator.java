/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.stringcodecs.Base64;

public class CapsInfoGenerator {
    private String node_ = "";
    private CryptoProvider crypto_;

    private final static Comparator<FormField> compareFields = new Comparator<FormField>() {
        @Override
        public int compare(FormField lhs, FormField rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };

    CapsInfoGenerator(final String node, CryptoProvider crypto) {
        this.node_ = node;
        this.crypto_ = crypto;
    }

    CapsInfo generateCapsInfo(final DiscoInfo discoInfo) {
        String serializedCaps = "";

        List<DiscoInfo.Identity> identities = discoInfo.getIdentities();
        Collections.sort(identities);
        for (final DiscoInfo.Identity identity : identities) {
            serializedCaps += identity.getCategory() + "/" + identity.getType()
                    + "/" + identity.getLanguage() + "/" + identity.getName()
                    + "<";
        }

        List<String> features = discoInfo.getFeatures();
        Collections.sort(features);
        for (final String feature : features) {
            serializedCaps += feature + "<";
        }

        for (Form extension : discoInfo.getExtensions()) {
            serializedCaps += extension.getFormType() + "<";
            List<FormField> fields = extension.getFields();
            Collections.sort(fields, compareFields);
            for (FormField field : fields) {
                if ("FORM_TYPE".equals(field.getName())) {
                    continue;
                }
                serializedCaps += field.getName() + "<";
                List<String> values = field.getValues();
                Collections.sort(values);
                for (final String value : values) {
                    serializedCaps += value + "<";
                }
            }
        }

        String version = Base64.encode(crypto_ .getSHA1Hash(new ByteArray(serializedCaps)));
        return new CapsInfo(node_, version, "sha-1");
    }

}
