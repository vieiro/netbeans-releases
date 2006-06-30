/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.junit.output;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.regex.Pattern;
import org.netbeans.modules.junit.RegexpPatterns;

/**
 * Utility class providing various parsing routines for parsing JUnit output.
 *
 * @author  Marian Petras
 */
final class RegexpUtils {

    /** */
    static final String TESTSUITE_PREFIX = "Testsuite: ";               //NOI18N
    /** */
    static final String TESTSUITE_STATS_PREFIX = "Tests run: ";         //NOI18N
    /** */
    static final String FLOAT_NUMBER_REGEX
            = "[0-9]*(?:\\.[0-9]+)?";                                   //NOI18N
    /** */
    static final String TIME_SECS_REGEX
            = '(' + FLOAT_NUMBER_REGEX + ')'
              + " +s(?:ec(?:ond)?(?:s|\\(s\\))?)?";                     //NOI18N
    /** */
    static final String TESTSUITE_STATS_REGEX
        = "Tests run: +([0-9]+)," +                                     //NOI18N
          " +Failures: +([0-9]+), +Errors: +([0-9]+)," +                //NOI18N
          " +Time elapsed: +" + TIME_SECS_REGEX;                        //NOI18N
    /** */
    static final String OUTPUT_DELIMITER_PREFIX = "--------";           //NOI18N
    /** */
    static final String STDOUT_LABEL = "Output";                        //NOI18N
    /** */
    static final String STDERR_LABEL = "Error";                         //NOI18N
    /** */
    static final String OUTPUT_DELIMITER_REGEX
            = "-{8,} (?:Standard ("                                     //NOI18N
              + STDOUT_LABEL + '|' + STDERR_LABEL + ")|-{3,}) -{8,}";   //NOI18N
    /** */
    static final String TESTCASE_PREFIX = "Testcase: ";                 //NOI18N
    /** */
    static final String TESTCASE_ISSUE_REGEX
            = "\\p{Blank}*(?:(FAILED) *|(?i:.*\\berror\\b.*))";         //NOI18N
    /** */
    static final String TESTCASE_HEADER_PLAIN_REGEX
            = "\\p{Blank}*(" + RegexpPatterns.JAVA_ID_REGEX             //NOI18N
              + ")\\p{Blank}+took\\p{Blank}+" + TIME_SECS_REGEX;        //NOI18N
    /** */
    static final String TESTCASE_HEADER_BRIEF_REGEX
            = "\\p{Blank}*(" + RegexpPatterns.JAVA_ID_REGEX             //NOI18N
              + ") *\\( *(" + RegexpPatterns.JAVA_ID_REGEX_FULL         //NOI18N
              + ") *\\) *:" + TESTCASE_ISSUE_REGEX;                     //NOI18N
    /** */
    static final String TESTCASE_EXCEPTION_REGEX
            = "((?:" + RegexpPatterns.JAVA_ID_REGEX_FULL                //NOI18N
              + "\\.?(?:Exception|Error|ComparisonFailure))"            //NOI18N
                        + "|java\\.lang\\.Throwable)"                   //NOI18N
              + "(?: *: *(.*))?";                                       //NOI18N
    /** */
    static final String CALLSTACK_LINE_PREFIX = "at ";                  //NOI18N
    /** */
    static final String CALLSTACK_LINE_PREFIX_CATCH = "[catch] ";       //NOI18N
    /** */
    static final String CALLSTACK_LINE_REGEX
            = "(?:\\t\\t?|  +| *\\t? *\\[catch\\] )"                    //NOI18N
              + CALLSTACK_LINE_PREFIX
              + RegexpPatterns.JAVA_ID_REGEX + "(?:\\."                 //NOI18N
              + RegexpPatterns.JAVA_ID_REGEX + ")+"                     //NOI18N
              + "(?: ?\\([^()]+\\))?";                                  //NOI18N
    static final String LOCATION_IN_FILE_REGEX
            = RegexpPatterns.JAVA_ID_REGEX_FULL + "(?:\\:[0-9]+)?";     //NOI18N
    /** */
    static final String XML_DECL_PREFIX = "<?xml";                      //NOI18N
    /** */
    static final String XML_SPACE_REGEX
            = "[ \\t\\r\\n]";                                           //NOI18N
    /** */
    static final String XML_EQ_REGEX
            = XML_SPACE_REGEX + '*' + '=' + XML_SPACE_REGEX + '*';
    /** */
    static final String XML_ENC_REGEX
            = "[A-Za-z][-A-Za-z0-9._]*";                                //NOI18N
    /** */
    static final String XML_DECL_REGEX
            = "\\Q" + XML_DECL_PREFIX + "\\E"                           //NOI18N
                  + XML_SPACE_REGEX + '+' + "version"     //version     //NOI18N
                    + XML_EQ_REGEX + "(?:\"1\\.0\"|'1\\.0')"            //NOI18N
              + "(?:"                                                   //NOI18N
                  + XML_SPACE_REGEX + '+' + "encoding"    //encoding    //NOI18N
                    + XML_EQ_REGEX + "(['\"])[A-Za-z][-A-Za-z0-9._]*\\1"//NOI18N
              + ")?"                                                    //NOI18N
              + "(?:"                                                   //NOI18N
                  + XML_SPACE_REGEX + '+' + "standalone"  //standalone  //NOI18N
                    + XML_EQ_REGEX + "(['\"])(?:yes|no)\\2"             //NOI18N
              + ")?"                                                    //NOI18N
                  + XML_SPACE_REGEX + '*' + "\\?>";                     //NOI18N
    
