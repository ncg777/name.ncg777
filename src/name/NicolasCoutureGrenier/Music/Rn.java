package name.NicolasCoutureGrenier.Music;

/**
 * The {@code Rn} enum defines the different numeral systems used for representing rhythms 
 * in the context of music processing. It provides a clear enumeration of the available 
 * formats that can be utilized when dealing with rhythmic patterns.
 *
 * <p>This can help in determining how to parse and display rhythm representations in 
 * various formats, facilitating conversions and ensuring consistency across different 
 * rhythm-handling functionalities.</p>
 *
 * The available numeral systems are:
 * <ul>
 * <li>{@link #Hex}: Represents a rhythm in standard hexadecimal format, for example "CA FE".</li>
 * <li>{@link #Octal}: Represents a rhythm in octal format, for example "77 77".</li>
 * <li>{@link #Tribble}: Represents a rhythm in tribble format, using a 
 * hexadecimal representation, for example "924 924 924 888".</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * Rn rhythmFormat = Rn.Hex;
 * </pre>
 */
public enum Rn {
  Hex,
  Octal,
  Tribble
}
