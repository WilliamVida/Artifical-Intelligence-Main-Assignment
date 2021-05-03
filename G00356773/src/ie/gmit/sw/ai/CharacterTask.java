package ie.gmit.sw.ai;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;
import javafx.concurrent.Task;

/*
 * CharacterTask represents a Runnable (Task is a JavaFX implementation
 * of Runnable) game character. The character wanders around the game
 * model randomly and can interact with other game characters using
 * implementations of the Command interface that it is composed with. 
 * 
 * You can change how this class behaves is a number of different ways:
 * 
 * 1) DON'T CHANGE THIS CLASS
 * 	  Configure the class constructor with an instance of Command. This can
 *    be a full implementation of a fuzzy controller or a neural network. You
 *    can also parameterise this class with a lambda expression for the 
 *    command object. 
 *    
 * 2) CHANGE THIS CLASS 
 * 	  You can extend this class and provide different implementations of the 
 * 	  call by overriding (not recommended). Alternatively, you can change the
 *    behaviour of the game character when it moves by altering the logic in
 *    the IF statement:  
 *    
 * 		if (model.isValidMove(row, col, temp_row, temp_col, enemyID)) {
 * 	    	//Implementation for moving to an occupied cell
 * 	    }else{
 *      	//Implementation for moving to an unoccupied cell
 *      } 
 */
public class CharacterTask extends Task<Void> {

	private static final int SLEEP_TIME = 300; // Sleep for 300 ms
	public static final double MAX_HEALTH = 100.00;
	private static final double MIN_HEALTH = 0.00;
	private static ThreadLocalRandom rand = ThreadLocalRandom.current();
	private boolean alive = true;
	private GameModel model;
	private char enemyID;
	private int row;
	private int col;
	private double health;

	/*
	 * Configure each character with its own action. Use this functional interface
	 * as a hook or template to connect to your fuzzy logic and neural network. The
	 * method execute() of Command will execute when the Character cannot move to a
	 * random adjacent cell.
	 */
	private Command cmd;
	public static int deadEnemies;

	public CharacterTask(GameModel model, char enemyID, int row, int col, double health, Command cmd) {
		this.model = model;
		this.enemyID = enemyID;
		this.row = row;
		this.col = col;
		this.health = health;
		this.cmd = cmd;
	}

	@Override
	public Void call() throws Exception {
		/*
		 * This Task will remain alive until the call() method returns. This cannot
		 * happen as long as the loop control variable "alive" is set to true. You can
		 * set this value to false to "kill" the game character if necessary (or maybe
		 * unnecessary...).
		 */
		while (alive) {
			Thread.sleep(SLEEP_TIME);
			FuzzyLogic fuzzyLogic = new FuzzyLogic();
			AverageDistance averageDistance = new AverageDistance();
			int neuralNetworkHealth;
			Random randomNN = new Random();
			int neuralNetworkResult = 0;

			synchronized (model) {
				// Randomly pick a direction up, down, left or right
				int temp_row = row, temp_col = col;
				if (rand.nextBoolean()) {
					temp_row += rand.nextBoolean() ? 1 : -1;
				} else {
					temp_col += rand.nextBoolean() ? 1 : -1;
				}

				if (model.isValidMove(row, col, temp_row, temp_col, enemyID)) {
					/*
					 * This fires if the character can move to a cell, i.e. if it is not already
					 * occupied. You can add extra logic here to invoke behaviour when the computer
					 * controlled character is in the proximity of the player or another
					 * character...
					 */
					model.set(temp_row, temp_col, enemyID);
					model.set(row, col, '\u0020');
					row = temp_row;
					col = temp_col;
				} else {
					/*
					 * This fires if a move is not valid, i.e. if someone or some thing is in the
					 * way. Use implementations of Command to control how the computer controls this
					 * character.
					 */
					System.out.println("Enemy ID " + enemyID + ".");
					System.out.println("Enemy health " + health + ".");
					System.out
							.println("Distance from the centre " + averageDistance.getAverageDistance(row, col) + ".");
					System.out.println("Fuzzy logic damage "
							+ fuzzyLogic.getDamageDealt(health, averageDistance.getAverageDistance(row, col)) + ".");
					health -= fuzzyLogic.getDamageDealt(health, averageDistance.getAverageDistance(row, col));
					System.out.println();
				}

				// Set the neural network health score based on the health remaining.
				if (health >= 70)
					neuralNetworkHealth = 2;
				else if (health >= 40)
					neuralNetworkHealth = 1;
				else
					neuralNetworkHealth = 0;

				// Check if the player is in range of an enemy to call the neural network.
				if (ifInRange()) {

					System.out.println("Enemy ID " + enemyID + " is near the player.");
					System.out.println("Player health " + GameWindow.playerHealth + ".");
					neuralNetworkResult = cmd.execute(neuralNetworkHealth, randomNN.nextInt(2), randomNN.nextInt(2));

					// Panic.
					if (neuralNetworkResult == 0) {
						System.out.println("Enemy ID " + enemyID + " panics and suffers health damage of 2.");
						health -= 2;
					}
					// Attack.
					else if (neuralNetworkResult == 1) {
						System.out.println("Enemy ID " + enemyID
								+ " attacks the player and he suffers health damage of 8 and the enemy gains 8 health.");
						GameWindow.playerHealth -= 8;
						health += 8;
					}
					// Hide.
					else if (neuralNetworkResult == 2) {
						System.out.println("Enemy ID " + enemyID + " hides and suffers health damage of 2.");
						health -= 2;
					}
					// Run.
					else if (neuralNetworkResult == 3) {
						System.out.println("Enemy ID " + enemyID
								+ " runs and suffers no health damage and the player gains 4 health.");
						GameWindow.playerHealth += 4;
					}

					System.out.println();
				}

				// Set the health if it goes above or below the maximum or the minimum.
				if (health >= MAX_HEALTH) {
					health = MAX_HEALTH;
				} else if (health <= MIN_HEALTH) {
					System.out.println("Enemy ID " + enemyID + " has died.");
					deadEnemies++;
					health = MIN_HEALTH;
					alive = false;
				}

				ifInRange();
			}
		}

		return null;
	}

	// Method to check if the player is in range of an enemy within 2 rows and
	// columns.
	public boolean ifInRange() {
		// Exit if the player's health is less than zero.
		if (GameWindow.playerHealth <= 0) {
			System.out.println("You died.");
			Platform.exit();
		}

		// Range from https://stackoverflow.com/a/10264336.
		if (Math.abs(row - GameWindow.currentRow) <= 2 && Math.abs(col - GameWindow.currentCol) <= 2)
			return true;
		else
			return false;
	}

}
