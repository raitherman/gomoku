/*
 * Paaristöö. 1
 * Rait Herman
 * Jaan Kaasik
 * */

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class Mängija {
    //igal mängijal on massiiv väljadest.
	private int[][] käigud = new int[Gomoku.mõõtmed][Gomoku.mõõtmed];
	private Color värv;
	private int tulemus = 0;
    private String nimetus;
    private boolean kasArvuti = false;

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
    public void algSeis(boolean kasArvuti) {
    	this.käigud = new int[Gomoku.mõõtmed][Gomoku.mõõtmed];
        this.kasArvuti = kasArvuti;
    }
    public Color getVärv() {
        return värv;
    }
    public String toString() {
        return nimetus;
    }

    public String getTulemus() {
        return nimetus + ": " + tulemus;
    }
    public Label getTulemusLabel() {
    	Label tulemus = new Label(this.getTulemus());
    	tulemus.setFont(new Font("Arial", 30));
        return tulemus;
    }

    public boolean isKasArvuti() {
		return kasArvuti;
	}
	public void setKasArvuti(boolean kasArvuti) {
		this.kasArvuti = kasArvuti;
	}
	//peameetod, mis kontrollib kas mängija on võitnud
    public boolean kasMängijaVõitnud(int i, int j) {
        //kontrollitav käikude massiiv vastavalt aktiivsele mängijale
        int[] hetkeveerg = new int[Gomoku.mõõtmed];
        for (int k = 0; k < Gomoku.mõõtmed; k++) {
            hetkeveerg[k] = käigud[k][i];
        }
        //kui üks tingimustest siis võitnud
        return onVertikaalselt_5(hetkeveerg, j)
                || onHorisontaalselt_5(käigud[j], i)
                || onDiagonaal1_5(käigud, j, i)
                || onDiagonaal2_5(käigud, j, i);
    }

    public boolean onDiagonaal2_5(int[][] väljak, int x, int y) {
        boolean tõeväärtus = false;
        for (int i = -4; i < 1; i++) {
            if (x + i > -1 && y - i < Gomoku.mõõtmed && x + i + 4 < Gomoku.mõõtmed && y - i - 4 > -1) {
                if (kasVõrduvad(väljak[x + i][y - i], väljak[x + i + 1][y - i - 1], väljak[x + i + 2][y - i - 2], väljak[x + i + 3][y - i - 3], väljak[x + i + 4][y - i - 4])) {
                    tõeväärtus = true;
                }
            }
        }
        return tõeväärtus;
    }

    public boolean kasVõrduvad(int a, int b, int c, int d, int e) {
        return a == 1 && a == b && b == c && c == d && d == e;
    }

    public boolean onHorisontaalselt_5(int[] rida, int mitmeselement) {
        boolean tõeväärtus = false;
        for (int i = -4; i < 1; i++) {
            if (mitmeselement + i > -1 && mitmeselement + i + 4 < Gomoku.mõõtmed) {
                if (kasVõrduvad(rida[mitmeselement + i], rida[mitmeselement + i + 1], rida[mitmeselement + i + 2], rida[mitmeselement + i + 3], rida[mitmeselement + i + 4])) {
                    tõeväärtus = true;
                }

            }
        }
        return tõeväärtus;
    }

    public boolean onVertikaalselt_5(int[] veerg, int mitmeselement) {
        boolean tõeväärtus = false;
        for (int i = -4; i < 1; i++) {
            if (mitmeselement + i > -1 && mitmeselement + i + 4 < Gomoku.mõõtmed) {
                if (kasVõrduvad(veerg[mitmeselement + i], veerg[mitmeselement + i + 1], veerg[mitmeselement + i + 2], veerg[mitmeselement + i + 3], veerg[mitmeselement + i + 4])) {
                    tõeväärtus = true;
                }
            }
        }
        return tõeväärtus;
    }

    public boolean onDiagonaal1_5(int[][] väljak, int x, int y) {
        boolean tõeväärtus = false;
        for (int i = -4; i < 1; i++) {
            if (x + i > -1 && y + i > -1 && x + i + 4 < Gomoku.mõõtmed && y + i + 4 < Gomoku.mõõtmed) {
                if (kasVõrduvad(väljak[x + i][y + i], väljak[x + i + 1][y + i + 1], väljak[x + i + 2][y + i + 2], väljak[x + i + 3][y + i + 3], väljak[x + i + 4][y + i + 4])) {
                    tõeväärtus = true;
                }
            }
        }
        return tõeväärtus;
    }

    public boolean onnupp(int[][] väljak) {
        boolean onnupp = false;
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (väljak[i][j] == 1) {
                    onnupp = true;
                }
            }
        }
        return onnupp;
    }

    public List<Koordinaadid> onDiagonaal1_2(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 1 < Gomoku.mõõtmed && j + 1 < Gomoku.mõõtmed) {
                    if (1 == väljak[i][j] && väljak[i][j] == väljak[i + 1][j + 1]) {
                        if(i-1>0 && j-1>0){
                        Koordinaadid uus = new Koordinaadid(i-1, j-1);
                            koordinaadid.add(uus);}
                        if(i+2<Gomoku.mõõtmed && j+2<Gomoku.mõõtmed){
                            Koordinaadid uus1 = new Koordinaadid(i+1, j+1);
                            koordinaadid.add(uus1);}
                    }
                }
            }
        }
        return koordinaadid;
    }

    public List<Koordinaadid> onDiagonaal1_3(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 2 < Gomoku.mõõtmed && j + 2 < Gomoku.mõõtmed) {
                    if (1 == väljak[i][j] && 1 == väljak[i + 1][j + 1] && 1 == väljak[i + 2][j + 2]) {
                        if(i-1>0 && j-1>0){
                            Koordinaadid uus = new Koordinaadid(i-1, j-1);
                            koordinaadid.add(uus);}
                        if(i+3<Gomoku.mõõtmed && j+3<Gomoku.mõõtmed){
                            Koordinaadid uus1 = new Koordinaadid(i+3, j+3);
                            koordinaadid.add(uus1);}
                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onDiagonaal1_4(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 3 < Gomoku.mõõtmed && j + 3 < Gomoku.mõõtmed) {
                    if (1 == väljak[i][j] && 1 == väljak[i + 1][j + 1] && 1 == väljak[i + 2][j + 2] && 1 == väljak[i + 3][j + 3]) {
                        if(i-1>0 && j-1>0){
                            Koordinaadid uus = new Koordinaadid(i-1, j-1);
                            koordinaadid.add(uus);}
                        if(i+4<Gomoku.mõõtmed && j+4<Gomoku.mõõtmed){
                            Koordinaadid uus1 = new Koordinaadid(i+1, j+1);
                            koordinaadid.add(uus1);}
                    }
                }
            }
        }
        return koordinaadid;
    }

    public List<Koordinaadid> onDiagonaal2_2(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 1 < Gomoku.mõõtmed && j - 1 > 0) {
                    if (1 == väljak[i][j] && väljak[i][j] == väljak[i + 1][j - 1]) {
                        if(i-1>0 && j+1<Gomoku.mõõtmed){
                            Koordinaadid uus = new Koordinaadid(i-1, j+1);
                            koordinaadid.add(uus);}
                        if(i+2<Gomoku.mõõtmed && j-2>0){
                            Koordinaadid uus1 = new Koordinaadid(i+2, j-2);
                            koordinaadid.add(uus1);}
                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onDiagonaal2_3(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 2 < Gomoku.mõõtmed && j - 2 > 0) {
                    if (1 == väljak[i][j] && 1 == väljak[i + 1][j - 1] && 1 == väljak[i + 2][j - 2]) {
                        if(i-1>0 && j+1<Gomoku.mõõtmed){
                            Koordinaadid uus = new Koordinaadid(i-1, j+1);
                            koordinaadid.add(uus);}
                        if(i+3<Gomoku.mõõtmed && j-3>0){
                            Koordinaadid uus1 = new Koordinaadid(i+3, j-3);
                            koordinaadid.add(uus1);}
                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onDiagonaal2_4(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 3 < Gomoku.mõõtmed && j - 3 > 0) {
                    if (1 == väljak[i][j] && 1 == väljak[i + 1][j - 1] && 1 == väljak[i + 2][j - 2] && 1 == väljak[i + 3][j - 3]) {
                        if(i-1>0 && j+1<Gomoku.mõõtmed){
                            Koordinaadid uus = new Koordinaadid(i-1, j+1);
                            koordinaadid.add(uus);}
                        if(i+4<Gomoku.mõõtmed && j-4>0){
                            Koordinaadid uus1 = new Koordinaadid(i+4, j-4);
                            koordinaadid.add(uus1);}
                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onHorisontaalselt_2(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 1 < Gomoku.mõõtmed && j < Gomoku.mõõtmed) {
                    if (1 == väljak[i][j] && väljak[i][j] == väljak[i + 1][j]) {
                        if(i-1>0){
                        Koordinaadid uus = new Koordinaadid(i-1, j);
                            koordinaadid.add(uus);}
                            if(i+2<Gomoku.mõõtmed) {
                                Koordinaadid uus1 = new Koordinaadid(i + 2, j);
                                koordinaadid.add(uus1);
                            }

                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onHorisontaalselt_3(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 2 < Gomoku.mõõtmed && j < Gomoku.mõõtmed) {
                    if (1 == väljak[i][j] && 1 == väljak[i + 1][j] && 1 == väljak[i + 2][j]) {
                        if(i-1>0){
                            Koordinaadid uus = new Koordinaadid(i-1, j);
                            koordinaadid.add(uus);}
                        if(i+3<Gomoku.mõõtmed) {
                            Koordinaadid uus1 = new Koordinaadid(i + 3, j);
                            koordinaadid.add(uus1);
                        }
                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onHorisontaalselt_4() {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i + 3 < Gomoku.mõõtmed && j < Gomoku.mõõtmed) {
                    if (1 == käigud[i][j] && 1 == käigud[i + 1][j] && 1 == käigud[i + 2][j] && 1 == käigud[i + 3][j]) {
                        if(i-1>0){
                            Koordinaadid uus = new Koordinaadid(i-1, j);
                            koordinaadid.add(uus);}
                        if(i+4<Gomoku.mõõtmed) {
                            Koordinaadid uus1 = new Koordinaadid(i + 4, j);
                            koordinaadid.add(uus1);
                        }
                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onVertikaalselt_2(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i< Gomoku.mõõtmed && j + 1<Gomoku.mõõtmed) {
                    if (1 == väljak[i][j] && väljak[i][j] == väljak[i][j+1]) {
                        if(j-1>0){
                            Koordinaadid uus = new Koordinaadid(i, j-1);
                            koordinaadid.add(uus);}
                        if(j+2<Gomoku.mõõtmed) {
                            Koordinaadid uus1 = new Koordinaadid(i, j+2);
                            koordinaadid.add(uus1);
                        }
                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onVertikaalselt_3(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i< Gomoku.mõõtmed && j + 2<Gomoku.mõõtmed) {
                    if (1 == väljak[i][j] && 1 == väljak[i][j+1] && 1 == väljak[i][j+2]) {
                        if(j-1>0){
                            Koordinaadid uus = new Koordinaadid(i, j-1);
                            koordinaadid.add(uus);}
                        if(j+2<Gomoku.mõõtmed) {
                            Koordinaadid uus1 = new Koordinaadid(i, j+3);
                            koordinaadid.add(uus1);
                        }
                    }
                }
            }
        }
        return koordinaadid;
    }
    public List<Koordinaadid> onVertikaalselt_4(int[][] väljak) {
        List<Koordinaadid> koordinaadid = new ArrayList<>();
        for (int i = 0; i < Gomoku.mõõtmed; i++) {
            for (int j = 0; j < Gomoku.mõõtmed; j++) {
                if (i< Gomoku.mõõtmed && j + 3<Gomoku.mõõtmed) {
                    if (1 == väljak[i][j] && 1 == väljak[i][j+1] && 1 == väljak[i][j+2] && 1 == väljak[i][j+3]) {
                        if(j-1>0){
                            Koordinaadid uus = new Koordinaadid(i, j-1);
                            koordinaadid.add(uus);}
                        if(j+4<Gomoku.mõõtmed) {
                            Koordinaadid uus1 = new Koordinaadid(i, j+4);
                            koordinaadid.add(uus1);
                        }
                    }
                }
            }
        }
        return koordinaadid;
    }

}