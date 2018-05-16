/*
 * Paaristöö. 1
 * Rait Herman
 * Jaan Kaasik
 * */

import java.io.File;
import java.util.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gomoku extends Application {
    //muutuja seadmaks mänguvälja suurust
    public static int mõõtmed = 18;
    //private static final int INIMENE_VS_INIMENE = -1;
    //private static final int ARVUTI_VS_INIMENE = 1;
    //int mängutüüp;
    Map<Koordinaadid, Lahter> lahtrid;
    //-1 kui inimesed omavahel, 1 kui arvuti ka mängib.
    private Mängija punaneMängija = new Mängija(Color.RED, "Punane");
    private Mängija sinineMängija = new Mängija(Color.BLUE, "Sinine");
    private Mängija aktiivneMängija = punaneMängija;
    private Label teavitus = new Label();
    private BorderPane välineAken = new BorderPane();
    private GridPane mänguväli = new GridPane();
    private VBox vbUusMäng = new VBox();
    private Map<Koordinaadid, Lahter> väljad = new HashMap<Koordinaadid, Lahter>();

    public static void main(String[] args) {
        launch(args);
    }

    //mängu algseis. Loome uue mänguvälja ning eemaldame mängija käigud
  //mängu algseis. Loome uue mänguvälja ning eemaldame mängija käigud
    public void algSeis(int mõõtmed, int mänguTüüp, Mängija aktiivneMängija) {
    	mänguväli.getChildren().clear();
    	Gomoku.mõõtmed = mõõtmed;
        for (int i = 0; i < mõõtmed; i++) {
            for (int j = 0; j < mõõtmed; j++) {
                Lahter lahter = new Lahter(new Koordinaadid(i, j));
                mänguväli.add(lahter, j, i);
                //lisame kõik väljad ka listi, kust saame kontrollida kas väli on täis või mitte
                väljad.put(lahter.getKoordinaadid(), lahter);
            }
        }
        punaneMängija.algSeis(false);
        sinineMängija.algSeis(mänguTüüp == 1);
        this.aktiivneMängija = aktiivneMängija;
        teavitus.setText(aktiivneMängija + " alustab");
    }

    @Override
    public void start(Stage stage) throws Exception {
    	stage.setTitle("Gomoku");
        Scene stseen = new Scene(välineAken, 600, 600);

        MenuBar menüü = new MenuBar();
    	Menu menüüFail = new Menu("Fail");
    	MenuItem uusMäng = new MenuItem("Uus mäng");
    	MenuItem välju = new MenuItem("Välju");
    	uusMäng.setOnAction(e -> looUusMäng());
    	välju.setOnAction(e -> System.exit(1));
    	menüüFail.getItems().addAll(uusMäng, välju);
    	Menu menüüInfo = new Menu("Info");
    	//siia paneme mingi õpetuse vms
    	menüü.getMenus().addAll(menüüFail, menüüInfo);

    	//loome külgriba
    	VBox külgriba = new VBox();
    	külgriba.setAlignment(Pos.CENTER);
    	külgriba.setBorder(new Border(new BorderStroke(Color.BLACK, 
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        külgriba.setMinWidth(100);
        külgriba.getChildren().addAll(
    			punaneMängija.getTulemusLabel(),
    			sinineMängija.getTulemusLabel(),
    			teavitus);
    	
    	välineAken.setTop(menüü);
    	File fail = new File("src/resources/gomoku_banner.png");
		ImageView piltVaade = new ImageView(new Image(fail.toURI().toString(), mänguväli.getHeight(), mänguväli.getWidth(), true, true));
    	mänguväli.getChildren().add(piltVaade);
		ImageView nupp = new ImageView(new Image(new File("src/resources/button_0.png").toURI().toString()));
		StackPane pane = new StackPane();
        pane.getChildren().add(nupp);
        StackPane.setAlignment(nupp, Pos.CENTER);
		nupp.setOnMouseClicked(e -> looUusMäng());
		nupp.setCursor(Cursor.HAND);
		mänguväli.getChildren().add(pane);
    	välineAken.setCenter(mänguväli);
    	välineAken.setRight(külgriba);

        stage.setScene(stseen);
        
        stage.setResizable(false);
        
        //Mänguvälja suuruse määramine
    	Spinner<Integer> suurus = new Spinner<>(8, 40, 18);
    	HBox hbVäljaSuurus = new HBox(new Label("Välja suurus: "), suurus);
    	hbVäljaSuurus.setAlignment(Pos.CENTER);

    	//Mängu tüübi valimine
    	HBox hbMänguTüüp = new HBox();
    	hbMänguTüüp.setAlignment(Pos.CENTER);
    	ToggleGroup tgMänguTüüp = new ToggleGroup();
    	RadioButton rb1 = new RadioButton("Inimene vs Arvuti");
    	rb1.setToggleGroup(tgMänguTüüp);
    	rb1.setSelected(true);
    	RadioButton rb2 = new RadioButton("Inimene vs Inimene");
    	rb2.setToggleGroup(tgMänguTüüp);
    	hbMänguTüüp.getChildren().addAll(new Label("Mängu tüüp: "), rb1, rb2);
    	
    	//Alustava värvi valimine
    	HBox hbAlustaja = new HBox();
    	hbAlustaja.setAlignment(Pos.CENTER);

    	ToggleGroup tgAlustaja = new ToggleGroup();
    	RadioButton alustabPunane = new RadioButton("Punane");
    	alustabPunane.setToggleGroup(tgAlustaja);
    	alustabPunane.setSelected(true);
    	RadioButton alustabSinine = new RadioButton("Sinine");
    	alustabSinine.setToggleGroup(tgAlustaja);
    	hbAlustaja.getChildren().addAll(new Label("Alustaja: "), alustabPunane, alustabSinine);
    	
    	Button alustaMängu = new Button("Alusta mängu");
    	alustaMängu.setAlignment(Pos.CENTER);
    	alustaMängu.setOnAction(e -> algSeis(suurus.getValue(), rb1.isSelected() ? 1 : -1, alustabPunane.isSelected() ? punaneMängija : sinineMängija));
    	vbUusMäng.setAlignment(Pos.CENTER);
    	vbUusMäng.setBackground(new Background(new BackgroundFill(Color.web("#FFF"), CornerRadii.EMPTY, Insets.EMPTY)));
    	vbUusMäng.getChildren().addAll(hbVäljaSuurus, hbMänguTüüp, hbAlustaja, alustaMängu);
        stage.show();
    }
    private void looUusMäng() {
    	mänguväli.getChildren().clear();
    	mänguväli.getChildren().add(vbUusMäng);
	}
    public class Lahter extends Pane {
        private Mängija omanik;
        private Koordinaadid koordinaadid;

        public Mängija getOmanik() {
            return omanik;
        }
        public Koordinaadid getKoordinaadid() {
			return koordinaadid;
		}

		public void setKoordinaadid(Koordinaadid koordinaadid) {
			this.koordinaadid = koordinaadid;
		}

		public void setOmanik(Mängija omanik) {
            this.omanik = omanik;
            File file = new File("src/resources/" + (omanik.equals(punaneMängija) ? "punane.png" : "sinine.png"));
            Image image = new Image(file.toURI().toString(), this.getWidth(), this.getWidth(), true, true);
            ImageView iv = new ImageView(image);
            this.getChildren().add(iv);
        }

        public Lahter(Koordinaadid koordinaadid) {
            this.koordinaadid = koordinaadid;
            setStyle("-fx-border-color: black");
            this.setPrefSize(150, 150);
            this.setOnMouseClicked(e -> teeKäik());
        }

        //meetod mis loeb välja klikki
        private void teeKäik(){
            if (omanik == null) {
                setOmanik(aktiivneMängija);
                omanik.getKäigud()[this.koordinaadid.getY()][this.koordinaadid.getX()] = 1;
                //kontrollime kas aktiivne mängija on võitnud. Kui jah, siis vastav teade ja peale seda algseis.
                if (aktiivneMängija.kasMängijaVõitnud(this.koordinaadid.getX(), this.koordinaadid.getY())) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Mäng läbi");
                    alert.setContentText(aktiivneMängija.toString() + " võitis.\nJätkamiseks vajuta OK!");
                    alert.showAndWait();
                    aktiivneMängija.suurendaTulemus();
                    looUusMäng();
                } else if (kasVäliTäis()) {
                    Alert mängläbi = new Alert(AlertType.INFORMATION);
                    mängläbi.setTitle("Mäng läbi");
                    mängläbi.setContentText("Viik.\nJätkamiseks vajuta OK!");
                    mängläbi.showAndWait();
                    looUusMäng();
                } else {
                    //kui aktiivne mängija ei võitnud, siis vahetame aktiivse mängija ära.
                    aktiivneMängija = aktiivneMängija.equals(punaneMängija) ? sinineMängija : punaneMängija;
                    teavitus.setText(aktiivneMängija + " käib");
                }
                //siin käib arvuti
                if (aktiivneMängija.isKasArvuti()) {
                    {

                        // erinevatele võimalikele käiguvormidele loome omad võimalikud koordinaadid


//vaatame järjest, kuidas oleks parim käik
                    }
                    Koordinaadid pandavad;
                    while (true) {
                        pandavad = arvutikäib();
                        if (väljad.get(pandavad).getOmanik() == null) {
                            väljad.get(pandavad).setOmanik(sinineMängija);
                            break;
                        }
                    }


                    //siin kontrollib kas mäng jätkub
                    if (!sinineMängija.kasMängijaVõitnud(pandavad.getX(), pandavad.getY())) {
                        if (kasVäliTäis()) {
                            Alert mängläbi = new Alert(AlertType.INFORMATION);
                            mängläbi.setTitle("Mäng läbi");
                            mängläbi.setContentText("Viik.\nJätkamiseks vajuta OK!");
                            mängläbi.showAndWait();
                            looUusMäng();
                        } else {
                            System.out.println("vahetan mängija");
                            //kui aktiivne mängija ei võitnud, siis vahetame aktiivse mängija ära.
                            aktiivneMängija = aktiivneMängija.equals(punaneMängija) ? sinineMängija : punaneMängija;
                            teavitus.setText(aktiivneMängija + " käib");
                        }
                    } else {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Mäng läbi");
                        alert.setContentText(aktiivneMängija.toString() + " võitis.\nJätkamiseks vajuta OK!");
                        alert.showAndWait();
                        aktiivneMängija.suurendaTulemus();
                        looUusMäng();
                    }
                }
            }
        }

        //meetod kontrollib kas Väli on täis. Kui leiab välja mis ei ole täidetud, siis false
        private boolean kasVäliTäis() {
            for (Lahter väli : väljad.values()) {
                if (väli.getOmanik() == null) {
                    return false;
                }
            }
            return true;
        }
    }

    public Koordinaadid arvutikäib() {
        Koordinaadid pandavad;
        List<Koordinaadid> punasekoordinaadid4 = punaneMängija.onVertikaalselt_4();
        List<Koordinaadid> sinisekoordinaadid4 = sinineMängija.onVertikaalselt_4();
        List<Koordinaadid> punasekoordinaadid3 = punaneMängija.onVertikaalselt_3();
        List<Koordinaadid> sinisekoordinaadid3 = sinineMängija.onVertikaalselt_3();
        List<Koordinaadid> punasekoordinaadid2 = punaneMängija.onVertikaalselt_2();
        List<Koordinaadid> sinisekoordinaadid2 = sinineMängija.onVertikaalselt_2();
        List<Koordinaadid> punasekoordinaadid1 = punaneMängija.onnupp();
        List<Koordinaadid> sinisekoordinaadid1 = sinineMängija.onnupp();

        //lisame kõik meetodid
        for (Koordinaadid koordinaat : sinineMängija.onDiagonaal1_4()
                ) {
            punasekoordinaadid4.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onDiagonaal1_4()
                ) {
            punasekoordinaadid4.add(koordinaat);

        }
        for (Koordinaadid koordinaat : sinineMängija.onDiagonaal1_3()
                ) {
            punasekoordinaadid3.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onDiagonaal1_3()
                ) {
            punasekoordinaadid3.add(koordinaat);

        }
        for (Koordinaadid koordinaat : sinineMängija.onDiagonaal1_2()
                ) {
            punasekoordinaadid2.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onDiagonaal1_2()
                ) {
            punasekoordinaadid2.add(koordinaat);

        }
        for (Koordinaadid koordinaat : sinineMängija.onDiagonaal2_4()
                ) {
            punasekoordinaadid4.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onDiagonaal2_4()
                ) {
            punasekoordinaadid4.add(koordinaat);

        }
        for (Koordinaadid koordinaat : sinineMängija.onDiagonaal2_3()
                ) {
            punasekoordinaadid3.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onDiagonaal2_3()
                ) {
            punasekoordinaadid3.add(koordinaat);

        }
        for (Koordinaadid koordinaat : sinineMängija.onDiagonaal2_2()
                ) {
            punasekoordinaadid2.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onDiagonaal2_2()
                ) {
            punasekoordinaadid2.add(koordinaat);

        }
        for (Koordinaadid koordinaat : sinineMängija.onHorisontaalselt_4()
                ) {
            punasekoordinaadid4.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onHorisontaalselt_4()
                ) {
            punasekoordinaadid4.add(koordinaat);

        }
        for (Koordinaadid koordinaat : sinineMängija.onHorisontaalselt_3()
                ) {
            punasekoordinaadid3.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onHorisontaalselt_3()
                ) {
            punasekoordinaadid3.add(koordinaat);

        }
        for (Koordinaadid koordinaat : sinineMängija.onHorisontaalselt_2()
                ) {
            punasekoordinaadid2.add(koordinaat);

        }
        for (Koordinaadid koordinaat : punaneMängija.onHorisontaalselt_2()
                ) {
            punasekoordinaadid2.add(koordinaat);

        }
        if (sinisekoordinaadid4.size() != 0) {
            Koordinaadid parim4 = leiaparim(sinisekoordinaadid4);
            pandavad = parim4;
            for (Koordinaadid koordinaat : sinisekoordinaadid4
                    ) {
                if (koordinaat.equals(parim4)) {
                    sinisekoordinaadid4.remove(parim4);
                }
            }

        } else if (punasekoordinaadid4.size() != 0) {
            Koordinaadid parim4 = leiaparim(punasekoordinaadid4);
            pandavad = parim4;
            for (Koordinaadid koordinaat : punasekoordinaadid4
                    ) {
                if (koordinaat.equals(parim4)) {
                    punasekoordinaadid4.remove(parim4);
                }
            }
        } else if (sinisekoordinaadid3.size() != 0) {
            Koordinaadid parim4 = leiaparim(sinisekoordinaadid3);
            pandavad = parim4;
            for (Koordinaadid koordinaat : sinisekoordinaadid3
                    ) {
                if (koordinaat.equals(parim4)) {
                    sinisekoordinaadid2.remove(parim4);
                }
            }

        } else if (punasekoordinaadid3.size() != 0) {
            Koordinaadid parim4 = leiaparim(punasekoordinaadid3);
            pandavad = parim4;
            for (Koordinaadid koordinaat : punasekoordinaadid3
                    ) {
                if (koordinaat.equals(parim4)) {
                    punasekoordinaadid3.remove(koordinaat);
                }
            }
        } else if (sinisekoordinaadid2.size() != 0) {
            Koordinaadid parim4 = leiaparim(sinisekoordinaadid2);
            pandavad = parim4;
            for (Koordinaadid koordinaat : sinisekoordinaadid2
                    ) {
                if (koordinaat.equals(parim4)) {
                    sinisekoordinaadid2.remove(parim4);
                }
            }

        } else if (punasekoordinaadid2.size() != 0) {
            Koordinaadid parim4 = leiaparim(punasekoordinaadid2);
            pandavad = parim4;
            for (Koordinaadid koordinaat : punasekoordinaadid2
                    ) {
                if (koordinaat.equals(parim4)) {
                    punasekoordinaadid2.remove(koordinaat);
                }
            }
        } else if (sinisekoordinaadid1.size() != 0) {
            Koordinaadid parim4 = leiaparim(sinisekoordinaadid1);
            pandavad = parim4;
            for (Koordinaadid koordinaat : sinisekoordinaadid1
                    ) {
                if (koordinaat.equals(parim4)) {
                    sinisekoordinaadid1.remove(parim4);
                }
            }
        } else if (punasekoordinaadid1.size() != 0) {
            Koordinaadid parim4 = leiaparim(punasekoordinaadid1);
            pandavad = parim4;
            for (Koordinaadid koordinaat : punasekoordinaadid1
                    ) {
                if (koordinaat.equals(parim4)) {
                    punasekoordinaadid1.remove(parim4);
                }
            }
        } else {
            väljad.get(new Koordinaadid((int) Math.ceil(Gomoku.mõõtmed / 2), (int) Math.ceil(Gomoku.mõõtmed / 2))).teeKäik();
            pandavad = new Koordinaadid((int) Math.ceil(Gomoku.mõõtmed / 2), (int) Math.ceil(Gomoku.mõõtmed / 2));
        }
        return pandavad;
    }

    private Koordinaadid leiaparim(List<Koordinaadid> antudlist) {
        int parim = 1;
        int mitmeselement = 0;
        for (int i = 0; i < antudlist.size(); i++) {
            int hetkel = 0;
            for (int j = 0; j < antudlist.size(); j++) {
                if (antudlist.get(i).equals(antudlist.get(j))) {
                    hetkel += 1;
                }
                if (hetkel > parim) {
                    parim = hetkel;
                    mitmeselement = i;
                }
            }

        }
        return antudlist.get(mitmeselement);
    }
}