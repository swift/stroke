/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.parser;

import java.util.Arrays;

import com.isode.stroke.base.ByteArray;

public class BOSHBodyExtractor {

    private BOSHBody body = null;

    public BOSHBodyExtractor(XMLParserFactory parserFactory,ByteArray data) {
        // Look for the opening body element
        byte[] rawData = data.getData();
        int i = 0;
        while (i < rawData.length && isWhitespace((char) rawData[i])) {
            ++i;
        }
        if ((rawData.length - i) < 6 || rawData[i] != '<' || rawData[i+1] != 'b' 
                || rawData[i+2] != 'o' || rawData[i+3] != 'd' || rawData[i+4] != 'y' 
                || !isEndCharacter((char) rawData[i+5])) {
            return;
        }
        i += 5;


        // Parse until end of element
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean endStartTagSeen = false;
        boolean endElementSeen = false;

        for (; i < rawData.length; ++i) {
            char c = (char) rawData[i];
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            }
            else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            }
            else if (c == '\'') {
                inSingleQuote = true;
            }
            else if (c == '"') {
                inDoubleQuote = true;
            }
            else if (c == '/') {
                if (i + 1 == rawData.length || rawData[i+1] != '>') {
                    return;
                }
                else {
                    endElementSeen = true;
                    endStartTagSeen = true;
                    i += 2;
                    break;
                }
            }
            else if (c == '>') {
                endStartTagSeen = true;
                i += 1;
                break;
            }
        }

        if (!endStartTagSeen) {
            return;
        }

        // Look for the end of the element

        int j = rawData.length - 1;
        if (!endElementSeen) {
            while (isWhitespace((char) rawData[j]) && j > -1) {
                j--;
            }

            if (j == -1 || rawData[j] != '>') {
                return;
            }
            j--;

            while (j > -1 && isWhitespace((char) rawData[j])) {
                j--;
            }

            if (j < 6 || rawData[j-5] != '<' || rawData[j-4] != '/' || rawData[j-3] != 'b' 
                    || rawData[j-2] != 'o' || rawData[j-1] != 'd' || rawData[j] != 'y' ) {
                return;
            }

            j -= 6;
        }

        body = new BOSHBody();

        if (!endElementSeen) {
            byte[] rawBodyContent =
                    Arrays.copyOfRange(rawData, i, j+1);
            body.content = (new ByteArray(rawBodyContent)).toString();
        }

        BOSHBodyParserClient parserClient = new BOSHBodyParserClient(this);
        XMLParser parser = parserFactory.createParser(parserClient);
        String stringToParse = data.toString().substring(0, i);
        if(!parser.parse(stringToParse)) {
            body = null;
        }
    }

    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    private static boolean isEndCharacter(char c) {
        return isWhitespace(c) || c == '>' || c == '/';
    }

    public BOSHBody getBody() {
        return body;
    }

    public static class BOSHBody {
        private AttributeMap attributes = new AttributeMap();
        private String content  = "";
        
        public AttributeMap getAttributes() {
            return attributes;
        }
        
        public String getContent() {
            return content;
        }
        
    }

    private final static class BOSHBodyParserClient implements XMLParserClient {

        private BOSHBodyParserClient(BOSHBodyExtractor bodyExtractor) {
            bodyExtractor_ = bodyExtractor;
        }

        @Override
        public void handleStartElement(String element, String ns,
                AttributeMap attributes) {
            bodyExtractor_.body.attributes = attributes;
        }

        @Override
        public void handleEndElement(String element, String ns) {
            // Empty Method
        }

        @Override
        public void handleCharacterData(String data) {
            // Empty Method
        }

        private final BOSHBodyExtractor bodyExtractor_;

    }

}
