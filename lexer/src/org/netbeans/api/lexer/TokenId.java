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

package org.netbeans.api.lexer;

import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Unique identifier of a particular token.
 * It is not a token, because in general it does not contain
 * the text (also called image) of the token.
 * <BR>The tokenIds are typically defined
 * as public static final constants in subtypes
 * of {@link Language}.
 * <BR>All the tokenIds in a language must have both
 * unique intId and name.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public final class TokenId {
    
    public static final Comparator NAME_COMPARATOR = NameComparator.INSTANCE;
    
    public static final Comparator INT_ID_COMPARATOR = IntIdComparator.INSTANCE;
    
    private static final List EMPTY_CATEGORY_NAMES_LIST
        = new ReadonlyList(new String[0]);
    
    /** Interned category names lists cache */
    private static final List categoryCache = new ArrayList();


    private final String name;
    
    private final int intId;
    
    private final List categoryNamesList;
    
    private final TokenTextMatcher tokenTextMatcher;
    
    public TokenId(String name, int intId) {
        this(name, intId, null, null);
    }
    
    public TokenId(String name, int intId, String[] categoryNames) {
        this(name, intId, categoryNames, null);
    }

    /** Construct new TokenId.
     * @param name non-null name of the TokenId unique among the tokenIDs
     *  in the language where this TokenId is defined.
     *  It can be retrieved by {@link #getName()}.
     * @param intId integer identification unique among the tokenIDs
     *  in the language where this TokenId is defined. It can
     *  It can be retrieved by {@link #getID()}.
     * @param categoryNames names of categories into which this TokenId belongs.
     *  It can be retrieved by {@link #getCategoryNames()}. It can be null
     *  to indicate that tokenId does not belong to any token category.
     * @param tokenTextMatcher matcher to one or more fixed texts
     *  or null if there is no matcher (the tokens with this tokenId have always
     *  variable text).
     *
     */
    public TokenId(String name, int intId, String[] categoryNames,
    TokenTextMatcher tokenTextMatcher) {
        
        if (name == null) {
            throw new NullPointerException("TokenId name cannot be null");
        }

        if (intId < 0) {
            throw new IllegalArgumentException("intId=" + intId
                + " of token=" + name + " is < 0");
        }
        
        if (name.indexOf('.') >= 0) {
            throw new IllegalArgumentException("TokenId name="
                + name + " cannot contain '.' character.");
        }

        this.name = name;
        this.intId = intId;
        this.categoryNamesList = internCategoryNames(categoryNames);
        this.tokenTextMatcher = tokenTextMatcher;
    }
    
    /** @return the unique name of the TokenId. The name must be unique
     * among other TokenId instances inside the language where
     * it is defined. The name should consist of
     * lowercase letters and hyphens only.
     * <P>It can serve for several purposes such as finding
     * a possible style information for the given token.
     * The name is always non-null.
     */
    public String getName() {
        return name;
    }

    /** @return the unique numeric identification of this TokenId.
     * IntId must be a non-negative
     * integer unique among all the tokenIDs inside the language
     * where it is declared.
     * <BR>The intIds are usually defined and adopted from the lexer
     * generator tool that generates the lexer for the given language.
     * <BR>The ids do not have to be consecutive but the ids should
     * not be unnecessarily high (e.g. 1000) because
     * indexing arrays are constructed based on the ids
     * so the length of the indexing arrays corresponds
     * to the highest intId of all the tokenIDs declared
     * for the particular language.
     * <BR>The intIds allow more efficient use
     * of the tokenIds in switch-case statements.
     */
    public int getIntId() {
        return intId;
    }

    /** @return non-null list of category names to which this token belongs.
     * They can be e.g. "operator", "separator" etc.
     * The "error" category marks the errorneous lexical construction.
     * The "incomplete" category marks the unclosed tokens such
     * as unclosed string literal or multi-line comment.
     * <BR>If the token belongs to no categories
     * the empty array will be returned.
     * <BR>As the list is returned the order
     * of the token categories is preserved.
     * Although there is no strict rule the first
     * token category in the list should be
     * the most "natural" one for the given tokenId.
     */
    public List getCategoryNames() {
        return categoryNamesList;
    }
    
    /**
     * @return a valid matcher if the tokens with this tokenId
     * have some fixed text(s) (e.g. keywords or operators)
     * or null if the text of the tokens always varies.
     */
    public TokenTextMatcher getTokenTextMatcher() {
        return tokenTextMatcher;
    }
    
    /** Get the possibly reused copy of the categoryNames list.
     */
    private List internCategoryNames(String[] categoryNames) {
        List ret = EMPTY_CATEGORY_NAMES_LIST;
        
        if (categoryNames != null && categoryNames.length > 0) {
            int index = Collections.binarySearch(categoryCache,
                categoryNames, CategoryNamesComparator.INSTANCE);
            
            if (index < 0) { // not found
                index = -index - 1;
                categoryCache.add(index,
                    new ReadonlyList((String[])categoryNames.clone()));
            }
            
            ret = (List)categoryCache.get(index);
        }
        
        return ret;
    }
    
    public String toString() {
        return getName() + "[" + getIntId() + "]";
    }
    
    public String toStringDetail() {
        StringBuffer sb = new StringBuffer();
        sb.append(toString());
        if (categoryNamesList.size() > 0) {
            sb.append(", cats=");
            sb.append(categoryNamesList.toString());
        }
        if (tokenTextMatcher != null) {
            sb.append(", tokenTextMatcher=");
            sb.append(tokenTextMatcher);
        }
        
        return sb.toString();
    }
    
    public static String categoryCacheToString() {
        return categoryCache.toString();
    }
    
    static final class ReadonlyList extends AbstractList {
        
        Object[] objs;

        ReadonlyList(Object[] objs) {
            this.objs = objs;
        }

        public int size() {
            return objs.length;
        }

        public Object get(int index) {
            return objs[index];
        }
        
    }
    
    private static final class CategoryNamesComparator implements Comparator {
        
        static Comparator INSTANCE = new CategoryNamesComparator();
     
        public int compare(Object o1, Object o2) {
            Object[] o1Array = (o1 instanceof List)
                ? ((ReadonlyList)o1).objs
                : (Object[])o1;

            Object[] o2Array = (o2 instanceof List)
                ? ((ReadonlyList)o2).objs
                : (Object[])o2;

            int o1ArrayLength = o1Array.length;
            int lengthDiff  = o1ArrayLength - o2Array.length;
            
            if (lengthDiff != 0) {
                return lengthDiff;
            }
            
            for (int i = 0; i < o1ArrayLength; i++) {
                int diff = ((String)o1Array[i]).compareTo(o2Array[i]);
                if (diff != 0) {
                    return diff;
                }
            }
            
            return 0;
        }
    }
    
    private static final class NameComparator implements Comparator {
        
        static final Comparator INSTANCE = new NameComparator();
        
        public int compare(Object o1, Object o2) {
            return ((TokenId)o1).getName().compareTo(((TokenId)o2).getName());
        }
    }

    private static final class IntIdComparator implements Comparator {
        
        static final Comparator INSTANCE = new IntIdComparator();
        
        public int compare(Object o1, Object o2) {
            return ((TokenId)o1).getIntId() - ((TokenId)o2).getIntId();
        }
    }

}

