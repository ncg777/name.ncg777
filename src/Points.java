import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.SphericalCoordinates;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

public class Points {

  static private Set<Vector3D> iterateTransformation(
         Vector3D P,
         Function<Set<Vector3D>,
         Set<Vector3D>> T, int n){
    Set<Vector3D> t = new HashSet<Vector3D>();
    t.add(P);
    return iterateTransformation(t,T,n);
  }
  
  static private Set<Vector3D> iterateTransformation(Set<Vector3D> s,
                      Function<Set<Vector3D>,Set<Vector3D>> T, int n){
    Set<Vector3D> t = new HashSet<Vector3D>();
    t.addAll(s);
    for(int i=0;i<n;i++){
      t.addAll(T.apply(t));
    }
    
    return t;
   
  }
  
  static private class EquidistantPointsOnConeSphereIntersection 
    implements Function<Set<Vector3D>,Set<Vector3D>>{
    private int d;
    private int t;
    public EquidistantPointsOnConeSphereIntersection(int d, int t){this.d=d;this.t=t;}
    @Override
    public Set<Vector3D> apply(Set<Vector3D> s) {
      HashSet<Vector3D> points = new HashSet<Vector3D>();
      for(Vector3D v : s){
        points.addAll(apply(v));
      }
      return points;
    }
    
    public Set<Vector3D> apply(Vector3D p) {
      HashSet<Vector3D> points = new HashSet<Vector3D>();
      SphericalCoordinates p_spherical = new SphericalCoordinates(p);
      
      Rotation r0 = new Rotation(p, 
        (FastMath.PI*2.0)/(Integer.valueOf(t).doubleValue()));
      
      Vector3D p0 = new SphericalCoordinates(1,
        p_spherical.getTheta(),
        p_spherical.getPhi()+FastMath.PI/(Integer.valueOf(d).doubleValue()))
          .getCartesian();
      
      for(int i=0;i<t;i++){
        points.add(p0);
        p0 = r0.applyTo(p0);
      }
      points.add(p);
      return points;
    }
    
    
  }
  static <U> U readInput(Function<Scanner, U> f, String prompt){
    System.out.print(prompt);
    U o = null;
    
    try {
      BufferedReader bufferedReader = 
          new BufferedReader(new InputStreamReader(System.in));
      String line = bufferedReader.readLine();
      Scanner s = new Scanner(line.trim());
      o = f.apply(s);
      
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    
    return o;
    
  }
  private static Function<Scanner, Fraction> fractionParser = 
      new Function<Scanner,Fraction>(){

        @Override
        public Fraction apply(Scanner s) {
          s.useDelimiter("/");
          Fraction o = new Fraction(s.nextInt(),s.nextInt());
          s.close();
          return o;
        }
    
  };
  
  private static Function<Scanner, Integer> intParser = 
      new Function<Scanner,Integer>(){
        @Override
        public Integer apply(Scanner s) {
          int o = s.nextInt();
          s.close();
          return o;
        }
    
  };
  
  private static Function<Scanner, Double> doubleParser = 
      new Function<Scanner,Double>(){
        @Override
        public Double apply(Scanner s) {
          double o = s.nextDouble();
          s.close();
          return o;
        }
    
  };
  
  static public void main(String[] args) throws FileNotFoundException{
    // Point P on the unit sphere
    double P_theta = readInput(doubleParser, "P theta = ");
    double P_phi = readInput(doubleParser, "P phi = ");
    // PI/d is the spherical distance from point P 
    Integer d = readInput(intParser, "d = ");
    // PI/t is the rotation angle around OP axis
    Integer t = readInput(intParser, "t = ");
    // n is the number of iterations
    Integer n = readInput(intParser, "n = ");
    
    Vector3D P = new SphericalCoordinates(1,
      P_theta*FastMath.PI,
      P_phi*FastMath.PI).getCartesian();
    
    EquidistantPointsOnConeSphereIntersection T = new 
        EquidistantPointsOnConeSphereIntersection(d, t);
    
    Set<Vector3D> s = iterateTransformation(P,T,n);
    
    PrintWriter pw = new PrintWriter("D:/test.dat");
    pw.println("");
    
    for(Vector3D v : s){
      
      pw.println(String.format("%f %f %f",v.getX(),v.getY(),v.getZ()));
      
    }
    pw.close();
  }
}
