package name.ncg777.maths.neural;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.ncg777.maths.physics.TernarySpinModel;

/** An immutable fitted model with its inference temperature and a versioned JSON format. */
public record TrainedTernaryNetwork(TernarySpinModel model, double beta) {
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final int MAX_CHECKPOINT_BYTES = 1_048_576;

  public TrainedTernaryNetwork {
    Objects.requireNonNull(model);
    if (model.size() < 1 || model.size() > TernaryBoltzmannTrainer.MAX_NODES
        || !Double.isFinite(beta) || beta <= 0) {
      throw new IllegalArgumentException("Invalid network size or beta");
    }
  }

  /** Saves inference parameters, not optimizer history or persistent-chain state. */
  public void save(Path path) throws IOException {
    JSON.writerWithDefaultPrettyPrinter().writeValue(path.toFile(),
        new Checkpoint(1, beta, model.couplings(), model.penalties(), model.fields()));
  }

  public static TrainedTernaryNetwork load(Path path) throws IOException {
    byte[] bytes;
    try (var input = Files.newInputStream(path)) {
      bytes = input.readNBytes(MAX_CHECKPOINT_BYTES + 1);
    }
    if (bytes.length > MAX_CHECKPOINT_BYTES) throw new IOException("Network checkpoint exceeds 1 MiB");
    Checkpoint checkpoint = JSON.readValue(bytes, Checkpoint.class);
    if (checkpoint == null || checkpoint.formatVersion() != 1 || checkpoint.fields() == null
        || checkpoint.penalties() == null || checkpoint.couplings() == null
        || checkpoint.fields().length < 1 || checkpoint.fields().length > TernaryBoltzmannTrainer.MAX_NODES) {
      throw new IOException("Unsupported or incomplete network checkpoint");
    }
    try {
      return new TrainedTernaryNetwork(new TernarySpinModel(checkpoint.couplings(),
          checkpoint.penalties(), checkpoint.fields()), checkpoint.beta());
    } catch (IllegalArgumentException | NullPointerException invalid) {
      throw new IOException("Invalid network checkpoint parameters", invalid);
    }
  }

  private record Checkpoint(int formatVersion, double beta, double[][] couplings,
      double[] penalties, double[] fields) {}
}
