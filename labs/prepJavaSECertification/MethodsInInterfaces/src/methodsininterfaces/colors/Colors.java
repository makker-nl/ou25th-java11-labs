/*
 * Inheritance Rules of default Methods.
 * Rule 1:
 * A super class method Takes Priority over an interface default method.
 * . The superclass method may be concrete or abastract.
 * . Only consider the interface default if no method exists from the superclass.
 * Rule 2:
 * A subtype interfaceś default method takes priority over a super-type interface's default method.
 * Rule 3:
 * If there is a conflict, treat the default method as abstract.
 * . The concrete class must provide its own implementation
 * . This may include a call to a specific interface's implementation of the method.
 * Interfaces don't replace abstract classes
 * . An interface doesn store the state of an instance.
 * . An abstract class may contain instance fields.
 * . A class cannot extend multible abstract classes.
 */
package methodsininterfaces.colors;

/**
 *
 * @author redhat
 */
public class Colors implements Black, Red, Gold {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Colors colors = new Colors();
        /* Here Rule 2 applies: 
         * Red takes precedence over Black. However Gold has a static method getColor() not default.
         * https://stackoverflow.com/questions/27833168/difference-between-static-and-default-methods-in-interface
         * Differences between static and default methods in Java 8:
         * 1) Default methods can be overriden in implementing class, while static cannot.
         * 2) Static method belongs only to Interface class, so you can only invoke static method on Interface class, not on class implementing this Interface.
         * 3) Both class and interface can have static methods with same names, and neither overrides other!
         */
        colors.getColor();
        Gold.getColor();
    }

}
