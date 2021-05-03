package ie.gmit.sw.ai;

public class NeuralNetworkActions implements Command {

	@Override
	public int execute(int health, int sword, int gun) {
		NN nn = new NN();
		double result = nn.neuralNetworkResult(health, sword, gun);
		System.out.println("Health " + health);
		System.out.println("Sword " + sword);
		System.out.println("Gun " + gun);
		System.out.println(
				"Output of nn.neuralNetworkResult(" + health + ", " + sword + ", " + gun + ") is " + result + ".");

		return (int) result;
	}

}
