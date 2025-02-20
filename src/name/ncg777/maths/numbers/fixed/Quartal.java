package name.ncg777.maths.numbers.fixed;

import java.util.List;

import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.sequences.Sequence;

public class Quartal extends FixedLength {
  public Quartal() {super(4);};
  public static Quartal instance = new Quartal();
  
  @Override
  public Class<? extends Natural> getNatural() {return instance.getNatural();}
  @Override
  public NaturalSequence newNaturalSequence(Cipher.Name alphabetName) {
    return new NaturalSequence(alphabetName);
  }
  public NaturalSequence newNaturalSequence(NaturalSequence seq) {
    return new NaturalSequence(seq);
  }
  public NaturalSequence newNaturalSequence(Cipher.Name alphabetName, String string) {
    return new NaturalSequence(alphabetName,string);
  }
  
  public NaturalSequence newNaturalSequence(Cipher.Name alphabetName, name.ncg777.maths.numbers.Natural natural) {
    return new NaturalSequence(alphabetName,natural);
  }
  
  public NaturalSequence newNaturalSequence(Cipher.Name alphabetName, BinaryNatural binaryNatural) {
    return new NaturalSequence(alphabetName,binaryNatural);
  }
  public NaturalSequence newNaturalSequence(FixedLength.NaturalSequence seq) {
    if(seq.getL()!=4) throw new RuntimeException();
    return new NaturalSequence(seq);
  }
  
  public NaturalSequence expand(NaturalSequence a, int x, boolean fill) {
    return expand(a, x, fill, null);
  }
  public NaturalSequence expand(NaturalSequence a, int x, boolean fill, 
      List<name.ncg777.maths.numbers.fixed.FixedLength.NaturalSequence> _patterns) {
    return Quartal.instance.newNaturalSequence(FixedLength.NaturalSequence.expand(a, x, fill, _patterns));
  }
  
  public NaturalSequence rotate(NaturalSequence r, int t) {
    return Quartal.instance.newNaturalSequence(FixedLength.NaturalSequence.rotate(r, t));
  }

  public NaturalSequence not(NaturalSequence a) {
    return Quartal.instance.newNaturalSequence(FixedLength.NaturalSequence.not(a));
  }

  public NaturalSequence and(NaturalSequence a, NaturalSequence b) {
    return Quartal.instance.newNaturalSequence(FixedLength.NaturalSequence.and(a, b));
  }

  public NaturalSequence or(NaturalSequence a, NaturalSequence b) {
    return Quartal.instance.newNaturalSequence(FixedLength.NaturalSequence.or(a, b));
  }

  public NaturalSequence xor(NaturalSequence a, NaturalSequence b) {
    return Quartal.instance.newNaturalSequence(FixedLength.NaturalSequence.xor(a, b));
  }

  public NaturalSequence minus(NaturalSequence a, NaturalSequence b) {
    return Quartal.instance.newNaturalSequence(FixedLength.NaturalSequence.minus(a, b));
  }

  public NaturalSequence convolve(NaturalSequence a, NaturalSequence b) {
    return Quartal.instance.newNaturalSequence(FixedLength.NaturalSequence.convolve(a, b));
  }

  public boolean isEquivalentUnderSyncronizedRotation(NaturalSequence other) {
    return this.isEquivalentUnderSyncronizedRotation(other);
  }
  @Override
  public Natural newNatural(FixedLength.Natural nat) {
    return new Natural(nat);
  }
  public Natural newNatural(name.ncg777.maths.numbers.Natural nat) {
    return new Natural(nat);
  }
  @Override
  public Natural newNatural(Cipher.Name alphabetName, Character[] array) {
    return new Natural(alphabetName,array);
  }
  @Override
  public Natural newNatural(Cipher.Name alphabetName, String string) {
    return new Natural(alphabetName,string);
  }
  @Override
  public Natural newNatural(Cipher.Name alphabetName, List<Character> list) {
    return new Natural(alphabetName,list);
  }
  @Override
  public Natural newNatural(Cipher.Name alphabetName, Sequence sequence) {
    return new Natural(alphabetName,sequence);
  }
  
  @Override
  public Class<? extends NaturalSequence> getNaturalSequence() {return instance.getNaturalSequence();}
  
  public class Natural extends FixedLength.Natural {
    public int getL() {return 4;}

    public FixedLength getFixedLength() {return Quartal.instance;}
    private static final long serialVersionUID = 1L;
    
    public Natural(FixedLength.Natural nat) {
      super(nat);
      if(nat.getL()!= 4) throw new RuntimeException();
    }
    public Natural(Cipher.Name alphabetName, Character[] array) {
      super(alphabetName, array);
    }
    
    public Natural(Cipher.Name alphabetName, String string) {
      super(alphabetName, string);
    }
    
    public Natural(Cipher.Name alphabetName, List<Character> list) {
      super(alphabetName, list);
    }
    
    public Natural(Cipher.Name alphabetName, Sequence sequence) {
      super(alphabetName, sequence);
    }
   
    public Natural(Natural natural) {
      super(natural);
    }
    public Natural(name.ncg777.maths.numbers.Natural nat) {
      super(nat);
    }
    
    @Override
    public String toString() {
      return this.toString(true);
    }
    
    public int compareTo(Quartal.Natural o) {
      if(o.getL()!=this.getL()) {
        return this.getL()-o.getL();
      }
      return this.toSequence().compareTo(o.toSequence());
    }
  }
  
  public class NaturalSequence extends FixedLength.NaturalSequence {
    private static final long serialVersionUID = 1L;
    public int getL() {return 4;}
    
    
    public NaturalSequence(Cipher.Name alphabetName) {
      super(alphabetName);
    }
    public NaturalSequence(NaturalSequence seq) {
      super(seq);
    }

    public NaturalSequence(Cipher.Name alphabetName, String string) {
      super(alphabetName, string);
    }
    
    public NaturalSequence(Cipher.Name alphabetName, name.ncg777.maths.numbers.Natural natural) {
      super(alphabetName, natural);
    }
    
    public NaturalSequence(Cipher.Name alphabetName, BinaryNatural binaryNatural) {
      super(alphabetName, binaryNatural);
    }
    public FixedLength getFixedLength() {return Quartal.instance;}
    public NaturalSequence(FixedLength.NaturalSequence seq) {
      super(seq);
      if(seq.getL()!=4) throw new RuntimeException();
    }
    
    
    
    
    
  }
}
