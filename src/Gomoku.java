/*
 * Paaristöö. 1
 * Rait Herman
 * Jaan Kaasik
 * */

import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gomoku extends Application {
	//muutuja seadmaks mänguvälja suurust
	public static final int MÕÕTMED = 16;

	private Mängija punaneMängija = new Mängija(Color.RED, "Punane");
	private Mängija sinineMängija = new Mängija(Color.BLUE, "Sinine");
	private Mängija aktiivneMängija = punaneMängija;
	private Label teavitus = new Label();
	BorderPane borderPane = new BorderPane();
	java.util.List<Lahter> väljad = new ArrayList<>();
	public static void main(String[] args) {
		launch(args);
	}
	//mängu algseis. Loome uue mänguvälja ning eemaldame mängija käigud
	public void algSeis() {
		GridPane mänguväli = new GridPane();
		for (int i = 0; i < MÕÕTMED; i++) {
			for (int j = 0; j < MÕÕTMED; j++) {
				Lahter lahter = new Lahter(i,j);
				mänguväli.add(lahter, j, i);
				//lisame kõik väljad ka listi, kust saame kontrollida kas väli on täis või mitte
				väljad.add(lahter);
			}
		}
        punaneMängija.algSeis();
        sinineMängija.algSeis();
        aktiivneMängija = punaneMängija;
        HBox päis = new HBox();
        Button nuppUusMäng = new Button("Uus mäng");
        nuppUusMäng.setLayoutX(75);
        nuppUusMäng.setOnMouseClicked(e -> {
        	algSeis();
        });
        päis.getChildren().addAll(nuppUusMäng, teavitus);
        borderPane.setTop(päis);
        borderPane.setCenter(mänguväli);
        teavitus.setText("Punane alustab");
        
        borderPane.setBottom(new Label(punaneMängija.getTulemus() + " " + sinineMängija.getTulemus()));
	}
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Gomoku");
        stage.setScene(new Scene(borderPane, 500, 500));
        stage.setResizable(false);
        stage.show();
        algSeis();
    }
    public class Lahter extends Pane {
    	private boolean kasLahterKinni;
        private int xkoord;
        private int ykoord;
        public int getXKoord() {
        	return xkoord;
        }
        public int getYKoord() {
        	return ykoord;
        }
        private boolean getKasLahterKinni() {
        	return kasLahterKinni;
        }
        public Lahter(int xkoord, int ykoord) {
            this.xkoord = xkoord;
            this.ykoord = ykoord;
            setStyle("-fx-border-color: black");
            this.setPrefSize(200, 200);
            this.setOnMouseClicked(e -> teeKäik());
        }
        //meetod mis loeb välja klikki
        private void teeKäik() {
        	if (!kasLahterKinni) {
        		kasLahterKinni = true;
	        	aktiivneMängija.teeKäik(this, getChildren());
	        	//kontrollime kas aktiivne mängija on võitnud. Kui jah, siis vastav teade ja peale seda algseis.
				if (aktiivneMängija.kasMängijaVõitnud(this.xkoord, this.ykoord)) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Mäng läbi");
					alert.setContentText(aktiivneMängija.toString() + " võitis.\nJätkamiseks vajuta OK!");
					alert.showAndWait();
					aktiivneMängija.suurendaTulemus();
					algSeis();
				} else if (kasVäliTäis()) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Mäng läbi");
					alert.setContentText("Viik.\nJätkamiseks vajuta OK!");
					alert.showAndWait();
					algSeis();
				} else {
					//kui aktiivne mängija ei võitnud, siis aktiivse mängija ära.
					aktiivneMängija = aktiivneMängija.equals(punaneMängija) ? sinineMängija : punaneMängija;
					teavitus.setText(aktiivneMängija + " käib");
				}
        	}
        }
    }
    //meetod kontrollib kas Väli on täis. Kui leiab välja mis ei ole täidetud, siis false
    private boolean kasVäliTäis() {
    	for (Lahter väli : väljad) {
    		if (!väli.getKasLahterKinni()) {
    			return false;
    		}
    	}
    	return true;
    }
}