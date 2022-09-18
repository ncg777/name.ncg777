import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class GISPoints {
  static public void main(String[] args){
    GeometryFactory gf = new GeometryFactory();
    Coordinate[] ct = {new Coordinate(-70.77982068519604,47.71330675603625),
                       new Coordinate(-75.54352782725444,45.28088409560937),
                       new Coordinate(-71.85293594393028,45.18389889800874),
                       new Coordinate(-70.77982068519604,47.71330675603625)};
    Polygon p = gf.createPolygon(ct);
    
    String f = 
        "<Placemark>" +
            "    <name>%s</name>" +
            "    <LookAt>" +
            "        <longitude>%f</longitude>" +
            "        <latitude>%f</latitude>" +
            "        <altitude>0</altitude>" +
            "        <heading>0.0003481013503525941</heading>" +
            "        <tilt>37.38885472819713</tilt>" +
            "        <range>1923.388559007609</range>" +
            "        <gx:altitudeMode>relativeToSeaFloor</gx:altitudeMode>" +
            "    </LookAt>" +
            "    <styleUrl>#m_ylw-pushpin</styleUrl>" +
            "    <Point>" +
            "        <gx:drawOrder>1</gx:drawOrder>" +
            "        <coordinates>%s,0</coordinates>" +
            "    </Point>" +
            "</Placemark>";
    
    int n = 100;
    double minLong = Double.MAX_VALUE;
    double maxLong = Double.MIN_VALUE;
    double minLat = Double.MAX_VALUE;
    double maxLat = Double.MIN_VALUE;
    for(int i=0;i<ct.length;i++){
      if(ct[i].x < minLong){minLong = ct[i].x;}
      if(ct[i].x > maxLong){maxLong = ct[i].x;}
      if(ct[i].y < minLat){minLat = ct[i].y;}
      if(ct[i].y > maxLat){maxLat = ct[i].y;}
      
    }
    
    int i = 0;
    while(i<n){
      double lon = minLong + Math.random()*(maxLong-minLong);
      double lat = minLat + Math.random()*(maxLat-minLat);
      
      if(p.contains(gf.createPoint(new Coordinate(lon, lat)))){
        i++;
        System.out.println(String.format(f, "P" +  i, lon, lat,Double.toString(lon) + "," + Double.toString(lat)));
      }
    }
    
  }
}
