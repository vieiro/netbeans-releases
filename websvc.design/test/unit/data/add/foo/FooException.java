/*
 * Foo.java
 *
 * Created on March 30, 2007, 4:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package add.foo;

import javax.xml.ws.WebFault;
/**
 *
 * @author mkuchtiak
 */
@WebFault(name="FooFault")
public class FooException extends Exception {

/** Creates a new instance of Foo */
    public FooException(String message) {
        super(message);
    }  
}
