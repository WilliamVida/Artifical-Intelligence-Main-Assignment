package ie.gmit.sw.ai;

/*
 * Use implementations of this functional interface to specify
 * how a computer controlled game character should behave.
 */

@FunctionalInterface
public interface Command {

	public int execute(int health, int sword, int gun);

}
