/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.lexer.demo;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.inc.RawOffsetToken;
import org.netbeans.spi.lexer.util.IntegerCache;

/**
 * Simple token implementation for demo purposes.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class DemoToken implements RawOffsetToken, CharSequence {

    private final DemoTokenUpdater tokenUpdater;

    private final TokenId id;

    private int rawOffset;

    private final int length;
    
    private int lookahead;
    
    private int lookback;
    
    private Object state;

    
    DemoToken(DemoTokenUpdater tokenUpdater, TokenId id, int rawOffset, int length) {
        if (tokenUpdater == null) {
            throw new NullPointerException();
        }
        
        if (id == null) {
            throw new NullPointerException();
        }
        
        if (rawOffset < 0) {
            throw new IllegalArgumentException("rawOffset=" + rawOffset);
        }
        
        if (length < 0) {
            throw new IllegalArgumentException("length=" + length);
        }
        
        this.tokenUpdater = tokenUpdater;
        this.id = id;
        this.rawOffset = rawOffset;
        this.length = length;
    }
    
    protected final DemoTokenUpdater getTokenUpdater() {
        return tokenUpdater;
    }
    
    public TokenId getId() {
        return id;
    }

    public CharSequence getText() {
        return this;
    }
    
    public int getOffset() {
        return tokenUpdater.getOffset(rawOffset);
    }
    
    public int getRawOffset() {
        return rawOffset;
    }
    
    public void setRawOffset(int rawOffset) {
        this.rawOffset = rawOffset;
    }
    
    public void updateRawOffset(int diff) {
        rawOffset += diff;
    }
    
    public int length() {
        return length;
    }
    
    public char charAt(int index) {
        if (index < 0 || index >= length) {
            throw new IllegalStateException("index=" + index + ", length=" + length);
        }

        return tokenUpdater.charAt(rawOffset, index);
    }
    
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || end < 0 || start > end || end > length()) {
            throw new IndexOutOfBoundsException(
                "start=" + start + ", end=" + end + ", length()=" + length());
        }

        return (CharSequence)(Object)toString().substring(start, end); // 1.3 compilability
    }
    
    public int getLookahead() {
        return lookahead;
    }

    void setLookahead(int lookahead) {
        this.lookahead = lookahead;
    }

    public int getLookback() {
        return lookback;
    }
    
    void setLookback(int lookback) {
        this.lookback = lookback;
    }
    
    public Object getState() {
        return state;
    }
    
    void setState(Object state) {
        this.state = state;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length(); i++) {
            sb.append(charAt(i));
        }
        return sb.toString();
    }

}

