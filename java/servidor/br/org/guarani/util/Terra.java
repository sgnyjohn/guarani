/**
	signey ago/2001
	//baseado em http://www.vsv.slu.se/johnb/java/lat-long.htm
	//JavaScript © 1997 by John A. Byers, Chemical Ecology, Dept. Crop Science 
*/

package br.org.guarani.util;


public class Terra {
	//raio da terra em KM 
	double er = 6366.707;
	//ave. radius = 6371.315 (someone said more accurate is 6366.707)
	//equatorial radius = 6378.388
	//nautical mile = 1.15078 (ERRADO)!!
	double milhas = 1.609344; //milhas
	double milhasn = 1.852; //milhas náuticas
	//1.85199999448581 - um minuto na vertical = milha náutica...
	//1.85199999448581 - 1 minuno no equador na horizontal idem...
	// perimetro terra em minutos = 360*60 = 21600
	// perimetro em km = 21600 * 1,852 = 40003,2 km
	// logo raio (2*pi*r) = 40003,2/pi/2 = 6366,707019
	// milha náutica 40000/21600 = 1,851851851851...
	//Porto Alegre&lat1=-30.05333&lon1=-51.32
	//São Leopoldo=lat2=-30.22667&lon2=-51.24
	//http://geonames.usgs.gov/gnisftp.html
 
	//abaixo tabela com longitude fixa e lat variando de 10 em 10
	/*km0=1.85199999448581 (equador)
			km10=1.8238639510191146
			km20=1.7403107272143883
			km30=1.6038790430226044
			km40=1.4187143037634216
			km50=1.1904426454661552
			km60=0.92599999722429
			km70=0.6334212960747294
			km80=0.3215964339569203
			km90=0.0
	*/

	Ponto origem;
 
	public Terra(long la,long lo) {
		origem = new Ponto(la,lo);
	}

	public double distancia(long la,long lo) {
		Ponto destino = new Ponto(la,lo);
  
		//sj double radlat1 = Math.PI * (td[nq] + tm[nq]/60 + ts[nq]/3600)/180;
		double radlat1 = Math.PI * origem.latDecimal()/180;
		//sj double radlat2 = Math.PI * (td[nj] + tm[nj]/60 + ts[nj]/3600)/180;
		double radlat2 = Math.PI * destino.latDecimal()/180;
		//now long.
		//sj double radlong1 = Math.PI * (gd[nq] + gm[nq]/60 + gs[nq]/3600)/180;
		double radlong1 = Math.PI * origem.longDecimal()/180;
		//sj double radlong2 = Math.PI * (gd[nj] + gm[nj]/60 + gs[nj]/3600)/180;
		double radlong2 = Math.PI * destino.longDecimal()/180;
		//spherical coordinates x=r*cos(ag)sin(at), y=r*sin(ag)*sin(at), z=r*cos(at)
		//zero ag is up so reverse lat
  
		if (origem.hemisferio()=="N") radlat1=Math.PI/2-radlat1;
		if (origem.hemisferio()=="S") radlat1=Math.PI/2+radlat1;
		if (origem.pos()=="W") radlong1=Math.PI*2-radlong1;

		if (destino.hemisferio()=="N") radlat2=Math.PI/2-radlat2;
		if (destino.hemisferio()=="S") radlat2=Math.PI/2+radlat2;
		if (destino.pos()=="W") radlong2=Math.PI*2-radlong2;

		double x1 = er * Math.cos(radlong1)*Math.sin(radlat1);
		double y1 = er * Math.sin(radlong1)*Math.sin(radlat1);
		double z1 = er * Math.cos(radlat1);

		double x2 = er * Math.cos(radlong2)*Math.sin(radlat2);
		double y2 = er * Math.sin(radlong2)*Math.sin(radlat2);
		double z2 = er * Math.cos(radlat2);

		double d = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)+(z1-z2)*(z1-z2));

		//side, side, side, law of cosines and arccos
		double theta = Math.acos((er*er+er*er-d*d)/(2*er*er));
		double distance = theta*er;
		return distance;

	}

	public class Ponto {
		private int lat[] = new int[3];
		private String latH;
		private int lon[] = new int[3];
		private String lonP;
		/*Lat: for 23°45.6'N type 23.456 (use negative values for South) 
				Lon: for 53°35.2'W type 53.352 (use negative values for East)
		*/
  
		public Ponto(long la,long lo) {
			String s;
			latH = "N";
			if (la<0) {
				latH = "S";
				la *= -1;
			}
			s = "000000"+la;
			s = s.substring(s.length()-6,s.length());
			lat[0] = Integer.parseInt(s.substring(0,2));
			lat[1] = Integer.parseInt(s.substring(2,4));
			lat[2] = Integer.parseInt(s.substring(4,6));
   
			lonP = "E";
			if (lo<0) {
				lo *= -1;
				lonP = "W";
			}
			s = "000000"+lo;
			s = s.substring(s.length()-6,s.length());
			lon[0] = Integer.parseInt(s.substring(0,2));
			lon[1] = Integer.parseInt(s.substring(2,4));
			lon[2] = Integer.parseInt(s.substring(4,6));
		}
		public String hemisferio() {
			return latH;
		}
		public String pos() {
			return lonP;
		}
		public double longDecimal() {
			return lon[0]+lon[1]/((double)60.0)+lon[2]/((double)3600.0);
		}
		public double latDecimal() {
			return lat[0]+lat[1]/((double)60.0)+lat[2]/((double)3600.0);
		}
	}
}
