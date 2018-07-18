package Availability;

/**
 * This is a functional interface designed to consume a byte and
 * return a boolean based the lambda expression passed into it.
 * <p>
 * Note that this method can be used either way, but is intended to
 * <strong>reject</strong> bytes, not accept them. 
 * <p>
 * Its general purpose is to facilitate the querying and modification
 * of bytes within the various AvailabilityList classes in this 
 * program.
 * <p>
 * An example usage of this would be to query if a byte in the 
 * {@link AvailabilityArray#isAvailable(TimeChunk)} method as 
 * follows.
 * <p>
 * <code> byte {@literal ->} != AVAILABILE </code>
 * <p>
 * This codes purpose is to cause the whole method to return false if
 * the byte it is currently examining is not marked as 
 * {@link AvailabilityArray#AVAILABLE}.
 * <p>
 * 
 * @author Michael Newman
 * @version 1.0
 * @see AvailabilityArray
 */
@FunctionalInterface
public interface ByteRejecter {

	/**
	 * Consumes a byte and returns a boolean based on the contents
	 * of the lambda expression. 
	 * 
	 * @param toConsume the byte passed into the function through 
	 *                  the lambda expression
	 * @return a boolean dependent upon the lambda expression.
	 *         <code>true</code>: if the byte is <strong>TO BE REJECTED</strong>
	 *         <code>false</code>: if the byte is <strong>TO BE ACCEPTED</strong>
	 */
	boolean reject(byte toConsume);
	

}