    /**
     * Regexp matching part of a Java task's invocation debug message
     * that specificies the classpath.
     * Hack to find the classpath an Ant task is using.
     * Cf. Commandline.describeArguments, issue #28190.<br />
     * Captured groups:
     * <ol>
     *     <li>the classpath
     * </ol>
     * <!-- copied from JavaAntLogger -->
     */
    static final Pattern CLASSPATH_ARGS
            = Pattern.compile("\r?\n'-classpath'\r?\n'(.*)'\r?\n");     //NOI18N
    /**
     * Regexp matching part of a Java task's invocation debug message
     * that specificies java executable.
     * Hack to find JDK used for execution.
     * <!-- copied from JavaAntLogger -->
     */
    static final Pattern JAVA_EXECUTABLE
            = Pattern.compile("^Executing '(.*)' with arguments:$",     //NOI18N
                              Pattern.MULTILINE);

    /** */
    private static Reference instRef;
    
    /**
     */
    static synchronized RegexpUtils getInstance() {
        Object inst;
        RegexpUtils instance;
        if ((instRef == null) || ((inst = instRef.get()) == null)) {
            instRef = new WeakReference(instance = new RegexpUtils());
        } else {
            instance = (RegexpUtils) inst;
        }
        return instance;
    }
    
    /** Creates a new instance of RegexpUtils */
    private RegexpUtils() { }
    
    private volatile Pattern fullJavaIdPattern, suiteStatsPattern, 
                             outputDelimPattern, testcaseIssuePattern,
                             testcaseExceptPattern, callstackLinePattern,
                             locationInFilePattern,
                             testcaseHeaderBriefPattern,
                             testcaseHeaderPlainPattern,
                             xmlDeclPattern, floatNumPattern;
    
    //<editor-fold defaultstate="collapsed" desc=" Note about synchronization ">
    /*
     * If-blocks in the following methods should be synchronized to ensure that
     * the patterns are not compiled twice if the methods are called by two or
     * more threads concurrently.
     *
     * But synchronization is quite expensive so I let them unsynchronized.
     * It may happen that a single pattern is compiled multiple times but
     * it does not cause any functional problem. I just marked the variables
     * as 'volatile' so that once the pattern is compiled (and the variable
     * set), subsequent invocations from other threads will find the actual
     * non-null value.
     */
    //</editor-fold>

    /** */
    Pattern getFullJavaIdPattern() {
        if (fullJavaIdPattern == null) {
            fullJavaIdPattern
                    = Pattern.compile(RegexpPatterns.JAVA_ID_REGEX_FULL);
        }
        return fullJavaIdPattern;
    }
    
    /** */
    Pattern getSuiteStatsPattern() {
        if (suiteStatsPattern == null) {
            suiteStatsPattern = Pattern.compile(TESTSUITE_STATS_REGEX);
        }
        return suiteStatsPattern;
    }
    
    /** */
    Pattern getOutputDelimPattern() {
        if (outputDelimPattern == null) {
            outputDelimPattern = Pattern.compile(OUTPUT_DELIMITER_REGEX);
        }
        return outputDelimPattern;
    }
    
    /** */
    Pattern getTestcaseHeaderBriefPattern() {
        if (testcaseHeaderBriefPattern == null) {
            testcaseHeaderBriefPattern = Pattern.compile(TESTCASE_HEADER_BRIEF_REGEX);
        }
        return testcaseHeaderBriefPattern;
    }
    
    /** */
    Pattern getTestcaseHeaderPlainPattern() {
        if (testcaseHeaderPlainPattern == null) {
            testcaseHeaderPlainPattern = Pattern.compile(TESTCASE_HEADER_PLAIN_REGEX);
        }
        return testcaseHeaderPlainPattern;
    }
    
