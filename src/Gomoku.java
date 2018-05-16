/*
 * Paaristöö. 1
 * Rait Herman
 * Jaan Kaasik
 * */

import java.io.File;
import java.util.*;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;

public class Gomoku extends Application {
    //muutuja seadmaks mänguvälja suurust
    public static final int MÕÕTMED = 18;
    //private static final int INIMENE_VS_INIMENE = -1;
    //private static final int ARVUTI_VS_INIMENE = 1;
    //int mängutüüp;
    Map<Koordinaadid, Lahter> lahtrid;
    //-1 kui inimesed omavahel, 1 kui arvuti ka mängib.
    private Mängija punaneMängija = new Mängija(Color.RED, "Punane");
    private Mängija sinineMängija = new Mängija(Color.BLUE, "Sinine");
    private Mängija aktiivneMängija = punaneMängija;
    private Label teavitus = new Label();
    Map<Koordinaadid, Lahter> väljad = new HashMap();
    //java.util.List<Lahter> väljad = new ArrayList<>();
    HBox mänguAken = new HBox();

    public static void main(String[] args) {
        launch(args);
    }

    //mängu algseis. Loome uue mänguvälja ning eemaldame mängija käigud
    public void algSeis() {
        mänguAken.getChildren().clear();
        GridPane mänguväli = new GridPane();
        for (int i = 0; i < MÕÕTMED; i++) {
            for (int j = 0; j < MÕÕTMED; j++) {
                Koordinaadid koordinaadid = new Koordinaadid(j, i);
                Lahter lahter = new Lahter(koordinaadid);
                mänguväli.add(lahter, j, i);
                //lisame kõik väljad ka listi, kust saame kontrollida kas väli on täis või mitte
                väljad.put(koordinaadid, lahter);
            }
        }
        Alert mänguViis = new Alert(AlertType.CONFIRMATION);
        mänguViis.setTitle("Valige endale sobiv mänguviis");
        mänguViis.setHeaderText("Pane ennast proovile AI vastu");
        mänguViis.setContentText("Ainult 1 mängu saab korraga mängida");
        ButtonType inimmäng = new ButtonType("Inimene vs inimene");
        ButtonType arvutimäng = new ButtonType("Mängin arvuti vastu");
        mänguViis.getButtonTypes().setAll(arvutimäng, inimmäng);

        Optional<ButtonType> result = mänguViis.showAndWait();

        punaneMängija.algSeis(false);
        sinineMängija.algSeis(result.get() == arvutimäng);

        aktiivneMängija = punaneMängija;
        VBox külgRiba = new VBox();
        külgRiba.setMinWidth(100);
        Button nuppUusMäng = new Button("Uus mäng");
        nuppUusMäng.setLayoutX(75);
        nuppUusMäng.setOnMouseClicked(e -> {
            algSeis();
        });
        teavitus.setText("Punane alustab");

        külgRiba.getChildren().addAll(
                nuppUusMäng,
                punaneMängija.getTulemusLabel(),
                sinineMängija.getTulemusLabel(),
                teavitus);
        mänguAken.getChildren().addAll(mänguväli, külgRiba);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Gomoku");
        Scene stseen = new Scene(mänguAken, 500, 500);
        stage.setScene(stseen);
        stage.show();
        algSeis();
    }

    public class Lahter extends Pane {
        private Mängija omanik;
        private Koordinaadid koordinaadid;

        public Mängija getOmanik() {
            return omanik;
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
                    algSeis();
                } else if (kasVäliTäis()) {
                    Alert mängläbi = new Alert(AlertType.INFORMATION);
                    mängläbi.setTitle("Mäng läbi");
                    mängläbi.setContentText("Viik.\nJätkamiseks vajuta OK!");
                    mängläbi.showAndWait();
                    algSeis();
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
                            algSeis();
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
                        algSeis();
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
            väljad.get(new Koordinaadid((int) Math.ceil(Gomoku.MÕÕTMED / 2), (int) Math.ceil(Gomoku.MÕÕTMED / 2))).teeKäik();
            pandavad = new Koordinaadid((int) Math.ceil(Gomoku.MÕÕTMED / 2), (int) Math.ceil(Gomoku.MÕÕTMED / 2));
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