package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import name.ncg777.maths.physics.TernarySpinModel;

public class TrainedTernaryNetworkTests {
  @Rule public TemporaryFolder temporary = new TemporaryFolder();

  @Test
  public void trainedCheckpointRoundTripPreservesInferenceAndTemperature() throws IOException {
    var initial = new TernarySpinModel(new double[2][2], new double[2], new double[2]);
    var result = TernaryBoltzmannTrainer.train(initial, new int[][] {{1, -1}, {0, 0}, {1, 0}},
        TernaryTrainingOptions.builder().epochs(10).beta(1.3).build());
    var network = new TrainedTernaryNetwork(result.model(), result.beta());
    var path = temporary.newFile("network.json").toPath();
    network.save(path);
    var loaded = TrainedTernaryNetwork.load(path);
    assertEquals(1.3, loaded.beta(), 0);
    assertArrayEquals(network.model().fields(), loaded.model().fields(), 0);
    assertArrayEquals(network.model().penalties(), loaded.model().penalties(), 0);
    assertArrayEquals(network.model().couplings()[0], loaded.model().couplings()[0], 0);
    assertArrayEquals(network.model().conditionalDistribution(new int[] {1, 0}, 1, network.beta()).probabilities(),
        loaded.model().conditionalDistribution(new int[] {1, 0}, 1, loaded.beta()).probabilities(), 0);
    assertArrayEquals(network.model().gibbsSample(new int[2], network.beta(), 10, new MersenneTwister(5)),
        loaded.model().gibbsSample(new int[2], loaded.beta(), 10, new MersenneTwister(5)));
  }

  @Test
  public void rejectsMalformedUnknownAndOversizedCheckpoints() throws IOException {
    var path = temporary.newFile("invalid.json").toPath();
    String[] invalid = {"null", "{}", "not JSON",
        "{\"formatVersion\":2,\"beta\":1,\"couplings\":[[0]],\"penalties\":[0],\"fields\":[0]}",
        "{\"formatVersion\":1,\"beta\":0,\"couplings\":[[0]],\"penalties\":[0],\"fields\":[0]}",
        "{\"formatVersion\":1,\"beta\":1,\"couplings\":[[1]],\"penalties\":[0],\"fields\":[0]}",
        "{\"formatVersion\":1,\"beta\":1,\"couplings\":[null],\"penalties\":[0],\"fields\":[0]}",
        "{\"formatVersion\":1,\"beta\":1,\"couplings\":[[0,1],[2,0]],\"penalties\":[0,0],\"fields\":[0,0]}"};
    for (String json : invalid) {
      Files.writeString(path, json);
      assertThrows(IOException.class, () -> TrainedTernaryNetwork.load(path));
    }
    Files.write(path, new byte[1_048_577]);
    assertThrows(IOException.class, () -> TrainedTernaryNetwork.load(path));
  }
}