    /** */
    Pattern getTestcaseIssuePattern() {
        if (testcaseIssuePattern == null) {
            testcaseIssuePattern = Pattern.compile(TESTCASE_ISSUE_REGEX);
        }
        return testcaseIssuePattern;
    }
    
    /** */
    Pattern getTestcaseExceptionPattern() {
        if (testcaseExceptPattern == null) {
            testcaseExceptPattern = Pattern.compile(TESTCASE_EXCEPTION_REGEX);
        }
        return testcaseExceptPattern;
    }
    
    /** */
    Pattern getCallstackLinePattern() {
        if (callstackLinePattern == null) {
            callstackLinePattern = Pattern.compile(CALLSTACK_LINE_REGEX);
        }
        return callstackLinePattern;
    }
    
    /** */
    Pattern getLocationInFilePattern() {
        if (locationInFilePattern == null) {
            locationInFilePattern = Pattern.compile(LOCATION_IN_FILE_REGEX);
        }
        return locationInFilePattern;
    }
    
    /** */
    Pattern getXmlDeclPattern() {
        if (xmlDeclPattern == null) {
            xmlDeclPattern = Pattern.compile(XML_DECL_REGEX);
        }
        return xmlDeclPattern;
    }
    
    /** */
    Pattern getFloatNumPattern() {
        if (floatNumPattern == null) {
            floatNumPattern = Pattern.compile(FLOAT_NUMBER_REGEX);
        }
        return floatNumPattern;
    }
    
    /**
     * Parses a floating-point number describing elapsed time.
     * The returned number is a number of elapsed milliseconds.
     *
     * @param  string represeting non-negative floating-point number of seconds
     * @return  integer representing number of milliseconds (rounded)
     * @exception  java.lang.NumberFormatException
     *             if the passed string does not match
     *             the {@link #FLOAT_NUMBER_REGEX} pattern
     */
    int parseTimeMillis(String timeString) throws NumberFormatException {
        int secs, millis;
        final int dotIndex = timeString.indexOf('.');
        if (dotIndex == -1) {
            secs = Integer.parseInt(timeString);
            millis = 0;
        } else {
            secs = (dotIndex == 0)
                   ? 0
                   : Integer.parseInt(timeString.substring(0, dotIndex));

            String fractString = timeString.substring(dotIndex + 1);
            if (fractString.length() > 4) {
                fractString = fractString.substring(0, 4);
            }
            int fractNum = Integer.parseInt(fractString);
            switch (fractString.length()) {
                case 1:
                    millis = 100 * fractNum;
                    break;
                case 2:
                    millis = 10 * fractNum;
                    break;
                case 3:
                    millis = fractNum;
                    break;
                case 4:
                    millis = (fractNum + 5) / 10;
                    break;
                default:
                    assert false;
                    millis = 0;
                    break;
            }
        }
        return 1000 * secs + millis;
    }
    
    /**
     * Parses a floating-point number describing elapsed time.
     * The returned number is a number of elapsed milliseconds.
     *
     * @param  string represeting non-negative floating-point number of seconds
     * @return  integer representing number of milliseconds (rounded),
     *          or <code>-1</code> if the passed string is <code>null</code>
     *          or if it does not match the {@link #FLOAT_NUMBER_REGEX} pattern
     */
    int parseTimeMillisNoNFE(String timeStr) {
        if ((timeStr == null)
                || !getFloatNumPattern().matcher(timeStr).matches()) {
            return -1;
        }
        try {
            return parseTimeMillis(timeStr);
        } catch (NumberFormatException ex) {
            assert false;
            return -1;
        }
    }
    
    /**
     * Trims leading and trailing spaces and tabs from a string.
     *
     * @param  string  string to remove spaces and tabs from
     * @return  the trimmed string, or the passed string if no trimming
     *          was necessary
     */
    static String specialTrim(String string) {
        
        /* Handle the trivial case: */
        final int len = string.length();
        if (len == 0) {
            return string;
        }
        
        final char[] chars = string.toCharArray();
        char c;

        int lead = 0;
        while (lead < len) {
            c = chars[lead];
            if ((c != ' ') && (c != '\t')) {
                break;
            }
            lead++;
        }
        
        /* Handle a corner case: */
        if (lead == len) {
            return string.substring(len);                                              
        }
        
        int trail = len;
        do {
            c = chars[--trail];
        } while ((c == ' ') || (c == '\t'));
        
        if ((lead == 0) && (trail == len - 1)) {
            return string;
        } else {
            return string.substring(lead, trail + 1);
        }
    }
    
}
