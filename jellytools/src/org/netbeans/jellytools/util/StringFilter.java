/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.jellytools.util;

import java.util.ArrayList;

/** Filters string, you can simple use replaceString() method or create string
 * filter for more sophisticated filtering.
 *
 * <p>
 * Usage:<br>
 * <pre>
 * StringFilter sf = new StringFilter();
 * // remove 1st comment
 * sf.addReplaceFilter("/*",  "*&#47;", "");
 * // replace all multiple spaces
 * sf.addReplaceAllFilter("  ", " ");
 * // change author name
 * sf.addReplaceFilter("author: ", "", "author: spl@sun.com");
 * String string = "/* comment *&#47;    4s  2s   3s author: xxx@sun.com";
 * String result = sf.filter(string);
 * </pre>
 *
 * @author Martin.Schovanek@sun.com
 * @see #replaceString(String original, String begin, String end, String replace)
 */
public class StringFilter {
    ArrayList filter = new ArrayList();
    
    /** Adds replace pattern into Filter.
     * @see #replaceString(String original, String begin, String end, String replace )
     * @param begin the begin of substring to be find
     * @param end the end of substring to be find
     * @param replace text to replace
     */
    public void addReplaceFilter(String begin, String end, String replace) {
        filter.add(new Pattern(begin, end, replace));
    }
    
    /** Adds replace pattern into Filter.
     * @see #replaceString(String original, String begin, String end, String replace )
     * @param find text to find
     * @param replace text to replace
     */
    public void addReplaceAllFilter(String find, String replace) {
        filter.add(new Pattern(find, find, replace));
    }
    
    /** Filters string.
     * @param str text to filter
     * @return filtred string
     */
    public String filter(String str) {
        for (int i = 0; i < filter.size(); i++) {
            Pattern p = (Pattern) filter.get(i);
            if (p != null)  str = replaceString(str, p.begin, p.end, p.replace);
        }
        return str;
    }
    
    /** Finds substring which starts with first occurrence of 'begin' and ends
     * with nearest next occurrence of 'end' and replces it with 'replace '.<p>
     *
     * Usage:
     * <br><pre>
     * replaceString("a-bcd-ef",    "b",  "d",  "")   => a--ef
     * replaceString("abc-def",     "",   "c",  "")   => -def
     * replaceString("ab-cdef",     "c",  "",   "")   => ab-
     * replaceString("ab-cdef-ab",  "ab", "ab", "AB") => AB-cdef-AB
     * replaceString("abcdef",      "",    "",  "AB") => abcdef
     * </pre>
     *
     * @return filtred string
     * @param replace text to replace
     * @param original the original string
     * @param begin the begin of substring to be find
     * @param end the end of substring to be find
     */
    public static String replaceString(String original, String begin, String end, String replace ) {
        boolean replaceAll = false;
        int from;
        int to;
        int offset = 0;
        
        if (isEmpty(original) || (isEmpty(begin) && isEmpty(end))) return original;
        if (begin.equals(end)) replaceAll = true;
        do {
            from = isEmpty(begin) ? 0 : original.indexOf(begin, offset);
            if (from < 0)
                break;
            if (isEmpty(end)) {
                to = original.length();
            } else {
                to = original.indexOf(end, from);
                if (to < 0)
                    break;
                to += end.length();
            }
            original = original.substring(0, from) + replace  + original.substring(to, original.length());
            offset = from + replace.length();
        } while (replaceAll);
        
        return original;
    }
    
    /** Finds and replace all substrings.
     * @see #replaceString(String original, String begin, String end, String replace)
     * @param original the original string
     * @param find text to find
     * @param replace text to replace
     * @return filtred string
     */
    public static String replaceStringAll(String original, String find, String replace) {
        return replaceString(original, find, find, replace);
    }
    
    private static boolean isEmpty(String s) {
        return s == "" || s == null;
    }
    
    private class Pattern {
        private String begin;
        private String end;
        private String replace;
        
        public Pattern(String begin, String end, String replace) {
            this.begin = begin;
            this.end = end;
            this.replace = replace;
        }
    }
}
