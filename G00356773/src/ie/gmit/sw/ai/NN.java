package ie.gmit.sw.ai;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class NN {

	// Health, sword, gun.
	private double[][] data = { { 2, 0, 0 }, { 2, 0, 0 }, { 2, 0, 1 }, { 2, 0, 1 }, { 2, 1, 0 }, { 2, 1, 0 },
			{ 1, 0, 0 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 1 }, { 1, 1, 0 }, { 1, 1, 0 }, { 0, 0, 0 }, { 0, 0, 0 },
			{ 0, 0, 1 }, { 0, 0, 1 }, { 0, 1, 0 }, { 0, 1, 0 } };

	// Panic, attack, hide, run.
	private double[][] expected = { { 0.0, 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0, 0.0 }, { 1.0, 0.0, 0.0, 0.0 },
			{ 1.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 }, { 1.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 1.0, 0.0 },
			{ 0.0, 0.0, 0.0, 1.0 }, { 1.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0, 1.0 },
			{ 0.0, 0.0, 0.0, 1.0 }, { 0.0, 0.0, 1.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0, 1.0 },
			{ 0.0, 1.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 } };

	private static BasicNetwork network;

	BasicNetwork createNeuralNetwork() {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, 3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 5));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 4));
		network.getStructure().finalizeStructure();
		network.reset();
		System.out.println("Neural network was created.");
		return network;
	}

	public void neuralNetwork() {
		// Declare neural network topology.
		System.out.println("Creating neural network.");

		// Create the neural network.
		network = createNeuralNetwork();

		// Create the training data set.
		System.out.println("Creating the neural network training set.");
		MLDataSet trainingSet = new BasicMLDataSet(data, expected);

		// Train the neural network.
		System.out.println("Training the neural network.");
		ResilientPropagation train = new ResilientPropagation(network, trainingSet);

		double minError = 0.09;
		int epoch = 1;
		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > minError);
		train.finishTraining();
		System.out.println("Neural network training complete in " + epoch + " epochs with error = " + train.getError());

		// Testing the neural network.
		System.out.println("Testing the neural network:");
		for (MLDataPair pair : trainingSet) {
			MLData output = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1) + ", Y = "
					+ (int) Math.round(output.getData(0)) + ", Yd = " + (int) pair.getIdeal().getData(0));
		}

		// Shut down the neural network.
		System.out.println("Shutting down the neural network.");
		Encog.getInstance().shutdown();

		// Save the neural network.
		// EncogDirectoryPersistence.saveObject(new
		// File("resources/neural/neuralnetwork.nn"), network);
	}

	public double neuralNetworkResult(int health, int sword, int gun) {
		System.out.println("Getting neural network result.");
		double[] input = { health, sword, gun };
		MLData mlData = new BasicMLData(input);
		return network.classify(mlData);
	}

}
