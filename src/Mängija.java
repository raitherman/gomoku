/*
 * Paaristöö. 1
 * Rait Herman
 * Jaan Kaasik
 * */

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class Mängija {
	//igal mängijal on massiiv väljadest. 
	private int[][] käigud = new int[Gomoku.MÕÕTMED][Gomoku.MÕÕTMED];
	private Color värv;
	private int tulemus = 0;
	private String nimetus;

	public Mängija(Color värv, String nimetus) {
		this.värv = värv;
		this.nimetus = nimetus;
	}
	//mängija käigud väljakul
	public int[][] getKäigud() {
		return käigud;
	}
	public void suurendaTulemus() {
		tulemus += 1;
	}
	public void algSeis() {
	    käigud = new int[Gomoku.MÕÕTMED][Gomoku.MÕÕTMED];
	}
	public Color getVärv() {
		return värv;
	}
	//kui mängija teeb käigu, siis märgime tema käikude massiivis selle koha peale 1
	public void teeKäik(Gomoku.Lahter lahter, ObservableList<Node> lapsed) {
		käigud[lahter.getYKoord()][lahter.getXKoord()] = 1;
		//joonistame nupu
		double laius = lahter.getWidth() / 2;
        double kõrgus = lahter.getHeight() / 2;
        Ellipse ellipse = new Ellipse( laius, kõrgus, laius - 3, kõrgus - 3);
        ellipse.setFill(värv);
		lapsed.add(ellipse);
	}
	public String toString() {
		return nimetus;
	}
	public String getTulemus() {
		return nimetus + ": " + tulemus;
	}
	//peameetod, mis kontrollib kas mängija on võitnud
	public boolean kasMängijaVõitnud(int i, int j) {
    	//kontrollitav käikude massiiv vastavalt aktiivsele mängijale
        int[] hetkeveerg = new int[Gomoku.MÕÕTMED];
        for (int k = 0; k < Gomoku.MÕÕTMED; k++) {
            hetkeveerg[k] = käigud[k][i];
        }
        //kui üks tingimustest siis võitnud
        return onVertikaalselt(hetkeveerg, j)
        	|| onHorisontaalselt(käigud[j], i)
        	|| onDiagonaal1(käigud, j, i)
        	|| onDiagonaal2(käigud, j, i);
    }
	public boolean onDiagonaal2(int[][] väljak, int x, int y){
        boolean tõeväärtus = false;
        for (int i = -4; i < 1; i++) {
            if(x+i>-1 && y-i<Gomoku.MÕÕTMED && x+i+4<Gomoku.MÕÕTMED && y-i-4>-1){
                if (kasVõrduvad(väljak[x+i][y-i], väljak[x+i+1][y-i-1],väljak[x+i+2][y-i-2],väljak[x+i+3][y-i-3],väljak[x+i+4][y-i-4])){
                    tõeväärtus = true;
                }
            }
        }
        return tõeväärtus;
    }
	public boolean kasVõrduvad(int a, int b, int c, int d, int e) {
		return a == 1 && a == b && b == c && c == d && d == e;
    }
	public boolean onHorisontaalselt(int[] rida, int mitmeselement) {
        boolean tõeväärtus = false;
        for (int i = -4; i < 1; i++) {
            if (mitmeselement+i>-1 && mitmeselement+i+4<Gomoku.MÕÕTMED){
                if (kasVõrduvad(rida[mitmeselement + i], rida[mitmeselement + i + 1], rida[mitmeselement + i + 2], rida[mitmeselement + i + 3], rida[mitmeselement + i + 4])) {
                    tõeväärtus = true;
                }
                
            }
        }
        return tõeväärtus;
    }
    public boolean onVertikaalselt(int[] veerg, int mitmeselement) {
        boolean tõeväärtus = false;
        for (int i = -4; i < 1; i++) {
            if (mitmeselement+i>-1 && mitmeselement+i+4<Gomoku.MÕÕTMED){
                if (kasVõrduvad(veerg[mitmeselement + i], veerg[mitmeselement + i + 1], veerg[mitmeselement + i + 2], veerg[mitmeselement + i + 3], veerg [mitmeselement + i + 4])) {
                    tõeväärtus = true;
                }
            }
        }
        return tõeväärtus;
    }
    public boolean onDiagonaal1(int[][] väljak, int x, int y){
        boolean tõeväärtus = false;
        for (int i = -4; i < 1; i++) {
            if (x+i>-1 && y+i>-1 && x+i+4<Gomoku.MÕÕTMED && y+i+4<Gomoku.MÕÕTMED){
                if (kasVõrduvad(väljak[x+i][y+i], väljak[x+i+1][y+i+1],väljak[x+i+2][y+i+2],väljak[x+i+3][y+i+3],väljak[x+i+4][y+i+4])) {
                    tõeväärtus = true;
                }
            }}
        return tõeväärtus;
    }
}